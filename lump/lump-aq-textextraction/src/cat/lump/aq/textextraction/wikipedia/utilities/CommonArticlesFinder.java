package cat.lump.aq.textextraction.wikipedia.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.wikilink.WikipediaDBdata;
import cat.lump.aq.wikilink.config.MySQLWikiConfiguration;
import cat.lump.aq.wikilink.connexion.WikipediaDriverManager;

/**
 * A class to identify the common articles across n languages in Wikipedia
 * from files with the list of IDs for every language.
 * Redirects are not resolved as it is necessary for dumps before 2015. 
 * Use {@code CommonNamespaceFinder} instead for the following dumps.
 * The process queries directly to the SQL database.
 * 
 * @author cristina
 * @since Feb 12, 2015
 * TODO: Convert into a cli?
 * TODO: Generalise for every article (no only those in a list) Si?
 *       (either here or generalise CommonCategoriesExtractor to deal with articles) 
 */

public class CommonArticlesFinder {
	
	/**DB with langlinks & articles pairs*/
	final protected static String pairs_db = MySQLWikiConfiguration.getPairsDBname();

	private WikipediaDriverManager dmc;

	private String[] langs;
	private int year;
	private String[] filesID;
	private File folder;
	private String prefix;

	// Structure to store the ids available in every language
	private Map<String, List<Integer>> ids = new HashMap<String, List<Integer>>();

	private static LumpLogger logger = new LumpLogger(
			CommonArticlesFinder.class.getSimpleName());

	/**
	 * Instantiates the object with the provided languages. The language with less articles 
	 * is the base to find pairs/tuples in the intersection. 
	 * The year must be one of the available dumps. 
	 * Tables "langlinks" and "page" are necessary
	 * 
	 * Languages are required in the Wikipedia code format (e.g. en for English and es 
	 * for Spanish). The DB connections are invoked according to these parameters.
	 *  
	 * @param langs[]
	 * @param year
	 * @param folder 
	 * @throws Throwable 
	 */		
	public CommonArticlesFinder(String[] langs, int year, String[] filesID, File folder) throws Throwable	{		

		dmc = new WikipediaDriverManager();
		dmc.createConnection();

		this.langs = langs;
		this.year = year;
		this.filesID = filesID;
		this.folder = folder;
		
		//This pattern should agree with the articlesFileName in the config file
		Pattern r = Pattern.compile("(\\w+\\.\\d+\\.)\\d+.articles");
		String tmp = "";
		for (String fileID : filesID) {
			Matcher m = r.matcher(fileID);
			if (m.find()) {
				tmp = tmp + m.group(1);
			}
		}
		this.prefix = tmp;
		
	}


	/**
	 * Example for using the class
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Throwable{

		// Defaults
		int year = 2015;
		String[] langs = {"en", "fr"};
		String path = System.getProperty("user.home") + FileIO.separator + "pln"+
				 FileIO.separator + "wikipedia" + FileIO.separator + "articles" + FileIO.separator+ year + FileIO.separator;
		//String path = System.getProperty("user.home") + FileIO.separator + "wiki"+
		//		 FileIO.separator + "tacardi" + FileIO.separator + year + FileIO.separator ; //cluster
		File folder = new File (path);
		String[] filesID = {"", ""};
		//Informatica 2015
		//filesID[0] = path.concat("ca.13684.5.articles");
		//filesID[0] = path.concat("en.691117.6.articles");
		filesID[0] = path.concat("es.65343.6.articles");
		filesID[1] = path.concat("eu.5784.6.articles");

		//Invoke an instance of the finder
		CommonArticlesFinder artFinder = new CommonArticlesFinder(langs, year, filesID, folder);
		artFinder.checkAllTablesAvailable();
		artFinder.loadIDs();

		String smallestLang = artFinder.lookForMinimumNumber();
		artFinder.findIntersection(smallestLang);
		String largestLang = artFinder.lookForMaximumNumber();
		artFinder.findUnion(largestLang);
	
		artFinder.closeConnection();

	}
	
	/** 
	 * Loads the lists of IDs stored in the input files {@code filesID}
	 */
	private void loadIDs() {
		int i = 0;
		for (String fileID : filesID) {
			List<Integer> list = null;
			try {
				FileInputStream fis = new FileInputStream(new File(fileID));
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String line = null;
                list = new ArrayList<Integer>();   
				while ((line = br.readLine()) != null) { //for every article
					int id = Integer.parseInt(line.trim());
					list.add(id);
				}
				br.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("IO Error with file " + fileID + " (@loadIDs())");
			}
			ids.put(langs[i], list);
			i++;
		}
		logger.info("Files loaded");
	}

	
	/**
	* Looks for the articles that appear in any of the selected languages {@code langs}
	* and builds a set with its union. So, for other languages than the original, if the 
	* articles exist in the DB but do not appear in the ID files, they are added.
	* Generates the list {@code listUnion} from which a file is printed by  
	* {@code getAndPrintAllInfoUnionIDs(List<Integer> listUnion, String largestLang)}
	* 
	* @param largestLang
    */
	public void findUnion(String largestLang) {
		
		dmc.setDB(pairs_db);
		
		String titlePageTlarge = WikipediaDBdata.getPageTableName(largestLang, year);
		int missLinks = 0;
		int countCommon = 0;
		List<Integer> listUnion = new ArrayList<Integer>();

		// for every language
		for (String lang : langs) {
			logger.info("Looking for common IDs and conversion from language " + lang + " into " + largestLang);
			String titleLanglinksT = WikipediaDBdata.getLanglinksTableName(lang, year); 
			Iterator<Integer> iter = ids.get(lang).iterator();
			
			// for every ID within the language
			while (iter.hasNext()){    
				int id = iter.next();
				ResultSet rsLangs = null;
				String queryLangs = 
						"SELECT `ll_lang`  FROM `" + titleLanglinksT + "` WHERE `ll_from` = " + id;
				try {
					rsLangs = dmc.runStatement(queryLangs);
					List<String> languages = new ArrayList<String>();
					while (rsLangs.next()){
						  languages.add(rsLangs.getString("ll_lang"));
					}
					// Look if an ID is in all the remaining languages according to langlinks table
					boolean isIntersection = true;
					for (String lng : langs) {
						if (!languages.contains(lng) && !lng.equals(lang)) {
							isIntersection = false;
							break;
						}
					}
					// If belongs to the intersection and is in the largestLang let's keep the ID
					if (isIntersection == true && lang.equals(largestLang)){
						listUnion.add(id);
						countCommon++;
					// If belongs to the intersection and is another language look for the ID in the largestLang
					} else if (isIntersection == true && !lang.equals(largestLang)){
						//SELECT `ll_title` FROM `wikica_2013_langlinks` WHERE `ll_from` = id AND `ll_lang` = 'largestLang'   
						String query1 = "SELECT `ll_title`  FROM `" + titleLanglinksT + 
								"` WHERE `ll_from` = " + id + " AND `ll_lang` = '" + largestLang + "'";
						ResultSet rsq1 = dmc.runStatement(query1);
						if(rsq1.next()) {
							String title = WikipediaDBdata.getformatWPtitle(rsq1, "ll_title");
							//SELECT page_id FROM `wikien_2013_page` WHERE `page_title` = 'title' AND `page_namespace` = 0
							String query2 = "SELECT `page_id` FROM `" + titlePageTlarge + 
									"` WHERE `page_title` = \"" + title + "\" AND `page_namespace` = 0";
							ResultSet rsq2 = dmc.runStatement(query2);
							if (rsq2.next()) {   
								int langID = Integer.parseInt(rsq2.getString("page_id"));
								//We check this ID is not already there because of a previous language
								if (!listUnion.contains(langID)) {
									listUnion.add(langID);
									countCommon++;
								}
							}else { // titleLanglinksT has some inexistent links
								missLinks++;
							} 
						} else {  //these are in fact articles with no title
							missLinks++;
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
					logger.error("Error in querying the database (@findUnion())");
				}
				
			} //fi while elements per language
		} // fi for languages
		int moreMissLinks = getAndPrintAllInfoUnionIDs(listUnion, largestLang);
		missLinks = missLinks + moreMissLinks;
		countCommon = countCommon - moreMissLinks;
		logger.info("Articles: " + countCommon  + " common, " +	missLinks + " missLinks in the DB");

	}

	
	/**
	 * Given a list {@code listUnion} with the IDs of all the articles that are common to
	 * the set of languages {@code langs}, the method looks for the ID and title in all
	 * the languages. The initial list has the IDs in the {@code largestLang} language.
     * "ID \t title \t" of the articles is printed in a file with the information for 
     * all the languages concatenated in a row.
     * 
	 * @param listUnion
	 * @param largestLang
	 * @return missLinks
	 * 			int with the number of articles with a missing link to another language
	 */
    private int getAndPrintAllInfoUnionIDs(List<Integer> listUnion, String largestLang) {

		String titlePageTlarge = WikipediaDBdata.getPageTableName(largestLang, year);
		String titleLanglinksTlarge = WikipediaDBdata.getLanglinksTableName(largestLang, year); 

		// largestLang is removed from the vector of languages to be compared against
		int numberLangs = langs.length;
		String[] langsReduced = new String[numberLangs-1];
		int i = 0;
		for (String lang : langs) {
			if (!lang.equals(largestLang)){	
		        langsReduced[i] = lang;
				i++;
		    }		    	
		}

        String unionList = "";
		int j = 0;
		int missLinks = 0;
		Iterator<Integer> iter = listUnion.iterator();
		while (iter.hasNext()){    
			int id = iter.next();
	        String unionTmp[] = new String[numberLangs];
			boolean include = true;
	        try {
			    // Complete the largest Wikipedia info: title
			    //SELECT `page_title`FROM `wikien_2013_page` WHERE `id` = id AND `page_namespace` = 0
				String queryS = "SELECT `page_title` FROM `" + titlePageTlarge + 
						"` WHERE `page_id` = \"" + id + "\" AND `page_namespace` = 0";
				ResultSet rsqS = dmc.runStatement(queryS);
				if (rsqS.next()){
					String title = WikipediaDBdata.getformatWPtitle(rsqS, "page_title");
					if (title.isEmpty()) {missLinks++; include = false;}
					else {unionTmp[numberLangs-1]= id + "\t" + title;}
				} else{ // titleLanglinksT has some inexistent links
					missLinks++;
					include = false;
				}

				// Look for all the information of the other editions
				for (i=0; i<langsReduced.length; i++) {
					unionTmp[i] = "";
					String titPageTab = WikipediaDBdata.getPageTableName(langsReduced[i], year);
					//logger.warn("lang " +langsReduced[i] + "i "+ i);
					//SELECT `ll_title` FROM `wikien_2013_langlinks` WHERE `ll_from` = id AND `ll_lang` = 'langsReduced[i]'   
					String query1 = "SELECT `ll_title`  FROM `" + titleLanglinksTlarge + 
						"` WHERE `ll_from` = " + id + " AND `ll_lang` = '" + langsReduced[i] + "'";
					ResultSet rsq1 = dmc.runStatement(query1);
					if(rsq1.next()) {
						String title = WikipediaDBdata.getformatWPtitle(rsq1, "ll_title");
						//SELECT page_id FROM `wikica_2013_page` WHERE `page_title` = 'title' AND `page_namespace` = 0
						String query2 = "SELECT `page_id` FROM `" + titPageTab + 
								"` WHERE `page_title` = \"" + title + "\" AND `page_namespace` = 0";
						ResultSet rsq2 = dmc.runStatement(query2);
						if (rsq2.next()) {   
							String langID = rsq2.getString("page_id");
							unionTmp[i] = langID + "\t" + title;
						}else { // titleLanglinksT has some inexistent links
							missLinks++;
							include = false;
						} 
					} else {missLinks++; include = false; break;}
				} //fifor remaining languages
				
				// If all links are available, format into a string and print 
				if(include) {
					j++;
					unionList = unionList.concat(unionTmp[numberLangs-1]).concat("\t");
					for (i=0; i<numberLangs-1; i++) {
						unionList = unionList.concat(unionTmp[i]).concat("\t");
					}	
					unionList = unionList.concat("\n");
					if (j%100 == 0) { 
						boolean firstTime = j == 100 ? true : false;
						logger.info("Common articles up to now " + j);
						printArticlesIDFile(prefix+"union", unionList, firstTime);
							unionList = "";
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error("Error in querying the database (@getAndPrintAllInfoUnionIDs())");
			}
			
		} //fi tots IDs
		printArticlesIDFile(prefix+"union", unionList, false); // print last round of articles
		
		return missLinks;
	}

    /**
     * Looks for the articles that appear in all the selected languages {@code langs}
     * simultaneously. If they exist in the DB but do not appear in the files with the
     * IDs, the articles are discarded.
     * "ID \t title \t" of the articles is printed in a file with the information for 
     * all the languages concatenated in a row.
     * 
     * @param smallestLang
     */
	public void findIntersection(String smallestLang) {
		dmc.setDB(pairs_db);

		String titlePageT = WikipediaDBdata.getPageTableName(smallestLang, year);
		String titleLanglinksT = WikipediaDBdata.getLanglinksTableName(smallestLang, year); 
		
		// smallestLang is removed from the vector of languages to be compared against
		int numberLangs = langs.length;
		String[] langsReduced = new String[numberLangs-1];
		int i = 0;
		for (String lang : langs) {
			if (!lang.equals(smallestLang)){	
		        langsReduced[i] = lang;
				i++;
		    }		    	
		}

		String intersectionList = "";
		String intersectionListLang[] = new String[numberLangs];
		for (i=0; i<numberLangs; i++) { 
			intersectionListLang[i] = "";
		}
		
		try {
			int countCommon = 0;
			int missLinks = 0;
			int numNotAligned = 0;
			Iterator<Integer> iter = ids.get(smallestLang).iterator();
			while (iter.hasNext()){    
				int id = iter.next();
				// Look for the languages that have the articles from smallestLang
				ResultSet rsLangs = null;
				String queryLangs = 
						"SELECT `ll_lang`  FROM `" + titleLanglinksT + "` WHERE `ll_from` = " + id;
				rsLangs = dmc.runStatement(queryLangs);
				List<String> languages = new ArrayList<String>();
				while (rsLangs.next()){
					  languages.add(rsLangs.getString("ll_lang"));
				}
				
				// Look if an ID is in all the remaining languages according to langlinks table
				boolean isIntersection = true;
				for (String lang : langsReduced) {
					if (!languages.contains(lang)) {
						isIntersection = false;
						break;
					}
				}

				// If belongs to the intersection
				if (isIntersection == true){
					// Include the smallest wikipedia
					// but if there is a missLinks in one language, none must be included (intersectionTmp)
					boolean include = true;
			        String intersectionTmp[] = new String[numberLangs];
					//SELECT `page_title`FROM `wikieu_2013_page` WHERE `id` = id AND `page_namespace` = 0
					String queryS = "SELECT `page_title` FROM `" + titlePageT + 
							"` WHERE `page_id` = \"" + id + "\" AND `page_namespace` = 0";
					ResultSet rsqS = dmc.runStatement(queryS);
					if (rsqS.next()){
						String title = WikipediaDBdata.getformatWPtitle(rsqS, "page_title");
						intersectionTmp[numberLangs-1]= id + "\t" + title;
					} else{ // titleLanglinksT has some inexistent links but this shouldn't happen for this language
						include = false;
						missLinks++;
						continue;
					}
					// Include all the other editions
					for (i=0; i<langsReduced.length; i++) {
						intersectionTmp[i] = "";
						String titPageTab = WikipediaDBdata.getPageTableName(langsReduced[i], year);
						//SELECT `ll_title` FROM `wikica_2013_langlinks` WHERE `ll_from` = id AND `ll_lang` = 'langsReduced[i]'   
						String query1 = "SELECT `ll_title`  FROM `" + titleLanglinksT + 
								"` WHERE `ll_from` = " + id + " AND `ll_lang` = '" + langsReduced[i] + "'";
						ResultSet rsq1 = dmc.runStatement(query1);
						rsq1.next();
						String title = WikipediaDBdata.getformatWPtitle(rsq1, "ll_title");
						//SELECT page_id FROM `wikica_2013_page` WHERE `page_title` = 'title' AND `page_namespace` = 0
						String query2 = "SELECT `page_id` FROM `" + titPageTab + 
								"` WHERE `page_title` = \"" + title + "\" AND `page_namespace` = 0";
						ResultSet rsq2 = dmc.runStatement(query2);
						if (rsq2.next()) {   
							String langID = rsq2.getString("page_id");
							if (ids.get(langsReduced[i]).contains(Integer.parseInt(langID))) {
								intersectionTmp[i] = langID + "\t" + title;
							} else { // articles can exist in all languages but haven't been extracted and
								     // therefore are not available in the input file
								include = false;
								numNotAligned++;
								break;
							}
						}else { // titleLanglinksT has some inexistent links
							include = false;
							missLinks++;
							break;
						} 
					} //fifor remaining languages

					// print the ones to include
					if(include) {
						countCommon++;
						for (i=0; i<numberLangs-1; i++) {
							//intersectionListLang[i] = intersectionListLang[i].concat(intersectionTmp[i]).concat("\n");
							intersectionList = intersectionList.concat(intersectionTmp[i]).concat("\t");
						}	
						intersectionList = intersectionList.concat(intersectionTmp[numberLangs-1]).concat("\n");
						if (countCommon%100 == 0) { 
							boolean firstTime = countCommon == 100 ? true : false;
							logger.info("Common articles up to now " + countCommon);
							printArticlesIDFile(prefix+"intersection", intersectionList, firstTime);
							intersectionList = "";
						}
					}					
				} //fi intersection
			}
			printArticlesIDFile(prefix+"intersection", intersectionList, false); // print last round of articles
			logger.info("Articles: " + countCommon  + " common, " + numNotAligned + " existent but not common, " +
					missLinks + " missLinks in the DB");
			
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("MySQL error (@findIntersection())");
		}		
		
	}


	/**
	 * Given the list of articles for every language {@code String[] filesID} the
	 * language from {@code String[] langs} with less articles is returned.
	 * The position of the language and the file must correspond in the arrays.
	 * TODO is this better than extracting the language from the name of the file?
	 * 
	 * @return
	 *   String minLang
	 */
	public String lookForMinimumNumber() {
		
		String minLang = null;
		int i = 0;
		int val = Integer.MAX_VALUE;
		int valAnt = Integer.MAX_VALUE;
		for (String fileID : filesID) {
			try {
				val = FileIO.fileCountLines(new File(fileID));
				if (val < valAnt){
					minLang = langs[i];
					valAnt = val;
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("IO Error with file " + fileID + " (@lookForMinimumNumber())");
			}
			i++;
		}
		logger.info("The wikipedia edition with less articles is " + minLang + " ("  + valAnt + " arts)");
		return minLang;
	}
	
	
	/**
	 * Given the list of articles for every language {@code String[] filesID} the
	 * language from {@code String[] langs} with more articles is returned.
	 * The position of the language and the file must correspond in the arrays.
	 * 
	 * @return
	 *   String maxLang
	 */
	public String lookForMaximumNumber() {
		
		String maxLang = null;
		int i = 0;
		int val = 0;
		int valAnt = 0;
		for (String fileID : filesID) {
			try {
				val = FileIO.fileCountLines(new File(fileID));
				if (val > valAnt){
					maxLang = langs[i];
					valAnt = val;
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("IO Error with file " + fileID + " (@lookForMaximumNumber())");
			}
			i++;
		}
		logger.info("The wikipedia edition with more articles is " + maxLang + " ("  + valAnt + " arts)");
		return maxLang;
	}

	
	public void closeConnection() {
		try {
			dmc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("MySQL Error closing the connection");
		}
	}


	/** 
	 * Checks if all the tables needed are in the database. Exits otherwise.
	 */
	public void checkAllTablesAvailable() {
    	
		for (String lang : langs) {
			String titlePage = WikipediaDBdata.getPageTableName(lang, year);
			String titleLanglinks = WikipediaDBdata.getLanglinksTableName(lang, year); 

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
	 * Prints a string with articles or appends it to the file if it exists 
	 * @param prefix
	 * @param articles
	 * @param firstTime
	 */
	private void printArticlesIDFile(String fileName, String articles, boolean firstTime) {
		//String fileName = prefix + year + "." + StringUtils.join(langs, "") + ".txt";
		try {
			if(firstTime) {
				FileIO.stringToFile(new File(folder,fileName), articles, false);				
			} else {
				FileIO.appendStringToFile(new File(folder,fileName), articles, false);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.warn("There was an error printing the file " + fileName);
		}			
	}

	
	/** getters */
	public String getPrefixOutputFile()
	{
		return prefix;
	}


}
