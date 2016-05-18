package cat.lump.ir.comparison.toCheck;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.retrievalmodels.similarity.Similarity;
import cat.lump.ir.retrievalmodels.similarity.SimilarityCalculator;
import cat.lump.ir.retrievalmodels.similarity.SimilarityMatrix;

public class ArticlesSimilarity
{
	/** Code of the source language */
	private String srcLang;
	/** Code of the target language */
	private String trgLang;
	/** List of pairs to calculate their similarities. */
	private ArrayList<SimilarityPair> pairs;
	/** List of the available similarities */
	private final Similarity[] similarities = { Similarity.CHAR_N_GRAMS,
			Similarity.LENGTH_FACTOR, Similarity.MONOLINGUAL_BOW,
			Similarity.PSEUDOCOGNATES };

	public ArticlesSimilarity(String src, String trg)
	{
		srcLang = src;
		trgLang = trg;
		pairs = new ArrayList<SimilarityPair>();
	}

	public void calculate(Similarity similarity, File outputDir)
			throws IOException
	{
		calculate(similarity, outputDir, 1);
	}

	public void calculate(Similarity similarity, File outputDir, int n)
			throws IOException
	{
		CHK.CHECK(isAvailableSimilarity(similarity),
				"Unknown similarity method");
		SimilarityCalculator calculator = SimilarityCalculator.getInstance(
				srcLang, trgLang, similarity, n);
		if (!outputDir.exists())
		{
			outputDir.mkdirs();
		}
		for (SimilarityPair pair : pairs)
		{
			ImmutablePair<Integer, File> src = pair.getSource();
			ImmutablePair<Integer, File> trg = pair.getTarget();
			File srcFile = src.getValue();
			File trgFile = trg.getValue();

			//cómputo de similitudes por oraciones (n x m)
			SimilarityMatrix matrix = calculator.calculate(srcFile, trgFile);
			String filename = String.format("%d.%s.%d.%s.txt", src.getKey(),
					srcLang, trg.getKey(), trgLang);
			File output = new File(outputDir, filename);
			writeMatrix(matrix, output);
			// DEbug
			System.out.println(String.format("%d\t%d", src.getKey(),
					trg.getKey()));
		}
	}

	/**
	 * Load the pairs of articles from a file
	 * 
	 * @param listOfPairs
	 *            File with the list of pairs
	 * @throws IOException
	 */
	public void loadPairs(File listOfPairs) throws IOException
	{
		String[] lines = FileIO.fileToString(listOfPairs).split("\n");
		for (String line : lines)
		{
			String[] values = line.split("\t");
			int srcID = Integer.parseInt(values[0]);
			File src = new File(values[1]);
			int trgID = Integer.parseInt(values[2]);
			File trg = new File(values[3]);
			addPair(srcID, src, trgID, trg);
		}
	}

	/**
	 * Add an article pair to the list
	 * 
	 * @param srcID
	 *            Source articleID
	 * @param trgID
	 *            Target article ID
	 */
	public void addPair(int srcID, File src, int trgID, File trg)
	{
		SimilarityPair pair = new SimilarityPair(srcID, src, trgID, trg);
		pairs.add(pair);
	}

	/**
	 * Add a new collection of pairs
	 * 
	 * @param morePairs
	 *            The new pairs
	 */
	public void addPairs(List<SimilarityPair> morePairs)
	{
		pairs.addAll(morePairs);
	}

	/**
	 * Remove the pair with the given source ID
	 * 
	 * @param srcID
	 *            Source ID
	 * @return The removed pair
	 */
	public SimilarityPair removeSource(int srcID)
	{
		SimilarityPair removed = null;
		int index = 0;
		while (removed == null && index < pairs.size())
		{
			if (pairs.get(index).getSource().getKey() == srcID)
			{
				removed = pairs.remove(index);
			}
			index++;
		}
		return removed;
	}

	/**
	 * 
	 * Removes the pair with the given source ID
	 * 
	 * @param trgID
	 *            Target ID
	 * @return The removed pair
	 */
	public SimilarityPair removeTarget(int trgID)
	{
		SimilarityPair removed = null;
		int index = 0;
		while (removed == null && index < pairs.size())
		{
			if (pairs.get(index).getTarget().getKey() == trgID)
			{
				removed = pairs.remove(index);
			}
			index++;
		}
		return removed;
	}

	/**
	 * @param srcLang
	 *            The new source language
	 */
	public void setSrcLang(String srcLang)
	{
		this.srcLang = srcLang;
	}

	/**
	 * @param trgLang
	 *            The new target language
	 */
	public void setTrgLang(String trgLang)
	{
		this.trgLang = trgLang;
	}

	/**
	 * Sets the list of pairs. This action implies remove all the current pairs.
	 * 
	 * @param thePairs
	 *            The new list of pairs
	 */
	public void setPairs(ArrayList<SimilarityPair> thePairs)
	{
		pairs = thePairs;
	}

	/**
	 * @return The current list of pairs
	 */
	public ArrayList<SimilarityPair> getPairs()
	{
		return pairs;
	}

	/**
	 * Checks if the given similarity is known for this class
	 * 
	 * @param similarity
	 *            The similarity to check
	 * @return {@code true} if teh similarity is known. {@code false} otherwise.
	 */
	public boolean isAvailableSimilarity(Similarity similarity)
	{
		boolean available = false;
		int index = 0;
		while (!available && index < similarities.length)
		{
			available = similarities[index].equals(similarity);
			index++;
		}
		return available;
	}

	/**
	 * @return The current source language
	 */
	public String getSrcLang()
	{
		return srcLang;
	}

	/**
	 * @return The current target language
	 */
	public String getTrgLang()
	{
		return trgLang;
	}

	/**
	 * @return The available similarities
	 */
	public Similarity[] getSimilarities()
	{
		return similarities;
	}

	private void writeMatrix(SimilarityMatrix matrix, File output)
			throws IOException
	{
		FileIO.stringToFile(output, matrix.toString(), false);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		CHK.CHECK(args.length >= 3);
		File inputConfig = new File(args[0]);
		CHK.CHECK(inputConfig.isFile() && inputConfig.canRead(),
				"The input configuration must be readable: " 
				+ inputConfig.getAbsolutePath());
		
		File outputDir = new File(args[1]);
		Similarity similarity = Similarity.getSimilarityByName(args[2]);
		CHK.CHECK(similarity != null, "Similarity unknown");

		if (outputDir.exists())
		{
			CHK.CHECK(outputDir.isDirectory() && outputDir.canWrite(),
					"The output directory must be writable");
		}
		else
		{
			outputDir.mkdirs();
		}

		String[] config = null;
		try
		{
			config = FileIO.fileToLines(inputConfig);
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		CHK.CHECK_NOT_NULL(config);
		String[] data = config[0].split("\t");
		String src = data[0];
		String trg = data[2];
		ArticlesSimilarity artSim = new ArticlesSimilarity(src, trg);

		// Get Pairs
		File srcDir = new File(data[1]);
		CHK.CHECK(srcDir.isDirectory() && srcDir.canRead(),
				"The source input directory must be writable");
		File trgDir = new File(data[3]);
		CHK.CHECK(trgDir.isDirectory() && trgDir.canRead(),
				"The target input directory must be writable");
		System.out.println("Loading pairs... ");
		for (int index = 1; index < config.length; index++)
		{
			String[] entry = config[index].split("\t");
			int srcID = Integer.parseInt(entry[0]);
			File srcFile = new File(srcDir, entry[1]);
			CHK.CHECK(srcFile.isFile() && srcFile.canRead(), "The source file "
					+ srcFile.getAbsolutePath()
					+ " is not a file or it's unreadable");
			int trgID = Integer.parseInt(entry[2]);
			File trgFile = new File(trgDir, entry[3]);
			CHK.CHECK(trgFile.isFile() && trgFile.canRead(), "The target file "
					+ trgFile.getAbsolutePath()
					+ " is not a file or it's unreadable");

			artSim.addPair(srcID, srcFile, trgID, trgFile);
			if (index % 25000 == 0)
			{
				System.out.println(index + " pairs loaded");
			}
		}

		System.out.println("All pairs loaded. Total: "
				+ artSim.getPairs().size());
		try
		{
			if (similarity.equals(Similarity.CHAR_N_GRAMS))
			{
				System.out.println("CNG!!!");
				int n = Integer.parseInt(args[3]);
				artSim.calculate(similarity, outputDir, n);
			}
			else
			{
				System.out.println("Start to calculate " + similarity.getName()
						+ " in " + outputDir.getAbsolutePath());
				artSim.calculate(similarity, outputDir);
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

}
