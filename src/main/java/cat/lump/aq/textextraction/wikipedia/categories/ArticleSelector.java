package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliArticleSelector;
import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * This class extracts all the articles that belong to a given category in 
 * Wikipedia 
 * 
 * @author cmops
 */
public class ArticleSelector {

	/** Wikipedia connection */
	private WikipediaJwpl wiki;
	
	/** File with the list of categories */
	private File categories;

	/** Extracted articles */
	private Set<Integer> articleIDs;
	
	private static LumpLogger logger = 
			new LumpLogger (CategoryExtractor.class.getSimpleName());

	/**
	 * Constructor.
	 * 
	 * @param categories
	 *            File with the list of category IDs
	 * @param wikiConnection
	 *            Wikipedia connection
	 * @throws IOException
	 *             If the categories files cannot be read
	 */
	public ArticleSelector(File categories, Locale locale, int year) {
		CHK.CHECK_NOT_NULL(categories);
		CHK.CHECK_NOT_NULL(locale);
		CHK.CHECK_NOT_NULL(year);
		
		try {
			wiki = new WikipediaJwpl(locale, year);
		} catch (WikiApiException e) {		
			e.printStackTrace();
		}		
		
		this.categories = categories;
		articleIDs = new HashSet<Integer>();
	}

	/**
	 * Extracts the articles associated to the indicated categories.
	 * 
	 * @throws WikiApiException
	 * @throws IOException
	 */
	public void extract(short depth) throws WikiApiException, IOException
	{
		logger.info("Selecting articles");
		HashSet<Integer> categoryList = loadCategories(categories, depth);
		for (Integer categoryID : categoryList)	{
			Category category = wiki.getCategory(categoryID);
			articleIDs.addAll(category.getArticleIds());
		}
	}

	/**
	 * TODO ???
	 * Dumps to a file the extracted articles. One article per line.
	 * 
	 * @param output
	 *            File wherein the articles must been written.
	 */
	public void toFile(File output)	{
		logger.info("Saving articles into file " + output);
		
		StringBuilder sb = new StringBuilder();
		for (Integer articleID : articleIDs) {
			//Some articles have no title. We skip them
			try	{
			String articleName = wiki.getPage(articleID).getTitle()
					.getWikiStyleTitle();
			String line = String.format("%d\t%s", articleID, articleName);
			sb.append(line).append("\n");
			} catch (WikiApiException e){
				logger.error(String.format("Article %d has no title", articleID));
			}
		}

		try {
			FileIO.stringToFile(output, sb.toString(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stores the IDs of the extracted articles into a file; one ID per line.
	 * 
	 * @param output
	 *            Name for the file wherein the articles must been written.
	 * @throws IOException
	 */
	public void idsToFile(String output) {
		logger.info("Saving the IDs of the articles into file " + output);
		File outputFile = new File(output);		
		StringBuilder sb = new StringBuilder();
		
		for (Integer articleID : articleIDs){
			String line = String.format("%d", articleID);
			sb.append(line).append("\n");
		}

		try {
			FileIO.stringToFile(outputFile, sb.toString(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public Integer[] getArticles(){
		return articleIDs.toArray(new Integer[articleIDs.size()]);		
	}

	private HashSet<Integer> loadCategories(File catFile, short depth)
			throws IOException {
		logger.info("Loading categories");
		
		int category;
		HashSet<Integer> categoryList = new HashSet<Integer>();
		String[] lines = FileIO.fileToLines(catFile);
		for (String line : lines)	{
			String[] data = line.split("\t");
			short catDepth = Short.parseShort(data[0]);
			if (catDepth <= depth) {
				category = Integer.parseInt(data[1]);
				categoryList.add(category);
			}
		}
		return categoryList;
	}

	/**
	 * @param args
	 *            language year categories [depth]
	 * @throws WikiApiException
	 * @throws IOException
	 */
	public static void main(String[] args) throws WikiApiException, IOException	{		
		WikipediaCliArticleSelector cli = new WikipediaCliArticleSelector();
		cli.parseArguments(args);
		
		short depth = cli.getDepth();
		Locale locale = cli.getLanguage();
		int year = cli.getYear();		
		File input = cli.getCategoryFile();		
		String outpath = cli.getOutputPath();
		
		
		ArticleSelector artSelector = new ArticleSelector(input, locale, year);
				
		artSelector.extract(depth);
		
		File output = new File(
				String.format("%s%s%s.%s.relatedarticles.%d", 
						outpath, File.separator, input.getName(), 
						locale.getLanguage(), 
						depth)
				);

		artSelector.toFile(output);
		logger.info("End of the process");
	}

}
