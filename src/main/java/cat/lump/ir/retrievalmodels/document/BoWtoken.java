package cat.lump.ir.retrievalmodels.document;

import java.util.Locale;

/**
 * A document representation based on the bag-of-words model. It differs on BoW in that no 
 * stemming is applied. The preprocessing applied to the text here includes:
 * <ul>
 * <li> tokenization,
 * <li> stopword removal,
 * <li> casefolding, and
 * <li> diacritics removal
 * </ul>
 * 
 * @author cristinae
 * @since 12 May, 2014
 * @see cat.lump.ir.retrievalmodels.document.Representation
 */
class BoWtoken extends BoW{
	
	private static final long serialVersionUID = 4987669574969001912L;

	public BoWtoken(Dictionary dictionary, Locale language) {
		super(dictionary, language);
	}
	
	@Override
	protected String preprocess(String text){
		return text.toLowerCase();
	}	
	
}
