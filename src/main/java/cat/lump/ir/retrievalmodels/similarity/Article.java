package cat.lump.ir.retrievalmodels.similarity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import cat.lump.aq.basics.structure.InvIndexContainer;
import cat.lump.ir.retrievalmodels.document.Document;
import cat.lump.ir.retrievalmodels.document.RepresentationType;
//import cat.talp.lump.ir.structures.InvIndexContainer;

/**
 * An article object is the abstraction of a document to translate whith its
 * related information.</br>
 * The related information is:</br>
 * <ul>
 * <li>Language in which the article is written.</li>
 * <li>Type of data representation that must be used.</li>
 * </ul>
 * 
 * Moreover, it provides functions to represent its data and calculate the
 * magnitudes to use later by the similarity calculator.
 * 
 * @author jboldoba
 * 
 * @see cat.lump.ir.retrievalmodels.document.RepresentationType
 */
public class Article
{
	/** Text of the article */
	private String[] text;
	/** Type of representation to perform */
	private RepresentationType type;
	/** Language of the article */
	private Locale language;
	/** Representation of the article */
	private Document representation;
	/** Inverted index used to compute similarities */
	private InvIndexContainer invIndex;
	/** Map with the article's text magnitudes */
	protected Map<Integer, Double> text_magnitudes = 
					new HashMap<Integer, Double>();

	/**
	 * Creates an undefined article together with its language and type of
	 * representation.
	 * 
	 * @param language
	 *            Language of the article
	 * @param type
	 *            Type of representation
	 */
	public Article(String language, RepresentationType type)
	{
		this("", language, type);
	}

	/**
	 * Creates an article with text and language.
	 * 
	 * @param text
	 *            Text of the article
	 * @param language
	 *            Language of the article
	 */
	public Article(String text, String language)
	{
		this(text, language, RepresentationType.NONE);
	}

	/**
	 * Creates an article with text, language and type of representation.
	 * 
	 * @param text
	 *            Text of the article
	 * @param language
	 *            Language of the article
	 * @param type
	 *            Type of representation
	 */
	public Article(String text, String language, RepresentationType type)
	{
		this.text = text.split("\n");
		this.language = new Locale(language);
		this.type = type;
		representation = null;
		invIndex = null;
	}

	/**
	 * Represents the article text according to the type of representation
	 * assigned.
	 */
	public void representText(int nGrams)
	{
		switch (type)
		{
			case BOW:
				representation = new Document(text, language, true, false,
						false,false);
				break;
			case CNG:
				representation = new Document(text, language, false, true,
						false,false, nGrams);
				break;
			case COG:
				representation = new Document(text, language, false, false, 
						false, true);
				break;
			case WNG:
				representation = new Document(text, language, false, false,
						true,false, nGrams);
				break;
			default:
				representation = new Document(text, language);
				break;
		}
	}

	/**
	 * Loads the inverted index from the text as well as the corresponding 
	 * magnitudes
	 */
	public void generateInvIndex()
	{
		invIndex = new InvIndexContainer();
		double this_magnitude;

		for (int i = 0; i < representation.length(); i++)
		{
			this_magnitude = 0.0;
			Map<String, Double> ng = representation.getFragment(i).getWeighted(
					type);
			for (String tok : ng.keySet())
			{
				if (tok.equals(""))
					continue;
				this_magnitude += Math.pow(ng.get(tok), 2);
				if (!invIndex.tBox.containsKey(tok))
					invIndex.tBox.put(tok, new HashMap<String, Double>());
				invIndex.tBox.get(tok).put(String.valueOf(i), (Double) ng.get(tok));
			}
			text_magnitudes.put(i, Math.sqrt(this_magnitude));
		}
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text)
	{
		this.text = text.split("\n");
	}

	/**
	 * @param language
	 *            the language to set
	 */
	public void setLanguage(Locale language)
	{
		this.language = language;
	}

	/**
	 * @param representation
	 *            the type of data representation to set
	 */
	public void setRepresentation(Document representation)
	{
		this.representation = representation;
	}

	/**
	 * @param invIndex
	 *            the inverted index container to set
	 */
	public void setInvIndex(InvIndexContainer invIndex)
	{
		this.invIndex = invIndex;
	}

	/**
	 * @return the text
	 */
	public String[] getText()
	{
		return text;
	}

	/**
	 * @return the language
	 */
	public Locale getLanguage()
	{
		return language;
	}

	/**
	 * @return the representation
	 */
	public Document getRepresentation()
	{
		return representation;
	}

	public int getFragmentSize(int a)
	{
		return representation.getFragment(a).size(type);
	}

	/**
	 * @return the inverted index container
	 */
	public InvIndexContainer getInvIndex()
	{
		return invIndex;
	}

	/**
	 * @return The number of fragments in the document.
	 */
	public int length()
	{
		return representation.length();
	}

	/**
	 * @return the vocabulary in this article
	 */
	public Set<String> getVocabulary()
	{
		return invIndex.tBox.keySet();
	}

	/**
	 * @param token
	 * @return The values related to the given token
	 */
	public Map<String, Double> getTokenValues(String token)
	{
		return invIndex.tBox.get(token);
	}

	/**
	 * The magnitude of the given sentence
	 * 
	 * @param index
	 * @return the magnitude of a sentence
	 */
	public Double getMagnitude(int index)
	{
		return text_magnitudes.get(index);
	}
}
