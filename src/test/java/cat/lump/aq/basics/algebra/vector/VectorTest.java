package cat.lump.aq.basics.algebra.vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author albarron
 *
 */
public class VectorTest {

	/**Precision for the float operations*/
	private final float delta = (float) 0.0001;	

	private Vector v;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {	
		//System.out.println("int he setup");
	}	

	/**
	 * Test method for {@link cat.basset.vector.Vector#sum(float[])}.
	 */
	@Test
	public void testAdd() {	
		v = new Vector(new float[]{1,2,3});
		Assert.assertArrayEquals(
			new float[]{2,3,4}, 
			v.add(new float[]{1,1,1}), 
			delta);
	}

	/**
	 * Test method for {@link cat.basset.vector.Vector#addEquals(float[])}.
	 */
	@Test
	public void testAddEquals() {
		v = new Vector(new float[]{1,2,3});
		v.addEquals(new float[]{1,1,1});
		
		Assert.assertArrayEquals(
				new float[]{2,3,4}, 
				v.get(),
				delta);		
	}

	/**
	 * Test method for {@link cat.basset.vector.Vector#dotProduct(float[])}.
	 */
	@Test
	public void testDotProduct() {
		v = new Vector(new float[]{1,2,3});
				
		Assert.assertEquals(
				14, 
				v.dotProduct(new float[]{1,2,3}), 
				delta);		
	}

	/**
	 * Test method for {@link cat.basset.vector.Vector#times(float)}.
	 */
	@Test
	public void testTimes() {
		v = new Vector(new float[]{1,2,3});
		Assert.assertArrayEquals(
				new float[]{2,4,6}, 
				v.times(2), 
				delta);
	}

	/**
	 * Test method for {@link cat.basset.vector.Vector#timesEquals(float)}.
	 */
	@Test
	public void testTimesEquals() {
		Vector v = new Vector(new float[]{1,2,3});
		v.timesEquals(2);
		
		Assert.assertArrayEquals(
				new float[]{2,4,6}, 
				v.get(), 
				delta);
	}

	/**
	 * Test method for {@link cat.basset.vector.Vector#divide(float)}.
	 */
	@Test
	public void testDivide() {
		Vector v = new Vector(new float[]{2,4,6});
		Assert.assertArrayEquals(
			new float[]{1,2,3}, 
			v.divide(2), 
			delta);
	}

	/**
	 * Test method for {@link cat.basset.vector.Vector#divideEquals(float)}.
	 */
	@Test
	public void testDivideEquals() {
		Vector v = new Vector(new float[]{2,4,6});
		v.divideEquals(2);
		
		Assert.assertArrayEquals(
				new float[]{1,2,3}, 
				v.get(), 
				delta);
	}

	/**
	 * Test method for {@link cat.basset.vector.Vector#magnitude()}.
	 */
	@Test
	public void testMagnitude() {
		Vector v = new Vector(new float[]{1,2,3});
		Assert.assertEquals(
			3.7416, 
			v.magnitude(),
			delta);
	}

	/**
	 * Test method for {@link cat.basset.vector.Vector#max()}.
	 */
	@Test
	public void testMax() {
		Vector v = new Vector(new float[]{-5,2,3});
		Assert.assertEquals(
			3, 
			v.max(),
			delta);
	}

	/**
	 * Test method for {@link cat.basset.vector.Vector#argmax()}.
	 */
	@Test
	public void testArgmax() {
		Vector v = new Vector(new float[]{-5,2,3});
		Assert.assertEquals(
			2, 
			v.argmax());
	}

	/**
	 * Test method for {@link cat.basset.vector.Vector#min()}.
	 */
	@Test
	public void testMin() {
		Vector v = new Vector(new float[]{-5,2,30});
		Assert.assertEquals(-5,	v.min(), delta);
	}

	/**
	 * Test method for {@link cat.basset.vector.Vector#argmin()}.
	 */
	@Test
	public void testArgmin() {
		Vector v = new Vector(new float[]{-5,2,3});		
		Assert.assertEquals(0,	v.argmin());
	}

	/**
	 * Test method for {@link cat.basset.vector.Vector#get()}.
	 */
	@Test
	public void testGet() {
		Vector v = new Vector(new float[]{1,2,3});
		
		Assert.assertArrayEquals(
			new float[]{1,2,3}, 
			v.get(), 
			delta);
		
		Assert.assertEquals(2, v.get(1), delta);		
	}
}
