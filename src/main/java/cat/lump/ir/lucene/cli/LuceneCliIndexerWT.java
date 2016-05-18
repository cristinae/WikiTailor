package cat.lump.ir.lucene.cli;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliMinimum;

public class LuceneCliIndexerWT extends WikipediaCliMinimum{
	
	/**Path to the input file*/
	private String inDir;
	
	/**Path to the output file */
	private String outDir;
	
	public LuceneCliIndexerWT(){
		super();
		LABEL = "LuceneIndexerWT";
		logger = new LumpLogger(LABEL);
		Class<LuceneCliCategoriesXecutor> c = LuceneCliCategoriesXecutor.class;
		try {
			File exe = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			command = "java -cp " + exe.getName() + " cat.lump.ir.lucene." + LABEL;
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
		options.addOption("d", "input", true, 
				"Directory with the input files (one document per file)");
		options.addOption("x", "output", true, 
				"Directory to save the index to");	
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
		
	    if(!cLine.hasOption("d") || !cLine.hasOption("x")) {
	    	logger.warn("Please, provide input and output directories as requested");
			formatter.printHelp(widthFormatter, command, header, options, footer, true);
	    	System.exit(1);
	    }
	    
		inDir = cLine.getOptionValue("d");
		outDir = cLine.getOptionValue("x");	    
	}

	
	/** Getters */
	public String getIn(){
		return inDir;
	}	

	public String getOut(){
		return outDir;
	}


	public boolean getVerbosity() {
		// TODO Implement verbosity
		return false;
	}	

}
