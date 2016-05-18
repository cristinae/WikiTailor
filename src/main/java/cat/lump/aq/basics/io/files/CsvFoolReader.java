/** 
 * 
 * Created on 4 Dec 2011<br><br>
 * Software being developed by lbarron
 */

package cat.lump.aq.basics.io.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
public class CsvFoolReader {
	
	/**
	 * Reads a csv file and return a 2-dimensional array of Strings of 
	 * it. Comments can be included in the csv file (starting with "#").
	 * <br/>
	 * Default separator used: ","
	 * @param file
	 * @return 2-dimensional array of entries in the csv file
	 */
	public static String[][] csv2matrix(File file){
		return csv2matrix(file, ",");
	}
	
	/**
	 * Reads a csv file. Comments can be included in the file (starting 
	 * with "#"). 
	 * @param file
	 * @param separator character(s) that separate the fields
	 * @return 2-dimensional array of entries in the csv file
	 */
	public static String[][] csv2matrix(File file, String separator) {
		List<String[]> elements = csvFileToList(file, separator);  
		return elements.toArray(new String[elements.size()][]);
	}

	public static List<String[]> csvFileToList(File file){
		return csvFileToList(file, ",");
	}
	
	public static List<String[]> csvFileToList(File file, String separator){
		int columns;
		String[] lines = null,
				 temporal,
				 potentialLabels;
		List<String[]> elements = new ArrayList<String[]>();
		
		try {
			lines = FileIO.fileToString(file).split("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}					
				
		potentialLabels = loadLabels(lines[0], separator);
		if (potentialLabels == null){
			columns =  lines[0].split(separator).length;
		} else {
			columns = potentialLabels.length;
			//System.out.print("Labels found:\t");
			//displayArray(potentialLabels);
		}			
		
		temporal = new String[columns];
				
		for (int i = 0 ; i < lines.length ; i ++ ) {
			if (lines[i].startsWith("#")){
				continue;		//It is a comment
			}

			temporal = lines[i].split(separator);
			elements.add(temporal);
		}	

		return elements;		
	}
	
	/**
	 * Tries to obtain the labels from the csv file (if they exist)
	 *
	 * @param labelsLine line with the labels
	 * @param separator csv separator
	 * @return labels of the file; null if they don't exist
	 */
	private static String[] loadLabels(String labelsLine, String separator){
		if (! labelsLine.startsWith("#"))
			return null;		
		
		//remove leading(s) #
		labelsLine = labelsLine.replaceAll("^[#]*", ""); 
		
		if (labelsLine.length() == 0)
			return null;		//only #[##..] in the line		

		return labelsLine.split(separator);		
	}
	
}
