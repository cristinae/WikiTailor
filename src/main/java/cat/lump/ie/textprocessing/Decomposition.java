package cat.lump.ie.textprocessing;

import java.util.List;
import java.util.Map;

public interface Decomposition {
	
	/**
	 * @param text
	 * @return
	 */
	public abstract List<String> getStrings(String text);
	
	
	//TODO we here consider the spans only, instead of returning the actual text
	public abstract List<Span> getSpans(String text);

//	public abstract Map<String, Integer> getFreqs(String text);
	
}


