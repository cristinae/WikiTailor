package cat.lump.ir.retrievalmodels.document;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DocumentTest {

	private Document doc;
	private final String text = "The Mars Volta fue una banda estadounidense "
			+ "de rock progresivo proveniente de El Paso, Texas y fundada en "
			+ "2001. Fundada por el guitarrista Omar Rodríguez-López y el "
			+ "vocalista Cedric Bixler-Zavala, la banda incorpora varias "
			+ "influencias incluyendo rock progresivo, Krautrock, jazz fusion, "
			+ "hard rock, y música latinoamericana dentro de su música. Fueron "
			+ "llamados \"La mejor banda prog-rock\" del 2008 por Rolling Stone.";
	
	@Before
	public void setUp() throws Exception {
		doc = new Document(text, new Locale("es"), true, true, true, true, 3);		
	}	
	
	
	@Test
	public void testGetText(){
		String expected = text;
//		System.out.println(doc.getText());
		Assert.assertEquals(expected, doc.getText());
	}
	
	@Test
	public void testGetFragment(){
		String expected = "The Mars Volta fue una banda estadounidense "
			+ "de rock progresivo proveniente de El Paso, Texas y fundada en "
			+ "2001."; 
		Assert.assertEquals(expected, doc.getFragment(0).getPlain());
	}
	
	@Test
	public void testGetNullFragment(){
		Assert.assertNull(doc.getFragment(5));
	}
	
	@Test
	public void testGetBow(){
		List<String> expected = Arrays.asList(	new String[] {		
			"the",			"mars",				"volt",
			"band",			"estadounidens",	"rock",
			"progres",		"provenient",		"pas",
			"tex",			"fund",				"2001",
			"guitarr",		"omar",				"rodriguez",
			"lopez",		"cedric",			"vocal",
			"zaval",		"bixl",				"vari",
			"incorpor",		"inclu",			"influenci",
			"jazz",			"krautrock",		"hard",
			"fusion",		"latinoamerican",	"music",
			"llam",			"dentr",			"2008",
			"rolling",		"mejor",			"prog",
			"ston",			
		});		
		
		List<String> real =  doc.get(RepresentationType.BOW);
		Assert.assertTrue(expected.containsAll(real) && real.containsAll(expected));
	}
	
	@Test
	public void testGetFragmentBow(){
		List<String> expected = Arrays.asList(	new String[] {		
			"mejor",	"prog",	"2008",
			"rolling",	"band",	"rock",
			"ston",		"llam",
		});
		List<String> real = doc.getFragment(2).get(RepresentationType.BOW);
		Assert.assertTrue(expected.containsAll(real) && real.containsAll(expected));	
	}
	
	@Test
	public void testGetCng(){
		List<String> expected = Arrays.asList(	new String[] {		
			"the",	"pas",	"tex",	"e m",	" ma",	
			"he ",	"rs ",	"s v",	"mar",	"ars",
			"olt",	"lta",	" vo",	"vol",	"fue",
			" fu",	"a f",	"ta ",	"una",	" un",
			"e u",	"ue ",	"ban",	" ba",	"a b",
			"na ",	"a e",	"da ",	"nda",	"and",	
			"ado",	"dou",	"oun",	"uni",	" es",
			"est",	"sta",	"tad",	"nse",	"se ",	
			"e d",	" de",	"nid",	"ide",	"den",
			"ens",	"ck ",	"ock",	" pr",	"k p",
			"e r",	"de ",	"roc",	" ro",	"esi",
			"res",	"ivo",	"siv",	"rog",	"pro",
			"gre",	"ogr",	"nie",	"ien",	"ven",
			"eni",	"rov",	"ove",	"vo ",	"o p",	
			"l p",	" pa",	" el",	"el ",	"te ",
			"e e",	"ent",	"nte",	"as ",	"xas",
			"exa",	" te",	", t",	"o, ",	"so,",
			"aso",	" en",	"ada",	"dad",	"und",
			"fun",	"y f",	" y ",	"s y",	" po",
			"a p",	"or ",	"por",	"l g",	"r e",
			"gui",	" gu",	"n 2",	"en ",	"200",
			" 20",	"01.",	"001",	". f",	"1. ",
			" om",	"oma",	"ar ",	"r r",	"rod",
			"odr",	"dri",	"rig",	"uit",	"ita",
			"tar",	"arr",	"rri",	"ris",	"ist",
			"a o",	"y e",	"z y",	"ez ",	"pez",
			"cal",	"oca",	"voc",	"l v",	"ez-",
			"uez",	"gue",	"igu",	"ope",	"lop",
			"-lo",	"z-l",	"bix",	"ixl",	"c b",
			" bi",	"er-",	"r-z",	"xle",	"ler",
			"a c",	" ce",	"ali",	"lis",	"ric",
			"ic ",	"ced",	"edr",	"nco",	"inc",
			"orp",	"cor",	"la ",	" la",	" in",
			"a i",	"la,",	"ala",	", l",	"a, ",
			"zav",	"-za",	"val",	"ava",	"flu",
			"lue",	"uen",	"enc",	"ias",	"s i",
			"inf",	"nfl",	" va",	"var",	"ari",
			"ria",	"rpo",	"ora",	"ra ",	"a v",
			"rau",	"kra",	" kr",	", k",	"vo,",
			"o r",	"do ",	"ndo",	"end",	"yen",
			"uye",	"luy",	"clu",	"ncl",	"cia",
			"nci",	"ion",	"on,",	"usi",	"sio",
			"z f",	"fus",	"azz",	"zz ",	" ja",
			"jaz",	"k, ",	", j",	"tro",	"ck,",
			"aut",	"utr",	"noa",	"ino",	"tin",
			"ati",	"eri",	"mer",	"ame",	"oam",
			"ntr",	"a d",	"ana",	"can",	" su",
			"e s",	"o d",	"ro ",	" ha",	"har",
			"n, ",	", h",	"d r",	", y",	"ard",
			"rd ",	"mus",	"sic",	"y m",	" mu",
			"a l",	"lat",	"ica",	"ca ",	" \"l",
			"s \"",	"a m",	"\"la",	"mej",	" me",	
			"jor",	"ejo",	"og-",	"r b",	"-ro",
			"g-r",	"k\" ",	"ck\"",	"del",	"\" d",	
			"su ",	"u m",	"ca.",	"a. ",	"uer",
			"ero",	"ron",	"on ",	"n l",	" ll",
			"lla",	"lam",	"ama",	"mad",	"dos",
			"os ",	"lli",	"lin",	"rol",	"oll",
			"08 ",	"8 p",	"l 2",	"008",	"one",
			"ne.",	"sto",	"ton",	"g s",	" st",
			"ing",	"ng "		
		});		

		List<String> real = doc.get(RepresentationType.CNG);
		Assert.assertTrue(expected.containsAll(real) && real.containsAll(expected));
		
	}
	
	@Test
	public void testGetWng(){
		List<String> expected = Arrays.asList(	new String[] {
			"por rolling stone",			"rolling stone .",
			"del 2008 por",					"2008 por rolling",
			"mejor banda prog",				"banda prog -", 
			"\" la mejor", 					"la mejor banda",
			"rock \" del",					"\" del 2008", 
			"prog - rock",	 				"- rock \"", 
			"su musica .",					"de su musica", 	
			"dentro de su", 				"latinoamericana dentro de", 
			"llamados \" la",		 		"fueron llamados \"", 	
			". fueron llamados",			"musica . fueron", 	
			"hard rock ,", 					", hard rock", 
			"fusion , hard", 				"jazz fusion ,",
			"musica latinoamericana dentro",	"y musica latinoamericana", 
			", y musica", "rock , y", 		"de rock progresivo", 
			"estadounidense de rock",		"banda estadounidense de", 
			"una banda estadounidense", 	"fue una banda", 
			"volta fue una", 				"mars volta fue", 
			"the mars volta", 				"texas y fundada", 
			", texas y", 					"paso , texas", 
			"el paso ,", 					"de el paso", 
			"proveniente de el",			"progresivo proveniente de", 
			"rock progresivo proveniente", 	"la banda incorpora", 
			", la banda", 					"incorpora varias influencias", 
			"banda incorpora varias",		"bixler - zavala", 
			"cedric bixler -", 				"zavala , la", 
			"- zavala ,", 					", krautrock ,", 
			"progresivo , krautrock", 		", jazz fusion", 
			"krautrock , jazz",				"influencias incluyendo rock", 
			"varias influencias incluyendo", "rock progresivo ,", 
			"incluyendo rock progresivo", 	". fundada por", 
			"fundada por el", 				"por el guitarrista", 
			"el guitarrista omar", 			"y fundada en", 
			"fundada en 2001", 				"en 2001 .", 
			"2001 . fundada", 				"lopez y el", 
			"y el vocalista", 				"el vocalista cedric", 
			"vocalista cedric bixler", 		"guitarrista omar rodriguez", 
			"omar rodriguez -",				"rodriguez - lopez", 
			"- lopez y"	
		});
		
		
			
//		
//		 El Paso, Texas y fundada en "
//		+ "2001. Fundada por el guitarrista Omar Rodríguez-López y el "
//		+ "vocalista Cedric Bixler-Zavala, la banda incorpora varias "
//		+ "influencias incluyendo rock progresivo, Krautrock, jazz fusion, "
//		+ "hard rock, y música latinoamericana dentro de su música. Fueron "
//		+ "llamados \"La mejor banda prog-rock\" del 2008 por Rolling Stone.";
		
		
//		System.out.println("XXX" +doc.get(RepresentationType.WNG));
		List<String> real = doc.get(RepresentationType.WNG);	
		Assert.assertTrue(expected.containsAll(real) && real.containsAll(expected));
		
		}
	
//	@Test
//	public void testGetRepresentation(){
//				
//		List<String> expected = Arrays.asList(
//			new String[] {
//				"coleopter", 	"orden", 	"insect", 
//				"unas", 		"375.000", 	"especi", 
//				"descrit", 		"tant", 	"plant", 
//				"vascular",		"hong", 	"66", 
//				"vec", 			"mamifer"
//		});
//				
//		for (String t : doc.getRepresentation()){
//			System.out.println(t);
//		}
//		
//		Assert.assertEquals(expected, bow.getRepresentation());				
//	}
//	
//	@Test
//	public void testGetWeightedRepresentation(){		
//		Map<String, Double> expected= 
//				new HashMap<String, Double>(){/***/
//			private static final long serialVersionUID = 2552098132934864032L;
//		{
//			put("hong", 1.0);
//			put("plant", 1.0);
//			put("orden", 1.0);
//			put("66", 1.0);
//			put("vascular", 1.0);
//			put("tant", 1.0);
//			put("coleopter", 2.0);
//			put("mamifer", 1.0);
//			put("especi", 3.0);
//			put("descrit", 1.0);
//			put("unas", 1.0);
//			put("insect", 1.0);
//			put("375.000", 1.0);
//			put("vec", 1.0);
//		}};		
//		
//		Map<String, Double> map = bow.getWeightedRepresentation();
//		for (String k : map.keySet()){
//			System.out.println(k + " : " + map.get(k));
//		}
//		Assert.assertEquals(expected, bow.getWeightedRepresentation());
//
//
//		
//	}
//	
//	@Test
//	public void testGetNormalizedNormalizedRepresentation(){
//		//TODO whether the test should be term-wise to properly
//		//compare the doubles
//		Map<String, Double> expected= 
//				new HashMap<String, Double>(){/***/
//			private static final long serialVersionUID = 2552098132934864032L;
//		{
//			put("hong", 0.058823529411764705);
//			put("plant", 0.058823529411764705);
//			put("orden", 0.058823529411764705);
//			put("66", 0.058823529411764705);
//			put("vascular", 0.058823529411764705);
//			put("tant", 0.058823529411764705);
//			put("coleopter", 0.11764705882352941);
//			put("mamifer", 0.058823529411764705);
//			put("especi", 0.17647058823529413);
//			put("descrit", 0.058823529411764705);
//			put("unas", 0.058823529411764705);
//			put("insect", 0.058823529411764705);
//			put("375.000", 0.058823529411764705);
//			put("vec", 0.058823529411764705);			
//
//		}};		
//		
//		Map<String, Double> map = bow.getNormalizedRepresentation();
//		for (String k : map.keySet()){
//			System.out.println(k + " : " + map.get(k));
//		}
//		Assert.assertEquals(expected, bow.getNormalizedRepresentation());
//	}

}
