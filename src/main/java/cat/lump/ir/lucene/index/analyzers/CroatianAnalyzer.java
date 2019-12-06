package cat.lump.ir.lucene.index.analyzers;


import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

import org.apache.lucene.analysis.hunspell.Dictionary;
import org.apache.lucene.analysis.hunspell.HunspellStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;


//	@Override
//	protected TokenStreamComponents createComponents(String fieldName) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
// TODO perhaps base it on the Analyzers from lucene-analyzers-common-8.3.0
// See http://lucene.apache.org/core/8_3_0/analyzers-common/index.html


public class CroatianAnalyzer extends Analyzer {
    private final Dictionary dictionary;
   
    public CroatianAnalyzer() {
      super();
      try {
      dictionary = new Dictionary(
          getClass().getResourceAsStream("dicts/hr_HR.aff"),
          getClass().getResourceAsStream("dicts/hr_HR.dic"),
          Version.LUCENE_30);
      new Dictionary()
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
   
    @Override
    public TokenStream tokenStream(String field, Reader reader) {
      return new HunspellStemFilter(new StandardTokenizer(Version.LUCENE_30, reader), dictionary);
    }

    private class SavedStreams {
      Tokenizer tokenizer;
      TokenStream filter;
    }
   
    @Override
    public TokenStream reusableTokenStream(String fieldName, Reader reader)
        throws IOException {
      SavedStreams streams = (SavedStreams) getPreviousTokenStream();
      if (streams == null) {
        streams = new SavedStreams();
        streams.tokenizer = new StandardTokenizer(Version.LUCENE_30, reader);
        streams.filter = new HunspellStemFilter(streams.tokenizer, dictionary);
        setPreviousTokenStream(streams);
      } else {
        streams.tokenizer.reset(reader);
        streams.filter.reset();
      }
      return streams.filter;
    }


}

