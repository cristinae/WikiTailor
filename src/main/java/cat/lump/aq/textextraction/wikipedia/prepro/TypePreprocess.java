package cat.lump.aq.textextraction.wikipedia.prepro;

/**
 * Enumeration of the different types of preprocess.
 * 
 * @author jboldoba
 */
public enum TypePreprocess
{
	PLAIN_TEXT("plain"),
	TFS("tfs"),
	TRANSLATED("translated"),
	WIKI_TEXT("wiki");

	/** Friendly identifier */
	private String friendlyName;

	/**
	 * Creates a new entry with a friendly name defined by {@code identifier}
	 * 
	 * @param identifier
	 *            Identifier of the entry.
	 */
	private TypePreprocess(String identifier)
	{
		friendlyName = identifier;
	}

	/**
	 * @return The identifier of the entry.
	 */
	public String getIdentifier()
	{
		return friendlyName;
	}

	/**
	 * Searches the entry identified by the given name.
	 * 
	 * @param name
	 *            The identifier to search.
	 * @return The type related to the given identifier. If any entry is
	 *         identified by the given parameter, {@code null} is returned.
	 */
	public static TypePreprocess getByTag(String name)
	{
		TypePreprocess type = null;
		TypePreprocess[] entrySet = TypePreprocess.values();
		for (TypePreprocess entry : entrySet)
		{
			if (entry.friendlyName.equals(name))
			{
				type = entry;
			}
		}
		return type;
	}

	public String toString()
	{
		return friendlyName;
	}
}
