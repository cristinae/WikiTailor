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
import cat.lump.ir.sim.ml.esa.SimilarityESAdocs;

/**
 * We test the operation of SimilarityESA through its extension 
 * SimilarityESAdocs.
 * 
 * We check that both the n*m and the pairwise similarity computation
 * operate properly for identical and completely unrelated sentences.
 * 
 * @since December 2 2013
 * @version	0.1.0
 * @author albarron
 *
 * @see cat.lump.ir.sim.ml.esa.esa.SimilarityESA
 * @see cat.lump.ir.sim.ml.esa.esa.SimilarityESAdocs
 */
public class TestSimilarityESAdocs {

	public static final String INDEX_PATH = 
			"/media/alberto/SeagateBackupPlusDrive/wikiUPC/indexes/en";
	
	/**Precision for the double results */
	private double delta = 0.0001;
	
	/**Instance of ESA-based similarity */
	private SimilarityESAdocs esa;
	
	/**
	 * Generates the temporal file inside of corpus directories files.A 
	 * and files.B ESA is invoked and the representative vectors are 
	 * generated.
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		//generation of the documents in the folders.
		File pathA = new File("files.A");
		File pathB = new File("files.B");
		FileIO.createDir(pathA);
		FileIO.stringToFile(
				new File(pathA + FileIO.separator + "doc.1.txt"), 
				"Maya Devi Temple is an ancient Buddhist temple situated at the UNESCO World Heritage Site of Lumbini, Nepal.", 
				false);
		
		FileIO.stringToFile(
				new File(pathA + FileIO.separator + "doc.2.txt"), 
				"The battles on the Eastern Front constituted the largest military confrontation in history", 
				false);
				
		FileIO.createDir(pathB);
		
		FileIO.stringToFile(
				new File(pathB + FileIO.separator + "doc.1.txt"), 
				"Maya Devi Temple is an ancient Buddhist temple situated at the UNESCO World Heritage Site of Lumbini, Nepal.", 
				false);
		
		FileIO.stringToFile(
				new File(pathB + FileIO.separator + "doc.2.txt"), 
				"American football is a sport played by two teams of eleven players on a rectangular field 120 yards long by 53.33 yards wide with goalposts at each end.", 
				false);	
		
		esa = new SimilarityESAdocs(pathA, 
				pathB, 
				//TODO Use the .properties file
				INDEX_PATH,
				"en", 
				true);
		
		esa.computeVectorsA();
		esa.computeVectorsB();
	}

	/**We delete the temporal directories and close the instance of ESA.*/
	@After
	public void tearDown()  {
		FileIO.deleteDir(new File("files.A"));
		FileIO.deleteDir(new File("files.B"));		
		//esa.close();
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
		Assert.assertEquals(1, esa.getSimilarity("doc.1.txt"), delta);
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
		Assert.assertEquals(0, esa.getSimilarity("doc.2.txt"), delta);
	}

}
