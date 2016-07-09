package cat.lump.ir.sim.ml.esa;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

import Jama.Matrix;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.ir.sim.ml.esa.EsaVectors;
import cat.lump.ir.retrievalmodels.distance.VectorEuclideanDistance;
import cat.lump.ir.retrievalmodels.similarity.SimilarityMeasure;
import cat.lump.ir.retrievalmodels.similarity.VectorCosine;
import cat.lump.ir.sim.Similarity;

public abstract class Esa implements Similarity{
	
	protected static LumpLogger log = new LumpLogger("IndexerFactory");
	
	/**Instance of an ESA vector for the set of documents A. It contains 
	 * the representation of this documents on the basis of the reference 
	 * corpus */
	protected EsaVectors esaVectorsA;
	
	/**Instance of an ESA vector for the set of documents B (see description 
	 * for esaVectors_A*/
	protected EsaVectors esaVectorsB;	

	protected SimilarityMeasure vc;
	
	 
	/**Matrix where the computed similarities are stored */
	private Matrix measures;

	public Esa(){
		this(new VectorCosine());
	}
	
	public Esa(SimilarityMeasure measure){
		vc = measure;
	}
	
	public Esa(boolean distance) {
		if (distance) {
			vc = new VectorEuclideanDistance();
		} else {
			vc = new VectorCosine();
		}
	}
	
	
	/**Computes the ESA-based measures (similarities/distances) between the 
	 * previously loaded documents. The results are stored in a private 
	 * variable which can be accessed either with getMeasure() for a 
	 * specific pair or with getMeasures() for a nested map including 
	 * all the similarities for every document pair.
	 */	
	@Override
	public void computeMeasures(){		
		measures = new Matrix(new double[esaVectorsA.length()]
		                                     [esaVectorsB.length()]);

		
		for (int i = 0; i < esaVectorsA.length() ; i++)
			for (int j = 0; j< esaVectorsB.length(); j++)
				measures.set(i, j, 
						vc.compute(esaVectorsA.getVector(i),
										  esaVectorsB.getVector(j)));		
	}
	
	
	/**
	 * Computes the measure (similarity/distance) between two specific 
	 * documents. (if previously computed, getMeasure() can be simply 
	 * called.
	 * 
	 * @param idA
	 * @param idB
	 * @return sim(idA, idB) if both documents exist; -1 otherwise
	 */
	/* (non-Javadoc)
	 * @see cat.lump.ir.sim.Similarity#computeSimilarity(java.lang.String, java.lang.String)
	 */
	@Override
	public double computeMeasure(String idA, String idB){
		if (! documentsExist(idA, idB) ){
			log.error("One of the documents does not exist");
			return -1;
		}			
		return vc.compute(esaVectorsA.getVector(idA), 
						 esaVectorsB.getVector(idB));
	}

	/** 
	 * Obtains the measure (similarity/distance) between texts id_A and id_B. 
	 * The* documents should have been loaded before and similarities 
	 * computed, both through computeSimilarities()
	 * @param idA
	 * @param idB
	 * @return sim(id_A, id_B); -1 if not computed beforehand.
	 */
	/* (non-Javadoc)
	 * @see cat.lump.ir.sim.Similarity#getSimilarity(java.lang.String, java.lang.String)
	 */
	@Override
	public double getMeasure(String idA, String idB){
		if (! documentsExist(idA, idB) ){
			log.error("One of the documents does not exist");
			return -1;
		}
		return measures.get(esaVectorsA.getIndex(idA), 
								esaVectorsB.getIndex(idB));
	}	

	/* (non-Javadoc)
	 * @see cat.lump.ir.sim.Similarity#displaySimilarities()
	 */
	@Override
	public void displayMeasures(){
		DecimalFormat df = new DecimalFormat("#.##");		
		
		System.out.print("   ");
		for (int i = 0; i < measures.getColumnDimension(); i++)
			System.out.print(esaVectorsB.getId(i) + "  ");
		System.out.println();
		
		measures.print(df, 9);
		
		//TODO potentially useful for the final ranking per document.
//		double x = JamaUtils.getMax(JamaUtils.getrow(similarities, 0));		

//		for (int i = 0; i < similarities.length; i++){
//			System.out.print(esaVectors_A.getId(i) + "\t");
//			for (int j = 0; j < similarities[0].length; j++)
//				System.out.print(df.format(similarities[i][j]) + "\t");
//			System.out.println();
//		}		  
	}
	
	/**Checks whether both documents exist already in the corresponding vector.
	 * @param id_A
	 * @param id_B
	 * @return true if both documents were previously loaded
	 */
	protected boolean documentsExist(String id_A, String id_B){
		if (esaVectorsA.containsKey(id_A) &&
			esaVectorsB.containsKey(id_B) )
			return true;
		
		return false;
	}
	
	/** Compute only similarities for the matrix diagonal */
	public void computePairwiseMeasures(){		
		measures = new Matrix(new double[1][esaVectorsA.length()] );
		double sim;

		for (int i = 0; i < esaVectorsA.length() ; i++){
			sim = vc.compute(esaVectorsA.getVector(i),
					  esaVectorsB.getVector(i));
			//Necessary because sometimes the query generator obtains empty queries
			//(e.g. in cases where only stopwords are included in the string)
			//ABC: 17/05/13
			if (! Double.isNaN(sim))
				measures.set(0, i, 
				vc.compute(esaVectorsA.getVector(i),
									  esaVectorsB.getVector(i)));
			else
				measures.set(0, i, 0);
		}
	}	
	
	/**
	 * @return a matrix with all the computed similarities
	 */
	public  Matrix getMeasuresMatrix(){
		return measures;
	}

	/* (non-Javadoc)
	 * @see cat.lump.ir.sim.Similarity#getSimilarities()
	 */
	@Override
	public Map<String, Map<String, Double>> getMeasures(){
		Map<String, Map<String, Double>> mSims = 
							new TreeMap<String, Map<String, Double>>();

		for (String id_A : esaVectorsA.keySet()){
			mSims.put(id_A, new TreeMap<String, Double>());
			for (String id_B : esaVectorsB.keySet()){
				mSims.get(id_A).put(id_B, getMeasure(id_A, id_B));
			}
		}		
		return mSims;
	}	
	
	/**
	 * @return nested map containing the pairwise similarities.
	 */
	public Map<String, Double> getPairwiseSimilarities(){
		Map<String, Double> mSims = new TreeMap<String, Double>();
		
		for (String id_A : esaVectorsA.keySet())		
			mSims.put(id_A.replace(".txt", ""), 
						getSimilarity(id_A));		
		return mSims;
	}
	
	/**
	 * @param idA
	 * @return 
	 */
	public double getSimilarity(String idA){
		return measures.get(0, esaVectorsA.getIndex(idA));
	}

	protected void exitError(String message){
		log.error(message);
		System.exit(1);
	}	
}