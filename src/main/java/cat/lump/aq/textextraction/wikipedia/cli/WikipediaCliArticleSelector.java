package cat.lump.aq.textextraction.wikipedia.cli;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.log.LumpLogger;

/**
 * CLI to access directly the selection of articles step of Xecutor pipeline 
 * for the WikiTailor category-based in-domain comparable corpora extraction.
 * (step 6 of the Xecutor).
 *  
 * @author albarron
 *
 */
public class WikipediaCliArticleSelector extends WikipediaCliMinimum{

		
	/**Depth limit for exploring the tree (root=1)*/
	private short depth;
	
	/**Path to the categories input file */
	private File input;
	
	/**Path to the output directory	 */
	private String outpath;
	
	public WikipediaCliArticleSelector()
	{
		super();
		LABEL = "ArticleSelector";
		logger = new LumpLogger (LABEL);
		Class<WikipediaCliArticleSelector> c = WikipediaCliArticleSelector.class;
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
				"Input file with the categories and their depth"
						+ "(generated with CategoryExtractor)");
		
		options.addOption("d", "depth", true,				
				"Maximum depth to consider (or take the entire tree");
		
		options.addOption("o", "outpath", true,
				"Path to the output directory (default: current)");
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
						
		if (! cLine.hasOption("f"))  //no input file
		{
			exitError(formatter, "I need categories file");
		}
		
		input = new File(cLine.getOptionValue("f"));
		
		if (cLine.hasOption("d"))
		{
			depth = Short.parseShort(cLine.getOptionValue("d"));
			if (depth < 0 ) 
			{
				exitError(formatter, "The depth cannot be negative");				
			}			
		} else {
			depth = Short.MAX_VALUE;
		}
		
		outpath = cLine.hasOption("o") ?
						  cLine.getOptionValue("o")
						: System.getProperty("user.dir")
						;
		
		CHK.CHECK(new File(outpath).isDirectory(), "I cannot access the output directory");
		
	}
			
	public File getCategoryFile()
	{
		return input;
	}
	
	
	public short getDepth()
	{
		return depth;
	}
	
	/**
	 * @return	Path to the output directory
	 */
	public String getOutputPath(){
		return outpath;		
	}
	
	
}
