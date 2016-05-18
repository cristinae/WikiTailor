package cat.lump.ir.sim.cl.len;

import java.io.File;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import cat.lump.aq.basics.check.CHK;

/**
 * A class to estimate length models for a language pair. The program is 
 * divided in two:
 * 
 * <ul>
 * <li> Parameters learning. A parallel collection is used to estimate the
 * parameters of the Gaussian distribution that expresses the expected length 
 * of a texts's translation from one language to another. 
 * <li> Quality estimation. The parameters for the language pair is known
 * beforehand and it is used to estimate the length factor of a pair of texts 
 * (potential translations).
 * </ul>
 * 
 * <p>
 * The model was originally proposed in:
 * </br>
 * Pouliquen, Steinberger, and Ignat. Automatic Identification of Document 
 * Translations in Large Multilingual Document Collections. In: Proceedings of 
 * RANLP-2003, pp. 401-408. Borovets, Bulgaria, 2003.
 * </p>
 * <p>
 * It can be used as a feature for machine translation quality estimation.
 * </p>
 * 
 * <p>
 * It has been used for plagiarism detection as well. The definition 
 * implemented here, as well as some background is available at:
 * </br>
 * Potthast, Barrón-Cedeño, Stein, and Rosso. Cross-Language Plagiarism 
 * Detection. Language Resources and Evaluation (LRE), Special Issue on 
 * Plagiarism and Authorship Analysis 45(1), pp. 1-18. Springer 
 * Netherlands (2011)
 * </br>
 * The class includes a CLI that can be called as follows:
 * </br>
 * LEARNING
 * </br>
 * <code> java -jar LengthModel.jar -l -s en.txt -t es.txt </code>
 * </br>
 * ESTIMATION
 * </br>
 * <code>java -jar LengthModel.jar -s en.txt -t es.test -m 1.17491349130 -d 0.34648875 -v</code>
 * </br>
 * (The default operation is estimation.)
 * 
 * @author albarron
 *
 */
public class LengthModel {

	
//	//TODO apparently these variables are not necessary
//	/**The source file */
//	protected File srcFile;
//
//	/**The target file*/
//	protected File trgFile;
	
	//TODO Check if we are interested in the entire file-based estimation
//	private List<String> src_files;
//	private List<String> trg_files;	
	
	/**Check whether default parameters exist for the language pair. 
	 * @param langPair
	 * @return The codification of the language pair
	 */
	private static String checkLanguagePair(String langPair){
		CHK.CHECK(LengthFactors.hasPair(langPair), 
				String.format("No default values exist for the pair %s\n"
						+ "The available pairs are: %s",  langPair, 
					StringUtils.join(LengthFactors.getAvailablePairs(),"; ")
					));		
//		if (! LengthFactors.hasPair(langPair) ) {
//			System.out.println("No default values exist for the pair " + langPair);
//			System.out.println("The available pairs are: " + 
//						StringUtils.join(LengthFactors.getAvailablePairs(),"; "));
//			System.exit(1);
//		}
		return langPair;
	}
	
	/**Print the help message (potentially with an error message)
	 * @param options
	 * @param err whether there was error or not
	 */
	private static void help(Options options, int err){
		String header = "a) Computes mean and sd for length model estimation.\n" +

		"b) Computes the length factor between instances";
		String footer = "\nTODO: methods for entire files, not lines";

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("LengthModel", header, options, footer, true);
		System.exit(err);
	}
	
	/**
	 * Parses the input parameters and either learns a length model from a 
	 * collection or estimates the corresponding values for a set of texts
	 * 
	 * @param args
	 * @throws ParseException
	 */
	public static void main (String[] args) throws ParseException{
				
		Options options = new Options();
		
		//Options for the weights estimation process		
		options.addOption("h", "help", false,
				"This help message");
		
		//Either learning or estimation mode
		options.addOption("l", "learn", false, 
				"If present, run the program in LEARNInG MODE (estimate " +
				"means and deviations). If not present, the process is run "
				+ "in QUALITY ESTIMATION mode");
		
		//The input is composed of a ser of files (or line-wise)
		options.addOption("f", "files", false, 
				"The input is a set of documents (files); the default "
				+ "behaviour is one instance per line");
		
		//Options for the quality estimation process		
		options.addOption("o", "one-case", false, 
				"The entire pair of files is considered as one single case;" +
				"the default behaviour is one instance per line");		
		
		//Setting the mean of the Gaussian distribution
		options.addOption("m", "mean", true,
				"Use this mean (should be previously estimated with -l)");
		//Setting the deviation of the Gaussian distribution
		options.addOption("d", "standard-dev", true,
				"Use this standard deviation "
				+ "(should be previously estimated with -l)");
		
		//Use default mean and dev for this pair
		options.addOption("p", "pair", true,
				"Use the default meand and sd for this pair (e.g. en-es) ");
		
		//Options for the input files: source and target files
		options.addOption("s", "source", true, "Source instances file");
		options.addOption("t", "target", true, "Target instances file");

		//TODO confirm that the entire file mode is working
		
		//Options for the input files: source and target directories
		options.addOption("S", "SOURCE", true, 
			"Path to source files (explored recursively, looking for .txt files");
		options.addOption("T", "TARGET", true, 
			"Path to target files (explored recursively, looking for .txt files");
		
		//Output format
		options.addOption("v", "verbose", false, 
			"Verbose mode: include input sentences");
				
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);		
		
		if (cmd.hasOption("h"))
			help(options, 0);			
						
		if (! (cmd.hasOption("s") && cmd.hasOption(("t")) ) &&
			! (cmd.hasOption("S") && cmd.hasOption(("T")) )	){
			System.out.println("I need both source and target texts\n");			
			help(options, 1);
		}
	
		if (cmd.hasOption("l")) {
			//LEARNING MODE
			//TODO change to log4j
			//Reporter.report("Running in learning mode");
			LengthModelLearn lm = new LengthModelLearn();

//			if (cmd.hasOption("s")){ //its one file
			lm.setFiles(new File(cmd.getOptionValue("s")), 
						new File(cmd.getOptionValue("t")) );
//			} else {
//				lm.loadFiles(new File(cmd.getOptionValue("S")),
//							 new File(cmd.getOptionValue("T")) );
//			}			
			lm.learn();			
			
		} else {
			//ESTIMATION MODE			
			//TODO change to log4j
			//Reporter.report("Running in quality estimation mode");
			
			LengthModelEstimate le = new LengthModelEstimate();			
//			if (cmd.hasOption("v"))
//				le.setVerbose(true);

			le.setFiles(new File(cmd.getOptionValue("s")), 
					new File(cmd.getOptionValue("t")) );
			if (cmd.hasOption("p") ){ 
				String pair = checkLanguagePair(cmd.getOptionValue("p"));
				le.setMuSigma(LengthFactors.getMean(pair), 
							LengthFactors.getSD(pair)
							);			
			} else {
				le.setMuSigma(Double.valueOf(cmd.getOptionValue("m")), 
						Double.valueOf(cmd.getOptionValue("d")) );
			}
			
			le.estimate();
			if (cmd.hasOption("v")){
				le.displayVerbose();
			} else {
				le.display();
			}
		}
	}	
	
}
