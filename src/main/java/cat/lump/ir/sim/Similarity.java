package cat.lump.ir.sim;

import java.util.Map;

import cat.lump.aq.basics.log.LumpLogger;


/**
 * Interface with the minimum required methods to code a similarity 
 * model.  
 *
 * @author albarron
 * @since April 13 2012
 *
 */
public interface Similarity{
	
	/**Logger for the application*/
	static LumpLogger log = new LumpLogger("Similarity");

	/**
	 * Compute the measures (similarities/distances) between all the texts in 
	 * the collection.
	 */
	public void computeMeasures();
	
	/**Compute the measure (similarity/distance) between two specific texts.
	 * @param idA
	 * @param idB
	 * @return
	 */
	public double computeMeasure(String idA, String idB);
	
	/**
	 * Get the (previously computed) measure (similarity/distance) between 
	 * the two ids. 
	 * @param idA
	 * @param idB
	 * @return
	 */
	public double getMeasure(String idA, String idB);
	
	/**
	 * @return a nested map with all the computed similarities.
	 */
	public Map<String, Map<String, Double>> getMeasures();
	
	/**Prints a matrix including all the similarities  */
	public void displayMeasures();	

}