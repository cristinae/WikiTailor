package cat.lump.ie.textprocessing.sentence;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import cat.lump.aq.basics.check.CHK;
import cat.lump.ie.textprocessing.Decomposition;
import cat.lump.ie.textprocessing.Span;


/**A class to get the sentences from a given text. 
 * <br/>
 * The currently supported languages are:
 * <ul>
 * <li> English
 * <li> German
 * </ul>
 * 
 * Still other languages can be processed. If the provided locale is 
 * not that of English or German, the English models are used by default.
 *  
 * @author albarron
 * @version 0.1
 * @since Feb 2014
 *
 */
public class SentencesOpennlp implements Decomposition{

	
	private static SentenceDetectorME sentenceDetector;
	
	public SentencesOpennlp(Locale language) {
		InputStream modelIn = null;
		try{
			modelIn = getModel(language);
			SentenceModel model = new SentenceModel(modelIn);
			sentenceDetector = new SentenceDetectorME(model);
		//TODO set this catch properly
		} catch (IOException e) {	e.printStackTrace(); } 
		finally {
			if (modelIn != null) {
				try { modelIn.close();} 
				catch (IOException e){	}
			}
		}
	}	

	private InputStream getModel(Locale language){
		CHK.CHECK_NOT_NULL(language);
		String model ="";		
		System.out.print("Sentence detector loaded for... ");
		if (language.equals(Locale.ENGLISH)){			
			model = "en-sent.bin";
			System.out.println("English");
		} else if (language.equals(Locale.GERMAN)){
			model = "de-sent.bin";
			System.out.println("German");
		} else if (language.equals(new Locale("ar"))){
			model = "ar-sent.bin";
			System.out.println("Arabic");
		} else if (language.equals(new Locale("el"))){
			model ="el-sent.bin";
			System.out.println("Greek");
		}
		
		else {
			//TODO add/create more models, particularly for Spanish
			model = "en-sent.bin";
			System.out.println("English (default)");
		}
		
		return getClass().getResourceAsStream(model);
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
		return Arrays.asList(sentenceDetector.sentDetect(text));
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
		opennlp.tools.util.Span[] spans = sentenceDetector.sentPosDetect(text);
		for (opennlp.tools.util.Span x : spans){
			mySpans.add(new Span(x.getStart(), x.getEnd()));
		}
		return mySpans;		
	}

  @Override
  public Map<String, Integer> getFreqs(String text) {
    // TODO Auto-generated method stub
    return null;
  }

}
