package cat.lump.ir.lucene.cli;

import java.util.Locale;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cat.lump.aq.basics.log.LumpLogger;

/**
 * CLI to access the Lucene-related classes for the Wikiparable project
 * 
 * @author cristina
 * @since July 1, 2015
 */
public abstract class LuceneCliMinimum0 {
	
	/** Logs */
	protected LumpLogger logger;	
	protected String LABEL;
	protected String command;
	protected String header;
	protected String footer;

	/** The options for the given CLI */
	protected Options options;
		
	/** Language */
	protected Locale language;

	/** Verbosity */
	protected boolean verbose;

	/** Loads the logger and the available options (by calling loadOptions) */
	public LuceneCliMinimum0(){			
		logger = new LumpLogger(this.getClass().getCanonicalName());
		loadOptions();
	}

	
	/** Load the options for input, output, language and help */
	protected void loadOptions(){
		options= new Options();
		//For indexing
		/*options.addOption("i", "input", true, 
				"Directory with the input files (one document per file)");
		options.addOption("o", "output", true, 
				"Directory to save the index to");	*/
		//Generals
		options.addOption("l", "language", true, 
				"Language. One among ar, ca, de, el, en, es, eu, "
					+ "fr...");
		options.addOption("v", "verbose", false,
				"Verbose execution");
		options.addOption("h", "help", false,
				"This help");

	}

	/**
	 * Method to parse the arguments received.
	 * @param args
	 */
	public void parseArguments(String[] args)
	{
		HelpFormatter formatter = new HelpFormatter();
				
		CommandLine cLine = parseLine(args);
		
		if (cLine == null) {
			formatter.printHelp(LABEL, options);
			System.exit(1);
		}		
		
		if (cLine.hasOption("h")){
			formatter.printHelp(LABEL, options);
			System.exit(0);
		}
		
		if ( !cLine.hasOption("l") ){
			exitError(formatter, "Please, specify the language");
		}else{				
			language = new Locale(cLine.getOptionValue("l"));
		}
			
		if (cLine.hasOption("v")){
			verbose = true; 
		}else {
			verbose = false;
		}

		
	}
	/**
	 * Parses the arguments and generates the command line for further
	 * processing the parameters.
	 * @param args 
	 * 			Array of arguments as received from the command line
	 * @return
	 * 			The command line parameters processed
	 */
	protected CommandLine parseLine(String[] args){
		CommandLineParser parser = new BasicParser();
		CommandLine cLine = null;
		try {			
		    cLine = parser.parse( options, args );
		} catch( ParseException exp ) {
			logger.error( "Unexpected exception:" + exp.getMessage() );			
		}	
		return cLine;	
	}
	
	/**
	 * Finish the process with the CLI help and the an error message.
	 * @param formatter
	 * @param message
	 */
	protected void exitError(HelpFormatter formatter, String message){		
		formatter.printHelp( LABEL, options );
		logger.errorEnd(message);		
	}
	
	/**
	 * Exit displaying the CLI help
	 * @param formatter
	 */
	protected void exitHelp(HelpFormatter formatter){
		formatter.printHelp( LABEL, options );
		System.exit(0);
	}
	
	
	/** Getters */
	public Locale getLanguage() {
		return language;
	}
	
	public boolean getVerbosity() {		
		return verbose;
	}
	
}
