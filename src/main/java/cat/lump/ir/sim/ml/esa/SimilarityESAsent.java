package cat.lump.ir.sim.ml.esa;

import java.io.File;
import java.io.IOException;

import cat.lump.ir.sim.ml.esa.SimilarityESAlines;

/**
 * Implementation of SimilarityESAlines that works over one single 
 * tab-separated document in which the left-side text-line has 
 * to be compared against the right-side one.
 * 
 * @since		November 29 2013
 * @author		albarron
 * @version    0.1.0                   
 *  
 * @see  cat.lump.ir.sim/SimilarityESAlines#
 */
public class SimilarityESAsent extends SimilarityESAlines{
	
	/**the side of the text to process: 0=left; 1=right. */
	private int side;
	
	/**It invokes the superclass SimilarityESAlines, but it deceives 
	 * it by claiming two docs exist which are indeed the same one.
	 * @param doc
	 * @param indexPath
	 * @param overrideObjects
	 * @param analyzer
	 */
	public SimilarityESAsent(File doc,			
			String indexPath,			 
			String language,
			Boolean overrideObjects) {

		super(doc, doc, indexPath, language, overrideObjects);
		
	}
	
	/* (non-Javadoc)
	 * @see cat.lump.ir.sim.SimilarityESAlines#setObjects()
	 */
	@Override
	protected void setObjects(){
		objectA = textsA + ".left.esarep.obj";		
		objectB = textsB + ".right.esarep.obj";
	}
	
	/**Method used to do some preprocessing to the input text.
	 * In this configuration the text is split between left and right
	 * at tab.
	 * @param text
	 * @return left- or right-hand-side text.
	 */
	/* (non-Javadoc)
	 * @see cat.lump.ir.sim.SimilarityESAlines#processLine(java.lang.String)
	 */
	@Override
	protected String processLine(String text){
		return text.split("\t")[side];
	}
	

	/* (non-Javadoc)
	 * @see cat.lump.ir.sim.SimilarityESAlines#computeVectorsA()
	 */
	@Override
	public void computeVectorsA() throws ClassNotFoundException, IOException {
		side = 0;
		super.computeVectorsA();
	}

	/* (non-Javadoc)
	 * @see cat.lump.ir.sim.SimilarityESAlines#computeVectorsB()
	 */
	@Override
	public void computeVectorsB() throws ClassNotFoundException, IOException {
		side = 1;
		super.computeVectorsB();
	}	
	
}