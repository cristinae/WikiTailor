package cat.lump.ie.textprocessing.word;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cat.lump.aq.basics.check.CHK;
import cat.lump.ie.textprocessing.Decomposition;
import cat.lump.ie.textprocessing.Span;

import com.ibm.icu.text.BreakIterator;

/**A class based on aitools WordDEcompositionICU4J class.
 * It is basically a tokenizer.
 * The language is already considered.
 * @author alberto
 *
 */
public class WordDecompositionICU4J implements Decomposition{
		
	private BreakIterator bit;

	public WordDecompositionICU4J(Locale language){
		CHK.CHECK_NOT_NULL(language);
		bit = BreakIterator.getWordInstance(language);
	}

	@Override
	public List<String> getStrings(String text) {
		CHK.CHECK_NOT_NULL(text);
		String word;
		List<String> words = new LinkedList<String>();
		
		bit.setText(text);
		int start = bit.first();
		int end = bit.next();
		
		while (end != BreakIterator.DONE) {
			word = text.substring(start, end);
			
			if (! word.trim().equals("")) {
			//if (word.length() >= 1 || Character.isLetterOrDigit(word.charAt(0))) {
				words.add(word);
			}
			start = bit.current();
			end = bit.next();
		}
		
		return words;
	}

	@Override
	public List<Span> getSpans(String text) {
		CHK.CHECK_NOT_NULL(text);
		String word;
		List<Span> spans = new LinkedList<Span>();
		bit.setText(text);
		
		int start = bit.first();
		int end = bit.next();
		
		while (end != BreakIterator.DONE) {
			word = text.substring(start, end);
			if (! word.trim().equals("")){
//			if (word.length() >= 1 || Character.isLetterOrDigit(word.charAt(0))) {
				spans.add(new Span(start, end) );
			}
			start = bit.current();
			end = bit.next();
		}
		
		return spans;
	}

//  @Override
//  public Map<String, Integer> getFreqs(String text) {
//    // TODO Auto-generated method stub
//    return null;
//  }
	
	
	
}
