package cat.lump.ie.textprocessing;

import java.util.List;

public interface Decomposition {
	
	/**
	 * @param text
	 * @return
	 */
	public abstract List<String> getStrings(String text);
	
	
	//TODO we here consider the spans only, instead of returning the actual text
	public abstract List<Span> getSpans(String text);

}


