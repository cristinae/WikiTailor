package cat.lump.aq.basics.structure.ir;

/**
 * This class provides a term frequency abstraction. A term frequency object
 * has the expression and the number of its occurrences in a set of phrases.
 * 
 * @author jboldoba
 */
public class TermFrequencyTuple implements Comparable<TermFrequencyTuple>
{
	/**
	 * Term
	 */
	private final String term;
	/**
	 * Number of times that the term has been found.
	 */
	private int frequency;

	/**
	 * Constructor. It initializes an instance with the term and an initial
	 * frequency.
	 * 
	 * @param term
	 *            The term
	 * @param frequency
	 *            Initial frequency
	 */
	public TermFrequencyTuple(String term, int frequency)
	{
		this.term = term;
		this.frequency = frequency;
	}

	/**
	 * Constructor. It initializes an instance with the indicated term and 0
	 * occurrences.
	 * 
	 * @param term
	 *            The term
	 */
	public TermFrequencyTuple(String term)
	{
		this.term = term;
		frequency = 0;
	}

	/**
	 * Increments the number of occurrences of the term in one unit.
	 */
	public void increment()
	{
		frequency = frequency + 1;
	}

	/**
	 * Decrements the number of occurrences of the term in one unit.
	 */
	public void decrement()
	{
		frequency = frequency - 1;
	}

	@Override
	public int compareTo(TermFrequencyTuple tf)
	{
		return frequency - tf.frequency;
	}
	
	public boolean equals(Object o) {
		boolean equals = false;
		if (this == o) {
			equals = true;
		}
		else if (o instanceof TermFrequencyTuple) {
			TermFrequencyTuple tf = (TermFrequencyTuple) o;
			equals = tf.term.equals(term) && (tf.frequency == frequency);
		}
		return equals;
	}

	/**
	 * Returns the associated term
	 * 
	 * @return The term
	 */
	public String getTerm()
	{
		return term;
	}

	/**
	 * Returns the number of occurrences of the term
	 * 
	 * @return The frequency. (Number of occurrences of the term)
	 */
	public int getFrequency()
	{
		return frequency;
	}
	
	@Override
	public String toString(){
		return String.format("%s %d", term, frequency);
	}
	
}
