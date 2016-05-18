package cat.lump.ir.weighting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.basics.structure.ir.TermFrequencyTuple;


/**
 * A class to compute and store a simple term frequency. It includes the methods
 * to add and remove terms, get the sorted list of TermFrequencyTuples, and just 
 * a subset of them
 * 
 * @author albarron
 *
 */
public class TermFrequency {

	private Map<String, TermFrequencyTuple> tf;
	
	private static LumpLogger logger = 
			new LumpLogger (TermFrequency.class.getSimpleName());
	
	/** Invokes the class with an empty list of term tuples */
	public TermFrequency() {
		this(new ArrayList<TermFrequencyTuple>());
	}
	
	/**
	 * Invokes the class with an existing empty list of term tuples
	 * 
	 * @param terms
	 */
	public TermFrequency(List<TermFrequencyTuple> terms){
		CHK.CHECK_NOT_NULL(terms);
		tf = new HashMap<String, TermFrequencyTuple>();
		for (TermFrequencyTuple  t : terms){
			tf.put(t.getTerm(), t);
		}			
	}
	
	/**
	 * @param term
	 * @return true if the term exists
	 */
	public boolean existTerm(String term){
		CHK.CHECK_NOT_NULL(term);
		return tf.containsKey(term);
	}
	
	/**
	 * Add a term into the collection. The frequency of the term is increased 
	 * if it already exists. Otherwise, it is set to 1.
	 *  
	 * @param term a non-null, non-empty string
	 */
	public void addTerm(String term){
		CHK.CHECK_NOT_NULL(term);
		CHK.CHECK(! term.isEmpty(), "The term should not be empty");		
		calculateFrequency(term);
	}
	
	/**
	 * Remove the given term (warning if the term does not exist)
	 * @param term non-null string
	 */
	public void removeTerm(String term){
		CHK.CHECK_NOT_NULL(term);	
		if (tf.containsKey(term)){
			tf.remove(term);
		} else {
			logger.warn(String.format("The term %s does not exist", term));
		}			
	}
	
	/**
	 * Add these terms into the collection.
	 * @param terms
	 */
	public void addTerms(List<String> terms){
		for (String t : terms)
			addTerm(t);
	}
	
	/**
	 * Remove these terms into the collection.
	 * @param terms
	 */
	public void removeTerms(List<String> terms){
		for (String t : terms)
			removeTerm(t);
	}
	
	/**
	 * @param term
	 * @return the term and its weight
	 */
	public TermFrequencyTuple getTerm(String term){
		CHK.CHECK_NOT_NULL(term);
		CHK.CHECK(tf.containsKey(term), "The requested term does not exist");
		
		return tf.get(term);
//		TermFrequencyTuple tft;
//		if (tf.containsKey(term)){
//			tft = tf.get(term);
//		} else {
//			tft = null;
//			logger.warn("The term is not included; null returned");
//		}
//		return tft;		
	}
		
	
	/**
	 * @return The list of term frequency tuples. This tuples are sorted by 
	 *          frequency in descending order.
	 */
	public List<TermFrequencyTuple> getAll(){		
		List<TermFrequencyTuple> tuples = 
				new ArrayList<TermFrequencyTuple>(tf.values());
		Comparator<TermFrequencyTuple> highToLow = 
										new Comparator<TermFrequencyTuple>() {

			@Override
			public int compare(TermFrequencyTuple o1, TermFrequencyTuple o2)
			{
				return o2.compareTo(o1);
			}
		};
		Collections.sort(tuples, highToLow);
		return tuples;		
	}
	
	/**
	 * Subset of terms with the highest tf up to top% or up to max
	 * Note that not the top% is returned sometimes but a little bit more. It 
	 * happens because the collection is increased until the last considered
	 * frequency drops. 
	 * 
	 * @param top percentage of the terms to get (0 < top <=100)
	 * @param maximum number of terms allowed in the vocabulary (max > 1)
	 * 		max=-1 indicates no maximum value
	 * @return subset of the terms with the highest tf up to top%
	 */
	public List<TermFrequencyTuple> getTop(int top, int max){
		CHK.CHECK_NOT_NULL(top);
		CHK.CHECK_NOT_NULL(max);
		CHK.CHECK(top > 0 && top <=100, "top is a percentage, i.e, 0<k<=100");
		CHK.CHECK(max > 1 || max == -1, "there must be more than one element in the vocabulary, max>1");
		
		List<TermFrequencyTuple> subset = getAll();
		int index = (subset.size() * top) / 100 -1;
		if (max < index && max != -1) index = max;
		int lastFreq = subset.get(index).getFrequency();
		boolean goAhead = ++index < subset.size();
		while (goAhead) {			
			if (subset.get(index).getFrequency() == lastFreq) {				
				goAhead = ++index < subset.size();						
			} else {
				goAhead = false;
			}
		}
		
		return subset.subList(0, index);

//		//CHECK This was the original implementation by Josu for completing the
		//list with the equal frequencies
//		//in order to include all the terms with the last frequency
//		if (t != null) {
//			int lastFreq = t.getFrequency();
//			boolean goAhead = index < tft.size();
//			while (goAhead) {
//				t = tft.get(index);
//				if (t.getFrequency() == lastFreq) {
//					subset.add(t);					
//					goAhead = ++index < tft.size();						
//				}
//				else {
//					goAhead = false;
//				}
//			}
//		}		
//		return subset;

	}
	

	/**
	 * Note that not the top% is returned sometimes but a little bit more. It 
	 * happens because the collection is increased until the last considered
	 * frequency drops. 
	 * 
	 * @param top percentage of the terms to get (0 < top <=100)
	 * @return subset of the terms with the highest tf up to top% plus
	 * 		   the terms that appear in the title of the category 
	 */
	public List<TermFrequencyTuple> getTopPlus(int top, int max, List<String> catTerms){
		
		List<TermFrequencyTuple> topList = getTop(top, max);
		List<TermFrequencyTuple> allList = getAll();
		
		TermFrequencyTuple tuple;
		int index;
		for (String catTerm : catTerms) {
			tuple = tf.get(catTerm);
			index = allList.indexOf(tuple);
			if (index > topList.size() ){
				topList.add(tuple);
			}
		}

		return topList;

	}
	
	
	
	public int size(){
		return tf.size();
	}
	
	
	/**
	 * Updates the term frequency tuples processing a list of new terms.
	 * 
	 * @param terms
	 *            The list of new terms
	 * @param tf
	 *            The map of term frequency tuples. This tuples are indexed by
	 *            the terms value.
	 */
	private void calculateFrequency(String term){
		if (tf.containsKey(term)) {
			tf.get(term).increment();
		} else {			
			tf.put(term, new TermFrequencyTuple(term, 1));
		}
	}
	
	
	
	
	
//	HashMap<String, TermFrequencyTuple> tf = 
//	new HashMap<String, TermFrequencyTuple>();
//
//for (Page page : articles)
//{
//ArrayList<String> terms;
//try
//{
//terms = (ArrayList<String>) getTerms(page);
//} catch (WikiApiException e)
//{
//logger.error(String.format("I can't read the page %d", 
//	page.getPageId()));
//continue;
//}
//calculateFrequency(terms, tf);
//}
//ArrayList<TermFrequencyTuple> tuples = 
//new ArrayList<TermFrequencyTuple>(tf.values());
//Comparator<TermFrequencyTuple> highToLow = new Comparator<TermFrequencyTuple>() {
//
//@Override
//public int compare(TermFrequencyTuple o1, TermFrequencyTuple o2)
//{
//return o2.compareTo(o1);
//}
//};
//Collections.sort(tuples, highToLow);
//return tuples;

}
