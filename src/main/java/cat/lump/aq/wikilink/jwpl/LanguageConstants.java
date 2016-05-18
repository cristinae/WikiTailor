package cat.lump.aq.wikilink.jwpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import joptsimple.internal.Strings;
import cat.lump.aq.basics.check.CHK;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;

/**
 * Includes the constant identifiers for Wikipedia labels in different languages.
 * <br/> 
 * Many of the labels come from 
 * http://en.wikipedia.org/wiki/Wikipedia:Manual_of_Style/Layout and 
 * the corresponding articles in other languages
 * 
 * @author alberto
 * @version 0.2
 * @since Feb 26 2014
 */
public class LanguageConstants {

	private Properties properties;
		
	/**Most constants are read from this file, located in this same package */
	private final String CONFIG_FILE="languageConstants.properties";
	
	/** Constructor */
	
	/**
	 * Opens the CONFIG_FILE and loads all the language constants for the 
	 * available languages.
	 */
	public LanguageConstants(){
		properties = new Properties();
		try {							
			InputStreamReader isr = 
				new InputStreamReader(getClass().getResourceAsStream(CONFIG_FILE), 
						"UTF8");				
			properties.load(isr);
			isr.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(properties.get("arabic_redirect"));
		//System.out.println(properties.get("basque_redirect"));
	}	
	
	/** Getters and setters */
	
	/**
	 * @param language
	 * @return image label(s) for the required language
	 */
	public String[] getImageLabel(Language language){
		return getValuesForLabel(language, "image");
	}
	
	/**
	 * @param language
	 * @return category label for the required language
	 */
	public String[] getCategoryLabel(Language language){
		return getValuesForLabel(language, "category");
	}
	
	/**
	 * @param language
	 * @return Section "See also" label for the required language
	 */
	public String[] getSeeAlsoLabel(Language language){
		return getValuesForLabel(language, "see_also");
	}
	
	/**
	 * @param language
	 * @return Section "References" label for the required language
	 */
	public String[] getReferencesLabel(Language language){
		return getValuesForLabel(language, "references");
	}

	/**
	 * @param language
	 * @return Section "Notes" label for the required language
	 */
	public String[] getNotesLabel(Language language){
		return getValuesForLabel(language, "notes");
	}

	/**
	 * @param language
	 * @return Section "Bibliography" label for the required language
	 */
	public String[] getBibliographyLabel(Language language){
		return getValuesForLabel(language, "bibliography");
	}

	/**
	 * @param language
	 * @return Section "Further reading" label for the required language
	 */
	public String[] getFurhterReadingLabel(Language language){
		return getValuesForLabel(language, "further_reading");
	}

	/**
	 * @param language
	 * @return Section "External Links" label for the required language
	 */
	public String[] getExternalLinksLabel(Language language){
		return getValuesForLabel(language, "external_links");
	}

	/**
	 * @param language
	 * @return Redirect label for the required language
	 */
	public String[] getRedirectLabel(Language language){
		return getValuesForLabel(language, "redirect");
	}

	/**
	 * @param language
	 * @return Disambiguation label for the required language
	 */
	public String[] getDisambiguationLabel(Language language){
		return getValuesForLabel(language, "disambiguation");
	}

	/**
	 * @param language
	 * @return Acronym label for the required language (only seen for Catalan) 
	 */
	public String[] getAcronymLabel(Language language){
		// TODO remove this label included now in Disambiguation 
		return getValuesForLabel(language, "acronym");
	}
	
	
	/** Privates **/
	
	private String[] getValuesForLabel(Language language, String label)
	{	
		String rawLabel;
		String[] labels;
		
		String key = Strings.join(new String[] {language.name(), label}, "_");
		
		CHK.CHECK(properties.containsKey(key), 
				"The label is not available for this language ");

		rawLabel = properties.getProperty(key)
							.toLowerCase()
							.trim();	//Allows for the comparison...
		
		labels = rawLabel.contains(",,,") 
					? labels = rawLabel.split(",,,")
					: new String[] {rawLabel};		
		
		for (int i = 0 ; i < labels.length ; i++)
			labels[i] = labels[i].trim();
		
		return labels;
	}
	
//	private String[] getMultiLabel(String str)
//	{
//		
//		String[] labels = str.split(",,,");
//		if (labels.length == 0){// && labels[0].matches("\\s+")){			
//			CHK.CHECK(false, "The label is not available for this language ");
//		}
//		return labels;	
//	}
	
	/**
	 * The label is returned <b>lowercased</b> in order to allow for the comparison
	 * against other strings
	 * 
	 * @param language
	 * @param label 
	 * @return The value for the requested label as in the languageConstants file
	 */
//	private String getLabel(Language language, String label)
//	{
//		String key = Strings.join(new String[] {language.name(), label}, "_");
//		if (! properties.containsKey(key))
//		{
//			CHK.CHECK(false, "The label is not available for this language ");
//		}
//		
//		String lab = properties.getProperty(key);
//		if (lab.matches("\\s*")){
//			CHK.CHECK(false, "The label is not available for this language ");
//		}		
//		return properties.getProperty(key).toLowerCase();	
//	}

	/** Labels for the images */
	@Deprecated
	private static final Map<Language, String[]> IMAGE_LABELS = 
			new TreeMap<Language, String[]>() {/** */
		private static final long serialVersionUID = 5692558781536955128L;
	{
		put(Language.arabic,     new String[] {"ملف"});
		put(Language.basque,     new String[] {"Fitxategi"});	
		put(Language.catalan,    new String[] {"Fitxer"});		
		put(Language.croatian,   new String[] {"Datoteka"});
		put(Language.english,    new String[] {"File", "Image"});//app. has both
		put(Language.estonian,   new String[] {"Pilt"});
		put(Language.french,     new String[] {"Fichier", "Image"});
		put(Language.german,     new String[] {"Image"});
		put(Language.greek,  	 new String[] {"Αρχείο"});		
		put(Language.hindi,      new String[] {"चित्र"});
		put(Language.italian,    new String[] {"Immagine", "File"});
		put(Language.latvian,    new String[] {"Attēls"});
		put(Language.lithuanian, new String[] {"Vaizdas"});
		put(Language.romanian,   new String[] {"Fişier"});
		put(Language.russian,	 new String[] {"Image"});	
		put(Language.slovenian,  new String[] {"Slika"});	
		put(Language.spanish,	 new String[]{"Archivo", "Imagen"});//app. has both
	}};
	
	@Deprecated
	private static final Map<Language, String> CATEGORY_LABELS = 
			new TreeMap<Language, String>() {/** */
		private static final long serialVersionUID = -5734960770894777199L;
	{
		put(Language.arabic,     "تصنيف"); 		
		put(Language.basque,     "Kategoria"); 
		put(Language.catalan,    "Categoria");
		put(Language.croatian,   "Kategorija");		
		put(Language.english,	 "Category");
		put(Language.estonian,   "Kategooria");
		put(Language.french,	 "Catégories");
		put(Language.german,	 "Kategorie");
		put(Language.greek,	     "Κατηγορία");
//		put(Language.hindi,		 "");		
		put(Language.italian,	 "Categorie");
		put(Language.latvian,	 "Kategorija");
		put(Language.lithuanian, "Kategorija");	
		put(Language.romanian,   "Categorie");
		put(Language.russian,    "Всё");
		put(Language.slovenian,  "Kategorije");		
		put(Language.spanish,	 "Categoría");		
	}};
	
	@Deprecated
	private static final Map<Language, String> SEE_ALSO_LABELS = 
			new TreeMap<Language,String>() {/**	 */
		private static final long serialVersionUID = -3730771460500767544L;
	{
		put(Language.arabic,  "انظر أيضاً");		
		put(Language.basque, "Ikus, gainera");
		put(Language.catalan, "Vegeu també");
		put(Language.english, "See also");
		put(Language.spanish, "Véase también");
	}};
	
	@Deprecated
	private static final Map<Language, String> REFERENCES_LABELS = 
			new TreeMap<Language,String>() {/** */
		private static final long serialVersionUID = 5432282863060514555L;
	{
		put(Language.arabic,  "مراجع");
		put(Language.basque,  "Erreferentziak");
		put(Language.catalan, "Referències");
		put(Language.english, "References");
		put(Language.spanish, "Referencias");
	}};
	
	@Deprecated
	private static final Map<Language, String> NOTES_LABELS = 
			new TreeMap<Language,String>() {/** */
		private static final long serialVersionUID = 1227178732180772878L;
	{
		put(Language.catalan, "Notes");
		put(Language.english, "Notes");
		put(Language.spanish, "Notas");
	}};
	
	@Deprecated
	private static final Map<Language, String> BIBLIOGRAPHY_LABELS = 
			new TreeMap<Language,String>() {/** */
		private static final long serialVersionUID = 6410993635827884402L;
	{
		put(Language.basque, "Bibliografia");
		put(Language.catalan, "Bibliografia");
		put(Language.english, "Bibliography");
		put(Language.spanish, "Bibliografía");
	}};
	
	@Deprecated
	private static final Map<Language, String> FURTHER_READING_LABELS = 
			new TreeMap<Language,String>() {/** */
		private static final long serialVersionUID = 8291208634618126801L;
	{
		put(Language.english, "Further reading");
		put(Language.spanish, "Otras lecturas adicionales");
	}};
	
	@Deprecated
	private static final Map<Language, String> EXTERNAL_LINKS_LABELS = 
			new TreeMap<Language,String>() {/** */
		private static final long serialVersionUID = 4835751454431374044L;
	{
		put(Language.basque, "Kanpo loturak");
		put(Language.catalan, "Enllaços externs");
		put(Language.english, "External links");
		put(Language.spanish, "Enlaces externos");
	}};
	
	@Deprecated
	private static final Map<Language, String> REDIRECT_LABELS = 
			new TreeMap<Language,String>() {/** */
		private static final long serialVersionUID = 4835751454431374044L;
	{
		put(Language.english, "#redirect");
		put(Language.spanish, "#redirección");
	}};
	
	@Deprecated
	private static final Map<Language, String> DISAMBIGUATION_LABELS = 
			new TreeMap<Language,String>() {/** */
		private static final long serialVersionUID = 4835751454431374044L;
	{
		put(Language.catalan, "desambiguació");
		put(Language.english, "disambiguation");
		put(Language.spanish, "desambiguación");
	}};
	
	////////////////
	
	/**
	 * @param language
	 * @return image label(s) for the required language
	 */
	@Deprecated
	public static String[] getImageLabelDep(Language language){
		if (IMAGE_LABELS.containsKey(language)){
			return IMAGE_LABELS.get(language);
		}
		CHK.CHECK(false, "The required language is not available");
		return null;
	}

	/**
	 * @param language
	 * @return category label for the required language
	 */
	@Deprecated
	public static String getCategoryLabelDep(Language language){
		if (CATEGORY_LABELS.containsKey(language)){
			return CATEGORY_LABELS.get(language);
		}
		CHK.CHECK(false, "The required language is not available");
		return null;
	}

	/**
	 * @param language
	 * @return Section "See also" label for the required language
	 */
	@Deprecated
	public static String getSeeAlsoLabelDep(Language language){
		if (SEE_ALSO_LABELS.containsKey(language)){
			return SEE_ALSO_LABELS.get(language);
		}
		CHK.CHECK(false, "The required language is not available");
		return null;
	}

	/**
	 * @param language
	 * @return Section "References" label for the required language
	 */
	@Deprecated
	public static String getReferencesLabelDep(Language language){
		if (REFERENCES_LABELS.containsKey(language)){
			return REFERENCES_LABELS.get(language);
		}
		CHK.CHECK(false, "The required language is not available");
		return null;
	}

	/**
	 * @param language
	 * @return Section "Notes" label for the required language
	 */
	@Deprecated
	public static String getNotesLabelDep(Language language){
		if (NOTES_LABELS.containsKey(language)){
			return NOTES_LABELS.get(language);
		}
		CHK.CHECK(false, "The required language is not available");
		return null;
	}

	/**
	 * @param language
	 * @return Section "Bibliography" label for the required language
	 */
	@Deprecated
	public static String getBibliographyLabelDep(Language language){
		if (BIBLIOGRAPHY_LABELS.containsKey(language)){
			return BIBLIOGRAPHY_LABELS.get(language);
		}
		CHK.CHECK(false, "The required language is not available");
		return null;
	}

	/**
	 * @param language
	 * @return Section "Further reading" label for the required language
	 */
	@Deprecated
	public static String getFurhterReadingLabelDep(Language language){
		if (FURTHER_READING_LABELS.containsKey(language)){
			return FURTHER_READING_LABELS.get(language);
		}
		CHK.CHECK(false, "The required language is not available");
		return null;
	}

	/**
	 * @param language
	 * @return Section "External Links" label for the required language
	 */
	@Deprecated
	public static String getExternalLinksLabelDep(Language language){
		if (EXTERNAL_LINKS_LABELS.containsKey(language)){
			return EXTERNAL_LINKS_LABELS.get(language);
		}
		CHK.CHECK(false, "The required language is not available");
		return null;
	}

	/**
	 * @param language
	 * @return Redirect label for the required language
	 */
	@Deprecated
	public static String getRedirectLabelDep(Language language){
		if (REDIRECT_LABELS.containsKey(language)){
			return REDIRECT_LABELS.get(language);
		}
		CHK.CHECK(false, "The required language is not available");
		return null;
	}

	/**
	 * @param language
	 * @return Disambiguation label for the required language
	 */
	@Deprecated
	public static String getDisambiguationLabelFep(Language language){
		if (DISAMBIGUATION_LABELS.containsKey(language)){
			return DISAMBIGUATION_LABELS.get(language);
		}
		CHK.CHECK(false, "The required language is not available");
		return null;
	}

	
}
