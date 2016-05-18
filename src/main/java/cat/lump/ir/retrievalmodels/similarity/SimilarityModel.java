package cat.lump.ir.retrievalmodels.similarity;

public interface SimilarityModel
{
	public SimilarityMatrix calculateMatrix(Article source, Article target);
}
