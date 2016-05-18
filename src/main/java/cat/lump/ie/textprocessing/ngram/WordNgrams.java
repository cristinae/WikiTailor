package cat.lump.ie.textprocessing.ngram;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import cat.lump.aq.basics.check.CHK;
import cat.lump.ie.textprocessing.Decomposition;
import cat.lump.ie.textprocessing.Span;
import cat.lump.ie.textprocessing.word.WordDecompositionICU4J;

import com.ibm.icu.text.Transliterator;

/**This class allows for generating word-level n-grams from a text. 
 *	TODO add the option to have a different offset 
 * 
 * @author albarron
 */
public  class WordNgrams implements Decomposition{

	/**Desired Level of the n-gram	 */
	private int n;
	
	private Decomposition decomposition;
	
	private final Transliterator trans = 
			Transliterator.getInstance("NFD; [:NonspacingMark:] Remove; NFC");
	
	/**
	 * @param n
	 * @param decomposition
	 */
	public WordNgrams (int n, Locale language) {		
		CHK.CHECK(n > 0, "A value higher than 0 is expected");
		this.n = n;
		decomposition = new WordDecompositionICU4J(language);
		
	}	
		
//	/**Performs preprocessing for n-grams generation. The preprocessing 
//	 * includes trimming, case folding and diacritics removal.
//	 * @param text
//	 */
//	protected void preprocess(String text){
//		strTxt.setString(text.trim());
//		strTxt.toLowerCase();
//		strTxt.removeDiacritics();	
//	}
	
	
	@Override
	public List<Span> getSpans(String text) {
		CHK.CHECK_NOT_NULL(text);
		
		text = trans.transliterate(text);
		List<Span> decomposed = decomposition.getSpans(text);
		
		List<Span> result = new ArrayList<Span>();
		if (decomposed.size() < n) {
			return result;
		}
		
		ListIterator<Span> startIt = decomposed.listIterator();
		ListIterator<Span> endIt = decomposed.listIterator(n -1);
		
		while(endIt.hasNext()){
			result.add(new Span(startIt.next().getStart(), endIt.next().getEnd()));
//			for (int i = 0; i < )
//			endIt.next();
//			startIt.next();
		}
		
		return result;
	}
	
	@Override
	public List<String> getStrings(String text) {
		CHK.CHECK_NOT_NULL(text);
		text = trans.transliterate(text);
		
		List<Span> decomposed = decomposition.getSpans(text);
		List<String> result = new LinkedList<String>();
		StringBuffer sb = new StringBuffer();
		Span span;
		
		if (decomposed.size() < n) {
			return result;
		}
		
		for (int i=0; i<=decomposed.size() - n; i++){
			for (int j=i; j <i+n; j++){
				span = decomposed.get(j);
				String wordNgram = text.substring(span.getStart(), span.getEnd());
				sb.append(wordNgram)
				  .append(" ");
			}
			result.add(sb.toString().trim());
			sb.delete(0, sb.length());
		}		
		return result;
	}
	
	
//	public List<String> getNgramStrings(String[] t){
//		StringBuffer sb = new StringBuffer();
//		List<String> ngr = new ArrayList<String>();
//		
//		n = Math.min(n, t.length);
//		int aux_index = t[0].length();
//			
//		//get the first n-gram
//		for (int i = 0 ; i < n ; i++){		
//			sb.append(t[i]).append(" ");			
//		}
//		sb.deleteCharAt(sb.length()-1);
//		ngr.add(sb.toString());
//		sb.delete(0, aux_index+1);
//		
//		//get the next n-grams
//		for (int i = n-(n-1); i < (t.length -n +1); i++){
//			aux_index = t[i].length();
//			sb.append(" ").append(t[i+(n-1)]);
//			ngr.add(sb.toString().trim());
//			sb.delete(0, aux_index +1);
//		}
//		return ngr;
//	}
	
	
		
//	public Map<String, Double> getNgramStringsFreq(String t){
//		Map<String, Double> map = new HashMap<String, Double>();
//		for (String cNgram : getNgramStrings(t)){
//			if (!map.containsKey(cNgram))
//				map.put(cNgram, 0.0);
//			map.put(cNgram, map.get(cNgram) + 1);
//		}		
//		return map;
//	}		
	
	public static void main (String[] args) {

		String input =  "ésta es sólo una prueba; la cual ha comenzado a ser " +
							"más interesante (sólo un poco).";
		Decomposition ng = new WordNgrams(3, new Locale("es"));	
		
		
		List<String> res = ng.getStrings(input);

		System.out.println(input);
		for (int i= 0; i < res.size(); i++)
			System.out.println(res.get(i) + "\t");// + resInt[i]);

		List<Span> spans = ng.getSpans(input);
		
		for (Span span : spans){
			System.out.println(span.getStart() + " " + span.getEnd());
		}
		
	}

	

	
	
}
