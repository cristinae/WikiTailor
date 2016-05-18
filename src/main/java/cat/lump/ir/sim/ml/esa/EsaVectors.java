package cat.lump.ir.sim.ml.esa;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.algebra.matrix.DynamicMatrixOfVectors;
import cat.lump.aq.basics.algebra.vector.Vector;

/**Set of vector representation of a set of texts. It relates a vector and the
 * text it represents
 *  
 *  * 
 * @author albarron
 * @version 0.1
 * @since April 30 2012 
 */
public class EsaVectors implements Serializable{

	/**Serial necessary to save the resulting objects */
	private static final long serialVersionUID = 5230679436297014868L;


	/**A bidirectional maps containing the identifiers of the documents 
	 * and the slot they occupy in the matrix of vectors*/
	private BidiMap vectorIndex;
	
	
	/**The matrix containing the document's representative vectors */
	private DynamicMatrixOfVectors mVectors; 

	
	/**At invocation time the index and initial matrix of vectors is
	 * generated
	 * @param dimension
	 */
	public EsaVectors(int dimension){
		vectorIndex = new DualHashBidiMap();
		mVectors = new DynamicMatrixOfVectors();
	}	
	
	//if at some stage we consider the number of documents 
	//to include in the matrix in advance
//	public EsaVectors(int n_texts, int dimension){
//		vector_index = new DualHashBidiMap();
//		m_vectors = new DynamicMatrixOfDouble(dimension);
//	}
	
	
	/**
	 * @return length of the vectors' matrix
	 */
	public int length(){
		return mVectors.length();			
	}
	
	/**
	 * @param key
	 * @return true if the vectorial representation includes
	 * a vector for document with given key.
	 */
	public boolean containsKey(String key){
		return vectorIndex.containsKey(key);
	}
	
	/**
	 * @param key
	 * @return index for the given document; -1 if it does not exist
	 */
	public int getIndex(String key){
		if (!containsKey(key)){
			return -1;
		}
		return (Integer) vectorIndex.get(key);
	}
	
	/**
	 * @param i
	 * @return id for the document represented at slot i
	 */
	public String getId(int i){
		return (String) vectorIndex.getKey(i);
	}
	

	/**
	 * @return identifiers of the represented documents
	 */
	@SuppressWarnings("unchecked")
	public Set<String> keySet(){
		return  vectorIndex.keySet();
	}

	/**Obtain the vector corresponding to this id
	 * (null if it does not exist)
	 * @param id
	 * @return vector characterizing the document; null if does not exist
	 */
	public Vector getVector(String id){
		return getVector((Integer) vectorIndex.get(id));			
	}		

	/**Obtain the vector corresponding to this slot
	 * @param i
	 * @return vector characterizing the document; null if does not exist
	 */
	public Vector getVector(int i){
		if (i > mVectors.length()){
			return null;
		}
		return mVectors.get(i);
	}

	/**
	 * @return entire matrix with all the vectors.
	 */	
	public Vector[] getAllVectors(){
		return mVectors.getMatrix();
	}

	/**Add a new vector into the matrix. The final identifier
	 * is going to be the file name in case of full path
	 * @param vector
	 * @param id
	 */
	public void addVector(Vector vector, String id){
		if (id.contains(FileIO.separator))
			id = id.substring(id.lastIndexOf("/") +1);

		vectorIndex.put(id, 
						mVectors.add(vector));
	}

	/**Very unlikely to happen, but a previously filled vector
	 * could be modified.
	 * @param vector
	 * @param id
	 */	
	public void modifyVector(Vector vector, String id){		
		try{				
			mVectors.put(vector, (Integer) vectorIndex.get(id));
		} catch(Exception e){								
			System.err.println("No document ID exists in the index");
		}			
	}		
	
}