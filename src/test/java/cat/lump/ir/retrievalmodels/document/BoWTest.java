package cat.lump.ir.retrievalmodels.document;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
//import static org.junit.matchers.JUnitMatchers.*;

public class BoWTest {

	private Representation bow;
	private final String text = "Los coleópteros (Coleoptera) son un orden de "
			+ "insectos con unas 375.000 especies descritas; tiene tantas especies "
			+ "como las plantas vasculares o los hongos y 66 veces más especies que "
			+ "los mamíferos.";
	
	@Before
	public void setUp() throws Exception {
		bow = new BoW(new Dictionary(), 
				new Locale("es"));
		bow.setText(text);
	}	

	@Test
	public void testGetRepresentation(){
				
		List<String> expected = Arrays.asList(
			new String[] {
				"coleopter", 	"orden",	"insect",	
				"unas",			"375.000", 	"especi",	
				"descrit",		"tant", 	"plant", 
				"vascular",		"hong", 	"66", 
				"vec", 			"mamifer"	
									
		});
				
//		for (String t : bow.getRepresentation()){
//			System.out.println(t);
//		}
//		
//		System.out.println(bow.getRepresentation().size() + ""+ expected.size());
//		
//		for (String t : expected){
//			System.out.println(t);
//		}
		
//		Assert.assertThat(expected, is(equalTo(bow.getNormalizedRepresentation())));
		//System.out.println(bow.getRepresentation());
		Assert.assertEquals(expected, bow.getRepresentation());				
	}
	
	@Test
	public void testGetWeightedRepresentation(){
		Map<String, Double> expected= new HashMap<String, Double>();
		expected.put("hong", 1.0);
		expected.put("plant", 1.0);
		expected.put("orden", 1.0);
		expected.put("66", 1.0);
		expected.put("vascular", 1.0);
		expected.put("tant", 1.0);
		expected.put("coleopter", 2.0);
		expected.put("mamifer", 1.0);
		expected.put("especi", 3.0);
		expected.put("descrit", 1.0);
		expected.put("unas", 1.0);
		expected.put("insect", 1.0);
		expected.put("375.000", 1.0);
		expected.put("vec", 1.0);
		
		//NOTE punctuation marks are not included as they are discarded 
		//during preprocessing (stopword deletion)
		
		Assert.assertEquals(expected, bow.getWeightedRepresentation());
	}
	
	@Test
	public void testGetNormalizedNormalizedRepresentation(){		
		Map<String, Double> expected= new HashMap<String, Double>();
		expected.put("hong",	0.058823529411764705);
		expected.put("plant",	0.058823529411764705);
		expected.put("orden",	0.058823529411764705);
		expected.put("66",		0.058823529411764705);
		expected.put("vascular",0.058823529411764705);
		expected.put("tant",	0.058823529411764705);
		expected.put("coleopter",0.11764705882352941);
		expected.put("mamifer",	0.058823529411764705);
		expected.put("especi",	0.17647058823529413);
		expected.put("descrit",	0.058823529411764705);
		expected.put("insect",	0.058823529411764705);
		expected.put("unas",	0.058823529411764705);			
		expected.put("375.000",	0.058823529411764705);
		expected.put("vec",		0.058823529411764705);
		
		
		Assert.assertThat(expected, is(equalTo(bow.getNormalizedRepresentation())));
//		assertEqualsMapEpsilon(expected, map, 0.0000001);
	}

	


	/**
	 * This simple method allows for comparing to maps with double values. By
	 * usign it, small differences are allowed.
	 * @param expected
	 * @param actual
	 * @param epsilon
	 */
	public static void assertEqualsMapEpsilon(Map<String,Double> expected, 
			Map<String,Double> actual, double epsilon) {
		Assert.assertEquals(expected.size(), actual.size());
		for(Map.Entry<String,Double> value:expected.entrySet()){
			Double actualValue = actual.get(value.getKey());
			Assert.assertNotNull(actualValue);
			Assert.assertEquals(value.getValue(), actualValue, epsilon);
		}
	}


	
}
