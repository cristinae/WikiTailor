package cat.lump.aq.textextraction.wikipedia.utilities;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.wikilink.WikipediaDBdata;
import cat.lump.aq.wikilink.config.MySQLWikiConfiguration;
import cat.lump.aq.wikilink.connection.WikipediaDriverManager;


/**
 * A class to identify the common categories across n languages in Wikipedia.
 * The process queries directly to the SQL database.
 * 
 * @author cristina
 * @since Jan 26, 2015
 * TODO: Convert into a cli
 */
public class CommonCategoriesExtractor {
	
	/**DB with langlinks & articles pairs*/
	final protected static String pairs_db = MySQLWikiConfiguration.getPairsDBname();

	private WikipediaDriverManager dmc;

	private String[] langs;
	private String year;
	private File folder;

	private static LumpLogger logger = new LumpLogger(
			CommonCategoriesExtractor.class.getSimpleName());

	/**
	 * Instantiates the object with the provided languages. The smallest Wikipedia 
	 * is the one to compare against. The year must be one of the available dumps. 
	 * Tables "langlinks" and "page" are necessary
	 * 
	 * Languages are required in the Wikipedia code format (e.g. en for English and es for Spanish).
	 * The DB connections are invoked according to these parameters.
	 *  
	 * @param langs[]
	 * @param year
	 * @param folder 
	 * @throws Throwable 
	 */		
	public CommonCategoriesExtractor(String[] langs, String year, File folder) throws Throwable	{		

		dmc = new WikipediaDriverManager();
		dmc.createConnection();

		this.langs = langs;
		this.year = year;
		this.folder = folder;
		
		checkAllTablesAvailable();
		String smallestCat = lookForSmallestSet();
		selectCats(smallestCat);
		
		dmc.close();
	}


	@SuppressWarnings("unused")
	public static void main(String[] args) throws Throwable{
		
		// Defaults
		String year = "2013";
		String[] langs = {"en", "es"};
//		String[] langs = {"en", "es", "de", "fr", "ca", "ar", "eu", "el", "ro", "oc"};
		//File folder = new File (System.getProperty("user.home") + FileIO.separator + 
		//		"pln"+ FileIO.separator + "wikipedia" + FileIO.separator + "categories");
		File folder = new File (System.getProperty("user.home") + FileIO.separator+ "wiki"+
						 FileIO.separator + "wikiparable" + FileIO.separator + "categories"); //cluster all

		FileIO.createDir(folder);

		//Invoke an instance of the extractor
		CommonCategoriesExtractor catExt = new CommonCategoriesExtractor(langs, year, folder);

	}	


	/** 
	 * Checks if all the tables needed are in the database. Exits otherwise.
	 */
	private void checkAllTablesAvailable() {
    	
		for (String lang : langs) {
			String titlePage = WikipediaDBdata.getPageTableName(lang, Integer.parseInt(year));
			String titleLanglinks = WikipediaDBdata.getLanglinksTableName(lang, Integer.parseInt(year)); 

			try {
				if (!dmc.tableExists(titlePage, pairs_db)){
					logger.errorEnd("Table " + titlePage + " does not exist in the DB.");					
				}
				if (!dmc.tableExists(titleLanglinks, pairs_db)){
					logger.errorEnd("Table " + titleLanglinks + " does not exist in the DB.");										
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Looks for the categories available in the smallestCat Wikipedia edition into the other editions
	 * and prints into a file per language the intersection
	 * 
	 * @param smallestCat
	 */
	private void selectCats(String smallestCat) {
		dmc.setDB(pairs_db);

		String titlePageT = WikipediaDBdata.getPageTableName(smallestCat, Integer.parseInt(year));
		String titleLanglinksT = WikipediaDBdata.getLanglinksTableName(smallestCat, Integer.parseInt(year)); 

		// smallestCat is removed from the vector of languages to be compared against
		int numberLangs = langs.length;
		String[] langsReduced = new String[numberLangs-1];
		int i = 0;
		for (String lang : langs) {
			if (!lang.equals(smallestCat)){	
		        langsReduced[i] = lang;
				i++;
		    }
		}
        String catsList[] = new String[numberLangs];
		for (i=0; i<numberLangs; i++) {  //AQUI
			catsList[i] = "";
		}
		
		ResultSet rs = null;
		String query = "SELECT `page_id` FROM `" + titlePageT + "` WHERE `page_namespace` = 14";
		try {
			// Select all the category pages in the smallest Wikipedia
			rs = dmc.runStatement(query);
			int count = 0;
			int countCommon = 0;
			int missLinks = 0;
			while (rs.next()) {
				
				int id = rs.getInt("page_id");
				ResultSet rsLangs = null;
				String queryLangs = "SELECT `ll_lang`  FROM `" + titleLanglinksT + "` WHERE `ll_from` = " + id;
				rsLangs = dmc.runStatement(queryLangs);
				List<String> languages = new ArrayList<String>();
				while (rsLangs.next()){
					  languages.add(rsLangs.getString("ll_lang"));
				}
				
				boolean isIntersection = true;
				// Look if a category page is in all the remaining languages according to langlinks table
				for (String lang : langsReduced) {
					if (!languages.contains(lang)) {
						isIntersection = false;
						break;
					}
				}
				// If this category is common to all wikipedias increment the counter, look for
				// the id and title in every language, and store them in a string per language
				if (isIntersection == true){
					// Include the smallest wikipedia
					// but if there is a missLinks in one language, none must be included (catsTmp)
					boolean include = true;
			        String catsTmp[] = new String[numberLangs];
					//SELECT `page_title`FROM `wikieu_2013_page` WHERE `id` = id AND `page_namespace` = 14
					String queryS = "SELECT `page_title` FROM `" + titlePageT + 
							"` WHERE `page_id` = \"" + id + "\" AND `page_namespace` = 14";
					ResultSet rsqS = dmc.runStatement(queryS);
					if (rsqS.next()){
						String title = WikipediaDBdata.getformatWPtitle(rsqS, "page_title");
						catsTmp[numberLangs-1]= id + " " + title + "\n";
					} else{ // titleLanglinksT has some inexistent links but this shouldn't happen for this language
						include = false;
						missLinks++;
						continue;
					}
					// Include all the other editions
					for (i=0; i<langsReduced.length; i++) {
						catsTmp[i] = "";
						String titPageTab = WikipediaDBdata.getPageTableName(langsReduced[i], Integer.parseInt(year));
						
						//SELECT `ll_title` FROM `wikica_2013_langlinks` WHERE `ll_from` = id AND `ll_lang` = 'langsReduced[i]'   
						String query1 = "SELECT `ll_title`  FROM `" + titleLanglinksT + 
								"` WHERE `ll_from` = " + id + " AND `ll_lang` = '" + langsReduced[i] + "'";
						ResultSet rsq1 = dmc.runStatement(query1);
						rsq1.next();
						String title = WikipediaDBdata.getformatWPtitle(rsq1, "ll_title");
						//SELECT page_id FROM `wikica_2013_page` WHERE `page_title` = 'title'
						String query2 = "SELECT `page_id` FROM `" + titPageTab + 
								"` WHERE `page_title` = \"" + title + "\" AND `page_namespace` = 14";
						ResultSet rsq2 = dmc.runStatement(query2);
						if (rsq2.next()) {   
							String langID = rsq2.getString("page_id");
							catsTmp[i] = langID + " " + title + "\n";
						}else { // titleLanglinksT has some inexistent links
							include = false;
							missLinks++;
							break;
						} 
					}
					if(include) {
						countCommon++;
						for (i=0; i<numberLangs; i++) {
							catsList[i] = catsList[i].concat(catsTmp[i]);
							if (countCommon%100 == 0) { 
								boolean firstTime = countCommon == 100 ? true : false;
								String lang = i==numberLangs-1 ? smallestCat : langsReduced[i];
								printCatsFile(lang, catsList[i], firstTime);
								catsList[i] = "";
							}
						}						
						if (countCommon%100 == 0) { 
							logger.info("Common categories up to now " + countCommon);
						}
					}
				} //fi while category
			count++;
			}
			for (i=0; i<numberLangs; i++) {
				String lang = i==numberLangs-1 ? smallestCat : langsReduced[i];
					printCatsFile(lang, catsList[i], false);
			}
			logger.info(countCommon + " extracted common categories (" + missLinks + " inexistent langlinks)");
		} catch (SQLException e) {
			e.printStackTrace();	
			logger.warn("Error in querying the database (@selectcats())");
		}

		/* Print the results in one file per language
		for (i=0; i<langsReduced.length; i++) {
			printCatsFile(langsReduced[i], catsList[i]);
		}
		// Print the smallest wikipedia
		printCatsFile(smallestCat, catsList[numberLangs-1]);
*/
	}
		

	/**
	 * Looks for the Wikipedia edition with less categories. This will be the
	 * the seed for the intersection among languages.
	 * @return smallest
	 * 		String with the language
	 */
	private String lookForSmallestSet() {
		dmc.setDB(pairs_db);
		
		int minim = 1000000000;
		String smallest = "en";
		for (String lang : langs) {
			String titlePageT = WikipediaDBdata.getPageTableName(lang, Integer.parseInt(year));

			ResultSet rs = null;
			//Categories have a namespace 14
			//SELECT COUNT(*) FROM `wikica_2013_page` WHERE `page_namespace` = 14
			String query = "SELECT COUNT(*) AS total FROM `" + titlePageT + "` WHERE `page_namespace` = 14";
			try {
				rs = dmc.runStatement(query);
				rs.next();
				int cats = rs.getInt("total");
				logger.info("There are "  + cats + " cats for " + lang );
				if (cats < minim) {
					minim = cats;
					smallest = lang;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				logger.warn("MySQL error (@lookForSmallestSet())");
			}
		}
		if (smallest.equalsIgnoreCase("en")){
			logger.warn("Check: The language with less categories is English");
		}
		logger.info("The wikipedia edition with less categories is " + smallest + " ("  + minim + " cats)");
		return smallest; 
	}

	
	/**
	 * Prints all the categories stored in the string categories into a file 
	 * @param lang
	 * @param categories
	 */
	private void printCatsFile(String lang, String categories, boolean firstTime) {
		String fileName = "commonCats" + year + "." + StringUtils.join(langs, "") + "." + lang + ".txt";
		try {
			if(firstTime) {
				FileIO.stringToFile(new File(folder,fileName), categories, false);				
			} else {
				FileIO.appendStringToFile(new File(folder,fileName), categories, false);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.warn("There was an error printing the file " + fileName);
		}			
	}

}
