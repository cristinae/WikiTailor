package cat.lump.aq.textextraction.wikipedia.prepro;

import java.util.List;
import java.util.Locale;

import cat.lump.aq.basics.check.CHK;
import cat.lump.ie.textprocessing.TextPreprocessor;


/**
 * A class that extracts terms according to different definitions.
 * Only one definition is given now. The distinction among languages should 
 * be done here.
 * 
 * @author cristina
 * @since May 28, 2015
 */

public class TermExtractor {

	/** Language */
	private final Locale locale;
	
	/** Tool used to work with strings (tokenizer, stemmer, stopwords...) */
	private TextPreprocessor prepro;

	//private static LumpLogger logger = 
	//		new LumpLogger (TermExtractor.class.getSimpleName());
	

	// Constructors
	/**
	 * Creates a new TermExtractor
	 * 
	 * @param language
	 */
	public TermExtractor(Locale language) {
		CHK.CHECK_NOT_NULL(language);
		locale = language;
		prepro = new TextPreprocessor(locale);
	}

	
	/**
	 * Transforms the content of a String into a set of terms.
	 * A term is obtained as follows:
	 * <ol>
	 * <li>The content is converted to lower case</li>
	 * <li>The tokens which are composed only by punctuation and symbols are
	 * removed.</li>
	 * <li>The tokens which appears in the stopwords list are removed</li>
	 * <li>The remaining tokens are stemmed with a Snowball/Lucene Stemmer</li>
	 * <li>Diacritics are removed (Now it is needed for French and Romanian only \\TODO)</li>
	 * <li>It selects only the tokens composed by letters and with a minimum
	 * size (usually 4 characters, but always 3 for Arabic).</li>
	 * </ol>
	 * 
	 * @param text
	 *            The String from which terms are extracted.
	 * @param minimumSize
	 * 			  integer with the minimum size to keep a token as a term
	 * @return The list of tokens resulting of the treatment explained above.
	 */
	public List<String> getTerms(String text, int minimumSize) {
		prepro.setStringTokens(text);
		prepro.toLowerCase();
		prepro.removePunctuation();
		prepro.removeStopwords();
		prepro.stem();
		//Most roots are triliteral in Arabic and diacritics are handled by the stemmer 
		if (locale.toString().equalsIgnoreCase("ar")) {	
			prepro.removeEngStopwords();
			prepro.removeNonAlphabetic(3);
		} else if (locale.toString().equalsIgnoreCase("el")  || locale.toString().equalsIgnoreCase("bg")
				|| locale.toString().equalsIgnoreCase("ru") ) {	
			prepro.removeEngStopwords();
			prepro.removeDiacritics();
			prepro.removeNonAlphabetic(minimumSize);
		} else if (locale.toString().equalsIgnoreCase("gu")) {	
			prepro.removeEngStopwords();
			prepro.removeDiacritics();  //TODO see how to deal with Gujarati here
			prepro.removeNonAlphabetic(minimumSize);
		} else {
			prepro.removeDiacritics();
			prepro.removeNonAlphabetic(minimumSize);
		}

		// "Tokenizer"
		List<String> tokens = prepro.getTokens();
		return tokens;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
