package cat.lump.ir.comparison.toCheck;

import java.io.File;

import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * A similarity pair contains the pairs which define the input data for the
 * similarity calculators.</br>
 * Each pair is composed by the ID as left element and the file with the article
 * as right element. They are implemented with the {@link ImmutablePair} class
 * from Apache Commons.
 * 
 * @author jboldoba
 * @see org.apache.commons.lang3.tuple.ImmutablePair
 */
public class SimilarityPair implements Comparable<SimilarityPair>
{
	/** Pair which defines the source input article */
	private ImmutablePair<Integer, File> source;
	/** Pair which defines the target input article */
	private ImmutablePair<Integer, File> target;

	public SimilarityPair(int srcID, File src, int trgID, File trg)
	{
		source = new ImmutablePair<Integer, File>(srcID, src);
		target = new ImmutablePair<Integer, File>(trgID, trg);
	}

	/**
	 * @return the pair which defines the sourcearticle
	 */
	public ImmutablePair<Integer, File> getSource()
	{
		return source;
	}

	/**
	 * @return the pair which defines the target article
	 */
	public ImmutablePair<Integer, File> getTarget()
	{
		return target;
	}

	/**
	 * @param source
	 *            the new pair which defines the source article
	 */
	public void setSource(ImmutablePair<Integer, File> source)
	{
		this.source = source;
	}

	/**
	 * @param target
	 *            the new pair which defines the target article
	 */
	public void setTarget(ImmutablePair<Integer, File> target)
	{
		this.target = target;
	}

	@Override
	public int compareTo(SimilarityPair sp)
	{
		// Compare source IDs
		int cmp = source.left.compareTo(sp.source.left);
		if (cmp == 0) {
			// Compare target IDs
			cmp = target.left.compareTo(sp.target.left);
		}
		return cmp;
	}

}
