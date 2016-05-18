package cat.lump.aq.textextraction.wikipedia.cli;

import java.io.File;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.categories.Xecutor;
import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * CLI to access the extraction of parallel sentences from the corpus obtained
 * with WikiTailor textextraction.wikipedia.
 *  
 * TODO implement the extraction of parallel sentences!
 *  
 * @author cristina
 *
 */
public class WikipediaCliFragmentsXecutor extends WikipediaCliMinimum{

	/**Second language involved*/
	private Locale language2;
	
	/**Files with the IDs of the articles for every language*/
	private String[] filesID;
	
	/**Identifiers of the category in the two languages involved*/
	private String sCategory1;
	private int iCategory1;
	private String sCategory2;
	private int iCategory2;
		
	/**Path to the input/output files */
	private String directory;
		
	/**Number of step at which the process begins */
	private int firstStep;
	
	/**Number of step at which the process ends */
	private int endStep;

	/**Method for finding the equivalent articles (union/intersection) */
	private String method;
	
	/**Defines is a translation of the articles is needed or it is already done*/
	private int trad;
	
	/**File containing the union/intersection of articles */
	private String fileCommonArticles;
	
	public WikipediaCliFragmentsXecutor() {
		super();
		LABEL = "Xecutor";
		logger = new LumpLogger (LABEL);
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
		//MANDATORY
		options.addOption("i", "language2", true, 
				"Second language of interest involved (e.g., en, es, ca)");
		//options.addOption("y", "year", true, 
		//			"Dump year (e.g. 2010, 2012, 2013, 2015)");
		options.addOption("f", "files", true, 
				"Files with the IDs of the articles, one for every language" +
				"separated by two commas. Order must correspond to the languages" +
				"\n Ex: -f 13684.ca.9.articles,,5784.eu.6.articles");
		
		//ALTERNATIVELY
		options.addOption("c", "categories", true, 
				"Name of the category in the two languages separated by two commas" +
				" (with '_' instead of ' '; you can use -n instead) " +
				"\n Ex: -c Informàtica,,Informatika");		
		options.addOption("n", "numcat", true,
				"Numerical identifier of the category in the two languages separated " +
				"by two commas (you can use -c instead) " +
				"\n Ex: -n 13684,,5784");
		
		//OPTIONAL
		options.addOption("m", "method", true,
				"Optional: method for finding the equivalent articles union/intersection " +
				"(default: intersection)");
		options.addOption("j", "joined", true,
				"Optional: file containing the union/intersection of articles");
		options.addOption("t", "translation", true,
				"Optional: translation of the articles needed 0/1 " +
				"(default: 0)");
		options.addOption("s", "step", true,
				"Optional: initial step for the process (default: 1)");
		options.addOption("e", "end", true,
				"Optional: last step for the process (default: XX)");
		options.addOption("d", "outpath", true,
				"Optional: input & output directory (default: current)");		
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
		CommandLine cLine = parseLine(args);
		
		if (!cLine.hasOption("i")){				
			logger.error("Two languages are needed for the extraction\n");
			formatter.printHelp(Xecutor.class.getSimpleName(), options);
			System.exit(1);
		} else {
			language2 = new Locale(cLine.getOptionValue("i"));
		}

		if (!cLine.hasOption("f")) {				
			logger.error("Files with the IDs of the articles in every language are needed\n");
			formatter.printHelp(Xecutor.class.getSimpleName(), options);
			System.exit(1);
		} else {
			String files = cLine.getOptionValue("f");       //"13684.ca.9.articles,,5784.eu.6.articles";
			filesID = files.split(",,");
			if (filesID.length != 2) {
				logger.error("Two input files are expected\n");
				formatter.printHelp(Xecutor.class.getSimpleName(), options);
				System.exit(1);					
			}
		}

		if (cLine == null ||
			! ((cLine.hasOption("c") || cLine.hasOption("n")))){				
			logger.error("The categories must be defined either with -c or -n\n");
			formatter.printHelp(Xecutor.class.getSimpleName(), options);
			System.exit(1);
		}
		
		if (cLine.hasOption("h")) {
			formatter.printHelp(Xecutor.class.getSimpleName(), options);
			System.exit(0);
		}		

		if (cLine.hasOption("s")) {
			firstStep = Integer.valueOf(cLine.getOptionValue("s"));
		} else{
			firstStep = 1;
		}
		
		if (cLine.hasOption("e")) {
			endStep = Integer.valueOf(cLine.getOptionValue("e"));
		} else{
			endStep = 7;  //CRIS, vigila
		}

		if (cLine.hasOption("m")) {
			method = cLine.getOptionValue("m");
		} else{
			method = "intersection";
		}

		if (cLine.hasOption("t")) {
			trad = Integer.valueOf(cLine.getOptionValue("t"));
			if (trad!=0 && trad!=1) {
				logger.error("The only valid options for ranslation parameter are 0 and 1\n");
				formatter.printHelp(Xecutor.class.getSimpleName(), options);
				System.exit(1);				
			}  
		} else{
			trad = 0;
		}

		if (cLine.hasOption("j")) {
			fileCommonArticles = cLine.getOptionValue("j");		
			if(!new File(fileCommonArticles).exists()) {
				logger.error("File " + fileCommonArticles + " does not exist");
			};
		} else {
			fileCommonArticles = "";		
		}
 
		if (cLine.hasOption("d")) {
			directory = cLine.getOptionValue("d");			
		} else {
			directory = System.getProperty("user.dir");
		} 

		WikipediaJwpl wiki1;
		WikipediaJwpl wiki2;
		try {
			wiki1 = new WikipediaJwpl(locale, year);
			wiki2 = new WikipediaJwpl(language2, year);
			if (cLine.hasOption("c")) {
				String sCategory = cLine.getOptionValue("c"); //"Informàtica,,Informatika";
				String[] parts = sCategory.split(",,");
				if (parts.length != 2) {
					logger.error("Categories are not properly initialised\n");
					formatter.printHelp(Xecutor.class.getSimpleName(), options);
					System.exit(1);					
				}
				sCategory1 = parts[0]; 
				sCategory2 = parts[1]; 
				iCategory1 = wiki1.getCategory(sCategory1).getPageId();
				iCategory2 = wiki2.getCategory(sCategory2).getPageId();
			} else {
				String iCategory = cLine.getOptionValue("n");       //13684,,5784;
				String[] parts = iCategory.split(",,");
				if (parts.length != 2) {
					logger.error("Categories are not properly initialised\n");
					formatter.printHelp(Xecutor.class.getSimpleName(), options);
					System.exit(1);					
				}
				iCategory1 = Integer.parseInt(parts[0]); 
				iCategory2 = Integer.parseInt(parts[1]); 				
				sCategory1 = wiki1.getCategory(iCategory1).getTitle().getPlainTitle();
				sCategory2 = wiki2.getCategory(iCategory2).getTitle().getPlainTitle();
			}
		} catch (WikiApiException e) {
			e.printStackTrace();
		}				

	}

	
	
	/** getters */

	public Locale getLanguage2() {
		return language2;
	}

	public String getsCategory1()	{
		return sCategory1;
	}

	public int getiCategory1() {
		return iCategory1;
	}
	
	public String getsCategory2()	{
		return sCategory2;
	}

	public int getiCategory2() {
		return iCategory2;
	}
	
	public int getFirstStep()	{
		return firstStep;
	}
	
	public int getLastStep() {
		return endStep;
	}
	
	public int getTranslation()	{
		return trad;
	}

	public String getFileCommonArticles()	{
		return fileCommonArticles;
	}	

	public String getMethod()	{
		return method;
	}	

	public String getDirectory() {
		return directory;
	}	

	public String[] getFilesID() {
		return filesID;
	}	
	
}
