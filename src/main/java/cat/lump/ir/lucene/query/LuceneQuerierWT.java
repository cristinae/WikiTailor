package cat.lump.ir.lucene.query;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.ir.lucene.LuceneInterface;
import cat.lump.ir.lucene.cli.LuceneCliQuerierWT;


/**
 * Query into Lucene Indexes for WikiTailor. Given a query and the indexes it
 * returns a LinkedHashMap<Float, String> with the pairs (score, filename) for the
 * top TOP documents retrieved. Also a string with only the IDs of the documents
 * can be obtained with queryIDs.
 * 
 * @author cristina
 * @since June 30, 2015
 *
 */
public class LuceneQuerierWT extends LuceneInterface{

	private static LumpLogger logger = 
			new LumpLogger(LuceneQuerierWT.class.getSimpleName());
			
	private final Version LUCENE_VERSION = Version.LUCENE_35;
	
	private Analyzer analyzer;
	
	/**Lucene reader instance*/
	private IndexReader reader; 
	/**Lucene searcher instance */
	private IndexSearcher searcher;
	/**Lucene parser*/
	private QueryParser parser;
	
	/**Defines the maximum number of documents to retrieve as the size of the largest WP */
	private final int TOP = 4500000;

	/**Documents with a score larger than max_score/percentage will be retrieved */
	private float percentage;
	
	/**No percentage will be applied */
	private final int NO_PCT = 999;

	/** Default invocation for English */
	public LuceneQuerierWT(String indexDir, float pct){
		this(Locale.ENGLISH, indexDir, pct);
	}
	
	public LuceneQuerierWT(String indexDir){
		this(Locale.ENGLISH, indexDir, 10);
	}
	
	public LuceneQuerierWT(String language, String indexDir, float pct){
		this(new Locale(language), indexDir, pct);
	}

	public LuceneQuerierWT(String language, String indexDir){
		this(new Locale(language), indexDir, 10);
	}
	
	public LuceneQuerierWT(Locale language, String indexDir, float pct){
		super(indexDir);
		this.percentage = pct;
		setLanguage(language);
		setAnalyzer();
	}
	
	/**
	 * Sets the analyzer as a new instance of WTAnalyzer
	 * @param LUCENE_VERSION
	 * @param lan
	 * 
	 */
	public void setAnalyzer(){
		//analyzer = new WTAnalyzer(LUCENE_VERSION, lan);
		//In this concrete experiment our terms are already "analysed",
		// we don't need to do it again, so Whitespace, which is indeed
		// language independent
		analyzer = new WhitespaceAnalyzer(LUCENE_VERSION);
	}

	
	
	/**Loads the Lucene index (previously created) with the reference 
	 * corpus.
	 * @throws IOException
	 */
	public void loadIndex(Locale lan){
		Directory dir;
		try {
			dir = FSDirectory.open(new File(indexDir));
			reader = IndexReader.open(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		searcher = new IndexSearcher(reader);		
		parser = new QueryParser(LUCENE_VERSION, "contents", analyzer);		
		logger.info("Index loaded");
		//indexDimension = reader.numDocs();		
		//loadDocIds();		
	}
	
	
	/**
	 * Queries Lucene with the query {@code text} and returns a LinkedHashMap 
	 * with the pairs (score, filename) for those documents that have a score
	 * larger than max_score/percentage
	 * 
	 * @param text
	 * @return idsLHM
	 */
	public LinkedHashMap<Float, String> queryScoreDoc(String text){

		LinkedHashMap<Float, String> idsLHM = new LinkedHashMap<Float, String>();
		try {
			//Necessary if the query is empty (e.g. text ha stopwords only)
			Query query = parser.parse(text);	
			TopDocs topHits = searcher.search(query, TOP);	
			float maxScore = topHits.getMaxScore();
			
			for (ScoreDoc scoreDoc : topHits.scoreDocs) {	
				Float score = scoreDoc.score;
				if (percentage == NO_PCT || score >= maxScore/percentage) {
					int id = scoreDoc.doc;
					Document doc = searcher.doc(id);
					String name = doc.get("filename");		
					idsLHM.put(score, name);
					//System.out.println(scoreDoc.toString() + "  " +name); 
				} else {  // hits must be ranked according to its score
					break;
				}
			}
		
		    searcher.close();
		
		} catch (Exception e) {
			logger.warn("Empty query");
//			emptyQuery = true;
//			v = new float[docIds.size()];
		}
		
		return idsLHM;
	}
	
	
	/**
	 * Queries Lucene with the query {@code text} and returns a String 
	 * with the ID of those documents that have a score larger than 
	 * max_score/percentage
	 * 
	 * @param text
	 * @return ids
	 */
	public String queryIDs(String text){

		String ids = "";
		try {
			//Necessary if the query is empty (e.g. text ha stopwords only)
			Query query = parser.parse(text);	
			TopDocs topHits = searcher.search(query, TOP);	
			float maxScore = topHits.getMaxScore();
			
			logger.info("Obtained " + topHits.totalHits + " hits with a maximum score of " + maxScore); 
			for (ScoreDoc scoreDoc : topHits.scoreDocs) {	
				Float score = scoreDoc.score;
				if (percentage == NO_PCT || score >= maxScore/percentage) {
					int id = scoreDoc.doc;
					Document doc = searcher.doc(id);
					String name = doc.get("filename");	
					Pattern p = Pattern.compile("(\\d+).\\w+.txt");
					Matcher m = p.matcher(name);
					String articleID = "";
					if (m.find()) {
						articleID = m.group(1);  
					} else{
						logger.warn("There's been a problem with the ID of document " + name);
					}
					ids = ids.concat(articleID).concat("\n");
					//System.out.println(ids); 
				} else {  // hits must be ranked according to its score for this
					break;
				}
			}
		
		    searcher.close();
		
		} catch (Exception e) {
			logger.warn("Empty query");
		}
		
		return ids;
	}

	
	public static void main(String[] args) {
		
		LuceneCliQuerierWT cli = new LuceneCliQuerierWT();
		cli.parseArguments(args);
		Locale lang = cli.getLanguage();
		String inPath = cli.getIn();
		float pct = cli.getPercentage();
		boolean verbose = cli.getVerbosity();
		
	    LuceneQuerierWT lQuerier = new LuceneQuerierWT(lang, inPath, pct);		
	    lQuerier.setVerbose(verbose);
		lQuerier.loadIndex(lang);
		//LinkedHashMap<Float, String> topDocumentsLHM = 
		//		lQuerier.queryScoreDoc("bla, bla");	
		//String topDocumentsS = lQuerier.queryIDs("bla, bla");		
		
		/*Locale lang = Locale.ENGLISH;
		String text = "L’occitan coneguèt son epòca daurada entre los sègles XI e XIII gràcias a sa literatura e subretot";
		//String inPath = "/home/cristinae/pln/wikipedia/categories/extractions/oc.0/plain/oc/25/";
		String indexPath = "/home/cristinae/pln/wikipedia/categories/indexes/oc/";
		LuceneQuerierWT lQuerier =  new LuceneQuerierWT(new Locale("oc"), indexPath, 10);		

		lQuerier.loadIndex(lang);
		//LinkedHashMap<Float, String> topDocuments = lQuerier.queryScoreDoc(text);	
		String topDocuments = lQuerier.queryIDs(text);*/
		
	 }
	
	
}
