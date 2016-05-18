package cat.lump.aq.textextraction.wikipedia.categories;

import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiTitleParsingException;

/**
 * This class stores all the relevant information about a classified
 * category.
 * 
 * @author jboldoba
 * 
 */
public  class CategoryTreeNode
{
	/**
	 * Depth of the subtree where the category has been found.
	 */
	private int depth;
	/**
	 * Wikipedia category
	 */
	private Category category;
	/**
	 * Parent ID. ID of the category from the extractor has reached this
	 * category. A root category should have a PID=-1.
	 */
	private int PID;
	/**
	 * The format used to transform the object to String
	 */
	private static final String out_template = "%d\t%d\t%s\t%d\t%d\t%d\t%d";

	/**
	 * Constructor.
	 * 
	 * @param category
	 *            Wikipedia category
	 * @param depth
	 *            Depth where it has been found
	 * @param PID
	 *            Parent PID
	 */
	public CategoryTreeNode(Category category, int depth, int PID)
	{
		this.depth = depth;
		this.category = category;
		this.PID = PID;
	}

	public boolean equals(Object o)
	{
		return o.getClass().equals(Category.class)
				&& ((Category) o).getPageId() == getPageID();
	}

	/**
	 * Returns the depth where this category has been found
	 * 
	 * @return The depth
	 */
	public int getDepth()
	{
		return depth;
	}

	/**
	 * Returns the ID of the category at the used Wikipedia
	 * 
	 * @return The page ID of this category
	 */
	public int getPageID()
	{
		return category.getPageId();
	}

	/**
	 * Returns the title of the category in Wikitext (without blanks).
	 * 
	 * @return The title of the category. It returns the dummy title
	 *         <code>NO_NAME</code> when the query fails.
	 */
	public String getTitle()
	{
		try
		{
			return category.getTitle().getWikiStyleTitle();
		} catch (WikiTitleParsingException e)
		{
			return "NO_NAME";
		}
	}

	/**
	 * Returns the number of articles that are categorized under this
	 * category.
	 * 
	 * @return The number of Wikipedia pages of this category
	 */
	public int getNumberOfArticles()
	{
		try
		{
			return category.getNumberOfPages();
		} catch (WikiApiException e)
		{
			return -1;
		}
	}

	/**
	 * Returns the number of children (subcategories) of this category.
	 * 
	 * @return The number of subcategories of this category
	 */
	public int getNumberOfChildren()
	{
		return category.getNumberOfChildren();
	}

	/**
	 * Returns the number of parents (supercategories) of this category.
	 * 
	 * @return The number of supercategories of this category
	 */
	public int getNumberOfParents()
	{
		return category.getNumberOfParents();
	}

//	/**
//	 * Returns the ID of the supercategory used to found this category
//	 * 
//	 * @return Parent ID
//	 */
//	public int getPID()
//	{
//		return PID;
//	}

	/**
	 * Returns the category.
	 * 
	 * @return The category
	 */
	public Category getCategory()
	{
		return category;
	}

//	/**
//	 * Returns the header of the output.
//	 * 
//	 * @return The header
//	 */
//	public static String getOutHeader()
//	{
//		return "Depth\t PageID \t Title  \tArticles\tChildren\tParents\t  PID  ";
//	}

	/**
	 * Transforms the object into a string
	 */
	public String toString()
	{
		return String.format(out_template, depth, getPageID(), getTitle(),
				getNumberOfArticles(), getNumberOfChildren(),
				getNumberOfParents(), PID);
	}
}