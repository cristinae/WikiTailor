package cat.lump.aq.textextraction.wikipedia.prepro;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Locale;

import org.junit.Test;


/** 
 * Test for the {@code TermExtractor} class.
 */

public class TermExtractorTest {

	/** Arabic terms */
	@Test
	public void testAr() {
		TermExtractor te = new TermExtractor(Locale.forLanguageTag("ar"));
		String text = "أنا أحب البطاطا الْجُمْعَةِ. You fool!";
		List<String> tokens = te.getTerms(text, 4);
		assertEquals("احب", tokens.get(0));
		assertEquals("بطاطا", tokens.get(1));
		assertEquals("جمع", tokens.get(2));
		assertEquals("fool", tokens.get(3));
		/*String text = "يذهبون";               // The stemmer should be doing this!
		assertEquals("ذهب", tokens.get(0));
		String text = "سيارتنا";
		assertEquals("سيار", tokens.get(0));*/
	}


	/** Catalan terms */
	@Test
	public void testCa() {
		TermExtractor te = new TermExtractor(Locale.forLanguageTag("ca"));
		List<String> tokens = te.getTerms("Menjarem a la una, i explicarem històries", 4);
		assertEquals("menj", tokens.get(0));
		assertEquals("explic", tokens.get(1));
		assertEquals("histor", tokens.get(2));
	}
	
	/** German terms */
	@Test
	public void testDe() {
		TermExtractor te = new TermExtractor(Locale.forLanguageTag("de"));
		List<String> tokens = te.getTerms("Das Mädchen und der Junge laufen. Sie sagt, daß schnell!", 4);
		assertEquals("madch", tokens.get(0));
		assertEquals("jung", tokens.get(1));
		assertEquals("lauf", tokens.get(2));
		assertEquals("schnell", tokens.get(4));
	}

	/** English terms */
	@Test
	public void testEn() {
		TermExtractor te = new TermExtractor(Locale.forLanguageTag("en"));
		List<String> tokens = te.getTerms("My friend had a déjà vu yesterday.", 4);
		assertEquals("friend", tokens.get(0));
		assertEquals("deja", tokens.get(1));
		assertEquals("yesterday", tokens.get(2));
	}

	/** Spanish terms */
	@Test
	public void testEs() {
		TermExtractor te = new TermExtractor(Locale.forLanguageTag("ca"));
		List<String> tokens = te.getTerms("Comeré a la una, y explicaré historias!", 4);
		assertEquals("explic", tokens.get(0));
		assertEquals("histor", tokens.get(1));
	}

}
