package cat.lump.aq.basics.io.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CsvFoolReaderTest {

	private final File csvFile = new File("tmp.csv");
	
	/**
	 * Create a temporal csv file
	 */
	@Before
	public void setUp()  {
		String contents = "###field1#field2#field3\n"
				+ "11#12#13\n"
				+ "21#22#23\n"
				+ "#Some comment\n"
				+ "31#32#33\n";
		try {
			Writer writer = new BufferedWriter(
								new FileWriter(
									csvFile));
			writer.append(contents);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Remove the temporal csv file
	 */
	@After
	public void tearDown() {		
		csvFile.delete();
	}

	/**
	 * The matrix is properly produced from a file with 
	 * headers, comments and separated by "#".
	 */
	@Test
	public void testCsvFileToMatrix() {
		String[][] expected = new String[][]{
				{"11","12","13"}, 
				{"21","22","23"},
				{"31","32","33"}};
		
		String[][] actual = 
				CsvFoolReader.csv2matrix(csvFile, "#");
		for (String[] line : actual){
			for (String field : line){
				System.out.print(field+" ");
			}
			System.out.println();
		}
		
		Assert.assertArrayEquals(expected, actual);
	}
	
	@Test
	public void testCsvFileToMatrixDefault() {
		String[][] expected = new String[][]{
				{"11#12#13"}, 
				{"21#22#23"},
				{"31#32#33"}};
		
		String[][] actual = CsvFoolReader.csv2matrix(csvFile);
		for (String[] line : actual){
			for (String field : line){
				System.out.print(field+" ");
			}
			System.out.println();
		}
		
		Assert.assertArrayEquals(expected, actual);
	}

}
