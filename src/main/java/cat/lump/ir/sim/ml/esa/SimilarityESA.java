package cat.lump.ir.sim.ml.esa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.sim.ml.esa.EsaGenerator;
import cat.lump.ir.sim.ml.esa.EsaVectors;

/**
 * Implementation of Explicit Semantic Analysis as described in:
 * <br>
 * Gabrilovich, Evgeniy, and Shaul Markovitch. "Computing Semantic
 * Relatedness Using Wikipedia-based Explicit Semantic Analysis". 
 * 1606–1611. Hyderabad, India, 2007.
 * <br><br>
 * For this version the inverted index is based on Lucene.
 * <br>
 * In brief, the process is as follows. Given two documents 
 * d<sub>1</sub> and d<sub>2</sub>:
 * 
 * <ul>
 * <li> query d<sub>1</sub> (d<sub>2</sub>) to the previously generated 
 * 		index (with LuceneIndexer) 
 * <li> retrieve and normalize the resulting relevance vectors for 
 * 		d<sub>1</sub> (d<sub>2</sub>)  
 * <li> compute the cosine similarity between the vector representations 
 * 		d<sub>1</sub> and d<sub>2</sub>
 * </ul>
 *
 * 
 * @since     April 18 2012
 * @author albarron
 * @version     0.2.1                   
 *  
 * @see  cat.lump.ir.sim.EsaGenerator#
 */
public abstract class SimilarityESA extends Esa{
	
	/**Path to documents A */
	protected File textsA;
	
	/**Path to documents B */
	protected File textsB;
		
	/**Whether previously computed semantic representations
	 * should be discarded (if they exist)	 */
	protected boolean overrideObjects = false;
	
	/**A generator of ESA vectorial representations*/
	protected EsaGenerator esaGen;
	
	/**Identifier for object A*/
	protected String objectA;

	/**Identifier for object B*/
	protected String objectB;
	
	
	

	
	/**
	 * Constructor. It sets the path to the Lucene index, the 
	 * language of the texts we want to compare and if previously 
	 * computed ESA vectors should be discarded. If 
	 * overrideObjects==false, previously-computed vector 
	 * representations of the texts are looked for in the shape of a 
	 * Java object file. If it doesn't exists, it will be created and
	 * saved for future use. 
	 *
	 *TODO move the parameterization to a properties file
	 * @param indexPath		path to Lucene's index
	 * @param lan			language to work with
	 * @param overrideObjects	if previously computed vectors will be discarded
	 */
	public SimilarityESA(String indexPath, Locale lan, Boolean overrideObjects){
		super();
		//invoke ESA generator with a given index path and language
		esaGen = new EsaGenerator(indexPath, lan);				
		this.overrideObjects = overrideObjects;		
	}
	
	/** A method that loads the texts in collections A and B.
	 * @param documentsApath
	 * @param documentsBpath
	 */
	protected abstract void setDocumentsPath(File documentsApath, 
											File documentsBpath);
		
	/**Set the name of the resulting vector objects	 */
	protected void setObjects(){
		objectA = textsA + FileIO.separator + "esaRepresentationA.obj";			
		objectB = textsB + FileIO.separator + "esaRepresentationB.obj";
	}			
	
	
	/**Compute the characteristic vectors for dataset A 
	 * @throws IOException 
	 * @throws ClassNotFoundException */
	public void computeVectorsA() throws ClassNotFoundException, IOException{
		esaVectorsA = computeVectors(textsA, objectA, "A");
	}
	
	/**Compute the characteristic vectors for dataset B 
	 * @throws IOException 
	 * @throws ClassNotFoundException */
	public void computeVectorsB() throws ClassNotFoundException, IOException{
		esaVectorsB = computeVectors(textsB, objectB, "B");
	}
	
	/**Computes the vectors for the texts in the given set. If a 
	 * previously computed object exists (and overrideObjects==False), 
	 * it just loads the previously computed vectors.
	 *
	 * @param documentsPath		path to the documents
	 * @param object			name of the (previously generated object)
	 * @param set				whether we are processing A or B
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	protected abstract EsaVectors computeVectors(File documentsPath, String object, String set) throws ClassNotFoundException, IOException;
	
	/**Saves a textual representation into an object file
	 * @param objFile
	 * @param esa
	 * @throws FileNotFoundException 
	 * @throws IOException
	 */
	protected void saveObject(File objFile, EsaVectors esa) throws FileNotFoundException, IOException{
		FileIO.writeObject(esa, objFile);
	}
	
	/**Checks whether the vector-representation object exists. Note that if 
	 * override_object==True, it will claim no objects exists in order to 
	 * compute it again.
	 * @param object	object to check
	 * @param id		flag to report
	 * @return			true if exists and override_object==false
	 */
	protected boolean objectExists(File object, String id){
		if ( overrideObjects == false && object.exists() ){			
			log.info("Object with vector representations exists already for " 
					+ id );
			return true;
		}		
		return false;
	}

}//END CLASS