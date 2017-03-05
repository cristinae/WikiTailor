package cat.lump.ir.lucene.cli;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliMinimum;
import cat.lump.ir.lucene.cli.LuceneCliCategoriesXecutor;

public class LuceneCliCrossIndexerWT extends WikipediaCliMinimum{
	
  private final String DEFAULT_KEY_LAN = "en";
  
  
	/**Path to the input file*/
	private String inDir;
	
	/** ISO-639 code for the key language */
	private String keyLan;
	
	/**Path to the output file */
	private String outDir;
	
	/** File with the ids for all the languages*/
	private String csvFile;
	
	public LuceneCliCrossIndexerWT(){
		super();
		LABEL = "LuceneCrossIndexerWT";
		logger = new LumpLogger(LABEL);
		Class<LuceneCliCategoriesXecutor> c = LuceneCliCategoriesXecutor.class;
		try {
			File exe = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			command = "java -cp " + exe.getName() + " cat.lump.sts2017.similarity" + LABEL;
			footer = "\nEx: "+ command +" -l en -y 2015 -i wikiTailor.ini " +
					 "-d WTlucene/rawFiles -x WTlucene/indexes \n";
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Loads additional options
	 */
	@Override
	protected void loadOptions(){
		super.loadOptions();
//		options.addOption("k", "keyLanguage", true,
//		    "ISO-639 language code that will be used as key (should be in the csv file) [default: en]");
		options.addOption("d", "input", true, 
				"Directory with the input files (one folder per language; document per file)");
		options.addOption("x", "output", true, 
				"Directory to save the index to");
		options.addOption("c", "csv", true, 
		    "File with the IDs per language. It contains the language id as header "
		    + "and the IDs for the documents. A row corresponds to the same article in "
		    + "different languages");
	}
	
	
	
	/**
	 * Parses the arguments received
	 * @param args
	 */
	@Override
	public void parseArguments(String[] args){
		super.parseArguments(args);
		
		HelpFormatter formatter = new HelpFormatter();
		int widthFormatter = 88;
		CommandLine cLine = parseLine(args);
		
	    if(!cLine.hasOption("d") || !cLine.hasOption("x") || !cLine.hasOption("c")) {
	    	logger.warn("Please, provide input and output directories as well as input CSV file");
			formatter.printHelp(widthFormatter, command, header, options, footer, true);
	    	System.exit(1);
	    }
	    
		inDir = cLine.getOptionValue("d");
		outDir = cLine.getOptionValue("x");	    
		csvFile = cLine.getOptionValue("c");
		keyLan = cLine.getOptionValue("l");
	}

	
	/** Getters */
	public String getIn(){
		return inDir;
	}	
	
	public String getKeyLan() {
	  return keyLan;
	}

	public String getOut(){
		return outDir;
	}

	public String getCsvFile() {
	  return csvFile;
	}
 	

	public boolean getVerbosity() {
		// TODO Implement verbosity
		return false;
	}	

}
