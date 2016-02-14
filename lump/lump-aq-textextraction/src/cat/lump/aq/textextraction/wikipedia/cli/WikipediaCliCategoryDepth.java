package cat.lump.aq.textextraction.wikipedia.cli;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import cat.lump.aq.basics.log.LumpLogger;

/**
 * CLI to access directly the estimation of the category depth step of Xecutor pipeline 
 * for the WikiTailor category-based in-domain comparable corpora extraction.
 * (step 4 of the Xecutor).
 *  
 * @author cristina
 *
 */
public class WikipediaCliCategoryDepth extends WikipediaCliMinimum{

	/**Path of the input file */
	private File input;

	/**Percentage of positive categories wanted*/
	private double percentage;
	
	/** For alternative options */
	/**Identifier of the category */
	private int categoryID;	
	
	/** Minimum depth considered to extract the corpus */
	private int minDepth;
	/** Maximum depth considered for a category tree */
	private int maxDepth;
	
	
	public WikipediaCliCategoryDepth() {
		super();
		LABEL = "CategoryDepth";
		logger = new LumpLogger (LABEL);
		Class<WikipediaCliCategoryDepth> c = WikipediaCliCategoryDepth.class;
		try {
			File exe = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			command = "java -cp " + exe.getName() + ' ' +c.getCanonicalName();
			footer = "\nEx: "+ command +" -l en -y 2015 -i wikiTailor.ini -f en.45613.stats \n";
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads additional options: percentage of categories with keywords, input file,
	 * depth and category
	 */
	@Override
	protected void loadOptions()
	{
		super.loadOptions();
						
		options.addOption("m", "model", true,
				"Percentage of in-domain categories");		
		
		options.addOption("f", "file", true,
				"Input file with the depth and percentage of positive categories"
						+ " (generated with NameStats)");

		options.addOption("u", "maxDepth", true,				
				"Maximum depth considered  for a category tree");

		options.addOption("d", "minDepth", true,				
				"Minimum depth considered to extract the corpus");

		options.addOption("n", "numcategory", true,
				"Numerical identifier of the category");
	}

	/* (non-Javadoc)
	 * @see cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliMinimum#parseArguments(java.lang.String[])
	 */
	@Override
	public void parseArguments(String[] args)
	{
		super.parseArguments(args);
		
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cLine = parseLine(args);
						
		if ( !cLine.hasOption("f") || 
			 (!cLine.hasOption("l") && !cLine.hasOption("y") && 
			  !cLine.hasOption("n") && !cLine.hasOption("d")) ) {
			     exitError(formatter, "I need either the name of the input file" +
		  	     " or language/year/depth/catID to generate it");
		}

		if (cLine.hasOption("m")) {
			percentage = Double.parseDouble(cLine.getOptionValue("p"));
		} else {
			percentage = Double.valueOf(p.getProperty("percentage"));
		}
		
		if (cLine.hasOption("n")) {
			categoryID = Integer.parseInt(cLine.getOptionValue("n")); 
		}		
		
		if (cLine.hasOption("u")){
			maxDepth = Integer.parseInt(cLine.getOptionValue("u"));
		} else {
			maxDepth = Integer.parseInt(p.getProperty("maxDepth"));
		}
				
		if (cLine.hasOption("d")){
			minDepth = Integer.parseInt(cLine.getOptionValue("d"));
		} else {
			minDepth = Integer.parseInt(p.getProperty("minDepth"));
		}
		

		if (cLine.hasOption("f")){
			input = new File(cLine.getOptionValue("f"));			
		} else {
			String fileStats = p.getProperty("statsFileName");
			String lang = locale.toString();
			input = new File(System.getProperty("user.dir"), String.format(
					fileStats, lang, categoryID));
		} 
				
	}
		
	
	/** getters **/
	
	public double getPercentage() {
		return percentage;
	}
	
	public File getStatsFile() {
		return input;
	}
		
	public int getminDepth() {
		return minDepth;
	}
		
	public int getmaxDepth() {
		return maxDepth;
	}

	public int getCategoryID() {
		return categoryID;
	}

}
