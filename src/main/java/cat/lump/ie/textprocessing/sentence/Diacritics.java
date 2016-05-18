package cat.lump.ie.textprocessing.sentence;

import com.ibm.icu.text.Normalizer;

/**This class is indeed a link to icu's normalizer. It casefolds (optional) 
 * a text and removes diacritics. 
 * @author albarron
 * TODO Determine whether we really need this class or we can insert the 
 * ICU's methods in one another.
 *
 */
public class Diacritics {

	static boolean lowercase;
	
	/**
	 * Default invocation. Text is not casefolded.
	 */
	public Diacritics(){
		lowercase = false;
	}
	
	/**At invocation time defining whether the texts are going to 
	 * be casefolded is required.
	 * 
	 * TODO casefolding seems to be the job of some other class.
	 * @param lowercase
	 */
	public Diacritics(Boolean lowercase){
		Diacritics.lowercase = lowercase;
	}
	
	public static String removeDiacritics(String text){
		if (lowercase == true)
			return Normalizer.decompose(text.toLowerCase(),false, 0)
					.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return Normalizer.decompose(text, false, 0)
					.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");			
	}	
	
	public static String removeMarks(String text){
		if (lowercase == true)
			return Normalizer.decompose(text.toLowerCase(),false, 0)
					.replaceAll("\\p{IsM}+", "");
		return Normalizer.decompose(text, false, 0)
					.replaceAll("\\p{IsM}+", "");			
	}	
	
	public String splitDiacritics(){
		//TODO ?
		return null;
	}	

}
