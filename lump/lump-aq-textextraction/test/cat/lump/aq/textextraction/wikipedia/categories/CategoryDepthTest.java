package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import cat.lump.aq.basics.io.files.FileIO;

public class CategoryDepthTest {

	@Test
	public void testGetDepthLinear() {
		String t =  "1 1.000000\n" +
				    "2 0.833333\n" +
					"3 0.694737\n" +
					"4 0.608108\n" +
					"5 0.727273\n" +
					"6 0.500000\n" +
					"7 0.666667\n";

		File f = new File(System.getProperty("user.dir"),"tmp.txt");
				
		try {
			FileIO.stringToFile(f, t, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CategoryDepth cd1 = new CategoryDepth(f, 0.7, 0, 50);
		Assert.assertEquals(3, cd1.getDepthLinear());
		//This one is the reason why we chose splines
		CategoryDepth cd2 = new CategoryDepth(f, 0.6, 3, 50);
		Assert.assertEquals(6, cd2.getDepthLinear());
		CategoryDepth cd3 = new CategoryDepth(f, 0.5, 3, 50);
		Assert.assertEquals(6, cd3.getDepthLinear());
		
		f.delete();

	}

	
	@Test
	public void testGetDepthSplines() {
		String t =  "1 1.000000\n" +
				    "2 0.833333\n" +
					"3 0.694737\n" +
					"4 0.608108\n" +
					"5 0.727273\n" +
					"6 0.500000\n" +
					"7 0.666667\n";

		File f = new File(System.getProperty("user.dir"),"tmp.txt");
				
		try {
			FileIO.stringToFile(f, t, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CategoryDepth cd1 = new CategoryDepth(f, 0.7, 0, 50);
		Assert.assertEquals(3, cd1.getDepthSplines());
		CategoryDepth cd2 = new CategoryDepth(f, 0.6, 3, 50);
		Assert.assertEquals(4, cd2.getDepthSplines());
		CategoryDepth cd3 = new CategoryDepth(f, 0.5, 3, 50);
		Assert.assertEquals(6, cd3.getDepthSplines());
		
		f.delete();

	}

}
