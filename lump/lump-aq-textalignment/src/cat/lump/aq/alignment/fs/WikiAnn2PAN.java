package cat.lump.aq.alignment.fs;

import java.util.List;

import cat.lump.aq.alignment.fragment.AlignmentLevel;
import cat.lump.aq.alignment.fragment.ParallelFragment;
import cat.lump.aq.alignment.fragment.ComparableFragment;
import cat.lump.aq.corpus.pan.DocumentAnnotation;
import cat.lump.aq.corpus.pan.Annotation;
import cat.lump.aq.corpus.pan.DocumentSuspicious;

public class WikiAnn2PAN {
	
	private DocumentAnnotation annDoc;
	
	private final String LABEL_TRANSLATED = "translated-real";
	private final String LABEL_COMPARABLE = "comparable-real";
	
	public WikiAnn2PAN(){
		annDoc = new DocumentSuspicious();
	}

	public DocumentAnnotation convert2pan (
			String docID,
			String plagRef,	String srcRef,
			AlignmentLevel parSentences,	AlignmentLevel compSentences, 
			String srcText,	String trgText){
		annDoc.setID(docID);
		List<Integer[]> annotations;
	
		annotations =  parSentences.getAllids();

		for (Integer[] srcAndTrg : annotations ){
			addAnnotation(plagRef, srcRef, srcText, trgText, 
					parSentences.getSrcFragment(srcAndTrg[0]),	//src 
					parSentences.getTrgFragment(srcAndTrg[1]),	//trg
					LABEL_TRANSLATED);				
		}		
		
		annotations =	compSentences.getAllids();

		for (Integer[] srcAndTrg : annotations ){
			addAnnotation(plagRef, srcRef, srcText, trgText, 
					compSentences.getSrcFragment(srcAndTrg[0]),	//src 
					compSentences.getTrgFragment(srcAndTrg[1]),	//trg
					LABEL_COMPARABLE);				
		}		
	    	return annDoc;
	}
	
	private void addAnnotation(String plagRef, String srcRef, 
							String srcText, String trgText,
							String srcSent, String trgSent,
							String phenomenon){
		Annotation an = new Annotation();
	
		an.suspiciousReference = plagRef;
    	an.intrinsic = false;
    	an.thisOffset = getOffset(trgText, trgSent	);
    	an.thisLength = trgSent.length();
    	an.srcReference = srcRef;
    	an.srcOffset = getOffset(srcText, srcSent);
    	an.srcLength = srcSent.length();

    	an.name = phenomenon;//+"-plagiarism";//TIPO DE PARAFRASIS"simulated-plagiarism"
    	an.translation=true;
    	an.obfuscation = "x";
    	an.obfuscation_value = 0;
    	an.same_cluster = true;// -->es un hecho que esto es el mismo tema
    	annDoc.add(an);
	}	
	
	/**
	 * Obtains the offset of the given sentence in the text
	 * @param text		entire text
	 * @param sentence  current sentence
	 * @return		offset of the sentence 
	 */
	private int getOffset(String text, String sentence){
		return text.indexOf(sentence);
	}	
}
