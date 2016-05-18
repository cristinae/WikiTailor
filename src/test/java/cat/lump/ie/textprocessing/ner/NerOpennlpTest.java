package cat.lump.ie.textprocessing.ner;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cat.lump.ie.textprocessing.Span;

public class NerOpennlpTest {

	private NerOpennlp nerEN;
	private NerOpennlp nerES;
	
	@Before
	public void setUp() throws Exception {
		 nerEN = new NerOpennlp(Locale.ENGLISH);
		 nerES = new NerOpennlp(new Locale("es"));
	}


//	@Test
//	public void testGetStringsStringArray() {
//		
//		
//		fail("Not yet implemented");
//	}
//
	/**Checks that proper names are correctly extracted */
	@Test
	public void testGetStringsString() {
		String[] text = new String[]{
			"The",			"Liberal",	"Party",
			"led",			"by",		"Will",	
			"Hodgman",		"have",		"won",
			"government",	"in",		"the",
			"Tasmanian",	"election", "."
		};
		List<String> nes = nerEN.getStrings(text);
		for (String x: nes){
			System.out.println(x);
		}
		List<String> expected = new ArrayList<String>();
		expected.add("Will Hodgman");
		
		Assert.assertEquals(expected, nerEN.getStrings(text));
	}

	/**
	 * Checks that proper names are correctly extracted from a 
	 * text in Spanish
	 */
	@Test
	public void testGetStringsStringEs() {
		String[] text = new String[]{
			"Alfredo",		"Pérez",		"Rubalcaba",
			"(",			"Solares",		",",	
			"Cantabria",	",",			"España", 
			",",			"es",			"un",
			"político",		"español",		"del",	
			"Partido",		"Socialista",	 "."
		};

		List<String> expected = new ArrayList<String>();
		expected.add("Alfredo Pérez Rubalcaba");
		
		Assert.assertEquals(expected, nerES.getStrings(text));
	}
	
	
	@Test
	public void testGetSpans() {
		String[] text = new String[]{
			"The",			"Liberal",	"Party",
			"led",			"by",		"Will",	
			"Hodgman",		"have",		"won",
			"government",	"in",		"the",
			"Tasmanian",	"election", "."
				
			//THIS ONE IS FOR PLACE
//				"Doha",		"(",		"literally",	
//			":",		"\"",		"the",	
//			"big",		"tree",		"\"",
//			")",		"is",		"the",	
//			"capital",	"city",		"of",	
//			"the",		"state",	"of",
//			"Qatar", 	"."
		};
		List<Span> spans = nerEN.getSpans(text);
//		for (Span x: spans){
//			System.out.println(x.getStart() + " " + x.getEnd());
//		}
		//Span expected = new Span(5,7);
		Assert.assertTrue(spans.size() == 1);
		//TODO HOW TO VALIDATE THE CONTENTS
//		Assert.assertEquals(expected, spans.get(0));
		//Assert.assertTrue(expected.equals(spans.get(0)));
		
	}

}
