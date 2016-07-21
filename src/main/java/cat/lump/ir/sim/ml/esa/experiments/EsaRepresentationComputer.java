package cat.lump.ir.sim.ml.esa.experiments;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cat.lump.aq.basics.algebra.vector.Vector;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.experiments.CorrelationsxCategory;
import cat.lump.ir.sim.ml.esa.EsaGenerator;
import cat.lump.ir.sim.ml.esa.EsaGeneratorWT;

/**
 * This class takes a collection of texts and compute an ESA representation out of them.
 * It is currently based on EsaGeneratorWT.
 * 
 *  INPUT:
 *  - index folder
 *  - language
 * 
 * @author alberto
 * 
 *
 */
public class EsaRepresentationComputer {

	private static LumpLogger logger = 
			new LumpLogger (CorrelationsxCategory.class.getSimpleName());
	private static final String DEFAULT_OUTPUT_FILE = "esa_vectors.obj";
	
	private static File inputPath ;
	
	private static File outputFile;
	
	private static EsaGenerator esaGen;
	
	private void setup(String[] args) {
		HelpFormatter formatter = new HelpFormatter();
		int widthFormatter = 88;		
		
		Options options= new Options();
		CommandLineParser parser = new BasicParser();

		options.addOption("l", "language", true, 
					"Language of interest (e.g., en, es, ca)");		
		options.addOption("x", "index", true, 
			        "Path to the previously-generated Lucene index");		
		options.addOption("h", "help", false, "This help");
		options.addOption("d", "inputDir", true,
				    "Input folder (with txt files)");
		options.addOption("o", "outputFile", true,
			        "Output file, where the vectors are going to be serialized");

		CommandLine cLine = null;
		
		try {			
		    cLine = parser.parse( options, args );
		} catch( ParseException exp ) {
			logger.error( "Unexpected exception:" + exp.getMessage() );			
		}	
		
		if (cLine == null ||
			! (cLine.hasOption("l") && cLine.hasOption("x") && cLine.hasOption("d"))	
			) {
			logger.error("Please, provide the necessary parametets");
			formatter.printHelp(CorrelationsxCategory.class.getSimpleName(),options);
			System.exit(1);
		}
		
		loadIndex(cLine.getOptionValue("x"), cLine.getOptionValue("l"));
		setInputFolder(new File(cLine.getOptionValue("d")));
		if (cLine.hasOption("o")) {
			setOutputFile(new File(cLine.getOptionValue("o")));
		} else {
			setOutputFile(
				new File(cLine.getOptionValue("d") + File.separator + DEFAULT_OUTPUT_FILE));
		}
		//return cLine;		
	}
	
	
	private static void loadIndex(String path, String lan) {
		//LOAD THE INDEX TO COMPUTE THE REPRESENTATIONS
		esaGen = new EsaGeneratorWT(new File(path), new Locale(lan));
		
	}
	
	private void computeVectors() throws IOException {
		List<String> files = FileIO.getFilesRecursively(inputPath, "txt");
		Map<String, Vector> esaVectors = new HashMap<String, Vector>();
		for (String f : files) {
			esaVectors.put(
					f, 
					esaGen.computeVector(FileIO.fileToString(new File(f)))
			);
		}
		//I'M HERE
		//DO SOMETHING
	}
	
	private static void setInputFolder(File path) {
		if (!path.isDirectory()) {
			System.err.print("I cannot read the directory " + path);
			System.exit(1);			
		}
		inputPath = path;
	}
		
	private static void setOutputFile(File outFile) {
		if (outFile.exists()) {
			System.err.print("The given output fie already exists ");
			System.exit(1);		
		}	
		
		//TODO validate the file does not exist
		outputFile = outFile;
	}
	
	
	
	public static void main(String[] args) {
		EsaRepresentationComputer erc = new EsaRepresentationComputer();
		erc.setup(args);
		try {
			erc.computeVectors();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
}
