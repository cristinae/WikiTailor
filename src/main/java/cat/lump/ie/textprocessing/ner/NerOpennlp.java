package cat.lump.ie.textprocessing.ner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import cat.lump.aq.basics.check.CHK;
import cat.lump.ie.textprocessing.Decomposition;
import cat.lump.ie.textprocessing.Span;


/**
 * A class to get the named entities from a given text based on OpenNLP.
 * <br/>
 * The currently supported languages are:
 * <ul>
 * <li> English
 * <li> Spanish
 * </ul> 
 * 
 * TODO include a module to receive an entire document (sentences generated).
 * TODO After finishing a document, the models should be resetted. 
 * 
 * More info in:
 * 
 * http://opennlp.apache.org/documentation/1.5.3/manual/opennlp.html#tools.namefind
 *   
 * @author albarron
 * @version 0.1
 * @since Mar 2014
 *
 */
public class NerOpennlp implements Decomposition{

	
	private TokenNameFinderModel model;
	
	private NameFinderME nameFinder; 
	
	private static final String PERSON_EN = "en-ner-person.bin";
	
	private static final String PERSON_ES = "es-ner-person.bin";
	
	private InputStream MODEL_PERSON;
//	private InputStream MODEL_ORGANIZATION;
//	private InputStream MODEL_LOCATION;
//	private InputStream MODEL_DATE;
	
	public NerOpennlp(Locale language) {
		
//		InputStream modelIn = null;
		
		try{
			getPersonModel(language);
			model = new TokenNameFinderModel(MODEL_PERSON);

		//TODO set this catch properly
		} catch (IOException e) {	e.printStackTrace(); } 
		finally {
			if (MODEL_PERSON != null) {
				try { MODEL_PERSON.close();} 
				catch (IOException e){	}
			}
		}
		
		nameFinder = new NameFinderME(model);
	}	

	public List<String> getStrings(String[] text){
		StringBuffer sb = new StringBuffer();
		
		List<Span> spans = getSpans(text);
		List<String> nes = new ArrayList<String>();
		for (Span sp : spans){
			
			for (int i = sp.getStart(); i < sp.getEnd(); i++){
				sb.append(text[i])
				  .append(" ");
			}
//			sp.getSubstring(text);
			sb.deleteCharAt(sb.length() -1);
			nes.add(sb.toString());
			sb.delete(0, sb.length());
		}
		return nes;
	}
	
	/**
	 * @param text
	 * @return sentences in the given text.
	 */
	/* (non-Javadoc)
	 * @see cat.lump.ie.textprocessing.Decomposition#getStrings(java.lang.String)
	 */
	@Override
	public List<String> getStrings(String text) {
		return null;
//		return Arrays.asList(sentenceDetector.sentDetect(text));
	}

	public List<Span> getSpans(String[] text) {
		List<Span> mySpans = new ArrayList<Span>();
		opennlp.tools.util.Span[] sp = nameFinder.find(text);		
		for (int i = 0; i < sp.length; i++){
			mySpans.add(new Span(sp[i].getStart(), sp[i].getEnd()));			
		}					
		return mySpans;
	}
	
	/** 
	 * 
	 * @param text
	 * @return sentence spans in the given text.
	 */
	/* (non-Javadoc)
	 * @see cat.lump.ie.textprocessing.Decomposition#getSpans(java.lang.String)
	 */
	@Override
	public List<Span> getSpans(String text) {
		List<Span> mySpans = new ArrayList<Span>();
//		opennlp.tools.util.Span[] spans = sentenceDetector.sentPosDetect(text);
//		for (opennlp.tools.util.Span x : spans){
//			mySpans.add(new Span(x.getStart(), x.getEnd()));
//		}
		return mySpans;
	}
	
	private void getPersonModel(Locale language) throws FileNotFoundException{
		if (language.equals(Locale.ENGLISH)){
			MODEL_PERSON = getClass().getResourceAsStream(PERSON_EN);
			//new FileInputStream(PERSON_EN);
		} else if (language.equals(new Locale("es"))){
			MODEL_PERSON = getClass().getResourceAsStream(PERSON_ES);
		} else {
			CHK.CHECK(false, 
					"No NEr models available for the requested language");
		}
	}

	public static void main(String[] args){
		NerOpennlp ner = new NerOpennlp(Locale.ENGLISH);
		String[] text = new String[]{//				
			"The",	"Liberal",	"Party",	"led",	"by",	"Will",	"Hodgman",	
			"have",	"won",	"government",	"in",	"the",	"Tasmanian",	
			"election"				
		};
		List<Span> spans = ner.getSpans(text);
		for (Span x: spans){
			System.out.println(x.getStart() + " " + x.getEnd());
			for (int i=x.getStart(); i< x.getEnd(); i++) {
				System.out.println(text[i]);
			}
		}
	}

//  @Override
//  public Map<String, Integer> getFreqs(String text) {
//    // TODO Auto-generated method stub
//    return null;
//  }


}
