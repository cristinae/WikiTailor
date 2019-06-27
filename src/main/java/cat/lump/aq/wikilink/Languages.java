package cat.lump.aq.wikilink;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import cat.lump.aq.basics.check.CHK;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;

/**
 * A collection of all the available languages in Wikipedia up to 2019
 * 
 * @author albarron
 */
public class Languages {
	

	private static final Map<String, Language> AVAILABLE_LANGUAGES= 
			new TreeMap<String, Language>(){/***/
		private static final long serialVersionUID = 2552098132934864032L;
	{
		put("ar", Language.arabic);			put("eu", Language.basque);
		put("bg", Language.bulgarian);      put("cs", Language.czech);
		put("ca", Language.catalan);		put("hr", Language.croatian);
		put("en", Language.english);		put("et", Language.estonian);
		put("fr", Language.french);			put("de", Language.german);
		put("el", Language.greek);			put("hi", Language.hindi);
		put("it", Language.italian);		put("lv", Language.latvian);
		put("lt", Language.lithuanian);		put("pt", Language.portuguese);
		put("ro", Language.romanian);       put("hu", Language.hungarian);
		put("ru", Language.russian);		put("sl", Language.slovenian);
		put("es", Language.spanish);		put("oc", Language.occitan);
		put("gu", Language.gujarati);       put("simple", Language.simple_english);
	}};
	
	
//	/**Relevant languages for the Tacardi project */
//	public static final String[] langWikicardi = {
//		"ca", 	//Catalan
//		"en", 	//English
//		"es", 	//Spanish
//		"eu"	// Basque
//	};
//	
//	/**A few languages for testing. */
//	public static final String[] langProve = {
//		"ca", 	//Catalan
//		"el",	//Greek
//		"es", 	//Spanish
//		"lv"	//Latvian
//		};
//	
//	/**Languages relevant to the Accurat project  */
//	public static final String[] langAccurat = {
//		"ca",	//Catalan
//		"de", 	//German
//		"el", 	//Greek
//		"en", 	//English
//		"et", 	//Estonian
//		"hr", 	//Croatian
//		"lt", 	//Lithuanian
//		"lv", 	//Latvian
//		"ro", 	//Romanian		
//		"sl" 	//Slovenian		
//		};
		
	/**All the languages for which a Wikipedia exists. Commented languages 
	 * are listed in Wikipedia itself, but by the time (2010) no dumps were 
	 * downloaded because they did not exist. (tokipona is a special case) 
	 *</br>
	 *267 languages in total 
	 */
	private static String[] langAll = {
		"aa",			// Afar
		"ab",			// Abkhazian
		//"ace",			// Acehnese
		"af",			// Afrikaans
		"ak",			// Akan
		"als",			// Alemannic
		"am",			// Amharic
		"an",			// Aragonese
		"ang",			// Anglo-Saxon
		"ar",			// Arabic
		"arc",			// Assyrian Neo-Aramaic
		"arz",			// Egyptian Arabic
		"as",			// Assamese
		"ast",			// Asturian
		"av",			// Avar
		"ay",			// Aymara
		"az",			// Azeri
		"ba",			// Bashkir
		"bar",			// Bavarian
		"bat_smg",		// Samogitian
		"bcl",			// Central_Bicolano
		"be",			// Belarusian
		"be_x_old",		// Belarusian (Taraškievica)
		"bg",			// Bulgarian
		"bh",			// Bihari
		"bi",			// Bislama
		//"bjn",			// Banjar
		"bm",			// Bambara
		"bn",			// Bengali
		"bo",			// Tibetan
		"bpy",			// Bishnupriya Manipuri
		"br",			// Breton
		"bs",			// Bosnian
		"bug",			// Buginese
		"bxr",			// Buryat (Russia)
		"ca",			// Catalan
		"cbk_zam",		// Zamboanga Chavacano
		"cdo",			// Min Dong
		"ce",			// Chechen
		"ceb",			// Cebuano
		"ch",			// Chamorro
		"cho",			// Choctaw
		"chr",			// Cherokee
		"chy",			// Cheyenne
		//"ckb",			// Sorani
		"co",			// Corsican
		"cr",			// Cree
		"crh",			// Crimean Tatar
		"cs",			// Czech
		"csb",			// Kashubian
		"cu",			// Old Church Slavonic
		"cv",			// Chuvash
		"cy",			// Welsh
		"da",			// Danish
		"de",			// German
		"diq",			// Zazaki
		"dsb",			// Lower Sorbian
		"dv",			// Divehi
		"dz",			// Dzongkha
		"ee",			// Ewe
		"el",			// Greek
		"eml",			// Emilian-Romagnol
		"en",			// English
		"eo",			// Esperanto
		"es",			// Spanish
		"et",			// Estonian
		"eu",			// Basque
		"ext",			// Extremaduran
		"fa",			// Persian
		"ff",			// Fula
		"fi",			// Finnish
		"fiu_vro",		// Võro
		"fj",			// Fijian
		"fo",			// Faroese
		"fr",			// French
		"frp",			// Franco-Provençal/Arpitan
		//"frr",			// North Frisian
		"fur",			// Friulian
		"fy",			// West Frisian
		"ga",			// Irish
		"gan",			// Gan
		"gd",			// Scottish Gaelic
		"gl",			// Galician
		"glk",			// Gilaki
		"gn",			// Guarani
		"got",			// Gothic
		"gu",			// Gujarati
		"gv",			// Manx
		"ha",			// Hausa
		"hak",			// Hakka
		"haw",			// Hawaiian
		"he",			// Hebrew
		"hi",			// Hindi
		"hif",			// Fiji Hindi
		"ho",			// Hiri Motu
		"hr",			// Croatian
		"hsb",			// Upper Sorbian
		"ht",			// Haitian
		"hu",			// Hungarian
		"hy",			// Armenian
		"hz",			// Herero
		"ia",			// Interlingua
		"id",			// Indonesian
		"ie",			// Interlingue
		"ig",			// Igbo
		"ii",			// Sichuan Yi
		"ik",			// Inupiak
		"ilo",			// Ilokano
		"io",			// Ido
		"is",			// Icelandic
		"it",			// Italian
		"iu",			// Inuktitut
		"ja",			// Japanese
		"jbo",			// Lojban
		"jv",			// Javanese
		"ka",			// Georgian
		"kaa",			// Karakalpak
		"kab",			// Kabyle
		"kg",			// Kongo
		"ki",			// Kikuyu
		"kj",			// Kuanyama
		"kk",			// Kazakh
		"kl",			// Greenlandic
		"km",			// Khmer
		"kn",			// Kannada
		"ko",			// Korean
		//"koi",			// Komi-Permyak
		"kr",			// Kanuri
		//"krc",			// Karachay-Balkar
		"ks",			// Kashmiri
		"ksh",			// Ripuarian
		"ku",			// Kurdish
		"kv",			// Komi
		"kw",			// Cornish
		"ky",			// Kirghiz
		"la",			// Latin
		"lad",			// Ladino
		"lb",			// Luxembourgish
		"lbe",			// Lak
		"lg",			// Luganda
		"li",			// Limburgian
		"lij",			// Ligurian
		"lmo",			// Lombard
		"ln",			// Lingala
		"lo",			// Lao
		"lt",			// Lithuanian
		"lv",			// Latvian
		"map_bms",		// Banyumasan
		"mdf",			// Moksha
		"mg",			// Malagasy
		"mh",			// Marshallese
		"mhr",			// Meadow Mari
		"mi",			// Maori
		"mk",			// Macedonian
		"ml",			// Malayalam
		"mn",			// Mongolian
		"mo",			// Moldovan
		"mr",			// Marathi
		//"mrj",			// Hill Mari
		"ms",			// Malay
		"mt",			// Maltese
		"mus",			// Muscogee
		//"mwl",			// Mirandese
		"my",			// Burmese
		"myv",			// Erzya
		"mzn",			// Mazandarani
		"na",			// Nauruan
		"nah",			// Nahuatl
		"nap",			// Neapolitan
		"nds",			// Low Saxon
		"nds_nl",		// Dutch Low Saxon
		"ne",			// Nepali
		"new",			// Newar / Nepal Bhasa
		"ng",			// Ndonga
		"nl",			// Dutch
		"nn",			// Norwegian (Nynorsk)
		"no",			// Norwegian (Bokmål)
		"nov",			// Novial
		"nrm",			// Norman
		"nv",			// Navajo
		"ny",			// Chichewa
		"oc",			// Occitan
		"om",			// Oromo
		"or",			// Oriya
		"os",			// Ossetian
		"pa",			// Punjabi
		"pag",			// Pangasinan
		"pam",			// Kapampangan
		"pap",			// Papiamentu
		//"pcd",			// Picard
		"pdc",			// Pennsylvania German
		"pi",			// Pali
		"pih",			// Norfolk
		"pl",			// Polish
		"pms",			// Piedmontese
		//"pnb",			// Western Panjabi
		"pnt",			// Pontic
		"ps",			// Pashto
		"pt",			// Portuguese
		"qu",			// Quechua
		"rm",			// Romansh
		"rmy",			// Romani
		"rn",			// Kirundi
		"ro",			// Romanian
		"roa_rup",		// Aromanian
		"roa_tara",		// Tarantino
		"ru",			// Russian
		"rw",			// Kinyarwanda
		"sa",			// Sanskrit
		"sah",			// Sakha
		"sc",			// Sardinian
		"scn",			// Sicilian
		"sco",			// Scots
		"sd",			// Sindhi
		"se",			// Northern Sami
		"sg",			// Sango
		"sh",			// Serbo-Croatian
		"si",			// Sinhalese
		"simple",		// Simple English
		"sk",			// Slovak
		"sl",			// Slovenian
		"sm",			// Samoan
		"sn",			// Shona
		"so",			// Somali
		"sq",			// Albanian
		"sr",			// Serbian
		"srn",			// Sranan
		"ss",			// Swati
		"st",			// Sesotho
		"stq",			// Saterland Frisian
		"su",			// Sundanese
		"sv",			// Swedish
		"sw",			// Swahili
		"szl",			// Silesian
		"ta",			// Tamil
		"te",			// Telugu
		"tet",			// Tetum
		"tg",			// Tajik
		"th",			// Thai
		"ti",			// Tigrinya
		"tk",			// Turkmen
		"tl",			// Tagalog
		"tn",			// Tswana
		"to",			// Tongan
		"tokipona",   	// Toki Pona IN THE DUMPS, BUT NO LONGER INCLUDED IN WIKIPEDIA
		"tpi",			// Tok Pisin
		"tr",			// Turkish
		"ts",			// Tsonga
		"tt",			// Tatar
		"tum",			// Tumbuka
		"tw",			// Twi
		"ty",			// Tahitian
		"udm",			// Udmurt
		"ug",			// Uyghur
		"uk",			// Ukrainian
		"ur",			// Urdu
		"uz",			// Uzbek
		"ve",			// Venda
		"vec",			// Venetian
		"vi",			// Vietnamese
		"vls",			// West Flemish
		"vo",			// Volapük
		"wa",			// Walloon
		"war",			// Waray-Waray
		"wo",			// Wolof
		"wuu",			// Wu
		"xal",			// Kalmyk
		"xh",			// Xhosa
		"yi",			// Yiddish
		"yo",			// Yoruba
		"za",			// Zhuang
		"zea",			// Zealandic
		"zh",			// Chinese
		"zh_classical",	// Classical Chinese
		"zh_min_nan",	// Min Nan
		"zh_yue",		// Cantonese
		"zu"			// Zulu		
		};

	/**
	 * @param iso639code
	 * @return true if the language is considered.
	 */
	public static boolean isLanguageAvailable(String iso639code) {		
		CHK.CHECK_NOT_NULL(iso639code);
		return AVAILABLE_LANGUAGES.containsKey(iso639code);
	}
	
	
	public static Language getJwplLanguage(Locale locale){
		return getJwplLanguage(locale.getLanguage());
	}
	
	/**
	 * @param iso639code
	 * @return get the JWPL language
	 */
	public static Language getJwplLanguage(String iso639code) {		
		if (! isLanguageAvailable(iso639code)){			
			CHK.CHECK(false, "The language is not yet implemented");
		}
		return AVAILABLE_LANGUAGES.get(iso639code);			
	}
	
	public static String[] getLangAll(){
		return langAll;
	}
	

}
