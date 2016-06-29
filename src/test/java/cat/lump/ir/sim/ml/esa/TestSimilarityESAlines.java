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
import cat.lump.ir.sim.ml.esa.SimilarityESAlines;

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
public class TestSimilarityESAlines {

	/**Precision for the double results */
	private double delta = 0.0001;
	
	/**Instance of ESA-based similarity */
	private SimilarityESA esa;
	
	
	
//	/**Temporal document with the sentences to compare */

	private File documentA = new File("a.txt");
	private File documentB = new File("b.txt");
	
	/**
	 * Generates a temporal file with tab-separated texts to compare 
	 * against each other. ESA is invoked and the representative vectors are generated.
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//generation of the documents
			
		FileIO.stringToFile(
				documentA, 
				"Maya Devi Temple is an ancient Buddhist temple situated at the UNESCO World Heritage Site of Lumbini, Nepal.\n"
				+ "The battles on the Eastern Front constituted the largest military confrontation in history\n", 
				false);
		
		FileIO.stringToFile(
				documentB, 
				"Maya Devi Temple is an ancient Buddhist temple situated at the UNESCO World Heritage Site of Lumbini, Nepal.\n"
				+ "American football is a sport played by two teams of eleven players on a rectangular field 120 yards long by 53.33 yards wide with goalposts at each end.\n", 
				false);
	
		esa = new SimilarityESAlines(
				documentA,
				documentB,
				TestSimilarityESAdocs.INDEX_PATH, 
				"en",
				true);
		esa.computeVectorsA();
		esa.computeVectorsB();
	}

	/**We delete the temporal file and close the instance of ESA.*/
	@After
	public void tearDown()  {
		FileIO.deleteFile(documentA);
		FileIO.deleteFile(documentB);
		FileIO.deleteFile(new File (documentA.toString() + ".esarep.obj"));
		FileIO.deleteFile(new File (documentB.toString() + ".esarep.obj"));		
	}

	/**
	 * Test method for 
	 * {@link cat.lump.ir.sim.ml.esa.esa.SimilarityESA#computeSimilarities()}.
	 * <br/>
	 * We check that the entire matrix is computed properly
	 */
	@Test
	public void testComputeSimilarities() {
		esa.computeSimilarities();
		Matrix mat = esa.getSimilaritiesMatrix();
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
	 * {@link cat.lump.ir.sim.ml.esa.esa.SimilarityESA#computePairwiseSimilarities()}.
	 * <br/>
	 * We check that the pairwise similarity is computed properly.
	 */
	@Test
	public void testComputePairwiseSimilarities() {
		esa.computePairwiseSimilarities();
		Matrix mat = esa.getSimilaritiesMatrix();
		Assert.assertArrayEquals(
				new double[]{1,0}, 
				mat.getArray()[0], 
				delta);		
	}

	/**Check identical sentences
	 * Test method for 
	 * {@link cat.lump.ir.sim.ml.esa.esa.SimilarityESA#getPairwiseSimilarities()}.
	 * <br>
	 * We check that the similarity between two identical sentences is 1.
	 */
	@Test
	public void testGetPairwiseSimilaritiesOne() {
		esa.computePairwiseSimilarities();
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
		esa.computePairwiseSimilarities();
		Assert.assertEquals(0, esa.getSimilarity("1"), delta);
	}

}
