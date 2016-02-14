package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliArticleSelector;
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
 *
 */
public class XecutorTheSecond {

	//private List<TermFrequencyTuple> terms;
	
	private Locale locale;
	private int year;
	
	/**The ids of the articles to store */
	private Integer[] articleIDs;
	
//	private final String relatedArticles = "%s%s%s.%s.relatedarticles.%d";
	
	private String outPath;
	
	public XecutorTheSecond(Locale locale, int year){
		CHK.CHECK_NOT_NULL(locale);
		CHK.CHECK_NOT_NULL(year);
		this.locale = locale;
		this.year = year;
		//terms = new ArrayList<TermFrequencyTuple>();
		setOutPath(System.getProperty("user.dir"));
	}
			
	/** Select the articles that are supposed to appear in a category */	
	public void selectArticles(File input, short depth) { 
		ArticleSelector artSelector = new ArticleSelector(input, locale, year);

		try {
			artSelector.extract(depth);
		} catch (WikiApiException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//in case we also want to save it
//		File output = new File(
//				String.format(relatedArticles, 
//						outPath, File.separator, input.getName(), 
//						locale.getLanguage(), 
//						depth)
//				);
//		artSelector.toFile(output);
		
		articleIDs = artSelector.getArticles();
		
	}

	public void extractTexts(){
		ArticleTextExtractor.extractSpecificArticles(
								locale, year, articleIDs, new File(outPath));
	}
	
	
//	public void extractCategories(int categoryID, int maxDepth){
//		CategoryExtractor ce = new CategoryExtractor(locale, year);		
//		ce.getCategoryTree(categoryID, maxDepth);
//		ce.setOutputDir(outPath);
//		ce.toFile(categoryID, maxDepth);		
//	}
//	
//			
//	
//	
//	public void computeCategoryStats(int categoryID, int maxDepth, String catFile){
//		HashSet<String> dict = new HashSet<String>();
//		
//		File f = new File(outPath, String.format(
//				categoryFileName, categoryID, locale, maxDepth));
//		
//		for (TermFrequencyTuple tft : terms){
//			dict.add(tft.getTerm());
//		}
//		
//		
//		CategoryNameStats cns = new CategoryNameStats(locale);		
//		cns.loadDictionary(dict);			
//		try {
//			cns.loadCategoryNames(f);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
//		cns.computeStats();
//		
//		cns.toFile(outPath +File.separator + catFile);		
//	}
	
	public void setOutPath(String path){
		outPath = path;
						 
		CHK.CHECK(new File(outPath).isDirectory(), 
					"The output directory does not exist");		
	}
	
//	private static CommandLine parseArguments(String[] args)
//	{
//		HelpFormatter formatter = new HelpFormatter();
//		CommandLine cLine = null;
//		Options options= new Options();
//		CommandLineParser parser = new BasicParser();
//
//		//MANDATORY
//		options.addOption("l", "language", true, 
//					"Language of interest (e.g., en, es, ca)");
//		options.addOption("y", "year", true, 
//					"Dump year (e.g. 2010, 2012, 2013)");
//		options.addOption("m", "maxdepth", true,
//				"Maximum depth to consider in the three (e.g. 5)");
//		
//		//ALTERNATIVELY
//		options.addOption("c", "category", true, 
//				"Name of the category (with '_' instead of ' '; you can use -n instead)");		
//		options.addOption("n", "numcat", true,
//				"Numerical identifier of the category (you can use -c instead)");
//		
//		//OPTIONAL
//		options.addOption("d", "directory", true,
//				"Optional: save the output into this directory (default: current)");
//		
//		options.addOption("h", "help", false,
//				"This help");
//		try {			
//		    cLine = parser.parse( options, args );
//		} catch( ParseException exp ) {
//			System.err.println( "Unexpected exception:" + exp.getMessage() );			
//		}	
//		
//		if (cLine == null ||
//			! ((cLine.hasOption("l") && cLine.hasOption("y") && cLine.hasOption("m"))))
//		{
//			System.err.println("Please, set language, category, and max depth\n");
//			formatter.printHelp(CategoryNameStats.class.getSimpleName(), 
//								options);
//			System.exit(1);
//		}
//		
//		if (cLine == null ||
//			! ((cLine.hasOption("c") || cLine.hasOption("n")))){
//				
//			System.err.println("The category must be defined either with -c or -n\n");
//			formatter.printHelp(CategoryNameStats.class.getSimpleName(), 
//								options);
//			System.exit(1);
//		}
//		
//		if (cLine.hasOption("h"))
//		{
//			formatter.printHelp(CategoryNameStats.class.getSimpleName(),
//								options );
//			System.exit(0);
//		}
//	
//		return cLine;		
//	}
	
		
	public static void main(String[] args){
		
		//TODO the output files do not have the same exit format!!
		
		//CommandLine cLine = parseArguments(args);
		
		//TODO merge all the CLIs into one abstract and its extensions
		
		WikipediaCliArticleSelector cli = new WikipediaCliArticleSelector();
		cli.parseArguments(args);
		
		short depth = cli.getDepth();
		Locale locale = cli.getLanguage();
		int year = cli.getYear();		
		File inputForSelector = cli.getCategoryFile();		
		String outpath = cli.getOutputPath();
		
//		WikipediaCliArticleTextExtractor cli2 = new WikipediaCliArticleTextExtractor();
//		cli2.parseArguments(args);	
//		File inputForExtractor = cli2.getArticlesFile();
		
		XecutorTheSecond executor = new XecutorTheSecond(locale, year);
		
		executor.setOutPath(outpath);
		
		//4. Run ArticleSelector
		//----------------------
		//
		//It will extract a list of the articles associated to a tree of 
		//categories up to the selected depth. You need the categories' tree 
		//generated in step 3.
		executor.selectArticles(inputForSelector, depth);
		
		
		//5. Run  ArticleTextExtractor
		//----------------------------
		//
		//It will extract the articles' contents into plain text files. You need 
		//the list or articles produced in step 4.
		executor.extractTexts();				
		
		//END OF THE PROCESS
				
	}
}
