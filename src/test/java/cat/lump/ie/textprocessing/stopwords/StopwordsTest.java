package cat.lump.ie.textprocessing.stopwords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

public class StopwordsTest {

	Stopwords stop;
	@Before
	public void setUp() throws Exception {
		stop = new Stopwords(Locale.ENGLISH);
//		for (String x : stop.STOP_LIST) {
//			System.out.println(x);
//		}
	}

	/**Determine whether a token is a stopword */
	@Test
	public void testIsStopword() {
//		for (String x : stop.STOP_LIST) {
//			System.out.println(x);
//		}
		assertTrue(stop.isStopword("again"));
		assertTrue(stop.isStopword("whom"));
		assertFalse(stop.isStopword("campaign"));
		assertFalse(stop.isStopword("public"));
	}

	/**Remove the stop words from a text */
	@Test
	public void testRemoveStopwordsString() {
		String text = "he has worked at mit media lab center for bits and ,";
		assertTrue("worked mit media lab center bits , "
				.equals(stop.removeStopwords(text)));
	}

	/**Remove the stoprods from a list of tokens */
	@Test
	public void testRemoveStopwordsListOfString() {
		List<String> words = new ArrayList<String>();
		
		words.addAll(Arrays.asList(new String[]
			{"he",	"has",		"worked",	"at",
			 "mit",	 "media", 	"lab",		"center",
			 "for",	"bits",		 "and",		","
			 }));
		stop.removeStopwords(words);

		assertEquals(6, words.size());
	}

}
