package cat.lump.ir.retrievalmodels.document;

import java.io.Serializable;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import cat.lump.aq.basics.check.CHK;

public class Dictionary implements Serializable{
	
	/** Auto generated ID for serialization */
	private static final long serialVersionUID = 1L;

	/**Dictionary that links terms to their numerical representation */
	private final BidiMap<String, Integer> dictionary;
	
	/**Internal record for the term ids' numerical identifier */
	private int LAST_TERM_ID;
	
	public Dictionary(){
		dictionary = new DualHashBidiMap<String, Integer>();
		LAST_TERM_ID = 0;
	}
	
	/**
	 * Add the term to the dictionary (if it was not there yet). 
	 * @param term
	 * @return numerical id associated to the term
	 */
	public int addString(String term){
		CHK.CHECK_NOT_NULL(term);
		if (! exist(term)){
			dictionary.put(term, LAST_TERM_ID++);
		}		
		return dictionary.get(term);
	}
	
	/**
	 * @param id
	 * @return term associated to the id; null if does not exist
	 */
	public String getString(int id){
		CHK.CHECK_NOT_NULL(id);
		return dictionary.getKey(id);
	}
	
	/**
	 * @param term
	 * @return numerical id for the term; null if does not exist
	 */
	public int getId(String term){
		CHK.CHECK_NOT_NULL(term);
		return dictionary.get(term);
	}
	
	/**
	 * @param term
	 * @return true if the term exists
	 */
	public boolean exist(String term){
		return dictionary.containsKey(term);
	}
	
}
