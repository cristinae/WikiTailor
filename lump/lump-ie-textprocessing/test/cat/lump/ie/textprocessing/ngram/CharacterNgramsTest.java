package cat.lump.ie.textprocessing.ngram;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import cat.lump.ie.textprocessing.Decomposition;

public class CharacterNgramsTest {

	Decomposition cNgrams;
	@Before
	public void setUp() throws Exception {
		cNgrams = new CharacterNgrams(3);
	}

	/**
	 * Ten character 3-grams should be generated from "abecedario. ".
	 */
	@Test
	public void testGetStrings() {		
		String t = "abecedario. ";
		assertEquals("This string should generate eight 3-grams", 10, 
					cNgrams.getStrings(t).size());
	}
	
	/**
	 * Every diacritic from "áèîöñ" should be removed.
	 */
	@Test
	public void testGetWithoutDiacritics() {		
		cNgrams = new CharacterNgrams(5);
		String t = "áèîöñ";
		assertEquals("aeion", cNgrams.getStrings(t).get(0));
		
		
	}
	
	
	
//	@Test
//	public void testGetNgrams() {
//		
//		cNgrams.setN(3);
//		
//		
//	}

}
