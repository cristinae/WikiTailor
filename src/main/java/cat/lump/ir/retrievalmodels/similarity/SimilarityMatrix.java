package cat.lump.ir.retrievalmodels.similarity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;

/**
 * This class represents a matrix of similarities. Each position stores the
 * similarity between two fragments. The rows represents the source fragments
 * and the columns the target fragments.
 * </br>
 * For example, the position matrix(0,1) contains the similarity between the
 * first source fragment and the second target fragment.
 * 
 * @author jboldoba
 */
public class SimilarityMatrix implements Serializable
{

	/** Auto generated ID for serialization */
	private static final long serialVersionUID = -5555429854990323699L;
	/** Matrix with similarity values */
	private double[][] matrix;

	/**
	 * Creates a matrix filled with zeros with the size given by the parameters.
	 * Any size can be zero.
	 * 
	 * @param rows
	 *            Number of rows of the matrix. (Number of source fragments)
	 * @param cols
	 *            Number of columns of the matrix. (Number of target fragments)
	 */
	public SimilarityMatrix(int rows, int cols)
	{
		CHK.CHECK(rows != 0 && cols != 0,
				"The number of rows or columns is zero.");
		matrix = new double[rows][cols];
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (int row = 0; row < matrix.length; row++)
		{
			for (int col = 0; col < matrix[row].length; col++)
			{
				String value = String.format("%.4f", matrix[row][col]);
				sb.append(value).append("\t");
			}
			// Replace last comma by new line
			sb.replace(sb.length() - 1, sb.length(), "\n");
		}
		return sb.toString();
	}

	/**
	 * Writes the matrix as text in a given file
	 * 
	 * @param output
	 *            The wile wherein the matrix must be written
	 * @throws IOException
	 *             If happens any problem during the writing
	 */
	public void toFile(File output) throws IOException
	{
		FileIO.stringToFile(output, toString(), false);
	}

	/**
	 * Saves this object in a file as binary raw data.
	 * 
	 * @param output
	 *            The file wherein the matrix will be saved
	 * @throws IOException
	 */
	public void serialize(File output) throws IOException
	{
		ObjectOutputStream out = null;
		try
		{
			out = new ObjectOutputStream(new FileOutputStream(output));
			out.writeObject(this);
		} catch (IOException ioe)
		{
			throw ioe;
		} finally
		{
			if (out != null)
			{
				out.close();
			}
		}
	}

	/**
	 * Adds the {@code value} to the current value of the position given by
	 * {@code row} and {@code col}
	 * 
	 * @param row
	 *            Row index
	 * @param col
	 *            Column index
	 * @param value
	 *            Value to add
	 */
	public void add(int row, int col, double value)
	{
		CHK.CHECK(row < matrix.length && col < matrix[0].length);
		matrix[row][col] += value;
	}

	/**
	 * Divides the current value of the position given by {@code row} and
	 * {@code col} by the indicated {@code value}
	 * 
	 * @param row
	 *            Row index
	 * @param col
	 *            Column index
	 * @param value
	 *            Divisor
	 */
	public void div(int row, int col, double value)
	{
		CHK.CHECK(row < matrix.length && col < matrix[0].length);
		matrix[row][col] /= value;
	}

	/**
	 * Changes the value of the position of the matrix given by {@code row} and
	 * {@code col}.
	 * 
	 * @param row
	 *            Index of the row
	 * @param col
	 *            Index of the column
	 * @param value
	 *            New value for the given position
	 */
	public void setSimilarity(int row, int col, float value)
	{
		CHK.CHECK(row < matrix.length && col < matrix[0].length);
		matrix[row][col] = value;
	}

	/**
	 * @param matrix
	 *            the new similarity matrix
	 */
	public void setMatrix(double[][] matrix)
	{
		this.matrix = matrix;
	}

	/**
	 * Loads a similarity matrix froma binary a file.
	 * 
	 * @param file
	 *            File wherein the matrix is saved
	 * @param binary
	 *            Indicates if the matrix is saved as binary data (
	 *            {@code binary = true}) or as text ({@code binary = false})
	 * @return The similarity matrix contained in {@code file}
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static SimilarityMatrix getFromFile(File file, boolean binary)
			throws ClassNotFoundException, IOException
	{
		CHK.CHECK(file.isFile() && file.canRead(),
				"The file doesn't exist or it's unreadable");
		SimilarityMatrix sm = null;
		if (binary)
		{// Serialized
			FileInputStream door = new FileInputStream(file);
			ObjectInputStream reader = null;
			try
			{
				reader = new ObjectInputStream(door);
				sm = (SimilarityMatrix) reader.readObject();
			} catch (IOException ioe)
			{
				throw ioe;
			} finally
			{
				if (reader != null)
				{
					reader.close();
				}
				door.close();
			}
		}
		else
		{// As text
			String[] lines = FileIO.fileToString(file).split("\n");
			CHK.CHECK(lines.length > 0);
			int rows = lines.length;
//			System.out.println("Rows: "+rows);
			int cols = lines[0].split("\\s").length;
//			System.out.println("Columns: "+cols);
			sm = new SimilarityMatrix(rows, cols);
			for (int row = 0; row < rows; row++)
			{
				String[] values = lines[row].split("\\s");
				for (int col = 0; col < cols; col++)
				{
					String textVal = values[col];
					textVal = textVal.replaceAll(",", ".");
					float value = Float.parseFloat(textVal.trim());
					// CHK.CHECK(Float.isNaN(value),"SimMatrix NaN: "+value);
					sm.setSimilarity(row, col, value);
				}
			}
		}
		return sm;
	}

	/**
	 * @param row
	 *            Index of the row
	 * @param col
	 *            Index of the column
	 * @return The value stored in the position of the matrix given by
	 *         {@code row} and {@code col}
	 */
	public double getSimilarity(int row, int col)
	{
		CHK.CHECK(row < matrix.length && col < matrix[0].length);
		return matrix[row][col];
	}

	public int getNRows()
	{
		return matrix.length;
	}

	public int getNCols()
	{
		return (matrix.length > 0) ? matrix[0].length : 0;
	}

	public int getSize()
	{
		return getNRows() * getNCols();
	}

	public SimilarityMatrixIterator iterator()
	{
		return new SimilarityMatrixIterator(this);
	}

	/**
	 * @return the similarity matrix
	 */
	public double[][] getMatrix()
	{
		return matrix;
	}
}
