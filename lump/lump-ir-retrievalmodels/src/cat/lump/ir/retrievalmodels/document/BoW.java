package cat.lump.ir.retrievalmodels.document;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.tartarus.snowball.SnowballStemmer;

import cat.lump.aq.basics.check.CHK;
import cat.lump.ie.textprocessing.TextPreprocessor;
import cat.lump.ie.textprocessing.stopwords.Stopwords;
import cat.lump.ie.textprocessing.word.StemmerFactory;

/**
 * A document representation based on the bag-of-words model. The preprocessing
 * applied to the text includes:
 * <ul>
 * <li> tokenization,
 * <li> stopword removal,
 * <li> casefolding, 
 * <li> diacritics removal, and
 * <li> stemming
 * </ul>
 * 
 * As in every Representation, the BoW representation is available for the entire
 * document (also sentence-wise) as well as for each sentence.
 * 
 * @author albarron
 * @version 0.1
 * @since 4 Mar, 2014
 * @see cat.lump.ir.retrievalmodels.document.Representation
 */
class BoW extends Representation{
	
	/** */
	private static final long serialVersionUID = -6673967447658458877L;

	/** Instance of the stopwording class */
	private transient Stopwords sw;
	
	/** Instance of a snowball stemmer */
	private transient  SnowballStemmer stemmer;	
	
	/**
	 * @param text from the document
	 * @param language of the text
	 */
	public BoW(Dictionary dictionary, Locale language){
		super(dictionary, language);
		sw = new Stopwords(language);
		stemmer = StemmerFactory.loadStemmer(language);
		GLOBAL_COUNTER = 0;
	}
	
	/**
	 * Sets the input text. The preprocessing operations include tokenization,
	 * stopword removal, and stemming.
	 * 
	 *  //TODO pay attention as we do not keep track of the words' position
	 * @param text
	 */
	@Override
	public void setText(String text){
		CHK.CHECK_NOT_NULL(text);
		text = TextPreprocessor.normalizeText(text);
		TERMS = new HashMap<Integer, Double>();
		int id;
		List<String> tokens = getTokens(text);
		sw.removeStopwords(tokens);	
		if (tokens.isEmpty()) {
			String dummyToken = getDummyWord();
			tokens.add(dummyToken);
		}		
		
		for (String token : tokens){
			token = preprocess(token);
			GLOBAL_COUNTER++;
			
			id = dictionary.addString(token);
			
			if (TERMS.containsKey(id)){
				TERMS.put(id, TERMS.get(id) +1); 
			} else {
				TERMS.put(id, 1.0);
			}						
		}		
	}
	
	@Override
	protected String preprocess(String text){
		stemmer.setCurrent(text);
		stemmer.stem();
		return stemmer.getCurrent().toLowerCase();
	}	
}
