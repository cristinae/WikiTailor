/**
 * 
 */
package cat.lump.ir.sim.ml.esa;

import java.io.File;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;
import cat.lump.aq.basics.io.files.FileIO;

/**
 * We test the operation of EuclideanDistanceESA through its extension 
 * EuclideanDistanceESAdocs.
 * 
 * We check that both the n*m and the pairwise distance computations
 * operate properly for identical and completely unrelated sentences.
 * 
 * @since July 7 2016
 * @version	0.1.0
 * @author albarron
 *
 * @see cat.lump.ir.sim.ml.esa.esa.EuclideanDistanceESA
 * @see cat.lump.ir.sim.ml.esa.esa.EuclideanDistanceESAdocs
 */
public class TestEuclideanDistanceESAdocs {

	public static final String INDEX_PATH = 
			"/media/alberto/SeagateBackupPlusDrive/wikiUPC/indexes/en";
	
	/**Precision for the double results */
	private double delta = 0.0001;
	
	/**Instance of ESA-based distance */
	private EuclideanDistanceESAdocs esa;
	
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
		
		esa = new EuclideanDistanceESAdocs(pathA, 
				pathB, 
				//TODO Use the .properties file
				INDEX_PATH,
				new Locale("en"), 
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
	 * {@link cat.lump.ir.sim.ml.esa.esa.EuclideanDistanceESA#computeMeasures()}.
	 * <br/>
	 * We check that the entire matrix is computed properly
	 */
	@Test
	public void testComputeMeasures() {
		esa.computeMeasures();
		Matrix mat = esa.getMeasuresMatrix();
		
		double[] expected1 = new double[] {0.0, 2.4504};
		double[] expected2 = new double[] {2.4959, 0.7047};
		
		Assert.assertArrayEquals(expected1,	mat.getArray()[0], delta);
		Assert.assertArrayEquals(expected2, mat.getArray()[1], delta);
	}
	
	/**
	 * Test method for 
	 * {@link cat.lump.ir.sim.ml.esa.esa.EuclideanDistanceESA#computePairwiseMeasures()}.
	 * <br/>
	 * We check that the pairwise distance is computed properly.
	 */
	@Test
	public void testComputePairwiseSimilarities() {
		esa.computePairwiseMeasures();
		Matrix mat = esa.getMeasuresMatrix();
		double[] expected = new double[] {0.0, 0.7046809676178419};
		Assert.assertArrayEquals(expected, mat.getArray()[0], delta);		
	}

	/**Check identical sentences
	 * Test method for 
	 * {@link cat.lump.ir.sim.ml.esa.esa.SimilarityESA#getPairwiseSimilarities()}.
	 * <br>
	 * We check that the distance between two identical sentences is 0.
	 */
	@Test
	public void testGetPairwiseMeasuresZero() {
		esa.computePairwiseMeasures();
		double expected = 0.0;
		Assert.assertEquals(expected, esa.getSimilarity("doc.1.txt"), delta);
	}
}
