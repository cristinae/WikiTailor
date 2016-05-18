package cat.lump.aq.textextraction.wikipedia.cli;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;

import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * CLI to access the Xecutor pipeline for the WikiTailor category-based
 * in-domain comparable corpora extraction.
 *  
 * @author cristina
 *
 */

public class WikipediaCliCategoriesXecutor extends WikipediaCliMinimum{

	
	/**Identifier of the category */
	private String sCategory;
	private int iCategory;
		
	/**Path to the output file */
	private String output;
		
	/**Number of step at which the process begins */
	private int firstStep;
	
	/**Number of step at which the process ends */
	private int endStep;

	/**Model as determined by the percentage of in-domain categories */
	private Double model;

	/**Number of terms to be considered */
	private int top;

	/**Depth to explore a given category */
	private int depth;
	
	
	public WikipediaCliCategoriesXecutor() {
		super();
		LABEL = "Xecutor";
		logger = new LumpLogger(LABEL);
		Class<WikipediaCliCategoriesXecutor> c = WikipediaCliCategoriesXecutor.class;
		try {
			File exe = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			command = "java -jar " + exe.getName();
			footer = "\nEx: "+ command +" -l en -y 2015 -i wikiTailor.ini -c Science\n";

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}
	
	/* (non-Javadoc)
	 * @see cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliMinimum#loadOptions()
	 */
	/**
	 * Loads additional options: category (numerical and string), percentage of
	 * words required and output file 
	 */
	@Override
	protected void loadOptions() {
		super.loadOptions();
		//MANDATORY (at WikipediaCliMinimum)
		//options.addOption("l", "language", true, 
		//			"Language of interest (e.g., en, es, ca)");
		//options.addOption("y", "year", true, 
		//			"Dump year (e.g. 2010, 2012, 2013, 2015)");
		
		//ALTERNATIVELY
		OptionGroup group = new OptionGroup();
		Option catOption = new Option("c", "category", true, 
				"Name of the category (with '_' instead of ' '; you can use -n instead)");		
		group.addOption(catOption);
		Option numOption = new Option("n", "numcategory", true,
				"Numerical identifier of the category (you can use -c instead)");
		group.addOption(numOption);
		options.addOptionGroup(group);
		
		//OPTIONAL
		options.addOption("s", "start", true,
				"Initial step for the process \n(default: 1)");
		options.addOption("e", "end", true,
				"Last step for the process \n(default: 7)");
		options.addOption("d", "depth", true,
				"Depth obtained in a previous execution \n(default: 0)");
		options.addOption("o", "outpath", true,
				"Save the output into this directory \n(default: current)");		
		options.addOption("m", "model", true,
				"Percentage of in-domain categories \n(default: 0.5)");		
		options.addOption("t", "top", true,
				"Number of vocabulary terms within the 10% \n(default: 100, all: -1)");		
		options.addOption("h", "help", false,
				"This help");
		
	}
	
	/* (non-Javadoc)
	 * @see cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliMinimum#parseArguments(java.lang.String[])
	 */
	@Override
	public void parseArguments(String[] args)	{
		super.parseArguments(args);
		
		HelpFormatter formatter = new HelpFormatter();
		int widthFormatter = 88;
		CommandLine cLine = parseLine(args);
		
		if (cLine == null ||
			! ((cLine.hasOption("c") || cLine.hasOption("n")))){				
			logger.error("The category must be defined either with -c or -n\n");
			formatter.printHelp(widthFormatter, command, header, options, footer, true);
			System.exit(1);
		}
		
		if (cLine.hasOption("s")){
			firstStep = Integer.valueOf(cLine.getOptionValue("s"));
		} else{
			firstStep = 1;
		}
		
		if (cLine.hasOption("e")){
			endStep = Integer.valueOf(cLine.getOptionValue("e"));
		} else{
			endStep = 7;
		}

		if (cLine.hasOption("d")){
			depth = Integer.valueOf(cLine.getOptionValue("d"));
		} else{
			depth = 0;
		}

		if (cLine.hasOption("m")){
			model = Double.valueOf(cLine.getOptionValue("m"));
		} else{
			model = Double.valueOf(p.getProperty("percentage"));
		}

		if (cLine.hasOption("t")){
			top = Integer.valueOf(cLine.getOptionValue("t"));
		} else{
			top = Integer.valueOf(p.getProperty("topKeywords"));
		}

		if (cLine.hasOption("o")){
			output = cLine.getOptionValue("o");			
		} else{
			output = System.getProperty("user.dir");
		} 

		if (cLine.hasOption("c")){
		    sCategory = cLine.getOptionValue("c");          //"Historia_de_Aragón";
			WikipediaJwpl wiki;
			try {
				wiki = new WikipediaJwpl(locale, year);
				iCategory = wiki.getCategory(sCategory).getPageId();
			} catch (WikiApiException e) {
				e.printStackTrace();
			}	
		} else {
			iCategory = Integer.valueOf(cLine.getOptionValue("n"));//50428;
			if (firstStep==1){
				WikipediaJwpl wiki;
				try {
					wiki = new WikipediaJwpl(locale, year);
					sCategory = wiki.getCategory(iCategory).getTitle().getPlainTitle();
					} catch (WikiApiException e) {
						e.printStackTrace();
					}
			}
		}
/*//The previous option is longer but avoids the connection to the database if not necessary
		WikipediaJwpl wiki;
		try {
			wiki = new WikipediaJwpl(locale, year);
			if (cLine.hasOption("c")){
				sCategory = cLine.getOptionValue("c");          //"Historia_de_Aragón";
				iCategory = wiki.getCategory(sCategory).getPageId();
			} else {
				iCategory = Integer.valueOf(cLine.getOptionValue("n"));//50428;
				sCategory = wiki.getCategory(iCategory).getTitle().getPlainTitle();
			}
		} catch (WikiApiException e) {
			e.printStackTrace();
		}				
*/
	}

	
    /** Getters */	
	public String getsCategory()	{
		return sCategory;
	}

	public int getiCategory() {
		return iCategory;
	}
	
	public int getFirstStep()	{
		return firstStep;
	}
	
	public int getLastStep() {
		return endStep;
	}
	
	public double getModel() {
		return model;
	}	

	public int getTop()	{
		return top;
	}

	public int getDepth()	{
		return depth;
	}

	public String getOutputFile()	{
		return output;
	}	
	
}
