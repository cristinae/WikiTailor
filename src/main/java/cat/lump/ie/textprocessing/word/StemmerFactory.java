package cat.lump.ie.textprocessing.word;

import java.util.Locale;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.danishStemmer;
import org.tartarus.snowball.ext.dutchStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import org.tartarus.snowball.ext.finnishStemmer;
import org.tartarus.snowball.ext.frenchStemmer;
import org.tartarus.snowball.ext.germanStemmer;
import org.tartarus.snowball.ext.hungarianStemmer;
import org.tartarus.snowball.ext.italianStemmer;
import org.tartarus.snowball.ext.norwegianStemmer;
import org.tartarus.snowball.ext.portugueseStemmer;
import org.tartarus.snowball.ext.romanianStemmer;
import org.tartarus.snowball.ext.russianStemmer;
import org.tartarus.snowball.ext.spanishStemmer;
import org.tartarus.snowball.ext.swedishStemmer;
import org.tartarus.snowball.ext.turkishStemmer;
import org.tartarus.snowball.ext.contributed.basqueStemmer;
import org.tartarus.snowball.ext.contributed.catalanStemmer;
import org.tartarus.snowball.ext.contributed.czechStemmer;
import org.tartarus.snowball.ext.contributed.lithuanianStemmer;

import cat.lump.aq.basics.check.CHK;

/**
 * Factory that allows for getting a stemmer for the required
 * language (if available)
 * 
 * @author albarron
 * @since 4 Mar, 2014
 * @version 0.1
 * @see org.tartarus.snowball.SnowballStemmer
 */
public class StemmerFactory {
	
	/**
	 * @param language
	 * @return The Snowball stemmer for the required language; error if not available.
	 */
	public static SnowballStemmer loadStemmer(Locale language){
		CHK.CHECK_NOT_NULL(language);
		String l = language.getLanguage();
		
		if (l.equals("eu"))			return new basqueStemmer();
		else if (l.equals("ca"))	return new catalanStemmer();
		else if (l.equals("cs"))	return new czechStemmer();   //TODO check
		else if (l.equals("oc"))	return new catalanStemmer();	//TODO we need an Occitan stemmer
		else if (l.equals("da"))	return new danishStemmer();
		else if (l.equals("nl"))	return new dutchStemmer();
		else if (l.equals("en"))	return new englishStemmer();
		else if (l.equals("fi"))	return new finnishStemmer();	//TODO check
		else if (l.equals("fr"))	return new frenchStemmer();
		else if (l.equals("de"))	return new germanStemmer();
		else if (l.equals("hu"))	return new hungarianStemmer(); 	//TODO check
		else if (l.equals("it"))	return new italianStemmer();
		else if (l.equals("lt"))	return new lithuanianStemmer();  //TODO check
		else if (l.equals("no"))	return new norwegianStemmer();
		else if (l.equals("pt"))	return new portugueseStemmer();
		else if (l.equals("ro"))	return new romanianStemmer();
		else if (l.equals("ru"))	return new russianStemmer();
		else if (l.equals("es"))	return new spanishStemmer();
		else if (l.equals("se"))	return new swedishStemmer();	//TODO check
		else if (l.equals("tk"))	return new turkishStemmer();	//TODO check
		
		//The null is enough and the check is done somewhere else (better to
		//combine with Lucene stemmer)
		//CHK.CHECK(false, "No Snowball stemmer is available for language "+
		//language.getDisplayLanguage() );
		return null;	
	}
	

}
