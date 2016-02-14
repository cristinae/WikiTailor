package cat.lump.ir.index;

//import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cat.lump.aq.basics.structure.standard.MapUtil;

public class RankingTest {

	private final double DELTA = 0.0001;
	
	private Ranking ranking;
	
	@Before
	public void setUp() throws Exception {
		ranking = new Ranking();
		ranking.add("362", 0.1515);
		ranking.add("474", 1.0);
		ranking.add("500", 0.0385);		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGet() {
		Assert.assertEquals(0.1515, ranking.get("362"), DELTA);
	}

	@Test
	public void testGetRank() {
		Map<String, Double> expected = new HashMap<String, Double>();
		expected.put("362", 0.1515);
		expected.put("474", 1.0);
		expected.put("500", 0.0385);
		expected = MapUtil.sortByValueInverse(expected);
		Assert.assertEquals(expected, ranking.getRank());
	}

	@Test
	public void testGetTopK() {
		Map<String, Double> expected = new HashMap<String, Double>();
		expected.put("362", 0.1515);
		expected.put("474", 1.0);		
		expected = MapUtil.sortByValueInverse(expected);
		Assert.assertEquals(expected, ranking.getTopK(2));
	}

	@Test
	public void testGetOverThreshold() {
		Map<String, Double> expected = new HashMap<String, Double>();
		expected.put("474", 1.0);
		Assert.assertEquals(expected, ranking.getOverThreshold(0.5));
	}

}
