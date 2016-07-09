/**
 * 
 */
package cat.lump.ir.sim.ml.esa;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.sim.ml.esa.SimilarityESA;
import cat.lump.ir.sim.ml.esa.SimilarityESAsent;

/**
 * In this junit we test the operation of SimilarityESA through its 
 * extension SimilarityESAsent.
 * 
 * We check that both the n*m and the pairwise similarity computation
 * operate properly for identical and completely unrelated sentences.
 * 
 * @since November 29 2013
 * @version	0.1.0
 * @author albarron
 *
 * @see cat.lump.ir.sim.ml.esa.esa.SimilarityESA
 * @see cat.lump.ir.sim.ml.esa.esa.SimilarityESAsent
 */
public class TestSimilarityESAsent {

	/**Precision for the double results */
	private double delta = 0.0001;
	
	/**Instance of ESA-based similarity */
	private SimilarityESA esa;
	
	
	/**Temporal document with the sentences to compare */
	private File document = new File("f.txt.tmp"); 
	
	/**
	 * Generates a temporal file with tab-separated texts to compare 
	 * against each other. ESA is invoked and the representative vectors are generated.
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//generation of a document
		
		StringBuffer sb = new StringBuffer();
		sb.append("Maya Devi Temple is an ancient Buddhist temple situated at the UNESCO World Heritage Site of Lumbini, Nepal.\t")
		  .append("Maya Devi Temple is an ancient Buddhist temple situated at the UNESCO World Heritage Site of Lumbini, Nepal.\n")
		  .append("The battles on the Eastern Front constituted the largest military confrontation in history\t")
		  .append("American football is a sport played by two teams of eleven players on a rectangular field 120 yards long by 53.33 yards wide with goalposts at each end.\n")
		  ;
		//TODO see what happens when a text is indeed empty. 
		//Should we trigger an error? What is the similarity?
//		  .append("this\t")
//		  .append("that\n");
		FileIO.stringToFile(document, sb.toString(), false);
		
		esa = new SimilarityESAsent(
				document, 
				TestSimilarityESAdocs.INDEX_PATH,
				"en",
				true);
		esa.computeVectorsA();
		esa.computeVectorsB();
	}

	/**We delete the temporal file and close the instance of ESA.*/
	@After
	public void tearDown()  {
		FileIO.deleteFile(document);
		FileIO.deleteFile(new File(document.toString() + ".left.esarep.obj"));
		FileIO.deleteFile(new File(document.toString() + ".right.esarep.obj"));
	}

	/**
	 * Test method for 
	 * {@link cat.lump.ir.sim.ml.esa.esa.SimilarityESA#computeMeasures()}.
	 * <br/>
	 * We check that the entire matrix is computed properly
	 */
	@Test
	public void testComputeSimilarities() {
		esa.computeMeasures();
		Matrix mat = esa.getMeasuresMatrix();
		Assert.assertArrayEquals(
				new double[]{1,0}, 
				mat.getArray()[0], 
				delta);
		Assert.assertArrayEquals(
				new double[]{0,0}, 
				mat.getArray()[1], 
				delta);
	}
	
	/**
	 * Test method for 
	 * {@link cat.lump.ir.sim.ml.esa.esa.SimilarityESA#computePairwiseMeasures()}.
	 * <br/>
	 * We check that the pairwise similarity is computed properly.
	 */
	@Test
	public void testComputePairwiseSimilarities() {
		esa.computePairwiseMeasures();
		Matrix mat = esa.getMeasuresMatrix();
		Assert.assertArrayEquals(
				new double[]{1,0}, 
				mat.getArray()[0], 
				delta);		
	}

	/**
	 * Test method for 
	 * {@link cat.lump.ir.sim.ml.esa.esa.SimilarityESA#getPairwiseSimilarities()}.
	 * <br>
	 * We check that the similarity between two identical sentences is 1.
	 */
	@Test
	public void testGetPairwiseSimilaritiesOne() {
		esa.computePairwiseMeasures();
		Assert.assertEquals(1, esa.getSimilarity("0"), delta);
	}

	/**Check unrelated sentences
	 * Test method for 
	 * {@link cat.lump.ir.sim.ml.esa.esa.SimilarityESA#getPairwiseSimilarities()}.
	 * <br>
	 * We check that the similarity between two unrelated sentences is 0.
	 */
	@Test
	public void testGetPairwiseSimilaritiesZero() {
		esa.computePairwiseMeasures();
		//esa.displaySimilarities();
		Assert.assertEquals(0, esa.getSimilarity("1"), delta);
	}

}
