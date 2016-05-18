/**
 * Created on 8 May 2013<br><br>
 * Software being developed by lbarron
 */

package cat.lump.ir.sim.ml.esa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.sim.ml.esa.EsaVectors;
import cat.lump.ir.sim.ml.esa.SimilarityESA;


/**
 * Extension of SimilarityESA, with two files to compute similarities 
 * against each other. It is assumed that the two single files have 
 * to be computed line-wise.
 * 
 * @since  May 8 2013
 * @author albarron
 * @version 0.1.1
 * @see cat.lump.ir.sim.ml.esa.esa.SimilarityESA
 */
public class SimilarityESAlines extends SimilarityESA{

	/**Given two files with independent sentences, process line-wise 
	 * similarities. 
	 * @param docA first text file
	 * @param docB second text file
	 * @param indexPath path to the Lucene index
	 * @param overrideObjects	whether vector similarities should be 
	 * 							computed from scratch
	 * @param analyzer	the analyzer to consider (for various languages)
	 */
	public SimilarityESAlines(File docA, 
					File docB, 
					String indexPath,
					String language,
					Boolean overrideObjects) {
		super(indexPath, "en", overrideObjects);
		setDocumentsPath(docA, docB);		
		setObjects();			
	}

	/* (non-Javadoc)
	 * @see cat.lump.ir.sim.SimilarityESA#setDocumentsPath(java.io.File, java.io.File)
	 */
	protected void setDocumentsPath(File fileA, File fileB){
		if (fileA.equals(fileB))
			log.info("Loading documents from "+ fileA);			
		else
			log.info("Loading documents from " + fileA +
								" and " + fileB);
		
		if ( fileA.isFile() && fileB.isFile()){
			textsA = fileA;
			textsB = fileB;
		} else {
			exitError("I cannot read some of the input files");
		}
			
	}
	
	@Override
	protected void setObjects(){
		objectA = textsA + ".esarep.obj";		
		objectB = textsB + ".esarep.obj";
	}

	/* (non-Javadoc)
	 * @see cat.lump.ir.sim.SimilarityESA#computeVectors(java.io.File, java.lang.String, java.lang.String)
	 */
	//here we process all the lined from a file
	@Override
	protected EsaVectors computeVectors(File file, String object, String set) throws ClassNotFoundException, IOException{
		File fObj = new File(object);		
		if (objectExists(fObj, set) && overrideObjects==false)
			return (EsaVectors) FileIO.readObject(fObj);
		
		//the texts have to be processed		
		log.info("Processing documents for set " + set);
		EsaVectors vectors = new EsaVectors(esaGen.getIndexDimension());	
		
		int i = 0;
		try {
			for(Scanner sc = new Scanner(new FileReader(file)); sc.hasNext(); )
				vectors.addVector(
						esaGen.computeVector(processLine(sc.nextLine())), 
						String.valueOf(i++));
			FileIO.writeObject(vectors, fObj);
		} catch (FileNotFoundException e) {
			e.printStackTrace();			
		}
		
		saveObject(fObj, vectors);
		return vectors;			
	}
	
	/**Method used to do some preprocessing to the input text.
	 * In this configuration nothing is done, but in others (e.g. in 
	 * SimilarityESAsent.java), some modifications are carried out
	 * @param text
	 * @return
	 */	
	protected String processLine(String text){
		return text;
	}	


}