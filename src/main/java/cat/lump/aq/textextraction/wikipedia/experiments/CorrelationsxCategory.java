package cat.lump.aq.textextraction.wikipedia.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;


/**
 * Wikiparable: Experiment 2 for evaluation. 
 * 
 * The Spearman's and Kendall's rank correlation coefficients are used to assess the correlation between 
 * two corpus (See Kilgarriff 2001 for Spearman, extended to Kendall). 
 * Corpus A is the collection of root articles and is assumed to describe the category;
 * corpus B is the collection corresponding to the extracted category.
 * 
 * Collection A can be taken as a whole or one can limit its size to mimic the size of the same 
 * collection in another model
 * 
 * @author cristina
 * @since June 5, 2015
 * 
 * TODO This class shares at lot of code with experiment 1 MeanTFxCategory.java. Maybe they should extend a 
 * common class.
 */
public class CorrelationsxCategory {
	
	/** Maximum size of the vocabulary defining the domain*/
	private static final int MAX_VOCAB = 1000;

	/** Minimum number of occurrences of a term to be considered*/
	private static final int MIN_OCURR = 1;

	/** Maximum number of files that a folder stores as defined in io.FileManager */
	private static final int IDS_PER_DIR = 100000;
	
	/** Error code*/
	private static final int ERRORINT = 99;
	
	private String extractedArticlesExt = "extracted.articles";
	
	/** Language of the articles */
	private String lang;
	
	/** Model to analyse */
	private String model;
				
	/** Reference model. Correlations will be estimated for the first n elements of the 
	 * collection given by model being n the size of the modelRef collection */
	private String modelRef;

	/** Model from which we obtain the vocabulary 
	 * (we want to be sure that it includes the top 10% terms) */
	private String modelVocab = "0.5";

	/** Path to the directory where the original articles are stored. */
	private String rootDirectory;
	
	private static LumpLogger logger = 
			new LumpLogger (CorrelationsxCategory.class.getSimpleName());


	public CorrelationsxCategory (String language, String model, String modelRef, String path){
		CHK.CHECK_NOT_NULL(language);
		CHK.CHECK_NOT_NULL(path);
		this.lang = language;
		this.model = model;
		this.modelRef = modelRef;
		this.rootDirectory = path;
		if (model.equalsIgnoreCase("500") || model.equalsIgnoreCase("0.5-500")){
			extractedArticlesExt = "ranked.extracted.articles";
		}
	}


	private void processAll() {

		// Input folder
		File directory = new File(rootDirectory); 
        CHK.CHECK(directory.isDirectory(), "I cannot read the directory " + rootDirectory);	
        // Output file
        String csvFile = String.format("Correlations.%s.%s-%s.csv", lang, model, modelRef);
        File output = new File(rootDirectory + FileIO.separator + csvFile);
        String csvValues = "Spearman,Kendall,covariance,artsInCat,elements,catID,depth\n";
        
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
			String result = processCategory(folder);
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
	
	
	private String processCategory(File folder) {
		
		File category = new File(folder.toString() + FileIO.separator + model + FileIO.separator);
		String catID = FilenameUtils.getExtension(folder.toString());

		//TODO Aixo es una guarrada feta pq han canviat el nom de les carpetes
		//arreglar!!
		if (lang.equalsIgnoreCase("ar") || lang.equalsIgnoreCase("el")){
			modelVocab = "0.5--1";
		}
		File vocabulary = new File(folder.toString() + FileIO.separator + modelVocab + FileIO.separator);
		String fileName = lookForFile(vocabulary, "dict");
		File vocabularyF;
		if (!fileName.matches("NONE") ){
			 vocabularyF = new File(fileName);
		} else {
			logger.warn("No dict for category " + catID );
			return String.valueOf(ERRORINT);
		}

		File articlesF;
		fileName = lookForFile(category, extractedArticlesExt);
		String depth = "0";
		if (!fileName.matches("NONE")){
			articlesF = new File(fileName);
			Pattern p = Pattern.compile(".(\\d+).".concat(extractedArticlesExt));
			Matcher m = p.matcher(fileName);
			if (m.find()) depth=m.group(1);
		} else {
			logger.warn("No extracted.articles for category " + catID);
			return String.valueOf(ERRORINT);
		}
		
		extractedArticlesExt = "extracted.articles";  //aixo es una guarrada
		// If there is a reference model look for the maximum size of the collection to be considered
		int sizeRef=-1;
		if (!modelRef.matches("NONE")){
			File categoryRef = new File(folder.toString() + FileIO.separator + modelRef + FileIO.separator);
			String fileNameRef = lookForFile(categoryRef, extractedArticlesExt);

			if (!fileNameRef.matches("NONE")){
				try {
					sizeRef = FileIO.fileCountLines(new File(fileNameRef));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				logger.warn("No extracted.articles for the category " + catID + " in the reference model");
				return String.valueOf(ERRORINT);
			}			
		}

							
		// Find the vocabulary into the articles
		//List<String> articlesID = readArticlesID(articlesF);
		List<String> articlesID = readArticlesIDsizeMax(articlesF, sizeRef);
		int size = articlesID.size();
		if (size == 0){
			logger.warn("No extracted.articles for category " + catID);
			return String.valueOf(ERRORINT);
		}
		
		HashMap<String, Integer> hmapTFcat= new HashMap<String, Integer>();
		for (String id : articlesID) {
			HashMap<String, Integer> hmapTFs= readArticleTFs(id);
			hmapTFs.remove("MAX_FREQUENCY");  // this makes no sense for this experiment
			// add the frequencies of every article to the category TF hashmap
			for (String key : hmapTFs.keySet()){
				int newValue = hmapTFs.get(key);
				if (hmapTFcat.get(key) != null ){
					newValue = newValue + hmapTFcat.get(key);
				}
				hmapTFcat.put(key, newValue);
			}
		} //end articles		
		
		//Let's take the first MAX_VOCAB terms from the full category 
		ValueComparator comp =  new ValueComparator(hmapTFcat);
        TreeMap<String,Integer> sortedTFcat = new TreeMap<String,Integer>(comp);        
		sortedTFcat.putAll(hmapTFcat);
        int i = 0;
        TreeMap<String, Integer> topTFcat =  new TreeMap<String,Integer>();
        for (String key : sortedTFcat.keySet()) {
        	topTFcat.put(key, hmapTFcat.get(key));
           if (i == MAX_VOCAB || hmapTFcat.get(key) <= MIN_OCURR) break;
           i++;
        }
		
        HashMap<String, Integer> terms = readTerms(vocabularyF);

        //Look for a common set of terms (sumTerms). The ones in a collection that do not appear on the
        //other collection are included with 0 frequency
        HashMap<String, Integer> sumhmap = (HashMap<String, Integer>) terms.clone();
        sumhmap.putAll(topTFcat);
        Set<String> sumTerms = sumhmap.keySet();
        if (sumTerms.size()<5){
        	logger.warn("Only " + sumTerms.size() + " elements for category " 
                         + catID + ", correlations not estimated\n");
        	return String.valueOf(ERRORINT);
        }
        //Convert for input into the Correlation's class
        double[] collectionA = new double[sumTerms.size()];
        double[] collectionB = new double[sumTerms.size()];
        int j=0;
        for(String key : sumTerms){
         	collectionA[j] = (terms.get(key) != null ) ? terms.get(key).doubleValue() : 0.0;
        	//logger.info("colA " + collectionA[j]);
         	collectionB[j] = (topTFcat.get(key) != null ) ? topTFcat.get(key).doubleValue() : 0.0;
        	//logger.info("colB " + collectionB[j]);
        	j++;
        }
        
        //Call Spearmans
        SpearmansCorrelation sc = new SpearmansCorrelation();
        double spearman = sc.correlation(collectionA,collectionB);
        //Call Kendalls (Kendall's Tau-b rank correlation)
        KendallsCorrelation kc = new KendallsCorrelation();
        double kendall = kc.correlation(collectionA,collectionB);
        //Covariance -- Let's save time
        //Covariance cov = new Covariance();
        //double covariance = cov.covariance(collectionA,collectionB);
        double covariance = 0.0;
        
	    String resultCSV = String.format(Locale.ENGLISH,"%.4f", spearman) 
	    		+ "," + String.format(Locale.ENGLISH,"%.4f", kendall) 
	    		+ "," + String.format(Locale.ENGLISH,"%.4f", covariance) 
	    		+ ","  + size + "," + sumTerms.size() + "," + catID + "," + depth + "\n";
	    logger.info("Spearman " +  String.format(Locale.ENGLISH,"%.4f", spearman)  
	    		+ " Kendall " + String.format(Locale.ENGLISH,"%.4f", kendall)  
	    		+ " Covariance " + String.format(Locale.ENGLISH,"%.4f", covariance) 
	    		+ " ( " + size + " articles, " + sumTerms.size() 
	    		+ " elements) for category "+ catID + " at depth " + depth);
	    return resultCSV;
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


	/**
	 * For an article with id {@code id}, the frequencies for all its terms are retrieved from 
	 * the file generated by prepo.TermExtractor. The highest frequency seen in the document is
	 * also stored as that of the MAX_FREQUENCY term
	 * 
	 * @param id
	 * @return HashMap<String, Integer> hmapTFs
	 */
	private HashMap<String, Integer> readArticleTFs(String id) {
		
		int index =  Integer.parseInt(id)/IDS_PER_DIR;
		String pathToFile = rootDirectory + FileIO.separator + "tfs" + FileIO.separator + lang
				+ FileIO.separator + index + FileIO.separator + id + "." + lang + ".txt" ;
		
		HashMap<String, Integer> hmapTFs = new HashMap<String, Integer>();		
	    BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File(pathToFile)));
	        String line = br.readLine();
	        String[] columns;
        	//The first term is that with the highest frequency, its value is stored independently
	        if (line != null) {
	        	columns = line.split("\\s+");  
	        	hmapTFs.put("MAX_FREQUENCY", Integer.parseInt(columns[0]));  
	        } else {
	        	hmapTFs.put("MAX_FREQUENCY", 0);  
	        }
	        while (line != null) {
		        columns = line.split("\\s+");
		        hmapTFs.put(columns[1], Integer.parseInt(columns[0]));
	            line = br.readLine();
	        }
	        br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hmapTFs;
	}


	/**
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
	

	/**
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



	/**
	 * Reads {@codeFile vocabularyF} and extracts a hashmap of terms with the characteristic
	 * vocabulary of the category
	 * 
	 * @param vocabularyF
	 * @return HashMap<String,Integer> terms 
	 * 			 hashmap with at most the top {@code MAX_VOCAB} terms
	 */
	private HashMap<String,Integer> readTerms(File vocabularyF) {
		
		HashMap<String, Integer> hmapTerms = new HashMap<String, Integer>();		
	    BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(vocabularyF));
	        String line = br.readLine();
	        int i=1;
	        while (line != null) {
		        String[] columns = line.split("\\s+");
		        if (Integer.parseInt(columns[0]) <= MIN_OCURR) break;
		        hmapTerms.put(columns[1], Integer.parseInt(columns[0]));
		        if (i > MAX_VOCAB) break;
	            line = br.readLine();
	            i++;
	        }
	        br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hmapTerms;
	}
	
	


	private static CommandLine parseArguments(String[] args)
	{
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cLine = null;
		Options options= new Options();
		CommandLineParser parser = new BasicParser();

		options.addOption("l", "language", true, 
					"Language of interest (e.g., en, es, ca)");		
		options.addOption("m", "model", true, 
					"Model to analyse (e.g., 0.5, 0.6--1, 100, IR999)");	
		options.addOption("n", "modelRef", true, 
			        "Reference model to limit the size (e.g., 0.5, 0.6--1, 100, IR999)");		
		options.addOption("h", "help", false, "This help");
		options.addOption("p", "path2root", true,
				    "Path to the folder where plain/ is (default: current)");
		options.addOption("c", "categoryID", true,
			        "Category to analyse (default: all categories in path2root)");

		try {			
		    cLine = parser.parse( options, args );
		} catch( ParseException exp ) {
			logger.error( "Unexpected exception:" + exp.getMessage() );			
		}	
		
		if (cLine == null || !(cLine.hasOption("l")) ) {
			logger.error("Please, set the language\n");
			formatter.printHelp(CorrelationsxCategory.class.getSimpleName(),options);
			System.exit(1);
		}		
		if (!(cLine.hasOption("m")) ) {
			logger.error("Please, set the model\n");
			formatter.printHelp(CorrelationsxCategory.class.getSimpleName(),options);
			System.exit(1);
		}		
		if (!(cLine.hasOption("p")) ) {
			logger.error("Please, set the path to the root folder of the language\n");
			formatter.printHelp(CorrelationsxCategory.class.getSimpleName(),options);
			System.exit(1);
		}		
		
		if (cLine.hasOption("h")) {
			formatter.printHelp(CorrelationsxCategory.class.getSimpleName(),options );
			System.exit(0);
		}
	
		return cLine;		
	}

	/**
	 * Main function to run the class, serves as example
	 * 
	 * @param args
	 * 		-l Language of the articles
	 * 		-m Model to analyse
	 * 		-p Path to the root folder for the given language
	 * 		-c Category to analyse (optional)
	 */
	public static void main(String[] args) {

		CommandLine cLine = parseArguments(args);
		
		String language = cLine.getOptionValue("l");
		String model = cLine.getOptionValue("m");
		String modelRef = cLine.hasOption("n") ?
				  cLine.getOptionValue("n") : "NONE";	
		String path = cLine.hasOption("p") ?
			    cLine.getOptionValue("p") : System.getProperty("user.dir");	
		String category = cLine.hasOption("c") ?
				cLine.getOptionValue("c") : "ALL";	
	
		/*String path = "/home/cristinae/pln/wikipedia/categories/extractions/oc.0";
		String model = "0.5";
		String language = "oc";*/
		CorrelationsxCategory corr = new CorrelationsxCategory(language, model, modelRef, path);
		if (category.equals("ALL")) {
			corr.processAll();
		} else {
			corr.processCategory(new File(path+"/"+language+"."+category));
		}
	}

}

/**
 * @author 	http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
 */
class ValueComparator implements Comparator<String> {
    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}