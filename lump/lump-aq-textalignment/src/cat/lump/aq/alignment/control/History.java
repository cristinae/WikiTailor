package cat.lump.aq.alignment.control;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import cat.lump.aq.check.CHK;

/**
 * A class that stores the history of the operations carried out with 
 * the manual alignment interface.
 * 
 * The history tracking is based on a set of stacks for the sentences
 * and kind of relation.
 * 
 * @see cat.lump.aq.alignment.layout.files.FullPanel  
 * 
 * @author albarron
 * @since Sep 2012
 * @version 0.3
 */
public class History {
	
	/**Identifier of the source text */
	private Stack<Integer> srcIdStack = new Stack<Integer>();
	
	/**Identifier of the target text */
	private Stack<Integer> trgIdStack;
	
	/**Whether the pair is parallel or not */
	//TODO should be boolean?
	private Stack<Integer> parallelStack;
	
	/**Source text stack*/
	private Stack<String> srcTextStack;
	
	/**Target text stack*/
	private Stack<String> trgTextStack; 
	
	private Map<Integer, String> kind;

	public History(){
		srcIdStack = new Stack<Integer>();		
		trgIdStack = new Stack<Integer>();		
		parallelStack = new Stack<Integer>();		
		srcTextStack = new Stack<String>();		
		trgTextStack = new Stack<String>(); 
		
		kind = new HashMap<Integer, String>();
		kind.put(1, "parallel");
		kind.put(2, "comparable");	 
	}
	
	/**Clears the history*/
	public void clear(){
		srcIdStack.clear();
		trgIdStack.clear();
		parallelStack.clear();
		srcTextStack.clear();
		trgTextStack.clear();
	}
	
	/**
	 * Push a new parallel entry onto the history stack
	 * @param srcID source id
	 * @param trgID target id
	 * @param srcText source text
	 * @param trgText target text
	 */
	public void pushParallel(int srcID, int trgID, 
					String srcText,	String trgText){
		CHK.CHECK_NOT_NULL(srcID);
		CHK.CHECK_NOT_NULL(trgID);
		CHK.CHECK_NOT_NULL(srcText);
		CHK.CHECK_NOT_NULL(trgText);
		
		push(srcID, trgID, 1, srcText, trgText);
	}
	
	/**
	 * Push a new comparable entry onto the history stack
	 * @param srcID source id
	 * @param trgID target id
	 * @param srcText source text
	 * @param trgText target text
	 */
	public void pushComparable(int srcID, int trgID, 
					String srcText,	String trgText){
		CHK.CHECK_NOT_NULL(srcID);
		CHK.CHECK_NOT_NULL(trgID);
		CHK.CHECK_NOT_NULL(srcText);
		CHK.CHECK_NOT_NULL(trgText);
		
		push(srcID, trgID, 2, srcText, trgText);
	}
	
	private void push(int srcID, int trgID, int kind,
					String srcText,	String trgText){
		
		srcIdStack.push(srcID);
		trgIdStack.push(trgID);
		parallelStack.push(kind);
		srcTextStack.push(srcText);
		trgTextStack.push(trgText);
	}
	
	/** @return true if the history is empty */
	public boolean isEmpty(){
		return srcIdStack.isEmpty();
	}
	
	/**
	 * Pops the last record in the history
	 * @return the kind of the pair on top: parallel or comparable;
	 * 	null if it is empty
	 */
	public Map<String, Integer> pop(){
		if (isEmpty()){
			return null;
		}
		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("id_src", srcIdStack.pop());
		m.put("id_trg", trgIdStack.pop());
		m.put("kind", parallelStack.pop());
		return m;
	}
	
	/**
	 * @return size of the history
	 */
	public int size(){
		return srcIdStack.size();
	}
	
	/**
	 * @return kind of the last entry (parallel or comparable)
	 * //TODO this doesn't seem to be very useful. It's just a number 
	 */
	public String lastKind(){
		return kind.get(parallelStack.lastElement());
	}
	
	/** @return source text in the last entry */
	//TODO modify these names
	public String lastTxtSrc(){
		if (isEmpty()){
			return null;
		}
		return srcTextStack.lastElement();
	}
	
	/** @return the target text in the last entry */
	public String lastTxtTrg(){
		if (isEmpty()){
			return null;
		}
		return trgTextStack.lastElement();
	}
	
}

