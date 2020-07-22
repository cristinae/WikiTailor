package cat.lump.aq.textextraction.wikipedia.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;


/**
 * TODO: implement margin-based approach to parallel sentence extraction
 * 
 * @author cristina
 * @since Feb 08, 2019
 * 
 */
public class ParallelSentenceExtractor {
	
	/** Maximum number of files that a folder stores as defined in io.FileManager */
	private static final int IDS_PER_DIR = 100000;
	
	/** Error code*/
	private static final int ERRORINT = 99;
	
	/** Path to the directory where the original articles are stored. */
	private String rootDirectory;
	
	/** Path to the output directory. */
	private String outDirectory;

	/** Tab separated file with the comparable article's IDs and titles */
	private String comparableArtFile;

	/** Pair of languages to extract the sentences*/
	private String l1;
	private String l2;
	
	private static LumpLogger logger = 
			new LumpLogger (ParallelSentenceExtractor.class.getSimpleName());


	public ParallelSentenceExtractor (String inputFile, String inputFileAll, String path, String outpath, String l1, String l2){
		CHK.CHECK_NOT_NULL(inputFile);
		CHK.CHECK_NOT_NULL(path);
		CHK.CHECK_NOT_NULL(outpath);
		this.rootDirectory = path;
		this.outDirectory = outpath;
		this.comparableArtFile = inputFile;
		this.l1 = l1;
		this.l2 = l2;
	}
	

	private void processAll() {

		// Input folder
		File directory = new File(rootDirectory); 
        CHK.CHECK(directory.isDirectory(), "I cannot read the directory " + rootDirectory);	
  
		// Output folder
		File writeDirectory = new File(outDirectory); 
        CHK.CHECK(directory.isDirectory(), "I cannot access the directory " + outDirectory);	

        String [] languages = getLanguages();
        
        // Load the multilingual titles
        // TODO: load from a different file
		List<ArrayList<String>> mlTits = loadFieldCompArts(2, comparableArtFile);
		// Load the multilingual IDs
        List<ArrayList<String>> mlIDs = loadFieldCompArts(1, comparableArtFile);
       
        
        // Detects the desired languages
        int l = 0;
        int idL1=0;
        int idL2=0;
        for (String language : languages ){
        	if (language.equals(l1)){idL1=l;}
        	if (language.equals(l2)){idL2=l;}
        	l++;
        }
		    
        for (ArrayList<String> articleIDs : mlIDs){
        	int ilan = 0;
        	for (String articleID : articleIDs){
        		if (ilan==idL1 || ilan==idL2){
                int index =  Integer.parseInt(articleID)/IDS_PER_DIR;
        		String articleLocation = rootDirectory+languages[ilan]+".0"+ FileIO.separator+"plain"+ FileIO.separator + languages[ilan]
        				+ FileIO.separator + index + FileIO.separator + articleID + "." + languages[ilan] + ".txt";
        		try {
					String [] article = FileIO.fileToLines(new File(articleLocation));
					int j=1;
					for ( String sentence : article){
						String line =  languages[ilan] + "."  + articleID + "."  + j + " " + sentence;
					
						System.out.println(line);	
						j++;
					}
					if (j>15){
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		} //endif language correct
        		ilan++;
        	} //rof 
        } //rof an article
        
		logger.info("END ");
	}
	
	
	/**
	 * Extract the languages from the filename
	 * 
	 * @return
	 */
	private String[] getLanguages() {
		
		String filename = comparableArtFile.substring(comparableArtFile.lastIndexOf("/")+1);    
		filename = filename.replace("union","");
		filename = filename.replace("intersection","");
        String[] parts = filename.split("\\.");
        int i = 0;
		StringBuffer languages = new StringBuffer();
        for (String part : parts) {
        	if (i%2==0){
        		languages.append(part);
        		languages.append(",");
        	}
            i++;
        }
		logger.info(String.format("Parallelisation for languages %s", languages.toString()));
        
		return languages.toString().split(",");
	}

	
    /**
     * Extracts a list with the required field (ID/title) per language for each article in the
     * input file. The input file has been generated in the format given by {@code CommonNamespaceFinder.java}.
     * 
     * @param field (1: ID; 2:title)
	 * @param fileComparable (file with the comparable articles)
     * 
     * @return List<ArrayList<String>> fieldArticles
     */
	private List<ArrayList<String>> loadFieldCompArts(int field, String fileComparable) {

		List<ArrayList<String>> fieldArticles = new ArrayList<ArrayList<String>>();
		try {
			FileInputStream fis = new FileInputStream(new File(fileComparable));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = br.readLine()) != null) { //for every article
				ArrayList<String> list = new ArrayList<String>();   
		        String[] infoArticles = line.split("\t");
		        for (String info : infoArticles){
		        	String[] idTit = info.split("\\s+", 2);
		        	if (field == 1){  // let's store the IDs
		        		list.add(idTit[0]);
		        	} else {  // let's store the titles
		        		String title = idTit[1].trim().replaceAll("_"," ");
		        		list.add(title);		        		
		        	}
		        }
		        fieldArticles.add(list);
			}
			br.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("IO Error with file " + comparableArtFile + " (@loadFieldCompArts())");
		}
		logger.info("Information extracted from input files (field "+field+")");
		return fieldArticles;
	}


	
	
	
	private static CommandLine parseArguments(String[] args)
	{
		
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cLine = null;
		Options options= new Options();
		CommandLineParser parser = new BasicParser();

		options.addOption("h", "help", false, "This help");
		options.addOption("p", "path2root", true,
				    "Absolute Path to the folder where plain/ is (default: current)");
		options.addOption("f", "file", true,
	 				"Absolute path to the input file with the in-domain comparable articles across languages"
						+ "(generated with CommonNamespaceFinder)");
		options.addOption("a", "file", true,
 					"Absolute path to the input file with all the comparable articles across languages"
					+ "(generated with CommonNamespaceFinder)");
		options.addOption("o", "outpath", true,
					"Path to the output directory (default: current)");
		options.addOption("l", "L1", true,
				"Language to extract (iso 639 code)");
		options.addOption("i", "L2", true,
				"Language to extract (iso 639 code)");


		try {			
		    cLine = parser.parse( options, args );
		} catch( ParseException exp ) {
			logger.error( "Unexpected exception:" + exp.getMessage() );			
		}	
		
		if (!(cLine.hasOption("p")) ) {
			logger.error("Please, set the path to the root folder to the articles\n");
			formatter.printHelp(ParallelSentenceExtractor.class.getSimpleName(),options);
			System.exit(1);
		}		
		if (! cLine.hasOption("f")) { //no input file 
			logger.error("An input file with the comparable articles is mandatory\n");
			formatter.printHelp(ParallelSentenceExtractor.class.getSimpleName(),options);
			System.exit(1);
		}		        
		if (cLine.hasOption("h")) {
			formatter.printHelp(ParallelSentenceExtractor.class.getSimpleName(),options );
			System.exit(0);
		}
	
		return cLine;		
	}

	/**
	 * Main function to run the class, serves as example
	 * 
	 * @param args
	 *      -f Input file with the comparable article's IDs and titles
	 *      -l Language to extract parallel sentences for, l1
	 *      -i Language to extract parallel sentences for, l2
	 * 		-o Path to the output folder
	 * 		-p Path to the root folder
	 *      -f path to the input file with the in-domain comparable articles across languages
	 *      -a path to the input file with all the comparable articles across languages
	 */
	public static void main(String[] args) {

		/*CommandLine cLine = parseArguments(args);
		
 		String inputFile =  cLine.getOptionValue("f");	
 		String path = cLine.hasOption("p") ?
				  cLine.getOptionValue("p") : System.getProperty("user.dir");	
		CHK.CHECK(new File(path).isDirectory(), "I cannot access the directory with the in-domain articles");
		String outpath = cLine.hasOption("o") ? 
				  cLine.getOptionValue("o") : System.getProperty("user.dir");
	    CHK.CHECK(new File(outpath).isDirectory(), "I cannot access the output directory");
		String L1 =  cLine.getOptionValue("l");	
 		String L2 =  cLine.getOptionValue("i");	
 	    */
		
	    String L1 = "ca";
	    String L2 = "es";
		String path = "/home/cristinae/pln/WikiTailor/";
		String outpath = "/home/cristinae/pln/WikiTailor/";
		String inputFile = "/home/cristinae/pln/WikiTailor/en.22.ar.22.de.22.es.22.ca.22.fr.22.intersection";
		String inputFileAll = "/home/cristinae/pln/WikiTailor/es.45961.oc.54634.ro.34.ca.4564.union";

		ParallelSentenceExtractor xtractor = new ParallelSentenceExtractor(inputFile, inputFileAll, path, outpath, L1, L2);
		xtractor.processAll();

	}

}
