package cat.lump.ir.lucene.query;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

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


public class LuceneQuerier extends LuceneInterface{

	private static Document2Query d2q;
	//AQUI VOY. HAY QUE CARGAR EL ANALIZADOR ADECUADO Y HACER LA QUERY
	private static Locale lan = Locale.ENGLISH;
	private static String text = "esta es solo una prueba";

	
	/**Lucene reader instance*/
	private IndexReader reader; 
	
	/**Lucene searcher instance */
	private IndexSearcher searcher;

	/**Lucene parser*/
	private QueryParser parser;
	
	
	
	
	/**Defines the maximum number of documents to retrieve */
	private final int TOP = 100;
	
	public LuceneQuerier(String indexDir){
		super(indexDir);
		logger = new LumpLogger( "LuceneQuerier" );
	}
	
	public static void main(String[] args) {
		
		//TODO SET THE PROPER CLI
		LuceneQuerier lq = new LuceneQuerier("");
		
		lq.loadIndex(lan);
		
		lq.query(text);
		
	}
	
	
	
	
	/**Loads the Lucene index (previously created) with the reference 
	 * corpus.
	 * @throws IOException
	 */
	public void loadIndex(Locale lan){
		d2q = new Document2Query(lan);
		Directory dir;
		try {
			dir = FSDirectory.open(new File(indexDir));
			reader = IndexReader.open(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		searcher = new IndexSearcher(reader);		
		parser = new QueryParser(Version.LUCENE_30, 
								"contents",
								d2q.getAnalyzer()
					);		
		//indexDimension = reader.numDocs();		
		//loadDocIds();		
	}
	
	public void query(String text){
		String q = d2q.str2FlatQuery(text);		
		try {
			//Necessary if the query is empty (e.g. text ha stopwords only)
			Query query = parser.parse(q);			
			TopDocs hits = searcher.search(query, TOP);		
			
			
			for (ScoreDoc scoreDoc : hits.scoreDocs) {			
				System.out.println(scoreDoc.toString());
				//AQUI VOY. AHORA HAY QUE HACER EL QUERY Y VER QUE PASA

			}
		} catch (Exception e) {

//			emptyQuery = true;
//			v = new float[docIds.size()];

		}
		
	}
	
}
