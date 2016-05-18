package cat.lump.ie.textprocessing.sentence;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** * 
 * @author albarron
 *
 */
public class PunctuationTest {

	@Test
	public void testContainsPunctuation() {
		String text="is contains a marks:";
		assertTrue("it should coutn 3 punctuation marks",		
				Punctuation.containsPunctuation(text));		
	}

	@Test
	public void testOnlyPunctuation() {
		String text=".,;()";
		assertTrue("contains only punctuation marks",		
				Punctuation.containsPunctuation(text));
	}
}