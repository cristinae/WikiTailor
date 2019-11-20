package cat.lump.ir.lucene.index;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneIndexExplorer {
  
  private static final String indexPath = "/data/alt/corpora/semeval2017/task1/clesa/tinyindex/ar";
  
  public static void main(String[] args) throws IOException {
	Path path = FileSystems.getDefault().getPath(indexPath);
    Directory dir = FSDirectory.open(path);
    
    IndexReader reader = IndexReader.open(dir);
    for (int i=0; i<reader.maxDoc(); i++) {
      if (reader.isDeleted(i))
          continue;

      Document doc = reader.document(i);
      String docId = doc.get("filename");
      
      System.out.println(docId);
      TermFreqVector x = reader.getTermFreqVector(i, LuceneIndexerWT.CONTENTS_NAME);
      for (String s : x.getTerms()) {
        System.out.print(s + " ");
      }
      System.out.println();
//      doc.get
//      System.out.println(doc.getFields());
//      System.out.println(doc.get(LuceneIndexerWT.CONTENTS_NAME));//"fullpath"));

//      String docId = doc.get(LuceneIndexerWT.CONTENTS_NAME);
//      doc.get("")
//      System.out.println(docId);
//      System.out.println(contents.toString());
      // do something with docId here...
  }
  }

}
