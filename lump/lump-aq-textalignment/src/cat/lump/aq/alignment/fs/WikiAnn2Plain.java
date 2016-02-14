package cat.lump.aq.alignment.fs;


import cat.lump.aq.alignment.fragment.AlignmentLevel;
import cat.lump.aq.alignment.fragment.ParallelFragment;
import cat.lump.aq.alignment.fragment.ComparableFragment;

/**
 * A simple class that transforms a set of annotations into 
 * a String matrix for either display or storing.
 *  
 * @author albarron
 * @version 0.1
 * @since Apr, 2014
 */
public class WikiAnn2Plain {
	
	private static final String LABEL_TRANSLATED = "translated-real";
	private static final String LABEL_COMPARABLE = "comparable-real";
	
	/**
	 * <p>
	 * Returns a matrix with the information of the given comparable 
	 * and parallel pairs.
	 * </p>
	 * <p>
	 * The matrix has tuples with [src_line, trg_line, kind]
	 * where kind is one of "translated-real" or "comparable-real"
	 * </p>  
	 * @param parallelFragments
	 * @param comparableFragments
	 * @return
	 */
	public static String[][] get(AlignmentLevel parallelFragments, 
								AlignmentLevel comparableFragments){
	
	String fragments[][] = new String[parallelFragments.size() 
	                                 + comparableFragments.size()][3] ;		

	int i = 0;
	for (Integer[] pair : parallelFragments.getAllids())		{
		fragments[i][0] = String.valueOf(pair[0]);
		fragments[i][1] = String.valueOf(pair[1]);
		fragments[i][2] = LABEL_TRANSLATED;
		i++;			
	}
	
	for (Integer[] pair : comparableFragments.getAllids())		{
		fragments[i][0] = String.valueOf(pair[0]);
		fragments[i][1] = String.valueOf(pair[1]);
		fragments[i][2] = LABEL_COMPARABLE;
		i++;
	}
	return fragments;
}

	
}
