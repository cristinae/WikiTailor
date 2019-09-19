package cat.lump.ie.textprocessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.SnowballStemmer;

import cat.lump.aq.basics.check.CHK;
import cat.lump.ie.textprocessing.sentence.Diacritics;
import cat.lump.ie.textprocessing.sentence.Punctuation;
import cat.lump.ie.textprocessing.stopwords.Stopwords;
import cat.lump.ie.textprocessing.word.AnalyzerFactoryLucene;
import cat.lump.ie.textprocessing.word.StemmerFactory;
import cat.lump.ie.textprocessing.word.WordDecompositionICU4J;
//import cat.lump.ie.textprocessing.sentence.Punctuation;

import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.experiments.CorrelationsxCategory;

/**
 * <p>
 * This class represents an "interface" to the different text processing tools 
 * available in this package.
 * </p>
 * 
 * <p>The currently available operations are:
 * <ul>
 * <li> toLowerCase()
 * <li> removePunctuation()
 * <li> removeDiacritics()
 * <li> removeStopwords()
 * <li> stem()
 * <li> onlyAlphabetic(4) 
 * <ul>
 * </p>
 * <p>
 *  Note that a copy of the originally set string is stored and the changes are
 *  carried out on a copy
 * </p>
 * @author albarron
 * @author cristinae
 *
 */
public class TextPreprocessor{
	
	//private Decomposition sentenceDetector;
	private WordDecompositionICU4J tokenizer;
	private Stopwords stop;
	private Stopwords stopEng;
	private SnowballStemmer stemmer;
	private Analyzer analyzer; 
	
	/**The string on which all the operations are applied*/
	private String str;
	
	/**The list with all the tokens. This list is modified as different preprocessing 
	 * operations are carried out	 */
	private List<String> tokens;
	/** The language for this preprocessor */
	private Locale language;
    /** The tool the preprocessor uses for stemming*/
	private Boolean isSnowball;
	
	private final static String SPACE_PATTERN = "\\s+";	
	
	// Single quotes to normalise
	private final static Pattern r_quote1_norm = Pattern.compile("([`‘’´])");
	private final static String s_quote1_norm = "'";
	// Double quotes to normalise
	private final static Pattern r_quote2_norm = Pattern.compile("([“”„«»]|'')");
	private final static String s_quote2_norm = "\"";
	// Dashes to normalise
	private final static Pattern r_dash_norm = Pattern.compile("(--)");
	private final static String s_dash_norm = "-";
	// MDots to normalise
	private final static Pattern r_mdot_norm = Pattern.compile("(…|\\.\\.)");
	private final static String s_mdot_norm = "...";
		
	private static LumpLogger logger = new LumpLogger (TextPreprocessor.class.getSimpleName());

	//Constructors
	
	public TextPreprocessor(Locale lan)
	{
		setLocale(lan);
		tokens = new ArrayList<String>();
	}
		
	// Setters
	
	private void setLocale(Locale lan)
	{
		CHK.CHECK_NOT_NULL(lan);
		language = lan;
		//String langS = language.getLanguage();
		
		//	sent_detector = new SentencesOpennlp(Locale.ENGLISH);
		tokenizer = new WordDecompositionICU4J(language);
		stop = new Stopwords(language);
		stopEng  = new Stopwords(Locale.ENGLISH);
		if (StemmerFactory.loadStemmer(language) != null){
			isSnowball = true;
			stemmer = StemmerFactory.loadStemmer(language);	
		} else if (language.toString().equalsIgnoreCase("ne")) {
			logger.warn("Please, add a stemmer for Nepali, using English now (so, no doing anything!)");
			isSnowball = true;
			stemmer = StemmerFactory.loadStemmer(Locale.ENGLISH);	
		} else if (language.toString().equalsIgnoreCase("gu")) {
			logger.warn("Please, add a stemmer for Gujarati, using English now (so, no doing anything!)");
			isSnowball = true;
			stemmer = StemmerFactory.loadStemmer(Locale.ENGLISH);	
		} else if (language.toString().equalsIgnoreCase("simple")) {
			isSnowball = true;
			stemmer = StemmerFactory.loadStemmer(Locale.ENGLISH);	
		} else {
			isSnowball = false;
		    analyzer = AnalyzerFactoryLucene.loadAnalyzer(language); 
		}
	}
	
	// Public	
	/**Converts the string to lowercase */
	public void toLowerCase(){
		for (int i = 0 ; i<tokens.size() ; i++)
		{
			tokens.set(i, tokens.get(i).toLowerCase());
		}		
	}
	
	/**
	 * Removes the diacritics from the string
	 */
	public void removeDiacritics(){
		for (int i = 0 ; i<tokens.size() ; i++)
		{
			tokens.set(i, Diacritics.removeDiacritics(tokens.get(i)));
		}		
	}

	/**
	 * Removes the punctuation marks from the string
	 */
	public void removePunctuation(){
		Iterator<String> iter = tokens.iterator(); 
		
		while (iter.hasNext())
		{
			if (Punctuation.onlyPunctuation(iter.next()) )
				iter.remove();
		}
	}
	
	/**Eliminates all the stopwords from the string */
	public void removeStopwords(){
		stop.removeStopwords(tokens);
	}
	
	/**Eliminates all the English stopwords from the string */
	public void removeEngStopwords(){
		stopEng.removeStopwords(tokens);
	}
	
	/**
	 * Remove any token which is not the in [:alnum:] character class.
	 * It also removes the tokens with a length lower than a minimum size. 
	 *  
	 * @param minimumSize 
	 * 				Minimum size of accepted tokens. If equals to 0,
	 * 						all the tokens will be accepted
	 */
	public void removeNonAlphaNumeric(int minimumSize) 
	{	
		CHK.CHECK_NOT_NULL(minimumSize);
		CHK.CHECK(minimumSize >= 0, "No negative values are accepted");
		
		//String pattern = String.format("[\\p{Alnum}]{%d,}", minimumSize);
		String pattern;
		if (language.toString().equalsIgnoreCase("ar")) {	
			pattern = String.format("[\\p{IsArabic}\\p{Alnum}]{%d,}", minimumSize);
		} else if(language.toString().equalsIgnoreCase("ne"))  {
			pattern = String.format("[\\p{IsDevanagari}\\p{Alnum}]{%d,}", minimumSize);			
		} else if(language.toString().equalsIgnoreCase("gu"))  {
			pattern = String.format("[\\p{IsGujarati}\\p{Alnum}]{%d,}", minimumSize);			
		} else if(language.toString().equalsIgnoreCase("si"))  {
			pattern = String.format("[\\p{IsSinhala}\\p{Alnum}]{%d,}", minimumSize);			
		} else if(language.toString().equalsIgnoreCase("el"))  {
			pattern = String.format("[\\p{IsGreek}\\p{Alnum}]{%d,}", minimumSize);			
		} else if(language.toString().equalsIgnoreCase("bg") || language.toString().equalsIgnoreCase("ru"))  {
			pattern = String.format("[\\p{IsCyrillic}\\p{Alnum}]{%d,}", minimumSize);			
		} else {
			pattern = String.format("[\\p{Alnum}]{%d,}", minimumSize);
		}

		Pattern p = Pattern.compile(pattern);		
		removePattern(p);
	}
	
	
	/**
	 * Remove any token which is not in [:alpha:] character class.
	 * It also removes the tokens with a length less than minimum size. 
	 * However, a minimum size of 0 implies any length restriction.
	 * 
	 * @param minimumSize Minimum size of accepted tokens. If it equals 0,
	 * 						all the tokens will be accepted
	 */
	public void removeNonAlphabetic(int minimumSize) 
	{
		CHK.CHECK_NOT_NULL(minimumSize);
		CHK.CHECK(minimumSize >= 0, "No negative values are accepted");
		String pattern;
		if (language.toString().equalsIgnoreCase("ar")) {	
			pattern = String.format("[\\p{IsArabic}\\p{Alpha}]{%d,}", minimumSize);
		} else if(language.toString().equalsIgnoreCase("gu"))  {
			pattern = String.format("[\\p{IsGujarati}\\p{Alpha}]{%d,}", minimumSize);			
		} else if(language.toString().equalsIgnoreCase("ne"))  {
			pattern = String.format("[\\p{IsDevanagari}\\p{Alpha}]{%d,}", minimumSize);			
		} else if(language.toString().equalsIgnoreCase("si"))  {
			pattern = String.format("[\\p{IsSinhala}\\p{Alpha}]{%d,}", minimumSize);			
		} else if(language.toString().equalsIgnoreCase("el"))  {
			pattern = String.format("[\\p{IsGreek}\\p{Alpha}]{%d,}", minimumSize);			
		} else if(language.toString().equalsIgnoreCase("bg") || language.toString().equalsIgnoreCase("ru"))  {
			pattern = String.format("[\\p{IsCyrillic}\\p{Alpha}]{%d,}", minimumSize);			
		} else {
			pattern = String.format("[\\p{Alpha}]{%d,}", minimumSize);
		}
		Pattern p = Pattern.compile(pattern);
		removePattern(p);
	}
	
	
	/**
	 * Normalize the text by shrinking white spaces in one as well as substituting 
	 * quotations, dashes and dots. 
	 * 
	 * TODO include an example
	 * 
	 * @param text
	 * @return
	 */
	public static String normalizeText(String text) {
		text = text.replaceAll(SPACE_PATTERN, " ").trim();
		text = r_quote1_norm.matcher(text).replaceAll(s_quote1_norm);
		text = r_quote2_norm.matcher(text).replaceAll(s_quote2_norm);
		text = r_dash_norm.matcher(text).replaceAll(s_dash_norm);
		text = r_mdot_norm.matcher(text).replaceAll(s_mdot_norm);
		return text;
	}
	
	/**
	 * Normalize the text by shrinking white spaces in one as well as substituting 
	 * quotations, dashes and dots. It substitutes apostrophes by a space as a previous
	 * step for tokenisation with only BreakIterators
	 * 
	 * @param text
	 * @return
	 */
	public String normalizeAndDeAposText(String text) {
		text = text.replaceAll(SPACE_PATTERN, " ").trim();
		text = r_quote1_norm.matcher(text).replaceAll(s_quote1_norm);
		text = text.replaceAll(s_quote1_norm, " ");
		text = r_quote2_norm.matcher(text).replaceAll(s_quote2_norm);
		text = r_dash_norm.matcher(text).replaceAll(s_dash_norm);
		text = r_mdot_norm.matcher(text).replaceAll(s_mdot_norm);
		
		/*//Language-dependent normalisations 
		if (language.toString().equalsIgnoreCase("de")) {  
			text = text.replaceAll("\u00DF", "ss");   //Eszett
			text = text.replaceAll("\u1E9E", "SS");
		}*/
		
		return text;
	}
	
		
	
		
	public void stem()	{
//		if (language.toString().equalsIgnoreCase("ar") ||
//			language.toString().equalsIgnoreCase("el")) {
		if (!isSnowball) {
			 stemLucene();
		} else {
			for (int i = 0 ; i<tokens.size() ; i++)
			{
				stemmer.setCurrent(tokens.get(i));
				stemmer.stem();
				tokens.set(i, stemmer.getCurrent());

			}
		}
	}
	
	public void stemLucene()	{
		for (int i = 0 ; i<tokens.size() ; i++)
		{    
			TokenStream ts = AnalyzerFactoryLucene.loadStemFilter(language, analyzer, tokens.get(i));
			CharTermAttribute cattr = ts.addAttribute(CharTermAttribute.class);
			try {
				ts.reset();
				while (ts.incrementToken()) {
					//System.out.println(tokens.get(i) + "::new " + cattr.toString());
					tokens.set(i, cattr.toString());
				}
				ts.end();
				ts.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			            
		}
	}

	// Setters
	
	/**
	 * Stores a copy of the original string and generates a tokenized copy. 
	 * 
	 * @param str
	 */
	public void setString(String str){
		CHK.CHECK_NOT_NULL(str);
		this.str = normalizeText(str);		
		tokens = tokenizer.getStrings(this.str);
	}

	/**
	 * Stores a copy of the original string and generates a tokenized copy.
	 * Apostrophes are substituted by spaces as a previous step for tokenisation with 
	 * only BreakIterators.
	 * 
	 * @param str
	 */
	public void setStringTokens(String str){
		CHK.CHECK_NOT_NULL(str);
		this.str = normalizeAndDeAposText(str);		
		tokens = tokenizer.getStrings(this.str);
	}
		
	// Getters
	
	public String getString(){
		StringBuffer sb = new StringBuffer();
		if (tokens.size() > 0) {
			for (String t : tokens)
			{
				sb.append(t).append(" ");
			}
			return sb.substring(0, sb.length() - 1).toString();
		} else 
		{
			return "";
		}

	}
	
	public List<String> getTokens(){
		return tokens;
	}

	public String getOriginalString()
	{
		return str;
	}

	// Privates

	/**
	 * Remove those tokens that match the given pattern.
	 * @param p
	 */
	private void removePattern(Pattern p)
	{
		Iterator<String> iter = tokens.iterator(); 
		
		while (iter.hasNext())
		{
			Matcher m = p.matcher(iter.next());
			if (! m.matches() ) 
				//System.out.println(" removing with pattern: " + p.toString() );
				iter.remove();			
		}		
	}


//	
//	
//	
//	//////////////
//	//			//
//	// PROCESS	//
//	//			//
//	//////////////
//	
//	/**Removes the diacritics from the string*/
//	public void removeDiacritics(){
//		str = Diacritics.removeDiacritics(str);
//	}
//	
//	
//	public List<String> getSentences(){
//		return sent_detector.getStrings(str);
//	}
//
//	
//	/**Remove spaces and punctuation marks*/
//	public void removePunctSpaces(){
//		str = str.replaceAll("[^a-zA-Z0-9]", "");
//	}
//	//		
//	/** Eliminates diacritics, spaces, and punctuation marks. */
//	protected void FullPreprocessing(){
//		//TODO apparently the snowball stemmer does diacritics removal. 
//		//Therefore, if the words are to be stemmed, this function shouldn't 
//		//be called.
//		removeDiacritics();
//		removePunctSpaces();
//		toLowerCase();		
//	}

	

}
