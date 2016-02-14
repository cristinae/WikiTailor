/**
 * Created on 23 Jan 2012<br><br>
 * Software being developed by lbarron
 */

package cat.lump.ir.retrievalmodels.similarity;

import cat.lump.aq.basics.algebra.vector.Vector;



/**
 * A class to compute the cosine similarity between two vectors.
 * 
 * @author albarron
 * @version 0.1
 * @since November 22 2013
 * TODO should this be moved to the vector class?
 *
 */
public class VectorCosine implements SimilarityMeasure{

	/**Computes the cosine similarity measure between two vectors
	 * 
	 * sim(v1,v2) = (v1 * v2) / (|v1||v2|)
	 * @param v1
	 * @param v2
	 * @return
	 */
	public double compute(Vector v1, Vector v2) {
		return  (v1.dotProduct(v2) / 
				(v1.magnitude() * v2.magnitude()) );
	}
		

}
