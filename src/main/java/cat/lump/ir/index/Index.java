package cat.lump.ir.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.structure.InvIndexContainer;

public class Index implements Serializable {
	
	
	/** Auto generated ID for serialization  */
	private static final long serialVersionUID = -5978433950960863024L;

	protected Map<String, Double> magnitudes;
	
	/** Inverted index used to compute similarities */
	private InvIndexContainer invIndex;
	
	/** Record of the document ids contained in the index */
	private Set<String> ids;
	
	//TODO faltan getters del vocabulario, magnitudes y demas
	
	public Index(){
		invIndex = new InvIndexContainer();
		ids = new HashSet<String>();
		magnitudes = new HashMap<String, Double>();
	}
	
	
	/**
	 * Add a new document with the given weights to the index.
	 * If a document with the given id already exists, it triggers 
	 * an error. 
	 * @param text
	 * @param id
	 */
	public void add(Map<String, Double> text, String id){		
		CHK.CHECK_NOT_NULL(text);
		CHK.CHECK_NOT_NULL(id);
			
		CHK.CHECK(!ids.contains(id), 
			String.format("A document with id %s already exists", id));
		
		int magnitude = 0;		
		ids.add(id);
		
		for (String tok : text.keySet()){
			magnitude += Math.pow(text.get(tok), 2);
			if (!invIndex.tBox.containsKey(tok))
				invIndex.tBox.put(tok, new HashMap<String, Double>());
			invIndex.tBox.get(tok).put(id, (Double) text.get(tok));
		}	
		magnitudes.put(id, Math.sqrt(magnitude));
	}
	
	
	
//
//	for (int i = 0; i < representation.length(); i++)
//	{
//		this_magnitude = 0.0;
//		Map<String, Double> ng = representation.getFragment(i).getWeighted(
//				type);
//		for (String tok : ng.keySet())
//		{
//			if (tok.equals(""))
//				continue;
//			this_magnitude += Math.pow(ng.get(tok), 2);
//			if (!invIndex.tBox.containsKey(tok))
//				invIndex.tBox.put(tok, new HashMap<Integer, Double>());
//			invIndex.tBox.get(tok).put(i, (Double) ng.get(tok));
//		}
//		textMagnitudes.put(i, Math.sqrt(this_magnitude));
//	}
	
	
	
	/**
	 * Remove an existing index from the index. If no document 
	 * exists with the given id, it triggers an error.
	 * @param id
	 */
	public void remove(int id){
		CHK.CHECK_NOT_NULL(id);		
		CHK.CHECK(ids.contains(id), 
				String.format("No document with id %s already exists", id));
		
		//iterate over the entire vocabulary to remove the entry 
		//this takes O(v) where v is the size of the vocabulary.
		for (String tok : invIndex.tBox.keySet()){
			if (invIndex.tBox.get(tok).containsKey(id)){
				invIndex.tBox.get(tok).remove(id);
				if (invIndex.tBox.get(tok).size() == 0){
					invIndex.tBox.remove(tok);
				}					
			}
		}
		ids.remove(id);		
	}
	
	/**
	 * @return true if the index is empty.
	 */
	public boolean isEmpty()
	{
		if (invIndex.tBox.size() == 0)
		{
			return true;
		} else {
			return false;
		}		
	}
	
	/**
	 * @param token 
	 * 				relevant document
	 * @return 
	 * 				documents where the token appears, together with the
	 * 				associated weight.
	 */
	public Map<String, Double> getDocuments(String token)
	{
		CHK.CHECK_NOT_NULL(token);
		
		if (! invIndex.tBox.containsKey(token))
		{
			System.err.println("The index does not contain an entry for this token");
			return null;
		}
		return invIndex.tBox.get(token);
	}
	
	/**
	 * @return ids of all the documents in the index
	 */
	public Set<String> getDocuments(){
		return ids;
	}
	
	/**
	 * @param id
	 * @return magnitude of the given document
	 */
	public double getMagnitude(String id){
		CHK.CHECK_NOT_NULL(id);
		CHK.CHECK(magnitudes.containsKey(id), 
				"No document with this id exists in the collection");
		return magnitudes.get(id);
	}
	
	/**
	 * @return entire vocabulary in the index; null if emtpy
	 */
	public Set<String> getVocabulary()
	{
		if (vocabularySize() == 0)
		{
			return null;
		}
		return invIndex.tBox.keySet();
	}
	
	/**
	 * @return size of the vocabulary in the index
	 */
	public int vocabularySize()
	{
		return invIndex.tBox.size();
	}
	
	/**
	 * @return amount of documents included in the index
	 */
	public int documentNumber()
	{
		return ids.size();
	}
	
//	/**
//	 * Loads the inverted index from the text as well as the corresponding 
//	 * magnitudes
//	 */
//	public void generateInvIndex()
//	{
//		
//		double this_magnitude;
//
//		for (int i = 0; i < representation.length(); i++)
//		{
//			this_magnitude = 0.0;
//			Map<String, Double> ng = representation.getFragment(i).getWeighted(
//					type);
//			for (String tok : ng.keySet())
//			{
//				if (tok.equals(""))
//					continue;
//				this_magnitude += Math.pow(ng.get(tok), 2);
//				if (!invIndex.tBox.containsKey(tok))
//					invIndex.tBox.put(tok, new HashMap<Integer, Double>());
//				invIndex.tBox.get(tok).put(i, (Double) ng.get(tok));
//			}
//			textMagnitudes.put(i, Math.sqrt(this_magnitude));
//		}
//	}
	
	//saves and loads an index
	
	//contains the interfaces to that index
	
	//includes both tf and tf-idf weighting

}
