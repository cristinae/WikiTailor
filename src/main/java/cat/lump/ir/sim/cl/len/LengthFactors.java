package cat.lump.ir.sim.cl.len;

import java.util.HashMap;
import java.util.Map;

/**It includes the default values for a bunch of language pairs, namely:
 * <ul>
 * <li> ar-en
 * <li> cz-en
 * <li> de-en
 * <li> en-ar	      
 * <li> en-cz
 * <li> en-de
 * <li> en-es
 * <li> en-fr
 * <li> en-ru	      	      
 * <li> es-en
 * <li> fr-en
 * <li> ru-en
 * </ul>
 * 
 * The parameters were estimated by txell and Cristina (ar-related) on 
 * different corpora, including:
 * 
 *  <ul> 
 *  <li> commoncrawl.wmt2013  
 *  <li> CzEng.v1.0
 *  <li> el_periodico
 *  <li> europarl.v6
 *  <li> europarl.v7
 *  <li> FAUST_D4.2
 *  <li> French_treebank
 *  <li> newscommentary.v8
 *  <li> news.shuffled.en.conll.gz
 *  <li> news.shuffled.fr.conll.gz
 *  <li> patents
 *  <li> Romanian_treebank
 *  <li> UNdoc.2000
 *  <li> wiki-titles.ru-en
 *  <li> wmt10
 *  <li> wmt10.select
 *  </ul>
 *  
 * 
 * @author albarron
 *
 */
public class LengthFactors {
  
	/**Stores the default mean and sd values for different language pairs.
	 * The values are only for test purposes. They should be confirmed */
	private static final double means_sd[][] = {
	  {0.904 , 0.371},  //ar-en
    {1.085 , 0.273},	//cz-en
    {0.961 , 0.463},	//de-en
    {1.229 , 0.591},  //en-ar
    {0.972 , 0.245},	//en-cz
    {1.176 , 0.926},	//en-de
    {1.133 , 0.415},	//en-es
    {1.158 , 0.411},	//en-fr
    {1.157 , 0.678},	//en-ru
    {1.040 , 0.577},  //en-tr
    {0.926 , 0.441},	//es-en
    {0.914 , 0.313},	//fr-en
    {1.069 , 0.668},	//ru-en	
    // From Barron-Cedeno, et al. Plagiarism Detection across Distant Language Pairs.  
    {1.157 , 0.235},  //es-eu  
    {1.056 , 0.545},  //en-eu  // TODO idem
    {1.005 , 0.087},	//ca-es  // From El Periodico
    {1.002 , 0.094},	//es-ca  // FIXME It seems it should be closer to the inverse ca-es
    // this last factor is intended for comparing texts in the same language
    {1.112 , 0.564},  //tr-en
    {1.000 , 0.317}
  };	
	
	/**Stores the index to access the different default values */
	private static final Map<String, Integer> index = new HashMap<String, Integer>(){
		private static final long serialVersionUID = 1L;
		{
		  put("ar-en", 0);
			put("cz-en", 1);
			put("de-en", 2);
			put("en-ar", 3);
			put("en-cz", 4);
			put("en-de", 5);
			put("en-es", 6);
			put("en-fr", 7);
			put("en-ru", 8);
			put("en-tr", 9);
			put("es-en", 10);
			put("fr-en", 11);
			put("ru-en", 12);
			put("es-eu", 13);
			put("en-eu", 14);
			put("ca-es", 15);
			put("es-ca", 16);
			put("tr-en", 17);
			put("en-en", 18);
			put("es-es", 18);
			put("ar-ar", 18);
		}};
     	
	/**
	 * Get the mean for the desired pair. 
	 * @param src source language
	 * @param trg target language
	 * @return mean(src-->trg); -1 if the pair is not available
	 */
	public static double getMean(String src, String trg){
		return getMean(src+"-"+trg);
	}

	/**
	 * Get the mean for the desired pair. 
	 * @param langpair src-target languages
	 * @return mean(src-->trg); -1 if the pair is not available
	 */
	public static double getMean(String langpair){
		return getValue(langpair, 0);
	}
	
	/**
	 * Get the standard deviation for the desired pair. 
	 * @param src source language
	 * @param trg target language
	 * @return sd(src-->trg) -1 if the pair is not available
	 */
	public static double getSD(String src, String trg){
		return getSD(src+"-"+trg);		
	}
	
	/**
	 * Get the standard deviation for the desired pair. 
	 * @param lang-pair source-target languages
	 * @return sd(src-->trg) -1 if the pair is not available
	 */
	public static double getSD(String langpair){
		return getValue(langpair, 1);		
	}
	
	/**Obtain either mean (j=0) or sigma (j=1) for the language pair
	 * @param langpair
	 * @param j 0 for mean; 1 for sigma
	 * @return either mean of sigma value
	 */
	private static double getValue(String langpair, int j){
		double m = -1.0;
		if (index.containsKey(langpair))
			m = means_sd[index.get(langpair)][j];
		else
			System.out.println("ERROR: no entry for language pair "+ langpair);		
		return m;	
	}
	
	/**
	 * Retrieve the set of language pairs for which default parameters are
	 * available.
	 * @return An array with the pairs 
	 */
	public static String[] getAvailablePairs(){
		return index.keySet().toArray(new String[index.keySet().size()]);
	}
	
	/**
	 * Check whether parameters for the desired language pair are available
	 * @param langpair
	 * @return true if the language pair is available
	 */
	public static Boolean hasPair(String langpair){
		if (index.containsKey(langpair))	return true;
		else 								return false;		
	}
}