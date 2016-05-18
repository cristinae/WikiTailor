package cat.lump.ir.retrievalmodels.similarity;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class SimilarityMatrixIterator
{
	private int currentRow;
	private int currentCol;
	private SimilarityMatrix matrix;

	public SimilarityMatrixIterator(SimilarityMatrix iterable)
	{
		currentRow = 0;
		currentCol = 0;
		matrix = iterable;
	}

	public boolean hasNext()
	{
		boolean hasNext = currentRow < matrix.getNRows()
				&& currentCol < matrix.getNCols();
		return hasNext;
	}

	public double next()
	{
		double value = matrix.getSimilarity(currentRow, currentCol);
//		CHK.CHECK(Double.isNaN(value), "SimMatrixIterator value NaN:"+value);
		nextIndices();
		return value;
	}

	private void nextIndices()
	{
		currentCol++;
		if (currentCol >= matrix.getNCols())
		{
			currentRow++;
			currentCol = 0;
		}
	}

	public ImmutablePair<Integer, Integer> getNextCoordenates()
	{
		return new ImmutablePair<Integer, Integer>(currentRow, currentCol);
	}
}
