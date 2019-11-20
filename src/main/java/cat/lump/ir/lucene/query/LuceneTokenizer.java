/**
 * Created on 13 Apr 2012<br><br>
 * Software being developed by lbarron
 */

package cat.lump.ir.lucene.query;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.ca.CatalanAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.eu.BasqueAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import cat.lump.ir.lucene.index.analyzers.LithuanianAnalyzer;
import cat.lump.aq.basics.log.LumpLogger;


/**
 * A simple interface to perform tokenization through the Lucene
 * methods. 
 * 
 * Partially based on 
 * http://stackoverflow.com/questions/2638200/how-to-get-a-token-from-a-lucene-tokenstream
 * 
 * @author albarron
 *
 */
public class LuceneTokenizer {
	
	
	
	protected Analyzer analyzer; 

	private static LumpLogger log = new LumpLogger("Lucene Tokenizer");
	
	public LuceneTokenizer() {
		this(Locale.ENGLISH);
	}
	
	public LuceneTokenizer(Locale lan){
		setAnalyzer(lan);
	}
	
	/**
	 * TODO: This code is duplicated with cat.lump.ir.lucene.engine.loadAnalyzer
	 * @param lan
	 */
	protected void setAnalyzer(Locale lan){
		switch(lan.getLanguage()){
			case "ar":
				analyzer = new ArabicAnalyzer();
				break;
			case "bg":
				analyzer = new BulgarianAnalyzer();
				break;
			case "ca":
				analyzer = new CatalanAnalyzer();
				break;
			case "cs":
				analyzer = new CzechAnalyzer();
				break;
			case "de":
				analyzer = new GermanAnalyzer();
				break;
			case "el":
				analyzer = new GreekAnalyzer();
				break;
			case "en":
				analyzer = new StandardAnalyzer();
				break;
			case "es":
				analyzer = new SpanishAnalyzer();
				break;
			case "eu":
				analyzer = new BasqueAnalyzer();
				break;
			case "fr":
				analyzer = new FrenchAnalyzer();
				break;
			case "hu":
				analyzer = new HungarianAnalyzer();
				break;
			case "it":
				analyzer = new ItalianAnalyzer();
				break;
			case "lt":
				analyzer = new LithuanianAnalyzer();
				break;	
			case "pt":
				analyzer = new PortugueseAnalyzer();
				break;
			case "ro":
				analyzer = new RomanianAnalyzer();
				break;
			case "ru":
				analyzer = new RussianAnalyzer();
				break;
			default:
				log.warn("I cannot process the required language.");
				setAnalyzer(Locale.ENGLISH);
				
		}
		log.info("Tokenizer for language " +lan.getLanguage() + "loaded.");
		
		
	}
	
	/**
	 * Tokenize the text from the given file using 
	 * Lucene's StandardAnalyzer 
	 * @param file
	 * @return tokens in the file
	 * @throws IOException
	 */
	public String[] standardTokenize(File file) throws IOException{		
		return tokenize(file);		
	}
	
	/**Tokenize the text using Lucene's StandardAnalyzer
	 * @param text 
	 * @return
	 */
	public String[] standardTokenize(String text) {
		return tokenize(text);
	}
		
	public static String[] ngramTokenize(String file){
		//TODO n-gram tokenizer
//		analyzer = new NGramTokenizer(LUCENE_VERSION);
		return null;
	}
	
	
	/**
	 * Actually tokenizes the text
	 * @param file
	 * @return tokens from the file
	 * @throws IOException
	 */
	private String[] tokenize(File file) throws IOException{
		List<String> l_tokens = new ArrayList<String>();  
		
		CharTermAttribute charTermAttribute;
		
		TokenStream stream = analyzer.tokenStream(null, //"meaningless", 
				new	FileReader(file));	
		
		while (stream.incrementToken()) {
			charTermAttribute = stream.addAttribute(CharTermAttribute.class);
			String term = charTermAttribute.toString();
			l_tokens.add(term);
		}		
		return l_tokens.toArray(new String[l_tokens.size()]);
	}
	
	private String[] tokenize(String text){
		List<String> result = new ArrayList<String>();
		
		TokenStream stream = analyzer.tokenStream(null, new StringReader(text));
		try {
			stream.reset();
		
			while (stream.incrementToken()) {
				result.add(stream.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toArray(new String[result.size()]);  
	}
	
}

