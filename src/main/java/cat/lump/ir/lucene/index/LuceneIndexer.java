package cat.lump.ir.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.ca.CatalanAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.eu.BasqueAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.lv.LatvianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.lucene.LuceneInterface;
import cat.lump.ir.lucene.index.analyzers.CroatianAnalyzer;
import cat.lump.ir.lucene.index.analyzers.EstonianAnalyzer;
import cat.lump.ir.lucene.index.analyzers.LithuanianAnalyzer;
import cat.lump.ir.lucene.index.analyzers.SlovenianAnalyzer;

/**
 * An indexer based on Lucene in Action 2nd edition.
 * A few adaptations were necessary to make it work with 
 * Lucene 3.5.0. 
 * <br>
 * 
 * Note that in order to add a new document to an already
 * existing index it is only necessary to run the code again
 * considering the same path to the already existing index.
 * <br>
 * 
 * Special attention has to be paid respect to the rest of 
 * libraries because it seems like JWPL's Lucene modules 
 * included (which are older) are sometimes considered.
 * 
 * @author albarron
 *
 */
public class LuceneIndexer extends LuceneInterface{
	
//	protected int n_gram_length;
	
	/**Directory where the text files are located*/
	private String dataDir;
	
	/**Directory where the Lucene index has to be stored*/
		
	protected boolean verbose = false;
	
	private final Version LUCENE_VERSION = Version.LUCENE_35;
	
	private Analyzer analyzer;
	
	private IndexWriter writer;
	
	private int numIndexed = 0;
	
	/** Default invocation for English */
	public LuceneIndexer(String dataDir, String indexDir){
		this(Locale.ENGLISH, dataDir, indexDir);
	}
	
	public  LuceneIndexer(Locale language, String dataDir, String indexDir){
		super(indexDir);		
		setLanguage(language);
		setAnalyzer();
		setDataDir(dataDir);
		
		
		Directory dir = null;
		try {
			dir = FSDirectory.open(new File(indexDir));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		IndexWriterConfig iwc = new IndexWriterConfig(
				LUCENE_VERSION, 
				analyzer 	//the analyser of the considered language
				);
		//We use the simple similarity model (tf-only)
//		iwc.setSimilarity(new TFSimilarity());		
		//if we want to define a different similarity measure
//		writer.setSimilarity(new DefaultSimilarity());
		try {			 			
			writer = new IndexWriter(dir,	//directory 
									iwc);	//configuration
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		//create		
		
	}
	
	
	public void index() throws IOException{
		index(dataDir, new TextFilesFilter());		
	}
	
	/**
	 * Open an index and start file directory traversal
	 * @param dataDir
	 * @param filter
	 * @throws IOException 
	 */
	public void index(String dataDir, FileFilter filter) 
			throws IOException{
		
		List<String> files = FileIO.getFilesRecursively(new File(dataDir), ".txt");
				//new File(dataDir).listFiles();
		File f;
		for (String sF : files) {
			f = new File(sF);
			if (!f.isDirectory() &&
					!f.isHidden() &&
					f.exists() &&
					f.canRead() &&
					(filter == null || filter.accept(f))) {
				indexFile(f);
				if (verbose){
					logger.info("Indexing document " + f.getName());
				}
				
			}
		}
		numIndexed = writer.numDocs();
	}
	
	/**Closes the Lucene index
	 * @throws IOException
	 */
	public void close() throws IOException{
		writer.close();	
		logger.info(String.format(
			"PROCESS TERMINATED\n %d documents indexed in %d miliseconds", 
			numIndexed, System.currentTimeMillis() - PROCESS_START));	
	}
	
	/**
	 * @param language
	 * @param indexDir
	 * @return
	 */
	public void setAnalyzer(){
		switch (lan.getLanguage()) {
			case "ar":	
				analyzer = new ArabicAnalyzer(LUCENE_VERSION);
				break;			
			case "bg":	
				analyzer = new BulgarianAnalyzer(LUCENE_VERSION);
				break;
			case "ca":	
				analyzer = new CatalanAnalyzer(LUCENE_VERSION);
				break;
			case "cs":	
				analyzer = new CzechAnalyzer(LUCENE_VERSION);
				break;				
			case "de":	
				analyzer = new GermanAnalyzer(LUCENE_VERSION);
				break;
			case "el":	
				analyzer = new GreekAnalyzer(LUCENE_VERSION);
				break;
			case "en":	
				analyzer = new StandardAnalyzer(LUCENE_VERSION);
				break;
			case "es":	
				analyzer = new SpanishAnalyzer(LUCENE_VERSION);
				break;
			case "et":	
				analyzer = new EstonianAnalyzer(LUCENE_VERSION);
				break;
			case "eu":	
				analyzer = new BasqueAnalyzer(LUCENE_VERSION);
				break;
			case "pt":	
				analyzer = new PortugueseAnalyzer(LUCENE_VERSION);
				break;
			case "fr":	
				analyzer = new FrenchAnalyzer(LUCENE_VERSION);
				break;
			case "hr":	
				analyzer = new CroatianAnalyzer();
				break;
			case "lt":	
				analyzer = new LithuanianAnalyzer();
				break;
			case "lv":	
				analyzer = new LatvianAnalyzer(LUCENE_VERSION);
				break;
			case "ro":	
				analyzer = new RomanianAnalyzer(LUCENE_VERSION);
				break;
			case "sl":	
				analyzer = new SlovenianAnalyzer();
				break;
			default:	
				logger.warn("I cannot process the required language. "
						+ "English used.");
				analyzer = new StandardAnalyzer(LUCENE_VERSION);
		}		
	}
	
	public void setDataDir(String data){
		CHK.CHECK_NOT_NULL(data);		
		if (new File(data).isDirectory()){
			logger.info("Data directory found");
			dataDir = data;			
		} else {
		   	logger.error("I cannot read the data directory");
		   	System.exit(1);
		}		    
	}
	
	protected Document getDocument(File f) throws IOException{
		Document doc = new Document();
		//TermVector.WITH... allows for getting the vector later on 
		// and potentially allows for computing the cosine similarity between 
		//documents' vectors 		
		
		doc.add(new Field("contents", new FileReader(f), 
				TermVector.WITH_POSITIONS_OFFSETS)); 	//Index file content
		
		doc.add(new Field("filename", f.getName(),		//Index file name
				Field.Store.YES, Field.Index.NOT_ANALYZED)); 
		
		doc.add(new Field("fullpath", f.getCanonicalPath(),	//Index file full path
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		
		return doc;
	}


//	/**Sets the level of the desired n-gram
//	 * TODO still not applied
//	 * @param n
//	 */
//	public void setNgramLength(int n){
//		//in order to generate indexes of different lengths of n.
//		this.n_gram_length = n;
//	}
	
	
	private void indexFile(File f) throws IOException{
		//System.err.println("Indexing " + f.getCanonicalPath());
		Document doc = getDocument(f);
		writer.addDocument(doc);
	}	
	
	public static class TextFilesFilter implements FileFilter{
		public boolean accept(File path){
			return path.getName().toLowerCase()
					.endsWith(".txt");
		}
	}
	
	public static void main(String[] args) throws IOException{
		
		// create the command line parser
		CommandLineParser parser = new BasicParser();
		
		LuceneIndexer lIndexer;

		// create the Options
		Options options = new Options();
		options.addOption("i", "input", true, 
					"Directory with the input files (one document per file)");
		options.addOption("o", "output", true, 
					"Directory to save the index to");		
		options.addOption("l", "language", true, 
				"Language. One among ar, ca, de, el, en, es, et, eu, "
						+ "fr, hr, lt, lv, ro (Default: en)");
		options.addOption("v", "verbose", false,
					"Verbose execution");
		
		// parse the command line arguments
		CommandLine line = null;
		try {
		    line = parser.parse( options, args );
		} catch( ParseException exp ) {
			System.out.println( "Unexpected exception:" + exp.getMessage() );
		}
		    
		HelpFormatter formatter = new HelpFormatter();
	    // validate that block-size has been set
	    if(! line.hasOption( "i" ) || ! line.hasOption("o")) {
	    	System.out.println("Provide input and output directories " +
	    								"as requested");
	    	formatter.printHelp( "Indexer", options );
	    	System.exit(1);
	    }
	    lIndexer = new LuceneIndexer(new Locale(line.getOptionValue("l")), 
	    		line.getOptionValue("i"), 
	    		line.getOptionValue("o"));
					
		lIndexer.setVerbose(line.hasOption("v"));
		
		lIndexer.index();
		lIndexer.close();	
		
	}
	
	
//	public void setLanguage(String sLan){
//		CHK.CHECK_NOT_NULL(sLan);
//		lan = new Locale(sLan);
//	}
	
//	public static LuceneIndexerAbstract getIndexerAr(String indexDir){
//		return new LuceneIndexerAr(indexDir);
//	}
//	
//	public static LuceneIndexerAbstract getIndexerCa(String indexDir){
//		return new LuceneIndexerCa(indexDir);
//	}
//		
//	/**
//	 * @param indexDir
//	 * @return
//	 */
//	public static LuceneIndexerAbstract getIndexerDe(String indexDir){
//		return new LuceneIndexerDe(indexDir);
//	}
//	public static LuceneIndexerAbstract getIndexerEl(String indexDir){
//		return new LuceneIndexerEl(indexDir);
//	}	
//	public static LuceneIndexerAbstract getIndexerEn(String indexDir){
//		return new LuceneIndexerEn(indexDir);
//	}
//	public static LuceneIndexerAbstract getIndexerEs(String indexDir){
//		return new LuceneIndexerEs(indexDir);
//	}
//	
//	public static LuceneIndexerAbstract getIndexerEu(String indexDir){
//		return new LuceneIndexerEu(indexDir);
//	}
//	
//	public static LuceneIndexerAbstract getIndexerEt(String indexDir){
//		return new LuceneIndexerEt(indexDir);
//	}
//	public static LuceneIndexerAbstract getIndexerFr(String indexDir){
//		return new LuceneIndexerFr(indexDir);
//	}
//	public static LuceneIndexerAbstract getIndexerHr(String indexDir){
//		return new LuceneIndexerHr(indexDir);
//	}
//	public static LuceneIndexerAbstract getIndexerLt(String indexDir){
//		return new LuceneIndexerLt(indexDir);
//	}
//	public static LuceneIndexerAbstract getIndexerLv(String indexDir){
//		return new LuceneIndexerLv(indexDir);
//	}
//	
//	public static LuceneIndexerAbstract getIndexerRo(String indexDir){
//		return new LuceneIndexerRo(indexDir);
//	}
//	
//	public static LuceneIndexerAbstract getIndexerSl(String indexDir){
//		return new LuceneIndexerSl(indexDir);
//	}
}
