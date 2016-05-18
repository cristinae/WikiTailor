package cat.lump.ir.sim.cl.len;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;

import cat.lump.ir.sim.cl.len.LengthFactors;
import cat.lump.ir.sim.cl.len.LengthModelEstimate;

public class LengthModelEstimateTest extends TestAbstract{

	private LengthModelEstimate le;
	
	private final String PAIR = "en-es";
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		le = new LengthModelEstimate();
		le.setFiles(SRC_FILE, TRG_FILE);
		le.setMuSigma(LengthFactors.getMean(PAIR), 
				LengthFactors.getSD(PAIR)
				);
	}
	


	@Test
	public void testEstimate() {
		double[] expecteds = {
			0.92864652,	0.99981421,	0.94489560,	0.99852408,	0.92013260
			};		
		le.estimate();

//		le.display();
//		le.displayVerbose();

		assertArrayEquals(expecteds, le.estimate(), DELTA);
	}

	@Test
	public void testEstimateMatrix() {
		double[][] expecteds = {
			{0.92864652, 0.99636046, 0.85435134, 0.94824714, 0.85435134},	
			{0.89850519, 0.99981421, 0.81304832, 0.92222434, 0.81304832},	
			{0.98524922, 0.96531300, 0.94489560, 0.99310498, 0.94489560},	
			{0.99419589, 0.94994201, 0.96458522, 0.99852408, 0.96458522},	
			{0.97163428, 0.97851763, 0.92013260, 0.98323960, 0.92013260}				
		};		
		
		double[][] actuals = le.estimateMatrix();
	
		for (int i = 0; i < actuals.length; i++) {
			assertArrayEquals(expecteds[i], actuals[i], DELTA);
//			for (int j = 0; j < res[0].length; j++) {
//				System.out.print(res[i][j] + "\t");
//			}
//			System.out.println();
		}
	}

}
