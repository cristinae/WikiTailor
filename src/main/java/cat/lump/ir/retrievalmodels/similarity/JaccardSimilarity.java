package cat.lump.ir.retrievalmodels.similarity;

import java.util.Map;
import java.util.Set;

public class JaccardSimilarity implements SimilarityModel
{
	/** Matrix with all the similarities */
	private SimilarityMatrix simMatrix;
	/** Index to the maximum similarity value for each text fragment in A */
	protected int[] maxSimilaritiesIndex;

	@Override
	public SimilarityMatrix calculateMatrix(Article source, Article target)
	{
		intersection(source, target);
		jaccardPerFragment(source, target);
		return simMatrix;
	}

	/**
	 * Compute the size intersection for all the pairs.
	 * 
	 * @param documentA
	 * @param documentB
	 */
	private void intersection(Article documentA, Article documentB)
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
					simMatrix.add(Integer.valueOf(sent_A), Integer.valueOf(sent_B), 1f);
				}
			}
		}
	}

	/**
	 * Computes the Jaccard index for all the fragments (e.g. sentences).
	 * 
	 * The Jaccard index is calculated as |A ∩ B| / |A ∪ B|. The intersection is
	 * previously calculated.
	 * 
	 * @param documentA
	 * @param documentB
	 */
	private void jaccardPerFragment(Article documentA, Article documentB)
	{
		int ind_max;
		double this_max;
		double value;

		for (int a = 0; a < documentA.length(); a++)
		{
			this_max = 0;
			ind_max = 0;
			for (int b = 0; b < documentB.length(); b++)
			{

				value = (documentA.getFragmentSize(a) + documentB.getFragmentSize(b)) 
						- simMatrix.getSimilarity(a, b);
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
