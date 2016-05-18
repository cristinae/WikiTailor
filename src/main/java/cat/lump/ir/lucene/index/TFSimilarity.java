package cat.lump.ir.lucene.index;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.DefaultSimilarity;

/**
 * An extension to Lucene's default similarity that intends to represent a 
 * document simply by its TFs. The rest of elements included in the original
 * DefaultSimilarity implementation are overriden and set to 1:
 * 
 * http://lucene.apache.org/core/3_5_0/api/all/org/apache/lucene/search/Similarity.html#formula_tf
 * 
 * 
 * @author albarron
 *
 */
public class TFSimilarity extends DefaultSimilarity{

	/** */
	private static final long serialVersionUID = -1737981092596370360L;

	@Override
	public float computeNorm(String arg0, FieldInvertState arg1) {
		return 1;
	}

	@Override
	public float coord(int arg0, int arg1) {
		return 1;
	}

	@Override
	public float idf(int arg0, int arg1) {
		return 1;
	}

	@Override
	public float queryNorm(float arg0) {
		return 1;
	}

	@Override
	public float sloppyFreq(int arg0) {
		return 1;
	}

	//We consider the default tf factor, which is precisely the square root of
	//the frequency: 
	//http://lucene.apache.org/core/3_5_0/api/all/org/apache/lucene/search/Similarity.html#formula_tf
	//
//	@Override
//	public float tf(float arg0) {
//		// TODO Auto-generated method stub
//		return Math.sqrt(arg0);
//	}
	
}
