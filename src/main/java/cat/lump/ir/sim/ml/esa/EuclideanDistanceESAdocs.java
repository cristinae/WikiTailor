package cat.lump.ir.sim.ml.esa;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.sim.ml.esa.EsaVectors;

/**
 * Implementation of EuclideanESA, with two files collections 
 * to compute distances against each other. It is assumed that 
 * the similarities are computed document-wise.
 * 
 * @since     	July 9 2016
 * @author 		albarron
 * @version    0.2.1
 * @see 		cat.lump.ir.sim.ml.esa.esa.EuclideanESA
 */
public class EuclideanDistanceESAdocs extends EuclideanESA{

	/** Includes the path to the documents in A and B. It is assumed that 
	 * all the documents in the two paths should be considered for the 
	 * distance computations.
	 *
	 * @param documentsApath	path to documents A
	 * @param documentsBpath	path to documents B
	 * @param indexPath			path to Lucene's index
	 * @param language			language 
	 * @param overrideObjects	whether objects should be overridden 
	 */
	public EuclideanDistanceESAdocs(File documentsApath,
						File documentsBpath,
						String indexPath,
						Locale language,
						boolean overrideObjects) 
				{
		super(indexPath, language, overrideObjects);
		
		setDocumentsPath(documentsApath, documentsBpath);
		setObjects();			
	}	
	
	/* (non-Javadoc)
	 * @see cat.lump.ir.sim.SimilarityESA#setDocumentsPath(java.io.File, java.io.File)
	 */
	protected void setDocumentsPath(File documentsApath, File documentsBpath){		
		if (documentsApath.equals(documentsBpath))
			log.info("Loading documents from "+ documentsApath);			
		else
			log.info("Loading documents from " + documentsApath +
								" and " + documentsBpath);
		
		if ( documentsApath.isDirectory() && documentsBpath.isDirectory()){
			this.textsA = documentsApath;
			this.textsB = documentsBpath;
		} else {
			exitError("I cannot read some of the input directories");
		}
			
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
	@Override
	protected EsaVectors computeVectors(File documentsPath, String object, String set) throws ClassNotFoundException, IOException{
		File fObj = new File(object);		
		if (objectExists(fObj, set) && overrideObjects==false)
			return (EsaVectors) FileIO.readObject(fObj);
		
		//the texts have to be processed		
		log.info("Processing documents for set " + set);
		EsaVectors vectors = new EsaVectors(esaGen.getIndexDimension()); 
				
		for (String f : FileIO.getFilesRecursively(documentsPath, "txt"))
			try {
				vectors.addVector(esaGen.computeVector(FileIO.fileToString(new File(f))),
									f.replace(".txt", f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		saveObject(fObj, vectors);
		return vectors;			
	}
	
	
}//END CLASS