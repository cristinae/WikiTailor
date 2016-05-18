package cat.lump.ir.retrievalmodels.similarity;

import cat.lump.ir.comparison.Model;
import cat.lump.ir.retrievalmodels.document.RepresentationType;

/**
 * This enumeration contains the similarity models and characterizations 
 * available.
 * </br>
 * 
 * Each element has the type of representation of the data that it needs and the
 * similarity model it uses. Both can be {@code null} if they are not required.
 * </br>
 * 
 * Moreover, they have a friendly name that can be used to identify them.
 * 
 * @author jboldoba
 * 
 */
public enum Similarity
{
	CHAR_N_GRAMS(RepresentationType.CNG, Model.COSINE, "cng"), 
	LENGTH_FACTOR(RepresentationType.NONE, Model.NONE, "len"), 
	MONOLINGUAL_BOW(RepresentationType.BOW, Model.COSINE, "mono"), 
	PSEUDOCOGNATES(RepresentationType.COG, Model.COSINE, "cog"), 
	WIKILINKS(RepresentationType.WLK, Model.JACCARD, "wiki");

	/** Type of data representation */
	private final RepresentationType representation;
	/** Model of similarity */
	private final Model model;
	/** Friendly identifier */
	private final String friendlyName;

	/**
	 * Constructor.
	 * 
	 * @param repr
	 *            Type of data representation
	 * @param model
	 *            Model of similarity
	 * @param name
	 *            Friendly identifier
	 */
	private Similarity(RepresentationType repr, Model model, String name)
	{
		representation = repr;
		this.model = model;
		this.friendlyName = name;
	}

	/**
	 * @return the type of data representation needed by the element
	 */
	public RepresentationType getRepresentation()
	{
		return representation;
	}

	/**
	 * @return the model of similarity used by the element
	 */
	public Model getModel()
	{
		return model;
	}

	/**
	 * @return the friendly identifier of the element
	 */
	public String getName()
	{
		return friendlyName;
	}

	/**
	 * @param name
	 *            Name which identifies the similarity that we want 
	 *            (cng, cog, len, mono).
	 * @return The similarity entry related to the given name. If no value has
	 *         this name, {@¢ode null} is returned.
	 */
	public static Similarity getSimilarityByName(String name)
	{
		Similarity sim = null;
		int index = 0;
		Similarity[] values = values();
		while (sim == null && index < values.length)
		{
			if (values[index].getName().equals(name))
			{
				sim = values[index];
			}
			index++;
		}
		return sim;
	}
	
	/**
	 * TODO ask Josu about this
	 * @param name
	 *            Name which identifies the similarity that we want 
	 *            (cng, cog, len, mono).
	 * @return The similarity entry related to the given name. If no value has
	 *         this name, {@¢ode null} is returned.
	 */
	public static Similarity getSimilarityByRepr(RepresentationType repr)
	{
		Similarity sim = null;
		int index = 0;
		Similarity[] values = values();
		while (sim == null && index < values.length)
		{
			if (values[index].getModel().equals(repr))
			{
				sim = values[index];
			}
			index++;
		}
		return sim;
	}
	
}
