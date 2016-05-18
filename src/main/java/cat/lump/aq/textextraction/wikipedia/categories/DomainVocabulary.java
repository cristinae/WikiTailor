package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.CsvFoolReader;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.structure.ir.TermFrequencyTuple;
import cat.lump.aq.textextraction.wikipedia.prepro.TermExtractor;

/**
 * <p>
 * A {@code DomainVocabulary} instance is used to store a set of terms with its
 * frequency. It means, each entry contains the number of times that has been
 * added to the dictionary (its frequency).
 * </p>
 * <p>
 * It is able to insert terms from a text as well as a collection of strings.
 * Before adding new terms they are preprocessed as follows:
 * <ol>
 * <li>Case folding the input.</li>
 * <li>Remove stop words.</li>
 * <li>Remove diacritics.</li>
 * <li>Apply a stemming process.</li>
 * <li>Filter only alphabetic tokens with a size greater than or equal to a
 * given value (By default, {@value #DEF_MIN_SIZE} characters).
 * </ol>
 * After this preprocess, the unknown terms are added to the dictionary with a
 * frequency of 1, but the previously contained are modified by incrementing its
 * frequency by 1.
 * 
 * The preprocess behavior can be easily changed extending this class and
 * redefining the {@link #preprocess(String)} function.
 * </p>
 * <p>
 * It also provides functions to get its content in other ways. On the one hand,
 * it is able to save them in a file (binary or textual) with the
 * {@link #serialize(File)} and {@link #toFile(File)} functions respectively. On
 * the other hand, it can be transformed into a list of
 * {@code TermFrequencyTuple} using the {@link #toList()} function.
 * </p>
 * 
 * @author cmops
 * @see cat.lump.aq.basics.structure.ir.TermFrequencyTuple
 * 
 */
public class DomainVocabulary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9179280392052843538L;
	// Constants
	/** Default size to accept tokens in the vocabulary. */
	private static final int DEF_MIN_SIZE = 4;
	/** Field separator used when the vocabulary is written in a file */
	private static final String TAB = "\t";

	// Attributes
	/** Map which contains the terms with its frequency. */
	private HashMap<String, Integer> terms;
	/** Language of the vocabulary */
	private final Locale lang;
	/** Minimum size of a term to be accepted in the vocabulary. */
	private int minimumSize;

	// Constructors
	/**
	 * Creates an empty vocabulary.
	 * 
	 * @param language
	 *            Language of the vocabulry.
	 */
	public DomainVocabulary(Locale language) {
		CHK.CHECK_NOT_NULL(language);
		terms = new HashMap<String, Integer>();
		lang = language;
		minimumSize = DEF_MIN_SIZE;
	}

	/**
	 * Creates a vocabulary which include some initial terms with an initial
	 * frequency.
	 * 
	 * @param language
	 *            Language of the vocabulary.
	 * @param terms
	 *            Initial terms to include in the vocabulary.
	 */
	public DomainVocabulary(Locale language,
			Collection<TermFrequencyTuple> terms) {
		this(language);
		setTerms(terms);
	}

	/**
	 * Creates a new vocabulary identical to the given one. It means, it copies
	 * the given vocabulary.
	 * 
	 * @param vocabulary
	 *            The reference vocabulary
	 */
	public DomainVocabulary(DomainVocabulary vocabulary) {
		lang = vocabulary.lang;
		minimumSize = vocabulary.minimumSize;
		terms = vocabulary.terms;
	}

	// Public
	/**
	 * Add new terms from a collection of words. Only will be accepted the not
	 * empty tokens after preprocess them.
	 * 
	 * @param terms
	 *            Collection of terms to add.
	 */
	public void addTerms(Collection<String> terms) {
		String text = StringUtils.join(terms, " ");
		addTerms(text);
	}

	/**
	 * Add new terms or modify the already added with the tokens obtained by the
	 * preprocessing of the given text.
	 * 
	 * @param text
	 *            The text with the new terms.
	 */
	public void addTerms(String text) {
		CHK.CHECK_NOT_NULL(text);
		for (String term : preprocess(text)) {
			if (contains(term)) {
				int freq = terms.get(term);
				terms.put(term, freq + 1);
			} else {
				// New entry.
				terms.put(term, 1);
			}
		}
	}

	/**
	 * Checks if a term is contained in the vocabulary.
	 * 
	 * @param term
	 *            The term to check.
	 * @return {@code true} if the term is contained in the vocabulary.
	 *         {@code false} otherwise.
	 */
	public boolean contains(String term) {
		CHK.CHECK_NOT_NULL(term);
		return terms.containsKey(term);
	}

	/**
	 * Gives the frequency of the given term.
	 * 
	 * @param term
	 *            The term to check its frequency.
	 * @return The absolute frequency of the term. If the term is not contained
	 *         in the vocabulary, it returns 0.
	 */
	public int getFrequency(String term) {
		CHK.CHECK_NOT_NULL(term);
		int frequency = 0;
		if (contains(term)) {
			frequency = terms.get(term);
		}
		return frequency;
	}

	/**
	 * Extracts the most frequent terms of the vocabulary.
	 * 
	 * @param percentage
	 *            The percentage which defines the top to extract. The
	 *            percentage is given by a value between 0 and 1.
	 * @return A new {@code DomainVocabulary} instance which contains the
	 *         extracted terms.
	 */
	public DomainVocabulary getTop(float percentage) {
		DomainVocabulary topVocabulary;
		if (Float.compare(percentage, 1.0f) == 0) {
			topVocabulary = new DomainVocabulary(this);
		} else {
			int elemToExtract = (int) ((float) terms.size() * percentage);
			ArrayList<TermFrequencyTuple> sortedList = (ArrayList<TermFrequencyTuple>) getSortedList();
			topVocabulary = new DomainVocabulary(lang, sortedList.subList(0,
					elemToExtract));
		}

		return topVocabulary;
	}

	/**
	 * Extract the {@code qtt} most frequent terms of the vocabulary.
	 * 
	 * @param qtt
	 *            Number of terms to extract
	 * @return A new {@code DomainVocabulary} instance which contains the
	 *         extracted terms.
	 */
	public DomainVocabulary getTop(int qtt) {
		DomainVocabulary topVocabulary;
		if (qtt >= terms.size()) {
			topVocabulary = new DomainVocabulary(this);
		} else {
			ArrayList<TermFrequencyTuple> sortedList = (ArrayList<TermFrequencyTuple>) getSortedList();
			topVocabulary = new DomainVocabulary(lang, sortedList.subList(0,
					qtt));
		}

		return topVocabulary;
	}

	/**
	 * Exports the vocabulary as a list of tuples which contains the term and
	 * its frequency. This list has not any order.
	 * 
	 * @return A list of tuples with the contents of the vocabulary.
	 * @see cat.lump.aq.basics.structure.ir.TermFrequencyTuple
	 */
	public List<TermFrequencyTuple> toList() {
		ArrayList<TermFrequencyTuple> list = new ArrayList<TermFrequencyTuple>();
		for (Entry<String, Integer> entry : terms.entrySet()) {
			TermFrequencyTuple tft = new TermFrequencyTuple(entry.getKey(),
					entry.getValue());
			list.add(tft);
		}
		return list;
	}

	/**
	 * Exports the vocabulary to a textual file. This file is composed by
	 * {@code N} lines where each one contains one vocabulary entry. The
	 * {@code DomainVocabulary} instances saves in this way can be loaded using
	 * the {@link #insertFromFile(File)} function. Each line has the following
	 * format: "{@code term\tfrequency}".
	 * 
	 * @param output
	 *            Path to the output file. If it doesn't exists, it will be
	 *            created. If it already exists, it will be overwritten.
	 * @throws IOException
	 */
	public void toFile(File output) throws IOException {
		CHK.CHECK_NOT_NULL(output);
		String lineTemplate = "%s" + TAB + "%d\n";
		FileWriter fw = new FileWriter(output);
		BufferedWriter bw = new BufferedWriter(fw);
		for (Entry<String, Integer> entry : terms.entrySet()) {
			bw.append(String.format(lineTemplate, entry.getKey(),
					entry.getValue()));
		}
		bw.close();
		fw.close();
	}

	/**
	 * Saves the vocabulary in a binary file. The {@code DomainVocabulary}
	 * instances saves in this way can be loaded using the
	 * {@link #loadfromFile(File)} function.
	 * 
	 * @param output
	 *            Path to the output file. If it doesn't exists, it will be
	 *            created. If it already exists, it will be overwritten.
	 */
	public void serialize(File output) {
		CHK.CHECK_NOT_NULL(output);
		FileIO.writeObject(this, output);
	}

	/**
	 * Creates a {@code DomainVocabulary} instance by reading a binary file
	 * which contains a domain vocabulary.
	 * 
	 * @param input
	 *            Path to the binary file which contains a domain vocabulary.
	 * @return A {@code DomainVocabulary} instance which is equal to the one
	 *         stored in the {@code input} file.
	 */
	public static DomainVocabulary loadfromFile(File input) {
		CHK.CHECK_NOT_NULL(input);
		return (DomainVocabulary) FileIO.readObject(input);
	}

	/**
	 * Inserts the terms stored in the given file into the domain vocabulary.
	 * The unkown terms of the file are added to the current vocabulary and the
	 * previously added terms are modified incrementing its frequency by the
	 * frequency stored in the file.
	 * 
	 * @param input
	 *            Path to the readable file which contains a set of terms stored
	 *            with the format used by the {@link #toFile(File)} function.
	 */
	public void insertFromFile(File input) {
		CHK.CHECK_NOT_NULL(input);
		String[][] content = CsvFoolReader.csv2matrix(input, TAB);
		for (String[] tuple : content) {
			String term = tuple[0];
			int freq = Integer.parseInt(tuple[1]);
			if (contains(term)) {
				int oldFreq = terms.get(term);
				terms.put(term, oldFreq + freq);
			} else {
				terms.put(term, freq);
			}
		}
	}

	// Protected
	/**
	 * Preprocess a text to extract its terms as defined in TermExtractor.getTerms();
	 * 
	 * @param text
	 *            The text to preprocess.
	 * @return The collection of tokens accepted from the given text.
	 */
	protected Collection<String> preprocess(String text) {
		CHK.CHECK_NOT_NULL(text);
		TermExtractor te = new TermExtractor(lang);
		return te.getTerms(text, minimumSize);
	}

	// Setters
	/**
	 * Changes the minimum size of the tokens to be accepted as terms of the
	 * vocabulary.
	 * 
	 * @param size
	 *            The new size.
	 */
	public void setMinimumSize(int size) {
		CHK.CHECK(size >= 0, "No negative values are accepted");
		minimumSize = size;
	}

	/**
	 * Sets a collection of tuples formed by a term and its frequency as
	 * vocabulary. This call implies the purging of the current vocabulry.
	 * 
	 * @param terms
	 *            The new terms of the vocabulary.
	 */
	public void setTerms(Collection<TermFrequencyTuple> terms) {
		CHK.CHECK_NOT_NULL(terms);
		for (TermFrequencyTuple tuple : terms) {
			this.terms.put(tuple.getTerm(), tuple.getFrequency());
		}
	}

	// Getters
	/**
	 * @return The locale that defines the language used by this vocabulary.
	 */
	public Locale getLanguage() {
		return lang;
	}

	/**
	 * @return The minimum size of the terms after preprocess them to be
	 *         accepted as new term.
	 */
	public int getMinimumSize() {
		return minimumSize;
	}

	/**
	 * @return The terms and its frequency. Each key of the map is a term of the
	 *         vocabulary and the value related to the key is the frequency of
	 *         that term.
	 */
	public Map<String, Integer> getTerms() {
		return terms;
	}

	// Private
	/**
	 * Sorts on descending order a exported list of the vocabulary.
	 * 
	 * @return The terms and its frequency sorted in descending order according
	 *         to their frequency.
	 */
	private List<TermFrequencyTuple> getSortedList() {
		ArrayList<TermFrequencyTuple> list = (ArrayList<TermFrequencyTuple>) toList();
		Collections.sort(list, Collections.reverseOrder());
		return list;
	}
	
	//TODO main: idioma, año y categoría  --> file 
	
}
