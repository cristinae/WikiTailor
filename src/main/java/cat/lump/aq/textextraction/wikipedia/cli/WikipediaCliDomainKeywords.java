package cat.lump.aq.textextraction.wikipedia.cli;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;

import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * CLI to access directly the extraction of domain keywords step of Xecutor pipeline 
 * for the WikiTailor category-based in-domain comparable corpora extraction.
 * (step 1 of the Xecutor).
 *  
 * @author cristina
 *
 */
public class WikipediaCliDomainKeywords extends WikipediaCliMinimum{

	/**Identifier of the category */
	private int categoryID;	
	
	/**Number of elements required (pctge.) */
	private int top;
	
	/**Path to the output file */
	private File output;
	
	public WikipediaCliDomainKeywords()	{
		super();
		LABEL = "DomainKeywords";
		Class<WikipediaCliDomainKeywords> c = WikipediaCliDomainKeywords.class;
		try {
			File exe = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			command = "java -cp " + exe.getName() + ' ' +c.getCanonicalName();
			footer = "\nEx: "+ command +" -l en -y 2015 -i wikiTailor.ini -c Science \n";
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
		options.addOption("t", "top", true,
			"Top-k number of words required in pctge");
		options.addOption("f", "file", true,
			String.format("Output file. A file in the current directory will be generated if not"
					+ "provided (e.g., %s/es.49468.dict", System.getProperty("user.dir")));	

	}
	
	/* (non-Javadoc)
	 * @see cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliMinimum#parseArguments(java.lang.String[])
	 */
	@Override
	public void parseArguments(String[] args)	{
		super.parseArguments(args);
		
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cLine = parseLine(args);
					
		if (! (cLine.hasOption("c") || cLine.hasOption("i"))) { //no string nor int category
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
		
		if (cLine.hasOption("t"))	{
			top = Integer.parseInt(cLine.getOptionValue("t"));
			if (top < 0 || top > 100)	{
				top = Integer.valueOf(p.getProperty("topPercentage"));
			}			
		} else{
			top = Integer.valueOf(p.getProperty("topPercentage"));
		}	
		
		String filename;
		if (cLine.hasOption("f"))	{
			output = new File(cLine.getOptionValue("f"));
		} else {
			String fileDict = p.getProperty("dictFileName");
			filename = String.format(fileDict, locale, categoryID);
			output = new File(System.getProperty("user.dir"), filename);
		}
	}
	
	public String getCategoryName() {
		//TODO check if it's worth
		return null;
	}
	
	public int getCategoryID() {
		return categoryID;
	}

	public int getTop()	{
		return top;
	}
	
	/** 
	 * @return
	 * 			the output defined by the user or a default one, based
	 * 			on the current directory, category id, and language.
	 */
	public File getOutputFile()	{
		return output;
	}	
	
}
