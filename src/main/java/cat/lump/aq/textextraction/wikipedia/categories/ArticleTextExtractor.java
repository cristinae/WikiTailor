package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliArticleTextExtractor;
import cat.lump.aq.textextraction.wikipedia.prepro.AbstractPreprocess;
import cat.lump.aq.textextraction.wikipedia.prepro.TypePreprocess;
import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * This class provides methods to load a list of Wikipedia articles IDs and
 * preprocess them.
 * 
 * @author jboldoba
 * 
 * TODO check whether lists are already removed
 * TODO (potentially) apply the language identifier in order to discard noisy 
 * sentences
 */
public class ArticleTextExtractor {
  
	/** Character to separate lines */
//	private static final String LINE_SEPARATOR = "\n";

  /** Template to generate thread name (identifier) */
	private static final String THREAD_NAME_TEMPL = "thread.%d.%s";

	/** Language of the Wikipedia */
	private final Locale locale;

	/** Year of the Wikipedia dump */
	private final int year;

	/** Directory wherein the preprocessed pages will be stored. */
	private File rootDirectory;

	/** Set of IDs of the pages to preprocess */
	private HashSet<Integer> pageIDs;

	/**
	 * Set of available preprocess. A preprocess defines how to deal with the
	 * page.
	 */
	private HashSet<TypePreprocess> availablePreprocesses;
	
	private static LumpLogger logger = 
			new LumpLogger (ArticleTextExtractor.class.getSimpleName());

	/**
	 * Creates a preprocessor without any page to preprocess. The root directory
	 * is set to the user's home.
	 * 
	 * @param language
	 *            Language of the Wikipedia
	 * @param year
	 *            Year of the dump of Wikipedia
	 */
	public ArticleTextExtractor(Locale language, int year) {
		CHK.CHECK_NOT_NULL(language);
		CHK.CHECK_NOT_NULL(year);
		
		this.locale = language;
		this.year = year;
		logger.info("Language: " + language);
		logger.info("Year: " + year);
		
		pageIDs = new HashSet<Integer>();
		String userHome = System.getProperty("user.home");
		rootDirectory = new File(userHome);
		availablePreprocesses = new HashSet<TypePreprocess>();
	}

	/**
	 * Creates a preprocessor with the pages listed in {@code listOfPages}. This
	 * file must have one ID by line. The root directory is set to the user's
	 * home.
	 * 
	 * @param language
	 *            Language of the Wikipedia
	 * @param year
	 *            Year of the dump of Wikipedia
	 * @param listOfPages
	 *            File with one page ID by line.
	 * @throws IOException
	 */
	public ArticleTextExtractor(Locale language, int year, File listOfPages)
			throws IOException {
		this(language, year);
		loadPages(listOfPages);
	}

	/**
	 * Applies all the available preprocessing steps to the pages.
	 * 
	 * @throws InterruptedException
	 * @throws WikiApiException
	 */
	public void preprocessAll() throws InterruptedException, WikiApiException	{
		if (pageIDs.isEmpty()) {	
			logger.warn("No page to preprocess");
		}	else {
			HashSet<TypePreprocess> allPreprocesses = 
			    (HashSet<TypePreprocess>) getAvailablePreprocesses();

			for (TypePreprocess prepro : allPreprocesses)	{
				preprocess(prepro);
			}
		}
	}

	/**
	 * Preprocess all the pages with the preprocess method indentified by
	 * preprocess
	 * 
	 * @param preprocess
	 *            Identifier of the preprocess
	 * @throws InterruptedException
	 * @throws WikiApiException
	 */
	public void preprocess(TypePreprocess preprocess)
			throws InterruptedException, WikiApiException	{
		CHK.CHECK_NOT_NULL(preprocess);
		logger.info(String.format("Start preprocessing (Type: %s)", preprocess));
		if (pageIDs.isEmpty()) {
			logger.warn("No page to preprocess");
		}	else {
			if (isAvailablePreprocess(preprocess)) {
				ArrayList<Integer> pages;
				File output;
				synchronized (this)	{
					pages = new ArrayList<Integer>(pageIDs);
					output = rootDirectory;
				}
				String threadName = String
						.format(THREAD_NAME_TEMPL, Calendar.getInstance()
								.getTimeInMillis(), preprocess.toString());
				AbstractPreprocess preprocessExec = AbstractPreprocess
						.getInstance(preprocess, threadName, 
								locale, year,
								output);

				preprocessExec.addPages(pages);
				preprocessExec.start();
				if (preprocessExec.isAlive())	{
					preprocessExec.join();
				}
			}	else {
				logger.warn("Preprocess identifier unknown");
			}
		}
	}

	/**
	 * Loads all the page IDs of the file {@code list}. The older loaded pages
	 * are kept in the preprocessor.
	 * 
	 * @param list
	 *            File with one page ID by line.
	 * @throws IOException
	 */
	public void loadPages(File list) throws IOException {
		CHK.CHECK_NOT_NULL(list);
		boolean validFile = list.isFile() && list.canRead();
		CHK.CHECK(
				validFile,
				String.format("File %s doesn't exist or it's unreadable.",
						list.getAbsoluteFile()));
		String[] pages = FileIO.fileToLines(list);
		pageIDs = new HashSet<Integer>(pages.length);
		for (String page : pages)	{
			String[] fields = page.split("\t");
			pageIDs.add(Integer.parseInt(fields[0]));
		}
	}
	
	public void loadPages(Integer[] list) {
		CHK.CHECK_NOT_NULL(list);		
		pageIDs = new HashSet<Integer>(list.length);
		pageIDs.addAll(Arrays.asList(list));		
	}

	/**
	 * Adds a page ID to the set of page IDs
	 * 
	 * @param id
	 *            ID of the page
	 */
	public void addPage(int id)	{
		pageIDs.add(id);
	}
	
	public void removePage(int id) {
		pageIDs.remove(id);
	}

	/**
	 * Adds a collection of pages to the set of page IDs
	 * 
	 * @param ids
	 *            Collection of page IDs
	 */
	public void addPages(Collection<Integer> ids)	{
		pageIDs.addAll(ids);
	}

	/**
	 * Adds a new preprocess to the available ones. If {@code preprocess} is
	 * already added, nothing is done
	 * 
	 * @param preprocess
	 *            The new preprocess. It can't be a {@code null} object.
	 */
	public void addPreprocess(TypePreprocess preprocess) {
		CHK.CHECK_NOT_NULL(preprocess);
		availablePreprocesses.add(preprocess);
	}

	/**
	 * Removes the preprocess if it belongs to the set of available
	 * preprocesses.
	 * 
	 * @param preprocess
	 *            Preprocess to remove.
	 * @return .
	 */
	public boolean removePreprocess(TypePreprocess preprocess) {
		CHK.CHECK_NOT_NULL(preprocess);
		return availablePreprocesses.remove(preprocess);
	}

	/**
	 * Changes the root directory of the preprocessor. If {@code directory} 
	 * isn't a valid directory (with writable permissions), any change is done.
	 * 
	 * @param directory
	 *            A valid directory.
	 */
	public void setRootDirectory(File directory) {
		CHK.CHECK_NOT_NULL(directory);
		boolean validDir = directory.isDirectory() && directory.canWrite();

		if (validDir)	{
			rootDirectory = directory;
		}
	}

	/**
	 * Query if exists the preprocess within the available preprocesses.
	 * 
	 * @param preprocess
	 *            Desired preprocess.
	 * @return {@true} if the desired preprocess is available.
	 */
	public boolean isAvailablePreprocess(TypePreprocess preprocess)	{
		return getAvailablePreprocesses().contains(preprocess);
	}

	 //TODO Merge this and the following methods and make them more efficient
  public static void extractSpecificArticles(
    Locale locale, int year, Integer[] articleIDs, File directory) {
    CHK.CHECK(directory.isDirectory(), "Output directory "+directory+" not found");
    
    try {
      ArticleTextExtractor prepro = new ArticleTextExtractor(locale, year);
      prepro.setRootDirectory(directory);
      prepro.loadPages(articleIDs);
      
      WikipediaJwpl wik = new WikipediaJwpl(locale, year);
      
      // Arantxa's modifications while<->for
      Iterator<Integer> it = prepro.pageIDs.iterator();
      while (it.hasNext()) {
    	  int id = it.next(); 
    	  Page page = wik.getPage(id); 
    	  if (pageShouldBeRemoved(page))  {
    		  //TODO this if does not seem to work as it queries non-existing
    		  //information in the DB.
    		  it.remove();
    	  }
      }

      /*
      for (int id : prepro.pageIDs) {       
        Page page = wik.getPage(id);                      
        if (pageShouldBeRemoved(page))  {
          //TODO this if does not seem to work as it queries non-existing
          //information in the DB.
          prepro.removePage(id);
        }
      }*/

      prepro.addPreprocess(TypePreprocess.PLAIN_TEXT);
      prepro.preprocessAll();
    } catch (Exception e) {
      e.printStackTrace();
    }   
  }
  
  /**
   * Extract only the articles specified in the pagesFile
   * @param locale
   * @param year
   * @param pagesFile
   * @param directory
   */
  public static void extractSpecificArticles(
      Locale locale, int year, File pagesFile, File directory) {
    // MAIN 3
    CHK.CHECK(pagesFile.isFile(), "Input file not found");       
    
    try {
      ArticleTextExtractor prepro = new ArticleTextExtractor(locale, year);
      prepro.setRootDirectory(directory);
      prepro.loadPages(pagesFile);
      WikipediaJwpl wik = new WikipediaJwpl(locale, year);
      
      for (int id : prepro.pageIDs) {
        Page page = wik.getPage(id);
                      
        if (pageShouldBeRemoved(page))  {
          //this if does not seem to work as it queries non-existing
          //information in the DB.
          prepro.removePage(id);
        } 
      }     
      prepro.addPreprocess(TypePreprocess.PLAIN_TEXT);
      prepro.preprocessAll();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  } 
	
	/**
	 * @return The set of available preprocesses.
	 */
	public Set<TypePreprocess> getAvailablePreprocesses()	{
		return availablePreprocesses;
	}

	/**
	 * @return The current root directory.
	 */
	public File getRootDirectory() {
		return rootDirectory;
	}
	
	/** The entire Wiki for the set language and year */
	public static void extractEntireWikipedia(
			Locale locale, int year, File directory) {
		// MAIN 2:
		int mod = 100;
		int i = 0;
		ArticleTextExtractor prepro;
		
		CHK.CHECK(directory.isDirectory(), "Directory not found, please create it");
		logger.info("Selecting articles");
		try {
		  prepro = new ArticleTextExtractor(locale, year);						
			prepro.setRootDirectory(directory);
			for (Page page : new WikipediaJwpl(locale, year).getArticles())	{
				if (i++ % mod == 0)	{
					logger.info(String.format("Processing article %d; ID: %d", 
								i, page.getPageId()) );
				}	
					if (pageShouldBeRemoved(page))	{
						continue;
					}	else {
						int id = page.getPageId();
						//System.out.println("Adding " + id);
						prepro.addPage(id);
					}
			}
			prepro.addPreprocess(TypePreprocess.PLAIN_TEXT);		
			prepro.preprocessAll();
		} catch (WikiApiException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}				
	}
	
	/**
	 * @param page
	 * @return <code> true </code> if the title of the page is empty, it is 
	 *         marked as disambiguationm discussion, or redirect 
	 */
	private static boolean pageShouldBeRemoved(Page page) {	  
	  try {
      if (page.getTitle().getPlainTitle().length() == 0  //the title is empty
          || page.isDisambiguation() || page.isDiscussion()
          || page.isRedirect()) {
        return true;
      }
	  } catch (Exception e)  {
        logger.error("Exception Adding (" + page.getPageId()
            + "): " + e.getLocalizedMessage());
        e.printStackTrace();
      }
	  return false;
	}
	
	/**
	 * Extract texts from Wikipedia articles and save them into text files, after 
	 * some given preprocessing. The Wikipedia edition is defined with the 
	 * language and year provided. Moreover, an output root directory is given 
	 * to write the files.
	 * 
	 * @param args
	 * 				language, year [file with ids]
	 */
	public static void main(String[] args) 
	{
		logger.info("Beginning of the extraction");
		WikipediaCliArticleTextExtractor cli = new WikipediaCliArticleTextExtractor();
		cli.parseArguments(args);
				
		Locale locale = cli.getLanguage();
		int year = cli.getYear();		
		File input = cli.getArticlesFile();
		File directory = cli.getOutputDir();

		if (input == null) {
			extractEntireWikipedia(locale, year, directory);
		} else {
			extractSpecificArticles(locale, year, input, directory);
		}
		logger.info("End of the extraction");	
	}
}
