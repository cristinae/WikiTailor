package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiTitleParsingException;

/**
 * <p>
 * A {@code GroupOfCategories} instance contains the scored categories from
 * Wikipedia which are related to other called <i>root category</i>. This class
 * doesn't implement any rule to determine if a category must be included in
 * the group or not.
 * </p>
 * <p>
 * A scored category is a Wikipedia category which has been checked if it
 * belongs to a certain domain.
 * </p>
 * 
 * @author cmops
 * @see de.tudarmstadt.ukp.wikipedia.api.Category
 * @see GroupOfCategories.ScoredCategory
 */
public class GroupOfCategories {
	/** Root category */
	private final Category root;
	/** Group of related categories. They are indexed by its page ID. */
	private HashMap<Integer, ScoredCategory> categories;

	// Constructor
	/**
	 * Creates an empty group of categories related to the given root category.
	 * 
	 * @param root
	 *            Root category of the group.
	 */
	public GroupOfCategories(Category root) {
		this.root = root;
		categories = new HashMap<Integer, ScoredCategory>();
	}
	
	public GroupOfCategories(File input, WikipediaJwpl wiki) throws IOException {
		CHK.CHECK_NOT_NULL(input);
		String[] content = FileIO.fileToLines(input);
		int rootID = Integer.parseInt(content[0]);
		root = wiki.getCategory(rootID);
		categories = new HashMap<Integer, ScoredCategory>();
		for (int index = 1; index < content.length; index++) {
			ScoredCategory newCat = new ScoredCategory(content[index], wiki);
			categories.put(newCat.getCategory().getPageId(), newCat);
		}
	}

	// Public
	public static ScoredCategory createScoredCategory(Category cat,
			Category parent, int distance) {
		return new ScoredCategory(cat, parent, distance);
	}

	public static ScoredCategory createScoredCategory(Category cat,
			Category parent, int distance, boolean isDomain) {
		return new ScoredCategory(cat, parent, distance, isDomain);
	}

	/**
	 * Adds a new {@code ScoredCategory} to the group.
	 * 
	 * @param category
	 *            The new scored category
	 */
	public void addScoredCategory(ScoredCategory category) {
		categories.put(category.getCategory().getPageId(), category);
	}

	public ScoredCategory removeScoredCategory(ScoredCategory category) {
		return categories.remove(category.getCategory().getPageId());
	}

	public void toFile(File output) throws IOException {
		CHK.CHECK_NOT_NULL(output);
		FileWriter fw = new FileWriter(output);
		BufferedWriter bw = new BufferedWriter(fw);
		// First line only contains the root category
		bw.append(Integer.toString(root.getPageId())).append("\n");
		// Next lines contains all the group scored categories
		for (ScoredCategory scat : categories.values()) {
			bw.append(scat.toString()).append("\n");
		}
		
		bw.close();
		fw.close();
	}

	// Setters
	// Getters
	public Category getRoot() {
		return root;
	}

	public Collection<ScoredCategory> getCategories() {
		return categories.values();
	}

	public double getScore() {
		int counter = 0;
		for (Entry<Integer, ScoredCategory> entry : categories.entrySet()) {
			if (entry.getValue().isDomain()) {
				counter++;
			}
		}
		double score = (double) counter / (double) categories.size();
		return score;
	}

	// Inner-classes
	/**
	 * <p>
	 * The {@code ScoredCategory} class enriches the
	 * {@code de.tudarmstadt.ukp.wikipedia.api.Category} objects providing the
	 * following information:
	 * <ul>
	 * <li>Parent: The first category which allows access to this one.</li>
	 * <li>Domain check: A binary classification which determines if this
	 * category belongs to the defined domain to work.</li>
	 * <li>Distance from root category: The minimum number of edges in the
	 * categories graph needed to connect this category with its <i>root
	 * category.</i></li>
	 * </ul>
	 * </p>
	 * 
	 * @author cmops
	 * @see de.tudarmstadt.ukp.wikipedia.api.Category
	 */
	public static class ScoredCategory {
		private static final String OUT_TEMPLATE = "%d\t%d\t%s\t%d\t%d\t%d\t%d\t%b";
		/** Category which has been scored. */
		private final Category cat;
		/** Parent of the category. */
		private final Category parent;
		/** Indicates if the category belongs to the domain. */
		private boolean domain;
		/** Distance from the root category to this one. */
		private final int distance;

		// Constructor
		/**
		 * Creates a scored category which hasn't already been scored. It means,
		 * the result of the {@link #isDomain()} function is undefined.
		 * 
		 * @param cat
		 *            Category to score.
		 * @param parent
		 *            Parent of the category {@code cat}.
		 * @param distance
		 *            Distance from the root category to the category
		 *            {@code cat}.
		 */
		public ScoredCategory(Category cat, Category parent, int distance) {
			this.cat = cat;
			this.parent = parent;
			this.distance = distance;
		}

		/**
		 * Creates a scored category defining if its score.
		 * 
		 * @param cat
		 *            Category to score.
		 * @param parent
		 *            Parent of the category {@code cat}.
		 * @param distance
		 *            Distance from the root category to the category
		 *            {@code cat}.
		 * @param domain
		 *            Boolean which indicates if the category {@code cat}
		 *            belongs to the domain or not.
		 */
		public ScoredCategory(Category cat, Category parent, int distance,
				boolean domain) {
			this(cat, parent, distance);
			this.domain = domain;
		}
		
		public ScoredCategory(String text, WikipediaJwpl wiki) {
			String[] fields = text.split("\t");
			int categoryID = Integer.parseInt(fields[1]);
			cat = wiki.getCategory(categoryID);
			int parentID = Integer.parseInt(fields[6]);
			parent = wiki.getCategory(parentID);
			distance = Integer.parseInt(fields[0]);
			if (fields.length > 7) {
				domain = Boolean.parseBoolean(fields[7]);
			}
		}

		// Public
		/**
		 * Returns a string representation of the instance. The string includes
		 * the following data seprated by tabulators:
		 * <ol>
		 * <li>Distance from the root category.</li>
		 * <li>Identifier of the category page.</li>
		 * <li>Name of the category in Wikipedia Style (words separated by
		 * underscores).</li>
		 * <li>Number of articles in the category.</li>
		 * <li>Number of subcategories.</li>
		 * <li>Numeber of parents (supercategories).</li>
		 * <li>Identifier of the parent category nearest to the root.</li>
		 * <li>Boolean which defines if the category belongs to the domain.</li>
		 * </ol>
		 * 
		 * @return The string representation of the instance.
		 */
		public String toString() {
			int pageID = cat.getPageId();
			String name;
			try {
				name = cat.getTitle().getWikiStyleTitle();
			} catch (WikiTitleParsingException e) {
				name = "";
			}
			int articles;
			try {
				articles = cat.getNumberOfPages();
			} catch (WikiApiException e) {
				articles = 0;
			}
			int children = cat.getNumberOfChildren();
			int parents = cat.getNumberOfParents();
			int parentID = (parent != null) ? parent.getPageId() : -1;
			return String.format(OUT_TEMPLATE, distance, pageID, name,
					articles, children, parents, parentID, domain);

		}

		// Setters
		/**
		 * Sets if this category belongs to the domain.
		 * 
		 * @param domain
		 *            Boolean which indicates if the category {@code cat}
		 *            belongs to the domain or not.
		 */
		public void setDomain(boolean domain) {
			this.domain = domain;
		}

		// Getters
		/**
		 * @return The {@code de.tudarmstadt.ukp.wikipedia.api.Category}
		 *         instance which represents the Wikipedia category.
		 */
		public Category getCategory() {
			return cat;
		}

		/**
		 * @return The parent of this category.
		 */
		public Category getParent() {
			return parent;
		}

		/**
		 * @return The score of the category. {@code true} if it belongs to the
		 *         domain and {@code false} if it doesn't belong to it.
		 */
		public boolean isDomain() {
			return domain;
		}

		/**
		 * @return The distance between the root category and this one.
		 */
		public int getDistance() {
			return distance;
		}
	}
}
