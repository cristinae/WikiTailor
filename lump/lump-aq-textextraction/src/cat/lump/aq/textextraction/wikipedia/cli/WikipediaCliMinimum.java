package cat.lump.aq.textextraction.wikipedia.cli;

import java.util.Locale;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.WTConfig;
import cat.lump.aq.wikilink.config.Dump;

/**
 * CLI to access JWPL-WIKIPEDIA-related programs in this package.
 * 
 * This class contains the minimum parameters required to setup a
 * JWPL instance of Wikipedia, namely language and year, and to
 * initialise the WikiTailor and IR-based methods for in-domain
 * corpora extraction.
 *  
 * @author albarron 
 * @author cristina
 *
 */
public abstract class WikipediaCliMinimum {
	
	protected String LABEL;
	protected String command;
	protected String header;
	protected String footer;
	
	/**The Wikipedia language */
	protected Locale locale;
	
	/**Year of the Wikipedia edition */
	protected int year;
	
	/**Configuration file */
	protected Properties p;

	protected LumpLogger logger;
	
	/** The options for the given CLI */
	protected Options options;
		
	/** Loads the logger and the available options (by calling loadOptions) */
	public WikipediaCliMinimum() {			
		logger = new LumpLogger(this.getClass().getCanonicalName());
		p = new Properties();
		header = "\nwhere the arguments are:\n";
		loadOptions();
	}
	
	/**
	 * Method to parse the arguments received.
	 * <br/>
	 * This minimal method includes the setup of the Wikipedia language and year.
	 * @param args
	 */
	public void parseArguments(String[] args) {
		HelpFormatter formatter = new HelpFormatter();
		int widthFormatter = 88;
		CommandLine cLine = parseLine(args);
		
		if (cLine == null) {
			formatter.printHelp(widthFormatter, command, header, options, footer, true);
			System.exit(1);
		}		
		
		if (cLine.hasOption("h"))	{
			formatter.printHelp(widthFormatter, command, header, options, footer, true);
			System.exit(0);
		}
		
		/* Ja el tracta loadOptrion
		if (! cLine.hasOption("l") || ! cLine.hasOption("y") ) {	//no language or year 
			exitError(formatter, "I need Wikipedia language code and year");
		}
	    */
		
		// Wikipedia related
		locale = new Locale(cLine.getOptionValue("l"));
		year = Integer.valueOf(cLine.getOptionValue("y"));

		Dump ad = new Dump(locale, year);
		ad.getLanguage();	
		
		// WikiTailor related
		// Guessing if its an absolute or a relative path
		String input = cLine.getOptionValue("i");
		String iniFile;
		if (input.startsWith(FileIO.separator)){
			iniFile = input;
		} else {
			iniFile = System.getProperty("user.dir")+FileIO.separator+input;
		}
		// Read it
		p = WTConfig.getProperties(iniFile);		
	}
	
	
	/** Load the options for language, year, and help */
	protected void loadOptions() {
		options= new Options();		
		OptionBuilder.withLongOpt("language");
		OptionBuilder.withDescription("Language of interest (e.g., en, es, ca)");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("arg");
		OptionBuilder.isRequired();
		options.addOption(OptionBuilder.create('l'));		

		OptionBuilder.withLongOpt("year");
		OptionBuilder.withDescription("Wikipedia year edition (2013, 2015, 2016)");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("arg");
		OptionBuilder.isRequired();
		options.addOption(OptionBuilder.create('y'));		

		OptionBuilder.withLongOpt("ini");
		OptionBuilder.withDescription("Global config file for WikiTailor");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("FILE");
		OptionBuilder.isRequired();
		options.addOption(OptionBuilder.create('i'));		
		options.addOption("h", "help", false,
				"This help");
	
	}
	
	/**
	 * Parses the arguments and generates the command line for further
	 * processing the parameters.
	 * @param args 
	 * 			Array of arguments as received from the command line
	 * @return
	 * 			The command line parameters processed
	 */
	protected CommandLine parseLine(String[] args) {
		CommandLineParser parser = new BasicParser();
		CommandLine cLine = null;
		try {			
		    cLine = parser.parse( options, args );
		} catch( ParseException exp ) {
			logger.error( "Unexpected exception: " + exp.getMessage() );			
		}	
		return cLine;	
	}
	
	/**
	 * Finish the process with the CLI help and the an error message.
	 * @param formatter
	 * @param message
	 */
	protected void exitError(HelpFormatter formatter, String message) {		
		formatter.printHelp(command, header, options, footer, true);
		logger.errorEnd(message);		
	}
	
	/**
	 * Exit displaying the CLI help
	 * @param formatter
	 */
	protected void exitHelp(HelpFormatter formatter) {
		formatter.printHelp(command, header, options, footer, true);
		System.exit(0);
	}
	
	
	/* 
	 * Getters 
	 */
	public Locale getLanguage()	{
		return locale;
	}
	
	public int getYear(){
		return year;		
	}
	
	public int getPropertyInt(String key){
		return Integer.valueOf(p.getProperty(key));
	} 
	
	public String getPropertyStr(String key){
		return p.getProperty(key);
	} 
}
