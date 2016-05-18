package cat.lump.ir.lucene.engine;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.util.Version;

import cat.lump.aq.textextraction.wikipedia.prepro.TermExtractor;


/**
 * Modification of the standard Lucene Analyzer to mimic the preprocess
 * and term extraction used in the Wikiparable experiment 
 * 
 * @author cristina
 * @since June 16, 2015
 *
 */
//public class WTAnalyzer extends ReusableAnalyzerBase{
public class WTAnalyzer extends Analyzer{

	/** The language for this tokenisation, needed because of the modification */
	private Locale lan;
	/** Lucene version*/
	private Version LUCENE_VERSION;

	  
	public WTAnalyzer(Version LUCENE_VERSION, Locale lan) {
		this.lan = lan;
		this.LUCENE_VERSION = LUCENE_VERSION;

	}

	/**
	 * Pipeline for the analyser. The text is retrieved and the preprocess and term extraction
	 * is done using the {@code TermExtractor} class used within Wikiparable. No extra work by
	 * Lucene is allowed other than indexing.
	 * 
	 * @param fieldName
	 * @param reader
	 * @return stream
	 */
	// Aquesta es per extendre Analyzer per a versions com aquesta de Lucene que son antigues
	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		
		// Extract the text from the reader in order to apply the preprocess
		String text = "";
		try {
			text = readFully(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		TermExtractor tx = new TermExtractor(lan);
		List<String> tokens = tx.getTerms(text, 4);		
		text = StringUtils.join(tokens, " ");
		
		// The reader has now the text as the join of terms
		reader = new StringReader(text);
		// We are forces to use a tokeniser so, this one just undoes the join with " " 
	    TokenStream stream = new WhitespaceTokenizer(LUCENE_VERSION, reader);
	    
	    return stream;
	}


	// Aquesta es per extendre ReusableAnalyzerBase per a versions com aquesta de Lucene que son antigues
	/**@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		ICUTokenizer tokenizer = new ICUTokenizer(reader);
		return new TokenStreamComponents(tokenizer);
	}*/
	
	
	// From: http://qnalist.com/questions/4495402/developing-custom-tokenizer
	public String readFully(Reader reader) throws IOException {
		char[] arr = new char[8 * 1024]; // 8K at a time
		StringBuffer buf = new StringBuffer();
		int numChars;
		while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
			buf.append(arr, 0, numChars);
		}
		return buf.toString();
	}
}
