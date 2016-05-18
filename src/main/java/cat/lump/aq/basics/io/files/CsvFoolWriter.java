/** 
 * 
 * Created on 4 Dec 2011<br><br>
 * Software being developed by lbarron
 */

package cat.lump.aq.basics.io.files;

import java.io.File;
import java.io.IOException;

import cat.lump.aq.basics.check.CHK;


/**
 * <p>
 * A simple reader for CSV files. It can handle files with comments
 * (starting with "#") and split on the basis of different characters
 * (default: ","). 
 * </p>
 * <p>
 * The most natural return format is a 2-dimensions arrays of Strings. 
 * Still it is also possible to get a List of 1-dimension arrays of 
 * Strings. If the CSV is not "squared"; i.e. the number of fields is
 * different across rows, the method might not work properly. 
 * </p>
 * @author albarron
 *
 */
public class CsvFoolWriter {
	
	private String separator;
	
	
	public CsvFoolWriter(){
		separator = ",";
		
	}
	
	public CsvFoolWriter(String sep){
		
		separator = sep;
	}
	
	/**
	 * Reads a csv file and return a 2-dimensional array of Strings of 
	 * it. Comments can be included in the csv file (starting with "#").
	 * <br/>
	 * Default separator used: ","
	 * @param file
	 * @return 2-dimensional array of entries in the csv file
	 */
	public void matrix2csv(String[][] values, File file){
		matrix2csv(null, values, file);
	}
	
	/**
	 * Reads a csv file. Comments can be included in the file (starting 
	 * with "#"). 
	 * @param file
	 * @param separator character(s) that separate the fields
	 * @return 2-dimensional array of entries in the csv file
	 */
	public void matrix2csv(String[] header, String[][] values, File file) {
		StringBuffer bf = new StringBuffer();
		if (header != null){
			if (header.length != values[0].length){
				CHK.CHECK(false, "Header and values should have the same dimension");
			}
			bf.append("#");
			for (int i = 0 ; i < header.length; i ++){
				bf.append(header[i])
				  .append(separator);
			}
			bf.delete(bf.length()-separator.length(), bf.length());
			bf.append("\n");
		}
		
		for (String[] line : values){
			for (int i = 0 ; i < line.length; i ++){
				bf.append(line[i])
				.append(separator);
			}
			bf.delete(bf.length()-separator.length(), bf.length());
			bf.append("\n");
		}
		
		try {
			FileIO.stringToFile(file, bf.toString(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
