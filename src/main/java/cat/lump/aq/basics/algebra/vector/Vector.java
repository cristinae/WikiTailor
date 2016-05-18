package cat.lump.aq.basics.algebra.vector;

import java.io.Serializable;

import cat.lump.aq.basics.check.CHK;

/**
 * A vector of doubles that allows for a number of vector-vector and 
 * vector-scalar algebraic operations, including:
 * 
 * <ul>
 * <li/> sum of vectors (--> vector)
 * <li/> product of vectors (--> scalar)
 * <li/> product by scalar (--> vector)
 * <li/> division by scalar (--> vector)
 * </ul>
 * 
 * Properties of the vector ---magnitude, max, min, argmax, and argmin---
 * are available as well.
 * <br/>
 * This class is partially inspired by Jama 
 * 
 * @author albarron
 * @version 0.2
 * @since April 26 2012
 */
public class Vector implements Serializable{
	

//	TODO potentially useful: http://code.google.com/p/matrix-toolkits-java/ could be used
	//Alberto (2010/03/28). The vector is moved to floats to require less space.
	/**
	 * 
	 */
	private static final long serialVersionUID = 5150892632463134492L;

	/**the array containing the vector*/
	private float[] vector;	
	
	/**Initialisation with an array of doubles
	 * @param values
	 */
	public Vector(float[] values){
		CHK.CHECK_NOT_NULL(values);
		vector = values;
	}	
	
	////////////////////////////////
	// VECTOR - VECTOR OPERATIONS //
	////////////////////////////////
	
	/**
	 * Sums the contents of v to vector 
	 * </br>
	 * This method does not modify the vector.
	 * @param v
	 * @return vector + v
	 */
	public Vector add(Vector v){
		CHK.CHECK_NOT_NULL(v);
		return new Vector( add(v.get()) );
	}
	
	/**	
	 * Sums the contents of vector to values and returns the result 
	 * </br>
	 * This method does not modify the vector.
	 * @param values
	 * @return vector + values
	 */
	public float[] add(float[] values){
		CHK.CHECK_NOT_NULL(values);
		checkSameCardinality(values);
		float[] result = new float[vector.length];
		
		for (int i = 0 ; i < vector.length; i++)
			result[i] = vector[i] + values[i];
		return result;		
	}	
	
	/**Sums v2 to vector and store the result in the vector itself.
	 * <br>
	 * This method <b>modifies</b> the internal values of the vector.
	 * 
	 * @param v
	 */
	public void addEquals(Vector v){
		CHK.CHECK_NOT_NULL(v);
		addEquals(v.get());
	}
	
	/**Sums the values to vector, modifying its contents.
	 * <br>
	 * This method <b>modifies</b> the internal values of the vector.
	 * @param values
	 */
	public void addEquals(float[] values){
		CHK.CHECK_NOT_NULL(values);
		sameCardinality(values);		
		for (int i = 0 ; i < vector.length; i++){
			vector[i] += values[i];	
		}
	}
	
	/**Computes the dot product between the vector and vector v2. 
	 * <br>
	 * 
	 * Note that v <b>must</b> have the same length as the vector.
	 * @param v
	 * @return dotproduct(vector, v)
	 */
	public float dotProduct(Vector v){
		CHK.CHECK_NOT_NULL(v);
		return dotProduct(v.get());
	}	
	
	/**
	 * Computes the dot product between the vector and an array of doubles.
	 * <br>
	 * 
	 * Note that the array <b>must</b> have the same length as the vector.
	 * 
	 * @param values
	 * @return	dotproduct(vector, values)	
	 */	
	public float dotProduct(float[] values){
		CHK.CHECK_NOT_NULL(values);
		checkSameCardinality(values);
				
		float result = 0;	
		for (int i=0; i<vector.length ; i++)		
			result += (vector[i] * values[i]);
		
		return result;
	}	
		
	////////////////////////////////
	// VECTOR - SCALAR OPERATIONS //
	////////////////////////////////
	
	/**Multiplies the vector times a scalar and returns the result.
	 * @param value
	 * @return vector * value
	 */
	public float[] times(float value){
		float[] result = new float[vector.length];
		for (int i = 0 ; i < vector.length ; i++){
			result[i] = vector[i] * value;
		}
		return result;
	}
	
	/**Multiplies the vector times a scalar and updates its internal value.
	 * <br>
	 * This method <b>modifies</b> the internal values of the vector.
	 * 
	 * @param value
	 */
	public void timesEquals(float value){
		for (int i = 0 ; i < vector.length ; i++)
			vector[i] = vector[i] * value;
	}
	
	/**Divides the vector by a scalar and returns the resulting array.
	 * @param value
	 * @return vector / d
	 */
	public float[] divide(float value){
		CHK.CHECK(value != 0, "Division by 0 is not possible");
		return times( 1/value );
	}	
	
	/**Divides the vector by a scalar and updates  its value internally.
	 * <br>
	 * This method <b>modifies</b> the internal values of the vector.
	 * 
	 * @param value
	 */
	public void divideEquals(float value){
		CHK.CHECK(value != 0, "Division by 0 is not possible");
		timesEquals( 1 / value );
	}
	
	/**
	 * Computes the magnitude of the vector (aka norm).
	 * @return magnitude of the vector
	 */
	public float magnitude(){
		float result;
		float len = 0;
		for (int i = 0 ; i < vector.length; i++)		
			len += Math.pow(vector[i], 2);
			result = (float) Math.pow(len, 0.5);		
		return result;
	}	
	
	/**
	 * @return maximum numerical value in the vector.
	 */
	public float max(){
		return vector[argmax()];
	}
	
	/**
	 * @return argument of the maximum numerical value in the vector.
	 */
	public int argmax(){
		float maximum = Float.NEGATIVE_INFINITY;
		int argMax = -1;
		for (int i = 0 ; i < vector.length ; i++) {
			if (maximum < vector[i]){
				maximum = vector[i];
				argMax = i;				
			}
		}		
		return argMax;
	}	
	
	/**
	 * @return minimum numerical value in the vector.
	 */
	public float min(){
		return vector[argmin()];
	}
	/**
	 * @return argument of the minimum value in the vector.
	 */
	public int argmin(){
		float minimum = Float.POSITIVE_INFINITY;
		int argMin = -1;
		for (int i = 0 ; i < vector.length ; i++) {
			if (minimum > vector[i]) {
				minimum = vector[i];
				argMin = i;
			}
		}
		return argMin;
	}	
	

	
//	/**Crops the vector to the given length.
//	 * @param newLength
//	 * @return true if cropping was possible
//	 * 
//	 */
//	public boolean crop(int newLength){
//		if (newLength > vector.length){
//			System.err.println("The length of the vector is longer" +
//								"than the proposed one");					
//			return false;
//		}
//		double[] new_vector = new double[newLength];
//		for (int i = 0 ; i < newLength; i++)
//			new_vector[i] = vector[i];
//			
//		vector = new_vector;
//		return true;
//	}
	
//	/**Extends the vector to the given length.
//	 * @param newLength
//	 * @return true if extension was possible
//	 */
//	public boolean extend(int newLength){
//		if (newLength < vector.length){
//			System.err.println("The length of the vector is shorter" +
//								"than the proposed one");					
//			return false;
//		}
//		double[] new_vector = new double[newLength];
//		for (int i = 0 ; i < vector.length; i++)
//			new_vector[i] = vector[i];
//			
//		vector = new_vector;
//		return true;
//	}
	
	
	/////////////
	// GETTERS //
	/////////////
	
	/** @return array of the vector */
	public float[] get(){
		return vector;
	}
	
	/**
	 * @param index
	 * @return the value at a particular index
	 * @throws NullPointerException
	 */
	public float get(int index){
		inBounds(index);
		return vector[index];
	}
	
	/** @return length of the vector */
	public int length(){
		return vector.length;		
	}
	
	//////////////////////
	// TESTS AND THROWS //
	//////////////////////
	
	/**Check if the vector and v2 have the same cardinality. 
	 *   
	 * @param v
	 * @return true if card(vector)=card(v)
	 */
	public boolean sameCardinality(Vector v){
		return sameCardinality(v.get());
	}
	
	/**Check if the vector and v2 have the same cardinality. 
	 *   
	 * @param v2
	 * @return true if card(vector)=card(v2)
	 */
	public boolean sameCardinality(float[] v2){
		return (vector.length == v2.length);					
	}
	
	/**Method used internally to guarantee that vector operations (such as 
	 * dot product) are possible. 
	 * @param v
	 */
	private void checkSameCardinality(float[] v){
		CHK.CHECK(vector.length == v.length, 
				"Both vectors must have the same dimension");
	}
	
	/**It triggers an error is some computation/request implies out of 
	 * bounds values
	 * @param index
	 * @throws NullPointerException
	 */
	private void inBounds(int index){
		if (index > vector.length)
			throw new NullPointerException("Index out of bounds");
	}
	
}
