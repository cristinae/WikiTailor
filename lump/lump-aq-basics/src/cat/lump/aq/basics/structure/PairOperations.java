package cat.lump.aq.basics.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A class that allows for comparing a list of pairs according to 
 * their second value.
 * 
 * @see cat.lump.aq.basics.structure.Pair
 * @author aeiselt
 * @since Jun 2011
 * @version 0.2
 */
public class PairOperations {
	
	/**
	 * Implements the comparison class to sort a list of pairs by 
	 * value in reverse order.  
	 * @param <S>
	 * @param <T>
	 */
	private static class PairValueReverseComparator<S extends Comparable<S>, T 
	extends Comparable<T>> implements Comparator<Pair<S, T>> {
        public int compare(Pair<S, T> obj1, Pair<S, T> obj2) {
                return obj1.getValue().compareTo(obj2.getValue())*-1;
        }
    }

    /**
     * Implements the comparison class to sort a list of pairs by value.
     * 
     * @param <S>
     * @param <T>
     */
    private static class PairValueComparator<S extends Comparable<S>, T 
    extends Comparable<T>> implements Comparator<Pair<S, T>> {
        public int compare(Pair<S, T> obj1, Pair<S, T> obj2) {
                return obj1.getValue().compareTo(obj2.getValue());
        }
    }
    
	/**
	 * Sort a list of pairs according to its value
	 * @param pairs
	 */
	public static void sortByValue(ArrayList<Pair<Integer, Double>> pairs){
          Collections.sort(pairs, new PairValueComparator<Integer, Double>());
	  }

	/**
	 * Sort a list of pairs in reverse order according to its value
	 * @param pairs
	 */
	public static void sortByValueReverse(ArrayList<Pair<Integer, Double>> pairs){
          Collections.sort(pairs, new PairValueReverseComparator<Integer, Double>());
	  }

}