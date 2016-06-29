package cat.lump.ir.lucene.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**A collection of static methods that contain the available 
 * languages for both ESA index construction and ESA-based 
 * text characterization
 * 
 * 
 * TODO probably move to en_GB, en_US, en_CA, es_ES, es_MX
 * 
 * @author albarron
 * @version 0.2
 * @since October 2013
 */
public class LuceneLanguages {
	
	/**Languages that we are able to process (index/query)*/
	private static List<String> languages;
		
	/**Languages already indexed that we are able to process*/
	private static List<String> indexedLanguages;
	
//	public static void main(String[] args){
//		System.out.println(LuceneLanguages.langToString());
//	}

	static {
		Properties p = new Properties();
		
		//String propFile = null;
		//AQUI VOY
		InputStream in = LuceneLanguages.class.getResourceAsStream("lucene.properties");
		try {
			p.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		InputStreamReader is = new InputStreamReader(in);
//		BufferedReader br = new BufferedReader(is);
//		String read;
//		
//		if (System.getProperty("user.name").equals("alberto"))
//			propFile = "/home/alberto/workspace/basset/config/lucene.properties";
//		else 
//			//if (System.getProperty("user.name").equals("albarron"))
//			propFile = "/home/albarron/workspace/basset/config/lucene.properties";
//		try {
//			p.load(new FileInputStream(propFile));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
      
      languages=Arrays.asList(p.getProperty("languages").split(","));
      indexedLanguages = Arrays.asList(p.getProperty("indexedLanguages").split(","));
	}
	
    /**
     * @return An array with comma-separated available languages
     */
    public static String langToString(){    	
    	return StringUtils.join(languages, ", ");
    }
	
	/**Checks whether we can index a given language.
	 * @param lan
	 * @return true if the language is available
	 */
	public static boolean isAvailable(String lan){
		if (languages.contains(lan))
			return true;
		return false;
	}
	
	/**Checks whether a given languages is indexed and ready to compute 
	 * similarities. 
	 * @param lan
	 * @return true if the language is available
	 */
	public static boolean isIndexAvailable(String lan){
		if (languages.contains(lan) &&
			indexedLanguages.contains(lan))
			return true;
		return false;
	}	
}
