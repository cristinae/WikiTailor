package cat.lump.aq.alignment.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import cat.lump.aq.check.CHK;
import cat.lump.aq.check.F;

/**
 * <p>
 * A simple class that reads the configuration file for acquiring
 * the source and target languages, articles' path, and csv file
 * relating the source and target articles for each pair.
 * </p>
 * 
 * <p>
 * The configuration file must be provided and it has the following 
 * format:
 * </p>
 * <br> ARTICLES_PATH=/path/to/articles/		   
 * <br> SOURCE_LANGUAGE=en
 * <br> TARGET_LANGUAGE=es
 * <br> PAIRS_CSV_FILE=/full/path/to/pairs.csv
 * 
 * <p>
 * Note that no spaces should exist at the beginning nor end of each 
 * line.
 * </p>
 * 
 * @author albarron
 * @version 0.1
 * @since Mar 2014
 */
public class ConfigReader {
	
	private String CONFIG_FILE;
	
	private String PAIRS_CSV_FILE;	
	private String ARTICLES_PATH;
	private String SOURCE_LANGUAGE;
	private String TARGET_LANGUAGE;
	
	private Properties p;
	
	
	public ConfigReader(String configFile){		
		CHK.CHECK_NOT_NULL(configFile);
		F.CAN_READ(new File(configFile), "I cannot read the config file");
				
		CONFIG_FILE = configFile;
		p = new Properties();
		
		try { 
			p.load(new BufferedReader(new FileReader(CONFIG_FILE)));
		} catch (IOException e) { 
			e.printStackTrace(); 
		}		
		
		ARTICLES_PATH   = p.getProperty("ARTICLES_PATH");
		SOURCE_LANGUAGE = p.getProperty("SOURCE_LANGUAGE");
		TARGET_LANGUAGE = p.getProperty("TARGET_LANGUAGE");
		PAIRS_CSV_FILE  = p.getProperty("PAIRS_CSV_FILE");
	}

	public String getArticlesPath(){
		return ARTICLES_PATH;
	}
	
	public String getSourceLanguage(){
		return SOURCE_LANGUAGE;
	}

	public String getTargetLanguage(){
		return TARGET_LANGUAGE;
	}

	public String getPairsCsvFile(){
		return PAIRS_CSV_FILE;
	}	

}