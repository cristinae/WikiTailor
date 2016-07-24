package cat.lump.ir.sim.ml.esa.experiments;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cat.lump.aq.basics.algebra.vector.VectorStorageSparse;
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
 *  - [output file]
 *  
 *  OUTPUT
 *  - Serialized object with the vectors
 * 
 * @author albarron
 * @since July, 2016
 *
 */
public class A_EsaRepresentationComputer {

	private static LumpLogger logger = 
			new LumpLogger (CorrelationsxCategory.class.getSimpleName());

	private static final String DEFAULT_OUTPUT_FILE_SUFFIX = "esa_vectors.obj";
	
	private static File inputPath ;
	
	private static File outputFile;
	
	private static EsaGenerator esaGen;
	
	private static void loadIndex(String path, String lan) {
		//LOAD THE INDEX TO COMPUTE THE REPRESENTATIONS
		esaGen = new EsaGeneratorWT(new File(path), new Locale(lan));
	}
	 
	private void computeVectors() throws IOException {
		List<String> files = FileIO.getFilesRecursively(inputPath, "txt");
		VectorStorageSparse esaVectors = new VectorStorageSparse();
		int counter =0;
		for (String f : files) {
			esaVectors.add(
					getIdFromFile(f), 
					esaGen.computeVector(FileIO.fileToString(new File(f))).get()
			);
			if (counter++ % 500 == 0) {
				logger.info(String.format("Computing %d of %d", counter-1, files.size())
						);
			}
		} 
		
//		for (String k : esaVectors.getIds()) {
//			System.out.format("%s: %d%n", k, esaVectors.getValues(k).size());
//		}
//		System.out.println(esaVectors.getVectorSize());

		logger.info("Storing the vectors into " + outputFile);
		FileIO.writeObject(esaVectors, outputFile);
	}
	
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
				new File(
					String.format("%s%s%s.%s", 
						cLine.getOptionValue("d"), File.separator, 
						cLine.getOptionValue("l"), DEFAULT_OUTPUT_FILE_SUFFIX))
				);
		}
		//return cLine;		
	}


	private String getIdFromFile(String file) {
		file = file.replace(".txt", "");
		if (file.contains(File.separator)) {
			file = file.substring(file.lastIndexOf(File.separator) + 1);
		}
		return file;
	}
	
	private static void setInputFolder(File path) {
		if (!path.isDirectory()) {
			logger.error("I cannot read the directory " + path);
			System.exit(1);			
		}
		inputPath = path;
	}
		
	private static void setOutputFile(File outFile) {
		if (outFile.exists()) {
			logger.error(String.format("Output file %s already exists", outFile));
			System.exit(1);		
		}
		outputFile = outFile;
	}
//	
//	public void storeVectors() {
//		logger.info("Storing the vectors into " + outputFile);
//		FileIO.writeObject(esaGen, outputFile);
//	}
	
	
	public static void main(String[] args) {
		A_EsaRepresentationComputer erc = new A_EsaRepresentationComputer();
		erc.setup(args);
		try {
			erc.computeVectors();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		erc.storeVectors();
		
	}
	
	
}
