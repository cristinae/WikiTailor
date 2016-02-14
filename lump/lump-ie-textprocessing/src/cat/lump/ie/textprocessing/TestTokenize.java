package cat.lump.ie.textprocessing;

import java.util.Locale;

import cat.lump.ie.textprocessing.word.WordDecompositionICU4J;

public class TestTokenize {
	
	public static void main(String[] args)
	{
		
		WordDecompositionICU4J tokenizer = new WordDecompositionICU4J(Locale.ENGLISH);;
		String text = "Escribe algo \"estúpido\" “estúpido” «estúpido» ‘estúpido’ —estúpido—. ¡Maldito, ؟maldito؛ seas! ١.٨";
		for (Span span: tokenizer.getSpans(text))
			System.out.println(text.substring(span.getStart(), span.getEnd()));
		
	}

}
