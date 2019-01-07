package cat.lump.aq.wikilink.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import cat.lump.aq.basics.check.CHK;

/**
 * The years for which we have dumps available
 * @author albarron
 *
 *  TODO implement this with a MySQL connector
 *   
 */
public class Dump {
	
	/**The file with a list of the available dumps */
	//private static final String CONFIG_FILE="cat/lump/aq/wikilink/config/availableDumps.properties";
	private static final String CONFIG_FILE="./configs/availableDumps.properties";
	
	/**The locale for the given dump */
	private Locale locale;
	
	/**The year for the given dump */
	private int year;
	
	/**
	 * Locale and year are set. Whether a dump is available for this compilation
	 * is tested.
	 * 
	 * @param locale
	 * @param year
	 */
	public Dump(Locale locale, int year) {
		CHK.CHECK(checkLanYear(locale,  year), 
				String.format("No dump is available for language %s and year %d", 
						locale.getLanguage(), year));
		this.locale = locale;
		this.year = year;
	}
	
	/* GETTERS */
	
	public Locale getLanguage(){
		return locale;
	}
	
	public int getYear(){
		return year;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return locale.getLanguage() + "_" + year;
	}
	
	/**
	 * Checks whether a language dump exist for a given year. The availability
	 * is checked on a configuration file called availableDumps.properties.
	 * 
	 * @param locale
	 * @param year
	 * @return true if the dump is available
	 */
	private Boolean checkLanYear(Locale locale, int year) {
		boolean exists = false;
		List<String> years;
		Properties p = new Properties();
				
		Class<Dump> c = Dump.class;
		try {
			//Configuration with config file within the jar
			//	File configPath = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
//			// String configFile = configPath.getParent().toString().concat(CONFIG_FILE);
			// //InputStreamReader isr = new InputStreamReader(new FileInputStream(CONFIG_FILE), Charset.forName("UTF-8"));
			// InputStreamReader isr = new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(CONFIG_FILE));
			// p.load(isr);
			// isr.close();
			
			//Configuration with config file outside the jar
			p.load(new FileInputStream(CONFIG_FILE));						

		} catch (IOException e) { 
			e.printStackTrace(); 
		//} catch (URISyntaxException e) {
	    //		e.printStackTrace();
		}
		
		
		if (p.containsKey(locale.getLanguage())){
			years = Arrays.asList(
					p.getProperty(locale.getLanguage()).trim().split(","));			
			if (years.contains(String.valueOf(year))){
				exists = true;
			}
		}
		return exists;
	}	

}
