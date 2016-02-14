package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.basics.structure.ir.TermFrequencyTuple;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliCategoriesXecutor;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * 
 * This class intends to join together all the necessary process to extract the 
 * articles related to a given category.
 * 
 * It covers from the extraction of the vocabulary up to the storage of the 
 * relevant articles.
 *  
 * @author albarron 
 * @author cristina
 *
 */
public class Xecutor {
		
	private List<TermFrequencyTuple> terms;
	
	private Locale locale;
	private int year;
	private double percentPositiveCats;
	private int maxDepth;
	private int minDepth;
	private int maxVocabulary;
	private int minNumArticles;
	private int topPercentage;
	private String categoryFileName;
	private String articlesFileName;
	private String dictFileName;
	private String statsFileName;
	
	// DONE move to a config file
	/*private final String categoryFileName = "%s.%d.%d.category";
	private final String articlesFileName = "%s.%d.%d.articles";
	private final String dictFileName = "%s.%d.dict";
	private final String statsFileName = "%s.%d.stats";
	private final int minArticlesVoc = 10;
	private final int topVocabulary = 10;
	private final int maxDepth = 50;
	private final int minDepth = 0;*/
	
	private String outPath;
	/**The ids of the articles to store */
	private Integer[] articleIDs;
	
	private static LumpLogger logger = 
			new LumpLogger (Xecutor.class.getSimpleName());

	public Xecutor(Locale locale, int year, double model, int top){
		Locale.setDefault(new Locale("en"));
		CHK.CHECK_NOT_NULL(locale);
		CHK.CHECK_NOT_NULL(year);
		this.locale = locale;
		this.year = year;
		this.percentPositiveCats = model;
		this.maxVocabulary = top;

		terms = new ArrayList<TermFrequencyTuple>();
	}

	public Xecutor(Locale locale, int year, double model, int maxDepth, int minDepth, 
			int top, int topPercentage, int minNumArticles, String categoryFileName, 
			String articlesFileName, String dictFileName, String statsFileName){
		Locale.setDefault(new Locale("en"));
		CHK.CHECK_NOT_NULL(locale);
		CHK.CHECK_NOT_NULL(year);
		this.locale = locale;
		this.year = year;
		this.percentPositiveCats = model;
		this.maxVocabulary = top;
		this.maxDepth = maxDepth;
		this.minDepth = minDepth;
		this.minNumArticles = minNumArticles;
		this.topPercentage = topPercentage;
		this.categoryFileName = categoryFileName;
		this.articlesFileName = articlesFileName;
	    this.dictFileName = dictFileName;
	    this.statsFileName = statsFileName;

		terms = new ArrayList<TermFrequencyTuple>();
	}
	
	
	public static void main(String[] args){
		
		WikipediaCliCategoriesXecutor cli = new WikipediaCliCategoriesXecutor();
		cli.parseArguments(args);

		// Variables from command line, including those that override the config file
		Locale locale = cli.getLanguage(); 	    //es
		int year = cli.getYear();         		//2013;
        int step = cli.getFirstStep();          //1						
        int end = cli.getLastStep();            //7						
		String sCategory = cli.getsCategory();  //"Historia_de_Aragón"
		int iCategory = cli.getiCategory();		//50428
		double model = cli.getModel();          //0.6
		int top = cli.getTop();					//100
		int depth = cli.getDepth();				//0
		
		// Variables from the config file
		int maxDepth = cli.getPropertyInt("maxDepth");
		int minDepth = cli.getPropertyInt("minDepth");
	    int minNumArticles = cli.getPropertyInt("minNumArticles");
		int topPercentage = cli.getPropertyInt("topPercentage");

		String categoryFileName = cli.getPropertyStr("categoryFileName");
		String articlesFileName = cli.getPropertyStr("articlesFileName");
		String dictFileName = cli.getPropertyStr("dictFileName");
		String statsFileName = cli.getPropertyStr("statsFileName");

		//Xecutor executor = new Xecutor(locale, year, model, top);
		Xecutor executor = new Xecutor(locale, year, model, maxDepth, minDepth, 
				top, topPercentage, minNumArticles, categoryFileName, 
				articlesFileName, dictFileName, statsFileName);
		
		executor.setOutPath(cli.getOutputFile());//*/

		/*// CUTREPROVA 
		Locale locale = new Locale("eu");
		int  year = 2015;
		//String sCategory = "Liliales"; 
		//int iCategory = 252707; 
		//String sCategory = "Religion"; 
		//int iCategory = 84974; 
		//String sCategory = "Mitjans de comunicació per país"; 
		//int iCategory = 250636; 
		//String sCategory = "Αρχαιολογία"; 
		//int iCategory = 6144;
		//String sCategory = "أسر مصرية قديمة"; 
		//int iCategory = 10892; 
				
		//String sCategory = "Φρανκφούρτη"; 
		//int iCategory = 42017; 
		//String sCategory = "Atheismus";  //de
		//int iCategory =953416; 
		String sCategory = "Informatika";  //EU
		int iCategory = 5784;
		int step = 1; 
		int end = 5; 
		int depth = 0; 
		Xecutor executor = new Xecutor(locale, year, 0.6, 100);
		executor.setOutPath("/home/cristinae/pln/wikipedia/articles/2015"); //*/

		
		switch (step) {
        case 1:
		//1. Run DomainKeywords
		//---------------------
		//The objective is extracting the vocabulary associated to the area.
		//Running it with t=10 (top 10%) should be enough. A maximum for the size
        //of the vocabulary is given 	
		//Keep the output file for step 3
        	logger.info("\nSTEP 1: Extracting in-domain vocabulary...");
        	executor.extractDomainKeywords(sCategory, iCategory);	
        	if (end==1) break;
		
        case 2:	
		//2. Run CategoryExtractor
		//------------------------
		//It extracts all the subcategories from the desired category. At this 
		//point, it should be called in verbose mode (true) and with a high maxdepth.
		//In that way, (almost) the entire tree will be generated and all the 
		//information will be reported.
		//Keep the output file for steps 3 and 4	
        	logger.info("\nSTEP 2: Extracting all subcategories of your domain... ");
        	executor.extractCategories(iCategory, true);
           	if (end==2) break;
		
        case 3:	
		//3. Run CategoryNameStats
		//------------------------
		//It will give you a figure of the percentage of in-domain category titles 
		//per level of the tree. You need the dictionary generated in step 1 as 
		//well as the categories' tree generated in step 2.	
        	logger.info("\nSTEP 3: Weighting of the extracted categories... ");
        	executor.computeCategoryStats(iCategory);
           	if (end==3) break;
		
        case 4:	
		//4. Run CategoryDepth
		//--------------------
		//Given the wanted percentage of in-domain category titles it searches for
		//the corresponding depth (>3 is forced) in the category tree
        	logger.info("\nSTEP 4: Selecting the appropiate limiting depth... ");
        	depth = executor.searchCategoryDepth(iCategory);
           	if (end==4) break;
		
        case 5:	
        // TODO Crec que no cal
		//5. Run createCategoriesFile
		//------------------------
		//It extracts all the subcategories from the desired category up to 
		//the depth obtained in step 4. It uses the file of step 2 and selects
		//the adequate ones.
        	logger.info("\nSTEP 5: Extracting the corresponding subcategories... ");
        	executor.createCategoriesFile(iCategory, depth);
           	if (end==5) break;
		
        case 6:	
		//6. Run ArticleSelector
		//----------------------
		//It will extract a list of the articles associated to a tree of 
		//categories up to the selected depth. You need the categories' tree 
		//generated in step 3.
        	logger.info("\nSTEP 6: Retrieving the in-domain articles IDs... ");
        	executor.selectArticles(iCategory, depth);
           	if (end==6) break;
		
        case 7:	
		//7. Run  ArticleTextExtractor
		//----------------------------
		//It will extract the articles' contents into plain text files. You need 
		//the list or articles produced in step 4.
           	logger.info("\nSTEP 7: Downloading the selected in-domain articles... ");
        	executor.extractTexts(iCategory, depth);				
           	if (end==7) break;
		}
		
		//END OF THE PROCESS		
		logger.info("END");
		
	}
	
	
	/**
	 * Obtains the vocabulary that represents the given category in the language 
	 * and year Wikipedia.
	 */
	public void extractDomainKeywords(String category, int iCategory) {
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
		terms = dkw.getTopTuplesPlus(category);
		String filename = String.format(dictFileName, locale, iCategory);
		File output = new File(outPath, filename);
		dkw.toFile(output, terms);		
				
	}
		
	

	public void extractCategories(int categoryID, boolean verbose){
		CategoryExtractor ce = new CategoryExtractor(locale, year, verbose, categoryFileName);		
		ce.getCategoryTree(categoryID, maxDepth);
		ce.setOutputDir(outPath);
		ce.toFile(categoryID, maxDepth);		
	}
		
	
	public void computeCategoryStats(int categoryID){
		HashSet<String> dict = new HashSet<String>();
		
		File f = new File(outPath, String.format(
				categoryFileName, locale, categoryID, maxDepth));
		
		for (TermFrequencyTuple tft : terms){
			dict.add(tft.getTerm());
		}
				
		CategoryNameStats cns = new CategoryNameStats(locale);		
		cns.loadDictionary(dict);			
		try {
			cns.loadCategoryNames(f);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		cns.computeStats();
		
		File fileStats = new File(outPath, 
		    String.format(statsFileName, locale, categoryID));
		cns.toFile(fileStats);
	}
	
    // A depth>minDepth is forced
	public int searchCategoryDepth(int categoryID) {
		File fileStats = new File(outPath, 
		    String.format(statsFileName, locale, categoryID));

		CategoryDepth cd = new CategoryDepth(
		    fileStats, percentPositiveCats, minDepth, maxDepth);
		int depth = cd.getDepthSplines();
        logger.info("Estimated depth in the category tree: " + depth);
		return depth;
	}

	/** Select the articles that are supposed to appear in a category */	
	public void selectArticles(int categoryID, int depth) {
		short shDepth = (short) depth;
		File input = new File(outPath, String.format(
				categoryFileName, locale, categoryID, depth));

		ArticleSelector artSelector = new ArticleSelector(input, locale, year);

		try {
			artSelector.extract(shDepth);
		} catch (WikiApiException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		articleIDs = artSelector.getArticles();

		String output = outPath + File.separator + 
		       String.format(articlesFileName, locale, categoryID, depth);
		artSelector.idsToFile(output);
		
	}

	public void extractTexts(int categoryID, int depth){
		String foldertmp = outPath + File.separator + locale + "." + categoryID;
		String folder = outPath + File.separator + locale + "." + categoryID + 
				File.separator + percentPositiveCats;
		try {
			FileIO.createDir(new File(foldertmp));
			FileIO.createDir(new File(folder));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.toString()+" error (@extractTexts)");
		}
		
		ArticleTextExtractor.extractSpecificArticles(
								locale, year, articleIDs, new File(folder));
	}

	public void setOutPath(String path){
		outPath = path;
						 
		CHK.CHECK(new File(outPath).isDirectory(), 
					"The output directory does not exist");		
	}
	
	private void createCategoriesFile(int categoryID, int depth) {
    File input = new File(outPath, String.format(
        categoryFileName, locale, categoryID, maxDepth));
    String output = outPath + File.separator + 
        String.format(categoryFileName, locale, categoryID, depth);
        
    FileInputStream fis;
    BufferedReader inBR = null;
    try {
      fis = new FileInputStream(input);
      inBR = new BufferedReader(new InputStreamReader(fis));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      logger.error("File " + input + " (@createCategoriesFile)");
    }
    FileWriter writer;
    BufferedWriter outBW = null;
    String line;
    Pattern pattern = Pattern.compile("^(\\d+)\\s+");
    try {
      writer = new FileWriter(output, true);
      outBW = new BufferedWriter(writer);
      while ((line = inBR.readLine()) != null) {
        Matcher matcher = pattern.matcher(line);
        matcher.find();
        if (Integer.parseInt(matcher.group(1)) <= depth) {
          outBW.write(line);
          outBW.newLine();          
        } else {
          break;
        }
      }   
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("I/O Error (@createCategoriesFile)");
    }   
    try {
      inBR.close();
      outBW.close();
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("Error when closing buffers (@createCategoriesFile)");
    }   
  }
	
}
