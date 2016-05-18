package cat.lump.ir.retrievalmodels.similarity;

import java.io.File;
import java.io.IOException;

import cat.lump.aq.basics.check.CHK;
import cat.lump.ir.comparison.Model;
import cat.lump.ir.retrievalmodels.document.RepresentationType;
import cat.lump.ir.sim.cl.len.LengthFactors;
import cat.lump.ir.sim.cl.len.LengthModelEstimate;

public class SimilarityCalculatorLenFact extends SimilarityCalculator
{

	private LengthModelEstimate lme;

	protected SimilarityCalculatorLenFact(String srcLang, String trgLang,
			RepresentationType type, Model simModel)
	{
		super(srcLang, trgLang, type, simModel);
		lme = new LengthModelEstimate();
	}

	/**
	 * Calculates the matrix of similarities for the given files.
	 * 
	 * @param src
	 *            File which contains the source article
	 * @param trg
	 *            File which contains the target article
	 * @return The matrix of similarities
	 * @throws IOException
	 */
	@Override
	public SimilarityMatrix calculate(File src, File trg) throws IOException
	{
		CHK.CHECK(src.isFile() && src.canRead(),
				"The source file doesn't exists or it's unreadable");
		CHK.CHECK(trg.isFile() && trg.canRead(),
				"The target file doesn't exists or it's unreadable");
		String srcLang = source.getLanguage().getLanguage();
		String trgLang = target.getLanguage().getLanguage();
		double mu = LengthFactors.getMean(srcLang, trgLang);
		double sigma = LengthFactors.getSD(srcLang, trgLang);
		lme.setMuSigma(mu, sigma);
		lme.setFiles(src, trg);
		return calculateSimilarityMatrix();
	}

	@Override
	protected SimilarityMatrix calculateSimilarityMatrix()
	{
		double[][] matrix = lme.estimateMatrix();
		SimilarityMatrix sm = new SimilarityMatrix(matrix.length,
				matrix[0].length);
		sm.setMatrix(matrix);
		return sm;
	}

}
