package cat.lump.ir.lucene.index.analyzers;


import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.hunspell.HunspellDictionary;
import org.apache.lucene.analysis.hunspell.HunspellStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public class SlovenianAnalyzer extends Analyzer {
    private final HunspellDictionary dictionary;
   
    public SlovenianAnalyzer() {
      super();
      try {
      dictionary = new HunspellDictionary(
          getClass().getResourceAsStream("dicts/sl_SI.aff"),
          getClass().getResourceAsStream("dicts/sl_SI.dic"),
          Version.LUCENE_30);
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

