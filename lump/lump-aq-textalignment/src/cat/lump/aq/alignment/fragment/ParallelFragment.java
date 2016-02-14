package cat.lump.aq.alignment.fragment;



public class ParallelFragment extends ComparableFragment{

		
	public ParallelFragment(){
		super();
	}	


	protected String getKind(){
		return "parallel";
	}
	
//	
//	@Override
//	public ErrorControl addElement(int srcID, int trgID, 
//					String srcText, String trgText){
//		StringBuffer sb = new StringBuffer();
//		if (elements.containsKey(srcID) || elements.containsValue(trgID)){
//			sb.append("ERROR [");
//			if (elements.containsKey(srcID))
//				sb.append("SOURCE");
//			if (elements.containsValue(trgID))
//				sb.append(" TARGET");
//			sb.append("] sentence(s) belong to other parallel sentences");
//			error.setIDtxt(1, sb.toString());
//			
//		} else {			//the pair is accepted
//			elements.put(srcID, trgID);
//			srcFragments.put(srcID, srcText);
//			trgFragments.put(trgID, trgText);
//			error.setIDtxt(0, "Pair accepted");
//		}
//		return error;		
//	}
	
//	public boolean equals(ParallelFragment p){
//		for (int i : elements.keySet() ){
//			if (! p.elements.containsKey(i))
//				return false;
//			if (! p.srcFragments.containsKey(i))
//				return false;
//			if (! p.trgFragments.containsKey(elements.get(i)))
//				return false;
//			if ((elements.get(i) != p.elements.get(i)) ||
//				(! srcFragments.get(i).equals(p.srcFragments.get(i))) ||
//				(! trgFragments.get(elements.get(i)).equals(p.trgFragments.get(elements.get(i)))))
//				return false;		
//		}
//		return true;		
//	}
//
//	public void overrideAndClone(ParallelFragment newParallel){		
//		removeAll();
//		if (newParallel.size() == 0)
//			return;
//		elements.putAll(newParallel.elements);
//		srcFragments.putAll(newParallel.srcFragments);
//		trgFragments.putAll(newParallel.trgFragments);		
//	}
//	
//	@Override
//	public void remove(int src_id, int trg_id) {
//		elements.remove(src_id);
//		srcFragments.remove(src_id);
//		trgFragments.remove(trg_id);		
//	}	
//
//	@Override
//	public void removeAll() {
//		elements.clear();
//		srcFragments.clear();
//		trgFragments.clear();		
//	}
//	
//	@Override
//	public List<Integer[]> getAllids() {
//		lstElements.clear();
//		for (int srcID : elements.keySet()){
//			lstElements.add(new Integer[]{srcID, elements.get(srcID)} );			
//		}
//		return lstElements;	
//	}
//
//	@Override
//	public int size() {
//		return elements.size();
//	}
}
