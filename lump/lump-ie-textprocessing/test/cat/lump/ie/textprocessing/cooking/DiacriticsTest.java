package cat.lump.ie.textprocessing.cooking;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cat.lump.ie.textprocessing.sentence.Diacritics;

public class DiacriticsTest {

	@Test
	public void testRemoveDiacritics() {
		String text = "carácter DIACRÍTICOS aôut pequeño barça pingüino";
		assertEquals("accents, tildes, and others should be removed",
				"caracter DIACRITICOS aout pequeno barca pinguino",
				Diacritics.removeDiacritics(text));	

	}

}
