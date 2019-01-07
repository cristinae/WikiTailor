package cat.lump.aq.textextraction.wikipedia.cli;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.log.LumpLogger;

/**
 * CLI to access directly the extraction of articles step of Xecutor pipeline 
 * for the WikiTailor category-based in-domain comparable corpora extraction.
 * (step 7 of the Xecutor).
 *  
 * @author albarron
 *
 */

public class WikipediaCliArticleTextExtractor extends WikipediaCliMinimum{

	/**File with articles ids */
	private File input;
	
	/**Path to the output directory*/
	private File directory;
	
	public WikipediaCliArticleTextExtractor()
	{
		super();
		LABEL = "ArticleTextExtractor";
		logger = new LumpLogger (LABEL);
		Class<WikipediaCliArticleTextExtractor> c = WikipediaCliArticleTextExtractor.class;
		try {
			File exe = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			command = "java -cp " + exe.getName() + ' ' +c.getCanonicalName();
			footer = "\nEx: "+ command +" -l en -y 2015 -i wikiTailor.ini\n";
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
	protected void loadOptions()
	{
		super.loadOptions();
				
		options.addOption("f", "file", true,
			"Input file with the articles' IDs (generated with ArticleSelector). "
			+ "Entire Wikipedia is extracted if not present");
		
		options.addOption("o", "outpath", true,
				"Output directory (default: current)");
	}
	
	/* (non-Javadoc)
	 * @see cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliMinimum#parseArguments(java.lang.String[])
	 */
	@Override
	public void parseArguments(String[] args)
	{
		super.parseArguments(args);		
		
		CommandLine cLine = parseLine(args);
						
		if (! cLine.hasOption("f"))  //no input file
		{
			input = null;
		} else
		{
			input = new File(cLine.getOptionValue("f"));
		}				
		
		
		directory = new File(cLine.hasOption("o") ?
						  cLine.getOptionValue("o")
						: System.getProperty("user.dir")				
				);		
		CHK.CHECK(directory.isDirectory(), "I cannot read the directory");		
	}
			
	public File getArticlesFile()
	{
		return input;
	}
	
	public File getOutputDir()
	{
		return directory;
	}

}
