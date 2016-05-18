package cat.lump.aq.wikilink;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cat.lump.aq.wikilink.Languages;

import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;

public class LanguagesTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetWikiLanguage() {		
		Assert.assertEquals(Language.arabic, Languages.getJwplLanguage("ar"));
		Assert.assertEquals(Language.basque, Languages.getJwplLanguage("eu"));
		Assert.assertEquals(Language.catalan, Languages.getJwplLanguage("ca"));
		Assert.assertEquals(Language.croatian, Languages.getJwplLanguage("hr"));
		Assert.assertEquals(Language.english, Languages.getJwplLanguage("en"));
		Assert.assertEquals(Language.estonian, Languages.getJwplLanguage("et"));
		Assert.assertEquals(Language.french, Languages.getJwplLanguage("fr"));
		Assert.assertEquals(Language.german, Languages.getJwplLanguage("de"));		
		Assert.assertEquals(Language.greek, Languages.getJwplLanguage("el"));
		Assert.assertEquals(Language.hindi, Languages.getJwplLanguage("hi"));		
		Assert.assertEquals(Language.italian, Languages.getJwplLanguage("it"));
		Assert.assertEquals(Language.latvian, Languages.getJwplLanguage("lv"));
		Assert.assertEquals(Language.lithuanian, Languages.getJwplLanguage("lt"));
		Assert.assertEquals(Language.romanian, Languages.getJwplLanguage("ro"));
		Assert.assertEquals(Language.russian, Languages.getJwplLanguage("ru"));
		Assert.assertEquals(Language.slovenian, Languages.getJwplLanguage("sl"));		
		Assert.assertEquals(Language.spanish, Languages.getJwplLanguage("es"));	
	}

}
