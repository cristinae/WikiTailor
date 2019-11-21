package cat.lump.ir.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexOptions;
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
	      INDEX_WRITER.getDocStats().numDocs, System.currentTimeMillis() - PROCESS_START)); 
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
		// TODO duplicated from LuceneIndexer
		// FieldType s the new way to set the configuration. Before, in version 3.6, 
		// we had 
		// doc.add(new Field(CONTENTS_NAME, new FileReader(f),	//Index file content
		//		TermVector.WITH_POSITIONS_OFFSETS));
		// Now we simply set this up in FieldType
		
		Document doc = new Document();	
		
		FieldType tft = new FieldType(TextField.TYPE_STORED);
		tft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		Field textField = new Field(CONTENTS_NAME, new FileReader(f), tft);
		doc.add(textField);
		 
		// TODO uncertain whether we need TYPE_STORED 
		FieldType sft = new FieldType(StringField.TYPE_STORED);
		//tft.setIndexOptions(IndexOptions.);
		Field stringField = new Field("filename", f.getName(), sft);
		doc.add(stringField); 
		
		FieldType pft = new FieldType(StringField.TYPE_STORED);
		Field pathField = new Field("fullpath", f.getCanonicalPath(), pft);
		
		doc.add(pathField);
		
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
    	Path path = FileSystems.getDefault().getPath(indexPath);
    	indexDir = FSDirectory.open(path);
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
