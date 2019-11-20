package cat.lump.aq.textextraction.wikipedia.prepro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import org.sweble.wikitext.engine.CompilerException;
import org.sweble.wikitext.lazy.LinkTargetException;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.io.FileManager;
import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
//import cat.talp.lump.josu.prepro.type.TypePreprocess;

public abstract class AbstractPreprocess extends Thread
{
	private static final String LINE_SEPARATOR = "\n";
	/** Type of preprocess */
	private final TypePreprocess type;
	/** List of page IDs to preprocess. */
	private HashSet<Integer> pages;
	/** Wikipedia JWPL connector */
	protected final WikipediaJwpl wiki;
	/** Language of the Wikipedia dump */
	//protected final String language;
	protected final Locale locale;
	
	private static LumpLogger logger = 
			new LumpLogger (AbstractPreprocess.class.getSimpleName());

	/**
	 * Root directory wherein the output will be stored. Its path is
	 * {@code root/type/lang} where:
	 * <ul>
	 * <li>{@code root} is a given parent directory.</li>
	 * <li>{@code type} is the type of the preprocessing procedure.</li>
	 * <li>{@code lang} is the language of the preprocesed Wikipedia.</li>
	 * </ul>
	 */
	protected File rootDirectory;

	/**
	 * Creates a preprocess method able to preprocess pages from the Wikipedia
	 * dump identified by {@code language} and {@code year}. The preprocess is
	 * identified by {@code name}
	 * 
	 * This new instance doesn't implements the {@link #preprocess()} function.
	 * 
	 * @param type
	 *            Type of preprocess
	 * @param name
	 *            Identifier
	 * @param language
	 *            Language of the Wikipedia dump
	 * @param year
	 *            Year of the Wikipedia dump
	 * @param rootDirectory
	 *            Directory wherein the preprocess stores the output directory
	 * @throws WikiApiException
	 */
	protected AbstractPreprocess(TypePreprocess type, String name,
			Locale locale, int year, File root) throws WikiApiException
	{
		super(name);
		CHK.CHECK_NOT_NULL(root);
		CHK.CHECK_NOT_NULL(locale);
		CHK.CHECK_NOT_NULL(year);
		CHK.CHECK_NOT_NULL(name);
		CHK.CHECK_NOT_NULL(type);
		CHK.CHECK(!root.isFile());
		this.type = type;
		//this.language = locale.getLanguage();
		this.locale = locale;
		wiki = new WikipediaJwpl(locale, year);
		pages = new HashSet<Integer>();
		rootDirectory = root;
	}

	

	/**
	 * Creates a preprocess method able to preprocess pages from the Wikipedia
	 * dump identified by {@code language} and {@code year}. The preprocess is
	 * identified by {@code name}
	 * 
	 * This new instance implements the preprocess function related to the given
	 * type.
	 * 
	 * @param type
	 *            Type of preprocess
	 * @param name
	 *            Identifier
	 * @param language
	 *            Language of the Wikipedia dump
	 * @param year
	 *            Year of the Wikipedia dump
	 * @param rootDirectory
	 *            Directory wherein the preprocess stores the output directory.
	 * @return An instance of preprocess which the implementation of
	 *         {@link #preprocess()} related to its type. If the type is
	 *         unknown, a {@code null} object is returned.
	 * @throws WikiApiException
	 * 
	 */
	public static final AbstractPreprocess getInstance(TypePreprocess type,
			String name, Locale language, int year, File rootDirectory)
			throws WikiApiException
	{
		AbstractPreprocess preprocess = null;
		switch (type)
		{
			case PLAIN_TEXT:
				preprocess = new PlainTextPreprocess(type, name, language,
						year, rootDirectory);
				break;
			default:
				break;
		}
		return preprocess;
	}

	/**
	 * Function which implements the preprocessing procedure.
	 * 
	 * @return A list of text fragments which are the result of the
	 *         preprocessing.
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws LinkTargetException
	 * @throws CompilerException
	 */
	public abstract ArrayList<String> preprocess(int id)
			throws WikiApiException, FileNotFoundException, 
			IOException, LinkTargetException, CompilerException;// ParsedPage
																// page);

	/**
	 * Writes the result of a preprocessing in the given output file. Each
	 * fragment is written in one line.
	 * 
	 * @param fragments
	 *            Set of text fragments resulted from a preprocessing.
	 * @param output
	 *            File wherein the fragments must be written.
	 * @throws IOException
	 */
	public File writePreprocessing(Collection<String> fragments, File root,
			int pageID) throws IOException
	{
		StringBuffer text = new StringBuffer();
		for (String fragment : fragments)
		{
			text.append(fragment).append(LINE_SEPARATOR);
		}

		return FileManager.savePage(root, type, locale.getLanguage(), pageID, text);
	}

	/**
	 * This function is executed when the instance is treated as a thread.</br>
	 * It preprocess all the pages and writes the results in files. This files
	 * are stored in the {@link #rootDirectory}
	 * 
	 * @see java.lang.Thread
	 */
	@Override
	public void run()
	{
		ArrayList<Integer> listOfPages;
		synchronized (this)
		{
			listOfPages = new ArrayList<Integer>(pages);
		}

		logger.info("Subset size: " + listOfPages.size());
		if (!rootDirectory.exists())
		{
			rootDirectory.mkdirs();
		}

		for (Integer pageID : listOfPages)
		{
			logger.info("Start preprocess: " + pageID);

			ArrayList<String> result = new ArrayList<String>();
			try
			{
				if (skipPage(pageID))
				{
					continue;
				}
				else
				{
					result = preprocess(pageID);
				}
			} catch (WikiApiException | IOException
					| LinkTargetException | CompilerException e)
			{
				logger.error("Error during the preprocessing: " + pageID);
				e.printStackTrace();
				continue;
			}

			try
			{
				writePreprocessing(result, rootDirectory, pageID);
			} catch (IOException e)
			{
				logger.error("Error when writing: " + pageID);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds a page to preprocess.
	 * 
	 * @param page
	 *            ID of the Wikipedia page
	 */
	public void addPage(Integer page)
	{
		pages.add(page);
	}

	/**
	 * Adds a set of pages to preprocess
	 * 
	 * @param pages
	 *            Set of page IDs of the Wikipedia
	 */
	public void addPages(Collection<Integer> pages)
	{
		this.pages.addAll(pages);
	}

	/**
	 * Removes a page to preprocess
	 * 
	 * @param page
	 *            ID of the Wikipedia page
	 * @return {@code true} if the set of pages contained that page.
	 *         {@code false} otherwise.
	 */
	public boolean removePage(Integer page)
	{
		return pages.remove(page);
	}
	
	/**
	 * Checks if the page must be skipped.
	 * 
	 * @param pageID Identifier of the page
	 * @return {@code true} if the page which id {@code pageID} must not be
	 *         preprocessed. {@code false} otherwise.
	 * @throws WikiApiException
	 */
	protected boolean skipPage(int pageID) throws WikiApiException
	{
		boolean skip = false;
		if (FileManager.existsFile(rootDirectory, getType(), locale.getLanguage(), pageID))
		{
			logger.warn("Already preprocessed: " + pageID);
			skip = true;
		}
		Page page = wiki.getPage(pageID);
		if (wiki.isDisambiguation(page))
		{
			logger.warn("Disambiguation: " + pageID);
			skip = true;
		}
		if (wiki.isRedirect(page))
		{
			logger.warn("Redirect: " + pageID);
			skip = true;
		}
		/* //this was only for Catalan, now included in Disambiguation
		if (wiki.isAcronym(page))
		{
			logger.warn("Acronym: " + pageID);
			skip = true;
		}*/
		

		return skip;
	}

	/**
	 * @return The type of the preprocessing procedure.
	 */
	public TypePreprocess getType()
	{
		return type;
	}
}
