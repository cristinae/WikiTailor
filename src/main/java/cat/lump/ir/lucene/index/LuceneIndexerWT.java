package cat.lump.ir.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.ir.lucene.LuceneInterface;
import cat.lump.ir.lucene.cli.LuceneCliIndexerWT;
import cat.lump.ir.lucene.engine.WTAnalyzer;

/**
 * This is an adaptation of class LuceneIndexer to be
 * used for Wikiparable
 * 
 * @author cristina
 * @since June 16, 2015
 *
 */
public class LuceneIndexerWT extends LuceneInterface{
	
	/**Directory where the text files are located*/
  //TODO I think we don't need this to be global
	protected final String dataDir;
	
	/** The name for the field storing the contents */
	public static final String CONTENTS_NAME = "contents";
	
	protected final boolean verbose = false;
	
	private static LumpLogger logger = 
			new LumpLogger(LuceneIndexerWT.class.getSimpleName());
	
//	private final Analyzer analyzer;
	
	private final IndexWriter INDEX_WRITER;
		
	/** Default invocation for English */
	public LuceneIndexerWT(String dataDir, String indexDir){
		this(Locale.ENGLISH, dataDir, indexDir);
	}
	
	public LuceneIndexerWT(Locale language, String dataDir, String indexDir){
		super(indexDir);		
		
		this.dataDir = setDataDir(dataDir);		
		INDEX_WRITER = getIndexWriter(language, indexDir);
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

		//ESBORRAR NOMES MIRO QUE FAIG
		//IndexReader reader = IndexReader.open(writer, false);
		//System.out.print(reader.getTermFreqVector(1, "contents").toString());
	}
	
	/**
	 * Closes the Lucene index and gives the number of indexed documents
	 * @throws IOException
	 */
	public void close() throws IOException{
	  logger.info(String.format(
	      "PROCESS TERMINATED:  %d documents indexed in %d miliseconds", 
	      INDEX_WRITER.numDocs(), System.currentTimeMillis() - PROCESS_START)); 
		INDEX_WRITER.close();	
		
	}
	

	/**
	 * Sets the analyzer as a new instance of WTAnalyzer
	 * @param language 
	 *             The locale for the required language
	 * @return
	 *             An analyzer for the right Lucene version and locale.
	 * 
	 */
	public Analyzer setAnalyzer(Locale language){
		return new WTAnalyzer(LUCENE_VERSION, language);
	}
	
	
	public String setDataDir(String data){
		CHK.CHECK_NOT_NULL(data);		
		if (new File(data).isDirectory()){
			logger.info("Data directory found");
						
		} else {
		   	logger.error("I cannot read the data directory");
		   	System.exit(1);
		}
		return data;
	}
	
	protected Document getDocument(File f) throws IOException{
		Document doc = new Document();
		//TermVector.WITH... allows for getting the vector later on 
		// and potentially allows for computing the cosine similarity between 
		//documents' vectors 		
		
		doc.add(new Field(CONTENTS_NAME, new FileReader(f),	//Index file content
				TermVector.WITH_POSITIONS_OFFSETS)); 
		
		doc.add(new Field("filename", f.getName(),	//Index file name
				Field.Store.YES, Field.Index.NOT_ANALYZED)); 
		
		doc.add(new Field("fullpath", f.getCanonicalPath(),	//Index file full path
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		
		//System.out.println(doc.toString());
		
		return doc;
	}
	
	 /**
   * Checks that the indexPath exists and can be read. Sets up the writier for the 
   * required language and (default) Lucene version.
   * @param language
   *               Language for the required documents
   * @param indexPath
   *               Path to the desired index directory. 
   * @return
   *       An IndexWrited with the necessary configuration.
   */
  protected IndexWriter getIndexWriter(Locale language, String indexPath) {
    IndexWriter idxWriter = null;
    //Setup the index path
    Directory indexDir = null;
    try {
      indexDir = FSDirectory.open(new File(indexPath));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }

    IndexWriterConfig iwc = new IndexWriterConfig(
        setAnalyzer(language)   //the analyser of the considered language
        );
    //We use the simple similarity model (tf-only)
    //    iwc.setSimilarity(new TFSimilarity());    
    //if we want to define a different similarity measure
    //    writer.setSimilarity(new DefaultSimilarity());
    try {           
      idxWriter = new IndexWriter(indexDir, //directory 
          iwc); //configuration
    } catch (CorruptIndexException e) {
      e.printStackTrace();
    } catch (LockObtainFailedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }   //create

    return idxWriter;
  }
  
	private void indexFile(File f) throws IOException{
		Document doc = getDocument(f);
		INDEX_WRITER.addDocument(doc);
	}	
	
	public static class TextFilesFilter implements FileFilter{
		public boolean accept(File path){
			return path.getName().toLowerCase()
					.endsWith(".txt");
		}
	}
	
	public static void main(String[] args) throws IOException{
		
		LuceneCliIndexerWT cli = new LuceneCliIndexerWT();
		cli.parseArguments(args);
		
		Locale lang = cli.getLanguage();
		String inPath = cli.getIn();
		String outPath = cli.getOut();
		boolean verbose = cli.getVerbosity();
		
		LuceneIndexerWT lIndexer;
	    lIndexer = new LuceneIndexerWT(lang, inPath, outPath);
		lIndexer.setVerbose(verbose);//*/
		/*
		String inPath = "/home/cristinae/pln/wikipedia/categories/extractions/ar.0/plain/ar/";
		String outPath = "/home/cristinae/pln/wikipedia/categories/indexes/arNou";

		LuceneIndexerWT lIndexer = new LuceneIndexerWT(new Locale("ar"), inPath, outPath);*/
		
		lIndexer.index();
		lIndexer.close();	
		
	}
	
}
