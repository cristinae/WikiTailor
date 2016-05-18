package cat.lump.ir.index;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.retrievalmodels.document.Document;
import cat.lump.ir.retrievalmodels.document.RepresentationType;

public class Querier extends Abstracter{
	
	/** Takes care of the ranking */
	private Ranking ranking;

	//TODO check whether the index is indeed empty
	
	/**
	 * Invocation where the documents' language is given and the directory
	 * for the index is provided.
	 * <br/>
	 * The program will try to load the index from the directory (if it
	 * previously exists)
	 * @param lan
	 * @param indexDir
	 */
	public Querier(Locale lan, File indexDir, RepresentationType repr)
	{
		//TODO whether multiple representations should be considered for querying
		super(lan, indexDir, new RepresentationType[]{repr});
		
		CHK.CHECK(!index.isEmpty(), 
				"The index is empty; there is nothing to do");
		
		CHK.CHECK( index.containsKey(repr), 
				"The representation is not included in the index");
		
		ranking = new Ranking();
		//similarity = Similarity.getSimilarityByRepr(repr);
		
		//CHK.CHECK(similarity != null, "Similarity unknown");
	}	
	
	/**
	 * Default invocation; files are in English
	 * @param indexDir
	 */
	public Querier(File indexDir, RepresentationType repr)
	{
		this(Locale.ENGLISH, indexDir, repr);
	}

	/**
	 * Queries a text file to the index.
	 * @param queryFile
	 * 			File to query to the index.
	 * @return 
	 * 			the ranking for the file's contents
	 * @throws IOException
	 */
	public Ranking computeRanking(File queryFile) 
	throws IOException{
		return computeRanking(FileIO.fileToString(queryFile));
	}
	
	/**
	 * Queries a text to the index. The text is pre-processed according to
	 * the loaded index. Magnitudes, dot-products and cosine are computed. 
	 * @param queryText
	 * 			Text to be queried to the index
	 * @return
	 * 			the ranking for the given text
	 */
	public Ranking computeRanking(String queryText)
	{
		//TODO whether a Document can be queried directly
		
		//pre-process the query according to the requirements
		Document qDoc = getRepresentation(queryText);
		
		//get the query magnitude		
		double magnitude = getMagnitude(qDoc.getWeighted(repType[0]));
	
		//compute similarities
		Map<String, Double> sim = dotProduct(qDoc);		
		for (String id : sim.keySet()){
			ranking.add(id, 
					sim.get(id) / 
					(magnitude * index.get(repType[0]).getMagnitude(id)));
		}
		
		return ranking;
	}	
	
	
	/**
	 * Compute the dot-products between the query document and the index's 
	 * texts. 
	 * @param q
	 * 			Query document
	 * @return
	 * 			Map of dot-products against the index
	 */
	private Map<String, Double> dotProduct(Document q)
	{
		Map<String, Double> indexWeights;
		
		Map<String, Double>  qTokens = q.getWeighted(repType[0]);
		
		Map<String, Double> products = new TreeMap<String, Double>();				
		for (String id : index.get(repType[0]).getDocuments()){
			products.put(id, 0.0);
		}
			
		//Intersection of vocabularies in query and index
		Set<String> types = index.get(repType[0]).getVocabulary();
		types.retainAll(q.get(repType[0]));		
			
		//dot product computation
		for (String type : types)
		{			
			indexWeights = index.get(repType[0]).getDocuments(type);
			for (String indDoc : indexWeights.keySet())
			{
				products.put(indDoc,  
						products.get(indDoc) + 
						qTokens.get(type) * indexWeights.get(indDoc)
						);
			}
		}
		return products;	
	}
	
	
	/**
	 * @param text
	 * 			vector representing the text
	 * @return
	 * 			magnitude of the vector representing the text.
	 */
	private double getMagnitude(Map<String, Double> text){
		int magnitude = 0;		
		
		for (String tok : text.keySet())
		{
			magnitude += Math.pow(text.get(tok), 2);			
		}	
		return Math.sqrt(magnitude);		
	}


	
	/**
	 * Pre-process the text according to the required representation
	 * @param text	
	 * 				plain text
	 * @return
	 */
	private Document getRepresentation(String text)
	{
		CHK.CHECK_NOT_NULL(text);		
		Document doc = new Document(text, locale, 
				INCLUDE_BOW, INCLUDE_CNG, INCLUDE_WNG, INCLUDE_COG);		
		return doc;
	}
	
	
	
	

	public static void main(String[] args) throws IOException{
		File indexDir = 
			new File("/home/albarron/workspace/lump2-ir-retrievalmodels/index");
		File q = 
			new File("/home/albarron/workspace/lump2-ir-retrievalmodels/texts/474");
		
//		RepresentationType[] repRequired = {RepresentationType.BOW};
		RepresentationType repRequired = RepresentationType.COG;
		
		Querier querier = new Querier(new Locale("es"), indexDir, repRequired);
		Ranking rank = querier.computeRanking(q);
		Map<String, Double> sortedRank = rank.getRank();
		
		System.out.println("Entire rank");
		for (String key : sortedRank.keySet())
		{
			System.out.println(key+"\t"+sortedRank.get(key));
		}
		
		System.out.println("\nTop 2\n");
		Map<String, Double> topRank = rank.getTopK(2);
		
		for (String key : topRank.keySet())
		{
			System.out.println(key+"\t"+topRank.get(key));
		}
		
		
		System.out.println("\nthreshold>0.5");
		Map<String, Double> thRank = rank.getOverThreshold(0.5);
		
		for (String key : thRank.keySet())
		{
			System.out.println(key+"\t"+thRank.get(key));
		}
		
		System.out.println("\ntoString");
		System.out.println(rank.toString());
	}

}
