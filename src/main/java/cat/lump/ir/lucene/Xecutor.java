package cat.lump.ir.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.basics.structure.ir.TermFrequencyTuple;
import cat.lump.aq.textextraction.wikipedia.categories.ArticleTextExtractor;
import cat.lump.aq.textextraction.wikipedia.categories.DomainKeywords;
import cat.lump.ir.lucene.cli.LuceneCliCategoriesXecutor;
import cat.lump.ir.lucene.index.LuceneIndexerWT;
import cat.lump.ir.lucene.query.WikiTailor2Query;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * 
 * This class intends to join together all the necessary process to extract the 
 * articles related to a given category through the Lucene engine.
 * 
 * It covers from the indexing of the Wikipedia articles up to the storage of the 
 * relevant articles.
 *  
 * @since Feb 7, 2016
 * @author cristina
 *
 */
public class Xecutor {
		
	
	private Locale locale;
	private int year;
	private int minNumArticles;
	private int topPercentage;
	private int maxVocabulary;
	private String vocabFile;
	
	private static LumpLogger logger = 
			new LumpLogger (Xecutor.class.getSimpleName());

	public Xecutor(Locale locale, int year, int minNumArts, int topPercentage, int maxVocabulary,
			String vocabFile){
		Locale.setDefault(new Locale("en"));
		CHK.CHECK_NOT_NULL(locale);
		CHK.CHECK_NOT_NULL(year);
		this.locale = locale;
		this.year = year;
		this.minNumArticles = minNumArts;
		this.topPercentage = topPercentage;
		this.maxVocabulary = maxVocabulary;
		this.vocabFile = vocabFile;
	}
	
	
	public static void main(String[] args){
		
		LuceneCliCategoriesXecutor cli = new LuceneCliCategoriesXecutor();
		cli.parseArguments(args);

		// Variables from command line, including those that override the config file
		Locale locale = cli.getLanguage(); 	    //es
		int year = cli.getYear();         		//2013;
        int step = cli.getFirstStep();          //1						
        int end = cli.getLastStep();            //7	
        String inputDir = cli.getInputDir();    //./es/plain/es/
        String indexDir = cli.getIndexDir();    //./indexes/
        String outputDir = cli.getOutputDir();  //./LuceneExtr/
		String vocabFile = cli.getDictFile();   //es.521442.dict
		String sCategory = cli.getsCategory();  //"Historia_de_Aragón"
		int iCategory = cli.getiCategory();		//50428
		String labelCat = String.valueOf(iCategory);
		int top = cli.getTop();					//100
		
		// Variables from the config file
	    int minNumArticles = cli.getPropertyInt("minNumArticles4L");
		int topPercentage = cli.getPropertyInt("topPercentage4L");
		String dictTemplate = cli.getPropertyStr("dictFileName");
		String eArticlesFileName = cli.getPropertyStr("eArticlesFileName");

        // Initialise 
		Xecutor executor = new Xecutor(locale, year, minNumArticles, topPercentage, top, vocabFile);
		

		switch (step) {
        case 1:
        //1. Run DomainKeywords
        //---------------------
        // The objective is extracting the vocabulary associated to the main category.
        // Running it with t=10 (top 10%) should be enough. A maximum for the size
        // of the vocabulary is given 	
        	logger.info("\nSTEP 1: Extracting in-domain vocabulary... ");
            executor.extractDomainKeywords(sCategory, iCategory, dictTemplate, outputDir);	
            // Keywords are extracted here: File(outputDir, filename);
            if (end==1) break;

        case 2:	
        //2. Run ArticleTextExtractor
        //---------------------------
        // All the articles of the WP edition defined by (locale, year) are
        // downloaded from the database into plain text in the inputDir directory.
        // You can remove it after indexing (step 3)	
        	logger.info("\nSTEP 2: Downloading your Wikipedia edition... ");
         	ArticleTextExtractor.extractEntireWikipedia(locale, year, new File (inputDir));
            if (end==2) break;
 		
        case 3:	 
		//3. Run LuceneIndexerWT
		//-------------------------
		// Indexes the collection of articles of a full Wikipedia edition
           	logger.info("\nSTEP 3: Indexing your Wikipedia edition... ");
            executor.indexEdition(inputDir, indexDir);	
         	if (end==3) break;

        case 4:	
		//4. Run WikiTailor2Query
		//------------------------
        // The vocabulary terms of step 1 are used to query the Lucene engine with the
        // documents indexed in step 3. The output is the list of IDs of the selected
        // articles	
           	logger.info("\nSTEP 4: Retrieving the in-domain articles IDs... ");
    		WikiTailor2Query wt2q = new WikiTailor2Query(locale, inputDir, indexDir, 
    				outputDir, topPercentage, top, eArticlesFileName, false);
    		wt2q.processCategory(labelCat, new File(vocabFile));
        	if (end==4) break;
        	
        case 5:
    	//5. Run ArticleTextExtractor
    	//---------------------------
       	// Downloads the articles selected in the previous step
           	logger.info("\nSTEP 5: Downloading the selected articles... ");
    		File catIds = new File(outputDir, String.format(eArticlesFileName, locale, labelCat));
           	ArticleTextExtractor.extractSpecificArticles(locale, year, catIds, new File(outputDir));
           	if (end==5) break;
           	
        case 6:
        //6. Cleaning
        //-----------
        // Once you have the full edition indexed you don't need the full collection of
        // documents any more
        // What if the user sets outputDir==inputDir?	
         //  	logger.info("\nSTEP 6: Cleaning, deleting the articles downloaded for indexing... ");
         //	    FileIO.deleteDir(new File(inputDir));
                    	
		}
		
		//END OF THE PROCESS		
		logger.info("END");
		
	}
	
	
		
	/**
	 * Obtains the vocabulary that represents the given category in the language 
	 * and year Wikipedia.
	 * @param category
	 * @param iCategory
	 * @param dictFileName
	 */
	public void extractDomainKeywords(String category, int iCategory, 
			String dictFileName, String outputDir) {
		DomainKeywords dkw = new DomainKeywords(locale, year);
		
		dkw.setPercentage(topPercentage);
		dkw.setMaxSize(maxVocabulary);
		dkw.setMinNumArticles(minNumArticles);
		try {
			dkw.loadArticles(category);
		} catch (WikiApiException e) {		
			e.printStackTrace();
		}
		//Version without promoting the title of the category in the dictionary
		//dkw.computeTF();
		//terms = dkw.getTopTuples();
		dkw.computeTF(category);
		List<TermFrequencyTuple> terms = new ArrayList<TermFrequencyTuple>();
		terms = dkw.getTopTuplesPlus(category);
		
		vocabFile = String.format(dictFileName, locale, iCategory);
		File output = new File(outputDir, vocabFile);
		dkw.toFile(output, terms);		
				
	}
		

	/**
	 * Indexes the Wikipedia edition in language "locale" available at
	 * inputDir, and outputs the indexes at indexDir.
	 * 
	 * @param inputDir
	 * @param indexDir
	 */
	public void indexEdition(String inputDir, String indexDir) {
		LuceneIndexerWT lIndexer = new LuceneIndexerWT(inputDir, indexDir);
		lIndexer.setVerbose(true);		
		try {
			lIndexer.index();
			lIndexer.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
