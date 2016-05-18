package cat.lump.aq.textextraction.wikipedia.fragments;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliFragmentsXecutor;
import cat.lump.aq.textextraction.wikipedia.utilities.ArticlesTranslator;
import cat.lump.aq.textextraction.wikipedia.utilities.CommonArticlesFinder;

/**
 * 
 * This class intends to join together all the necessary process to extract 
 * comparable fragments from articles belonging to a given category.
 * 
 * It covers from the alignment of the articles in the different languages to the
 * extraction of the comparable fragments as those with the highest similarity
 *  
 * @author cristina
 *
 */
public class Xecutor {
		
	private Locale locale1;
	private Locale locale2;
	private int year;
	private String [] langs = null;
	
	// TODO move to a config file
	private final double percentPositiveCats = 0.5;
	private int sgeThreads = 4;
	private String decoder = "moses";

	/**Root folder to locate inputs and store outputs*/
	private String directory;
	
	private static LumpLogger logger = 
			new LumpLogger (Xecutor.class.getSimpleName());

	public Xecutor(Locale locale1, Locale locale2, int year){
		Locale.setDefault(new Locale("en"));
		CHK.CHECK_NOT_NULL(locale1);
		CHK.CHECK_NOT_NULL(locale2);
		CHK.CHECK_NOT_NULL(year);
		this.locale1 = locale1;
		this.locale2 = locale2;
		this.year = year;
		langs[0] = locale1.toString();
		langs[1] = locale2.toString();
	}
			

	
	public static void main(String[] args){
		
		WikipediaCliFragmentsXecutor cli = new WikipediaCliFragmentsXecutor();
		cli.parseArguments(args);

		Locale locale1 = cli.getLanguage(); 	    //ca
		Locale locale2 = cli.getLanguage2(); 	    //eu
		int year = cli.getYear();         		    //2013;
	    int step = cli.getFirstStep();          	//1						
	    int end = cli.getLastStep();            	//7						
		String sCategory1 = cli.getsCategory1();    //"Informàtica"
		int iCategory1 = cli.getiCategory1();		//13684
		String sCategory2 = cli.getsCategory2();    //"Informatika"
		int iCategory2 = cli.getiCategory2();		//5784
        String [] filesID = cli.getFilesID();       //13684.ca.9.articles and 5784.eu.6.articles
		String method = cli.getMethod();			//"intersection"
		int trad = cli.getTranslation();			//1
		String outputFileCommon = cli.getFileCommonArticles();//ca.13684.eu.5784.union
		

		/*// CUTREPROVA 
		Locale locale1 = new Locale("ca");
		Locale locale2 = new Locale("eu");
		int  year = 2013;
		String sCategory1 = "Informàtica"; 
		String sCategory2 = "Informatika"; 
		int step = 1; 
		int end = 7; 
		int trad = 1;
		String outputFileCommon = "";*/

		Xecutor executor = new Xecutor(locale1, locale2, year);
		executor.setDir(cli.getDirectory());

		switch (step) {
        case 1:
		//1. Run CommonArticleFinder
		//--------------------------
		//It selects the articles that are common in both languages
        //Two methods available: intersection and union. Now, for union
        //the articles that are new to a language are not downloaded	
        	outputFileCommon = executor.selectCommonArticles(filesID, method);	
       	   	if (end==1) break;
        
        case 2:
    	//2. Run ArticlesTranslator
    	//------------------------------
   		//The previously selected articles are translated in the two directions.
        //Translations are needed later for a family of similarity measures.	
        //This step is optional since the user could use its own decoder (preferred
        //option for tokenisation issues)
        	if (trad==1){
        		executor.translateArticles(iCategory1, iCategory2, outputFileCommon);	
        	}
        	if (end==2) break;
        	
        case 3:
    	//3. Run ArticlesSimilarity  
    	//-------------------------
   		//        
           	if (end==3) break;

		//END OF THE PROCESS		
		logger.info("END");
		}
		
	}

	/** 
	 * Translates the subset of articles that are common to two languages L1, L2, both
	 * L1toL2 and L2toL1. The common articles are read from {@code String outputFileCommon} 
	 * and the articles are assumed to be in the folder defined by its category 
	 * {@code iCategory1}, {@code iCategory2}.
	 * Articles with already a translation are ignored and the processes are sent
	 * in parallel to a number of processors determined by the minimum of {@code sgeThreads}
	 * and the number of available processors.
	 * 
	 * @param iCategory1
	 * @param iCategory2
	 * @param outputFileCommon
	 */
	private void translateArticles(int iCategory1, int iCategory2, String outputFileCommon) {
		String folderIn1 = directory + File.separator + iCategory1 + File.separator +
				percentPositiveCats;
		String folderIn2 = directory + File.separator + iCategory2 + File.separator +
				percentPositiveCats;
		boolean onlyMissing = true;  //if the translation is already there, ignore
		
		// L1toL2 translation
		ArticlesTranslator tradArt1 = new ArticlesTranslator(langs, folderIn1);	
		tradArt1.generateSetsCommon(outputFileCommon, onlyMissing); 
		try{
			tradArt1.translateSets(decoder, sgeThreads);			
		} catch(InterruptedException ex){
			Thread.currentThread().interrupt();
		    logger.error("Interrupted exception " + ex.getCause());
		} catch(ExecutionException ex){
			logger.error("Problem executing " +decoder+": " + ex.getCause());
	    }
		tradArt1.reconstructTradArticlesCommon(outputFileCommon, onlyMissing);

		// L2toL1 translation
		String[] langsInv = {langs[1], langs[0]};
		ArticlesTranslator tradArt2 = new ArticlesTranslator(langsInv, folderIn2);	
		String fileCommonInv = tradArt2.mirrorCommonArticles2Langs(outputFileCommon);
		tradArt2.generateSetsCommon(fileCommonInv, onlyMissing); 
		try{
			tradArt2.translateSets(decoder, sgeThreads);			
		} catch(InterruptedException ex){
			Thread.currentThread().interrupt();
		    logger.error("Interrupted exception " + ex.getCause());
		} catch(ExecutionException ex){
			logger.error("Problem executing " +decoder+": " + ex.getCause());
	    }
		tradArt2.reconstructTradArticlesCommon(fileCommonInv, onlyMissing);

	}
	
	
    /** 
     * Finds the intersection/union of articles between two languages given a list of articles
     * for every language
     * @param filesID
     * @return
     * 		A string with the name of the file with the resulting articles
     */
	private String selectCommonArticles(String[] filesID, String method) {

		String outputFile = "";
		try {
			CommonArticlesFinder artFinder = new CommonArticlesFinder(langs, year, filesID, new File(directory));
			artFinder.checkAllTablesAvailable();
			if (method.equalsIgnoreCase("intersection")){
				String smallestLang = artFinder.lookForMinimumNumber();
				artFinder.findIntersection(smallestLang);
			} else if (method.equalsIgnoreCase("union")) {
				String largestLang = artFinder.lookForMaximumNumber();
				artFinder.findUnion(largestLang);
			}
			outputFile = artFinder.getPrefixOutputFile() + method;
			artFinder.closeConnection();
		} catch (Throwable e) {
			logger.warn("Exception @selectCommonArticles");
			e.printStackTrace();
		}

		return outputFile;
	}


	public void setDir(String path){
		directory = path;					 
		CHK.CHECK(new File(directory).isDirectory(), 
					"The input/output directory does not exist");		
	}

}	