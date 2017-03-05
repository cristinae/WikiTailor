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

  private final double COSINE_IF_MAGNITUDES_ZERO = 0.5;
  
  private final double COSINE_IF_ONE_MAGN_ZERO = 0;
	/**Computes the cosine similarity measure between two vectors
	 * 
	 * sim(v1,v2) = (v1 * v2) / (|v1||v2|)
	 * @param v1
	 * @param v2
	 * @return
	 */
	public double compute(Vector v1, Vector v2) {
	  double dProduct = v1.dotProduct(v2);
	  double magnitude1 = v1.magnitude();
	  double magnitude2 = v2.magnitude();
	  if (magnitude1 == 0 && magnitude2 == 0 ) {
	    return COSINE_IF_MAGNITUDES_ZERO;
	  }
	  
	  if (magnitude1 == 0 || magnitude2 == 0 ) {
      return COSINE_IF_ONE_MAGN_ZERO;
    }
	  
		return  (dProduct / 
				(magnitude1 * magnitude2) );
	}
		

}
