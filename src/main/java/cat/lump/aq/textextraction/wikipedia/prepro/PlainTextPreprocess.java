package cat.lump.aq.textextraction.wikipedia.prepro;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.wikilink.jwpl.LanguageConstants;
import cat.lump.ie.textprocessing.Decomposition;
import cat.lump.ie.textprocessing.TextPreprocessor;
import cat.lump.ie.textprocessing.sentence.SentencesOpennlp;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.parser.Paragraph;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Section;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;

/**
 * Preprocess a Wikipedia page using JWPL API. </br> Extracts the text of the
 * page except the tables and some special sections. That text is split into
 * sentences.
 * 
 * @author jboldoba
 * 
 */
public class PlainTextPreprocess extends AbstractPreprocess {

	private Decomposition detector;
	private MediaWikiParser parser;
	private final HashSet<String> specialSections;
	
	private final String HTML_PATTERN = "(<ref[^(/>)]*/>)|(<ref[^>]*>(.*?)</ref>)";
//	private final String SPACE_PATTERN = "\\s+";
//	
//	
//	// Single quotes to normalise
//	private final static Pattern r_quote1_norm = Pattern.compile("([`‘’´])");
//	private final static String s_quote1_norm = "'";
//	// Double quotes to normalise
//	private final static Pattern r_quote2_norm = Pattern.compile("([“”„«»]|'')");
//	private final static String s_quote2_norm = "\"";
//	// Dashes to normalise
//	private final static Pattern r_dash_norm = Pattern.compile("(--)");
//	private final static String s_dash_norm = "-";
//
//	// MDots to normalise
//	private final static Pattern r_mdot_norm = Pattern.compile("(…|\\.\\.)");
//	private final static String s_mdot_norm = "...";

	private static LumpLogger logger = 
			new LumpLogger (PlainTextPreprocess.class.getSimpleName());

	
	public PlainTextPreprocess(TypePreprocess type, String name,
			Locale language, int year, File rootDirectory)
			throws WikiApiException {
		super(type, name, language, year, rootDirectory);
		
		detector = new SentencesOpennlp(language);
//		detector = new SentencesOpennlp(Locale.ENGLISH);
		parser = wiki.getParser();
		specialSections = new HashSet<String>();
		Language lang = wiki.getLanguage();
		LanguageConstants constants = new LanguageConstants();
		
		specialSections.addAll(Arrays.asList(constants.getSeeAlsoLabel(lang)));
		specialSections.addAll(Arrays.asList(constants.getFurhterReadingLabel(lang)));
		specialSections.addAll(Arrays.asList(constants.getExternalLinksLabel(lang)));
		specialSections.addAll(Arrays.asList(constants.getNotesLabel(lang)));
		specialSections.addAll(Arrays.asList(constants.getReferencesLabel(lang)));
		specialSections.addAll(Arrays.asList(constants.getBibliographyLabel(lang)));
		// added because it could be either categories or category (and the
		// equivalent in other languages. TODO whether we should apply some
		// lemmatization or other preprocessing to better handling this.
		specialSections.addAll(Arrays.asList(constants.getCategoryLabel(lang)));
	}

	@Override
	public ArrayList<String> preprocess(int id) throws WikiApiException {
		ArrayList<String> result = new ArrayList<String>();
		try {
			Page page = wiki.getPage(id);

			String title = page.getTitle().getPlainTitle();
			result.add(TextPreprocessor.normalizeText(title));

			String markupText = removeHTML(page.getText());
			ParsedPage parsedPage = parser.parse(markupText);

			for (Section section : parsedPage.getSections()) {
				preprocessSection(section, result);
			}

		} catch (Exception e) {
			logger.error(String.format("Exception ID %d: %s", id,
					e.getLocalizedMessage()));
		}
		return result;
	}

	/**
	 * Preprocess a single section from a parsed page
	 * 
	 * @param section
	 *            The section
	 * @param result
	 *            The list wherein the preprocessed sentences are stored
	 */
	private void preprocessSection(Section section, List<String> result) {
		if (section != null) {
			String secTitle = section.getTitle();
			if (! isSpecialSection(secTitle)) {
				if (secTitle != null) {
					result.add(TextPreprocessor.normalizeText(secTitle));
				}
				
				for (Paragraph par : section.getParagraphs()) {
					preprocessParagraph(par, result);
				}
//				List<NestedListContainer> lists = section.getNestedLists();
//				for (NestedListContainer item : lists)
//				{
//					preprocessNestedList(item.getNestedLists(), result);
//				}
			}
		}
	}
	
	private boolean isSpecialSection(String secTitle)
	{
		boolean is = false;
		if (secTitle != null &&
			specialSections.contains(TextPreprocessor.normalizeText(secTitle.toLowerCase())))
		{
				is = true;					
		}
		return is;
	}

	/**
	 * Preprocess a single paragraph from a parsed page.
	 * 
	 * @param par
	 *            The paragraph
	 * @param result
	 *            The list wherein the preprocessed sentences are stored
	 */
	private void preprocessParagraph(Paragraph par, List<String> result) {
		String text = TextPreprocessor.normalizeText(par.getText().trim());
		for (String sentence : detector.getStrings(text)) {
			result.add(sentence);
		}
	}

//	/**
//	 * Preprocess a nested list or lists and add the results to the
//	 * preprocessing result list.
//	 * 
//	 * @param list
//	 *            Lists to preprocess
//	 * @param result
//	 *            Preprocessing list
//	 */
//	private void preprocessNestedList(List<NestedList> list, List<String> result)
//	{
//		if (list.size() > 0)
//		{
//			for (NestedList item : list)
//			{
//				if (item instanceof NestedListContainer)
//				{
//					NestedListContainer container = (NestedListContainer) item;
//					preprocessNestedList(container.getNestedLists(), result);
//				}
//				else if (item instanceof NestedListElement)
//				{
//					NestedListElement listElem = (NestedListElement) item;
//					String text = normalizeText(listElem.getText());
//					for (String sentence : detector.getStrings(text))
//					{
//						result.add(normalizeText(sentence));
//					}
//				}
//			}
//		}
//	}

//	/**
//	 * Normalize the text by shrinking white spaces in one.
//	 * 
//	 * @param text
//	 * @return
//	 */
//	private String normalizeText(String text) {
//		text = text.replaceAll(SPACE_PATTERN, " ").trim();
//		text = r_quote1_norm.matcher(text).replaceAll(s_quote1_norm);
//		text = r_quote2_norm.matcher(text).replaceAll(s_quote2_norm);
//		text = r_dash_norm.matcher(text).replaceAll(s_dash_norm);
//		text = r_mdot_norm.matcher(text).replaceAll(s_mdot_norm);
//		return text;
//	}

	/**
	 * Removes all the HTML from the given text.
	 * 
	 * @param text
	 *            Text with HTML
	 * @return Text without HTML
	 */
	private String removeHTML(String text) {
		// Remove references
		Pattern pattern = Pattern.compile(
				HTML_PATTERN, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(text);
		text = matcher.replaceAll(" ");

		return StringEscapeUtils.unescapeHtml4(text);
	}

}
