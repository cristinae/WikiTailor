package cat.lump.aq.textextraction.wikipedia.utilities;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cat.lump.aq.basics.io.files.FileIO;

/** 
 * Test for the {@code CommonArticlesFinder} class.
 * Methods that depend on specific tables in the database are not tested.
 * @author cristina
 * @since Feb 13, 2015
 */
public class CommonArticlesFinderTest {

	private CommonArticlesFinder artFinder;
	
	private final int year = 2013;
	private final static String[] langs = {"ca", "eu"};
	private String[] filesID = {"", ""};
	private File folder;

	
	@Before
	public void setUp() throws Throwable {
		String articlesCa = 
				"244872\n" +
			    "1092321\n"+
			    "974871\n" +  //b
			    "580855\n" +
			    "156359\n" +
			    "34523\n"  +  //a
			    "264402\n" +
			    "643071\n";
		String articlesEu = 
			    "95743\n"  +  //a
				"244872\n" +
			    "382840\n" +  //b
			    "15\n";

		filesID[0] = "tmpCa.txt";
		filesID[1] = "tmpEu.txt";
		File fCa = new File(System.getProperty("user.dir"),filesID[0]);
		File fEu = new File(System.getProperty("user.dir"),filesID[1]);
		try {
			FileIO.stringToFile(fCa, articlesCa, false);
			FileIO.stringToFile(fEu, articlesEu, false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		folder = new File(System.getProperty("user.dir"));

	}

	@Test
	public void testLookForMinimumNumber() throws Throwable {
		CommonArticlesFinder artFinder = new CommonArticlesFinder(langs, 2013, filesID, folder);
		String smallestLang = artFinder.lookForMinimumNumber();
		Assert.assertEquals("eu", smallestLang);		
	}

    //TODO add more 
	
	
}
