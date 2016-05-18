package cat.lump.ir.sim.cl.len;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cat.lump.ir.sim.cl.len.LengthModelLearn;

public class LengthModelLearnTest extends TestAbstract{	

	@Test
	public void testLearn() {
		LengthModelLearn lm = new LengthModelLearn(SRC_FILE, TRG_FILE);
		lm.learn();
		assertEquals(1.229658, lm.getMu(), DELTA);
		assertEquals(0.074232, lm.getSigma(), DELTA);
	}
	
	@Test
	public void testLearnEquals() {
		LengthModelLearn lm = new LengthModelLearn(SRC_FILE, SRC_FILE);
		lm.learn();
//		System.out.println(lm.getMu());
//		System.out.println(lm.getSigma());
		assertEquals(1, lm.getMu(), DELTA);
		assertEquals(0, lm.getSigma(), DELTA);
	}

}