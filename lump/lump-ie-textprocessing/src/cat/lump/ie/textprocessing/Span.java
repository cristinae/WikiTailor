package cat.lump.ie.textprocessing;

import cat.lump.aq.basics.check.CHK;

/**
 * Copied from the aitools span definition. 
 *
 */
public class Span {
	
	private int start;
	private int end;
	
	public Span(int start, int end){
		CHK.CHECK(start >= 0);
		CHK.CHECK(end >= 0);
		this.start = start;
		this.end = end;
	}
	
	/**String in the current span
	 * @param text
	 * @return
	 */
	public String getSubstring(String text){
		CHK.CHECK(text.length() >= end);
		return text.substring(start, end);
	}
	
	/**
	 * @return Start index
	 */
	public int getStart(){
		return start;
	}
	
	/**
	 * @return End indexs
	 */
	public int getEnd(){
		return end;
	}

	/**
	 * @return Length of the part in the original string
	 */
	public int length(){
		return end - start;
	}	
}
