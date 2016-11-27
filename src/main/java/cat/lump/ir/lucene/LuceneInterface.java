package cat.lump.ir.lucene;

import java.io.File;
import java.util.Locale;

import org.apache.lucene.util.Version;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.log.LumpLogger;

/**
 * An abstract class with the necessary data and methods to interact with 
 * Lucene's indexer and querier modules.
 * 
 * @author alberto
 *
 */
public abstract class LuceneInterface {	

	public static final Version LUCENE_VERSION = Version.LUCENE_35;
	
	/**Directory where the Lucene index has to be stored*/
	protected static String indexDir;
	
	/**Language of the texts*/
	protected Locale lan;
	
	protected boolean verbose = false;
	
	protected LumpLogger logger;
		
	protected long PROCESS_START;
//	protected long PROCESS_END;
	
	/**
	 * Set up  
	 */
	public LuceneInterface(String indexDir){
		logger = new LumpLogger("Lucene");
		CHK.CHECK_NOT_NULL(indexDir);
		setIndexDir(indexDir);
		//lan = Locale.ENGLISH;
		PROCESS_START = System.currentTimeMillis();		 
	}
	
	/* Setters */
	
	public void setVerbose(Boolean verbose){
		this.verbose = verbose;
	}
	
	public void setLanguage(String sLan){
		CHK.CHECK_NOT_NULL(sLan);
		lan = new Locale(sLan);
	}
	
	public void setLanguage(Locale lan){
		CHK.CHECK_NOT_NULL(lan);
		this.lan = lan;
	}
	
	public void setIndexDir(String ind){
		CHK.CHECK_NOT_NULL(ind);		
		if (new File(ind).isDirectory()){
			logger.info("Index directory found");
			indexDir = ind;
		} else {
		   	logger.error("I cannot read the index directoy");
		   	System.exit(1);	
		}
	}


}
