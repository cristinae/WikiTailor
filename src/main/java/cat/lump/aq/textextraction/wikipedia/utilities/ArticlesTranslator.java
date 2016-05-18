package cat.lump.aq.textextraction.wikipedia.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.ie.textprocessing.TextPreprocessor;

/**
 * A class to translate all the articles from L1 into L2 in a folder with the structure
 * of Wikicardi: path/plain/L1/index/id.L1.txt
 * The index files with the position of the articles and its length is required.
 * 
 * @author cristina
 * @since Feb 18, 2015
 * TODO: Convert into a cli?
 * TODO: think of moses as a server and/or apache's tika library
 */
public class ArticlesTranslator {

	// TODO move to a config file
	/**Location of the translator */
	private final String mosesPath = "/home/usuaris/tools/mosesdecoder/"; //@cluster
	/**Number of segments to send together to translate */
	private final int numSegments = 50000;
	/**Error identifier */
	private static int ERRORINT = 99;

	/**Language pair to be translated {source, target} */
	private String[] langs;
	/**Root folder where the articles lie */
	private String pathArticles;

	private static LumpLogger logger = new LumpLogger(
			ArticlesTranslator.class.getSimpleName());

	
	public ArticlesTranslator(String[] langs, String pathArticles) {		
		this.langs = langs;
		this.pathArticles = pathArticles;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Defaults
		String[] langs = {"ca", "eu"};  //source,target
		String pathArticles = "/home/cristinae/pln/wikipedia/articles/5784/0.5";
		//String pathArticles = "/home/usuaris/cristinae/wiki/debug/5784/0.5";
		String commonArticles = "/home/cristinae/pln/wikipedia/articles/ca.13684.eu.5784.intersection";
		String translator = "moses";
		boolean onlyMissing = true;
		int sgeThreads = 4;

		ArticlesTranslator tradArt = new ArticlesTranslator(langs, pathArticles);
		//to translate all the articles
		//tradArt.generateSetsFullFolder(new HashSet<Integer>(), onlyMissing); 
		//to translate only the union/intersection
		tradArt.generateSetsCommon(commonArticles, onlyMissing); 
		try{
			tradArt.translateSets(translator, sgeThreads);			
		} catch(InterruptedException ex){
			Thread.currentThread().interrupt();
		    logger.error("Interrupted exception " + ex.getCause());
		} catch(ExecutionException ex){
			logger.error("Problem executing " +translator+": " + ex.getCause());
	    }
		//to reconstruct all the articles
		//tradArt.reconstructTradArticles(new HashSet<Integer>(), onlyMissing);
		//to reconstruct only the union/intersection
		tradArt.reconstructTradArticlesCommon(commonArticles, onlyMissing);
	}
	

	/** 
	 * Main method to call the decoder {@code String translator} for all the files generated
	 * by {@code generateSetsFullFolder()}. 
	 * The processes are distributed among the available processors. 
	 * 
	 * @param translator
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void translateSets(String translator, int sgeThreads) throws InterruptedException, ExecutionException  {
	
		int processors = Runtime.getRuntime().availableProcessors();
		
		int numThreads = (sgeThreads < processors) ? sgeThreads : processors;
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		logger.info(numThreads + " threads will be launched simultaneously");
		
		String path = pathArticles + FileIO.separator + "plain" + FileIO.separator + langs[0] + FileIO.separator;
		//These are the files to translate
		List<String> listOfFiles = FileIO.getFilesExt(new File(path), langs[0]+"2"+langs[1]+"."+langs[0]);
		
		CompletionService<TranslatorResult> compService = new ExecutorCompletionService<>(executor);
		for(String file : listOfFiles){
			String outputFile = 
					file.replaceAll(langs[0]+"2"+langs[1]+"."+langs[0],langs[0]+"2"+langs[1]+"."+langs[1]);
			Task task = new Task(file, outputFile, translator);
			compService.submit(task);	
		}
		for(String file : listOfFiles){
			Future<TranslatorResult> future = compService.take();
			logger.info(future.get().toString());
		}
		executor.shutdown(); 
		
	}

	private final class Task implements Callable<TranslatorResult> {
		Task(String file, String outputFile, String translator){
			fileToTrad = file;
			fileTrad = outputFile;
			decoder = translator;
		}
		/** Runs a decoder and returns the associated information: success fileToTrad */
		@Override 
		public TranslatorResult call() throws Exception {
			return translateAndReportStatus(fileToTrad, fileTrad, decoder);
		}
		private final String fileToTrad;
		private final String fileTrad;
		private final String decoder;
	}
	 
	 
	 /**
	  * Sends an instance of the decoder {@code String translator} as a {@code Process} given the file to
	  * translate {@code String fileToTrad}, and a file to store the translation {@code String outputFile}.
	  * 
	  * @param fileToTrad
	  * @param outputFile
	  * @param translator
	  * @return result
	  * 		 a TranslatorResult with the information of the execution
	  */
	 private TranslatorResult translateAndReportStatus(String fileToTrad, String outputFile, String translator){
		    TranslatorResult result = new TranslatorResult();
		    result.inputFile = fileToTrad;
		    result.success = ERRORINT;
		    
		    long start = System.currentTimeMillis();
			String mosesBin = mosesPath + FileIO.separator + "bin" + FileIO.separator + "moses";
			String mosesIni = pathArticles + FileIO.separator + "plain" + FileIO.separator + langs[0] + 
					FileIO.separator + "moses." + langs[0]+"2"+langs[1] +".ini";
			
			//String[] command = {"/usr/bin/perl", "/home/cristinae/pln/devSoft/scripts/xmlChars.pl", 
			//		fileToTrad, "hello.txt"};
			String[] command = {"", "", ""};
    		switch (translator) {
            case "moses":
    			command[0] = mosesBin; 
    			command[1] = "-f";
    			command[2] = mosesIni;
               	break;
            case "apertium":
            	logger.error("Apertium has not been included yet");
            	break;
            default:
            	logger.error(translator + " is not a valid translator");	                	
    		}			         	        		

    		//logger.info("Comanda: " + command);
			ProcessBuilder tradpb = new ProcessBuilder(command);
			tradpb.redirectInput(new File(fileToTrad));
			tradpb.redirectOutput(new File(outputFile));
			String errorFile = outputFile + ".error";
			tradpb.redirectError(new File(errorFile));
	    	Process decoder = null;
	    	try {
	    		decoder = tradpb.start();
	    		logger.info("Translating " + (new File(fileToTrad).getName()));
				int error = decoder.waitFor();
				result.success = error;
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Unable to start the decoder");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	 
		    long end = System.currentTimeMillis();
		    result.time = (end-start)*1000;  //time in seconds 

		    return result;
	 }
	 
	/** 
	 * Simple struct to hold the info related to a decoding execution 
	 */
	private static final class TranslatorResult {
	    int success;
	    long time;
	    String inputFile;
	    @Override public String toString(){
	      return success + "::" + (new File(inputFile)).getName()  + "::" + time;
	    }
	  }
	
	

	/**
	 * Given the original index files and the temporal files already translated, 
	 * the method reconstructs the translation of every individual article that
	 * was originally in the file of common articles and saves them in 
	 * path/plain/L1/index/id.trad.L2.txt
	 * @param onlyMissing 
	 */
	public void reconstructTradArticlesCommon(String commonArticles, boolean onlyMissing) {
		
		HashSet<Integer> commonIDs = new HashSet<Integer>();
		Scanner s = new Scanner(commonArticles).useLocale(Locale.ENGLISH);
	    while (s.hasNext()) {
	    	commonIDs.add(s.nextInt());
	    	s.nextLine();
	    }	    
	    s.close();
	    reconstructTradArticles(commonIDs, onlyMissing);
	}

	/**
	 * Given the original index files and the temporal files already translated, 
	 * the method reconstructs the translation of every individual article and
	 * saves them in path/plain/L1/index/id.trad.L2.txt
	 * @param commonIDs 
	 * @param onlyMissing 
	 */
	public void reconstructTradArticles(HashSet<Integer> commonIDs, boolean onlyMissing) {
		
		String path = pathArticles + FileIO.separator + "plain" + FileIO.separator + langs[0];
		List<String> listOfIndexes = FileIO.getFilesExt(new File(path), "ids");

		logger.info("Reconstructing the translated articles from the temporal files");
		
		for (String file : listOfIndexes) {
			File currentFile = new File(file);
			String index =  FilenameUtils.removeExtension(currentFile.getName());
			String line;
			FileReader fr = null;
			BufferedReader br = null;
			try {
				fr = new FileReader(currentFile);
				br = new BufferedReader(fr);
				//First file (and buffer) for a given index with the translations
				int numFile = 0;
				String currentTradPath = path + FileIO.separator + index + "." + numFile + "."
						+ langs[0] + "2" + langs[1] + "." + langs[1];
				File currentTradFile = new File(currentTradPath);
				FileReader frTrad = new FileReader(currentTradFile);
				BufferedReader brTrad = new BufferedReader(frTrad);
				int counter = 0;
				
				//read the index file
				int numLines = 0;
				while ((line = br.readLine()) != null) {
					if (line.isEmpty()) continue;
					String[] fields = line.split("\\s+");
					
				    //in case we have the common articles check if this file is common to proceed
					if (!commonIDs.isEmpty()){
						Pattern r = Pattern.compile("(\\d+)\\.\\w+\\.txt");
						Integer id = Integer.getInteger(r.matcher(fields[0]).group(1));
						if (!commonIDs.contains(id)) continue;
					}
					//create a translated article file for every source article in the indexfile
					String currentArticle = path + FileIO.separator + index + FileIO.separator + fields[0];
					currentArticle = currentArticle.replaceAll(langs[0]+".txt","trad."+langs[1]+".txt");
					File currentArticleFile = new File(currentArticle);
					if (onlyMissing){
						if(currentArticleFile.exists()) continue;
					}
					numLines = numLines + Integer.parseInt(fields[1]);
					//locate the translations in the temporal file and print them
					int numLinesArticle = Integer.parseInt(fields[1]);
					String tradLine = "";
					while (counter < numLinesArticle) {
						//if (brTrad.readLine().isEmpty()) continue;
						tradLine = tradLine + brTrad.readLine() + "\n";
						counter++;
					}
					FileIO.stringToFile(currentArticleFile, tradLine, false);
					counter = 0;
					
					//if the number of appended lines >numSegments, the following translations are in another file
					if (numLines > numSegments){
						numLines = 0;
						numFile++;
						currentTradPath = path + FileIO.separator + index + "." + numFile + "."
								+ langs[0] + "2" + langs[1] + "." + langs[1];
						currentTradFile = new File(currentTradPath);
						brTrad.close();
						frTrad.close();
						frTrad = new FileReader(currentTradFile);
						brTrad = new BufferedReader(frTrad);
					}
				}
				brTrad.close();
				frTrad.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				logger.error("I/O error: file " + file + " not found");
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("I/O error: cannot read " + file);
			} 
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("I/O error: cannot close " + file);
			}
		} //fifor
		
	}

	

	/** 
	 * Looks for the index files in the given folder and generates temporal files (tokenised) 
	 * to be translated with only the articles that also appear in file {@code String commonArticles}. 
	 * TODO also lowercase??
	 * These files are the concatenation of the articles in the corresponding index folder 
	 * with length<numSegments. 
	 * @param onlyMissing 
	 */
	public void generateSetsCommon(String commonArticles, boolean onlyMissing) {
		
		HashSet<Integer> commonIDs = new HashSet<Integer>();
		Scanner s = new Scanner(commonArticles).useLocale(Locale.ENGLISH);
	    while (s.hasNext()) {
	    	commonIDs.add(s.nextInt());
	    	s.nextLine();
	    }	    
	    generateSetsFullFolder(commonIDs, onlyMissing);
	    		
	}

	/** 
	 * Looks for the index files in the given folder and generates temporal files (tokenised) 
	 * to be translated. (TODO also lowercase??)
	 * These files are the concatenation of the articles in the corresponding index folder 
	 * with length <numSegments. 
	 * @param commonIDs 
	 * @param onlyMissing 
	 */
	public void generateSetsFullFolder(HashSet<Integer> commonIDs, boolean onlyMissing) {
		
		String path = pathArticles + FileIO.separator + "plain" + FileIO.separator + langs[0];
		List<String> listOfFiles = FileIO.getFilesExt(new File(path), "ids");
		TextPreprocessor prepro = new TextPreprocessor(new Locale(langs[0]));

		logger.info("Generating the temporal files to translate");
		for (String file : listOfFiles) {
			File currentFile = new File(file);
			String index =  FilenameUtils.removeExtension(currentFile.getName());
			String line;
			FileReader fr = null;
			BufferedReader br = null;
			try {
				fr = new FileReader(currentFile);
				br = new BufferedReader(fr);
				//initialise the first file to append the articles
				int numFile = 0;
				String currentToTradPath = path + FileIO.separator + index + "." + numFile + "."
						+ langs[0] + "2" + langs[1] + "." + langs[0];
				File currentToTradFile = new File(currentToTradPath);
				//read every index file
				int numLines = 0;
				boolean first = true;
				while ((line = br.readLine()) != null) {
					if (line.isEmpty()) continue;
					String[] fields = line.split("\\s+");
				    //in case we have the common articles check if this file is common to proceed
					if (!commonIDs.isEmpty()){
						Pattern r = Pattern.compile("(\\d+)\\.\\w+\\.txt");
						Integer id = Integer.getInteger(r.matcher(fields[0]).group(1));
						if (!commonIDs.contains(id)) continue;
					}
				
					//include every article in the indexfile up to numSegments lines
					String currentArticle = path + FileIO.separator + index + FileIO.separator + fields[0];

					//before, we check if the article has already been translated
					if (onlyMissing){
						String translation = currentArticle.replaceAll(langs[0]+".txt","trad."+langs[1]+".txt");
						File currentArticleTrad = new File(translation);
						if(currentArticleTrad.exists()) continue;
					}
					
					String content = FileIO.fileToString(new File(currentArticle));
					//tokenise the articles ICU4J, need to keep end of lines
					String[] sentences = content.split("\n");
					String contentTok = "";
					for (String sentence : sentences){
						prepro.setString(sentence);
						contentTok = contentTok + prepro.getString() + "\n";						
					}
					printArticles(currentToTradFile, contentTok, first);
					first = false;
					numLines = numLines + Integer.parseInt(fields[1]);
					//when the number of appended lines >numSegments, create a new file
					if (numLines > numSegments){
						numLines = 0;
						numFile++;
						currentToTradPath = path + FileIO.separator + index + "." + numFile + "."
								+ langs[0] + "2" + langs[1] + "." + langs[0];
						currentToTradFile = new File(currentToTradPath);
						first = true;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				logger.error("I/O error: file " + file + " not found");
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("I/O error: cannot read " + file);
			} 
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("I/O error: cannot close " + file);
			}

		}//fifor
	}

	
	/**
	 * Method for converting the file with the articles in common L1.icat1.L2.icat2.method
	 * into the mirror file with L2.icat2.L1.icat1.method. It can be used to translate
	 * articles in both directions.
	 * 
	 * @param commonArticles
	 * @return
	 * 		String with the name of the new file
	 */
	public String mirrorCommonArticles2Langs(String commonArticles) {
		
		File originalFile = new File(commonArticles);
		String commonArticlesMirror = " ";
		Pattern r = Pattern.compile("(\\w+\\.\\d+\\.)(\\w+\\.\\d+\\.)(\\w+)");
		Matcher m = r.matcher(commonArticles);
		if (m.find()) {
			commonArticlesMirror = m.group(2) + m.group(1) + m.group(3);
		} else{
			logger.error("The file with the articles in common " + commonArticles + 
					"does not follow the standard nomenclature");
		}

		File mirrorFile = new File(commonArticlesMirror);
		String line;
		String mirrorLine = "";
		try {
			FileReader fr = new FileReader(originalFile);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) continue;
				String[] fields = line.split("\\s+");
				mirrorLine = mirrorLine + 
						fields[2]+"\t" + fields[3]+"\t" + fields[0]+"\t" + fields[1]+"\n";
			}
			br.close();
			fr.close();
			FileIO.stringToFile(mirrorFile, mirrorLine, false);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("I/O error: cannot read " + originalFile);
		} 	    
	 
		return commonArticlesMirror;
	}

	
	/**
	 * Prints a string with the content of the articles or appends it to the file if it exists 
	 * @param fileName
	 * @param content
	 * @param firstTime
	 */
	private void printArticles(File fileName, String content, boolean firstTime) {
		try {
			if(firstTime) {  				//this way, yes
				FileIO.stringToFile(fileName, content, false);				
			} else {
				FileIO.appendStringToFile(fileName, content, false);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.warn("There was an error printing the file " + fileName.toString());
		}			
	}

}
