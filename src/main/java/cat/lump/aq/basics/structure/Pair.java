package cat.lump.aq.basics.structure;

import java.io.Serializable;

/**
 * A class that contains a pair for storing data.
 * 
 * @author aeiselt
 *
 * @param <S>
 * @param <T>
 */
public class Pair<S extends Comparable<S>, T 
extends Comparable<T>> implements Serializable, Comparable<Pair<S,T>> {

	private static final long serialVersionUID = -3985194114132287219L;
	private S a;
	private T b;

	public Pair(S a, T b) {
		this.a=a;
		this.b=b;
	}

	/**
	 * @return the fist element
	 */
	public S getKey() {
		return this.a;
	}

	public T getValue() {
		return this.b;
	}

	/**
	 * @param s
	 * @param t
	 */
	public void set(S s, T t){
		setKey(s);
		setValue(t);
	}
	/**
	 * @param value
	 */
	public void setKey(S value) {
		this.a=value;
	}

	/**
	 * @param value
	 */
	public void setValue(T value) {
		this.b=value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Pair<S, T> o) {
		return this.a.compareTo(o.getKey());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "<"+a.toString()+", "+b.toString()+">";
	}
}
