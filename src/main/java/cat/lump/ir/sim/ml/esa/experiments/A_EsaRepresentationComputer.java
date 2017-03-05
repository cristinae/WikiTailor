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
 *  - input file
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

	private static final LumpLogger logger = 
			new LumpLogger (A_EsaRepresentationComputer.class.getSimpleName());

	private static final String DEFAULT_INPUT_FILE_EXT = "txt";
	
	private static final String DEFAULT_OUTPUT_FILE_SUFFIX = "esa_vectors";
		
	private static final String DEFAULT_OUTPUT_FILE_EXT = "obj";
	
	private static final int MAX_VECTORS_PER_FILE = 5000;
	
	private final boolean COMPUTE_MORE_LIKE_THIS = true;
	
	private static File inputPath ;
	
	private static String outputFile;
	
	private static EsaGenerator esaGen;
	
	private static void loadIndex(String path, String lan) {
		//LOAD THE INDEX TO COMPUTE THE REPRESENTATIONS
		esaGen = new EsaGeneratorWT(new File(path), new Locale(lan));
	}
	 
	private void computeVectors() throws IOException {
		List<String> files = FileIO.getFilesRecursively(inputPath, DEFAULT_INPUT_FILE_EXT);
		VectorStorageSparse esaVectors = new VectorStorageSparse();
		int counter =0;
		int idx = 0;
//		esaGen.displayDocIds();
		for (String f : files) {			
		  if (COMPUTE_MORE_LIKE_THIS) {
  			esaVectors.add(
  					getIdFromFile(f), 
  					esaGen.computeVectorMoreLikeThis(FileIO.fileToString(new File(f))).get()
  			);
			} else {
			  esaVectors.add(
	          getIdFromFile(f), 
	          esaGen.computeVector(FileIO.fileToString(new File(f))).get()
	      );
			}
			
			if (counter > 0 && counter % MAX_VECTORS_PER_FILE == 0) {
				//Save the current instances into an obj file.
				//reset the esaVectors (remove all the current instances)
				
			  String outFile = String.format("%s.%d.%s", outputFile, idx++, DEFAULT_OUTPUT_FILE_EXT);
			  
				logger.info(String.format("Storing the vectors up to %d in %s", counter, outFile));
				// TODO move this to xxx.idx.obj instead of xxx.obj.idx. This was currently made
				// manually on previously-generated vectors
				FileIO.writeObject(
						esaVectors, 
//						new File(String.format("%s.%d.%s", outputFile, idx++, DEFAULT_OUTPUT_FILE_EXT))
						new File(outFile)
						);
				    
				
				esaVectors.removeAllVectors();
				
			}
			if (counter++ % 500 == 0) {
				logger.info(String.format("Computing %d of %d", counter-1, files.size())
						);
			}
		} 

		logger.info("Storing the vectors up to " + counter);
		//System.out.println(esaVectors.getIds());
		
		//esaVectors.display();
		FileIO.writeObject(
		    esaVectors, 
		    new File(String.format("%s.%d.%s", outputFile, idx, DEFAULT_OUTPUT_FILE_EXT))
		 );
		          
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
			//formatter.printHelp(widthFormatter, command, header, options, footer, true)
			formatter.printHelp(CorrelationsxCategory.class.getSimpleName(),options);
			System.exit(1);
		}
		
		loadIndex(cLine.getOptionValue("x"), cLine.getOptionValue("l"));
		setInputFolder(new File(cLine.getOptionValue("d")));
		if (cLine.hasOption("o")) {
			setOutputFile(cLine.getOptionValue("o"));
		} else {
			setOutputFile(
					String.format("%s%s%s.%s", 
						cLine.getOptionValue("d"), File.separator, 
						cLine.getOptionValue("l"), DEFAULT_OUTPUT_FILE_SUFFIX)
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
		
	private static void setOutputFile(String outFile) {
//		if (new File(outFile).exists()) {
//			logger.error(String.format("Output file %s already exists", outFile));
//			System.exit(1);		
//		}
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
