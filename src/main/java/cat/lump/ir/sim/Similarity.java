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

	/**Compute the similarity between all the texts in the collection */
	public void computeSimilarities();
	
	/**Compute the similarity between two specific texts
	 * @param idA
	 * @param idB
	 * @return
	 */
	public double computeSimilarity(String idA, String idB);
	
	/**Get the (previously computed) similarity between the two ids 
	 * @param idA
	 * @param idB
	 * @return
	 */
	public double getSimilarity(String idA, String idB);
	
	/**
	 * @return a nested map with all the computed similarities.
	 */
	public Map<String, Map<String, Double>> getSimilarities();
	
	/**Prints a matrix including all the similarities  */
	public void displaySimilarities();	

}