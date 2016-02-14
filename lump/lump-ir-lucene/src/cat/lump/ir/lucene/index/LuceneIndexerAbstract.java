/**
 * Created on 12 Apr 2012<br><br>
 * Software being developed by lbarron
 */

package cat.lump.ir.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.log.LumpLogger;

/**
 * An indexer based on Lucene in Action 2nd edition.
 * A few adaptations were necessary to make it work with 
 * Lucene 3.5.0. The similarity considered adapts Lucene's DefaultSimilarity to 
 * consider TF only.
 * <br>
 * This class is generic and intended to be extended 
 * on the basis of the language of the texts to process.
 * <br>
 * 
 * Note that in order to add a new document to an already
 * existing index it is only necessary to run the code again
 * considering the same path to the already existing index.
 * <br>
 * 
 * Special attention has to be paid respect to the rest of 
 * libraries because it seems like JWPL's Lucene modules 
 * included (which are older) are sometimes considered.
 * 
 * @author albarron 
 *
 */
public abstract class LuceneIndexerAbstract {	
	
	

	

	
	
	
	

		
	
	
	


	

	
	
	
}