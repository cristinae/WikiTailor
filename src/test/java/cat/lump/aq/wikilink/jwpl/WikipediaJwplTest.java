package cat.lump.aq.wikilink.jwpl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cat.lump.aq.wikilink.Languages;
import cat.lump.aq.wikilink.config.MySQLWikiConfiguration;
import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

public class WikipediaJwplTest {

	
	private final String LANGUAGE = "es";
	private final String YEAR = "2012";
	
	private WikipediaJwpl wk; 
	
	
//	public TestWikipediaJwpl() throws WikiApiException{
//		String wikiDB = 
//				WikipediaJwpl.getJwplDBprefix() + 
//				LANGUAGE + "_" + YEAR;
//
//		DatabaseConfiguration dbConf = new DatabaseConfiguration(
//				MySQLWikiConfiguration.mysqlUrlJwpl(), wikiDB,
//				MySQLWikiConfiguration.sqlUser(),
//				MySQLWikiConfiguration.sqlPass(),
//				LanguageConstants.getWikiLanguage(LANGUAGE));
//		wk = new WikipediaJwpl(dbConf);
//
//	}
	
	@Before
	public void setUp() throws Exception {
		String wikiDB = 
				WikipediaJwpl.getJwplDBprefix() + 
				LANGUAGE + "_" + YEAR;

		DatabaseConfiguration dbConf = new DatabaseConfiguration(
				MySQLWikiConfiguration.mysqlUrlJwpl(), wikiDB,
				MySQLWikiConfiguration.sqlUser(),
				MySQLWikiConfiguration.sqlPass(),
				Languages.getJwplLanguage(LANGUAGE));
		wk = new WikipediaJwpl(dbConf);
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetImageLabel() {
		Assert.assertEquals(1, 1);
		Assert.assertArrayEquals(new String[]{"archivo", "imagen"}, 
				wk.getImageLabels());
	}

	@Test
	public void testGetCategoryLabel() {		
		Assert.assertArrayEquals(new String[] {"categoría","categorías"},
				wk.getCategoryLabels());
	}

	@Test
	public void testGetSectionsFromArticleInt() throws WikiApiException {
		Assert.assertEquals(20, 
				wk.getSectionsFromArticle(10).size());
	}

	@Test
	public void testGetSectionsFromArticleString() throws WikiApiException {
		Assert.assertEquals(13, 
				wk.getSectionsFromArticle("Gobierno Provisional Ruso").size());
	}

	@Test
	public void testGetParagraphsFromArticleInt() throws WikiApiException {
		Assert.assertEquals(318, 
				wk.getParagraphsFromArticle(10).size());
	}

	@Test
	public void testGetParagraphsFromArticleString() throws WikiApiException {
		Assert.assertEquals(42, 
				wk.getParagraphsFromArticle("Gobierno Provisional Ruso").size());
	}
	
	
}
