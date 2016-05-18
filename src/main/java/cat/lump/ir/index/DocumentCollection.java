package cat.lump.ir.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cat.lump.aq.basics.check.CHK;
import cat.lump.ir.retrievalmodels.document.Document;

class DocumentCollection implements Serializable{

	/** Auto generated ID for serialization */
	private static final long serialVersionUID = -2471207535837306346L;
	private Map<String, Document> documents;
	
	public DocumentCollection(){
		documents = new HashMap<String, Document>();
	}	

	/**
	 * Adds a document to the documents' collection
	 * @param doc 
	 * 			document representation
	 * @param id 
	 * 			identifier
	 * @return 
	 * 			true if the document was added; false if another document 
	 * 			existed already with the given id and this one was not added
	 */
	public Boolean addDocument(Document doc, String id){
		//TODO whether this should be indeed an exception
//		if (exists(id)){
//			System.err.println(
//				String.format("A document with id %s is already included", 
//								id));
//			return false;			
//		}
		documents.put(id, doc);
		return true;		
	}
	
	/** 
	 * @param id
	 * @return
	 * 		document representation corresponding to this identifier
	 */
	public Document getDocument(String id){
		CHK.CHECK_NOT_NULL(id);
		//TODO whether this should become an exception
		try {
			return documents.get(id);
		} catch (Exception e){
			System.err.println(
				String.format("No document with id %s exists", 
									id));
			return null;
		}		
	}
	
	/**
	 * Removes the document with the given id
	 * @param id
	 * @return false if it doesn't exist
	 */
	public Boolean removeDocument(int id){
		CHK.CHECK_NOT_NULL(id);
		if (exists(id)){
			documents.remove(id);
			return true;
		} else {
			System.err.println(
				String.format("No document with id %s exists", 
							id));
			return false;
		}		
	}
	
	/**
	 * @return
	 */
	public int length(){
		return documents.size();
	}
	
	/**
	 * @return
	 */
	public Boolean isEmpty(){
		return documents.isEmpty();
		
	}
	
	/**
	 * @param id
	 * @return
	 */
	public Boolean exists(int id){
		return documents.containsKey(id);
	}	

}