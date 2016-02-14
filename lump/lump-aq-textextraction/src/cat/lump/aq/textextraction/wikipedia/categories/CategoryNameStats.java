package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.prepro.TermExtractor;

/**
 * This class computes the percentage of categories that are claimed to belong 
 * to a concrete domain from a category tree. In order to do so, the vocabulary 
 * of that category should be included. It can be generated with 
 * <code>DomainKeywords</code>
 * 
 * TODO There is no cli: implement to make it as the other classes
 * TODO Read statsFileName from config
 * 
 * @author jboldoba
 * @see DomainKeywords
 */
public class CategoryNameStats {
	
	private final static String statsFileName = "%s.%d.stats";
	
	/** Language of the Wikipedia connector */
	private Locale locale;	

	/** List of terms defining the domain keywords */
	private Set<String> dictionary;
	
	/** Set of categories indexed by its names. */
	private Map<Integer, HashSet<String[]>> catNames; // <Depth,Names>
	
	private Map<Integer, Double> stats;

	private static LumpLogger logger = 
			new LumpLogger (CategoryNameStats.class.getSimpleName());

	public CategoryNameStats(Locale locale) {
		stats = new HashMap<Integer, Double>();
		setLang(locale);
	}
	
	/**
	 * Reads the "Domain Key Words" dictionary
	 * 
	 * @param dict File which contains the dictionary
	 * @throws IOException
	 */
	public void loadDictionary(File dict) throws IOException {
		logger.info("Loading dictionary: " + dict.toString());
		String[] words = FileIO.fileToLines(dict);
		//WARNING: as originally this file had only one column
		dictionary = new HashSet<String>();
		
		for (String line : words)	{
			dictionary.add(line.split("\t")[1]);
		}
	}

	public void loadDictionary(HashSet<String> dictionary){
		CHK.CHECK_NOT_NULL(dictionary);
		this.dictionary = dictionary;
	}
	
	/**
	 * Reads the file with the list of categories
	 * 
	 * TODO a loader from an object. In order to do that, the CategoryTreeNode
	 * should be fixed as for calling and use it here 
	 * 
	 * @param cat  File with one category per line
	 * @throws IOException
	 */
	public void loadCategoryNames(File cat) throws IOException {
		logger.info("Loading category names: " + cat.toString());
		String[] lines = FileIO.fileToLines(cat);
		catNames = new HashMap<Integer, HashSet<String[]>>();
		
		for (String line : lines)	{
			String[] items = line.split("\t");
			// items[0] = depth ; items[1] = category_name : items[2..N-1] other stuff
			Integer depth = Integer.parseInt(items[0]);
			String[] nameWords = items[2].split("_");
			if (catNames.containsKey(depth)) {
				// Add new name to that depth
				catNames.get(depth).add(nameWords);
			}	else {
				// Add new depth
				HashSet<String[]> names = new HashSet<String[]>();
				names.add(nameWords);
				catNames.put(depth, names);
			}
		}
	}

	/**
	 * Calculates and returns the percentages.
	 * 
	 * @return A map of percentages indexed by the category IDs
	 */
	public void computeStats()
	{
		logger.info("Computing statistics");
		stats = new HashMap<Integer, Double>();

		for (Map.Entry<Integer, HashSet<String[]>> entry : catNames.entrySet())	{
			if (entry.getKey() == 1) {   //By definition the pseudo-root category is of the domain
				stats.put(entry.getKey(), 1.0);
			} else {
				double accuracy = calculateAccuracy(entry.getValue());
				stats.put(entry.getKey(), accuracy);
			}
		}		
	}

	/**
	 * Checks if a category name "belongs" to the domain.
	 * 
	 * @param title  Name of the category to check
	 * @return If the category name belongs to the domain, it returns
	 *         <code>true</code>. Otherwise, it returns <code>false</code>
	 */
	public boolean isDomain(String[] title) {
		boolean isDomain = false;
		StringBuffer sb = new StringBuffer();
		
		for (String word : title)	{
			sb.append(word).append(" ");
		}

		CHK.CHECK_NOT_NULL(sb.toString());
		TermExtractor te = new TermExtractor(locale);
		List<String> tokens = te.getTerms(sb.toString(), 4);
		
		Iterator<String> iter = tokens.iterator();		
		while (iter.hasNext() && !isDomain)	{
			String next = iter.next();
			isDomain = dictionary.contains(next);
		}
		return isDomain;
	}
	
	public void toFile(File categoryFile){		
		StringBuffer statsText = new StringBuffer();
		Map<Integer, Double> treeMapStats = new TreeMap<Integer, Double>(stats);
		
		for (Map.Entry<Integer, Double> entry : treeMapStats.entrySet()) {
			String line = String.format(Locale.ENGLISH,"%d %f", 
			    entry.getKey(),	entry.getValue());
			statsText.append(line).append("\n");
		}
		//String output = String.format(statsFileName, locale, iCategory);
		//String output = categoryFile + ".stats";
		try {
			FileIO.stringToFile(categoryFile, statsText.toString(), false);
		} catch (IOException e) {			
			e.printStackTrace();
		}		
		logger.info("Stats dumped into  " + categoryFile.toString());		
	}

	 /**
   * Estimates how related a group of categories are to a domain
   * 
   * These computations are carried out as follows:
   * <ol>
   * <li> Count the number of categories of the group belonging to the
   * domain. (at least one word of the title matches a domain key word).</li>
   * <li> Compute the percentage represented by the number of categories
   * accounted for as belonging to the domain over the total of processed
   * categories</li>
   * </ol>
   * 
   * @param names Set of names of categories to process.
   * @return The estimated percentage for this group of names
   */
  private double calculateAccuracy(HashSet<String[]> names) {
    double domainSize = 0;
    Iterator<String[]> iter = names.iterator();
    while (iter.hasNext()) {
      String[] current = iter.next();
      if (isDomain(current)) {
        domainSize += 1;
      }
    }
    return domainSize / ((double) names.size());
  }

	
	private void setLang(Locale lang) {
		CHK.CHECK_NOT_NULL(lang);
		this.locale = lang;
	}
	
	private static CommandLine parseArguments(String[] args) {
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cLine = null;
		Options options= new Options();
		CommandLineParser parser = new BasicParser();

		options.addOption("l", "language", true, 
					"Language of interest (e.g., en, es, ca)");
		options.addOption("c", "categories", true, 
				"File with the per-depth categories (generated with CategoryExtractor)");
		options.addOption("d", "dictionary", true, 
					"File with the dictionary (generated with DomainKeywords)");
		options.addOption("h", "help", false,
				"This help");
		try {			
		    cLine = parser.parse( options, args );
		} catch( ParseException exp ) {
			logger.error( "Unexpected exception:" + exp.getMessage() );			
		}	
		
		if (cLine == null ||
			! ((cLine.hasOption("l") && cLine.hasOption("c") && cLine.hasOption("d"))))
		{
			logger.error("Please, set the three required parameters\n");
			formatter.printHelp(CategoryNameStats.class.getSimpleName(), 
								options);
			System.exit(1);
		}		
		
		if (cLine.hasOption("h"))	{
			formatter.printHelp(CategoryNameStats.class.getSimpleName(),
								options );
			System.exit(0);
		}	
		return cLine;		
	}
	
	
//
//	public void computeFromFiles(File categoryFile, File dictFile){
//		
//	}
	
	public static void main(String[] args)
	{
		
		CommandLine cLine = parseArguments(args);
		
		String language = cLine.getOptionValue("l");
		String categoryFile = cLine.getOptionValue("c");
		String dictFile = cLine.getOptionValue("d");

		CategoryNameStats cns;
		try	{
			cns = new CategoryNameStats(new Locale(language));
			//cns.computeFromFiles(categoryFile, dictFile);			
			
			//TODO load them from objects								
			cns.loadDictionary(new File(dictFile));			
			cns.loadCategoryNames(new File(categoryFile));		
			cns.computeStats();
			File fileStats = new File(
			    String.format(statsFileName, language, categoryFile));
			cns.toFile(fileStats);
			
//			logger.info("Dumping stats");
//			StringBuffer statsText = new StringBuffer();
//			for (Map.Entry<Integer, Double> entry : stats.entrySet())
//			{
//				String line = String.format("%d %f", entry.getKey(),
//						entry.getValue());
//				statsText.append(line).append("\n");
//			}
//			String output = categoryFile + ".stats";
//			FileIO.stringToFile(new File(output), statsText.toString(), false);
		} catch (IOException e) 	{
			e.printStackTrace();
		}
		logger.info("End of the process");

	}
}
