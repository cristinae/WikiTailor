package cat.lump.ir.sim.cl.clesa;

import java.io.File;
import java.io.IOException;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.sim.cl.clesa.SimilarityCLESA;
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
 * 		index (generated with LuceneIndexer)
 * <li> retrieve and normalise the resulting relevance vectors for 
 * 		d<sub>1</sub> (d<sub>2</sub>)  
 * <li> compute the cosine similarity between the vector representations 
 * 		d<sub>1</sub> and d<sub>2</sub>
 * </ul>
 * 
 * Process in the main method could be considered an example.
 * 
 * @since     April 18 2012
 * @author albarron
 * @version     0.2.1                   
 *  
 * @see  cat.basset.ir.index.LuceneIndexer#
 */
public class SimilarityCLESAdocs extends SimilarityCLESA{

	public SimilarityCLESAdocs(String indexApath, 
								String indexBpath,
								File documentsApath, 
								File documentsBpath, 
								String lanA, 
								String lanB,
								boolean overrideObjects) {
		super(indexApath, indexBpath, documentsApath, documentsBpath, lanA, lanB,
				overrideObjects);
		setDocumentsPath(documentsApath, documentsBpath);
		setObjects();
	}

	@Override
	protected void setDocumentsPath(File documentsApath, File documentsBpath) {
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

	@Override
	protected EsaVectors computeVectors(File documentsPath, String object,
			String set) throws ClassNotFoundException, IOException {
		File fObj = new File(object);		
		if (objectExists(fObj, set) && overrideObjects==false)
			return (EsaVectors) FileIO.readObject(fObj);
		
		//the texts have to be processed		
		log.info("Processing documents for set " + set);
		EsaVectors vectors = new EsaVectors(esaGen.getIndexDimension()); 
				
		for (String f : FileIO.getFilesRecursively(documentsPath, "txt"))
			try {
				if (set.equals("A"))
					vectors.addVector(esaGen.computeVector(FileIO.fileToString(new File(f))),
									f.replace(".txt", f));
				else
					vectors.addVector(esaGenB.computeVector(FileIO.fileToString(new File(f))),
							f.replace(".txt", f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		saveObject(fObj, vectors);
		return vectors;	
	}
	
}//END CLASS