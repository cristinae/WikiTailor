package cat.lump.aq.wikilink.jwpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cat.lump.aq.wikilink.jwpl.LanguageConstants;

import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;

public class LanguageConstantsTest {
	
	private LanguageConstants constants;
	@Before
	public void setUp() throws Exception {
		constants = new LanguageConstants();
	}
	
	@Test
	public void testGetImageLabel() {
		Assert.assertArrayEquals(new String[] {"ملف"}, constants.getImageLabel(Language.arabic));
		Assert.assertArrayEquals(new String[] {"fitxategi"}, constants.getImageLabel(Language.basque));		
		Assert.assertArrayEquals(new String[] {"fitxer"}, constants.getImageLabel(Language.catalan));
		Assert.assertArrayEquals(new String[] {"datoteka"}, constants.getImageLabel(Language.croatian));
		Assert.assertArrayEquals(new String[] {"file", "image"}, constants.getImageLabel(Language.english));
		Assert.assertArrayEquals(new String[] {"pilt"}, constants.getImageLabel(Language.estonian));
		Assert.assertArrayEquals(new String[] {"fichier", "image"}, constants.getImageLabel(Language.french));
		Assert.assertArrayEquals(new String[] {"image"}, constants.getImageLabel(Language.german));
		Assert.assertArrayEquals(new String[] {"αρχείο"}, constants.getImageLabel(Language.greek));
		Assert.assertArrayEquals(new String[] {"चित्र"}, constants.getImageLabel(Language.hindi));		
		Assert.assertArrayEquals(new String[] {"immagine", "file"}, constants.getImageLabel(Language.italian));
		Assert.assertArrayEquals(new String[] {"attēls"}, constants.getImageLabel(Language.latvian));
		Assert.assertArrayEquals(new String[] {"vaizdas"}, constants.getImageLabel(Language.lithuanian));
		Assert.assertArrayEquals(new String[] {"fişier", "imagine"}, constants.getImageLabel(Language.romanian));
		Assert.assertArrayEquals(new String[] {"image"}, constants.getImageLabel(Language.russian));
		Assert.assertArrayEquals(new String[] {"slika"}, constants.getImageLabel(Language.slovenian));		
		Assert.assertArrayEquals(new String[]{"archivo", "imagen"}, constants.getImageLabel(Language.spanish));		
	}

	/**
	 * @ERROR there is a bug here with the splitting of the text in Arabic!!!
	 */
	@Test
	public void testGetCategoryLabel() {
		Assert.assertArrayEquals(new String[] {"تصنيفات","تصنيف"}, constants.getCategoryLabel(Language.arabic));
		Assert.assertArrayEquals(new String[] {"kategoria"}, constants.getCategoryLabel(Language.basque));		
		Assert.assertArrayEquals(new String[] {"categoria","categories"}, constants.getCategoryLabel(Language.catalan));
		Assert.assertArrayEquals(new String[] {"kategorija"}, constants.getCategoryLabel(Language.croatian));
		Assert.assertArrayEquals(new String[] {"category","categories"}, constants.getCategoryLabel(Language.english));
		Assert.assertArrayEquals(new String[] {"kategooria"}, constants.getCategoryLabel(Language.estonian));
		Assert.assertArrayEquals(new String[] {"catégorie", "catégories"}, constants.getCategoryLabel(Language.french));
		Assert.assertArrayEquals(new String[] {"kategorie","kategorien"}, constants.getCategoryLabel(Language.german));
		Assert.assertArrayEquals(new String[] {"κατηγορία", "κατηγορίες"}, constants.getCategoryLabel(Language.greek));
		Assert.assertArrayEquals(new String[] {"श्रेणी"}, constants.getCategoryLabel(Language.hindi));		
		Assert.assertArrayEquals(new String[] {"categorie"}, constants.getCategoryLabel(Language.italian));
		Assert.assertArrayEquals(new String[] {"kategorija"}, constants.getCategoryLabel(Language.latvian));
		Assert.assertArrayEquals(new String[] {"kategorija"}, constants.getCategoryLabel(Language.lithuanian));
		Assert.assertArrayEquals(new String[] {"categorie", "categorii"}, constants.getCategoryLabel(Language.romanian));
		//Assert.assertArrayEquals(new String[] {"bсё"}, constants.getCategoryLabel(Language.russian));
		Assert.assertArrayEquals(new String[] {"kategorije"}, constants.getCategoryLabel(Language.slovenian));		
		Assert.assertArrayEquals(new String[] {"categoría","categorías"}, constants.getCategoryLabel(Language.spanish));
	}

	/**
	 * this test should fail, because no entries are included for every language 
	 */
	@Test
	public void testGetSeeAlsoLabel() {
		Assert.assertArrayEquals(new String[] {"انظر أيضا"},constants.getSeeAlsoLabel(Language.arabic));		
		Assert.assertArrayEquals(new String[] {"ikus, gainera"}, constants.getSeeAlsoLabel(Language.basque));		
		Assert.assertArrayEquals(new String[] {"vegeu també"}, constants.getSeeAlsoLabel(Language.catalan));
		Assert.assertArrayEquals(new String[] {""}, constants.getSeeAlsoLabel(Language.croatian));
		Assert.assertArrayEquals(new String[] {"see also"}, constants.getSeeAlsoLabel(Language.english));
		Assert.assertArrayEquals(new String[] {""}, constants.getSeeAlsoLabel(Language.estonian));
		Assert.assertArrayEquals(new String[] {"voir aussi", "autre"}, constants.getSeeAlsoLabel(Language.french));
		Assert.assertArrayEquals(new String[] {"siehe auch"}, constants.getSeeAlsoLabel(Language.german));
		Assert.assertArrayEquals(new String[] {"δείτε επίσης"}, constants.getSeeAlsoLabel(Language.greek));
		Assert.assertArrayEquals(new String[] {""}, constants.getSeeAlsoLabel(Language.hindi));		
		Assert.assertArrayEquals(new String[] {""}, constants.getSeeAlsoLabel(Language.italian));
		Assert.assertArrayEquals(new String[] {""}, constants.getSeeAlsoLabel(Language.latvian));
		Assert.assertArrayEquals(new String[] {""}, constants.getSeeAlsoLabel(Language.lithuanian));
		Assert.assertArrayEquals(new String[] {"vezi și"}, constants.getSeeAlsoLabel(Language.romanian));
		Assert.assertArrayEquals(new String[] {""}, constants.getSeeAlsoLabel(Language.russian));
		Assert.assertArrayEquals(new String[] {""}, constants.getSeeAlsoLabel(Language.slovenian));		
		Assert.assertArrayEquals(new String[] {"véase también"}, constants.getSeeAlsoLabel(Language.spanish));
	}

	/**	 */
	@Test
	public void testGetReferencesLabel() {
		Assert.assertArrayEquals(new String[] {"مراجع","المراجع"},  constants.getReferencesLabel(Language.arabic));
		Assert.assertArrayEquals(new String[] {"erreferentziak"}, constants.getReferencesLabel(Language.basque));		
		Assert.assertArrayEquals(new String[] {"referències","pàgines que s'hi relacionen"}, constants.getReferencesLabel(Language.catalan));
		Assert.assertArrayEquals(new String[] {""}, constants.getReferencesLabel(Language.croatian));
		Assert.assertArrayEquals(new String[] {"references"}, constants.getReferencesLabel(Language.english));
		Assert.assertArrayEquals(new String[] {""}, constants.getReferencesLabel(Language.estonian));
		Assert.assertArrayEquals(new String[] {"références", "notes et références"}, constants.getReferencesLabel(Language.french));
		Assert.assertArrayEquals(new String[] {"einzelnachweise"}, constants.getReferencesLabel(Language.german));
		Assert.assertArrayEquals(new String[] {"παραπομπές"}, constants.getReferencesLabel(Language.greek));
		Assert.assertArrayEquals(new String[] {""}, constants.getReferencesLabel(Language.hindi));		
		Assert.assertArrayEquals(new String[] {""}, constants.getReferencesLabel(Language.italian));
		Assert.assertArrayEquals(new String[] {""}, constants.getReferencesLabel(Language.latvian));
		Assert.assertArrayEquals(new String[] {""}, constants.getReferencesLabel(Language.lithuanian));
		Assert.assertArrayEquals(new String[] {"referințe"}, constants.getReferencesLabel(Language.romanian));
		Assert.assertArrayEquals(new String[] {""}, constants.getReferencesLabel(Language.russian));
		Assert.assertArrayEquals(new String[] {""}, constants.getReferencesLabel(Language.slovenian));		
		Assert.assertArrayEquals(new String[] {"referencias"}, constants.getReferencesLabel(Language.spanish));
	}
	
	@Test
	public void testGetNotesLabel() {		
		Assert.assertArrayEquals(new String[] {"ملاحظة,ملاحظات"}, constants.getNotesLabel(Language.arabic));
		Assert.assertArrayEquals(new String[] {"oharrak"}, constants.getNotesLabel(Language.basque));
		Assert.assertArrayEquals(new String[] {"notes", "nota"}, constants.getNotesLabel(Language.catalan));
		Assert.assertArrayEquals(new String[] {"notes"}, constants.getNotesLabel(Language.english));
		Assert.assertArrayEquals(new String[] {"notes", "notes et références"}, constants.getNotesLabel(Language.french));
		Assert.assertArrayEquals(new String[] {"schriften"}, constants.getNotesLabel(Language.german));
		Assert.assertArrayEquals(new String[] {"σημειώσεις","υποσημειώσεις"}, constants.getNotesLabel(Language.greek));
		Assert.assertArrayEquals(new String[] {"note","referințe"}, constants.getNotesLabel(Language.romanian));
		Assert.assertArrayEquals(new String[] {"notas", "nota"}, constants.getNotesLabel(Language.spanish));		
	}

	@Test
	public void testGetBibliographyLabel() {
		Assert.assertArrayEquals(new String[] {"مصادر"}, constants.getBibliographyLabel(Language.arabic));
		Assert.assertArrayEquals(new String[] {"bibliografia"}, constants.getBibliographyLabel(Language.basque));
		Assert.assertArrayEquals(new String[] {"bibliografia", "bibliografia complementària"}, constants.getBibliographyLabel(Language.catalan));
		Assert.assertArrayEquals(new String[] {"bibliography"}, constants.getBibliographyLabel(Language.english));
		Assert.assertArrayEquals(new String[] {"bibliographie"}, constants.getBibliographyLabel(Language.french));
		Assert.assertArrayEquals(new String[] {"quellen"}, constants.getBibliographyLabel(Language.german));
		Assert.assertArrayEquals(new String[] {"βιβλιογραφία"}, constants.getBibliographyLabel(Language.greek));
		Assert.assertArrayEquals(new String[] {"bibliografie"}, constants.getBibliographyLabel(Language.romanian));
		Assert.assertArrayEquals(new String[] {"bibliografía"}, constants.getBibliographyLabel(Language.spanish));
	}

	@Test
	public void testGetFurhterReadingLabel() {
		Assert.assertArrayEquals(new String[] {"قراءة إضافية"}, constants.getFurhterReadingLabel(Language.arabic));
		Assert.assertArrayEquals(new String[] {"lectures addicionals"}, constants.getFurhterReadingLabel(Language.catalan));
		Assert.assertArrayEquals(new String[] {"further reading"}, constants.getFurhterReadingLabel(Language.english));
		Assert.assertArrayEquals(new String[] {"articles connexes"}, constants.getFurhterReadingLabel(Language.french));
		Assert.assertArrayEquals(new String[] {"literatur"}, constants.getFurhterReadingLabel(Language.german));
		Assert.assertArrayEquals(new String[] {"πρόσθετη βιβλιογραφία"}, constants.getFurhterReadingLabel(Language.greek));
		Assert.assertArrayEquals(new String[] {"lectură suplimentară", "lecturi suplimentare"}, constants.getFurhterReadingLabel(Language.romanian));
		Assert.assertArrayEquals(new String[] {"otras lecturas adicionales"}, constants.getFurhterReadingLabel(Language.spanish));
	}

	@Test
	public void testGetExternalLinksLabel() {
		Assert.assertArrayEquals(new String[] {"وصلات خارجية"}, constants.getExternalLinksLabel(Language.arabic));
		Assert.assertArrayEquals(new String[] {"kanpo loturak"}, constants.getExternalLinksLabel(Language.basque));
		Assert.assertArrayEquals(new String[] {"enllaços externs"}, constants.getExternalLinksLabel(Language.catalan));
		Assert.assertArrayEquals(new String[] {"external links"}, constants.getExternalLinksLabel(Language.english));
		Assert.assertArrayEquals(new String[] {"liens externes"}, constants.getExternalLinksLabel(Language.french));
		Assert.assertArrayEquals(new String[] {"weblinks"}, constants.getExternalLinksLabel(Language.german));
		Assert.assertArrayEquals(new String[] {"εξωτερικοί σύνδεσμοι"}, constants.getExternalLinksLabel(Language.greek));
		Assert.assertArrayEquals(new String[] {"legături externe"}, constants.getExternalLinksLabel(Language.romanian));
		Assert.assertArrayEquals(new String[] {"enlaces externos"}, constants.getExternalLinksLabel(Language.spanish));
	}

	@Test
	public void testGetRedirectLabel() {
		//Assert.assertArrayEquals(new String[] {"تحويل#"}, constants.getRedirectLabel(Language.arabic));
		//The label for arabic is temporally moved to a single sharp (ABC; 3/10/2014)
		Assert.assertArrayEquals(new String[] {"#"}, constants.getRedirectLabel(Language.arabic));
		Assert.assertArrayEquals(new String[] {"#redirect"}, constants.getRedirectLabel(Language.basque));
		Assert.assertArrayEquals(new String[] {"#redirect"}, constants.getRedirectLabel(Language.catalan));
		Assert.assertArrayEquals(new String[] {"#redirect"}, constants.getRedirectLabel(Language.english));
		Assert.assertArrayEquals(new String[] {"#redirection", "#redirect"}, constants.getRedirectLabel(Language.french));
		Assert.assertArrayEquals(new String[] {"#redirect", "#weiterleitung"}, constants.getRedirectLabel(Language.german));
		Assert.assertArrayEquals(new String[] {"#ανακατευθυνση"}, constants.getRedirectLabel(Language.greek));
		Assert.assertArrayEquals(new String[] {"#redirecteaza", "#redirecționeaza"}, constants.getRedirectLabel(Language.romanian));
		Assert.assertArrayEquals(new String[] {"#redirección"}, constants.getRedirectLabel(Language.spanish));
	}
	
	@Test
	public void testGetDisambiguationLabel() {
		Assert.assertArrayEquals(new String[] {"توضيح","صفحة توضيح"}, constants.getDisambiguationLabel(Language.arabic));
		Assert.assertArrayEquals(new String[] {"argipen", "disambig", "disambiguation"}, constants.getDisambiguationLabel(Language.basque));
		Assert.assertArrayEquals(new String[] {"desambiguació", "biografies", "acrònim"}, constants.getDisambiguationLabel(Language.catalan));
		Assert.assertArrayEquals(new String[] { "disamb", "disambig", "disambiguation", "geodis", "hndis", "numberdis", "letter-numbercombdisambig"}, constants.getDisambiguationLabel(Language.english));
		Assert.assertArrayEquals(new String[] {"homonymie", "homonyme"}, constants.getDisambiguationLabel(Language.french));
		Assert.assertArrayEquals(new String[] {"begriffsklärung"}, constants.getDisambiguationLabel(Language.german));
		Assert.assertArrayEquals(new String[] {"αποσαφήνιση", "disambig", "αποσαφ"}, constants.getDisambiguationLabel(Language.greek));
		Assert.assertArrayEquals(new String[] {"dezambiguizare", "persoane omonime", "dezgeo", "disambig", "dezamb", "hndis", "deznume"}, constants.getDisambiguationLabel(Language.romanian));
		Assert.assertArrayEquals(new String[] {"desambig", "desambiguación", "disambig", "desambiguacion"}, constants.getDisambiguationLabel(Language.spanish));
		//Assert.assertEquals("desambiguación", constants.getDisambiguationLabel(Language.croatian));
	}

	@Test
	public void testGetAcronymLabel() {
		Assert.assertArrayEquals(new String[] {"acrònim", "biografies"}, constants.getAcronymLabel(Language.catalan));
	}
	
}
