package cat.lump.ie.textprocessing.sentence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**Regular expression-based punctuation finder. It determines 
 * whether a text contains punctuation marks (or is only 
 * composed of them).
 * 
 * TODO check if some class uses this. Otherwise, discard it.
 * 
 * @author albarron
 *@see PunctuationTest
 */
public class Punctuation {

	
	private static final String CONTAINS_PUNCT_PATTERN = ".*[\\p{Punct}]+.*";
	private static final String ONLY_PUNCT_PATTERN = "[\\p{Punct}]+";
	
	/**
	 * @param str 
	 * @return true if str contains a punctuation mark
	 */
	public static boolean containsPunctuation(String str) {
		Pattern p = Pattern.compile(CONTAINS_PUNCT_PATTERN);
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * @param str
	 * @return true if str is composed of punctuation marks only
	 */
	public static boolean onlyPunctuation(String str){
		Pattern p = Pattern.compile(ONLY_PUNCT_PATTERN);
		Matcher m = p.matcher(str);
		return m.matches();
	}
//	
//	
//	public static void main(String args[]){
////		String str = "esto, es una prueba";
////		String str = "....(){}[:;]";
//		String str = "\"";
//		System.out.println("contains  " + containsPunctuation(str));
//		System.out.println("only  " + onlyPunctuation(str));
//		
//	}
	
}
