package cat.lump.aq.textextraction.wikipedia.categories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

// http://ca.wikipedia.org/wiki/Categoria:Fauna_per_territori
// 508210 	508210 	Fauna_per_territori
//	508210 	584331
//	508210 	282664
// 	508210 	539019
//  508210 	915717
// 508214 	508214 	Llistes_d'animals_de_Catalunya
// 508101 	508101 	Llistes_d'animals

/** 
 * Test for the {@code DomainKeywordsTest} class.
 */
public class DomainKeywordsTest {

private DomainKeywords dkw;

private final int year = 2015;
private final Locale catLocale = new Locale("ca");
private final int percentage = 10;
private final int categoryID = 508210;  
private final String categoryName = "Fauna_per_territori";
private final int expectedArticles = 3;
private final ArrayList<String> expectedTitles = new ArrayList<String>(
	    Arrays.asList(
	    		"Fauna_d'Austràlia", 
	    		"Fauna_dels_Països_Catalans",
	    		"Fauna_del_Sàhara")
	    );

    @Before
	public void setUp() throws Exception {
		dkw = new DomainKeywords(catLocale, year);
		dkw.setPercentage(percentage);
	}
	
	@Test
	public void testLoadArticlesInt() throws WikiApiException {
        int numArticles;
        ArrayList<String> articleTitles = new ArrayList<String>();
        dkw.loadArticles(categoryID);
        WikipediaJwpl wiki = new WikipediaJwpl(catLocale, year);
        Category category = wiki.getCategory(categoryID);
        //category.getTitle().getPlainTitle(); 
        numArticles = category.getArticleIds().size();
		Assert.assertEquals(expectedArticles, numArticles);

		for (int artID : category.getArticleIds()){
			articleTitles.add(wiki.getPage(artID).getTitle().getWikiStyleTitle());
		}	
		Collections.sort(expectedTitles);
		Collections.sort(articleTitles);
		Assert.assertEquals(expectedTitles, articleTitles);
	}


	@Test
	public void testLoadArticlesString() throws WikiApiException {
	       int numArticles;
	        ArrayList<String> articleTitles = new ArrayList<String>();
	        dkw.loadArticles(categoryName);
	        WikipediaJwpl wiki = new WikipediaJwpl(catLocale, year);
	        Category category = wiki.getCategory(categoryID);
	        numArticles = category.getArticleIds().size();	        
			Assert.assertEquals(expectedArticles, numArticles);

			for (int artID : category.getArticleIds()){
				articleTitles.add(wiki.getPage(artID).getTitle().getWikiStyleTitle());
			}	
			Collections.sort(expectedTitles);
			Collections.sort(articleTitles);
			Assert.assertEquals(expectedTitles, articleTitles);
	}

	
	@Test
	public void testComputeTF() throws WikiApiException {
        dkw.loadArticles(categoryID);
	}

	/* needed??
	@Test
	public void testToFile() {
		fail("Not yet implemented");
	}
	*/

	@Test
	public void testGetYear() {		
		Assert.assertEquals(year, dkw.getYear());	
	}

	@Test
	public void testGetLang() {
		Assert.assertEquals(catLocale.toString(), dkw.getLang());	
	}

    /* Tested at TermFrequencyTest 
	@Test
	public void testGetTermTuples() {
		fail("Not yet implemented");
	}
	@Test
	public void testGetTopTuples() {
		fail("Not yet implemented");
	}
    */

}
