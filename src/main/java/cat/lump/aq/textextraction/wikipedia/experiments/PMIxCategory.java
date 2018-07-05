package cat.lump.aq.textextraction.wikipedia.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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


/**
 * Wikiparable: Experiment 3 for evaluation. 
 * 
 * For a given category the most frequent 100 stems in the vocabulary (extracted from the root
 * articles) are selected. The frequency of these stems is extracted from the collection and the 
 * Pointwise Mutual Information (and its normalised version) are estimated on the category over all
 * the possible pairs of terms. 
 * The definition of (N)PMI is not evident here. We first define as a window a whole article and, as 
 * additional version, we sum probabilities per article instead of counts per article.
 * A collection can be taken as a whole or one can limit its size to mimic the size of the same 
 * collection in another model.
 * 
 * @author cristina
 * @since Jul 03, 2018
 * 
 * TODO This class shares at lot of code with experiment 1 and 2, MeanTFxCategory and CorrelationsxCategory.java. 
 * Maybe they should extend a common class.
 */
public class PMIxCategory {
	
	/** Maximum size of the vocabulary defining the domain*/
	private static final int MAX_VOCAB = 100;
	
	/** Maximum number of files that a folder stores as defined in io.FileManager */
	private static final int IDS_PER_DIR = 100000;
	
	/** Error code*/
	private static final int ERRORINT = 99;
	
	/** Small number to add as noise and avoid log(0) */
	private static final double EPSILON = 1E-20;

	private String extractedArticlesExt = "extracted.articles";
	
	/** Language of the articles */
	private String lang;
	
	/** Model to analyse */
	private String model;
	
	/** Reference model. The mean will be estimated for the first n elements of the 
	 * collection given by model being n the size of the modelRef collection */
	private String modelRef;
	
	/** Model from which we obtain the vocabulary 
	 * (some models -IRXXX- do not have the vocabulary) */
	private String modelVocab = "0.6-100";

	/** Path to the directory where the original articles are stored. */
	private String rootDirectory;
	
	private static LumpLogger logger = 
			new LumpLogger (MeanTFxCategory.class.getSimpleName());


	public PMIxCategory (String language, String model, String modelRef, String path){
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
        String csvFile = String.format("PMI.%s.%s-%s.csv", lang, model, modelRef);
        File output = new File(rootDirectory + FileIO.separator + csvFile);
        String csvValues = "pmiMedian,npmiMedian,pmiMean,npmiMean,pmiCatMedian,npmiCatMedian,pmiCatMean,npmiCatMean,artsInCat,catID,depth\n";
        
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
		// Extract the characteristic vocabulary for that category
		List<String> terms = readTerms(vocabularyF);
		String[] termsArr = new String[terms.size()];
		termsArr = terms.toArray(termsArr);
		int numPairs = termsArr.length*(termsArr.length-1)/2;
		
		// Initialise the measures
		double pmiMedian = 0.0;
		double npmiMedian = 0.0;
		double pmiMean = 0.0;
		double npmiMean = 0.0;
		double pmiCatMedian = 0.0;
		double npmiCatMedian = 0.0;
		double pmiCatMean = 0.0;
		double npmiCatMean = 0.0;
		int pair = 0;
		// If for us the window is the full article we need to keep track of the values for each article
		double[] countsX = new double[numPairs];
		double[] countsY = new double[numPairs];
		double[] countsXY = new double[numPairs];
		double[] numTermsPair = new double[numPairs];
		double[] combTerms2Pair = new double[numPairs];
		double[] pmi = new double[numPairs];
		double[] npmi = new double[numPairs];
		// If we want to solve the criticisms about a different-length window we need to keep track of the probabilities
		double[] pX = new double[numPairs];
		double[] pY = new double[numPairs];
		double[] pXY = new double[numPairs];
		double[] pmiCat = new double[numPairs];
		double[] npmiCat = new double[numPairs];
		for (int i=0; i<numPairs-1; i++){
			countsX[i]=0;
			countsY[i]=0;
			countsXY[i]=0;
			numTermsPair[i]=0;
			combTerms2Pair[i]=0;
			pX[i]=0;
			pY[i]=0;
			pXY[i]=0;
		}

		// Find the vocabulary into the articles
		List<String> articlesID = readArticlesIDsizeMax(articlesF, sizeRef);
		int size = articlesID.size();
		if (size == 0){
			logger.warn("No extracted.articles for category " + catID);
			return String.valueOf(ERRORINT);
		}	
		int numArticles=0;
		for (String id : articlesID) {
			HashMap<String, Integer> hmapTFs= readArticleTFsSum(id);
			// In case there is only a term, MI cannot be estimated
			if (hmapTFs.get("NUM_TERMS")==1)
				continue;
			
			double numTerms = (double) hmapTFs.get("NUM_TERMS");
			// number of possible combinations of pairs of words (m n) = m!/(n! (m-n)!) = m*(m-1)/2
			double combTerms2 = numTerms * (numTerms-1) / 2;
			if (numTerms==0){ 
				logger.warn("FLAG! Article without terms!");
				continue;
			}
			
			// Grab info to calculate MI related measures
			int i;
			int j;
			int frequency_i;
			int frequency_j;
			pair = 0;
			for (i=0; i<termsArr.length-2; i++) {
				if (hmapTFs.containsKey(termsArr[i])) {
					frequency_i = hmapTFs.get(termsArr[i]);
				} else {
					frequency_i = 0;				
				}
				double px = frequency_i/numTerms;
				
				for (j=i+1; j <= termsArr.length-1; j++) {
					if (hmapTFs.containsKey(termsArr[j])) {
						frequency_j = hmapTFs.get(termsArr[j]);
					} else {
						frequency_j = 0;				
					}
					
					double py = frequency_j/numTerms;
					double pxy = Math.min(frequency_i, frequency_j)/combTerms2;
					
		 		    numTermsPair[pair] = numTermsPair[pair] + numTerms;
					combTerms2Pair[pair] = combTerms2Pair[pair] + combTerms2;
					countsX[pair] = countsX[pair] + frequency_i;
					countsY[pair] = countsY[pair] + frequency_j;
					countsXY[pair] = countsXY[pair] + Math.min(frequency_i, frequency_j);
					pX[pair] = pX[pair] + px;
					pY[pair] = pY[pair] + py;
					pXY[pair] = pXY[pair] + pxy;
					pair++;
				}
			}
			numArticles++;
		} //end article	
		
		// Calculate MI related measures (two versions)
		for (int i=0; i<numPairs-1; i++){
			pmi[i] = PMI(countsX[i]/numTermsPair[i], countsY[i]/numTermsPair[i], countsXY[i]/combTerms2Pair[i], EPSILON);
			npmi[i] = NPMI(pmi[i], countsXY[i]/combTerms2Pair[i], EPSILON);
			pmiCat[i] = PMI(pX[i]/numArticles, pY[i]/numArticles, pXY[i]/numArticles, EPSILON);
			npmiCat[i] = NPMI(pmiCat[i], pXY[i]/numArticles, EPSILON);
		}
		
        pmiMean = mean(pmi);
        npmiMean = mean(npmi);
		Arrays.sort(pmi);
		Arrays.sort(npmi);
        pmiMedian = median(pmi);
        npmiMedian = median(npmi);

        pmiCatMean = mean(pmiCat);
        npmiCatMean = mean(npmiCat);
		Arrays.sort(pmiCat);
		Arrays.sort(npmiCat);
        pmiCatMedian = median(pmiCat);
        npmiCatMedian = median(npmiCat);

        logger.info("PMI " + String.format(Locale.ENGLISH,"%.4f", pmiMedian) 
	    		+  " NPMI " + String.format(Locale.ENGLISH,"%.4f", npmiMedian) 
	    		+  " <PMI> " + String.format(Locale.ENGLISH,"%.4f", pmiMean) 
	    		+  " <NPMI> " + String.format(Locale.ENGLISH,"%.4f", npmiMean) 
	            +  " PMIc " + String.format(Locale.ENGLISH,"%.4f", pmiCatMedian) 
	    	   	+  " NPMIc " + String.format(Locale.ENGLISH,"%.4f", npmiCatMedian) 
	     		+  " <PMI>c " + String.format(Locale.ENGLISH,"%.4f", pmiCatMean) 
	       		+  " <NPMI>c " + String.format(Locale.ENGLISH,"%.4f", npmiCatMean) 
	    		+ " (" + pair + " term pairs, " + size + " articles) for category "
	    		+ catID + " at depth " + depth);
	    String resultCSV = String.format(Locale.ENGLISH,"%.4f", pmiMedian) 
	    		+ "," + String.format(Locale.ENGLISH,"%.4f", npmiMedian) 
	    		+ "," + String.format(Locale.ENGLISH,"%.4f", pmiMean) 
	    		+ "," + String.format(Locale.ENGLISH,"%.4f", npmiMean) 
	    		+ "," + String.format(Locale.ENGLISH,"%.4f", pmiCatMedian) 
	    		+ "," + String.format(Locale.ENGLISH,"%.4f", npmiCatMedian) 
	    		+ "," + String.format(Locale.ENGLISH,"%.4f", pmiCatMean) 
	    		+ "," + String.format(Locale.ENGLISH,"%.4f", npmiCatMean) 
	    		+ "," + pair + "," + size + "," + catID + "," + depth + "\n";
	    return resultCSV;
	}
	
	

	/**
	 * Pointwise Mutual Information with the addition of a small constant term to avoid log(0)
	 * TODO: check the base of the logarithm
	 * 
	 * @param px
	 * @param py
	 * @param pxy
	 * @param epsilon
	 * @return double
	 */
	private double PMI(double px, double py, double pxy, double epsilon) {
		double pmi =  Math.log((pxy + epsilon) / (px*py + epsilon)) / Math.log(2.0);
		return pmi;
	}
	
	/**
	 * Normalised Pointwise Mutual Information as defined in
	 * Bouma, G. (2009). Normalized (pointwise) mutual information in collocation extraction. 
	 * In From form to meaning: Processing texts automatically, Proceedings of the biennial GSCL conference
	 * 2009, pp. 31-40, Tübingen.
	 * 
	 * @param px
	 * @param py
	 * @param pxy
	 * @param epsilon
	 * @return double
	 */
	private double NPMI(double px, double py, double pxy, double epsilon) {
		return -PMI(px, py, pxy, epsilon)/ Math.log(pxy + epsilon) / Math.log(2.0);
	}

	/**
	 * Normalised Pointwise Mutual Information as defined in
	 * Bouma, G. (2009). Normalized (pointwise) mutual information in collocation extraction. 
	 * In From form to meaning: Processing texts automatically, Proceedings of the biennial GSCL conference
	 * 2009, pp. 31-40, Tübingen.
	 * @param pmi
	 * @param pxy
	 * @param epsilon
	 * @return
	 */
	private double NPMI(double pmi, double pxy, double epsilon) {
		return -pmi / Math.log(pxy + epsilon) / Math.log(2.0);
	}
	
	
	/**
	 * Calculates the median of a vector
	 * The array must be sorted
	 * @param m
	 * @return
	 */
	private double median(double[] m) {
	    int middle = m.length/2;
	    if (m.length%2 == 1) {
	        return m[middle];
	    } else {
	        return (m[middle-1] + m[middle]) / 2.0;
	    }
	}
	
	/**
	 * Calculates the mean of a vector
	 * @param m
	 * @return
	 */
	public static double mean(double[] m) {
	    double sum = 0;
	    for (int i = 0; i < m.length; i++) {
	        sum += m[i];
	    }
	    return sum / m.length;
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
	 * the file generated by prepo.TermExtractor. The total number of terms are stored as 
	 * a fake term NUM_TERMS
	 * 
	 * @param id
	 * @return HashMap<String, Integer> hmapTFs
	 */
	private HashMap<String, Integer> readArticleTFsSum(String id) {
		
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
	 * Reads {@codeFile vocabularyF} and extracts a list of terms with the characteristic
	 * vocabulary of the category
	 * 
	 * @param vocabularyF
	 * @return List<String> terms 
	 * 			 list with at most the top {@code MAX_VOCAB} terms
	 */
	private List<String> readTerms(File vocabularyF) {
		
		List<String> terms =new ArrayList<String>();
	    BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(vocabularyF));
	        String line = br.readLine();
	        int i=1;
	        while (line != null) {
		        String[] columns = line.split("\\s+");
		        terms.add(columns[1]);
		        if (i > MAX_VOCAB) break;
	            line = br.readLine();
	            i++;
	        }
	        br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return terms;
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
			formatter.printHelp(MeanTFxCategory.class.getSimpleName(),options);
			System.exit(1);
		}		
		if (!(cLine.hasOption("m")) ) {
			logger.error("Please, set the model\n");
			formatter.printHelp(MeanTFxCategory.class.getSimpleName(),options);
			System.exit(1);
		}		
		if (!(cLine.hasOption("p")) ) {
			logger.error("Please, set the path to the root folder of the language\n");
			formatter.printHelp(MeanTFxCategory.class.getSimpleName(),options);
			System.exit(1);
		}		
		
		if (cLine.hasOption("h")) {
			formatter.printHelp(MeanTFxCategory.class.getSimpleName(),options );
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


		/*String path = "/home/cristinae/pln/WikiTailor/oc.0";
		String model = "0.5";
		String modelRef = "NONE";
		String language = "oc";
		String category = "esport"; */
		PMIxCategory pmicat = new PMIxCategory(language, model, modelRef, path);
		if (category.equals("ALL")) {
			pmicat.processAll();
		} else {
			pmicat.processCategory(new File(path+"/"+language+"."+category));
		}



	}

}
