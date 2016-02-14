package cat.lump.ir.retrievalmodels.document;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WrdNgramsTest {

	private Representation wng;
	private final String text = "L'Hospital de la Santa Creu i Sant Pau "
			+ "és un conjunt modernista situat a Barcelona.";			
	
	@Before
	public void setUp() throws Exception {
		wng = new WrdNgrams(new Dictionary(), 
				new Locale("es"), 7);
		wng.setText(text);
	}	

	@Test
	public void testGetRepresentation(){
				
		List<String> expected = Arrays.asList(
			new String[] {
				"l'hospital de la santa creu i sant",
				"de la santa creu i sant pau",
				"la santa creu i sant pau es",
				"santa creu i sant pau es un",	
				"creu i sant pau es un conjunt",
				"i sant pau es un conjunt modernista",	
				"sant pau es un conjunt modernista situat",
				"pau es un conjunt modernista situat a",	
				"es un conjunt modernista situat a barcelona",
				"un conjunt modernista situat a barcelona ."
		});

		Assert.assertEquals(expected, wng.getRepresentation());
		
		//Alberto (Mar 17, 2014): trying to do the test with hamcrest 
//		Assert.assertThat(cng.getRepresentation(), (Matcher) hasItems(expected));
		
//		assertThat(
//				cng.getRepresentation(), contains("arcel")
//				
////		        (Matcher) IsIterableContainingInAnyOrder.<String>containsInAnyOrder(expected.toArray())
//		        );
//		
		
//		Assert.assertThat(cng.getRepresentation(),  hasItems(expected.toArray()));
//				"l'hos",	"'hosp",	"hospi",
//				"ospit",	"spita",	"pital",
//				"ital ",	"tal d",	"al de",
//				"l de ",	" de l",	"de la",
//				"e la ",	" la s",	"la sa",
//				"a san",	"santa",	" sant",
//				"nta c",	"anta ",	"a cre",
//				"ta cr",	"creu ",	" creu",
//				"eu i ",	"reu i",	" i sa",
//				"u i s",	"sant ",	"i san",
//				"nt pa",	"ant p",	"pau e",
//				"au es",	"t pau",	" pau ",
//				"es un",	"s un ",	"u es ",
//				" es u",	"n con",	" conj",
//				" un c",	"un co",	"njunt",
//				"junt ",	"conju",	"onjun",
//				" mode",	"t mod",	"nt mo",
//				"unt m",	"ernis",	"derni",
//				"odern",	"moder",	"sta s",
//				"ista ",	"nista",	"rnist",
//				"situa",	" situ",	"a sit",
//				"ta si",	"t a b",	" a ba",
//				"a bar",	" barc",	"ituat",
//				"tuat ",	"uat a",	"at a ",
//				"elona",	"barce",	"arcel",
//				"rcelo",	"celon"
//				));
	}
	
	@Test
	public void testGetWeightedRepresentation(){
		wng = new WrdNgrams(new Dictionary(), 
				new Locale("es"), 7);
		wng.setText(text);
		
		Map<String, Double> expected= new HashMap<String, Double>();
		expected.put("santa creu i sant pau es un", 1.0);
		expected.put("i sant pau es un conjunt modernista", 1.0);
		expected.put("de la santa creu i sant pau", 1.0);
		expected.put("sant pau es un conjunt modernista situat", 1.0);
		expected.put("creu i sant pau es un conjunt", 1.0);
		expected.put("es un conjunt modernista situat a barcelona", 1.0);
		expected.put("pau es un conjunt modernista situat a", 1.0);
		expected.put("la santa creu i sant pau es", 1.0);
		expected.put("l'hospital de la santa creu i sant", 1.0);
		expected.put("un conjunt modernista situat a barcelona .", 1.0);
				
		Assert.assertEquals(expected, wng.getWeightedRepresentation());	
	}
	
	@Test
	public void testGetNormalizedNormalizedRepresentation(){		
		Map<String, Double> expected= new HashMap<String, Double>();
		expected.put("santa creu i sant pau es un", 				0.1);
		expected.put("i sant pau es un conjunt modernista", 		0.1);
		expected.put("de la santa creu i sant pau",				0.1);
		expected.put("sant pau es un conjunt modernista situat", 0.1);
		expected.put("creu i sant pau es un conjunt",			0.1);
		expected.put("es un conjunt modernista situat a barcelona", 0.1);
		expected.put("pau es un conjunt modernista situat a",	0.1);
		expected.put("la santa creu i sant pau es",				0.1);
		expected.put("l'hospital de la santa creu i sant",		0.1);
		expected.put("un conjunt modernista situat a barcelona .", 0.1);
		
		
		Assert.assertEquals(expected, wng.getNormalizedRepresentation());
	}

}
