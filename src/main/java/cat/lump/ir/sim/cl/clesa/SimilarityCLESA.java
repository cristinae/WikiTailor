package cat.lump.ir.sim.cl.clesa;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.sim.ml.esa.EsaGenerator;
import cat.lump.ir.sim.ml.esa.EsaVectors;
import cat.lump.ir.sim.ml.esa.SimilarityESA;

/**
 * 
 * Implementation of Cross-Language Explicit Semantic Analysis,
 * an extension of explicit semantic analysis proposed by:
 * <br>
 * Potthast, Stein and Anderka. A Wikipedia-Based Multilingual 
 * Retrieval Model. In Advances in Information Retrieval. 30th 
 * European Conference on IR Research (ECIR 08), LNCS(4956), 
 * pages 522-530, 2008. Springer. ISBN 978-3-540-78645-0
 * <br/><br/>
 *  
 * @since     April 18 2012
 * @author albarron
 * @version     0.2.2                   
 *  
 * @see  cat.lump.ir.sim.Esa# 
 */
public abstract class SimilarityCLESA extends SimilarityESA{
	
	/**A generator of ESA vector representations for language B*/
	EsaGenerator esaGenB;
	
	/**
	 * @param documentsApath
	 * @param documentsBpath
	 * @param indexApath
	 * @param indexBpath
	 * @param lanA
	 * @param lanB
	 * @param overrideObjects
	 */
	public SimilarityCLESA(String indexApath,
							String indexBpath,
							File documentsApath,
							File documentsBpath,							
							String lanA,
							String lanB,
							boolean overrideObjects
							) {
		super(indexApath, new Locale(lanA), overrideObjects);

		//esaGen is in SimilarityESA and has the generator for the source language
		esaGenB =  new EsaGenerator(new File(indexBpath), new Locale(lanB));
		if (esaGen.getIndexDimension() != esaGenB.getIndexDimension())
			log.errorEnd("The source and target languages indexes should have"
					+ "the same amount of (comparable) documents");
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
				//@attention note the if for either generator index A or B 
				if (set.equals("A"))
					vectors.addVector(esaGen.computeVector(FileIO.fileToString(new File(f))),
									f.replace(".txt", f));
				else
					vectors.addVector(esaGenB.computeVector(FileIO.fileToString(new File(f))),
							f.replace(".txt", f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		FileIO.writeObject(vectors, fObj);
		return vectors;	
	}

}//END CLASS