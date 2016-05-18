package cat.lump.ir.retrievalmodels.document;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CharNgramsTest {

	private Representation cng;
	private final String text = "L'Hospital de la Santa Creu i Sant Pau "
			+ "és un conjunt modernista situat a Barcelona";			
	
	@Before
	public void setUp() throws Exception {
		cng = new CharNgrams(new Dictionary(), 
				new Locale("es"), 5);
		cng.setText(text);
	}	

	@Test
	public void testGetRepresentation(){
				
		List<String> expected = Arrays.asList(
			new String[] {
				"l'hos",	"'hosp",	"hospi",
				"ospit",	"spita",	"pital",
				"ital ",	"tal d",	"al de",
				"l de ",	" de l",	"de la",
				"e la ",	" la s",	"la sa",
				"a san",	" sant", 	"santa",	
				"anta ",	"nta c",	"ta cr",	
				"a cre",	" creu",	"creu ",
				"reu i",	"eu i ",	"u i s",	
				" i sa",	"i san",	"sant ",
				"ant p",	"nt pa",	"t pau",
				" pau ",	"pau e",	"au es",	
				"u es ",	" es u",	"es un",	
				"s un ",	" un c",	"un co",
				"n con",	" conj",	"conju",
				"onjun",	"njunt",	"junt ",	
				"unt m",	"nt mo",	"t mod", 
				" mode",	"moder",	"odern",
				"derni",	"ernis",	"rnist",
				"nista",	"ista ",	"sta s",
				"ta si",	"a sit",	" situ",
				"situa",	"ituat",	"tuat ",
				"uat a",	"at a ",	"t a b",
				" a ba",	"a bar",	" barc",
				"barce",	"arcel",	"rcelo",
				"celon",	"elona"	
		});
		
		Assert.assertEquals(expected, cng.getRepresentation());
		
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
		Map<String, Double> expected= new HashMap<String, Double>();
		
		expected.put("barce", 1.0);	expected.put("a bar", 1.0);	expected.put("t mod", 1.0);	
		expected.put("ernis", 1.0);	expected.put("elona", 1.0);	expected.put("arcel", 1.0);
		expected.put(" sant", 2.0);	expected.put("spita", 1.0);	expected.put(" barc", 1.0);
		expected.put("i san", 1.0);	expected.put("sant ", 1.0);	expected.put("pau e", 1.0);
		expected.put(" a ba", 1.0);	expected.put("njunt", 1.0);	expected.put("rnist", 1.0);
		expected.put("odern", 1.0);	expected.put("la sa", 1.0);	expected.put(" es u", 1.0);
		expected.put("anta ", 1.0);	expected.put("situa", 1.0);	expected.put("a sit", 1.0);
		expected.put(" i sa", 1.0);	expected.put("hospi", 1.0);	expected.put("l'hos", 1.0);
		expected.put("uat a", 1.0);	expected.put("u es ", 1.0);	expected.put("onjun", 1.0);
		expected.put("de la", 1.0);	expected.put("'hosp", 1.0);	expected.put("n con", 1.0);
		expected.put("creu ", 1.0);	expected.put("reu i", 1.0);	expected.put("t a b", 1.0);
		expected.put("ospit", 1.0);	expected.put("nta c", 1.0);	expected.put("s un ", 1.0);
		expected.put("celon", 1.0);	expected.put(" mode", 1.0);	expected.put("tal d", 1.0);
		expected.put("al de", 1.0);	expected.put("ital ", 1.0);	expected.put("es un", 1.0);
		expected.put("tuat ", 1.0);	expected.put("sta s", 1.0);	expected.put("e la ", 1.0);
		expected.put("moder", 1.0);	expected.put("ituat", 1.0);	expected.put("nt pa", 1.0);
		expected.put("au es", 1.0);	expected.put(" conj", 1.0);	expected.put(" situ", 1.0);
		expected.put("un co", 1.0);	expected.put("rcelo", 1.0);	expected.put(" la s", 1.0);
		expected.put("a cre", 1.0);	expected.put("santa", 1.0);	expected.put("unt m", 1.0);	
		expected.put("u i s", 1.0);	expected.put(" pau ", 1.0);	expected.put(" de l", 1.0);
		expected.put("t pau", 1.0);	expected.put("derni", 1.0);	expected.put("ista ", 1.0);
		expected.put("l de ", 1.0);	expected.put("ta si", 1.0);	expected.put("nista", 1.0);
		expected.put("at a ", 1.0);	expected.put("a san", 1.0);	expected.put("ta cr", 1.0);
		expected.put(" creu", 1.0);	expected.put(" un c", 1.0);	expected.put("nt mo", 1.0);
		expected.put("junt ", 1.0);	expected.put("conju", 1.0);	expected.put("ant p", 1.0);
		expected.put("eu i ", 1.0);	expected.put("pital", 1.0);
				
		
		Assert.assertEquals(expected, cng.getWeightedRepresentation());	
	}
	
	@Test
	public void testGetNormalizedNormalizedRepresentation(){
		Map<String, Double> expected= new HashMap<String, Double>();
		expected.put("barce", 0.01282051282051282);	expected.put("a bar", 0.01282051282051282);
		expected.put("t mod", 0.01282051282051282);	expected.put("ernis", 0.01282051282051282);
		expected.put("elona", 0.01282051282051282);	expected.put("arcel", 0.01282051282051282);
		expected.put(" sant", 0.02564102564102564);	expected.put("spita", 0.01282051282051282);
		expected.put(" barc", 0.01282051282051282);	expected.put("i san", 0.01282051282051282);
		expected.put("sant ", 0.01282051282051282);	expected.put("pau e", 0.01282051282051282);
		expected.put(" a ba", 0.01282051282051282);	expected.put("njunt", 0.01282051282051282);
		expected.put("rnist", 0.01282051282051282);	expected.put("odern", 0.01282051282051282);
		expected.put("la sa", 0.01282051282051282);	expected.put(" es u", 0.01282051282051282);
		expected.put("anta ", 0.01282051282051282);	expected.put("situa", 0.01282051282051282);
		expected.put("a sit", 0.01282051282051282);	expected.put(" i sa", 0.01282051282051282);
		expected.put("hospi", 0.01282051282051282);	expected.put("l'hos", 0.01282051282051282);
		expected.put("uat a", 0.01282051282051282);	expected.put("u es ", 0.01282051282051282);
		expected.put("onjun", 0.01282051282051282);	expected.put("de la", 0.01282051282051282);
		expected.put("'hosp", 0.01282051282051282);	expected.put("n con", 0.01282051282051282);
		expected.put("creu ", 0.01282051282051282);	expected.put("reu i", 0.01282051282051282);
		expected.put("t a b", 0.01282051282051282);	expected.put("ospit", 0.01282051282051282);
		expected.put("nta c", 0.01282051282051282);	expected.put("s un ", 0.01282051282051282);
		expected.put("celon", 0.01282051282051282);	expected.put(" mode", 0.01282051282051282);
		expected.put("tal d", 0.01282051282051282);	expected.put("al de", 0.01282051282051282);
		expected.put("ital ", 0.01282051282051282);	expected.put("es un", 0.01282051282051282);
		expected.put("tuat ", 0.01282051282051282);	expected.put("sta s", 0.01282051282051282);
		expected.put("e la ", 0.01282051282051282);	expected.put("moder", 0.01282051282051282);
		expected.put("ituat", 0.01282051282051282);	expected.put("nt pa", 0.01282051282051282);
		expected.put("au es", 0.01282051282051282);	expected.put(" conj", 0.01282051282051282);
		expected.put(" situ", 0.01282051282051282);	expected.put("un co", 0.01282051282051282);
		expected.put("rcelo", 0.01282051282051282);	expected.put(" la s", 0.01282051282051282);
		expected.put("a cre", 0.01282051282051282);	expected.put("santa", 0.01282051282051282);
		expected.put("unt m", 0.01282051282051282);	expected.put("u i s", 0.01282051282051282);
		expected.put(" pau ", 0.01282051282051282);	expected.put(" de l", 0.01282051282051282);
		expected.put("t pau", 0.01282051282051282);	expected.put("derni", 0.01282051282051282);
		expected.put("ista ", 0.01282051282051282);	expected.put("l de ", 0.01282051282051282);
		expected.put("ta si", 0.01282051282051282);	expected.put("nista", 0.01282051282051282);
		expected.put("at a ", 0.01282051282051282);	expected.put("a san", 0.01282051282051282);
		expected.put("ta cr", 0.01282051282051282);	expected.put(" creu", 0.01282051282051282);
		expected.put(" un c", 0.01282051282051282);	expected.put("nt mo", 0.01282051282051282);
		expected.put("junt ", 0.01282051282051282);	expected.put("conju", 0.01282051282051282);
		expected.put("ant p", 0.01282051282051282);	expected.put("eu i ", 0.01282051282051282);
		expected.put("pital", 0.01282051282051282);
			
		
//		Map<String, Double> map = cng.getNormalizedRepresentation();
//		for (String k : map.keySet()){
//			System.out.println("put(\""+k + "\", " + map.get(k) + ");");
//		}
		Assert.assertEquals(expected, cng.getNormalizedRepresentation());
	}

}
