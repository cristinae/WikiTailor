package cat.lump.ie.textprocessing.transform;

import com.ibm.icu.text.Transliterator;

public class Transformation {

	
	public static void main(String[] args){
		System.out.println(removeAccents("áäñà"));
	}
	
	public static String removeAccents(String text){
		Transliterator trans = Transliterator.getInstance("NFD; [:NonspacingMark:] Remove; NFC");
		return trans.transliterate(text);
			
	}
}
