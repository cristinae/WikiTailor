package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliArticleSelector;
import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * This class extracts all the articles belonging to a given category in 
 * Wikipedia 
 * 
 * @author cmops
 */
public class BackArticleSelector
{

	/**
	 * Wikipedia connection
	 */
	private WikipediaJwpl wiki;
	/**
	 * File with the list of categories
	 */
	private File categories;

	/**
	 * Extracted articles
	 */
	private HashSet<Integer> articleIDs;
	
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
	public BackArticleSelector(File categories, WikipediaJwpl wikiConnection)
	{
		wiki = wikiConnection;
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
		for (Integer categoryID : categoryList)
		{
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
	 * @throws WikiApiException
	 * @throws IOException
	 */
	public void toFile(File output) throws WikiApiException, IOException
	{
		logger.info("Saving articles to file " + output);
		
		StringBuilder sb = new StringBuilder();
		for (Integer articleID : articleIDs)
		{
			//There are some articles without title. We skip them
			try
			{
			String articleName = wiki.getPage(articleID).getTitle()
					.getWikiStyleTitle();
			String line = String.format("%d\t%s", articleID, articleName);
			sb.append(line).append("\n");
			} catch (WikiApiException e){
				logger.error(String.format("Article %d has no title", articleID));
				
			}
		}

		String text = sb.toString();
		FileIO.stringToFile(output, text, false);
	}

	private HashSet<Integer> loadCategories(File catFile, short depth)
			throws IOException
	{
		logger.info("Loading categories");
		HashSet<Integer> categoryList = new HashSet<Integer>();
		String[] lines = FileIO.fileToLines(catFile);
		for (String line : lines)
		{
			String[] data = line.split("\t");
			short catDepth = Short.parseShort(data[0]);
			if (catDepth <= depth)
			{
				int category = Integer.parseInt(data[1]);
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
	public static void main(String[] args) 
	throws WikiApiException, IOException
	{		
		WikipediaCliArticleSelector cli = new WikipediaCliArticleSelector();
		cli.parseArguments(args);
		
		short depth = cli.getDepth();
		Locale locale = cli.getLanguage();
		int year = cli.getYear();		
		File input = cli.getCategoryFile();		

		WikipediaJwpl wiki = new WikipediaJwpl(locale, year);
		BackArticleSelector artSelector = new BackArticleSelector(input, wiki);
				
		artSelector.extract(depth);
		
		File output = new File(input + "."+locale+".relatedarticles."+depth);

		artSelector.toFile(output);
		logger.info("End of the process");
	}

}
