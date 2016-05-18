package cat.lump.aq.basics.algebra.matrix;

import java.text.DecimalFormat;

import org.apache.commons.lang3.ArrayUtils;

import cat.lump.aq.basics.algebra.vector.Vector;

/**
 *
 * A matrix than can grow as required.
 * 
 * Adapted from:
 * http://www.faqs.org/docs/javap/c8/s3.html
 * 
 *  @author albarron
 *  
 *
 */
public class DynamicMatrixOfVectors extends DynamicMatrix{
	
	/** If an object is saved */
	private static final long serialVersionUID = 4466194760083038150L;

	public static void main(String args[]){		
		//the dimension of the matrix is 3
		int dim = 3;
		//a new matrix of 1xdim is created
		DynamicMatrixOfVectors vector_array = new DynamicMatrixOfVectors();
		
		//we fill the matrix with 11 vectors
		 
		for (int i = 0; i < 10; i++){
			float[] current = {i, i+1, i+2};			
//			current[0] = i;		current[1] = i+1;	current[2] = i+2;
			vector_array.add(new Vector(current));
		}
		
		//matrix[4] is directly modified
//		current[0] = 400;		current[1] = 500;	current[2] = 600;
		vector_array.put(new Vector(new float[]{400, 500, 600}), 4);
		
		//checking the contents of the matrix
		System.out.println("Matrix inside of the object:\n");
		vector_array.display();
		
		//checking the returned matrix
		System.out.println("Returned matrix:\n");
		Vector[] mat = vector_array.getMatrix();
		DecimalFormat df = new DecimalFormat("#.###");
		for (int i = 0; i<mat.length; i++){
			for (int j = 0; j < dim; j++)
				System.out.print(df.format(mat[i].get(j)) + "\t");			
			System.out.println();
		}		
	}

	/**the matrix itself*/
	private Vector[] data;  // A matrix to hold the data.

	/**Initialises the matrix (with size=1) and sets 
	 * the dimension, which cannot be changed afterwards
	 */
	public DynamicMatrixOfVectors() {
		super();
		//this.dimension = dimension;
		data = new Vector[1];
	}

	/**
	 * Get the vector from the specified position. When the position lies 
	 * outside the actual physical size of the data array, a value of null 
	 * is returned (optionally it could return a vector of zeros, but it 
	 * could trigger errors as it wouldn't correspond to actual data.
	 * @param position
	 * @return vector at the given position
	 */
	@Override
	public Vector get(int position) {
		if (position >= data.length)	return null;
		else							return data[position];
	}

	/**Adds a new vector to the matrix, in the last available slot
	 * @param vector
	 */
	public int add(Vector vector) {
		int position = size ;
		return put(vector, position);
	}

	
	
	/** Store the vector in the specified position of the array. The array 
	 * will increase in size to include this position, 
	 * if necessary.
	 * @param position
	 * @param vector
	 */
	public int put(Vector vector, int position) {
		if (position >= data.length) {
			// position is outside the size of current matrix
			// --> double the size, 
			int newSize = 2 * data.length;
			if (position >= newSize)
				//if still not enough, set new size
				// to position + 1.
				newSize = position + 1;
			Vector[] newData = new Vector[newSize];			
			System.arraycopy(data, 0, newData, 0, data.length);
			data = newData;
		}
		if (position >= size)
			size = position +1;
		data[position] = vector;
		return position;
	}

	public int length(){
		return size;
	}   

	/**
	 * @return a copy of the entire matrix
	 */
	@Override
	public Vector[] getMatrix(){
		return ArrayUtils.subarray(data, 0, length());
	}
	
	public void display(){
		DecimalFormat df = new DecimalFormat("#.###");
		for (int i = 0; i<size; i++){
			for (int j = 0; j < data[0].length(); j++)
				System.out.print(df.format(data[i].get(j)) + "\t");			
			System.out.println();
		}		
	}
	
	
}
