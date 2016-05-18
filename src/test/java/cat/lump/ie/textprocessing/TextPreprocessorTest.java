package cat.lump.ie.textprocessing;


import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TextPreprocessorTest {

	private TextPreprocessor prepro;
	
	private final String text = 
			"El golpe de Estado en Tailandia de 2014 tuvo lugar el 22 de mayo "
			+ "de 2014, cuando las Reales Fuerzas Armadas de Tailandia, "
			+ "lideradas por el general Prayuth Chan-ocha, comandante del Real "
			+ "Ejército Tailandés, puso en marcha la rebelión contra el gobierno "
			+ "interino del primer ministro Niwatthamrong Boonsongpaisan, después "
			+ "de seis meses de revueltas y crisis política.";
	
	@Before
	public void setUp() throws Exception {
		prepro = new TextPreprocessor(new Locale("es"));
		prepro.setString(text);
	}

	@After
	public void tearDown() throws Exception {		
	}

	@Test	
	public void testToLowerCase() {
		prepro.toLowerCase();
		String expected = "el golpe de estado en tailandia de 2014 tuvo lugar "
				+ "el 22 de mayo de 2014 , cuando las reales fuerzas armadas de "
				+ "tailandia , lideradas por el general prayuth chan - ocha , "
				+ "comandante del real ejército tailandés , puso en marcha la "
				+ "rebelión contra el gobierno interino del primer ministro "
				+ "niwatthamrong boonsongpaisan , después de seis meses de "
				+ "revueltas y crisis política .";
		
		Assert.assertEquals(expected, prepro.getString());		
	}

	@Test
	public void testRemovePunctuation() {
		prepro.removePunctuation();
		String expected = "El golpe de Estado en Tailandia de 2014 tuvo lugar "
				+ "el 22 de mayo de 2014 cuando las Reales Fuerzas Armadas de "
				+ "Tailandia lideradas por el general Prayuth Chan ocha "
				+ "comandante del Real Ejército Tailandés puso en marcha la "
				+ "rebelión contra el gobierno interino del primer ministro "
				+ "Niwatthamrong Boonsongpaisan después de seis meses de "
				+ "revueltas y crisis política";
		
		Assert.assertEquals(expected, prepro.getString());
	}

	@Test
	public void testRemoveStopwords() 
	{
		prepro.removeStopwords();
		String expected = "golpe Tailandia 2014 lugar 22 mayo 2014 Reales "
				+ "Fuerzas Armadas Tailandia lideradas general Prayuth Chan "
				+ "ocha comandante Real Ejército Tailandés puso marcha "
				+ "rebelión gobierno interino primer ministro Niwatthamrong "
				+ "Boonsongpaisan después seis meses revueltas crisis política";			
	
//		String x = "El golp de Estad en Tailandi de 2014 tuv lug el "
//				+ "22 de may de 2014 , cuand las Real Fuerz Armad de Tailandi , "
//				+ "lider por el general Prayuth Chan - ocha , comand del Real Ejercit Tailandes , pus en march la rebelion contr el gobiern interin del prim ministr Niwatthamrong Boonsongpais , despues de seis mes de revuelt y crisis polit .";
	
		System.out.println("XX" + prepro.getString());

		
		Assert.assertEquals(expected, prepro.getString());
	}

	@Test
	public void testRemoveNonAlphaNumeric()
	{
		prepro.removeNonAlphaNumeric(4);
		String expected = "golpe Estado Tailandia 2014 tuvo lugar mayo 2014 "
				+ "cuando Reales Fuerzas Armadas Tailandia lideradas general "
				+ "Prayuth Chan ocha comandante Real puso marcha contra "
				+ "gobierno interino primer ministro Niwatthamrong "
				+ "Boonsongpaisan seis meses revueltas crisis";			

		Assert.assertEquals(expected, prepro.getString());
	}
	
	@Test
	public void testRemoveNonAlphabetic() 
	{
		prepro.removeNonAlphabetic(4);
		String expected = "golpe Estado Tailandia tuvo lugar mayo cuando "
				+ "Reales Fuerzas Armadas Tailandia lideradas general "
				+ "Prayuth Chan ocha comandante Real puso marcha contra "
				+ "gobierno interino primer ministro Niwatthamrong "
				+ "Boonsongpaisan seis meses revueltas crisis";

		//System.out.println(prepro.getString());
		Assert.assertEquals(expected, prepro.getString());
	}

	@Test
	public void testStem() 
	{
		prepro.stem();
		String expected = "El golp de Estad en Tailandi de 2014 tuv lug el "
				+ "22 de may de 2014 , cuand las Real Fuerz Armad de Tailandi , "
				+ "lider por el general Prayuth Chan - ocha , comand del Real "
				+ "Ejercit Tailandes , pus en march la rebelion contr el "
				+ "gobiern interin del prim ministr Niwatthamrong "
				+ "Boonsongpais , despues de seis mes de revuelt y crisis polit .";

		Assert.assertEquals(expected, prepro.getString());
	}
	
	@Test
	public void testAll()
	{
		prepro.toLowerCase();
		prepro.removePunctuation();
		prepro.removeStopwords();
		prepro.removeNonAlphaNumeric(4);
		prepro.stem();
		String expected = "golp tailandi 2014 lug may 2014 real fuerz armad "
				+ "tailandi lider general prayuth chan ocha comand real pus "
				+ "march gobiern interin prim ministr niwatthamrong boonsongpais "
				+ "seis mes revuelt crisis";
		
		Assert.assertEquals(expected, prepro.getString());
		
	}
	
	
	@Test
	public void testGetOriginalString() {
		Assert.assertEquals(text, prepro.getOriginalString());
	}

}
