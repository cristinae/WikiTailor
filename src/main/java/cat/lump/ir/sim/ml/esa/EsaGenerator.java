package cat.lump.ir.sim.ml.esa;

import gnu.trove.map.hash.TIntIntHashMap;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cat.lump.ir.lucene.LuceneInterface;
import cat.lump.ir.lucene.query.Document2Query;
import cat.lump.ir.lucene.engine.AnalyzerFactory;
import cat.lump.aq.basics.algebra.vector.Vector;
import cat.lump.aq.basics.check.CHK;


/**A class that allows for passing from a text (collection) 
 * into its ESA vector representation.
 * @author albarron
 * @version 0.1
 * @since Dec 17, 2013
 */
public class EsaGenerator {

	/**Lucene instance*/
	protected Analyzer analyzer;
	
	/**Path to the Lucene index*/
	private File indexPath;
	
	/**Lucene reader instance*/
	private IndexReader reader; 
	
	/**Lucene searcher instance */
	private IndexSearcher searcher;
	
	/**Lucene parser*/
	private QueryParser parser;
	
	/**Dimension of the reference index */
	private int indexDimension;
	
	/**identifiers for the reference corpus documents within the index. */
	private TIntIntHashMap docIds;	
	
	/**Whether the characteristic vector is going to be normalized*/
	private Boolean normaliseVector = false;
		
	private final Document2Query d2q = new Document2Query();
	
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
		setAnalyzer(language);
		setIndexPath(indexPath);
		
		loadIndex();
	}
	
	
	
	/**Set the Lucene analyzer to use according to the given language
	 * @param myAnalyzer
	 */
	public void setAnalyzer(Locale lang){
		analyzer = AnalyzerFactory.loadAnalyzer(lang);
		// Properly implement this if we really wan to analyse a language,
		// even if we don't have its corresponding analyser.
//		try {
//			analyzer = AnalyzerFactory.loadAnalyzer(lang);
//		} catch (){
//			analyzer = AnalyzerFactory.loadAnalyzer(new Locale("en"));
//		}
	}
	
//	private void setLanguage(String lang){
//		CHK.CHECK(! LuceneLanguages.isIndexAvailable(lang), 
//				"No index is available for language " + language);
//		language = new Locale(lang);
//	}
//	
	/**Set the path to Lucene's index
	 * @param index_path
	 */
	public void setIndexPath(File indexPath){
		if (!indexPath.isDirectory()) {
			System.err.print("I cannot read the directory " + indexPath);
			System.exit(1);			
		}
		this.indexPath = indexPath;
	}
	
	/**Loads the Lucene index (previously created) with the reference 
	 * corpus.
	 * @throws IOException
	 */
	public void loadIndex(){
		Directory dir;
		try {
			dir = FSDirectory.open(indexPath);
			reader = IndexReader.open(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		searcher = new IndexSearcher(reader);		
		parser = new QueryParser(LuceneInterface.LUCENE_VERSION, 
								"contents",
								analyzer
					);		
		indexDimension = reader.numDocs();		
		loadDocIds();		
	}
	
	/**
	 * @return the dimension of the index (henceforth, the vector)
	 */
	public int getIndexDimension(){
		return indexDimension;
	}
	
	/**
	 * Loads the identifiers for the documents. Indeed, the index for the 
	 * arrays should be the same as for the index. However, an index document 
	 * could have been deleted, hence leaving behind an empty number.
	 */
	private void loadDocIds()	{		
		docIds = new TIntIntHashMap();		
		int j = 0;
		
		for (int i=0; i<reader.maxDoc(); i++) {
		    if (reader.isDeleted(i))
		        continue;
		    docIds.put(i, j++);
		}	
	}
	
	/**Computes the ESA vector representation for the given text.  
	 *
	 * @param text
	 * @return
	 */
	public Vector computeVector(String text) {
		float[] v = new float[docIds.size()];
		//used to normalize the fields at the end of the computation
		float maxSim = 0.0f;	
		
		boolean emptyQuery = false;
		
		//query the document and retrieve the top hits
		//TODO this is why the Doc2query class does not have an analyzer/language. It is set from here		
		String q = d2q.str2FlatQuery(analyzer, text);		
		try {
			//Necessary if the query is empty (e.g. text ha stopwords only)
			Query query = parser.parse(q);			
			TopDocs hits = searcher.search(query, 100);

			//fill the array with the scores
			for (ScoreDoc scoreDoc : hits.scoreDocs) {			
				v[docIds.get(scoreDoc.doc)] = scoreDoc.score;
				maxSim = Math.max(maxSim, scoreDoc.score);
			}
		} catch (Exception e) {

			emptyQuery = true;
			v = new float[docIds.size()];

		}

		//create the vector and 
		Vector vector = new Vector(v);

		//normalize by max_sim if normalise_vector
		if (normaliseVector && ! emptyQuery)
			vector.divideEquals(maxSim);

		return vector;
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
			searcher.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
}
