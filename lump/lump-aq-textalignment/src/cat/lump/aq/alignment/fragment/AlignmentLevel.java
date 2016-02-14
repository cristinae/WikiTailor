package cat.lump.aq.alignment.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import cat.lump.aq.alignment.control.ErrorControl;

public abstract class  AlignmentLevel {

	protected Map<Integer, Set<Integer>> elements;
	protected Map<Integer, Integer> values;
	
	List<Integer[]> lstElements = new ArrayList<Integer[]>();
	ErrorControl error = new ErrorControl();
	
	protected Map<Integer, String> srcFragments= new HashMap<Integer, String>();
	protected Map<Integer, String> trgFragments = new HashMap<Integer, String>();
	
	public AlignmentLevel(){
		srcFragments= new HashMap<Integer, String>();
		trgFragments = new HashMap<Integer, String>();
		setElements(new TreeMap<Integer, Set<Integer>>());
		setValues(new HashMap<Integer, Integer>());
	}
	
	private void setElements(Map<Integer, Set<Integer>> elements) {
		this.elements = elements;
	}
	
	private void setValues(Map<Integer, Integer> values) {
		this.values = values;
	}

	
	public void setSrcFragments(Map<Integer, String> fragments) {
		srcFragments = fragments;
	}

	public void setTrgFragments(Map<Integer, String> fragments) {
		trgFragments = fragments;
	}
	
	public Map<Integer, String> getSrcFragments() {
		return srcFragments;
	}
	
	public Map<Integer, String> getTrgFragments() {
		return trgFragments;
	}	

	
	public String getSrcFragment(int id) {
		return srcFragments.get(id);
	}

	public String getTrgFragment(int id) {
		return trgFragments.get(id);
	}	
	
	protected abstract String getKind();
	
	public ErrorControl add(int srcID, int trgID, 
			String srcText, String trgText) {
		//check if the pair already exists
		if (elements.containsKey(srcID) && elements.get(srcID).contains(trgID)){
			//pair rejected
			error.setIDtxt(1, "ERROR The " +getKind() + " pair already exists");
			return error;
		}

		//pair accepted; check either src or trg already exists in other pairs
		StringBuffer sb = new StringBuffer();
		if (elements.containsKey(srcID) || values.containsKey(trgID)){
			sb.append("WARNING [");
			if (elements.containsKey(srcID))
				sb.append("SOURCE");
			if (values.containsKey(trgID))
				sb.append(" TARGET");
			sb.append("] sentence(s) belong to more than one " +getKind()+ " pair");
			error.setIDtxt(2, sb.toString());
		} 
		else
			error.setIDtxt(0, "New " + getKind()+ " pair accepted");

		if (! elements.containsKey(srcID))
			elements.put(srcID, new TreeSet<Integer>());
		elements.get(srcID).add(trgID);
		//System.out.println(src_text);
		srcFragments.put(srcID, srcText); 
		trgFragments.put(trgID, trgText); 
		add2values(trgID);

		return error;
	}
	
	public boolean equals(ComparableFragment c){
		for (int i : elements.keySet() ){
			if (! c.elements.containsKey(i))
				return false;
			if (! c.srcFragments.containsKey(i))
				return false;
			
			for (int j : elements.get(i)){
				if (! c.trgFragments.containsKey(j))
					return false;
				if (! c.elements.get(i).contains(j))
					return false;
				
				if ((! srcFragments.get(i).equals(c.srcFragments.get(i))) ||
						(! trgFragments.get(j).equals(c.trgFragments.get(j))))
						return false;
			}					
		}
		return true;		
	}
	
	private void add2values(int trgID){
		if (!values.containsKey(trgID)){
			values.put(trgID, 0);
		}
		
		values.put(trgID, 
				   values.get(trgID) +1);
	}

	
	public Map<Integer, Integer> getValues() {
		return values;
	}
	

	
		
	
	public void overrideAndClone(AlignmentLevel newComparable){
		removeAll();
		if (newComparable.size() == 0)
			return;
		elements.putAll(newComparable.elements);
		values.putAll(newComparable.values);
		srcFragments.putAll(newComparable.srcFragments);
		trgFragments.putAll(newComparable.trgFragments);		
	}
	
	public void remove(int srcID, int trgID) {
		if (elements.get(srcID).size() > 1){	
			//more than one pair exists for this src_id
			elements.get(srcID).remove(trgID);
		} else {
			elements.remove(srcID);
			srcFragments.remove(srcID);
		}
		remove2values(trgID);		
	}
	
	
	public void removeAll() {
		elements.clear();
		srcFragments.clear();
		trgFragments.clear();
		values.clear();		
	}
	
	
	public List<Integer[]> getAllids() {
		lstElements.clear();
		for (int srcID : elements.keySet()){
			for (int trgID : elements.get(srcID))
				lstElements.add(new Integer[]{srcID, trgID} );			
		}
		return lstElements;
	}
	
	
	public int size() {
		int size = 0;
		for (int k : elements.keySet())
			size += elements.get(k).size();
		return size;
	}
	
	
	private void remove2values(int trgID){
		if (values.get(trgID) > 1){		
			//this trg_id exists in some other pair
			values.put(trgID, 
					   values.get(trgID) -1);
		} else {
			values.remove(trgID);
			trgFragments.remove(trgID);
			
		}					
	}
	

	

}
