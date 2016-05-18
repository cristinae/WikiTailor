package cat.lump.aq.basics.structure;

import java.io.Serializable;

/**
 * This class stores a pair of Wikipedia articles covering the same topic in
 * different languages. 
 * @author albarron
 */
public class ArticlePair implements Serializable{
	
	private static final long serialVersionUID = 6660736726314785198L;

	/**Identifier of the source language article */
	public int srcID;
	
	/**Length of the source language article */
	public int src_len;
	
	/**Title of the source language article*/
	public String srcTitle;
	
	/**Identifier of the target language article */
	public int trg_id;
	
	/**Length of the target language article */
	public int trg_len;
	
	/**Title of the target language article*/
	public String trgTitle;
	
	/** 1 (?) if this pair actually exists (articles in both languages are 
	 *available in the considered dump*/
	public int actualPair;
	
	
	public ArticlePair(){
		
	}
	
	/**TODO this is added for the alignment interface. Check whether the
	 * the one with attributes, as used in cat.talp.lump.wiki.aq.PairsExtractor should be modified 
	 * @param srcId
	 * @param srcTit
	 * @param trgId
	 * @param trgTit
	 */
	public ArticlePair(int srcId, String srcTit, int trgId, String trgTit){
		this.srcID =srcId;
		this.srcTitle =srcTit;
		this.trg_id =trgId;
		this.trgTitle =trgTit;
	}
}
