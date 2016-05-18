package cat.lump.ir.retrievalmodels.document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import cat.lump.aq.basics.check.CHK;
import cat.lump.ie.textprocessing.Decomposition;
import cat.lump.ie.textprocessing.word.WordDecompositionICU4J;

/**
 * An abstract class that contains the basic methods to preprocess, set, and get
 * the representation of a given text.
 * 
 * @author albarron
 * @since 4 Feb, 2014
 * @version 0.1
 */
abstract class Representation implements Serializable{

	
	
	/** Auto generated ID for serialization */
	private static final long serialVersionUID = 2150419596333302410L;
	/**
	 * Regular expression to generate a Word to use when a text doesn't meet the
	 * minimum requirements
	 */
	private final String DUMMY_WORD_REGEX = "aaaaaaaaaa";
	/** Instance of the tokenizer */
	
	private transient Decomposition tokenizer;

	/** The terms in the document together with their weights */
	protected Map<Integer, Double> TERMS;

	/** Keeps track of the amount of considered tokens */
	protected int GLOBAL_COUNTER;

	protected Dictionary dictionary;

	public abstract void setText(String text);

	protected abstract String preprocess(String text);

	/**
	 * @param language
	 */
	protected Representation(Dictionary dictionary, Locale language) {
		CHK.CHECK_NOT_NULL(dictionary);
		CHK.CHECK_NOT_NULL(language);
		this.dictionary = dictionary;
		
		tokenizer = new WordDecompositionICU4J(language);
	}
	
	/**
	 * @return A random token
	 */
	protected String getDummyWord() {
		Random r = new Random();
		int suffix = r.nextInt(Integer.MAX_VALUE);
		return String.format(DUMMY_WORD_REGEX, suffix);
	}

	/**
	 * @return Number of terms in the representation
	 */
	public int length() {
		return TERMS.size();
	}

	/**
	 * @param text
	 * @return tokens in the text
	 */
	protected List<String> getTokens(String text) {
		CHK.CHECK_NOT_NULL(text);
		return tokenizer.getStrings(text);
	}

	public List<String> getRepresentation() {
		List<String> rep = new ArrayList<String>();
		for (int termID : TERMS.keySet()) {
			rep.add(dictionary.getString(termID));
		}
		return rep;
	}

	/**
	 * @return map of terms and their weight
	 */
	public Map<String, Double> getWeightedRepresentation() {
		Map<String, Double> rep = new HashMap<String, Double>();
		for (int termID : TERMS.keySet()) {
			rep.put(dictionary.getString(termID), TERMS.get(termID));
		}
		return rep;
	}

	/**
	 * @return map of terms and their normalized weight
	 */
	public Map<String, Double> getNormalizedRepresentation() {
		Map<String, Double> rep = new HashMap<String, Double>();
		for (int termID : TERMS.keySet()) {
			rep.put(dictionary.getString(termID), TERMS.get(termID)
					/ GLOBAL_COUNTER);
		}
		return rep;
	}
}
