package cat.lump.ie.textprocessing.ngram;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import cat.lump.ie.textprocessing.Decomposition;

public class WordNgramsTest {

	Decomposition wNgrams;
	@Before
	public void setUp() throws Exception {
		wNgrams = new WordNgrams(3, Locale.ENGLISH);
	}

	/**
	 * Ten 3-grams are generated
	 */
	@Test
	public void testGetStrings() {		
		String t = "The Crimean crisis is an ongoing international crisis "
				+ "involving Russia and Ukraine.";

		List<String> expected = Arrays.asList(new String[] {
			"The Crimean crisis",				"Crimean crisis is",
			"crisis is an",						"is an ongoing",
			"an ongoing international",			"ongoing international crisis",
			"international crisis involving",	"crisis involving Russia",
			"involving Russia and",				"Russia and Ukraine",
			"and Ukraine .",
		});		
		
		assertEquals("This string should generate eleven 3-grams", 
					11, wNgrams.getStrings(t).size());
		
		assertEquals("The n-grams do not match", expected, wNgrams.getStrings(t));
	}
	
	/**
	 * One single 3-gram without diacritics generated from a 3-words string
	 */
	@Test
	public void testGetSingleString() {		
		wNgrams = new WordNgrams(3, new Locale("es"));
		
		String t = "crisis diplomática internacional";
		List<String> expected =Arrays.asList("crisis diplomatica internacional"); 
		
		assertEquals(expected, wNgrams.getStrings(t));		
	}
	

}
