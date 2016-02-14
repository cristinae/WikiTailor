package cat.lump.ir.retrievalmodels.similarity;

import java.util.Map;
import java.util.Set;

public class CosineSimilarity implements SimilarityModel
{

	/** Matrix with all the similarities */
	private SimilarityMatrix simMatrix;
	/** Index to the maximum similarity value for each text fragment in A */
	protected int[] maxSimilaritiesIndex;

	@Override
	public SimilarityMatrix calculateMatrix(Article source, Article target)
	{
		dotProductPerFragment(source, target);
		cosinePerFragment(source, target);
		return simMatrix;
	}

	/**
	 * Compute the dot product for all the pairs.
	 * <\br>
	 * a . b = sum a_i * b_i
	 * for a_i in A and b_i in B
	 */
	private void dotProductPerFragment(Article documentA, Article documentB)
	{
		Map<String, Double> valuesA, valuesB;

		// Create the similarity matrix and maximum similarities array
		simMatrix = new SimilarityMatrix(documentA.length(), documentB.length());
		maxSimilaritiesIndex = new int[documentA.length()];

		// Obtain the types in each text and compute the intersection
		Set<String> types = documentA.getVocabulary();
		types.retainAll(documentB.getVocabulary());

		// Sum the values for every pair of fragments sharing the token
		for (String token : types)
		{
			valuesA = documentA.getTokenValues(token);
			valuesB = documentB.getTokenValues(token);
			for (String sent_A : valuesA.keySet())
			{
				for (String sent_B : valuesB.keySet())
				{
					float value = (float)(valuesA.get(sent_A) * valuesB.get(sent_B));
					simMatrix.add(Integer.valueOf(sent_A), Integer.valueOf(sent_B),value);
				}
			}
		}
	}

	/**
	 * Compute the cosine similarity for all the fragments (e.g. sentences).
	 * The dot product is previously calculated (it is called here IF the
	 * process
	 * was not run before).
	 * </br>
	 * The normalisation by magnitudes is obtained here as
	 * </br>
	 * ||a|| = sqrt(sum a_i^2)
	 * for a_i in a
	 * </br>
	 * 
	 * cos(a,b) = a.b / ||a|| ||b||
	 */
	public void cosinePerFragment(Article documentA, Article documentB)
	{
		int ind_max;
		double this_max;

		for (int a = 0; a < documentA.length(); a++)
		{
			this_max = 0;
			ind_max = 0;
			for (int b = 0; b < documentB.length(); b++)
			{
				float value = (float)(documentA.getMagnitude(a) * documentB
						.getMagnitude(b));
				simMatrix.div(a, b, value);
				
				double max = simMatrix.getSimilarity(a, b);
				if (this_max < max)
				{
					this_max = max;
					ind_max = b;
				}
			}
			// stores the index to the most similar target text fragment
			maxSimilaritiesIndex[a] = ind_max;
		}
	}
}
