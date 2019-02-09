package cat.lump.aq.textextraction.wikipedia.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliArticleSelector;


/**
 * TODO: implement margin-based approach to parallalel sentence extraction
 * 
 * @author cristina
 * @since Feb 08, 2019
 * 
 */
public class ParallelSentenceExtractor {
	
	/** Maximum number of files that a folder stores as defined in io.FileManager */
	private static final int IDS_PER_DIR = 100000;
	
	/** Error code*/
	private static final int ERRORINT = 99;
	
	/** Path to the directory where the original articles are stored. */
	private String rootDirectory;
	
	/** Path to the output directory. */
	private String outDirectory;

	/** Tab separated file with the comparable article's IDs and titles */
	private String comparableArtFile;

	private static LumpLogger logger = 
			new LumpLogger (ParallelSentenceExtractor.class.getSimpleName());


	public ParallelSentenceExtractor (String inputFile, String path, String outpath){
		CHK.CHECK_NOT_NULL(inputFile);
		CHK.CHECK_NOT_NULL(path);
		CHK.CHECK_NOT_NULL(outpath);
		this.rootDirectory = path;
		this.outDirectory = outpath;
		this.comparableArtFile = inputFile;
	}
	

	private void processAll() {

		// Input folder
		File directory = new File(rootDirectory); 
        CHK.CHECK(directory.isDirectory(), "I cannot read the directory " + rootDirectory);	
  
		// Output folder
		File writeDirectory = new File(outDirectory); 
        CHK.CHECK(directory.isDirectory(), "I cannot access the directory " + outDirectory);	

        String [] languages = getLanguages();
        //String [][] titles = new ;
        
		Path path = Paths.get(comparableArtFile);
		long lineCount = 0;
		try {
			lineCount = Files.lines(path).count();
		} catch (IOException e1) {
			logger.warn("A problem occured when reading the number of lines of the input file.");
			e1.printStackTrace();
		}
		
		// For large files we split every 1000 articles
		if (lineCount > 2000){
			int counter = 0;
			
			
		}
		
		
		// NOPE cap abaix
        // Output file
        String csvFile = String.format("PMI.csv");
        File output = new File(rootDirectory + FileIO.separator + csvFile);
        String csvValues = "pmiMedian,npmiMedian,pmiMean,npmiMean,pmiCatMedian,npmiCatMedian,pmiCatMean,npmiCatMean,artsInCat,catID,depth\n";
        
		// Select the subfolders representing each category
		File[] fList = directory.listFiles();  
		List<File> listOfFolders = new ArrayList<File>();
		String lang = "";
		for (File file : fList) {
			if (file.isDirectory() && file.getName().matches("^"+lang+".\\d+")) {
				listOfFolders.add(file.getAbsoluteFile());
	        }
	    }
		
		// Go into every subfolder (category)
		for (File folder : listOfFolders) {
			//sString result = processCategory(folder);
			String result = "kk";
			if (result.equals(String.valueOf(ERRORINT))) {
				continue;
			} else {
				csvValues = csvValues + result;
			}
	    } //end category

		try {
			FileIO.stringToFile(output, csvValues, false);
		} catch (IOException e) {
			e.printStackTrace();
			logger.warn("Some problem occured when writing results into " + csvFile);
		}
		logger.info("END ");
	}
	
	
	/**
	 * Extract the languages from the filename
	 * 
	 * @return
	 */
	private String[] getLanguages() {
		
		String filename = comparableArtFile.substring(comparableArtFile.lastIndexOf("/")+1);    
		filename = filename.replace("union","");
		filename = filename.replace("intersection","");
        String[] parts = filename.split("\\.");
        int i = 0;
		StringBuffer languages = new StringBuffer();
        for (String part : parts) {
        	if (i%2==0){
        		languages.append(part);
        		languages.append(",");
        	}
            i++;
        }
		logger.info(String.format("Parallelisation for languages %s", languages.toString()));
        
		return languages.toString().split(",");
	}


	/** NOPE
	 * Checks if a file with the desired extension exists in folder {@code folder}
	 * 
	 * @param folder
	 * @param extension
	 * @return String file
	 */
	private String lookForFile(File folder, String extension) {

		List<String> listFiles = FileIO.getFilesExt(folder, extension);

		String file = "NONE";
		if (listFiles.size()>0) {
			file =  FileIO.getFilesExt(folder, extension).get(0);
			if (!(new File(file).exists())) {
				 file = "NONE";
			}
		}
		
		return file;
	}


	/** NOPE
	 * For an article with id {@code id}, the frequencies for all its terms are retrieved from 
	 * the file generated by prepo.TermExtractor. The total number of terms are stored as 
	 * a fake term NUM_TERMS
	 * 
	 * @param id
	 * @return HashMap<String, Integer> hmapTFs
	 */
	private HashMap<String, Integer> readArticleTFsSum(String id) {
		
		String lang = "en";
		int index =  Integer.parseInt(id)/IDS_PER_DIR;
		String pathToFile = rootDirectory + FileIO.separator + "tfs" + FileIO.separator + lang
				+ FileIO.separator + index + FileIO.separator + id + "." + lang + ".txt" ;
		
		HashMap<String, Integer> hmapTFs = new HashMap<String, Integer>();		
	    BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File(pathToFile)));
	        String line = br.readLine();
	        String[] columns;
	        Integer numTerms = 0; 
	        while (line != null) {
		        columns = line.split("\\s+");
		        hmapTFs.put(columns[1], Integer.parseInt(columns[0]));
		        numTerms = numTerms + Integer.parseInt(columns[0]);
	            line = br.readLine();
	        }
	        hmapTFs.put("NUM_TERMS", numTerms);  
	        br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hmapTFs;
	}


	/** NOPE
	 * Reads {@codeFile articlesF} and extracts the list of IDs with all the
	 * articles extracted for the category
	 *  
	 * @param articlesF
	 * @return List<String> ids
	 */
	@SuppressWarnings("unused")
	private List<String> readArticlesID(File articlesF) {
		return readArticlesIDsizeMax(articlesF, -1);
	}
	

	/** NOPE
	 * Reads {@codeFile articlesF} and extracts the list of IDs with all the
	 * articles extracted for the category with a maximum size of sizeRef
	 *  
	 * @param articlesF
	 * @param sizeRef
	 * @return List<String> ids
	 */
	private List<String> readArticlesIDsizeMax(File articlesF, int sizeRef) {
		List<String> ids =new ArrayList<String>();
	    BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(articlesF));
	        String line = br.readLine();
	        if(sizeRef == -1){  //there is no limit, let's take all the elements
		        while (line != null) {
			        ids.add(line.toString());
		            line = br.readLine();
		        }	        	
	        } else {
		        int i = 0;
		        while (line != null && i < sizeRef) {
			        ids.add(line.toString());
		            line = br.readLine();
		            i++;
		        }
	        }
	        br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ids;
	}

	
	
	private static CommandLine parseArguments(String[] args)
	{
		
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cLine = null;
		Options options= new Options();
		CommandLineParser parser = new BasicParser();

		options.addOption("h", "help", false, "This help");
		options.addOption("p", "path2root", true,
				    "Absolute Path to the folder where plain/ is (default: current)");
		options.addOption("f", "file", true,
	 				"Absolute path to the input file with the comparable articles across languages"
						+ "(generated with CommonNamespaceFinder)");
		options.addOption("o", "outpath", true,
					"Path to the output directory (default: current)");


		try {			
		    cLine = parser.parse( options, args );
		} catch( ParseException exp ) {
			logger.error( "Unexpected exception:" + exp.getMessage() );			
		}	
		
		if (!(cLine.hasOption("p")) ) {
			logger.error("Please, set the path to the root folder to the articles\n");
			formatter.printHelp(ParallelSentenceExtractor.class.getSimpleName(),options);
			System.exit(1);
		}		
		if (! cLine.hasOption("f")) { //no input file 
			logger.error("An input file with the comparable articles is mandatory\n");
			formatter.printHelp(ParallelSentenceExtractor.class.getSimpleName(),options);
			System.exit(1);
		}		        
		if (cLine.hasOption("h")) {
			formatter.printHelp(ParallelSentenceExtractor.class.getSimpleName(),options );
			System.exit(0);
		}
	
		return cLine;		
	}

	/**
	 * Main function to run the class, serves as example
	 * 
	 * @param args
	 *      -f Input file with the comparable article's IDs and titles
	 * 		-o Path to the output folder
	 * 		-p Path to the root folder for the given language
	 */
	public static void main(String[] args) {

		/*CommandLine cLine = parseArguments(args);
		
 		String inputFile =  cLine.getOptionValue("f");	
 		String path = cLine.hasOption("p") ?
				  cLine.getOptionValue("p") : System.getProperty("user.dir");	
		CHK.CHECK(new File(path).isDirectory(), "I cannot access the directory with the articles");
		String outpath = cLine.hasOption("o") ? 
				  cLine.getOptionValue("o") : System.getProperty("user.dir");
	    CHK.CHECK(new File(outpath).isDirectory(), "I cannot access the output directory");
	    */
		
	    
		String path = "/home/cristinae/pln/WikiTailor/oc.0";
		String outpath = "/home/cristinae/pln/WikiTailor/";
		String inputFile = "/home/cristinae/pln/WikiTailor/es.45961.oc.54634.ro.34.ca.4564.union";

		ParallelSentenceExtractor xtractor = new ParallelSentenceExtractor(inputFile, path, outpath);
		xtractor.processAll();

	}

}
