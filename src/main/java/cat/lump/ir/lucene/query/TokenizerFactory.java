package cat.lump.ir.lucene.query;

import java.util.Locale;

/**Creates and returns a Lucene Tokenizer instance for the required language
 * 
 * @author albarron
 *
 */
public class TokenizerFactory {	
	
	public static LuceneTokenizer getLuceneTokenizer(Locale language){
		return new LuceneTokenizer(language);		
	}
	
	
	
}
