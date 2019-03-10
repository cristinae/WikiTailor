package cat.lump.aq.wikilink.jwpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import cat.lump.aq.wikilink.Languages;
import cat.lump.aq.wikilink.config.Dump;
import cat.lump.aq.wikilink.config.MySQLWikiConfiguration;
import cat.lump.aq.wikilink.jwpl.LanguageConstants;
import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiTitleParsingException;
import de.tudarmstadt.ukp.wikipedia.parser.Paragraph;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Section;
import de.tudarmstadt.ukp.wikipedia.parser.SectionContainer;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.FlushTemplates;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;

/**
 * This class provides methods for initialising a jwpl Wikipedia instance. It
 * includes definitions for images and categories in 13 languages, namely:
 * 
 * <ul>
 * <li>Arabic
 * <li>Basque
 * <li>Catalan
 * <li>Croatian
 * <li>English
 * <li>Estonian
 * <li>French
 * <li>German
 * <li>Greek
 * <li>Italian
 * <li>Latvian
 * <li>Lithuanian
 * <li>Romanian
 * <li>Slovenian
 * <li>Spanish
 * </ul>
 * 
 * @author albarron
 * @version 0.1.2
 * @since Jan 15, 2013
 * 
 */

public class WikipediaJwpl extends Wikipedia {

	private MediaWikiParser parser;

	/** Language code */
	private Language wikiLanguage;

	/** Label that defines an image in the given language */
	private String[] imgLabels;

	/** Label that defines a category in the given language */
	private String[] categLabels;
	
	private static final String DB_PREFIX = "lump_wiki_";

	private LanguageConstants constants;
	
		
	/**
	 * Creates its own database configuration according to language and year
	 * 
	 * @param lang
	 * @param year
	 * @throws WikiApiException
	 */
	public WikipediaJwpl(Locale lang, int year) throws WikiApiException {

		this(new DatabaseConfiguration(MySQLWikiConfiguration.mysqlUrlJwpl(),
				DB_PREFIX + getLanguageYearString(lang, year),
				MySQLWikiConfiguration.sqlUser(),
				MySQLWikiConfiguration.sqlPass(),
				Languages.getJwplLanguage(lang)));
	}

	/**
	 * Invokes the super class with the database configuration, sets the 
	 * constants loads the JWPL Wikipedia instance.
	 * 
	 * @param dbConfig
	 * @throws WikiApiException
	 */
	public WikipediaJwpl(DatabaseConfiguration dbConfig)
			throws WikiApiException {
		super(dbConfig);
		constants = new LanguageConstants();
		wikiLanguage = getDatabaseConfiguration().getLanguage();
		imgLabels = constants.getImageLabel(wikiLanguage);
		categLabels = constants.getCategoryLabel(wikiLanguage);

		loadWikipedia();
	}

	/**
	 * Sets the image and category labels for the working language. We
	 * initialise the parser to get Wikipedia chunks in different formats.
	 * 
	 * @throws WikiApiException
	 */
	public void loadWikipedia() throws WikiApiException {
		MediaWikiParserFactory pf = new MediaWikiParserFactory();

		// filtering TEMPLATE-Elements
		pf.setTemplateParserClass(FlushTemplates.class);
		// filtering Image-Elements
		pf.getImageIdentifers().addAll(Arrays.asList(imgLabels));
		// filtering categories
		pf.getCategoryIdentifers().addAll(Arrays.asList(categLabels));
		// filtering langlinks
		pf.getLanguageIdentifers().addAll(getMissedLangLinks());

		parser = pf.createParser();
		// parsing based on T5_CleaningTemplateImage
		// /readonly-libraries/jwpl/v0.5b/JWPL/tutorials_parser/T5_CleaningTemplateImage.java
	}

	
	 /**
   * Checks if the page is an acronym page
   * 
   * @param page  The page to check
   * @return {@code true} if the given page is an acronym page;
   *         {@code false} otherwise.
   * @throws WikiTitleParsingException
   */
  public boolean isAcronym(Page page) throws WikiTitleParsingException {
    String text = page.getText()
              .toLowerCase()
              .trim();
    for (String tok : constants.getAcronymLabel(getLanguage())) {
      if (text.startsWith("{{"+tok)) {
        return true;
      }
    }
    return false;   
  }
  
  /**
   * Checks if the page is a redirect page
   * 
   * @param page  The page to check
   * @return {@code true} if the given page is a redirect page; 
   *         {@code false} otherwise.
   */
  public boolean isRedirect(Page page) {
    String text = page.getText()
              .toLowerCase()
              .trim();
    //System.out.println(text);
    for (String tok : constants.getRedirectLabel(getLanguage())) {
      if (text.startsWith(tok)) {
        return true;
      }
    }
    return false;
  }
	
	// ///////////
	// GETTERS //
	// ///////////

  /**
   * @param lang
   * @param year
   * @return
   */
  private static String getLanguageYearString(Locale lang, int year){
    Dump ad = new Dump(lang, year);   
    return ad.toString();   
  }
  
	/**
	 * @return prefix for the database containing the JWPL Wikipedia database
	 */
	public static String getJwplDBprefix() {
		return MySQLWikiConfiguration.getJwplDBprefix();
	}

	/** 
	 * @return Label for images in the language 
	 */
	public String[] getImageLabels() {
		return imgLabels;
	}

	/** @return Label for categories in the language */
	public String[] getCategoryLabels() {
		return categLabels;
	}

	/**
	 * Some langlinks are missed in jwpl and they are not properly filtered.
	 * Such detected languages are included here,
	 * 
	 * @return some languages that seem to be forgotten from the JWPL parsing
	 *         parameters.
	 */
	private List<String> getMissedLangLinks() {
		List<String> l = new ArrayList<String>();
		for (String lan : Languages.getLangAll())
			l.add(lan.replaceAll("_", "-"));
		// some commented languages
		l.addAll(Arrays.asList(new String[] { "ace", "bjn", "ckb", "frr",
				"koi", "krc", "mrj", "mwl", "pcd", "pnb" }));
		return l;
		// new String[]{
		// "ace", "ak", "be-x-old", "bcl", "bar", "bpy", "cbk-zam", "dsb",
		// "ext", "hif", "gan", "glk",
		// "hak", "hsb", "lij", "lg", "arz","mzn", "mwl", "pag", "nds-nl",
		// "new", "nov", "mhr",
		// "om", "pnb", "pnt", "kaa", "crh", "sah", "stq", "cu", "szl", "ckb",
		// "srn", "kab",
		// "fiu-vro", "zh-classical", "wuu", "zea", "zh-min-nan", "zh-yue",
		// "diq", "bat-smg" });
	}

	/**
	 * TODO determine whether this parser should be returned. This should be made 
	 * here.
	 * @return The parser for this WIkipedia
	 */
	public MediaWikiParser getParser() {
		return parser;
	}

	/**
	 * Parses a Wikipedia article
	 * 
	 * @param id id for the article to be parsed
	 * @return a parsed version of the Article
	 * @see {@link MediaWikiParser}
	 * @throws WikiApiException
	 */
	public ParsedPage getParsedArticle(int id) throws WikiApiException {
		return parser.parse(getPage(id).getText());
	}

	/**
	 * Parses a Wikipedia article
	 * 
	 * @param title  Title for the Wikipedia article
	 * @return a parsed version of the Article
	 * @see {@link MediaWikiParser}
	 * @throws WikiApiException
	 */
	public ParsedPage getParsedArticle(String title) throws WikiApiException {
		return parser.parse(getPage(title).getText());
	}

	/**
	 * Obtains the sections of parsed article
	 * 
	 * @param id for the article
	 * @return List including all the sections of the article.
	 * @throws WikiApiException
	 */
	public List<Section> getSectionsFromArticle(int id) throws WikiApiException {
		return getParsedArticle(id).getSections();
	}

	// public void getSubSectionsFromSection(String title) throws
	// WikiApiException{
	// List<Content> contents =
	// getPage(title).getParsedPage().getSection(4).getContentList();
	// // ParsedPage parsed = parser.parse(getPage(title).getText());
	// // ParsedPage sec = parser.parse(parsed.getSection(0).getText());
	// for (Content cont : contents){
	// System.out.println(cont.getText());
	// }
	// }

	/**
	 * Obtains the sections of a parsed article
	 * 
	 * @param title of the article
	 * @return List including all the sections of the article.
	 * @throws WikiApiException
	 */
	public List<Section> getSectionsFromArticle(String title)
			throws WikiApiException {
		return getParsedArticle(title).getSections();
	}

	/**
	 * Obtains the sub-sections of a parsed article
	 * 
	 * @param id of the article
	 * @return A list with all the subsections.
	 * @throws WikiApiException
	 */
	//AQUI VOY
	public void getSubSectionsFromArticle(int id) throws WikiApiException {
		getSubSectionsFromArticle(getPage(id).getTitle().getPlainTitle());
	}

	/**
	 * Obtains the sub-sections of a parsed article
	 * 
	 * @param title  of the article
	 * @throws WikiApiException
	 *             TODO: right now this doesn't get anything
	 */
	public void getSubSectionsFromArticle(String title) throws WikiApiException {
		for (Section section : getSectionsFromArticle(title)) {
			System.out.println(section.getTitle());
			if (section.getClass() == SectionContainer.class) {
				for (Section subsection : ((SectionContainer) section)
						.getSubSections())
					System.out.println("  " + subsection.getTitle());
			}
		}
	}

	/**
	 * Obtains the paragraphs of a parsed article id
	 * 
	 * @param id
	 * @return A list with all of the paragraphs.
	 * @throws WikiApiException
	 */
	public List<Paragraph> getParagraphsFromArticle(int id)
			throws WikiApiException {
		return getParsedArticle(id).getParagraphs();
	}

	/**
	 * Obtains the paragraphs of a parsed article title
	 * 
	 * @param title
	 * @return A list with all of the paragraphs.
	 * @throws WikiApiException
	 */
	public List<Paragraph> getParagraphsFromArticle(String title)
			throws WikiApiException {
		return getParsedArticle(title).getParagraphs();
	}

	// ///////////////
	// Actual work //
	// ///////////////

//	/**
//	 * Determines whether a jwpl Wikipedia installation is available for the
//	 * required language.
//	 * 
//	 * @param lan
//	 * @return True if the installation exists
//	 */
//	public static boolean validateLanguage(String lan) {
//		// TODO at some stage, this validation should depend on a list other
//		// than the Accurat one.
//		for (String l : Languages.langAccurat)
//			if (l.equals(lan))
//				return true;
//		return false;
//	}

	/**
	 * Checks if this is a disambiguation page
	 * 
	 * @param page The page to check
	 * @return {@code true} if the given page is a disambiguation page;
	 *         {@code false} otherwise.
	 * @throws WikiTitleParsingException
	 */
	public boolean isDisambiguation(Page page) throws WikiTitleParsingException {
		String[] disambiguationTokens = constants
				.getDisambiguationLabel(getLanguage());
		
		for (Category cat : page.getCategories()) {
			String catTitle = cat.getTitle().getPlainTitle().toLowerCase();			
			for (String tok : disambiguationTokens) {			
				if (catTitle.contains(tok)) {	return true; }		
			}
		}
		return false;
	}
	
	/**
	 * In this example we run an instance of Wikipedia and display a couple of
	 * articles' contents.
	 * 
	 * @param args
	 * @throws WikiApiException
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws WikiApiException, ClassNotFoundException {
		String lang = "en";
		int year = 2015;
		
		String wikiDB = getJwplDBprefix() + lang + "_" + year;

		//Class.forName("com.mysql.jdbc.Driver");
		DatabaseConfiguration dbConf = new DatabaseConfiguration(
				MySQLWikiConfiguration.mysqlUrlJwpl(), wikiDB,
				MySQLWikiConfiguration.sqlUser(),
				MySQLWikiConfiguration.sqlPass(),
				Languages.getJwplLanguage(new Locale(lang)));

		System.out.println(dbConf.getHost());
		System.out.println(dbConf.getDatabase());
		// TODO other way to give the password?
		System.out.println(dbConf.getUser());
		//System.out.println(dbConf.getPassword());

		
		
		
		
		WikipediaJwpl wk = new WikipediaJwpl(dbConf);

		// wk.getSubSectionsFromArticle("Geografía");
		// System.out.println("@@@@@@@@@@@@");
		// wk.getSubSectionsFromArticle(12);
//		System.out.println(wk.getPage(128).getText());
//		System.out.println(wk.isDisambiguation(wk.getPage(128)));
//		System.out.println(wk.isRedirect(wk.getPage(128)));
		System.out.println("@@@@@@@@@@@@");
		for (Category c : wk.getPage(20104565).getCategories()) {
			System.out.println(c.getTitle());
			System.out.println(c.getTitle().getPlainTitle().toLowerCase());
		}

		System.exit(0);
//		System.out.println(wk.getDatabaseConfiguration());
//		System.out.println(wk.getParsedArticle(39903509).getCategories());
//
//		for (Paragraph par : wk.getParagraphsFromArticle(39903509)) {
//			System.out.println(par.getText());
//			System.out.println("#############");
//		}
//
//		for (Section sec : wk.getSectionsFromArticle(39903509)) {
//			System.out.println(sec.getText().trim());
//			System.out.println("#############");
//		}
	}

}
