package cat.lump.ie.textprocessing.stopwords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import cat.lump.aq.basics.check.CHK;

/**Abstract class that gives the methods for stopwords acquisition
 * and modification in different languages.
 * 
 * The comparison strategies are case sensitive.
 * @author albarron
 *
 *\\TODO Ull! now nbsp is removed even if it is not in STOP_LIST
 */
public class Stopwords {
	protected List<String> STOP_LIST;

	public Stopwords(Locale language){
		STOP_LIST = new LinkedList<String>();
		loadStopWords(language);		
	}
	
	private void loadStopWords(Locale language) {
		InputStream in = getList(language);
		InputStreamReader is = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(is);
		String read;
		try {
			read = br.readLine();
			while(read != null) {
				//System.out.println("stop: "+ i + " read " + read);
				STOP_LIST.add(read.trim());
				read =br.readLine();
			}
			br.close();
			is.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
   	 //System.out.println("LEctura " +STOP_LIST.get(179) + " size: "+ STOP_LIST.size());

	}	
	
	/**
	 * @return list of stopwords 
	 */
	public List<String> getStopWords(){
		return STOP_LIST;
	}
	
	/**
	 * @param word
	 * @return true if the word is a stopword
	 */
	public boolean isStopword(String word){
		CHK.CHECK_NOT_NULL(word);
		return STOP_LIST.contains(word.trim());
	}
	
	/**
	 * Checks for the vocabulary in the text and returns a copy 
	 * after discarding stopwords. The text is expected to be 
	 * previously tokenized.
	 * @param text
	 * @return text with stopwords discarded
	 */
	public String removeStopwords(String text){
		StringBuffer sb = new StringBuffer();
		String[] words = text.split(" ");
		for (String w : words){
			if (!STOP_LIST.contains(w)){
				sb.append(w)
				  .append(" ");
			}
		}		
		return sb.toString();
	}
	
	public void removeStopwords(List<String> words){
		Iterator<String> itr = words.iterator();
	    while(itr.hasNext()) {
	         String element = itr.next();
	         if (STOP_LIST.contains(element.toLowerCase()) ||     //TODO include in the SW list? 
	        		 element.toLowerCase().equals("nbsp") ||      //Wikipedia issue
	        		 element.toLowerCase().equals("isbn") ||      //Wikipedia issue
	        		 element.length() == 1 ){		         
	        	 itr.remove();
		     }
	    }
	}	
	
	/**
	 * (the stopword files are in src/main/resources)
	 * @param language locale of the desired language
	 * @return	inputstream of the file with the stopwords
	 */
	private InputStream getList(Locale language){
		CHK.CHECK_NOT_NULL(language);
		String model ="";
		if (language.equals(Locale.ENGLISH)){
			model = "en.sw";
		} else if (language.equals(Locale.GERMAN)){
			model = "de.sw";			
		} else if (language.equals(new Locale("el"))){
			model = "el.sw";
		} else if (language.equals(new Locale("es"))){
			model = "es.sw";
		} else if (language.equals(new Locale("bg"))){
			model = "bg.sw";
		} else if (language.equals(new Locale("ca"))){
			model = "ca.sw";
		} else if (language.equals(new Locale("cs"))){
			model = "cs.sw";
		} else if (language.equals(new Locale("eu"))){
			model = "eu.sw";
		} else if (language.equals(new Locale("fr"))){
			model = "fr.sw";
		} else if (language.equals(new Locale("gu"))){
			model = "gu.sw";
		} else if (language.equals(new Locale("hu"))){
			model = "hu.sw";
		} else if (language.equals(new Locale("it"))){
			model = "it.sw";
		} else if (language.equals(new Locale("lt"))){
			model = "lt.sw";
		} else if (language.equals(new Locale("ne"))){
			model = "ne.sw";
		} else if (language.equals(new Locale("ro"))){
			model = "ro.sw";
		} else if (language.equals(new Locale("ru"))){
			model = "ru.sw";
		} else if (language.equals(new Locale("oc"))){
			model = "oc.sw";
		} else if (language.equals(new Locale("pt"))){
			model = "pt.sw";
		} else if (language.equals(new Locale("ar"))){
			model = "ar.sw";
		} else if (language.equals(new Locale("simple"))){
			model = "en.sw";
		} else {
			CHK.CHECK(false, "No stopwords are available for the given language");
		}	
		
		return getClass().getResourceAsStream(model);
	}
	
	
	
//	public void addWord(String word){
//		stopwords.add(word);		
//	}
//	
//	public void addWords(List<String> words){
//		stopwords.addAll(words);		
//	}
//	
//	public void removeWord(String word){
//		stopwords.remove(word);
//	}
//	
//	public void removeStopwords(List<String> words){		
//		Iterator<String> it = words.iterator();
//		while(it.hasNext())
//			
//			if (stopwords.contains(it.next()))
//				it.remove();		
//	}
}
