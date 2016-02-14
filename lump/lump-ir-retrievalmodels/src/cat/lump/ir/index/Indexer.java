package cat.lump.ir.index;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.retrievalmodels.document.Document;
import cat.lump.ir.retrievalmodels.document.RepresentationType;

/**
 * A class that index a set of Documents on the basis of a given
 * representation.
 * 
 * @version 0.2
 * @since May, 2014
 * @author albarron
 *
 */
public class Indexer extends Abstracter{
	
	
	/**
	 * Invocation where the documents' language is given and the directory
	 * for the index is provided.
	 * <br/>
	 * The program will try to load the index from the directory (if it
	 * previously exists)
	 * 
	 * @param lan
	 * 				language for the indexed documents
	 * @param indexDir
	 * 				path to the index (or where it should be saved)
	 * @param repr
	 * 				representation (to be) indexed
	 */
	public Indexer(Locale lan, File indexDir, RepresentationType[] repr)
	{
		super(lan, indexDir, repr);			
	}	
	
	/**
	 * Default invocation where files are in English
	 * 
	 * @param indexDir
	 * 				path to the index (or where it should be saved)
	 * @param representation
	 * 				representation (to be) indexed
	 */
	public Indexer(File indexDir, RepresentationType[] representation)
	{
		this(Locale.ENGLISH, indexDir, representation);
	}
	
	/**
	 * Add a new document file to the index
	 * @param file
	 * @param id
	 * @throws IOException
	 */
	public void addDocument(File file, String id) throws IOException
	{
		CHK.CHECK_NOT_NULL(file);
		CHK.CHECK_NOT_NULL(id);
		addDocument(FileIO.fileToString(file), id);
	}
	
	/**
	 * Add a new document to the index. 
	 * @param text 
	 * @param id
	 */
	public void addDocument(String text, String id)
	{
		CHK.CHECK_NOT_NULL(text);
		CHK.CHECK_NOT_NULL(id);		
		
		Document doc = new Document(text, locale, 
				INCLUDE_BOW, INCLUDE_CNG, INCLUDE_WNG, INCLUDE_COG);
		
		//Adds a document to the collection
		docCollection.addDocument(doc, id);
		//Fill the document into the index
		for (RepresentationType rep : repType)
		{			
			index.get(rep).add(doc.getWeighted(rep), id);
		}		
	}
	
	/**
	 * Remove document id from the index and documents' collection
	 * @param id
	 */
	public void removeDocument(int id)
	{		
		docCollection.removeDocument(id);
		for (RepresentationType rep : repType)
		{			
			index.get(rep).remove(id);
		}
				
	}	

	/**
	 * Saves the documents into the provided output directory
	 * @param dir
	 */
	public void saveIndex()
	{		
		CHK.CHECK(FileIO.dirCanBeRead(indexPath), 
			"The directory does not exist or cannot be read");
		
		FileIO.writeObject(docCollection, 
			new File(indexPath + FileIO.separator + COLLECTION_FILE));
		
		StringBuilder indFile;
		for (RepresentationType rep : repType)
		{		
			indFile = new StringBuilder()
					.append(indexPath)
					.append(FileIO.separator)
					.append(INDEX_FILE[0])	//documents
					.append(rep)
					.append(INDEX_FILE[1]);	//.idx	
					
			FileIO.writeObject(index.get(rep), new File(indFile.toString()));
		}				
	}
	
	public static void main(String[] args){
		String path =
			"/home/albarron/workspace/lump2-ir-retrievalmodels/texts";
		File indexDir = 
			new File("/home/albarron/workspace/lump2-ir-retrievalmodels/index");
		
		String[] files = {"362", "474", "500"};
		//TODO define the level of the n-grams
		RepresentationType[] repRequired = 
					{RepresentationType.BOW, RepresentationType.COG};
		
		Indexer indexer = new Indexer(new Locale("es"), indexDir, 
				repRequired);
		for (String f : files)
		{
			try 
			{
				indexer.addDocument(
					new File(String.format("%s%s%s", path, File.separator, f)), 
					f);
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}			
		}
		indexer.saveIndex();
		
	}

}
