package cat.lump.ir.lucene.engine;

import java.io.StringReader;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.ar.ArabicNormalizationFilter;
import org.apache.lucene.analysis.ar.ArabicStemFilter;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.bg.BulgarianStemFilter;
import org.apache.lucene.analysis.ca.CatalanAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.cz.CzechStemFilter;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.el.GreekLowerCaseFilter;
import org.apache.lucene.analysis.el.GreekStemFilter;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.eu.BasqueAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.lv.LatvianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import cat.lump.aq.basics.check.CHK;
import cat.lump.ir.lucene.LuceneInterface;
import cat.lump.ir.lucene.index.analyzers.LithuanianAnalyzer;

/**
 * Factory that allows for getting a Lucene {@code Analyzer} with all the preprocess needed 
 * 
 * TODO This is duplicated with textextraction, maybe we should unify 
 * @author cristina
 * @since June 18, 2015
 * @see org.apache.lucene.analysis
 */
public class AnalyzerFactory {
	
	protected static Analyzer analyzer; 


	/** Analyser from Lucene for different languages.
	 * 
	 * TODO: This code is duplicated with cat.lump.ir.lucene.query.LuceneTokenizer 
	 * @param language
	 * @return The Lucene analyser for the required language; error if not available.
	 */
	public static Analyzer loadAnalyzer(Locale language){
		CHK.CHECK_NOT_NULL(language);
		String lang = language.getLanguage();
		
		switch(lang){
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
		case "lv":
			analyzer = new LatvianAnalyzer();
			break;	
		case "oc":
			//TODO Solve this!
			analyzer = new CatalanAnalyzer();             
			CHK.CHECK(false, "Using the Catalan stemmer for language "+
					language.getDisplayLanguage() );
			break;
		case "ro":
			analyzer = new RomanianAnalyzer();
			break;
		case "ru":
			analyzer = new RussianAnalyzer();
			break;
		case "pt":
			analyzer = new PortugueseAnalyzer();
			break;
		default:
			CHK.CHECK(false, "No Lucene Analyzer is available for language "+
							language.getDisplayLanguage() );
			return null;
		}
		return analyzer;
	}

	
	
	/**
	 * Stemmer from Lucene for different languages. For those languages where Lucene
	 * uses Snowball there is no need to go further the TokenStream initialisation.
	 * 
	 * @param language
	 * @return The Lucene stemmer for the required language; error if not available.
	 */
	public static TokenStream loadStemFilter(Locale lng, Analyzer an, String str){
		CHK.CHECK_NOT_NULL(lng);
		String lang = lng.getLanguage();
		
		TokenStream ts = an.tokenStream(null, new StringReader(str));

		switch(lang){
		case "ar":
			ts = new ArabicNormalizationFilter(ts);
			ts = new ArabicStemFilter(ts);
			break;
		case "bg":      //TODO check this!
			ts = new BulgarianStemFilter(ts);
			break;
		case "ca":
			break;
		case "cs":      //TODO check this!
			ts = new CzechStemFilter(ts);
			break;
		case "de":
			break;
		case "el":
			ts = new GreekLowerCaseFilter(ts);
			ts = new GreekStemFilter(ts);
			break;
		case "en":
			break;
		case "es":
			break;
		case "eu":
			break;
		case "fr":
			break;
		case "it":
			break;
		case "oc":
			break;
		case "pt":    //TODO check this!
			//ts = new PortugueseStemFilter(ts);
			break;
		case "ro":
			break;
		default:
			CHK.CHECK(false, "No Lucene stemmer is available for language "+
							lng.getDisplayLanguage() );
			return null;
		}
		return ts;
	}

}
