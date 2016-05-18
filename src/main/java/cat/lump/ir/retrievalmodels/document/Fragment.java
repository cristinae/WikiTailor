package cat.lump.ir.retrievalmodels.document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cat.lump.aq.basics.check.CHK;

/**
 * 
 * A fragment of text with multiple representations including:
 * <ul>
 * <li>plain text
 * <li>bag of words
 * <li>character n-grams
 * </ul>
 * 
 * @author albarron
 * @since 17 Mar, 2014
 * @version 0.1 *
 */
public class Fragment implements Serializable{

	/** Auto generated ID for serialization */
	private static final long serialVersionUID = 5727279848620467940L;
	
	private String text = null;
	private Locale language;

	/** Index of the document's sentences */
	private Map<RepresentationType, Representation> representations;

	/** Plain text (without modifications) */
	private PlainText plain;

	/** Global dictionary for the entire vocabulary */
	private Dictionary dictionary;

	/**
	 * Default invocation where all the characterizations are computed
	 * 
	 * @param dictionary
	 *            external dictionary to codify the strings
	 * @param text
	 * @param language
	 */
	public Fragment(Dictionary dictionary, String text, Locale language) {
		this(dictionary, text, language, true, true, true, true, 1);
	}

	/**
	 * @param dictionary
	 *            external dictionary to codify the strings
	 * @param text
	 * @param language
	 * @param includeBow
	 *            whether BoW representations should be included
	 * @param includeCharNgrams
	 *            whether cNg representations should be included
	 */
	public Fragment(Dictionary dictionary, String text, Locale language,
			boolean includeBow, boolean includeCharNgrams,
			boolean includeWordNgrams, boolean includePseudoCognates, int N) {

		CHK.CHECK_NOT_NULL(dictionary);
		CHK.CHECK_NOT_NULL(language);

		this.dictionary = dictionary;
		this.language = language;

		representations = new HashMap<RepresentationType, Representation>();

		CHK.CHECK_NOT_NULL(text);
		this.text = text;
		setPlain();
		if (includeBow) {
			setBow();
		}

		if (includeCharNgrams) {
			setCng(N);
		}

		if (includeWordNgrams) {
			setWng(N);
		}
		
		if (includePseudoCognates) {
			setCog();
		}
		// setKw();
		// setNes();
	}

	/**
	 * @param representation
	 * @return Number of terms in the fragment's representation 
	 */
	public int size(RepresentationType representation) {
		return representations.get(representation).length();
	}

	/** Set the plain-text representation */
	private void setPlain() {
		plain = new PlainText();
		plain.setText(text);
	}

	public String getPlain() {
		return plain.getText();
	}

	/** Create the bag-of-words representation for this fragment */
	private void setBow() {
		representations.put(RepresentationType.BOW, new BoW(dictionary, language));
		representations.get(RepresentationType.BOW).setText(text);
	}

	/** Create the character-n-grams representation for this fragment */
	private void setCng(int N) {
		representations.put(RepresentationType.CNG, new CharNgrams(dictionary, language,N));
		representations.get(RepresentationType.CNG).setText(text);
	}

	private void setWng(int N) {
		representations.put(RepresentationType.WNG, new WrdNgrams(dictionary, language,N));
		representations.get(RepresentationType.WNG).setText(text);
	}
	
	private void setCog() {
		representations.put(RepresentationType.COG, new PseudoCognates(dictionary, language));
		representations.get(RepresentationType.COG).setText(text);
	}

	// private void setKw(){
	// //TODO
	// }
	//
	// private void setNes(){
	// //TODO
	// }

	/**
	 * @param representation
	 *            Fragment.[CNG,BOW] for the required representation
	 * @return flat list of elements in the representation
	 */
	public List<String> get(RepresentationType representation) {
		return representations.get(representation).getRepresentation();
	}

	/**
	 * @param representation
	 *            Fragment.[CNG,BOW] for the required representation
	 * @return tf-weighted map of elements in the representation
	 */
	public Map<String, Double> getWeighted(RepresentationType representation) {
		return representations.get(representation).getWeightedRepresentation();
		// return bow.getWeightedRepresentation();
	}

	/**
	 * @param representation
	 *            Fragment.[CNG,BOW] for the required representation
	 * @return normalized-tf-weighted map of elements in the representation
	 */
	public Map<String, Double> getNormalized(RepresentationType representation) {
		return representations.get(representation)
				.getNormalizedRepresentation();
	}

	//
	//
	// public List<String> getCng(){
	// if (INCLUDE_CHAR_NGRAMS)
	// return null;
	// return cng.getRepresentation();
	// }
	//
	// public Map<String, Double> getWeigtedCng(){
	// if (INCLUDE_CHAR_NGRAMS)
	// return null;
	// return cng.getWeightedRepresentation();
	// }
	//
	// public Map<String, Double> getNormalizedCng(){
	// if (INCLUDE_CHAR_NGRAMS)
	// return null;
	// return cng.getNormalizedRepresentation();
	// }

}
