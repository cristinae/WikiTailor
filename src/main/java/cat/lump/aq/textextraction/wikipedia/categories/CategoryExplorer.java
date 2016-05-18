package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.categories.GroupOfCategories.ScoredCategory;
import cat.lump.aq.wikilink.jwpl.WikipediaJwpl;
import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiTitleParsingException;

/**
 * <p>
 * The {@code CategoryExplorer} class is used to explore the categories of
 * Wikipedia. These categories are organized as a digraph where the vertex
 * represents them and one edge represent a relation of subcategorization. It
 * means, an arc {@code e = (x,y)} defines that the category {@code y} is a
 * subcategory of the category {@code x}.
 * </p>
 * <p>
 * This class is also used to obtain the subgraph related to a specific domain.
 * This process requires the construction of a domain vocabulary which can be
 * created with this class.
 * </p>
 * 
 * @author cmops
 * 
 */
public class CategoryExplorer {

	public static final int INFINITE_DISTANCE = Integer.MAX_VALUE;
	private WikipediaJwpl wiki;
	private static LumpLogger logger = new LumpLogger(
			CategoryExplorer.class.getSimpleName());

	// Constructor
	/**
	 * Creates a new explorer related to the Wikipedia dump defined by its
	 * language and year.
	 * 
	 * @param lang
	 *            Language of the Wikipedia.
	 * @param year
	 *            Year of the dump.
	 */
	public CategoryExplorer(Locale lang, int year) {
		try {
			wiki = new WikipediaJwpl(lang, year);
		} catch (WikiApiException e) {
			// TODO Log error?
			e.printStackTrace();
		}
	}

	// Public
	/**
	 * Loads a category from Wikipedia by its title.
	 * 
	 * @param title
	 *            Title of the category to load.
	 * @return The category if it exists. {@code null} if any category exists
	 *         with that name.
	 */
	public Category loadCategory(String title) {
		Category cat;
		try {
			cat = wiki.getCategory(title);
		} catch (WikiApiException e) {
			cat = null;
		}
		return cat;
	}

	/**
	 * Loads a category from Wikipedia by its page ID.
	 * 
	 * @param ID
	 *            Identifier of the category to load.
	 * @return The category if it exists. {@code null} if any category exists
	 *         with that ID.
	 */
	public Category loadCategory(int ID) {
		return wiki.getCategory(ID);
	}

	/**
	 * Creates the vocabulary related to the given category. This vocabulary is
	 * composed by the terms that appears in the category articles and its
	 * frequency.
	 * 
	 * @param category
	 *            The category.
	 * @return The vocabulary related to the category.
	 * @throws WikiApiException
	 */
	public DomainVocabulary createCategoryVocabulary(Category category)
			throws WikiApiException {
		CHK.CHECK_NOT_NULL(category);
		Locale language = new Locale(wiki.getLanguage().name());
		DomainVocabulary vocabulary = new DomainVocabulary(language);
		HashSet<Page> pages = null;
		pages = (HashSet<Page>) category.getArticles();
		for (Page page : pages) {
			String text = wiki.getParsedArticle(page.getPageId()).getText();
			vocabulary.addTerms(text);
		}

		return vocabulary;
	}

	/**
	 * Checks if a category belongs to the domain defined by the given
	 * vocabulary. A category belongs to one domain if its title contains at
	 * least one word which is included in the domain vocabulary.
	 * 
	 * @param category
	 *            The category to check.
	 * @param vocabulary
	 *            The vocabulary of the desired domain.
	 * @return {@code true} if the category belongs to the domain. Otherwise,
	 *         {@code false}.
	 * @throws WikiTitleParsingException
	 */
	public boolean isDomain(Category category, DomainVocabulary vocabulary)
			throws WikiTitleParsingException {
		CHK.CHECK_NOT_NULL(category);
		boolean isDomain = false;
		String title = category.getTitle().getPlainTitle();
		Iterator<String> iterator = vocabulary.preprocess(title).iterator();
		while (iterator.hasNext() && !isDomain) {
			String term = iterator.next();
			isDomain = vocabulary.contains(term);
		}

		return isDomain;
	}

	/**
	 * Scores all the groups which compose a domain. The score of each group is
	 * calculated using {@link GroupOfCategories#getScore()}. A group of
	 * categories is defined in this case as the set of categories which are at
	 * the same distance from a root category.
	 * 
	 * @param vocabulary
	 *            The vocabulary of the domain.
	 * @param root
	 *            The category which defines the domain.
	 * @return A list with all the groups of categories which can be accessed
	 *         from the given root. All the groups has been scored and are
	 *         indexed by the distance from the root category. It means, the
	 *         position 0 of the list only contains the root category because it
	 *         is the unique category at distance 0 from itself; the position
	 *         {@code i} contains all the categories at distance {@code i} from
	 *         the root category.
	 */
	public ArrayList<GroupOfCategories> scoreDomain(
			DomainVocabulary vocabulary, Category root) {
		return scoreDomain(vocabulary, root, INFINITE_DISTANCE);
	}

	/**
	 * Scores the groups which compose a domain and are at most as far as the
	 * {@code maxDistance} parameter defines. The score of each group is
	 * calculated using {@link GroupOfCategories#getScore()}. A group of
	 * categories is defined in this case as the set of categories which are at
	 * the same distance from a root category.
	 * 
	 * @param vocabulary
	 *            The vocabulary of the domain.
	 * @param root
	 *            The category which defines the domain.
	 * @param maxDistance
	 *            Maximum distance from the root category where is allowed tro
	 *            explore.
	 * @return A list with all the groups of categories which can be accessed
	 *         from the given root and are at most at {@code maxDistance} from
	 *         the root category . All the groups has been scored and are
	 *         indexed by the distance from the root category. It means, the
	 *         position 0 of the list only contains the root category because it
	 *         is the unique category at distance 0 from itself; the position
	 *         {@code i} contains all the categories at distance {@code i} from
	 *         the root category.
	 */
	public ArrayList<GroupOfCategories> scoreDomain(
			DomainVocabulary vocabulary, Category root, int maxDistance) {

		HashSet<Integer> visitedCategories = new HashSet<Integer>();
		ArrayList<GroupOfCategories> scores = new ArrayList<GroupOfCategories>();
		LinkedBlockingQueue<ScoredCategory> pendingCategories = new LinkedBlockingQueue<ScoredCategory>();

		// Initialize structures with root category
		GroupOfCategories goc = new GroupOfCategories(root);
		ScoredCategory scat = GroupOfCategories.createScoredCategory(root,
				null, 0, true);
		goc.addScoredCategory(scat);
		scores.add(goc);

		fillPendingQueue(pendingCategories, scat, maxDistance);
		visitedCategories.add(root.getPageId());

		// Explore the queued categories
		while (!pendingCategories.isEmpty()) {
			scat = pendingCategories.poll();
			Category currentCat = scat.getCategory();
			if (!visitedCategories.contains(currentCat.getPageId())) {
				boolean domain;
				try {
					domain = isDomain(currentCat, vocabulary);
				} catch (WikiTitleParsingException e) {
					domain = false;
				}
				scat.setDomain(domain);
				int currentDistance = scat.getDistance();
				if (currentDistance < scores.size()) {
					scores.get(currentDistance).addScoredCategory(scat);
				} else {
					goc = new GroupOfCategories(root);
					goc.addScoredCategory(scat);
					scores.add(goc);
				}
				fillPendingQueue(pendingCategories, scat, maxDistance);
				visitedCategories.add(root.getPageId());

			} else {
				continue;
			}
		}
		return scores;
	}

	/**
	 * Obtains the categories of all the groups which compose a domain.
	 * 
	 * @param scoredGroups
	 *            List of {@code GroupOfCategories} to check.
	 * @param threshold
	 *            Minimum score allowed.
	 * @return Set of categories which belong to a specific domain.
	 */
	public Set<Category> getDomainCategories(
			List<GroupOfCategories> scoredGroups, double threshold) {
		HashSet<Category> domainCategories = new HashSet<Category>();
		int lastIndex = Integer.MIN_VALUE;
		for (int index = 0; index < scoredGroups.size(); index++) {
			if (scoredGroups.get(index).getScore() >= threshold) {
				lastIndex = index;
			}
		}

		for (int index = 0; index <= lastIndex; index++) {
			GroupOfCategories goc = scoredGroups.get(index);
			for (ScoredCategory scat : goc.getCategories()) {
				domainCategories.add(scat.getCategory());
			}
		}
		return domainCategories;
	}

	// Protected
	// Setters
	// Getters
	public WikipediaJwpl getWikipediaConnector() {
		return wiki;
	}

	public Locale getLocale() {
		return new Locale(wiki.getLanguage().name());
	}

	// Private

	private void fillPendingQueue(Queue<ScoredCategory> queue,
			ScoredCategory scat, int maxDistance) {
		Category cat = scat.getCategory();
		int newDistance = scat.getDistance() + 1;
		if (newDistance <= maxDistance) {
			for (Category subCat : cat.getChildren()) {
				ScoredCategory newSCat = GroupOfCategories
						.createScoredCategory(subCat, cat, newDistance);
				queue.add(newSCat);
			}
		}
	}

	private static CommandLine parseArguments(String[] args) {
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cLine = null;
		Options options = new Options();
		CommandLineParser parser = new BasicParser();

		options.addOption("l", "language", true,
				"Language of the Wikipedia to use.");
		options.addOption("y", "year", true,
				"Year of the dump of Wikipedia to use.");
		options.addOption("c", "category", true,
				"Identifier of the category to explore.");
		options.addOption("v", "vocabulary", true,
				"File with the domain vocabulary.");
		options.addOption("t", "top", true,
				"Percentage of the domain vocabulary to use.");
		options.addOption("k", "file", true,
				"File with all the categories found from the category.");
		options.addOption("s", "score", true,
				"Minimum score to accept groups of categories.");

		options.addOption("h", "help", false, "This help");
		try {
			cLine = parser.parse(options, args);
		} catch (ParseException exp) {
			logger.error("Unexpected exception:" + exp.getMessage());
		}

		if (cLine == null) {
			System.err.println("Please, set the required parameters\n");
			formatter
					.printHelp(CategoryExplorer.class.getSimpleName(), options);
			System.exit(1);
		}

		if (cLine.hasOption("h")) {
			formatter
					.printHelp(CategoryExplorer.class.getSimpleName(), options);
			System.exit(0);
		}
		boolean wiki = cLine.hasOption("l") && cLine.hasOption("y");
		boolean step1 = cLine.hasOption("c") && cLine.hasOption("t")
				&& cLine.hasOption("s");
		boolean step2 = cLine.hasOption("c") && cLine.hasOption("t")
				&& cLine.hasOption("s") && cLine.hasOption("v");
		boolean step3 = cLine.hasOption("k") && cLine.hasOption("s");
		if (!(wiki && (step1 || step2 || step3))) {
			System.err.println("Please, set the required parameters\n");
			formatter
					.printHelp(CategoryExplorer.class.getSimpleName(), options);
			System.exit(1);
		}

		return cLine;
	}

	public static void main(String args[]) throws IOException {
		String VOC_OUT_FILE = "%s_%s_%s.vocabulary";
		CommandLine cLine = parseArguments(args);
		Locale language = new Locale(cLine.getOptionValue("l"));
		int year = Integer.valueOf(cLine.getOptionValue("y"));
		CategoryExplorer explorer = new CategoryExplorer(language, year);
		Category cat = null;
		DomainVocabulary vocabulary = new DomainVocabulary(explorer.getLocale());
		ArrayList<GroupOfCategories> groupsList = null;
		int step = 1;
		if (cLine.hasOption("c") && cLine.hasOption("t")
				&& cLine.hasOption("s") && cLine.hasOption("v")) {
			step = 2;
			String category = cLine.getOptionValue("c");
			cat = explorer.loadCategory(Integer.parseInt(category));
			File input = new File(cLine.getOptionValue("v"));
			vocabulary.insertFromFile(input);
		}
		if (cLine.hasOption("k") && cLine.hasOption("s")) {
			step = 3;
			// TODO init groupsList with k file
		}

		switch (step) {
		case 1:
			// Step 1: Create domain vocabulary
			logger.info("Step 1.");
			String category = cLine.getOptionValue("c");
			cat = explorer.loadCategory(Integer.parseInt(category));
			if (cat == null) {
				//TODO
			}
			try {
				vocabulary = explorer.createCategoryVocabulary(cat);
			} catch (WikiApiException e) {
				logger.errorEnd("Exception during Wikipedia DB access:"
						+ e.getLocalizedMessage());
				e.printStackTrace();
			}
			String filename = String.format(VOC_OUT_FILE, category, language,
					year);
			File vocabularyFile = new File(System.getProperty("user.dir"),
					filename);
			vocabulary.toFile(vocabularyFile);
		case 2:
			// Step 2: Explore Wikipedia categories with the most frequent terms
			// of the vocabulary
			logger.info("Step 2.");
			String top = cLine.getOptionValue("t");
			vocabulary = vocabulary.getTop(Float.parseFloat(top));
			groupsList = explorer.scoreDomain(vocabulary, cat);
			// TODO Save groups
		case 3:
			// Extract the categories which belong to the domain
			logger.info("Step 3.");
			String score = cLine.getOptionValue("s");
			HashSet<Category> domain = (HashSet<Category>) explorer
					.getDomainCategories(groupsList, Double.parseDouble(score));
			for (Category domainCat : domain) {
				try {
					System.out.println(domainCat.getPageId() +"\t"+
									domainCat.getTitle().getWikiStyleTitle());
				} catch (WikiTitleParsingException e) {
					logger.error("The category has no title assigned"
							+ e.getLocalizedMessage());
					e.printStackTrace();			
				
				}
			}
		}
		logger.info("End of the process.");
	}
}
