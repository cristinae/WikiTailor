package cat.lump.ir.sts2017;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cat.lump.aq.basics.algebra.vector.Vector;
//import cat.lump.aq.basics.algebra.vector.VectorStorageSparse;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
//import cat.lump.aq.textextraction.wikipedia.experiments.CorrelationsxCategory;
import cat.lump.ir.retrievalmodels.similarity.SimilarityMeasure;
import cat.lump.ir.retrievalmodels.similarity.VectorCosine;
import cat.lump.ir.sim.ml.esa.EsaGenerator;
import cat.lump.ir.sim.ml.esa.EsaGeneratorWT;

/**
 * This class takes a file from the SemEval 2017 STS Task and computes the 
 * similarity on the basis of either ESA or CL-ESA. 
 * 
 * It is capabla of handling en, es, ar and pairs of them.
 * It is based on cat.lump.ir.sim.ml.esa.experiments.A_EsaRepresentationComputer.
 * 
 *  INPUT:
 *  - index folder (not including en, es, ar)
 *  - input file (pairs of sentences; when across languages)
 *  - language1
 *  - [language2]
 *  - [output file]
 *  
 *  OUTPUT
 *  - File with similarities (and potentially distances)
 * 
 * @author albarron
 * @since July, 2016
 *
 */
public class CLEsaSimilarityComputer {

	private static final LumpLogger logger = 
			new LumpLogger (CLEsaSimilarityComputer.class.getSimpleName());
//
//	private static final String DEFAULT_INPUT_FILE_EXT = "txt";
	
	private static final String DEFAULT_OUTPUT_FILE_SUFFIX = "esa.sims";
	
	private final boolean COMPUTE_MORE_LIKE_THIS = true;
	
	private static File INPUT_FILE ;
	
	private static String OUTPUT_FILE;
	
	/** Has the index for the first language */
	private static EsaGenerator esaGen;
	
	/** Has the index for the second language */
	private static EsaGenerator esaGen2;
	
	private final int FEATURE_NUMBER = 3;
	
	private static final int MIN_DOC_FREQ = 2;
	
	/**
	 * Load the index for the first language 
	 * @param path
	 *           path to the index
	 * @param lan
	 *           specific language at hand
	 */
	private static void loadIndex(String path, String lan) {
	  path = String.format("%s%s%s", path, File.separator, lan);
		esaGen = new EsaGeneratorWT(new File(path), new Locale(lan), MIN_DOC_FREQ);
	}
	
	 /**
   * Load the index2 for the second language 
   * @param path
   *           path to the index
   * @param lan
   *           specific language at hand
   */
  private static void loadIndex2(String path, String lan) {
    path = String.format("%s%s%s", path, File.separator, lan);
    esaGen2 = new EsaGeneratorWT(new File(path), new Locale(lan), MIN_DOC_FREQ);
  }
	
	
	//TODO rename to computeSimilarities 
	private void computeVectors() throws IOException {
		
//		VectorStorageSparse esaVectors = new VectorStorageSparse();
		Vector src;
		Vector trg;
		String[] lines = FileIO.fileToLines(INPUT_FILE);
		SimilarityMeasure cosine = new VectorCosine();
		BufferedWriter bf = new BufferedWriter(new FileWriter(OUTPUT_FILE));
		double cos;
		for (int i = 0 ; i < lines.length ; i ++) {
		  String[] pair = lines[i].split("\t");
		
		  if (COMPUTE_MORE_LIKE_THIS) {
		    src = new Vector(esaGen.computeVectorMoreLikeThis(pair[0]).get());
		    trg = new Vector(esaGen2.computeVectorMoreLikeThis(pair[1]).get());
			} else {
			  src = new Vector(esaGen.computeVector(pair[0]).get());
        trg = new Vector(esaGen2.computeVector(pair[1]).get());
			}
		  
		  cos=cosine.compute(src, trg);
//		  System.out.println(cos);
		  bf.write(String.valueOf(cos));
		  bf.write("\n");
		}
		
		bf.close();
          
	}
	
	/**Computes the cosine similarity measure between two vectors
   * 
   * sim(v1,v2) = (v1 * v2) / (|v1||v2|)
   * @param v1
   * @param v2
   * @return
   */
  public static double cosineSim(Vector v1, Vector v2) {
    return  (v1.dotProduct(v2) / 
        (v1.magnitude() * v2.magnitude()) );
}
	
	private void setup(String[] args) {
		HelpFormatter formatter = new HelpFormatter();
		int widthFormatter = 88;		
		
		Options options= new Options();
		CommandLineParser parser = new BasicParser();

		options.addOption("l", "language", true, 
					"Language of interest (e.g., en, es, ar)");
		options.addOption("m", "language2", true, 
        "Second language, if cross-language (optional; e.g., en, es, ar)");
		options.addOption("x", "index", true, 
			        "Path to the previously-generated Lucene index");		
		options.addOption("h", "help", false, "This help");
		options.addOption("f", "file", true,
				    "Tab-separated file with the pairs of textx");
		options.addOption("o", "outputFile", true,
			        "Output file, where the similarities are going to be stored");

		CommandLine cLine = null;
		
		try {			
		    cLine = parser.parse( options, args );
		} catch( ParseException exp ) {
			logger.error( "Unexpected exception:" + exp.getMessage() );			
		}	
		
		if (cLine == null ||
			! (cLine.hasOption("l") && cLine.hasOption("x") && cLine.hasOption("f"))	
		   ) {
			logger.error("Please, provide the necessary parameters");
			//formatter.printHelp(widthFormatter, command, header, options, footer, true)
			formatter.printHelp(CLEsaSimilarityComputer.class.getSimpleName(),options);
			System.exit(1);
		}
		
		loadIndex(cLine.getOptionValue("x"), cLine.getOptionValue("l"));
		if (cLine.hasOption("m")) {
		  loadIndex2(cLine.getOptionValue("x"), cLine.getOptionValue("m"));
		} else {
		  // If only one language, we make reference to a common index for both instances.
		  esaGen2 = esaGen;
		}
		
		setInputFile(new File(cLine.getOptionValue("f")));
		if (cLine.hasOption("o")) {
			setOutputFile(cLine.getOptionValue("o"));
		} else {
			setOutputFile(
					String.format("%s%s", 
						cLine.getOptionValue("f"), DEFAULT_OUTPUT_FILE_SUFFIX)
				);
		}
		//return cLine;		
	}
//
//
//	private String getIdFromFile(String file) {
//		file = file.replace(".txt", "");
//		if (file.contains(File.separator)) {
//			file = file.substring(file.lastIndexOf(File.separator) + 1);
//		}
//		return file;
//	}
//	
	private static void setInputFile(File path) {
		if (!path.isFile()) {
			logger.error("I cannot read the directory " + path);
			System.exit(1);			
		}
		INPUT_FILE = path;
	}
		
	private static void setOutputFile(String outFile) {
//		if (new File(outFile).exists()) {
//			logger.error(String.format("Output file %s already exists", outFile));
//			System.exit(1);		
//		}
		OUTPUT_FILE = outFile;
	}
//	
////	public void storeVectors() {
////		logger.info("Storing the vectors into " + outputFile);
////		FileIO.writeObject(esaGen, outputFile);
////	}
//	
	
	public static void main(String[] args) {
		CLEsaSimilarityComputer erc = new CLEsaSimilarityComputer();
		erc.setup(args);
		try {
			erc.computeVectors();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		-f /data/alt/corpora/semeval2017/task1/en.train.1_fold/MINI_ENGLISH_ARABIC.txt -l en -m ar -x /data/alt/corpora/semeval2017/task1/clesa/index
		
	}
	
	
}
