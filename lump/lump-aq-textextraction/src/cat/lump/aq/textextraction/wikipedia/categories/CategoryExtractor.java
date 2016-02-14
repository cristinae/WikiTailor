package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliCategoryExtractor;
import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * This class extracts all the subcategories from an indicated category in
 * Wikipedia
 * 
 * TODO build junit
 * 
 * @author jboldoba
 * 
 */
public class CategoryExtractor
{

	private static final int SUCCESS = 0;
//	private static final int PARAM_ERR = -1;
	private final int SQL_ERROR = -2;
	private final int IO_ERROR = -3;

	private Locale locale;
	
	private boolean verbose;
	
	private WikipediaJwpl wiki;
	
	private TreeMap<Integer, List<CategoryTreeNode>> catTree;
	
	private final String no_verbose_line = "%d\t%d\t%s";
	
	private String outputDir = System.getProperty("user.dir");
	
	private String categoryFileName;

	private LumpLogger logger;
			
	/**
	 * Default ---non-verbose--- invocation.
	 * @param locale
	 * @param year
	 */
	public CategoryExtractor(Locale locale, int year, String categoryFileName) {
		this(locale, year, false, categoryFileName);
	}
	
	/**
	 * Invocation in which verbosity is set.
	 * 
	 * @param locale
	 * @param year
	 * @param verbose
	 */
	public CategoryExtractor(Locale locale, int year, boolean verbose, String categoryFileName){
		CHK.CHECK_NOT_NULL(locale);
		CHK.CHECK_NOT_NULL(year);
	
		this.locale = locale;	
		this.categoryFileName = categoryFileName;
		logger = new LumpLogger (CategoryExtractor.class.getSimpleName());
		wiki = null;
		try {
			wiki = new WikipediaJwpl(locale, year);
		} catch (WikiApiException e) {
			e.printStackTrace();
			System.exit(SQL_ERROR);
		}		
	}
		
	public void getCategoryTree(int categoryID, int maxDepth){
		Category rootCategory = wiki.getCategory(categoryID);	
		CHK.CHECK(rootCategory != null,
				String.format("The category %s doesn't exist", rootCategory));
		generateCategoryTree(rootCategory, maxDepth);
	}	
	
	/**
	 * Creates a tree of categories with the <code>root</code> category as root
	 * and all its subcategories allocated by levels of depth. The maximum
	 * depth is defined by <code>maxDepth</code>.
	 * 
	 * @param root Category used as root of the tree
	 * @param maxDepth Value of the maximum depth of the tree. If it equals
	 *            <code>-1</code>, there isn't any maximum depth defined.
	 * @return The subcategories tree with <code>root</code> category as
	 *         start point.
	 */
	public void generateCategoryTree(Category root, int maxDepth) {		
		logger.info("Starting with the tree analysis");
		int depth;
		Set<Integer> visitedCategories = new HashSet<Integer>();
		catTree = new TreeMap<Integer, List<CategoryTreeNode>>();;

		// TreeMap<Integer, CategoryTreeNode> classificationTree = new
		// TreeMap<Integer, CategoryTreeNode>();

		ConcurrentLinkedQueue<CategoryTreeNode> pendingCategories = 
								new ConcurrentLinkedQueue<CategoryTreeNode>();

		CategoryTreeNode currentNode = new CategoryTreeNode(root, 1, -1);
		pendingCategories.add(currentNode);

		while (!pendingCategories.isEmpty()) {
			currentNode = pendingCategories.poll();
			// Skip visited nodes
			if (!visitedCategories.contains(currentNode.getPageID()))	{
				visitedCategories.add(currentNode.getPageID());
				depth = currentNode.getDepth();
				// Add top the classification tree
				if (catTree.containsKey(depth))	{
					catTree.get(depth).add(currentNode);
				}	else {
					logger.info("Current depth: " + depth);
					List<CategoryTreeNode> newDepthList = new ArrayList<CategoryTreeNode>();
					newDepthList.add(currentNode);
					catTree.put(depth, newDepthList);
				}

				// If maximum depth is not reached, feed the queue
				if (depth != maxDepth) {
					for (Category child : currentNode.getCategory().getChildren()) {
						CategoryTreeNode auxiliarNode = new CategoryTreeNode(
								child, currentNode.getDepth() + 1,
								currentNode.getPageID());
						pendingCategories.add(auxiliarNode);
					}
				}
			} else {
				//too much verbose
				//logger.info(currentNode.getPageID()+ " already visited.");
			}
		}		
	}

//	/**
//	 * This class stores all the relevant information about a classified
//	 * category.
//	 * 
//	 * @author jboldoba
//	 * 
//	 */
//	private static class CategoryTreeNode
//	{
//		/**
//		 * Depth of the subtree where the category has been found.
//		 */
//		private int depth;
//		/**
//		 * Wikipedia category
//		 */
//		private Category category;
//		/**
//		 * Parent ID. ID of the category from the extractor has reached this
//		 * category. A root category should have a PID=-1.
//		 */
//		private int PID;
//		/**
//		 * The format used to transform the object to String
//		 */
//		private static final String out_template = "%d\t%d\t%s\t%d\t%d\t%d\t%d";
//
//		/**
//		 * Constructor.
//		 * 
//		 * @param category
//		 *            Wikipedia category
//		 * @param depth
//		 *            Depth where it has been found
//		 * @param PID
//		 *            Parent PID
//		 */
//		public CategoryTreeNode(Category category, int depth, int PID)
//		{
//			this.depth = depth;
//			this.category = category;
//			this.PID = PID;
//		}
//
//		public boolean equals(Object o)
//		{
//			return o.getClass().equals(Category.class)
//					&& ((Category) o).getPageId() == getPageID();
//		}
//
//		/**
//		 * Returns the depth where this category has been found
//		 * 
//		 * @return The depth
//		 */
//		public int getDepth()
//		{
//			return depth;
//		}
//
//		/**
//		 * Returns the ID of the category at the used Wikipedia
//		 * 
//		 * @return The page ID of this category
//		 */
//		public int getPageID()
//		{
//			return category.getPageId();
//		}
//
//		/**
//		 * Returns the title of the category in Wikitext (without blanks).
//		 * 
//		 * @return The title of the category. It returns the dummy title
//		 *         <code>NO_NAME</code> when the query fails.
//		 */
//		public String getTitle()
//		{
//			try
//			{
//				return category.getTitle().getWikiStyleTitle();
//			} catch (WikiTitleParsingException e)
//			{
//				return "NO_NAME";
//			}
//		}
//
//		/**
//		 * Returns the number of articles that are categorized under this
//		 * category.
//		 * 
//		 * @return The number of Wikipedia pages of this category
//		 */
//		public int getNumberOfArticles()
//		{
//			try
//			{
//				return category.getNumberOfPages();
//			} catch (WikiApiException e)
//			{
//				return -1;
//			}
//		}
//
//		/**
//		 * Returns the number of children (subcategories) of this category.
//		 * 
//		 * @return The number of subcategories of this category
//		 */
//		public int getNumberOfChildren()
//		{
//			return category.getNumberOfChildren();
//		}
//
//		/**
//		 * Returns the number of parents (supercategories) of this category.
//		 * 
//		 * @return The number of supercategories of this category
//		 */
//		public int getNumberOfParents()
//		{
//			return category.getNumberOfParents();
//		}
//
////		/**
////		 * Returns the ID of the supercategory used to found this category
////		 * 
////		 * @return Parent ID
////		 */
////		public int getPID()
////		{
////			return PID;
////		}
//
//		/**
//		 * Returns the category.
//		 * 
//		 * @return The category
//		 */
//		public Category getCategory()
//		{
//			return category;
//		}
//
////		/**
////		 * Returns the header of the output.
////		 * 
////		 * @return The header
////		 */
////		public static String getOutHeader()
////		{
////			return "Depth\t PageID \t                         Title                         \tArticles\tChildren\tParents\t  PID  ";
////		}
//
//		/**
//		 * Transforms the object into a string
//		 */
//		public String toString()
//		{
//			return String.format(out_template, depth, getPageID(), getTitle(),
//					getNumberOfArticles(), getNumberOfChildren(),
//					getNumberOfParents(), PID);
//		}
//	}
	
	
	
	/**
	 * Dump tree into a file. The resulting file will have the name 
	 * [categoryID].[iso639].[maxDept].category
	 * 
	 * @param categoryID
	 * @param maxDepth
	 */
	public void toFile(int categoryID, int maxDepth){		
		StringBuffer sb = new StringBuffer();
		for (Integer depth : catTree.keySet()) {
			List<CategoryTreeNode> nodes = catTree.get(depth);
			for (CategoryTreeNode node : nodes) {
				String line = verbose ? node.toString() :
				      String.format(no_verbose_line, node.getDepth(), 
				          node.getPageID(), node.getTitle());
				sb.append(line)
				  .append("\n");
			}
		}

		File output = new File(outputDir, String.format(
				categoryFileName, locale, categoryID, maxDepth));
		try	{
			FileIO.stringToFile(output, sb.toString(), false);
		} catch (IOException e)	{
			e.printStackTrace();
			System.exit(IO_ERROR);
		}
		
		logger.info("Categories saved into " + output.toString());
	}
	
	public  Map<Integer, List<CategoryTreeNode>> getCategoryTree(){
		return catTree;
	}
	
	public void setOutputDir(String outDir){
		outputDir = outDir;
		File f = new File(outputDir);
		CHK.CHECK(f.isDirectory(), "The output directory does not exist?");
	}
	
	/**
	 * Main method. It could be called as a tool to extract the subcategories
	 * 
	 * @param args
	 *            List of input parameters:
	 *            <ul>
	 *            <li>language: Language of the Wikipedia</li>
	 *            <li>year: Year of the Wikipedia</li>
	 *            <li>category: ID of the target category at the Wikipedia
	 *            defined by <code>language</code> and <code>year</code>.</li>
	 *            <li>depth: Maximum depth of the subcategories tree allowed to
	 *            reach when it's extracting them.</li>
	 *            <li>verbose: Optional. Any value different of 0 indicates that
	 *            verbose option is enabled.</li>
	 *            </ul>
	 */
	public static void main(String[] args)
	{
		WikipediaCliCategoryExtractor cli = new WikipediaCliCategoryExtractor();
		cli.parseArguments(args);
		Locale locale = cli.getLanguage();
		int year = cli.getYear();
		int categoryID = cli.getCategoryID();
		int maxDepth = cli.getMaxDepth();
		boolean verbose = cli.getVerbose();
		String categoryFileName = cli.getPropertyStr("categoryFileName");
		
		CategoryExtractor ce = new CategoryExtractor(locale, year, verbose, categoryFileName);
		
		ce.getCategoryTree(categoryID, maxDepth);
		ce.toFile(categoryID, maxDepth);			
		
		System.exit(SUCCESS);
	}
	
	//THE PROCESS WAS ORIGINALLY ALL IN main AS FOLLOWS.
//		WikipediaCliCategoryExtractor cli = new WikipediaCliCategoryExtractor();
//		cli.parseArguments(args);
//		Locale locale = cli.getLanguage();
//		int year = cli.getYear();
//		int categoryID = cli.getCategoryID();
//		int maxDepth = cli.getMaxDepth();
//		boolean verbose = cli.getVerbose();
//
//		WikipediaJwpl wiki = null;
//		try
//		{
//			wiki = new WikipediaJwpl(locale, year);
//		} catch (WikiApiException e)
//		{
//			e.printStackTrace();
//			System.exit(SQL_ERROR);
//		}
//
//		Category rootCategory = wiki.getCategory(categoryID);
//		logger.info("Starting with the tree analysis");
//		TreeMap<Integer, ArrayList<CategoryTreeNode>> catTree = getCategoryTree(
//				rootCategory, maxDepth);
//
//		// Dump tree into file
//		StringBuffer sb = new StringBuffer();
//		for (Integer depth : catTree.keySet()) {
//			ArrayList<CategoryTreeNode> nodes = catTree.get(depth);
//			for (CategoryTreeNode node : nodes) {
//				String line;
//				if (verbose) {
//					line = node.toString();
//				}
//				else {
//					line = String.format(no_verbose_line, node.getDepth(), node.getPageID(),
//							node.getTitle());
//				}
//				sb.append(line).append("\n");
//			}
//		}
//
//		File output = new File(System.getProperty("user.dir"), String.format(
//				categoryFileName, categoryID, locale, maxDepth));
//		try
//		{
//			FileIO.stringToFile(output, sb.toString(), false);
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//			System.exit(IO_ERROR);
//		}
//
//		logger.info("End of the process");
//		System.exit(SUCCESS);
	


}
