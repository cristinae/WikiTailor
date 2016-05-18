package cat.lump.ir.lucene.cli;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliMinimum;

public class LuceneCliQuerierWT extends WikipediaCliMinimum{
	
	/**Path to the input file */
	private String inDir;
	
	/**Path to the output file */
	private String outDir;
	private float percentage;
	
	
	public LuceneCliQuerierWT(){
		super();
		LABEL = "LuceneQuerierWT";
		logger = new LumpLogger(LABEL);
		Class<LuceneCliCategoriesXecutor> c = LuceneCliCategoriesXecutor.class;
		try {
			File exe = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			command = "java -cp " + exe.getName() + " cat.lump.ir.lucene." + LABEL;
			footer = "\nEx: "+ command +" -l en -y 2015 -i wikiTailor.ini " +
					 "-x WTlucene/indexes \n";
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
		options.addOption("x", "inputDir", true, 
				"Directory with the input indexes");
		options.addOption("o", "outDir", true,
				"Optional: save the output into this directory " +
				"(default: NOT USED)");
		options.addOption("s", "minScore", true,
				"Optional: minimum score for an article to be retrived as a " +
				"percentage of the maximum score (default: 10%)" +
				"Use 999 for retrieving all the articles");

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
	    
	    if( !cLine.hasOption("x") ) {
	    	logger.warn("Please, provide input directory with indexes as requested");
			formatter.printHelp(widthFormatter, command, header, options, footer, true);
	    	System.exit(1);
	    }
		inDir = cLine.getOptionValue("x");   
	    
	    if (cLine.hasOption("o")){
	    	outDir = cLine.getOptionValue("o");			
		} else{
			outDir = System.getProperty("user.dir");
		} 

		
	    if (cLine.hasOption("s")){
	    	percentage = Float.valueOf(cLine.getOptionValue("s"));			
		} else{
			percentage = Integer.parseInt(p.getProperty("minPercentage"));
		} 
	}

	
	/** Getters */
	public String getIn(){
		return inDir;
	}	

	public String getOut(){
		return outDir;
	}	

	public float getPercentage(){
		return percentage;
	}


	public boolean getVerbosity() {
		// TODO implement verbosity
		return false;
	}	

}