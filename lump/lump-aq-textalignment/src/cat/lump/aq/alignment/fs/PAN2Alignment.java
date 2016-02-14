package cat.lump.aq.alignment.fs;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cat.lump.aq.alignment.fragment.ComparableFragment;
import cat.lump.aq.alignment.fragment.ParallelFragment;
import cat.lump.aq.corpus.pan.DocumentAnnotation;
import cat.lump.aq.corpus.pan.Annotation;
import cat.lump.aq.corpus.pan.xml.XMLreaderSuspicious;
import cat.lump.aq.io.files.FileIO;


public class PAN2Alignment {

	private XMLreaderSuspicious xmlReader;
	
	private DocumentAnnotation annotations;
	
	private String srcDoc;
	private String trgDoc; 
	
	private String[] srcArray;
	private String[] trgArray;
	
	
	public PAN2Alignment(List srcList, List trgList){
		xmlReader = new XMLreaderSuspicious();
		srcArray = srcList.getItems();
		trgArray = trgList.getItems();
	}
	
//	private java.util.list awt2utilList()
	
	public void loadAnnotations(File f) throws ParserConfigurationException, SAXException, IOException{
		annotations = xmlReader.xml2AnnDoc(f);
	}
	
	public void loadTexts(String articlePath, Locale srcLan, Locale trgLan) throws IOException {
		String srcFile = String.format("%s%s%s.%s.txt", 
				articlePath, 
				FileIO.separator, 
				annotations.getAnnotation(0).srcReference,
				srcLan);

		String trgFile = String.format("%s%s%s.%s.txt", 
				articlePath, 
				FileIO.separator,  
				annotations.getAnnotation(0).suspiciousReference,
				trgLan);
		
		srcDoc = FileIO.fileToString(new File(srcFile));
		trgDoc = FileIO.fileToString(new File(trgFile));		
	}
	
	public ParallelFragment getParallelSentences() {
		String srcText;
		String trgText;
		
		int srcInd;
		int trgInd;
		
		ParallelFragment parallelSentences = new ParallelFragment();	
		
		for (Annotation sAnn : annotations.getAnnotations()){
			if (sAnn.name.equals("translated-real")){//				parallel_sentences.
				srcText = srcDoc.substring(sAnn.srcOffset, 
							sAnn.srcOffset + sAnn.srcLength);
				trgText = trgDoc.substring(sAnn.thisOffset, 
							sAnn.thisOffset + sAnn.thisLength);

				srcInd = getIndexOfSrc(srcText);
				trgInd = getIndexOfTrg(trgText);
				
				parallelSentences.add(
								srcInd,	//index in the awt.list
								trgInd,	//index in the awt.list						
								srcText,//text, only relevant for summary issued (and to define the index!)
								trgText);
			}				
		}
		return parallelSentences;
	}
	
	public ComparableFragment getComparableSentences() throws IOException{
		//return getSentences("comparable");
		String srcText;
		String trgText;
		
		int srcIndex;
		int trgIndex;
		
		ComparableFragment comparableSentences = new ComparableFragment();
		
		for (Annotation sAnn : annotations.getAnnotations()){
			if (sAnn.name.equals("comparable-real")){//				comparable_sentences.
				srcText = srcDoc.substring(sAnn.srcOffset, 
						sAnn.srcOffset + sAnn.srcLength);
				trgText = trgDoc.substring(sAnn.thisOffset, 
						sAnn.thisOffset + sAnn.thisLength);
				
				srcIndex = getIndexOfSrc(srcText);
				trgIndex = getIndexOfTrg(trgText);
				
				comparableSentences.add(
						srcIndex,
						trgIndex,
						srcText,
						trgText);
			}
				
		}
		return comparableSentences;
	}	
	

	
	private int getIndexOfSrc(String src_text){
		for (int i = 0 ; i < srcArray.length ; i++)
			if (srcArray[i].equals(src_text))
				return i;
		return -1;
		
	}
	private int getIndexOfTrg(String trg_text){
		for (int i = 0 ; i < trgArray.length ; i++)
			if (trgArray[i].equals(trg_text))
				return i;
		return -1;
		
	}

	
//	
//	private int getID(String text_id){
////		text_id = text_id.substring(text_id.indexOf("_")+1);
////		text_id = text_id.substring(0, text_id.indexOf("."));
//		return Integer.valueOf(text_id);
//	}

	
}
