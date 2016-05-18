package cat.lump.ir.sim.cl.len;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.After;
import org.junit.Before;

public abstract class TestAbstract {
	
	/**Temporal file with source sentences */
	protected final File SRC_FILE = new File("source.txt");
	
	/**Temporal file with target sentences */
	protected final File TRG_FILE = new File("target.txt");
	
	/**Double difference permitted for the tests */
	protected final double DELTA = 0.0001;
	
	/**
	 * Creates temporal parallel files to perform the estimation
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		String src =		
			"* - Main goods are marked with red color.\n"
			+ "Construction and repair of highways and.\n"
			+ "* - Main servises are marked with red color.\n"
			+ "Services of language translation with use of.\n"
			+ "Services in maintenance service and repair.";

		String trg = 
			"* - principales productos se marcaron con color rojo.\n"
			+ "La construcción y reparación de carreteras y.\n"
			+ "* - principal servises están marcadas con el color rojo.\n"
			+ "Los servicios de traducción con el uso del lenguaje.\n"
			+ "Los servicios en servicio de mantenimiento y reparación.\n"; 
		
		saveFile(src, SRC_FILE);
		saveFile(trg, TRG_FILE);		
	}
	
	/**
	 * Deletes the temporal files
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		SRC_FILE.delete();
		TRG_FILE.delete();		
	}
	
	private void saveFile(String text, File file){		
		try {
			Writer writer = new BufferedWriter(
								new FileWriter(
									file));
			writer.append(text);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}


}
