package cat.lump.ir.index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.structure.standard.MapUtil;

/**
 * <p>A class that implements a ranking of identifiers (documents) and 
 * their relevance.
 * </p>
 * 
 * <p>
 * Adding and removing entries is possible. It is able to generate 
 * (partial) ranks according to top number of desired entries or 
 * threshold.
 * </p> 
 * 
 * @version 0.2
 * @author albarron
 *
 */
public class Ranking {
	
	/** The map which actually contains the ranking */
	private Map<String, Double> data;
		
	public Ranking(){
		reset();		
	}
	
	/**
	 * Insert a new document to the ranking
	 * @param id
	 * @param relevance
	 */
	public void add(String id, double relevance)
	{
		CHK.CHECK_NOT_NULL(id);
		CHK.CHECK_NOT_NULL(relevance);
		CHK.CHECK(relevance >= 0, 
				"Negative relevances are not permitted");
		data.put(id, relevance);
	}
	
	/**
	 * @param id
	 * @return relevance of a given document
	 */
	public double get(String id){
		CHK.CHECK_NOT_NULL(id);
		try 
		{
		return data.get(id);
		} catch (Exception e){
			throw new IllegalArgumentException();
		}		
	}
	
	/**
	 * Remove a document from the ranking
	 * @param id
	 */
	public void remove(String id){
		CHK.CHECK_NOT_NULL(id);
		try {
			data.remove(id);
		} catch (Exception e){
			throw new IllegalArgumentException();
		}
	}

	/**Start a new ranking */
	public void reset(){
		data = new HashMap<String, Double>();
	}
	
	@Override
	public String toString(){
		sort();
		StringBuffer sb = new StringBuffer();
		for (String key : data.keySet())
		{
			sb.append(key)
			  .append("\t")
			  .append(data.get(key))
			  .append("\n");
		}
		return sb.toString();
	}	
	
	public int size(){
            return data.size();
        }

            /**
	 * @return	entire ranking bottom-up
	 */
	public Map<String, Double> getInverseRank(){
		sortbottomfirst();
		return data;		
	}
        
        
        /**
	 * @return	entire ranking
	 */
	public Map<String, Double> getRank(){
		sort();
		return data;		
	}
	
	/**
	 * @param k 
	 * 			number of elements to return
	 * @return
	 * 			the top k documents in the ranking (or the entire
	 * 			ranking if k > number of documents.
	 */
	public Map<String, Double> getTopK(int k){
		CHK.CHECK_NOT_NULL(k);
		//sort();		
		if (k >= data.size())
		{
			return getRank();
		}
		
		Map<String, Double> topK = new HashMap<String, Double>();
		int i = 0;
		
		Iterator<Entry<String, Double>> entries = 
				 			getRank().entrySet().iterator();
		while (i < k) 			
		{
			Entry<String, Double> thisEntry = 
					(Entry<String, Double>) entries.next();
			topK.put(thisEntry.getKey(), 
					thisEntry.getValue());
			i++;
		}		
		return topK;		
	}
        
        
        /**
	 * @param k 
	 * 			number of elements to return
	 * @return
	 * 			the top k documents in the ranking (or the entire
	 * 			ranking if k > number of documents.
	 */
	public Map<String, Double> getBottomK(int k){
		CHK.CHECK_NOT_NULL(k);
		//sort();		
		if (k >= data.size())
		{
			return getInverseRank();
		}
		
		Map<String, Double> bottomK = new HashMap<String, Double>();
		int i = 0;
		
		Iterator<Entry<String, Double>> entries = 
				 			getInverseRank().entrySet().iterator();
		while (i < k) 			
		{
			Entry<String, Double> thisEntry = 
					(Entry<String, Double>) entries.next();
			bottomK.put(thisEntry.getKey(), 
					thisEntry.getValue());
			i++;
		}		
		return bottomK;		
	}
	
	/** 
	 * @param threshold 
	 * 			minimum required relevance
	 * @return 
	 * 			sorted entries with a relevance over a given threshold;
	 * 			empty of no entry surpasses it. 
	 */
	public Map<String, Double> getOverThreshold(double threshold){
		CHK.CHECK(threshold >=0, "No negative thresholds allowed");
		boolean higher = true;
		Map<String, Double> topK = new HashMap<String, Double>();
		
		sort();
		Iterator<Entry<String, Double>> entries = 
				getRank().entrySet().iterator();
		
		while (higher && entries.hasNext()) 			
		{
			Entry<String, Double> thisEntry = 
					(Entry<String, Double>) entries.next();
			if (thisEntry.getValue() >= threshold)
			{
				topK.put(thisEntry.getKey(), thisEntry.getValue());  
			} else {
				higher = false;
					
			}
		}
		return topK;		
	}
	
	/** Sort the entries in inverse order: top to bottom */
	private void sort(){
		data = MapUtil.sortByValueInverse(data);
	}
        
	/** Sort the entries order: bottom to top*/
	private void sortbottomfirst(){
		data = MapUtil.sortByValue(data);
	}        
}