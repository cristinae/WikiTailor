/**
 * Created on 23 Jan 2012<br><br>
 * Software being developed by lbarron
 */

package cat.lump.ir.retrievalmodels.distance;

import cat.lump.aq.basics.algebra.vector.Vector;
import cat.lump.ir.retrievalmodels.similarity.SimilarityMeasure;



/**
 * A class to compute the Euclidean distance between two vectors.
 * 
 * @author albarron
 * @version 0.1
 * @since July 9 2016
 * TODO should this be moved to the vector class?
 *
 */
public class VectorEuclideanDistance implements SimilarityMeasure{

	/**Computes the Euclidean Distance between two vectors
	 * 
	 * dis(v1,v2) = sqrt( sum(v1i,v2i)^2) )
	 * @param v1
	 * @param v2
	 * @return
	 */
	public double compute(Vector v1, Vector v2) {
		return  v1.euclideanDistance(v2);
	}
		

}
