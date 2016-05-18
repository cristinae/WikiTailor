package cat.lump.aq.textextraction.wikipedia.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.textextraction.wikipedia.prepro.TypePreprocess;

/**
 * This class provides a set of methods to manage files which stores preprocessed
 * Wikipedia pages.
 * 
 * @author jboldoba
 * 
 * TODO system dependent notation /
 */
public class FileManager
{

	private static final int IDS_PER_DIR = 100000;

	/**
	 * Creates the abstract path for a given page considering its ID, language
	 * and the type of preprocess.
	 * 
	 * <p>
	 * The path will be constructed as follows:
	 * {@code root/type/language/index/filename.txt} where {@code index} is the
	 * result of dividing {@code pageID} by {@value #IDS_PER_DIR} and
	 * {@code filename} is formed by concatenating the {@code pageID} and
	 * {@code language}, separated by dots.
	 * </p>
	 * 
	 * @param root
	 *            Root directory.
	 * @param type
	 *            Type of preprocess
	 * @param language
	 *            Language of the page
	 * @param pageID
	 *            Identifier of the page
	 * @return The abstract path for the given parameters.
	 */
	public static File getFile(File root, TypePreprocess type, String language,
			int pageID)
	{
		String filename = String.format("%d.%s.txt", pageID, language);
		String pathToFile = String.format("%s/%s/%d/%s", type.toString(),
				language, (pageID / IDS_PER_DIR), filename);
		return new File(root, pathToFile);
	}

	/**
	 * Creates the abstract path for a given page considering its ID and
	 * language
	 * 
	 * <p>
	 * The path will be constructed as follows:
	 * {@code root/language/index/filename.txt} where {@code index} is the
	 * result of dividing {@code pageID} by {@value #IDS_PER_DIR} and
	 * {@code filename} is formed by concatenating the {@code pageID} and
	 * {@code language}, separated by dots.
	 * </p>
	 * 
	 * @param root
	 *            Root directory.
	 * @param language
	 *            Language of the page
	 * @param pageID
	 *            Identifier of the page
	 * @return The abstract path for the given parameters.
	 */
	public static File getFile(File root, String language, int pageID)
	{
		String filename = String.format("%d.%s.txt", pageID, language);
		String pathToFile = String.format("/%s/%d/%s", language,
				(pageID / IDS_PER_DIR), filename);
		return new File(root, pathToFile);
	}

	/** 
	 * Path and name to the file that indexes the position and length of the 
	 * articles in a subfolder as defined in 
	 * {@code getFile(File root, TypePreprocess type, String language, int pageID)}
	 * 
	 * @param parent
	 * @return The abstract path to the file
	 */
	private static File getFileIndex(File parent) {
		String parentDirName = parent.getName().toString();
		String parentDirFront = parent.toString().substring(0, 
				parent.toString().lastIndexOf(FileIO.separator));
		String fileDirName = parentDirFront + FileIO.separator + parentDirName + ".ids";
		
		return new File(fileDirName);
	}

	/**
	 * Retrieve the name of the preprocessed files in a language from the given
	 * directory.
	 * 
	 * @param directory
	 *            Directory wherein are the files
	 * @param language
	 * @param recursive
	 *            If {@code true}, it will search inside the sub-directories.
	 *            Otherwise, only the files in the given directory will be
	 *            retrieved.
	 * @return The set of names of the retrieved files
	 */
	public static Set<String> getFilenames(File directory, String language,
			boolean recursive)
	{
		HashSet<String> paths = new HashSet<String>();
		if (recursive)
		{
			getFilenamesRec(directory, language, paths);
		}
		else
		{
			File[] files = directory.listFiles(new FilenameLanguageFilter(language));
			for (File file : files) {
				paths.add(file.getAbsolutePath());
			}
		}
		return paths;
	}

	private static void getFilenamesRec(File directory, String language,
			Set<String> result)
	{
		File[] files = directory.listFiles(new FilenameLanguageFilter(language));
		for (File file : files) {
			result.add(file.getAbsolutePath());
		}

		File[] subdirs = directory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname)
			{
				return pathname.isDirectory();
			}
		});
		for (File dir : subdirs)
		{
			getFilenamesRec(dir, language, result);
		}
	}

	/**
	 * Saves the text contained in the buffer in the correct file considering
	 * the page ID, language and type of preprocess. The index file of the folder
	 * is actualised. If the index directory is created, also the file to keep 
	 * track of the files is created.
	 * 
	 * @param root
	 *            Root directory.
	 * @param type
	 *            Type of preprocess
	 * @param language
	 *            Language of the page
	 * @param pageID
	 *            Identifier of the page
	 * @param text
	 *            Buffer which contains the text to write in the file.
	 * @return The saved file
	 * @throws IOException
	 */
	public static File savePage(File root, TypePreprocess type,
			String language, int pageID, StringBuffer text) throws IOException
	{
		File output = getFile(root, type, language, pageID);
		File parent = output.getParentFile();
		
		//number of lines in the file
		int linesText = 0;
		for(int pos=text.indexOf("\n"); pos!=-1; pos=text.indexOf("\n",pos+1)){
			linesText++;
		}
		String infoFile = String.format("%s\t%d\n", output.getName(), linesText);;
		File indexFile = getFileIndex(parent);				
		
		if (!parent.exists())
		{
			parent.mkdirs();			
			FileIO.stringToFile(indexFile, infoFile, false);
		} else {
			FileIO.appendStringToFile(indexFile, infoFile, false);			
		}
		FileIO.stringToFile(output, text.toString(), false);
		return output;
	}


	/**
	 * Loads the file related to the given parameters in text format.
	 * 
	 * @param root
	 *            Root directory.
	 * @param type
	 *            Type of preprocess
	 * @param language
	 *            Language of the page
	 * @param pageID
	 *            Identifier of the page
	 * @return The text stored in the file.
	 * @throws IOException
	 */
	public static String loadFile(File root, TypePreprocess type,
			String language, int pageID) throws IOException
	{
		File file = getFile(root, type, language, pageID);
		return FileIO.fileToString(file);
	}

	/**
	 * Checks if a file related to a page exists on the given root directory.
	 * 
	 * @param root
	 *            Root directory.
	 * @param type
	 *            Type of preprocess
	 * @param language
	 *            Language of the page
	 * @param pageID
	 *            Identifier of the page
	 * @return {@code true} if the file exists.
	 */
	public static boolean existsFile(File root, TypePreprocess type,
			String language, int pageID)
	{
		return getFile(root, type, language, pageID).exists();
	}

	/**
	 * 
	 * @author cmops
	 *
	 */
	static class FilenameLanguageFilter implements FilenameFilter
	{

		private Pattern pattern;

		public FilenameLanguageFilter(String language)
		{
			String regex = String.format("\\d*\\.%s\\.txt", language);
			pattern = Pattern.compile(regex);
		}

		@Override
		public boolean accept(File dir, String name)
		{
			Matcher matcher = pattern.matcher(name);
			return matcher.matches();
		}

	}
	
	/**
	 * Example to get files with filename filter
	 * @param args
	 */
	public static void main(String[] args) {
		File dir = new File(System.getProperty("user.home"), "plain")	;
		System.out.println(FileManager.getFilenames(dir, "es", true));
		
	}
}
