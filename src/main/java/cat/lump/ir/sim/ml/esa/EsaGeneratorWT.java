package cat.lump.ir.sim.ml.esa;

import java.io.File;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;

import cat.lump.ir.lucene.LuceneInterface;
import cat.lump.ir.lucene.engine.WTAnalyzer;

/**A class that allows for passing from a text (collection) 
 * into its ESA vector representation.
 * 
 * Different to EsaGenerator, in this case the analysers, and other
 * pre-processing tools used, are the same as in the rest of *WT
 * classes.
 * 
 * In the near future the classes should converge into one single 
 * class (and potentially some extensions)/
 * @author albarron
 * @version 0.1
 * @since July, 2016
 */
public class EsaGeneratorWT extends EsaGenerator{

  public EsaGeneratorWT(File indexPath, Locale language, int minimumDocFreq) {
    super(indexPath, language, minimumDocFreq);
  }
  
	/**Invokes an instance of the EsaGenerator by loading the index and the 
	 * analyzer for the required language
	 * 
	 * @param indexPath
	 * @param language
	 */
	public EsaGeneratorWT(File indexPath, Locale language){		
		super(indexPath, language);
//		setAnalyzer(language);
//		setIndexPath(indexPath);
//		
//		loadIndex();
	}
	
	
	
	/**Set the Lucene analyzer to use according to the given language
	 * @param myAnalyzer
	 */
	@Override
	protected Analyzer getAnalyzer(Locale lang){
		return new WTAnalyzer(LuceneInterface.LUCENE_VERSION, lang);
		// Properly implement this if we really wan to analyse a language,
		// even if we don't have its corresponding analyser.
//		try {
//			analyzer = AnalyzerFactory.loadAnalyzer(lang);
//		} catch (){
//			analyzer = AnalyzerFactory.loadAnalyzer(new Locale("en"));
//		}
	}

}
