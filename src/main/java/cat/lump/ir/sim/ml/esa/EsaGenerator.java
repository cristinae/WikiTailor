package cat.lump.ir.sim.ml.esa;

import gnu.trove.map.hash.TIntIntHashMap;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similar.MoreLikeThis;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cat.lump.ir.lucene.LuceneInterface;
import cat.lump.ir.lucene.query.Document2Query;
import cat.lump.ir.lucene.engine.AnalyzerFactory;
import cat.lump.ir.lucene.index.LuceneIndexerWT;
import cat.lump.aq.basics.algebra.vector.Vector;
import cat.lump.aq.basics.check.CHK;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.common.logging.Logger;


/**A class that allows for passing from a text (collection) 
 * into its ESA vector representation.
 * @author albarron
 * @version 0.1
 * @since Dec 17, 2013
 */
public class EsaGenerator {

	/**Lucene analyzer instance*/
	protected final Analyzer LUCENE_ANALYZER;
	
	/**Lucene reader instance*/
	private IndexReader INDEX_READER; 
	
	/** Lucene MoreLikeThis instance */
	private final MoreLikeThis MORE_LIKE_THIS;
	
	/**Lucene searcher instance */
	private final IndexSearcher INDEX_SEARCHER;
	
	/**Lucene parser*/
	private final QueryParser QUERY_PARSER;
	
	/**identifiers for the reference corpus documents within the index. */
	private final TIntIntHashMap DOCUMENT_IDS;	
	
	/**Whether the characteristic vector is going to be normalized*/
	private Boolean normaliseVector = false;
		
	private final Document2Query D2Q;
	
	/**Invokes an instance of the EsaGenerator by loading the index and the 
	 * analyzer for the required language
	 * TODO delete this invocation 
	 * @param indexPath
	 * @param language
	 */
//	public EsaGenerator(File indexPath, String language){
//		setLanguage(language);
//		//setAnalyzer();
//		setIndexPath(indexPath);
//		
//		loadIndex();
//	}	
	
	/**Invokes an instance of the EsaGenerator by loading the index and the 
	 * analyzer for the required language
	 * 
	 * @param indexPath
	 * @param language
	 */
	public EsaGenerator(File indexPath, Locale language){		
	  indexPath = checkIndexPath(indexPath);
	  D2Q = new Document2Query();
	  
	  LUCENE_ANALYZER = getAnalyzer(language);

	  Directory dir;
	  try {
	    dir = FSDirectory.open(indexPath);
	    INDEX_READER = IndexReader.open(dir);
	  } catch (IOException e) {
	    e.printStackTrace();
	  }   

	  INDEX_SEARCHER = new IndexSearcher(INDEX_READER);  
	  
	  MORE_LIKE_THIS = new MoreLikeThis(INDEX_READER);
	  MORE_LIKE_THIS.setAnalyzer(LUCENE_ANALYZER);
	  System.out.println(MORE_LIKE_THIS.describeParams());
	  MORE_LIKE_THIS.setMinTermFreq(1);
	  QUERY_PARSER = new QueryParser(LuceneInterface.LUCENE_VERSION, 
	      LuceneIndexerWT.CONTENTS_NAME,
	      LUCENE_ANALYZER
	      );    
	  DOCUMENT_IDS = loadDocIds();   
	}

//	
//	 /**Loads the Lucene index (previously created) with the reference 
//   * corpus.
//   * @throws IOException
//   */
//  public void loadIndex(){
//    Directory dir;
//    try {
//      dir = FSDirectory.open(indexPath);
//      reader = IndexReader.open(dir);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }   
//    
//    searcher = new IndexSearcher(reader);   
//    moreLikeThis = new MoreLikeThis(reader);
//
//    parser = new QueryParser(LuceneInterface.LUCENE_VERSION, 
//                "contents",
//                analyzer
//          );    
//    indexDimension = reader.numDocs();    
//    loadDocIds();   
//  }
	
	
	

	
//	private void setLanguage(String lang){
//		CHK.CHECK(! LuceneLanguages.isIndexAvailable(lang), 
//				"No index is available for language " + language);
//		language = new Locale(lang);
//	}
//	

	

	


	
	/**Computes the ESA vector representation for the given text.  
	 *
	 * @param text
	 * @return
	 */
	public Vector computeVector(String text) {
		float[] v = new float[DOCUMENT_IDS.size()];
		//used to normalize the fields at the end of the computation
		float maxSim = 0.0f;	
		
		boolean emptyQuery = false;
		
		//query the document and retrieve the top hits
		//TODO this is why the Doc2query class does not have an analyzer/language. It is set from here		
		String q = D2Q.str2FlatQuery(LUCENE_ANALYZER, text);		
		//System.out.println(q);
		try {
			//Necessary if the query is empty (e.g. text ha stopwords only)
			Query query = QUERY_PARSER.parse(q);			
			TopDocs hits = INDEX_SEARCHER.search(query, INDEX_SEARCHER.maxDoc());

			//fill the array with the scores
			for (ScoreDoc scoreDoc : hits.scoreDocs) {			
				v[DOCUMENT_IDS.get(scoreDoc.doc)] = scoreDoc.score;
				maxSim = Math.max(maxSim, scoreDoc.score);
			}
		} catch (Exception e) {
//		  System.out.println(e);
		  CHK.CHECK(false, "e");
//			emptyQuery = true;
//			v = new float[DOCUMENT_IDS.size()];

		}

		//create the vector and 
		Vector vector = new Vector(v);

		//normalize by max_sim if normalise_vector
		if (normaliseVector && ! emptyQuery)
			vector.divideEquals(maxSim);
		//System.out.println(vector.length());
		System.out.println(vector.toString());
		return vector;
	}
	
	
	 public Vector computeVectorMoreLikeThis(String text) {
	    float[] v = new float[DOCUMENT_IDS.size()];
	    //used to normalize the fields at the end of the computation
	    float maxSim = 0.0f;  
	    
	    boolean emptyQuery = false;
	    
	    //query the document and retrieve the top hits
	    //TODO this is why the Doc2query class does not have an analyzer/language. It is set from here    
//	    String q = D2Q.str2FlatQuery(LUCENE_ANALYZER, text);   
	    //System.out.println(q);
	    try {
	      //Necessary if the query is empty (e.g. text ha stopwords only)
	      Reader reader = new StringReader(text);
	      Query query = MORE_LIKE_THIS.like(reader, LuceneIndexerWT.CONTENTS_NAME);      
	      TopDocs hits = INDEX_SEARCHER.search(query, INDEX_SEARCHER.maxDoc());

	      //fill the array with the scores
	      for (ScoreDoc scoreDoc : hits.scoreDocs) {      
	        v[DOCUMENT_IDS.get(scoreDoc.doc)] = scoreDoc.score;
	        maxSim = Math.max(maxSim, scoreDoc.score);
	      }
	    } catch (Exception e) {
	      System.out.println(e);
	      emptyQuery = true;
	      v = new float[DOCUMENT_IDS.size()];

	    }

	    //create the vector and 
	    Vector vector = new Vector(v);

	    //normalize by max_sim if normalise_vector
	    if (normaliseVector && ! emptyQuery)
	      vector.divideEquals(maxSim);
	    //System.out.println(vector.length());
	    System.out.println(vector.toString());
	    return vector;
	  }
	
	
	  /**
	   * @return the dimension of the index (henceforth, the vector)
	   */
	  public int getIndexDimension(){
	    return INDEX_READER.numDocs();
	  }
	  
	 
	  public void displayDocIds() {
	    
	    System.out.println(DOCUMENT_IDS);
	  }
	  
	/**Determines whether the ESA vectors are going to be normalised.
	 * <br>
	 * If true, the vectors are normalised by the maximum score in order 
	 * to range the values in [0,1]. 
	 * 
	 * @param norm
	 */
	public void setNormalisation(Boolean norm){
		normaliseVector = norm;
	}
	
	/**
	 * TODO important: check when to call this process instead of trusting the
	 * garbage collector
	 * Once the process is over, the index is closed 	
	 */
	protected void finalize() {				
		try {
			INDEX_SEARCHER.close();
			INDEX_READER.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

  
   /**Set the Lucene analyzer to use according to the given language
   * @param myAnalyzer
   */
  protected Analyzer getAnalyzer(Locale lang){
    return AnalyzerFactory.loadAnalyzer(lang);
    // Properly implement this if we really wan to analyse a language,
    // even if we don't have its corresponding analyser.
//    try {
//      analyzer = AnalyzerFactory.loadAnalyzer(lang);
//    } catch (){
//      analyzer = AnalyzerFactory.loadAnalyzer(new Locale("en"));
//    }
  }
	
  /**Set the path to Lucene's index
   * @param index_path
   * @return 
   */
  private File checkIndexPath(File indexPath){
    if (!indexPath.isDirectory()) {
      System.err.print("I cannot read the directory " + indexPath);
      System.exit(1);     
    }
    return  indexPath;
  }
  
  /**
  * Loads the identifiers for the documents. Indeed, the index for the 
  * arrays should be the same as for the index. However, an index document 
  * could have been deleted, hence leaving behind an empty number.
  * @return 
  */
 private TIntIntHashMap loadDocIds() {   
   TIntIntHashMap ids = new TIntIntHashMap();    
   int j = 0;
   
   for (int i=0; i<INDEX_READER.maxDoc(); i++) {
       if (INDEX_READER.isDeleted(i))
           continue;
       ids.put(i, j++);
   }
   return ids;
 }
  
  
}
