package cat.lump.ir.lucene.cli;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;

import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliMinimum;

/**
 * CLI for WikiTailor2Query 
 * 
 * @author cristina
 * @since July 1, 2015
 *
 */
public class LuceneCliWT2Query extends WikipediaCliMinimum{
	
	/**Path to the input/output/index files */
	private String indexDir;
	private String inDir;
	private String outDir;
	
	/**Category (collection) from where to extract the articles*/
	private String category;
	
	/**Documents with a score larger than max_score/percentage will be retrieved */
	private float percentage;
	
	/** Maximum size of the vocabulary defining the domain*/
	private int maxVocab;

	/**File to store the IDs of the retrieved articles */
	private String eArticlesFileName;

	
	public LuceneCliWT2Query(){
		super();
		LABEL = "LuceneCliWT2Query";
		logger = new LumpLogger(LABEL);
		Class<LuceneCliCategoriesXecutor> c = LuceneCliCategoriesXecutor.class;
		try {
			File exe = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			command = "java -cp " + exe.getName() + " cat.lump.ir.lucene.WikiTailor2Query";
			footer = "\nEx: "+ command +" -l en -y 2015 -i wikiTailor.ini " +
					 "-n 49024 -d WTlucene/rawFiles -x WTlucene/indexes \n";
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
		
		OptionBuilder.withLongOpt("indexDir");
		OptionBuilder.withDescription("Directory with the input indexes");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("arg");
		OptionBuilder.isRequired();
		options.addOption(OptionBuilder.create('x'));		
		//options.addOption("x", "indexes", true, 
		//		"Directory with the input indexes");
		
		options.addOption("o", "outpath", true,
				"Optional: save the output into this directory (default: current)");
		options.addOption("d", "inputDir", true,
				"Directory to find the raw Wikipedia articles");		
		options.addOption("c", "categoryID", true,
				"Optional: category to analyse (default: all categories in path2root)");
		options.addOption("s", "minScore", true,
				"Optional: minimum score for an article to be retrived as a " +
				"percentage of the maximum score (default: 10%) " +
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
	    	logger.warn("Please, provide input directory with the indexes");
			formatter.printHelp(widthFormatter, command, header, options, footer, true);
	    	System.exit(1);
	    }
		indexDir = cLine.getOptionValue("x");   
	    
	    if (cLine.hasOption("o")){
	    	outDir = cLine.getOptionValue("o");			
		} else{
			outDir = System.getProperty("user.dir");
		} 
	    
	    if (cLine.hasOption("d")){
	    	inDir = cLine.getOptionValue("d");			
		} else{
			inDir = System.getProperty("user.dir");
		} 
				
		if(cLine.hasOption("c")){ 
			category = cLine.getOptionValue("c");
		} else{
			category = "ALL";
		}
		
	    if (cLine.hasOption("s")){
	    	percentage = Float.valueOf(cLine.getOptionValue("s"));			
		} else{
			percentage = Float.valueOf(p.getProperty("minPercentage"));
		} 

        maxVocab = Integer.parseInt(p.getProperty("topKeywords4L"));
        
        eArticlesFileName = p.getProperty("eArticlesFileName");
	}

	
	/** Getters */
	public String getIndexDir(){
		return indexDir;
	}	

	public String getInDir(){
		return inDir;
	}	

	public String getOutDir(){
		return outDir;
	}	

	public String getCategory(){
		return category;
	}	
	
	public float getPercentage(){
		return percentage;
	}

	public int getMaxVocab(){
		return maxVocab;
	}

	public String getEArticlesFileName(){
		return eArticlesFileName;
	}	

	//TODO implement verbosity
	public boolean getVerbosity() {
		return false;
	}	

}
