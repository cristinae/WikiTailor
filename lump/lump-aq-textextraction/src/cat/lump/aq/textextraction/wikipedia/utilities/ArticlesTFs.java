package cat.lump.aq.textextraction.wikipedia.utilities;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.basics.structure.ir.TermFrequencyTuple;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliArticleTextExtractor;
import cat.lump.aq.textextraction.wikipedia.prepro.TermExtractor;
import cat.lump.ir.weighting.TermFrequency;


/**
 * A class to calculate the TFs associated to all terms in a document from an already extracted WP 
 * edition.
 * 
 * @author cristina
 * @since May 28, 2015
 * TODO: Convert into a cli?
 */

public class ArticlesTFs {

	/** Language of the articles */
	private String lang;
	
	/** Path to the directory where the original articles are stored. */
	private String rootDirectory;
	
	private static LumpLogger logger = 
			new LumpLogger (ArticlesTFs.class.getSimpleName());
	
	
	/**
	 * Constructor for the class. Starts the process of estimating the frequency of the
	 * terms for all the articles
	 */
	public ArticlesTFs(String language, String path){
		CHK.CHECK_NOT_NULL(language);
		CHK.CHECK_NOT_NULL(path);
		this.lang = language;
		this.rootDirectory = path;
	}
	
	
	/**
	 * Explores the {@code rootDirectory} to locate the files and calls the function to
	 * estimate the frequency of each article
	 */
	private void processAll() {
		
		// Input and Output folders
		String pathIN = rootDirectory + FileIO.separator + "plain" + FileIO.separator + lang;
		File directory = new File(pathIN); 
        CHK.CHECK(directory.isDirectory(), "I cannot read the directory " + pathIN);		

        String pathOUT = rootDirectory + FileIO.separator + "tfs" + FileIO.separator + lang; 
		if (! new File(pathOUT).exists()) {
			if (new File(pathOUT).mkdirs()) {
			} else {
				logger.error("Failed to create "+ pathOUT);
			}
		}
		
		// Select the subfolders where the articles are
		File[] fList = directory.listFiles();  
		List<File> listOfFolders = new ArrayList<File>();
		for (File file : fList) {
			if (file.isDirectory()) {
				listOfFolders.add(file.getAbsoluteFile());
	        }
	    }
		
		logger.info("Calculating TFs for the articles in language " + lang);
		// Go into every subfolder, grab the articles
		int i=1;
		for (File folder : listOfFolders) {
			logger.info( " " + i + " subfolder ");
			List<String> listOfFiles = FileIO.getFilesExt(folder, "txt");
			for (String file : listOfFiles) {
				calculateTFs(file);
			}
			i++;
	    }
		logger.info("END ");
	}


	/**
	 * Estimates the TF for all the terms in file {@code file} and writes the
	 * resulting {@code List<TermFrequencyTuple>} in a file with the same name as
	 * the text file but in folder "tfs" instead of "plain"
	 * 
	 * @param file
	 */
	public void calculateTFs(String file) {
		
		// Read the input file
		String text = "";
		try {
			text = FileIO.fileToString(new File(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Calculate term frequencies
		TermFrequency tf = new TermFrequency();
		TermExtractor te = new TermExtractor(new Locale(lang));
		List<String> tokens = te.getTerms(text, 4);

		tf.addTerms(tokens);
		List<TermFrequencyTuple> listTFT = tf.getAll();
		
		// Print term frequencies into the file
		String outputFile = file.replace("plain", "tfs");
		StringBuffer sb = new StringBuffer();
		for (TermFrequencyTuple t : listTFT){
			String line = String.format("%d\t%s", t.getFrequency(), t.getTerm());
			sb.append(line).append("\n");
		}		
		try {
			FileIO.stringToFile(new File(outputFile), sb.toString(), false);
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
	}


	private static CommandLine parseArguments(String[] args)
	{
		HelpFormatter formatter = new HelpFormatter();
		int widthFormatter = 96;
		String header = "\nwhere the arguments are:\n";
		String command ="";
		String footer ="";
		Class<ArticlesTFs> c = ArticlesTFs.class;
		try {
			File exe = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			command = "java -cp " + exe.getName() + ' ' +c.getCanonicalName();
			footer = "\nEx: "+ command +" -l en -p /home/user/wikitailor/en/ \n";
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		CommandLine cLine = null;
		Options options= new Options();
		CommandLineParser parser = new BasicParser();

		options= new Options();		
		OptionBuilder.withLongOpt("language");
		OptionBuilder.withDescription("Language of interest (e.g., en, es, ca)");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("arg");
		OptionBuilder.isRequired();
		options.addOption(OptionBuilder.create('l'));		

		//options.addOption("l", "language", true, 
		//			"Language of interest (e.g., en, es, ca)");		
		options.addOption("h", "help", false, "This help");
		options.addOption("p", "path2root", true,
				    "Path to the folder where plain/ is (default: current)");

		try {			
		    cLine = parser.parse( options, args );
		} catch( ParseException exp ) {
			logger.error( "Unexpected exception:" + exp.getMessage() );			
		}	
		
		if (cLine == null || !(cLine.hasOption("l")) ) {
			logger.error("Please, set the language\n");
			//formatter.printHelp(ArticlesTFs.class.getSimpleName(),options);
			formatter.printHelp(widthFormatter, command, header, options, footer, true);
			System.exit(1);
		}		
		
		if (cLine.hasOption("h")) {
			formatter.printHelp(widthFormatter, command, header, options, footer, true);
			//formatter.printHelp(ArticlesTFs.class.getSimpleName(),options );
			System.exit(0);
		}
	
		return cLine;		
	}

	/**
	 * Main function to run the class, serves as example
	 * 
	 * @param args
	 * 		-l Language of the articles
	 * 		-p Path to the root folder for the given language
	 */
	public static void main(String[] args) {

		CommandLine cLine = parseArguments(args);
		
		String language = cLine.getOptionValue("l");
		String path = cLine.hasOption("p") ?
				  cLine.getOptionValue("p") : System.getProperty("user.dir");	
	
		//String path = "/home/cristinae/pln/wikipedia/categories/extractions/ca.0";
		//String language = "ca";
		ArticlesTFs artstf = new ArticlesTFs(language, path);
		artstf.processAll();

	} 

}
