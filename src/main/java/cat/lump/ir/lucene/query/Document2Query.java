package cat.lump.ir.lucene.query;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * The contents of a document are processed to be in the
 * right format for Lucene querying. 
 * <br>
 * In order to do that, a tokenizer is called that acts exactly as 
 * it did during the index generation depends on Lucene's Analyzers)
 * 
 * 
 * TODO determine which format is better for different interests, such as ESA
 * TODO the stemming process seems to be missing here. Check if when querying it is carriend out
 * @author albarron
 * @since April 12 2012
 *
 */
public class Document2Query {

	
	private LuceneTokenizer lt;
//	public static void main(String[] args) throws IOException {
//		String file = "/home/albarron/kk/kk.txt";
//			
//		String[] tokens = LuceneTokenizer.standardTokenize(file);
//		String f_query = vocQuery(tokens);
//		String w_query = weightQuery(tokens);
//		System.out.println(f_query);
//		System.out.println();
//		System.out.println(w_query);
//	}
	
	public Document2Query(){
		this(Locale.ENGLISH);	
	}
	
	public Document2Query(Locale lan){
		lt = TokenizerFactory.getLuceneTokenizer(lan);
	}
	
	//TODO this getter should not exist. It's only for the current LuceneQuerier
	public Analyzer getAnalyzer(){
		return lt.analyzer;
	}
	
	/**
	 * Generates a query in which every token has the same relevance
	 * TODO why am I using the same tokenizer for every language???
	 * @param file
	 * @return string representation of the query
	 * @throws IOException
	 */
	public  String file2FlatQuery(String file) {
		String[] tokens = null;
		try {
			tokens = lt.standardTokenize(new File(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vocQuery(tokens);
	}
	
	/**Generates a query in which every token has the same relevance
	 * @param analyzer
	 * @param text string representation of the query
	 * @return string with space-separated tokens
	 *///TODO this analyzer shouldn't be set here, but depending on the languages?
	//TODO talk to Meritxell about this.
	public String str2FlatQuery(Analyzer analyzer, String text) {		
		StringBuffer result = new StringBuffer();
	    try {
	      TokenStream stream  = analyzer.tokenStream(null, new StringReader(text));
	      while (stream.incrementToken()) {
	        result.append(stream.getAttribute(CharTermAttribute.class).toString())
	        	.append(" ");
	      }
	    } catch (IOException e) {
	      // not thrown b/c we're using a string reader...
	      throw new RuntimeException(e);
	    }
	    if (result.length() > 1)
	    	result.deleteCharAt(result.length() -1);
	    return result.toString();
		
	}
	

	
	/**Generates a query in which every token has the same relevance
	 * @param text string representation of the query
	 * @return string with space-separated tokens
	 */
	public String str2FlatQuery(String text) {		
		StringBuffer result = new StringBuffer();
	    try {
	      TokenStream stream  = lt.analyzer.tokenStream(null, new StringReader(text));
	      while (stream.incrementToken()) {
	        result.append(stream.getAttribute(CharTermAttribute.class).toString())
	        	.append(" ");
	      }
	    } catch (IOException e) {
	      // not thrown b/c we're using a string reader...
	      throw new RuntimeException(e);
	    }
	    if (result.length() > 1)
	    	result.deleteCharAt(result.length() -1);
	    return result.toString();
		
	}
	
	/**
	 * Generates a query in which tokens' relevance depend on their frequency
	 * @param file
	 * @return a string with space-separated tokens and weights
	 */
	public String doc2WeightQuery(String file) {
		String[] tokens = null;
		try {
			tokens = lt.standardTokenize(new File(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return weightQuery(tokens);
	}	
	
	/**
	 * Creates a query considering only the vocabulary (i.e. types)
	 * @param tokens
	 * @return a string with space-separated tokens
	 */
	public static String vocQuery(String[] tokens){
		Set<String> s_tokens = new HashSet<String>(Arrays.asList(tokens));
		StringBuffer sb = new StringBuffer();
		
		for (String str : s_tokens)
			sb.append(str)
			  .append(" ");
		
		return sb.toString();
	}
	
	/**
	 * Creates a query considering all the tokens (i.e. some words could be 
	 * repeated)
	 * @param tokens
	 * @return string with flat query (space-separated tokens)
	 */
	public static String flatQuery(String[] tokens){
		StringBuffer sb = new StringBuffer();		
		for (String str : tokens)
			sb.append(str)
			  .append(" ");
		
		return sb.toString();
	}
	
	
	/**
	 * Creates a query where the relevance of a type depends on its 
	 * frequency (i.e. if a token w appears 4 times, it will appear 
	 * as w^4) 
	 * @param tokens
	 * @return string where tokens are weighted by their frequency
	 */
	public static String weightQuery(String[] tokens){
		Map<String, Integer> m_tokens = new HashMap<String, Integer>();
		StringBuffer sb = new StringBuffer();
		
		for (String str : tokens){
			if (!m_tokens.containsKey(str))
				m_tokens.put(str, 0);
			m_tokens.put(str, m_tokens.get(str) +1);
		}
		
		for (String str : m_tokens.keySet())
			sb.append(str)
			  .append("^")
			  .append(m_tokens.get(str))
			  .append(" ");
		
		return sb.toString();
	}	
}
