package cat.lump.aq.alignment.control;

import cat.lump.aq.alignment.fragment.AlignmentLevel;
import cat.lump.aq.alignment.fragment.ParallelFragment;
import cat.lump.aq.alignment.fragment.ComparableFragment;


/**
 * A class that keeps track of the last saved progress and 
 * says whether the current state is different. 
 * <br/>
 * It is particularly useful to save/undo
 * 
 * 
 * @author albarron
 * @since Sep 2012
 * @version 0.3
 */
public class Tracker {
	
	/**Instance of a parallel fragment */
	private ParallelFragment parallel;
	
	/**Instance of a comparable fragment */
	private ComparableFragment comparable;
		
	public Tracker(){
		parallel = new ParallelFragment();
		comparable = new ComparableFragment();
	}
	
	//TODO ADD EVERY COMMENT!!
	/**
	 * @param newParallel
	 */
	public void setParallel(AlignmentLevel newParallel){
		parallel.overrideAndClone(newParallel);		
	}
	
	/**
	 * @param newComparable
	 */
	public void setComparable(AlignmentLevel newComparable){
		comparable.overrideAndClone(newComparable);
	}
	
	/**
	 * @param currentParallel
	 * @return
	 */
	public boolean parallelChanged(AlignmentLevel currentParallel){		
		if ((currentParallel.size() == 0 && parallel.size() == 0)
			|| currentParallel.equals(parallel)	)
			return false;
		
		return true;
	}
	
	/**
	 * @param currentComparable
	 * @return
	 */
	public boolean comparableChanged(AlignmentLevel currentComparable){
		if ((currentComparable.size() == 0 && comparable.size() == 0)
			|| currentComparable.equals(comparable) )
			return false;
		
		return true;
	}	
	
}