package cat.lump.ir.lucene.index;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiBits;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;

public class LuceneIndexExplorer {
  
  private static final String indexPath = "/data/alt/corpora/semeval2017/task1/clesa/tinyindex/ar";
  
  public static void main(String[] args) throws IOException {
	Path path = FileSystems.getDefault().getPath(indexPath);
    Directory dir = FSDirectory.open(path);    
    IndexReader reader = DirectoryReader.open(dir);
    
    for (int i=0; i<reader.maxDoc(); i++) {
    	Bits liveDocs = MultiBits.getLiveDocs(reader);
    	if (liveDocs != null && !liveDocs.get(i)) 
          continue;
//    	 TODO before we had this to check if a document has been
//           deleted. According to this, though, this should not be
//          necessary:
//        	  https://lucene.apache.org/core/8_0_0/core/org/apache/lucene/index/IndexReader.html
//    	Check this as well:
//    		https://lucene.apache.org/core/4_1_0/MIGRATE.html
//    	(search for "IndexReaders are now read-only")
    	
//    	if (reader.isDeleted(i))
//          continue;

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
