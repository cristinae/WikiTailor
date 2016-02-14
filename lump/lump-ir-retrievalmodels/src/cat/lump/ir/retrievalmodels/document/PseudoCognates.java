package cat.lump.ir.retrievalmodels.document;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cat.lump.aq.basics.check.CHK;

/**
 * A document representation based in Simard cognateness model.</br> A token t
 * is candidate of be a cognate of another if:
 * <ol>
 * <li>t is entirely composed of letters and digits, but contains at least one
 * digit</li>
 * <li>t is exclusively composed of letters, and is at least four letters long</li>
 * <li>t is a single punctuation character</li>
 * </ol>
 * <p>
 * Reference: Michel Simard, George F. Foster and Piere Isabelle. Using Cognates
 * to Align Sentences in Bilingual Corpora
 * </p>
 * The cognates are preprocessed as follows:
 * <ul>
 * <li>casefolding</li>
 * <li>if it's a cognate exclusively composed of letters, then it's truncated to
 * 4 letters</li>
 * </ul>
 * 
 * WARN: Code copied from cat.talp.lump.aq.text.SimardCognateness (project lump)
 * 
 * @author jboldoba
 * 
 */
public class PseudoCognates extends Representation {

	private static final long serialVersionUID = -1624164574942983738L;
	private final static int INVALID = 0;
	private final static int LETTERS_DIGITS = 1;
	private final static int JUST_LETTERS = 2;
	private final static int PUNCTUATION = 3;

	protected PseudoCognates(Dictionary dictionary, Locale language) {
		super(dictionary, language);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setText(String text) {
		CHK.CHECK_NOT_NULL(text);
		TERMS = new HashMap<Integer, Double>();
		int id;
		List<String> tokens = getTokens(text);
		
		int localCounter = 0;
		for (String token : tokens) {
			token = preprocess(token);
			if (!token.isEmpty()) {
				GLOBAL_COUNTER++;
				localCounter++;

				id = dictionary.addString(token);

				if (TERMS.containsKey(id)) {
					TERMS.put(id, TERMS.get(id) + 1);
				} else {
					TERMS.put(id, 1.0);
				}
			}
		}
		if (localCounter == 0) {// Text without cognates
			GLOBAL_COUNTER++;
			String dummyToken = getDummyWord();
			id = dictionary.addString(dummyToken);

			if (TERMS.containsKey(id)) {
				TERMS.put(id, TERMS.get(id) + 1);
			} else {
				TERMS.put(id, 1.0);
			}
		}
	}

	@Override
	protected String preprocess(String text) {
		if (kindIsPunct(getKind(text)) || kindIsAlphanum(getKind(text)))
			return text.toLowerCase();

		if (kindIsLetters(getKind(text)))
			return text.toLowerCase().substring(0, 4);
		return "";
	}

	/**
	 * Determines the kind of the token at hand:
	 * 
	 * 0) invalid token 1) combination of letters and digits 2) only letters,
	 * with length >= 4 3) punctuation mark, with length = 1
	 * 
	 * 
	 * Note that if token has not been previously defined, it is "" and its kind
	 * is invalid.
	 * 
	 * @param t
	 * @return
	 */
	private static int getKind(String token) {
		if (token.length() == 1 && isPunctuation(token.charAt(0)))
			return PUNCTUATION;

		if (stringHasDigits(token))
			return LETTERS_DIGITS;

		if (stringJustLetters(token) && token.length() >= 4)
			return JUST_LETTERS;
		return INVALID;
	}

	private static boolean kindIsPunct(int kind) {
		if (kind == 3)
			return true;
		return false;
	}

	private static boolean kindIsAlphanum(int kind) {
		if (kind == 1)
			return true;
		return false;
	}

	private static boolean kindIsLetters(int kind) {
		if (kind == 2)
			return true;
		return false;
	}

	/**
	 * Checks if character c is a punctuation mark
	 * 
	 * TODO confirm whether isLetterOrDigit is enough to determine it.
	 * 
	 * @param c
	 * @return
	 */
	private static boolean isPunctuation(char c) {
		if (Character.isLetterOrDigit(c))
			return false;
		return true;
	}

	private static boolean stringHasDigits(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i)) == true)
				return true;
		}
		return false;
	}

	private static boolean stringJustLetters(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLetter(s.charAt(i)) == false)
				return false;
		}
		return true;
	}
}
