package cat.lump.aq.alignment.control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestConfigReader {

	private final String configFile =	"config.tmp";
	
	private Boolean setUpIsDone = false;
	private ConfigReader cr;
	
	@Before
	public void setUp() {
		if (setUpIsDone){
			return;
		}
		//Generating a temporal configuration file
		String contents = 
			"ARTICLES_PATH=/articles/path\n" +		   
			"SOURCE_LANGUAGE=en\n" + 
			"TARGET_LANGUAGE=es\n" +
			"PAIRS_CSV_FILE=/some/path/files.csv\n";
		
		try {
			Writer writer = new BufferedWriter(
								new FileWriter(
									new File(configFile)));
			writer.append(contents);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		cr = new ConfigReader(configFile);
		setUpIsDone = true;
	}
	
	@After
	public void tearDown(){
		new File(configFile).delete();
	}

	@Test
	public void testGetArticlesPath() {
		Assert.assertEquals(
			"/articles/path", 
			cr.getArticlesPath()
		);		
	}

	@Test
	public void testGetSourceLanguage() {
		Assert.assertEquals(
			"en", 
			cr.getSourceLanguage()
		);
	}

	@Test
	public void testGetTargetLanguage() {
		Assert.assertEquals(
			"es", 
			cr.getTargetLanguage()
		);
	}

	@Test
	public void testGetPairsCsvFile() {
		Assert.assertEquals(
			"/some/path/files.csv", 
			cr.getPairsCsvFile()
		);
	}

}
