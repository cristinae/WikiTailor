package cat.lump.ir.lucene.index;



import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.ir.lucene.cli.LuceneCliCrossIndexerWT;
import cat.lump.ir.lucene.index.LuceneIndexerWT;

public class LuceneCrossIndexerWT extends LuceneIndexerWT {

  private final Map<Locale, List<Integer>> FILES;
  
  private final Locale KEY_LAN;
  
  private static LumpLogger logger = 
      new LumpLogger(LuceneCrossIndexerWT.class.getSimpleName());
  
  public LuceneCrossIndexerWT (String csvFile, String dataDir, String indexDir, Locale keyLan) throws IOException {
    super(dataDir, indexDir);
    FILES = loadIds(csvFile);
    KEY_LAN = keyLan;
    
  }
  


  
  public void index() throws IOException {
    //List<Integer> ids = FILES.get(KEY_LAN);
    // AQUI VOY: cargar el índice de todas las lenguas con los ids del key
    for (Locale lan : FILES.keySet()) {
      index (lan);
    }
  }
  

  
  private void index(Locale lan) throws IOException {
    
    Map<Integer, Integer> mapping = getIdsMapping(FILES.get(KEY_LAN), FILES.get(lan)); 
    
    List<String> files = FileIO.getFilesRecursively(new File(getInputPath(lan)), ".txt");
    File f;
    
    IndexWriter iw = getIndexWriter(lan, getIndexDir(lan));
    for (String sF : files) {
      f = new File(sF);
      if (!f.isDirectory() &&
          !f.isHidden() &&
          f.exists() &&
          f.canRead() ) {
        // TODO check if the ids are correct and the same across languages
        Document doc = getDocument(f, mapping.get(getIdFromFile(sF, lan)));
        iw.addDocument(doc);
        if (verbose){
          logger.info("Indexing document " + f.getName());
        }
      }
    }
    logger.info(String.format(
        "%d documents indexed for language %s", 
        iw.getDocStats().numDocs, lan)); 
    iw.close();
  }
  
  protected Document getDocument(File f, int id) throws IOException{
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
		Field stringField = new Field("filename", String.valueOf(id), sft);
		doc.add(stringField); 
		
		FieldType pft = new FieldType(StringField.TYPE_STORED);
		Field pathField = new Field("fullpath", f.getCanonicalPath(), pft);
		
		doc.add(pathField);
		
		return(doc);
    
  }
  
  private int getIdFromFile(String file, Locale lan) {
    int separatorIndex = file.lastIndexOf(File.separator);
//    separatorIndex = (separatorIndex == -1) ? 0 : separatorIndex;
    int suff = file.indexOf(String.format("%s.txt", lan.toString()));
    
    System.out.println(file.substring(separatorIndex +1, suff -1));
    return Integer.valueOf(file.substring(separatorIndex +1, suff -1));
  }
    
  
  /**
   * Passes from two lists of the same size to a map
   * @param keys
   *            list of key ids
   * @param current
   *            list of current language ids
   * @return
   *            Map<current, key>
   */
  private Map<Integer, Integer> getIdsMapping(List<Integer> keys, List<Integer> current) {
    CHK.CHECK(keys.size()==current.size(), "Mismatch in th length of the input ids");
    Map<Integer, Integer> ids = new TreeMap<Integer, Integer> ();
    for (int i = 0; i<keys.size(); i++) {
      ids.put(current.get(i), keys.get(i));
    }
    return ids;
    
    
  }
  
  private String getIndexDir(Locale lan) {
    return String.format("%s%s%s", indexDir, File.separator, lan.toString());
  }

  private String getInputPath(Locale lan) {
    return String.format("%s%s%s", dataDir, File.separator, lan.toString());
  }
  
  
  
  /**
   * Load the ids for the relevant languages from the csv file.
   * The file is expected to have the language as header ISO 639-1 
   * @param csvFile
   * @return
   * @throws IOException
   */
  private Map<Locale, List<Integer>> loadIds(String csvFile) throws IOException {
    Map<Locale, List<Integer>> ids = new LinkedHashMap<Locale, List<Integer>>();
//    Reader in = new FileReader(csvFile);
    CSVParser parser = CSVParser.parse(new File(csvFile), Charset.defaultCharset(), 
        CSVFormat.TDF.withFirstRecordAsHeader());
    Map<String, Integer> headers = new TreeMap<String, Integer> ();
    headers.putAll(parser.getHeaderMap());
    for (String lan : headers.keySet()) {
      ids.put(new Locale(lan), new ArrayList<Integer>());
    }
    
    for (CSVRecord record : parser) {
      for (Locale lan : ids.keySet()) {
        ids.get(lan).add(Integer.valueOf(record.get(lan.toString())));
      }
    }
    return ids;
  }
  
  
  
  public static void main(String[] args) throws IOException { 
    LuceneCliCrossIndexerWT cli = new LuceneCliCrossIndexerWT();
    cli.parseArguments(args);
  
    String csvFile = cli.getCsvFile();
    String inPath = cli.getIn();
    String outPath = cli.getOut();
    Locale keyLang = cli.getLanguage();
    boolean verbose = cli.getVerbosity();
    
    LuceneCrossIndexerWT cIndexer = new LuceneCrossIndexerWT(csvFile, inPath, outPath, keyLang);
    cIndexer.setVerbose(verbose);
    
    
    cIndexer.index();
  }
  
  
}
