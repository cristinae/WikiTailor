package cat.lump.aq.textextraction.wikipedia;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

@Deprecated
public class WikiProperties {
	
	
	/**
	 * It was only used to load the LanguageConstants. If some other
	 * class requires this process, it could be de-deprecated and 
	 * moved into the right package. (after updating to 
	 * getClass().getResourceAsStream())
	 * <br/>
	 * The simple process is now in the LanguageConstants constructor
	 * @param configFile
	 * @return
	 */
	@Deprecated
	public static Properties getProperties(String configFile){
		Properties defaultProps = new Properties();
		//FileInputStream in;
		try {			
			
			InputStreamReader kk = 
					new InputStreamReader(
							new FileInputStream(configFile), 
							"UTF8");
			defaultProps.load(kk);
			kk.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return defaultProps;
	}	
}
