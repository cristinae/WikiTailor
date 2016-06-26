package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.basics.structure.ir.TermFrequencyTuple;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliDomainKeywords;
import cat.lump.aq.textextraction.wikipedia.prepro.TermExtractor;
import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import cat.lump.ir.weighting.TermFrequency;
import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * <p>
 * This class gets the most common terms in the articles belonging to, at 
 * least, one category of a given domain. A domain is defined by one 
 * category (root) and all of its subcategories.
 * </p>
 * <p>
 * Terms are stemmed and stopwords are not included. 
 * </p>
 * 
 * 
 * @author jboldoba
 */
public class DomainKeywords
{

	/** Year of the Wikipedia edition */
	private int year;

	/**Top k terms that are required (default: 10%; recommended 10%) */
	private int top;
	
	/**Max vocabulary size allowed even if top is present (default: 100) */
	private int max = 100;

	/**Minimum number of articles required to build a dictionary */
	private int minArticlesVoc;

	/** Object to compute and access tf for the terms in the collection */
	private TermFrequency tf;

	/** The set of articles considered as relevant */
	private HashSet<Page> articles;
	
	/** Language of the Wikipedia connector */
	private Locale lang;	
	
	private static LumpLogger logger = 
			new LumpLogger (DomainKeywords.class.getSimpleName());
	

	// Constructors
	public DomainKeywords(Locale locale, int year) {
		setLang(locale);
		setYear(year);
	}
	
	//Publics 
	
	/**
	 * Gets the term frequency tuples resulting the treatment of a set of
	 * TODO this should be private!! temporarily moved to fix issues with the 
	 * main Wikipedia pages.
	 * 
	 * ATTENTION: the contents, originally implementing all the tf-related 
	 * computation, are now in cat.lump.ir.weighting.TermFrequency
	 * 
	 * @param articles The Wikipedia pages to process
	 * @return The list of term frequency tuples. This tuples are sorted by the
	 *         frequency in descending order.
	 */
	public void computeTF()	{
		CHK.CHECK(articles.size() != 0, "Articles should be loaded first");
		tf = new TermFrequency();
		for (Page page : articles) {
			try {
				tf.addTerms(getTerms(page));				
			} catch (WikiApiException e) {
				logger.error(String.format("I cannot read article %d", page.getPageId()));
				continue;
			}			
		}
	}	

	/**
	 * As computeTF() but including the title of the root category
	 * 
	 * @param articles The Wikipedia pages to process
	 * @param categoryName The title of the root category          
	 * @return List of term frequency tuples. This tuples are sorted by 
	 *         frequency in descending order.
	 */
	public void computeTF(String categoryName)	{
		computeTF();
		tf.addTerms(getTerms(categoryName));	
	}	

	/**
	 * Loads the articles of the given category ID.
	 * 
	 * @param categoryID Identifier of the category. The category must exist in 
	 *         the Wikipedia database
	 * @return A set of pages which belong to the given category.
	 * 
	 * @throws WikiApiException Raised if creating a Wikipedia connector is not 
	 *         possible for the given language and year.
	 */
	public void loadArticles(int categoryID) throws WikiApiException {
		CHK.CHECK_NOT_NULL(categoryID);
		
		WikipediaJwpl wiki = new WikipediaJwpl(lang, year);
		Category category = wiki.getCategory(categoryID);
		CHK.CHECK(category != null,				
				String.format("The category %d doesn't exist", categoryID));

		loadArticles(wiki, category);		
	}
	
	/**
	 * Loads the articles of the given category. The category is defined by its
	 * name.
	 * 
	 * @param categoryName Name of the category
	 * @throws WikiApiException  Raised if creating a Wikipedia connector is not 
	 *         possible for the given language and year or if the category name is 
	 *         not valid in that Wikipedia connector
	 */
	public void loadArticles(String categoryName) throws WikiApiException	{
		CHK.CHECK_NOT_NULL(categoryName);
		
		WikipediaJwpl wiki = new WikipediaJwpl(lang, year);
		Category category = wiki.getCategory(categoryName);
		CHK.CHECK(category != null,
				String.format("The category %s doesn't exist", categoryName));
		loadArticles(wiki, category);	
	}

	/**
	 * Saves the top list of a text file. The resulting file contains one 
	 * tab-separated term per line with frequency and term.
	 *  
	 * @param file The file to save the list into.
	 */
	public void toFile(File file) { 
		toFileStorage(file, getTopTuples());		
//		for (TermFrequencyTuple t : getTopTuples()) {
////			String line = String.format("%d\t%s", t.getFrequency(),	t.getTerm());
////			sb.append(line).append("\n");
//		  //TODO check that this is equivalent
//		  sb.append(String.format("%d\t%s%n", t.getFrequency(), t.getTerm()));
//		}
//		
//		try {
//			FileIO.stringToFile(file, sb.toString(), false);
//		} catch (IOException e) {			
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Saves the given list of TermFrequencyTuples into a file
	 * @param file
	 * @param tfs
	 */
	public void toFile(File file, List<TermFrequencyTuple> tfs) {
		toFileStorage(file, tfs);
	}	
	
	// Getters
	
	public int getYear(){
		return year;
	}

	public String getLang(){
		return lang.getLanguage();
	}

	public List<TermFrequencyTuple> getTermTuples(){
		return tf.getAll();
	}
	
	public List<TermFrequencyTuple> getTopTuples(){
		return tf.getTop(top, max);
	}	

	public List<TermFrequencyTuple> getTopTuplesPlus(String category){
		return tf.getTopPlus(top, max, getTerms(category));
	}	

	// Setters
	
	/** 
	 * Defines the percentage of terms that should be considered as domain terms
	 *  
	 * @param t an integer in the range (0,100]
	 */
	public void setPercentage(int t){
		CHK.CHECK_NOT_NULL(t);
		CHK.CHECK(t > 0, "The top percentage must be higher than 0");
		CHK.CHECK(t <= 100, "The top percentage must be lower than 100");
		top = t;
	}

	/** 
	 * Defines the maximum number of terms that should be considered as domain terms
	 *  
	 * @param max an integer 
	 */
	public void setMaxSize(int m) {
		CHK.CHECK_NOT_NULL(m);
		CHK.CHECK(m > 1 || m == -1, 
		    "There must be more than one element in the vocabulary, max>1");
		max = m;		
	}


	
	/** 
	 * Defines the minimum number of articles required to build the vocabulary
	 *  
	 * @param min is an integer 
	 */
	public void setMinNumArticles(int min){
		CHK.CHECK_NOT_NULL(min);
		CHK.CHECK(min > 1, 
		    "The minimum number of articles required to build the vocabulary must be higher than 1");
		minArticlesVoc = min;
	}

	/**
	 * Sets the language.
	 * 
	 * @param lang   The locale for the new language
	 */
	public void setLang(Locale lang) 	{
		CHK.CHECK_NOT_NULL(lang);
		this.lang = lang;
	}

	public void setYear(int year) {
		CHK.CHECK_NOT_NULL(year);
		this.year = year;
	}
	
	// Privates

	/**
	 * Loads the articles associated to a given category. In case there are
	 * too few articles, its subcategories are also visited.
	 * Note that this method is included just to avoid replications between 
	 * the public methods loadArticles which receive integer and string identifiers. 
	 * The validity of the category is checked there.
	 *  
	 * @param wiki
	 * @param category
	 * @throws WikiApiException
	 */
	private void loadArticles(WikipediaJwpl wiki, Category category)
	throws WikiApiException{
		logger.info(String.format("Articles associated to category '%s': %d", 
						category.getTitle().getPlainTitle(), 
						category.getArticleIds().size()));
		
		if (category.getArticleIds().size() < minArticlesVoc) {
			logger.warn(String.format("Too few articles, visiting its %d children", 
			    category.getNumberOfChildren()));
			articles = getCategoryAndSubCatArticles(category);
		} else {
			articles = getCategoryArticles(category);			
		}
	}
	
	 private void toFileStorage(File file, List<TermFrequencyTuple> tfs) {
	    CHK.CHECK_NOT_NULL(file);
	    //CHK.CHECK(!file.exists(),"I cannot create the file as it already exists"); 
	    StringBuffer sb = new StringBuffer();
	     for (TermFrequencyTuple t : getTopTuples()) {
//	     String line = String.format("%d\t%s", t.getFrequency(), t.getTerm());
//	     sb.append(line).append("\n");
	     //TODO check that this is equivalent
	     sb.append(String.format("%d\t%s%n", t.getFrequency(), t.getTerm()));
	   }
	   
	   try {
	     FileIO.stringToFile(file, sb.toString(), false);
	   } catch (IOException e) {     
	     e.printStackTrace();
	   }
	  }
	
	/**
	 * Gets the set of articles categorized by the given category.
	 * 
	 * @param category  A Wikipedia category.
	 * @return The articles of the category
	 */
	private HashSet<Page> getCategoryArticles(Category category) {
		HashSet<Page> articles = new HashSet<Page>();
		try	{
			articles = (HashSet<Page>) category.getArticles();
		} catch (WikiApiException e) {
			logger.error(
				String.format("No articles found for category %d from wiki_%s_%s", 
								category.getPageId(), lang, year));
			e.printStackTrace();
		}
		return articles;
	}
	
	/**
	 * Gets the set of articles of the given category and its first children.
	 * 
	 * @param category A Wikipedia category.
	 * @return The articles of the category and first children
	 */
	private HashSet<Page> getCategoryAndSubCatArticles(Category category) {
		HashSet<Page> articles = new HashSet<Page>();
		int numArticles = 0;
		try	{		
			articles = (HashSet<Page>) category.getArticles();
			numArticles = category.getArticleIds().size();
			for (Category cat : category.getChildren()) {
				//articles = (HashSet<Page>) cat.getArticles();
				numArticles = numArticles + cat.getArticleIds().size();
				articles.addAll((HashSet<Page>) cat.getArticles());
			}
			logger.info(
			    String.format("Final number of articles associated to category '%s': %d", 
			        category.getTitle().getPlainTitle(), numArticles));
		} catch (WikiApiException e)
		{
			logger.error(
				String.format("No articles found for category %d from wiki_%s_%s", 
								category.getPageId(), lang, year));
			e.printStackTrace();
		}
		
		if (numArticles < minArticlesVoc){
			logger.warn("The number of articles to build the dictionary seems still too small. " +
					"Please, check the category manually.");
		}
		return articles;
	}

	/**
	 * Transforms the content of a page into a set of terms by using 
	 * {@code getTerms(String text)}. 
	 * 
	 * @param page  The Wikipedia page.
	 * @see   getTerms(String text)       
	 * @return The list of tokens resulting of the treatment explained above.
	 * @throws WikiApiException  Thrown if a query to Wikipedia database fails.
	 */
	private List<String> getTerms(Page page) throws WikiApiException
	{
		// Get plain text without Wikipedia noise
		WikipediaJwpl wiki = new WikipediaJwpl(lang, year);
		String text = wiki.getParsedArticle(page.getPageId()).getText();
		return getTerms(text);
	}
	
	/**
	 * Transforms the content of a String into a set of terms.
	 * The definition of term has been moved to
	 *  package cat.lump.aq.textextraction.wikipedia.prepro.TermExtractor
	 * 
	 * @param text The String from which terms are extracted.
	 * @return The list of tokens resulting of the treatment in TermExtractor.
	 */
	private List<String> getTerms(String text) {
		CHK.CHECK_NOT_NULL(text);
		TermExtractor te = new TermExtractor(lang);
		List<String> tokens = te.getTerms(text, 4);

		// I do not understand why this is needed, therefore I do not move it to the new location
		// in TermExtractor.getTerms
		ArrayList<String> terms = new ArrayList<String>();   
		for (String token : tokens) {
			terms.add(token);
		}
		return terms;
	}

	
	
	/**
	 * Main method. It can be used as tool to obtain a domain list of terms.
	 * This program writes a file in the user working directory with the terms
	 * sorted by their frequency in descending order. The file is called
	 * "categoryID_language.dict"
	 * 
	 * @param args
	 *            List of parameters:
	 *            <ul>
	 *            <li>Language: Language of the Wikipedia</li>
	 *            <li>Year: Year of the Wikipedia</li>
	 *            <li>Category: ID of the category which defines the domain</li>
	 *            <li>Top: Desired percentage of the dictionary, e.g. top=10
	 *            implies write the 10% of the dictionary with the highest
	 *            frequencies.</li>
	 *            </ul>
	 */
	public static void main(String[] args)
	{				
		
		WikipediaCliDomainKeywords cli = new WikipediaCliDomainKeywords();
		
		cli.parseArguments(args);
		
		Locale locale = cli.getLanguage();
		int year = cli.getYear();		
		int categoryID = cli.getCategoryID();				
		File output = cli.getOutputFile();
		
		int top = cli.getTop();
		//int max = cli.getPropertyInt("topKeywords");
		//int minArticlesVoc = cli.getPropertyInt("minNumArticles");

		/*Locale locale = new Locale("ar");
		int year = 2015;		
		int categoryID = 9555;				
		File output = new File("/home/cristinae/pln/wikipedia/categories/kk/kk.txt");
		*/
		logger.info(String.format("Language: %s; year: %s; category ID: %d",
				locale, year, categoryID));
		
		DomainKeywords dkw = new DomainKeywords(locale, year);
		dkw.setPercentage(top);

		try {
			logger.info("Loading articles associated to the category");
			dkw.loadArticles(categoryID);
		} catch (WikiApiException e) {
			e.printStackTrace();
		}

		logger.info("Computing frequencies");
		dkw.computeTF();
		
		logger.info("Dumping into " + output);
		dkw.toFile(output);		  
	}

	

//	/**
//	 * Updates the term frequency tuples processing a list of new terms.
//	 * 
//	 * @param terms
//	 *            The list of new terms
//	 * @param tf
//	 *            The map of term frequency tuples. This tuples are indexed by
//	 *            the terms value.
//	 */
//	private void calculateFrequency(List<String> terms,
//			Map<String, TermFrequencyTuple> tf)
//	{
//		for (String term : terms)
//		{
//			if (tf.containsKey(term))
//			{
//				tf.get(term).increment();
//			}
//			else
//			{
//				TermFrequencyTuple newTerm = new TermFrequencyTuple(term, 1);
//				tf.put(term, newTerm);
//			}
//		}
//	}


}