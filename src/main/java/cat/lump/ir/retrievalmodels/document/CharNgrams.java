package cat.lump.ir.retrievalmodels.document;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cat.lump.aq.basics.check.CHK;
import cat.lump.ie.textprocessing.Decomposition;
import cat.lump.ie.textprocessing.ngram.CharacterNgrams;

/**
 * A document representation based on character n-grams. The preprocessing
 * applied to the text includes:
 * <ul>
 * <li>diacritics removal and
 * <li>case folding
 * </ul>
 * 
 * @author albarron
 * @version 0.1
 * @since 4 Mar, 2014
 * @see cat.lump.ir.retrievalmodels.document.Representation
 */
class CharNgrams extends Representation {

	private static final long serialVersionUID = -3036528896630512315L;
	/** Instance of the character n-grams generator */
	private transient Decomposition cng;
	private int N;

	/**
	 * Default invocation with character 3-grams
	 * 
	 * @param dictionary
	 *            text from the document
	 * @param language
	 *            language of the text
	 */
	public CharNgrams(Dictionary dictionary, Locale language) {
		this(dictionary, language, 3);
	}

	// TODO maybe the language is not necessary in this case; add a default?
	/**
	 * @param text
	 *            from the document
	 * @param language
	 *            of the text
	 * @param n
	 *            level of the n-gram
	 */
	public CharNgrams(Dictionary dictionary, Locale language, int n) {
		super(dictionary, language);
		cng = new CharacterNgrams(n,true);
		GLOBAL_COUNTER = 0;
		N = n;
	}

	/**
	 * Sets the input text. The preprocessing operations include tokenization,
	 * stopword removal, and stemming.
	 * 
	 * @param text
	 */
	@Override
	public void setText(String text) {
		CHK.CHECK_NOT_NULL(text);
		TERMS = new HashMap<Integer, Double>();
		int id;
//		System.out.println(String.format("%s %d",text,text.length()));
//		if (text.length() < N) {
//			StringBuffer sb = new StringBuffer();
//			for (int i = 0; i < N; i++) {
//				sb.append(" ");
//			}
//			text = sb.toString();
//		}
		
		List<String> tokens = cng.getStrings(text);
		for (String token : tokens) {
			token = preprocess(token);
			GLOBAL_COUNTER++;

			id = dictionary.addString(token);

			if (TERMS.containsKey(id)) {
				TERMS.put(id, TERMS.get(id) + 1);
			} else {
				TERMS.put(id, 1.0);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cat.lump.ir.retrievalmodels.document.Representation#preprocess(java.lang
	 * .String)
	 */
	@Override
	protected String preprocess(String text) {
		return text.toLowerCase();
	}
}
