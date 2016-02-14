package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.structure.ir.TermFrequencyTuple;
import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
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
public class XecutorTheFirst {

	private List<TermFrequencyTuple> terms;	
	private Locale locale;
	private int year;
	
	private final String categoryFileName = "%d.%s.%d.category";
	
	private String outPath;
	
	public XecutorTheFirst(Locale locale, int year){
		CHK.CHECK_NOT_NULL(locale);
		CHK.CHECK_NOT_NULL(year);
		this.locale = locale;
		this.year = year;
		terms = new ArrayList<TermFrequencyTuple>();
	}
			
	/**
	 * Obtains the vocabulary that represents the given category in the language 
	 * and year Wikipedia.
	 */
	public void extractDomainKeywords(String category) {
		DomainKeywords dkw = new DomainKeywords(locale, year);
		
		dkw.setPercentage(10);
		try {
			dkw.loadArticles(category);
		} catch (WikiApiException e) {		
			e.printStackTrace();
		}
		
		dkw.computeTF();
		terms = dkw.getTopTuples();
	}
	
	public void extractCategories(int categoryID, int maxDepth){
		CategoryExtractor ce = new CategoryExtractor(locale, year, categoryFileName);		
		ce.getCategoryTree(categoryID, maxDepth);
		ce.setOutputDir(outPath);
		ce.toFile(categoryID, maxDepth);		
	}

	public void computeCategoryStats(int categoryID, int maxDepth, String catFile){
		HashSet<String> dict = new HashSet<String>();
		
		File f = new File(outPath, String.format(
				categoryFileName, categoryID, locale, maxDepth));
		
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
		cns.toFile(new File(outPath +File.separator + catFile));		
	}
	
	public void setOutPath(String path){
	  CHK.CHECK(new File(path).isDirectory(), 
        "The output directory does not exist");
		outPath = path;
	}
	
	private static CommandLine parseArguments(String[] args) {
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cLine = null;
		Options options= new Options();
		CommandLineParser parser = new BasicParser();

		//MANDATORY
		options.addOption("l", "language", true, 
					"Language of interest (e.g., en, es, ca)");
		options.addOption("y", "year", true, 
					"Dump year (e.g. 2010, 2012, 2013)");
		options.addOption("m", "maxdepth", true,
				"Maximum depth to consider in the three (e.g. 5)");
		
		//ALTERNATIVELY
		options.addOption("c", "category", true, 
				"Name of the category (with '_' instead of ' '; you can use -n instead)");		
		options.addOption("n", "numcat", true,
				"Numerical identifier of the category (you can use -c instead)");
		
		//OPTIONAL
		options.addOption("o", "outpath", true,
				"Optional: save the output into this directory (default: current)");
		
		options.addOption("h", "help", false,
				"This help");
		try {			
		    cLine = parser.parse( options, args );
		} catch( ParseException exp ) {
			System.err.println( "Unexpected exception:" + exp.getMessage() );			
		}	
		
		if (cLine == null ||
			! ((cLine.hasOption("l") && cLine.hasOption("y") && cLine.hasOption("m"))))
		{
			System.err.println("Please, set language, category, and max depth\n");
			formatter.printHelp(CategoryNameStats.class.getSimpleName(), 
								options);
			System.exit(1);
		}
		
		if (cLine == null ||
			! ((cLine.hasOption("c") || cLine.hasOption("n")))){
				
			System.err.println("The category must be defined either with -c or -n\n");
			formatter.printHelp(CategoryNameStats.class.getSimpleName(), 
								options);
			System.exit(1);
		}
		
		if (cLine.hasOption("h"))
		{
			formatter.printHelp(CategoryNameStats.class.getSimpleName(),
								options );
			System.exit(0);
		}
	
		return cLine;		
	}
	
		
	public static void main(String[] args){
		
		CommandLine cLine = parseArguments(args);
		Locale locale = new Locale(cLine.getOptionValue("l"));	//es
		int year = Integer.valueOf(cLine.getOptionValue("y"));//2013;

		String sCategory = null;
		int iCategory = -1;
				
		WikipediaJwpl wiki;
		try {
			wiki = new WikipediaJwpl(locale, year);
			if (cLine.hasOption("c")){
				sCategory = cLine.getOptionValue("c");//"Historia_de_Aragón";
				iCategory = wiki.getCategory(sCategory).getPageId();
			} else {
				iCategory = Integer.valueOf(cLine.getOptionValue("n"));//50428;
				sCategory = wiki.getCategory(iCategory).getTitle().getPlainTitle();
			}
		} catch (WikiApiException e) {
			e.printStackTrace();
		}				
		
		int maxDepth = Integer.valueOf(cLine.getOptionValue("m")); //5;
		
		XecutorTheFirst executor = new XecutorTheFirst(locale, year);
		
		executor.setOutPath(
				cLine.hasOption("o") ?
						  cLine.getOptionValue("o")
						: System.getProperty("user.dir")				
				);
		
		
		//1. Run DomainKeywords (.jar ready)
		//---------------------
		//
		//The objective is extracting the vocabulary associated to the area.
		//Running it with t=10 (top 10%) should be enough.
		//
		//	Keep the output file for step 3
		executor.extractDomainKeywords(sCategory);		
		
		//2. Run CategoryExtractor
		//------------------------
		//
		//It extracts all the subcategories from the desired category. At this 
		//time, it should be called in verbose mode and without defining 
		//maxdepth. In that way, the entire tree will be generated and all the 
		//information will be reported.
		//Keep the output file for steps 3 and 4		
		executor.extractCategories(iCategory, maxDepth);
		
		//3. Run CategoryNameStats
		//------------------------
		//
		//It will give you a figure of the percentage of in-domain category titles 
		//per level of the tree. You need the dictionary generated in step 1 as 
		//well as the categories' tree generated in step 2.
		
		executor.computeCategoryStats(iCategory, maxDepth, 
				String.valueOf(iCategory));

		//END OF THE PROCESS		
		
	}
	
}
