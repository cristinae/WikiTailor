package cat.lump.ie.textprocessing.ngram;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cat.lump.aq.basics.check.CHK;
import cat.lump.ie.textprocessing.Decomposition;
import cat.lump.ie.textprocessing.Span;

import com.ibm.icu.text.Transliterator;

public class CharacterNgrams  implements Decomposition{
	
	/**n-gram level */
	private int n;

	/**instance to remove diacritics*/
	private final Transliterator trans = 
		Transliterator.getInstance("NFD; [:NonspacingMark:] Remove; NFC");
	
	
	/**Flag to admit texts shorter than n	 */
	protected boolean admitShorterNgrams;

	
	public CharacterNgrams(int n) {
		this(n, false);
					
	}
	
	public CharacterNgrams(int n, Boolean admitShorterNgrams) {		
		this.admitShorterNgrams = admitShorterNgrams;
		CHK.CHECK(n > 0, "A value higher than 0 is expected");
		this.n = n;
	}
		
	
//	/* (non-Javadoc)
//	 * @see cat.talp.lump.aq.text.grams.Ngrams#getNgramStrings(java.lang.String)
//	 */
//	@Override
//	public List<String> getNgramStrings(String text) {
//		if (n<1){
//			System.err.printf("A value higher than 0 is expected");
//			System.exit(1);
//		}
//		preprocess(text);
//		text = strTxt.getString();
//		
//		List<String> ngr = new ArrayList<String>();
//		 
//		
//		if (admitShorterNgrams && text.length() < n){
//			//if (text.length() < n)
//			ngr.add(strTxt.getString());
//			return ngr;
//		}
//		
//		for (int i = 0; i< (text.length()- n+1); i++)
//				ngr.add(text.substring(i, i+n));			
//				
//		return ngr;	
//	}
	
	@Override
	public List<String> getStrings(String text) {	
		List<String> ngr = new ArrayList<String>();
		
		text = trans.transliterate(text);		 
		
		if (admitShorterNgrams && text.length() < n){
			ngr.add(text);
			return ngr;
		}
		for (int i = 0; i< (text.length()- n+1); i++){
			ngr.add(text.substring(i, i+n));		
		}
				
		return ngr;	
	}	

	public Map<String, Integer> getFreqs(String text) {
	  Map<String, Integer> ngr = new LinkedHashMap<String, Integer>();
	  for (String n : getStrings(text)) {
	    if (! ngr.containsKey(n)) {
	      ngr.put(n, 0);
	    }
	    ngr.put(n, ngr.get(n)+1);
	  }
	  return ngr;
	}
	
	
	
	@Override
	public List<Span> getSpans(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main (String[] args) {
		Decomposition cNgrams = new CharacterNgrams(3);
		String t = "Amsterdam, capital of the Netherlands";
		List<String> ngrams = cNgrams.getStrings(t);
		System.out.println(t);
		for (String g : ngrams)
			System.out.println(g);
	}
	
}