package cat.lump.ir.index;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.retrievalmodels.document.RepresentationType;

/**
 * Contains the basic operation for indexing and querying a documents' index
 * @author albarron
 *
 */
public abstract class Abstracter {
	
	/** Language of the index/query */
	protected Locale locale;
	
	/** Internal collection of documents */
	protected DocumentCollection docCollection;
	
	/** Inverted index used to compute similarities */	
	protected Map<RepresentationType, Index> index;
	
	/** Set of representations to generate/load in the index */
	protected RepresentationType[] repType;
	
	/**Representations to include */
	protected Boolean INCLUDE_BOW, INCLUDE_COG, INCLUDE_CNG, INCLUDE_WNG;
	
	/** Name for the output documents' object file */
	protected final String COLLECTION_FILE = "documents.clt";
	
	/** Prefix and suffix for the index-related files */
	protected final String[] INDEX_FILE = {"documents", ".idx"};

	/** Path to the index */
	protected File indexPath;
	
	/**
	 * Calls the setters for language and representation type. Additionally,
	 * loads the index (even if empty, for an index generation process).
	 *   
	 * @param lan
	 * @param indexDir
	 * @param repr
	 */
	public Abstracter(Locale lan, File indexDir, RepresentationType repr[])
	{
		setLanguage(lan);
		setIndexPath(indexDir);
		setRepresentation(repr);
		loadIndex();		
	}	
	
	/**
	 * Loads the index components (empty if new, with data if existed 
	 * previously).
	 * 
	 * @param dir 
	 * 			index directory
	 */
	protected void loadIndex()
	{
		CHK.CHECK_NOT_NULL(indexPath);
		CHK.CHECK(FileIO.dirCanBeRead(indexPath), 
			"The directory does not exist or cannot be read");
	
		index = new HashMap<RepresentationType, Index>();
		File colFile= new File(indexPath + FileIO.separator + COLLECTION_FILE);
		
		
		if (! colFile.exists())
		{
			//build the index from scratch
			docCollection = new DocumentCollection();
			for (RepresentationType repr : repType)
			{
			index.put(repr, new Index());
			}
		} else {
			//load a previously existing index
			docCollection = (DocumentCollection) FileIO.readObject(colFile);
			StringBuilder indFile;
		
			for (RepresentationType rep : repType)
			{		
				indFile = new StringBuilder()
						.append(indexPath)
						.append(FileIO.separator)
						.append(INDEX_FILE[0])	//documents
						.append(rep)
						.append(INDEX_FILE[1]);	//.idx
						
				index.put(rep, 
						(Index) FileIO.readObject(new File(indFile.toString())));
			}		
		}
	}
	
	/**
	 * Set the language for the document collection.
	 * @param lan
	 * 			language of the documents.
	 */
	private void setLanguage(Locale lan)
	{
		CHK.CHECK_NOT_NULL(lan);
		locale = lan;
	}
	
	/**
	 * @param path
	 * 			path to the index location.
	 */
	private void setIndexPath(File path)
	{
		CHK.CHECK_NOT_NULL(path);
		indexPath = path;
	}
	
	/**
	 * Set the representation used by the index. It could be bag of words, 
	 * pseudo-cognates, character n-grams, or word n-grams
	 * TODO in the case of n-grams, n has to be set.
	 * 
	 * @param representation
	 */
	private void setRepresentation(RepresentationType[] representation)
	{
		
		INCLUDE_BOW = false;			
		INCLUDE_COG = false;
		INCLUDE_CNG = false;
		INCLUDE_WNG = false;
		
		for (RepresentationType repr : representation)
		{
			CHK.CHECK_NOT_NULL(repr);
			if (repr.equals(RepresentationType.BOW)) {
				INCLUDE_BOW = true;			
			} else if (repr.equals(RepresentationType.COG)){
				INCLUDE_COG = true;
			} else if (repr.equals(RepresentationType.CNG)){
				INCLUDE_CNG = true;
			} else if (repr.equals(RepresentationType.WNG)){
				INCLUDE_WNG = true;
			}		
		}
		repType = representation;
	}

}
