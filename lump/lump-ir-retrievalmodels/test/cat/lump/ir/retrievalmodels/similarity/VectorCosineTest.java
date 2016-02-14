package cat.lump.ir.retrievalmodels.similarity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cat.lump.aq.basics.algebra.vector.Vector;

public class VectorCosineTest {
		
	/**Precision for the float operations*/
	private final double delta = 0.0001;

	private SimilarityMeasure cosine;
	
	@Before
	public void setUp() throws Exception {
		cosine = new VectorCosine();
	}
	
	@Test
	public void testSimilarityEquals() {
		Assert.assertEquals(
				1, 
				cosine.compute(
					new Vector(new float[]{1,1,1}), 
					new Vector(new float[]{1,1,1})),
					delta);
	}
	
	@Test
	public void testSimilarityZero() {
		Assert.assertEquals(
				0, 
				cosine.compute(
					new Vector(new float[]{1,0,1}), 
					new Vector(new float[]{0,1,0})),
					delta);		
	}
	
	@Test
	public void testSimilarityHalf() {
		Assert.assertEquals(
				0.5, 
				cosine.compute(
					new Vector(new float[]{1,0,1,0}), 
					new Vector(new float[]{1,1,0,0})),
					delta);		
	}

}