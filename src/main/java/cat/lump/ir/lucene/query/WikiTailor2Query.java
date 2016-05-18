package cat.lump.ir.lucene.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.ir.lucene.cli.LuceneCliWT2Query;

/**
 * Query into Lucene indexes for WikiTailor. That involves a specific preprocess both
 * when indexing and here in the queries. The preprocess is included in the WikiTailor
 * analyzer:
 * /lump2-ir-lucene/src/cat/lump/ir/lucene/engine/WTAnalyzer.java
 * Queries are launched with LuceneQuerierWT
 * 
 * There are two implementations: 
 * - Approach1: One is done so that we don't need a connection to the DB. Because of this 
 * we need a WT model beforehand. 
 * - Approach2: The second one obtain the terms instead of reading them. This is the approach 
 * followed in the Xecutor pipeline.
 * 
 * TODO (or not) This class shares code with cat.lump.aq.textextraction.wikipedia.experiments
 * Join?
 * 
 * @author cristina
 * @since July 1, 2015
 *
 */
public class WikiTailor2Query {

	private static LumpLogger logger = 
			new LumpLogger(WikiTailor2Query.class.getSimpleName());

	/** Path to the directory where the original articles are stored. */
	private String rootDirectory;
	
	/** Path to the directory where the original indexes are stored. */
	private String indexDirectory;
	
	/** Language of the articles */
	private String lang;
	
	/** Verbosity */
	private Boolean verbose;
	
	/**Documents with a score larger than max_score/percentage will be retrieved */
	private float percentage;

	/**Where to look for the terms list in the Approach1
	 * We should't use this anymore*/
	private final String model = "0.6-100";

	/** Maximum size of the vocabulary defining the domain*/
	private int maxVocab;

	/**File with the IDs of the retrieved articles */
	private String eArticlesFileName;

	/**Directory where to store the retrieved article IDs*/
	private String outputDir;
	
	
	/** Constructors*/
	public WikiTailor2Query(Locale lang, String inPath, String indexPath, String outPath, 
			float pct, int maxVocab, String eArticlesFileName){
		this(lang, inPath, indexPath, outPath, pct, maxVocab, eArticlesFileName, true);
	}
	
	public WikiTailor2Query(Locale lang, String inPath, String indexPath, String outPath, 
			float pct, int maxVocab, String eArticlesFileName, Boolean verbose){
		this.lang = lang.toString();
		this.rootDirectory = inPath;
		this.outputDir = outPath;
		this.indexDirectory = indexPath;
		this.percentage = pct;
		this.maxVocab = maxVocab;
		this.eArticlesFileName = eArticlesFileName;
		this.verbose = verbose;
	}

	/** 
	 * Extracts the top articles corresponding to the current model for all the categories
	 * available in the rootDirectory folder.
	 * Use this method for "fast" languages, that is, not for English! 
	 */
	private void processAll() {

		// Input folder
		File directory = new File(rootDirectory); 
        CHK.CHECK(directory.isDirectory(), "I cannot read the directory " + rootDirectory);	
         
		// Select the subfolders representing each category
		logger.info("START ");
		File[] fList = directory.listFiles();  
		List<File> listOfFolders = new ArrayList<File>();
		for (File file : fList) {
			if (file.isDirectory() && file.getName().matches("^"+lang+".\\d+")) {
				listOfFolders.add(file.getAbsoluteFile());
	        }
	    }		
		// Go into every subfolder (category)
		for (File folder : listOfFolders) {
			processCategory(folder);
	    } //end category

		logger.info("END ");
	}
	
	
	/**
	 * Extracts the top articles corresponding to the current model for a concrete category.
	 * (Approach 1, WT graph-based model needed beforehand)
	 * 
	 * @param folder
	 */
	private void processCategory(File folder) {
		
		logger.info("START Category");

		// Input folder for the vocabulary of the category
		File category = new File(folder.toString() + FileIO.separator + model + FileIO.separator);
		// Where to store the output
		Pattern p = Pattern.compile("\\w+.(\\d+)");
		Matcher m = p.matcher(folder.toString());
		String catID = folder.toString();
		if (m.find()) {
			catID = m.group(1);  // The matched substring
		} else{
			logger.warn("There's been a problem with the ID of the category " + catID);
		}
		File output = new File(outputDir, String.format(eArticlesFileName,lang,catID));
		
		// Locate the vocabulary
		File vocabularyF = null;
		String terms = "";
		String fileName = lookForFile(category, "dict");
		if (!fileName.matches("NONE") ){
			vocabularyF = new File(fileName);
			// Extract the characteristic vocabulary for that category
			terms = readTerms(vocabularyF);
		} else {
			logger.error("No dict for category " + folder.toString() );
		}
							
		if (!terms.isEmpty()) {
			// Query Lucene with those terms
			LuceneQuerierWT qWPar = new LuceneQuerierWT(lang, indexDirectory, percentage);
			qWPar.loadIndex(new Locale(lang));
			String topDocuments = qWPar.queryIDs(terms);
			// Print the IDs of the documents
			try {
				FileIO.stringToFile(output, topDocuments, false);
			} catch (IOException e) {
				logger.error("The file with extracted articles for category " 
						+ folder.toString() + "couldn't be created. ");
				e.printStackTrace();
			}		
		}else{
			logger.error("No terms are available for category " + category );
		}
	
		logger.info("END Category");

	}
	


    /**
     * Extracts the top articles corresponding to the current model for a concrete category.
     * (Approach 2, we have a vocabulary file obtained in any way and independent of a previous
     * WT execution. TODO This approach should be followed also for precalculated WT models.)
	 * 
     * @param folder
     * @param vocabularyF
     */
	public void processCategory(String catID, File vocabularyF) {
		
		logger.info("START Category");

		// Where to store the output
		File output = new File(outputDir, String.format(eArticlesFileName, lang, catID));
		
		// Extract the characteristic vocabulary for that category
		String terms = readTerms(vocabularyF);
							
		if (!terms.isEmpty()) {
			// Query Lucene with those terms
			LuceneQuerierWT qWPar = new LuceneQuerierWT(lang, indexDirectory, percentage);
			qWPar.loadIndex(new Locale(lang));
			String topDocuments = qWPar.queryIDs(terms);
			// Print the IDs of the documents
			try {
				FileIO.stringToFile(output, topDocuments, false);
			} catch (IOException e) {
				logger.error("The file with extracted articles for category " 
						+ catID + "couldn't be created. ");
				e.printStackTrace();
			}		
		}else{
			logger.error("No terms are available for category " + catID );
		}
	
		logger.info("END Category");

	}
	

	/**
	 * Reads {@codeFile vocabularyF} and extracts a list of terms with the characteristic
	 * vocabulary of the category
	 * 
	 * @param vocabularyF
	 * @return String terms 
	 * 			 string with at most the top {@code maxVocab} terms separated by a space
	 */
	private String readTerms(File vocabularyF) {
		
		String terms = "";
	    BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(vocabularyF));
	        String line = br.readLine();
	        int i=1;
	        while (line != null) {
		        String[] columns = line.split("\\s+");
		        terms = terms.concat(columns[1]).concat(" ");
		        if (i > maxVocab) {break;}
	            line = br.readLine();
	            i++;
	        }
	        //System.out.println(terms);
	        br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return terms;
	}
	
	/**
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


	public void setOutputDir(String output){
		CHK.CHECK_NOT_NULL(output);		
		if (new File(output).isDirectory()){
			logger.info("Output directory found");
			outputDir = output;			
		} else {
		   	logger.error("I cannot read the output directory");
		   	System.exit(1);
		}		    
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		LuceneCliWT2Query cli = new LuceneCliWT2Query();
		cli.parseArguments(args);
		Locale lang = cli.getLanguage();
		String indexPath = cli.getIndexDir();
		String inPath = cli.getInDir();
		String outPath = cli.getOutDir();
		String category = cli.getCategory();
		float pct = cli.getPercentage();
		boolean verbose = cli.getVerbosity();
		int maxVocab = cli.getMaxVocab();
		String eArticlesFileName = cli.getEArticlesFileName();
/*
		Locale lang = new Locale("ar");
		String indexPath = "/home/cristinae/pln/wikipedia/categories/indexes/ar";
		String inPath = "/home/cristinae/pln/wikipedia/categories/extractions/ar.0/";
		String outPath = "/home/cristinae/pln/wikipedia/categories/extractions/ar.0/";
		String category = "9555";
		//String category = "ALL";
		float pct = 10;
		boolean verbose = true;
*/
		
		WikiTailor2Query wp2q = new WikiTailor2Query(lang, inPath, indexPath, outPath, 
				pct, maxVocab, eArticlesFileName, verbose);
		
		if (category.equals("ALL")) {
			wp2q.processAll();
		} else {
			wp2q.processCategory(new File(inPath+"/"+lang.toString()+"."+category));
		}

	}

}
