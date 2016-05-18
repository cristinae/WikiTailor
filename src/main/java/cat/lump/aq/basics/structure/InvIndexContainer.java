package cat.lump.aq.basics.structure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** A container for storing an inverted index. It is composed of a 
 * nested hashmap with  <String<Integer, Double>>
 * 
 * TODO probably this class needs some methods
 * @author albarron
 *
 */
public class InvIndexContainer implements Serializable {
	private static final long serialVersionUID = 6660736726314785198L;
	//public HashMap<String, String> types = new HashMap<String,String>();
	
	public Map<String, HashMap<String, Double>> tBox = 
					new HashMap<String, HashMap<String, Double>>();
	}
