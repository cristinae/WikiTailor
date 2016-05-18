package cat.lump.ir.weighting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cat.lump.aq.basics.structure.ir.TermFrequencyTuple;

public class TermFrequencyTest {

	private TermFrequency tf;
	private final List<String> terms = 
	    Arrays.asList("Cabra", "Cavall", "Cérvol", "Conill", "Daina", "Eriçó", 
    		"Ermini", "Esquirol", "Gat", "Gos", "Guineu", "Isard", "Llebre", 
    		"Llúdriga", "Marmota", "Musaranya", "Ovella", "Porc", "Ratolí", 
    		"Rata", "Rat-penat", "Talp", "Vaca", "Granota", "Reineta", "Gripau", 
    		"Gripauet", "Salamandra", "Tritó", "Cap-roig", "Cavallet", "Tacó", 
    		"Truita", "Barb", "Carpa", "Llenguado", "Lluç", "Sardina", "Dragó", 
    		"Llangardaix", "Sargantana", "Serp", "Tortuga", "Abellerol", "Àguila", 
    		"Aligot", "Ànec", "Cadernera", "Capsigrany", "Cigne", "Esquirol", 
    		"Gat", "Tortuga", "Esquirol", "Gat", "Tortuga", "Esquirol", "Gat", 
    		"Tortuga", "Esquirol", "Gat", "Tortuga", "Esquirol", "Gat", "Tortuga", 
    		"Esquirol", "Gat", "Tortuga", "Sargantana","Cigne", "Sargantana",
    		"Cigne", "Sargantana","Cigne", "Sargantana","Cigne", "Sargantana",
    		"Cigne", "Cavall", "Cérvol", "Llenguado", "Cavall", "Cérvol", 
    		"Llenguado", "Cavall", "Cérvol", "Llenguado", "Cavall", "Cérvol", 
    		"Llenguado", "Cadernera", "Capsigrany", "Cadernera", "Capsigrany", 
    		"Cadernera", "Capsigrany", "Llúdriga", "Marmota", "Llúdriga", "Marmota");

	private final int numAnimals = 50;
	private final String missingTerm = "Mussol";
	private final String presentTerm = "Capsigrany";
    private final int freqPresentTerm = 4;

	@Before
	public void setUp() throws Exception {
		tf = new TermFrequency();
		/*for (String term : terms){
			tf.put(term, new TermFrequencyTuple(term, 1));
		}*/
		tf.addTerms(terms);	
	}
	
    /* This is tested in testGetAll() 
	@Test
	public void testListOfTermFrequencyTuple() {
		fail("Not yet implemented");
	}
	*/

	@Test
	public void testExistTerm() {
		Assert.assertEquals(tf.existTerm(presentTerm), true);	
		Assert.assertEquals(tf.existTerm(presentTerm.concat("a")), false);	
		Assert.assertEquals(tf.existTerm(missingTerm), false);	
	}

/*	Funcionara per definicio
    @Test
	public void testAddTerm() {
		fail("Not yet implemented");
	}
	@Test
	public void testAddTerms() {
		fail("Not yet implemented");
	}
*/

	
	/**We now test the application the proper removal of single and multiple 
	 * terms by deleting one, checking and then all the terms from the original 
	 * one and ending into a size of 0.
	 * </br>
	 * The original implementation is commented after. It was discarded because 
	 * the resulting lists had to be sorted; something unnecessary if we are 
	 * actually interested in whether two lists are identical. 
	 */
	@Test
	public void testRemoveTerm() {
			   
		List<String> shorterTerms = Arrays.asList(
			"Cabra",		"Cavall",	"Cérvol",		"Conill",	"Daina",
			"Eriçó",    	"Ermini",	"Esquirol",		"Gat",		"Gos",
			"Guineu",		"Isard",    "Llebre",   	"Llúdriga",	"Marmota",
			"Musaranya",	"Ovella",	"Porc",		    "Ratolí",	"Rata",
			"Rat-penat",	"Talp",		"Vaca",			"Granota",  "Reineta",
			"Gripau",		"Gripauet",	"Salamandra",	"Tritó",	"Cap-roig",	
			"Cavallet",		"Tacó",		"Truita",		"Barb",		"Carpa",	
			"Llenguado",	"Lluç",		"Sardina",		"Dragó",	"Llangardaix",
			"Sargantana",	"Serp",		"Tortuga",		"Abellerol","Àguila", 
		    "Aligot",		"Ànec",		"Cadernera",	"Cigne",	"Esquirol", 
		    "Gat",			"Tortuga",	"Esquirol",		"Gat",		"Tortuga",	
		    "Esquirol",		"Gat", 		"Tortuga",		"Esquirol",	"Gat",
		    "Tortuga",		"Esquirol",	"Gat",			"Tortuga",	"Esquirol", 
		    "Gat",			"Tortuga",	"Sargantana",	"Cigne",	"Sargantana",
		    "Cigne",		"Sargantana","Cigne",		"Sargantana","Cigne",	
		    "Sargantana",	"Cigne",	"Cavall",		"Cérvol",	"Llenguado",
		    "Cavall",		"Cérvol", 	"Llenguado",	"Cavall",	"Cérvol",	
		    "Llenguado",	"Cavall",	"Cérvol",  		"Llenguado","Cadernera",
		    "Cadernera",	"Cadernera",	"Llúdriga",	"Marmota",	"Llúdriga",	
		    "Marmota");
		
		int actualSize = tf.size();
		tf.removeTerm(presentTerm);
		
		Assert.assertEquals(actualSize -1 , tf.size());
		tf.removeTerms(shorterTerms);
		
	    Assert.assertEquals(tf.size(),  0);	
	}
	
//	@Test
//	public void testRemoveTerm() {
//		   ArrayList<String> shorterTerms = new ArrayList<String>(
//			        Arrays.asList("Cabra", "Cavall", "Cérvol", "Conill", "Daina", "Eriçó", 
//			    		"Ermini", "Esquirol", "Gat", "Gos", "Guineu", "Isard", "Llebre", 
//			    		"Llúdriga", "Marmota", "Musaranya", "Ovella", "Porc", "Ratolí", 
//			    		"Rata", "Rat-penat", "Talp", "Vaca", "Granota", "Reineta", "Gripau", 
//			    		"Gripauet", "Salamandra", "Tritó", "Cap-roig", "Cavallet", "Tacó", 
//			    		"Truita", "Barb", "Carpa", "Llenguado", "Lluç", "Sardina", "Dragó", 
//			    		"Llangardaix", "Sargantana", "Serp", "Tortuga", "Abellerol", "Àguila", 
//			    		"Aligot", "Ànec", "Cadernera", "Cigne", "Esquirol", 
//			    		"Gat", "Tortuga", "Esquirol", "Gat", "Tortuga", "Esquirol", "Gat", 
//			    		"Tortuga", "Esquirol", "Gat", "Tortuga", "Esquirol", "Gat", "Tortuga", 
//			    		"Esquirol", "Gat", "Tortuga", "Sargantana", "Cigne", "Sargantana",
//			    		"Cigne", "Sargantana","Cigne", "Sargantana", "Cigne", "Sargantana",
//			    		"Cigne", "Cavall", "Cérvol", "Llenguado", "Cavall", "Cérvol", 
//			    		"Llenguado", "Cavall", "Cérvol", "Llenguado", "Cavall", "Cérvol", 
//			    		"Llenguado", "Cadernera", "Cadernera", "Cadernera", "Llúdriga", 
//			    		"Marmota", "Llúdriga", "Marmota"));;
//			TermFrequency tfShort = new TermFrequency();
//			tfShort.addTerms(shorterTerms);	
//			List<TermFrequencyTuple> tftShort = tfShort.getAll();
//
//			TermFrequency tfTmp = new TermFrequency();
//			tfTmp = tf;
//			tfTmp.removeTerm(presentTerm);			
//			List<TermFrequencyTuple> tftTmp = tfTmp.getAll();
//
//			
//			for (TermFrequencyTuple t : tftTmp){
//				System.out.println(t.toString());
//			}
//
//			Comparator<TermFrequencyTuple> highToLow = new Comparator<TermFrequencyTuple>() {
//		    	@Override
//		    	public int compare(TermFrequencyTuple o1, TermFrequencyTuple o2){
//		    		return o2.getTerm().compareTo(o1.getTerm());
//		    	}
//		    };
//		    Collections.sort(tftTmp, highToLow);
//		    Collections.sort(tftShort, highToLow);
//		    Assert.assertEquals(tftShort, tftTmp);	
//	}

	@Test
	public void testRemoveTerms() {
		   List<String> shorterTerms = 
			        Arrays.asList("Cabra", "Cavall", "Cérvol", "Conill", "Daina", "Eriçó", 
			    		"Ermini", "Esquirol", "Gat", "Gos", "Guineu", "Isard", "Llebre", 
			    		"Llúdriga", "Marmota", "Musaranya", "Ovella", "Porc", "Ratolí", 
			    		"Rata", "Rat-penat", "Talp", "Vaca", "Granota", "Reineta", "Gripau", 
			    		"Gripauet", "Salamandra", "Tritó", "Cap-roig", "Cavallet", "Tacó", 
			    		"Truita", "Barb", "Carpa", "Llenguado", "Lluç", "Sardina", "Dragó", 
			    		"Llangardaix", "Sargantana", "Serp", "Tortuga", "Abellerol", "Àguila", 
			    		"Aligot", "Ànec", "Cadernera", "Cigne");
		   TermFrequency tfTmp1 = new TermFrequency();
		   tfTmp1.addTerms(terms);
		   tfTmp1.removeTerms(shorterTerms);			
		   List<TermFrequencyTuple> tftTmp1 = tfTmp1.getAll();

		   List<TermFrequencyTuple> tftExpected = new ArrayList<TermFrequencyTuple>();
		   tftExpected.add(new TermFrequencyTuple(presentTerm,freqPresentTerm)); 
 		   Assert.assertEquals(tftExpected, tftTmp1);
		   
 		   /* Remove all terms */
		   ArrayList<String> allTerms = new ArrayList<String>(
			        Arrays.asList("Cabra", "Cavall", "Cérvol", "Conill", "Daina", "Eriçó", 
			    		"Ermini", "Esquirol", "Gat", "Gos", "Guineu", "Isard", "Llebre", 
			    		"Llúdriga", "Marmota", "Musaranya", "Ovella", "Porc", "Ratolí", 
			    		"Rata", "Rat-penat", "Talp", "Vaca", "Granota", "Reineta", "Gripau", 
			    		"Gripauet", "Salamandra", "Tritó", "Cap-roig", "Cavallet", "Tacó", 
			    		"Truita", "Barb", "Carpa", "Llenguado", "Lluç", "Sardina", "Dragó", 
			    		"Llangardaix", "Sargantana", "Serp", "Tortuga", "Abellerol", "Àguila", 
			    		"Aligot", "Ànec", "Cadernera", "Cigne", "Capsigrany"));
		   TermFrequency tfTmpall = new TermFrequency();
		   tfTmpall.addTerms(terms); 
		   tfTmpall.removeTerms(allTerms);			
		   List<TermFrequencyTuple> tftTmpall = tfTmpall.getAll();
		   Assert.assertTrue(tftTmpall.isEmpty()); 		   
	}

	@Test
	public void testGetTerm() {
		TermFrequencyTuple expectedTuple = new TermFrequencyTuple(presentTerm, freqPresentTerm);
		Assert.assertEquals(tf.getTerm(presentTerm), expectedTuple);	
	}

	@Test
	public void testGetAll() {
		Map<Integer, List<String>> expected = new HashMap<Integer, List<String>>();
		expected.put(7, 
				Arrays.asList(new String[]{"Esquirol", "Gat", "Tortuga"}));		 
		expected.put(6, 
				Arrays.asList(new String[]{"Cigne", "Sargantana"}));
		expected.put(5, 
				Arrays.asList(new String[]{"Cavall", "Cérvol", "Llenguado"}));		
		expected.put(4, 
				Arrays.asList(new String[]{"Cadernera", "Capsigrany"}));
		expected.put(3, 
				Arrays.asList(new String[]{"Llúdriga","Marmota"}));

		expected.put(1, 
				Arrays.asList(new String[]{
					"Abellerol", "Aligot", "Barb", "Cabra", "Cap-roig",	"Carpa",
					"Cavallet",	"Conill", "Daina", "Dragó", "Eriçó", "Ermini", 
					"Gos", "Granota", "Gripau", "Gripauet", "Guineu", "Isard", 
					"Llangardaix", "Llebre", "Lluç", "Musaranya", "Ovella", 
					"Porc", "Rat-penat", "Rata", "Ratolí", "Reineta", 
					"Salamandra", "Sardina", "Serp", "Tacó", "Talp", "Tritó", 
					"Truita", "Vaca", "Àguila", "Ànec"
		})); 

		    
			
		Map<Integer, List<String>> actual = new HashMap<Integer, List<String>>();
			
		List<TermFrequencyTuple> tfTuples = tf.getAll();
		    			
		for (TermFrequencyTuple tft : tfTuples){
			if (! actual.containsKey(tft.getFrequency())){
				actual.put(tft.getFrequency(), new ArrayList<String>());
			}
			actual.get(tft.getFrequency()).add(tft.getTerm());		
		}
		     
		   for (Integer k : actual.keySet()){
		   	Collections.sort(actual.get(k));
		}
		
		
		Assert.assertEquals(expected, actual);	
	    
	}
	
	
//	@Test
//	public void testGetAll() {
//		List<TermFrequencyTuple> expectedTuples = new ArrayList<TermFrequencyTuple>();
//		expectedTuples.add(new TermFrequencyTuple("Esquirol",7)); 
//		expectedTuples.add(new TermFrequencyTuple("Gat",7));
//	    expectedTuples.add(new TermFrequencyTuple("Tortuga",7)); 
//	    expectedTuples.add(new TermFrequencyTuple("Sargantana",6)); 
//	    expectedTuples.add(new TermFrequencyTuple("Cigne",6));
//		expectedTuples.add(new TermFrequencyTuple("Cavall",5));
//		expectedTuples.add(new TermFrequencyTuple("Cérvol",5)); 
//	    expectedTuples.add(new TermFrequencyTuple("Llenguado",5)); 
//	    expectedTuples.add(new TermFrequencyTuple("Cadernera",4)); 
//	    expectedTuples.add(new TermFrequencyTuple("Capsigrany",4));
//	    expectedTuples.add(new TermFrequencyTuple("Llúdriga",3));
//	    expectedTuples.add(new TermFrequencyTuple("Marmota", 3));
//		expectedTuples.add(new TermFrequencyTuple("Cabra",1)); 
//		expectedTuples.add(new TermFrequencyTuple("Conill",1)); 
//		expectedTuples.add(new TermFrequencyTuple("Daina",1)); 
//		expectedTuples.add(new TermFrequencyTuple("Eriçó",1)); 
//		expectedTuples.add(new TermFrequencyTuple("Ermini",1)); 
//		expectedTuples.add(new TermFrequencyTuple("Gos",1));
//		expectedTuples.add(new TermFrequencyTuple("Guineu",1)); 
//		expectedTuples.add(new TermFrequencyTuple("Isard",1)); 
//		expectedTuples.add(new TermFrequencyTuple("Llebre",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Musaranya",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Ovella",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Porc",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Ratolí",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Rata",1)); 
//        expectedTuples.add(new TermFrequencyTuple("Rat-penat",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Talp",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Vaca",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Granota",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Reineta",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Gripau",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Gripauet",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Salamandra",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Tritó",1));
//	    expectedTuples.add(new TermFrequencyTuple("Cap-roig",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Cavallet",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Tacó",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Truita",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Barb",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Carpa",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Lluç",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Sardina",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Dragó",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Llangardaix",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Serp",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Abellerol",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Àguila",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Aligot",1)); 
//	    expectedTuples.add(new TermFrequencyTuple("Ànec",1)); 
//
//	    List<TermFrequencyTuple> tfTuples = tf.getAll();
//	    Comparator<TermFrequencyTuple> highToLow = new Comparator<TermFrequencyTuple>() {
//	    	@Override
//	    	public int compare(TermFrequencyTuple o1, TermFrequencyTuple o2){
//	    		return o2.getTerm().compareTo(o1.getTerm());
//	    	}
//	    };
//	    Collections.sort(tfTuples, highToLow);
//	    Collections.sort(expectedTuples, highToLow);
//	    Assert.assertEquals(expectedTuples, tfTuples);	    	
//	    
//	}

	
	
	/**Testing whether the top 10% of the terms are actually returned. We do 
	 * it by generating frequency-key maps with lists of the associated terms.
	 * TODO this could be a nice alternative for the TermFrequency class itself.
	 * </br> 
	 * The original test, based on comparators, is commented after.
	 * 
	 */
	@Test
	public void testGetTop() {
		
	    final int topPercentage = 10;

	    Map<Integer, List<String>> expected = new HashMap<Integer, List<String>>();
	    expected.put(7, 
	    		Arrays.asList(new String[]{"Esquirol", "Gat", "Tortuga"}));
	    expected.put(6, 
	    		Arrays.asList(new String[]{"Cigne", "Sargantana"}));
	    
		
		Map<Integer, List<String>> actual = new HashMap<Integer, List<String>>();
		
		List<TermFrequencyTuple> tfTuples = tf.getTop(topPercentage, -1);
	    
		for (TermFrequencyTuple tft : tfTuples){
			if (! actual.containsKey(tft.getFrequency())){
				actual.put(tft.getFrequency(), new ArrayList<String>());
			}
			actual.get(tft.getFrequency()).add(tft.getTerm());		
		}
	     
	    for (Integer k : actual.keySet()){
	    	Collections.sort(actual.get(k));
	    }    

	    Assert.assertEquals(expected, actual);	    	
	    	    
	}

	
	
//	@Test
//	public void testGetTop() {
//		List<TermFrequencyTuple> expectedTuples = new ArrayList<TermFrequencyTuple>();
//	    final int topPercentage = 10;
//
//		expectedTuples.add(new TermFrequencyTuple("Esquirol",7)); 
//	    expectedTuples.add(new TermFrequencyTuple("Gat",7));
//		expectedTuples.add(new TermFrequencyTuple("Tortuga",7)); 
//	    expectedTuples.add(new TermFrequencyTuple("Sargantana",6)); 
//	    expectedTuples.add(new TermFrequencyTuple("Cigne",6));
//		
//	    List<TermFrequencyTuple> tfTuples = tf.getTop(topPercentage);
//	    
//	    Comparator<TermFrequencyTuple> highToLow =     
//	    	new Comparator<TermFrequencyTuple>() {
//	    	@Override
//	    	public int compare(TermFrequencyTuple o1, TermFrequencyTuple o2){
//	    		return o2.getTerm().compareTo(o1.getTerm());
//	    	}
//	    };    
//	    
//	    
//	    Collections.sort(tfTuples, highToLow);
//	    Collections.sort(expectedTuples, highToLow);
//	    Assert.assertEquals(expectedTuples, tfTuples);	    	
//	    	    
//	}

	@Test
	public void testSize() {
		Assert.assertEquals(numAnimals, tf.size());	
	}

}
