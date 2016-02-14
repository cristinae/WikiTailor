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
 * CLI to access directly the extraction of categories step of Xecutor pipeline 
 * for the WikiTailor category-based in-domain comparable corpora extraction.
 * (step 2 of the Xecutor).
 *  
 * @author cristina
 *
 */
public class WikipediaCliCategoryExtractor extends WikipediaCliMinimum{

	
	/**Identifier of the category */
	private int categoryID;	
	
	/**Number of elements required (percentage) */
	private int maxdepth;
	
	/**Path to the output file */
	private File output;
	
	private boolean verbose;
	
	
	
	public WikipediaCliCategoryExtractor() {
		super();
		LABEL = "CategoryExtractor";
		logger = new LumpLogger (LABEL);
		Class<WikipediaCliCategoryExtractor> c = WikipediaCliCategoryExtractor.class;
		try {
			File exe = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			command = "java -cp " + exe.getName() + ' ' +c.getCanonicalName();
			footer = "\nEx: "+ command +" -l en -y 2015 -i wikiTailor.ini -n 49024\n";
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
		
		OptionGroup group = new OptionGroup();
		Option catOption = new Option("c", "category", true, 
				"Name of the category (with '_' instead of ' '; you can use -n instead)");		
		group.addOption(catOption);
		Option numOption = new Option("n", "numcategory", true,
				"Numerical identifier of the category (you can use -c instead)");
		group.addOption(numOption);
		options.addOptionGroup(group);
		/*options.addOption("c", "category", true,				
				"Category (e.g., \"Mitología\")");
		options.addOption("n", "numcategory", true,
				"Numerical identifier of the category (e.g., \"49204\"");*/
		options.addOption("m", "maxdepth", true,
			"Maximum depth of the categories' tree to explore at (all if not provided)");
		options.addOption("v", "verbose", false,
				"Include also depth, category ID, category name, articles "
				+ "associated, number of children, number of parents, "
				+ "parent id from which we reached this category (only "
				+ "category ID, category name, parent ID otherwise)");
		options.addOption("f", "file", true,
			String.format("Output file. A file in the current directory will be generated if not "
					+ "provided (e.g., %s/es.49204.2.category", System.getProperty("user.dir")));	

	}
	
	/* (non-Javadoc)
	 * @see cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliMinimum#parseArguments(java.lang.String[])
	 */
	@Override
	public void parseArguments(String[] args)	{
		super.parseArguments(args);
		
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cLine = parseLine(args);
						
		if (!(cLine.hasOption("c") || cLine.hasOption("n"))) { //no string nor int category
			exitError(formatter, "I need the Wikipedia category");
		}
		
		if (cLine.hasOption("c"))	{			
			try {
				WikipediaJwpl w = new WikipediaJwpl(locale, year);
				categoryID = w.getCategory(
						cLine.getOptionValue("c").replaceAll(" ", "_"))
							.getPageId();
			} catch (WikiApiException e) {		
				e.printStackTrace();
			}			
		} else if (cLine.hasOption("n")) {
			categoryID = Integer.parseInt(cLine.getOptionValue("n")); 
		} else {
			exitError(formatter, "I need the name or id for the category");			
		}		
		
		if (cLine.hasOption("m"))	{
			maxdepth = Integer.parseInt(cLine.getOptionValue("m"));
			if (maxdepth < 0 ) {
				exitError(formatter, "The maximum depth cannot be negative");				
			}			
		} else {
			maxdepth = -1;
		}		
		
		if (cLine.hasOption("v"))	{
			verbose = true;
		}		
		
		String filename;
		if (cLine.hasOption("f"))	{
			output = new File(cLine.getOptionValue("f"));
			
		} else {
			String fileStats = p.getProperty("dictFileName");
			filename = String.format(fileStats, locale, categoryID);
			output = new File(System.getProperty("user.dir"), filename);
		}
	}
	
	
	public String getCategoryName()	{
		//TODO check if it's worth
		return null;
	}
	
	public int getCategoryID() {
		return categoryID;
	}
	
	
	public int getMaxDepth() {
		return maxdepth;
	}
	
	public boolean getVerbose() {
		return verbose;
	}
	
	/** 
	 * @return the output defined by the user or a default one, based
	 * 			on the current directory, category id, and language.
	 */
	public File getOutputFile()	{
		return output;
	}	
	
}
