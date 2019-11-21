package cat.lump.ir.lucene.index;

import org.apache.lucene.search.similarities.ClassicSimilarity;

/**
 * An extension to Lucene's ClassicSimilarity that intends to represent a 
 * document simply by its TFs. This was TFIDF. So, in order to ignore 
 * IDF, it is overriden to 1
 * 
 * @author albarron
 *
 */
public class TFSimilarity extends ClassicSimilarity{
	@Override
	public float idf(long docFreq, long docCount) {
		return 1;
	}
	
}



//public class TFSimilarity extends DefaultSimilarity{
////public class TFSimilarity extends ClassicSimilarity{
//
//	/** */
//	private static final long serialVersionUID = -1737981092596370360L;
//
//	@Override
//	public float computeNorm(String arg0, FieldInvertState arg1) {
//		return 1;
//	}
//
//	@Override
//	public float coord(int arg0, int arg1) {
//		return 1;
//	}
//
//	@Override
//	public float idf(int arg0, int arg1) {
//		return 1;
//	}
//
//	@Override
//	public float queryNorm(float arg0) {
//		return 1;
//	}
//
//	@Override
//	public float sloppyFreq(int arg0) {
//		return 1;
//	}
//
//	//We consider the default tf factor, which is precisely the square root of
//	//the frequency: 
//	//http://lucene.apache.org/core/3_5_0/api/all/org/apache/lucene/search/Similarity.html#formula_tf
//	//
////	@Override
////	public float tf(float arg0) {
////		// TODO Auto-generated method stub
////		return Math.sqrt(arg0);
////	}
//	
//}
