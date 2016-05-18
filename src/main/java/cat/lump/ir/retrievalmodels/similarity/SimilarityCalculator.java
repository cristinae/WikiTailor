package cat.lump.ir.retrievalmodels.similarity;

import java.io.File;
import java.io.IOException;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.comparison.Model;
import cat.lump.ir.retrievalmodels.document.RepresentationType;

public class SimilarityCalculator
{
	/** Article in source language */
	protected Article source;
	/** Article in target language */
	protected Article target;
	/** N value for the n-grams */
	protected int nGrams;
	/** Type of text representation */
	protected RepresentationType type;
	/** Model of similarity */
	protected Model model;

	/**
	 * Creates a similarity calculator with the given arguments and {@code n=1}
	 * for the n-grams methods.
	 * 
	 * @param srcLang
	 *            Source language
	 * @param trgLang
	 *            Target language
	 * @param type
	 *            Type of representation needed by the calculator
	 * @param simModel
	 *            Type of similarity model needed by the calculator.
	 */
	protected SimilarityCalculator(String srcLang, String trgLang,
			RepresentationType type, Model simModel)
	{
		this(srcLang, trgLang, type, simModel, 1);
	}

	/**
	 * Creates a similarity calculator with the given arguments.
	 * 
	 * @param srcLang
	 *            Source language
	 * @param trgLang
	 *            Target language
	 * @param type
	 *            Type of representation needed by the calculator
	 * @param simModel
	 *            Type of similarity model needed by the calculator.
	 * @param nGrams
	 *            The value of N for the representations which uses n-grams
	 */
	public SimilarityCalculator(String srcLang, String trgLang,
			RepresentationType type, Model simModel, int nGrams)
	{
		source = new Article(srcLang, type);
		target = new Article(trgLang, type);
		this.type = type;
		model = simModel;
		this.nGrams = nGrams;
	}

	/**
	 * Returns the suitable calculator according to the given similarity method.
	 * In case that the desired method uses n-grams, the value of N will be 1.
	 * 
	 * @param method
	 *            The desired method.
	 * @return The full implemented calculator which is able to calculate the
	 *         desired method.
	 */
	public static SimilarityCalculator getInstance(String src, String trg, Similarity method)
	{
		return SimilarityCalculator.getInstance(src, trg, method, 1);
	}

	/**
	 * Returns the suitable calculator according to the given similarity method.
	 * In case that the desired method uses n-grams, the value of N will be the
	 * indicated by {@code nGrams}.
	 * 
	 * @param method
	 *            The desired method.
	 * @param nGrams
	 *            The value of n for the n-grams, if it's needed.
	 * @return The full implemented calculator which is able to calculate the
	 *         desired method. A {@code null} object is retourned if the method
	 *         is unknown.
	 */
	public static SimilarityCalculator getInstance(String src, String trg,
			Similarity method, int nGrams)
	{
		SimilarityCalculator calculator = null;
		switch (method)
		{
			case LENGTH_FACTOR:
				calculator = new SimilarityCalculatorLenFact(src, trg,
						method.getRepresentation(), method.getModel());
				break;
			case CHAR_N_GRAMS:
				calculator = new SimilarityCalculator(src, trg,
						method.getRepresentation(), method.getModel(), nGrams);
				break;
			default:
				calculator = new SimilarityCalculator(src, trg,
						method.getRepresentation(), method.getModel());
				break;
		}
		return calculator;
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
	public SimilarityMatrix calculate(File src, File trg) throws IOException
	{
		CHK.CHECK(src.isFile() && src.canRead(),
				"The source file doesn't exists or it's unreadable");
		CHK.CHECK(trg.isFile() && trg.canRead(),
				"The target file doesn't exists or it's unreadable");
		source.setText(FileIO.fileToString(src));
		target.setText(FileIO.fileToString(trg));
		createRepresentations();
		calculateInvIndex();
		return calculateSimilarityMatrix();
	}

	/**
	 * Creates the representations of the source text and the target text
	 * 
	 * @param src
	 *            Source text.
	 * @param trg
	 *            Target text
	 */
	protected void createRepresentations()
	{
		source.representText(nGrams);
		target.representText(nGrams);
	}

	/**
	 * Generate the inverted index of the articles
	 */
	protected void calculateInvIndex()
	{
		source.generateInvIndex();
		target.generateInvIndex();
	}

	/**
	 * Calculates the resulting matrix
	 * 
	 * @return The matrix with the similarities. A {@code null} matrix is
	 *         retourned if the model is unknown
	 */
	protected SimilarityMatrix calculateSimilarityMatrix()
	{
		CHK.CHECK(model != null);
		SimilarityMatrix matrix = null;
		switch (model)
		{
			case COSINE:
				CosineSimilarity cosModel = new CosineSimilarity();
				matrix = cosModel.calculateMatrix(source, target);
				break;
			case JACCARD:
				JaccardSimilarity jaccModel = new JaccardSimilarity();
				matrix = jaccModel.calculateMatrix(source, target);
				break;
			default:
				matrix = null;
				break;
		}
		return matrix;
	}

	/**
	 * @param source
	 *            Article of the source language
	 */
	public void setSource(Article source)
	{
		this.source = source;
	}

	/**
	 * @param target
	 *            Article of the target language
	 */
	public void setTarget(Article target)
	{
		this.target = target;
	}

	/**
	 * @param nGrams
	 *            The N value when a representation uses n-grams
	 */
	public void setnGrams(int nGrams)
	{
		this.nGrams = nGrams;
	}

	/**
	 * @param type
	 *            The type of representation of the text that can be performed.
	 */
	public void setType(RepresentationType type)
	{
		this.type = type;
	}

	/**
	 * @param model
	 *            The new model of similarity which is able to calculate
	 */
	public void setModel(Model model)
	{
		this.model = model;
	}

	/**
	 * @return Article of the source language
	 */
	public Article getSource()
	{
		return source;
	}

	/**
	 * @return Article of the target language
	 */
	public Article getTarget()
	{
		return target;
	}

	/**
	 * @return The N value when a representation uses n-grams
	 */
	public int getnGrams()
	{
		return nGrams;
	}

	/**
	 * @return The type of representation of the text that can be performed.
	 */
	public RepresentationType getType()
	{
		return type;
	}

	/**
	 * @return The model of similarity which is able to calculate
	 */
	public Model getModel()
	{
		return model;
	}
}
