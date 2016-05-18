package cat.lump.ir.retrievalmodels.document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cat.lump.aq.basics.check.CHK;
import cat.lump.ie.textprocessing.Decomposition;
import cat.lump.ie.textprocessing.sentence.SentencesOpennlp;

/**
 * A frame to represent a text document and its fragments (in the form of
 * sentences). It is possible accessing the document's overall representations
 * as well as fragment-wise. The representations include
 * <ul>
 * <li>plain text
 * <li>bag of words
 * <li>character n-grams
 * </ul>
 * More to be added: word n-grams, keywords, named entities, ESA.
 * 
 * @author albarron
 * @since 3 Mar, 2014
 * @version 0.1
 */
public class Document implements Serializable{

	/** Auto generated ID for serialization */
	private static final long serialVersionUID = -895111690492291410L;

	/** Instance of the sentence splitter */
	private transient Decomposition sentencer;

	// private String id = null;
	private Locale language;

	/** Internal dictionary that links terms to their numerical representation */
	protected final Dictionary dictionary;

	// /**Index of the document's sentences */
	// protected Map<String, Representation> representations;

	/** Representations for the diverse fragments */
	private List<Fragment> fragments;

	/** Stores the representations for the entire document */
	private Fragment entireText;

	/** Value of N in case of use n-grams */
	private int N;

	// private Keywords kw;
	// private NamedEntities ne;

	public Document(String text, Locale language) {
		this(text, language, true, true, true, true, 1);
	}

	public Document(String[] text, Locale language) {
		this(text, language, true, true, true, true, 1);
	}

	public Document(String text, Locale language, boolean includeBow,
			boolean includeCharNgrams, boolean includeWordNgrams,
			boolean includePseudoCognates) {
		this(text, language, includeBow, includeCharNgrams, includeWordNgrams,
				includePseudoCognates, 1);
	}

	public Document(String[] text, Locale language, boolean includeBow,
			boolean includeCharNgrams, boolean includeWordNgrams,
			boolean includePseudoCognates) {
		this(text, language, includeBow, includeWordNgrams, includeWordNgrams,
				includePseudoCognates, 1);
	}

	public Document(String text, Locale language, boolean includeBow,
			boolean includeCharNgrams, boolean includeWordNgrams,
			boolean includePseudoCognates, int nGrams) {
		dictionary = new Dictionary();
		sentencer = new SentencesOpennlp(language);
		fragments = new ArrayList<Fragment>();
		N = nGrams;
		// representations = new HashMap<String, Representation>();
		setLanguage(language);
		setText(text, includeBow, includeCharNgrams, includeWordNgrams,
				includePseudoCognates);
	}

	public Document(String[] text, Locale language, boolean includeBow,
			boolean includeCharNgrams, boolean includeWordNgrams,
			boolean includePseudoCognates, int nGrams) {
		dictionary = new Dictionary();
		sentencer = new SentencesOpennlp(language);
		fragments = new ArrayList<Fragment>();
		N = nGrams;
		// representations = new HashMap<String, Representation>();
		setLanguage(language);
		setText(text, includeBow, includeCharNgrams, includeWordNgrams,
				includePseudoCognates);
	}

	public boolean fragmentExists(int i) {
		if (i >= 0 && i < fragments.size()) {
			return true;
		}
		return false;
	}

	public void setLanguage(Locale language) {
		CHK.CHECK_NOT_NULL(language);
		this.language = language;
	}

	/**
	 * @param i
	 * @return fragment at index i; null if non-existent
	 */
	public Fragment getFragment(int i) {
		CHK.CHECK_NOT_NULL(i);
		if (i >= 0 && i <= fragments.size()) {
			return fragments.get(i);
		}
		return null;
	}

	public List<String> get(RepresentationType representation) {
		return entireText.get(representation);
	}

	public Map<String, Double> getWeighted(RepresentationType representation) {
		return entireText.getWeighted(representation);
	}

	public Map<String, Double> getNormalized(RepresentationType representation) {
		return entireText.getNormalized(representation);
	}

	/**
	 * @return entire text
	 */
	public String getText() {
		return entireText.getPlain();
	}

	/**
	 * @return number of fragments in the document
	 */
	public int length() {
		return fragments.size();
	}

	/**
	 * @param text
	 * @return sentences in the text
	 */
	protected List<String> splitText(String text) {
		return sentencer.getStrings(text);
	}

	/**
	 * Sets the input text
	 * 
	 * @param text
	 */
	private void setText(String text, boolean includeBow,
			boolean includeCharNgrams, boolean includeWordNgrams,
			boolean includePseudoCognates) {
		CHK.CHECK_NOT_NULL(text);
		entireText = new Fragment(dictionary, text, language, includeBow,
				includeCharNgrams, includeWordNgrams, includePseudoCognates, N);
		// String x = Fragment.BOW;
		// System.out.println(x);

		for (String sentence : splitText(text)) {
			fragments.add(new Fragment(dictionary, sentence, language,
					includeBow, includeCharNgrams, includeWordNgrams,
					includePseudoCognates, N));
		}
	}

	/**
	 * Sets the input text from its lines.
	 * 
	 * @param lines
	 */
	private void setText(String[] lines, boolean includeBow,
			boolean includeCharNgrams, boolean includeWordNgrams,
			boolean includePseudoCognates) {
		StringBuffer text = new StringBuffer();

		for (String sentence : lines) {
			fragments.add(new Fragment(dictionary, sentence, language,
					includeBow, includeCharNgrams, includeWordNgrams,
					includePseudoCognates, N));

			text.append(sentence).append("\n");
		}

		entireText = new Fragment(dictionary, text.toString(), language,
				includeBow, includeCharNgrams, includeWordNgrams,
				includePseudoCognates, N);
	}

	// public void setId(String id){
	// CHK.CHECK_NOT_NULL(id);
	// this.id = id;
	// }

	// public String getId(){
	// return id;
	// }

}
