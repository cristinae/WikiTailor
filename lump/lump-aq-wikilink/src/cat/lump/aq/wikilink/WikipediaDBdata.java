package cat.lump.aq.wikilink;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;

import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.wikilink.connexion.WikipediaDriverManager;

/**
 * Utilities for querying the Wikipedia DB and dealing with its data.
 * Methods assume a concrete format for the data, it may be different
 * for other users.
 * 
 * @author cristina
 * @since Feb 9, 2015
 */
public class WikipediaDBdata {

	private static LumpLogger logger = new LumpLogger(
			WikipediaDBdata.class.getSimpleName());

	
	/**
	 * Given a {@code ResultSet} object extracts from column {@code colName} and formats a 
	 * Wikipedia title as expected to be found in table page 
	 * @param rs
	 * @param colName
	 * @return title
	 * 		   Modified input string
	 */
	public static String getformatWPtitle(ResultSet rs, String colName){
		String title = "";
		try {
			title = new String (rs.getBytes(colName), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.warn("Encodig error (@getFormatWPTitle())");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warn("MySQL error (@getFormatWPTitle())");
		}
		title = title.replace(" ", "_");
		String[] parts = title.split(":");
		if (parts.length > 1) {
			title = parts[1];
		} 
		//escapeJava works, escapeSql don't
		//Problem:utf8 is broken with escapeJava
		//title = StringEscapeUtils.escapeJava(title);
		title = title.replaceAll("\"","\\\\\"");
		
		return title;
	}

	/**
	 * Retrieves the title of an article with ID {@code id} from table "page"
	 * The connection must be ready before.
	 * The existence of table "page" must be checked before.
	 * 
	 * @param id
	 * @param language
	 * @param year
	 * @param dmc
	 * @return title
	 * 			String with the title of the article corresponding to the ID
	 */
	public static String getformatTitleGivenID(int id, String language, int year, WikipediaDriverManager dmc){
		String title = "";
		String pageTable = WikipediaDBdata.getPageTableName(language, year);

		String query = "SELECT `page_title` FROM `" + pageTable + "` WHERE `page_id` = " + id;
		ResultSet rsq;
		try {
			rsq = dmc.runStatement(query);
			if(rsq.next()) {
				title = getformatWPtitle(rsq, "page_title");
			}
		} catch (SQLException e) {
				e.printStackTrace();
				logger.warn("MySQL error (@getformatTitleGivenID)");
		}

		return title;
	}

	/**
	 * Looks for a {@code resolvedId} given the original {@code id} only in case it 
	 * corresponds to a redirect. Chains of redirects are also followed.
	 * The connection must be ready before.
	 * The existence of the tables "page" and "redirect" must be checked before.
	 * 
	 * @param id
	 * @param type
	 * @param language
	 * @param year
	 * @param dmc
	 * @return resolvedId
	 * 			An int with the ID of the redirection
	 */
	public static int resolveIfRedirect(int id, int type, String language, int year, WikipediaDriverManager dmc){

		int resolvedId = id;
		int resolvedIdAnt = id;
		
		try {
			if (isRedirect(id, language, year, dmc)){
				String pageTable = getPageTableName(language, year);
				String redirectTable = getRedirectTableName(language, year);
				//Step 1: look for the title of the redirection
				String resolvedTitle = "";
				String query1 = "SELECT `rd_title` FROM `" + redirectTable + "` WHERE `rd_from` = " + id;
				ResultSet rsq1 = dmc.runStatement(query1);
				if (rsq1.next()) {  
					resolvedTitle = getformatWPtitle(rsq1, "rd_title");
				} else {
					logger.warn(id +" is a redirect but the title of the redirection is not found.");
				}
				//Step 2: look for the id corresponding to this title
				String query2 = "SELECT `page_id` FROM `" + pageTable + 
						"` WHERE `page_title` = \"" + resolvedTitle + "\" AND `page_namespace` = " + type;
				ResultSet rsq2 = dmc.runStatement(query2);
				if (rsq2.next()) { 
					resolvedId = rsq2.getInt("page_id");
					//logger.info(id + " is a redirect, " + resolvedTitle);
					if ((resolvedId != resolvedIdAnt) && isRedirect(resolvedId, language, year, dmc)){
						logger.info(id + " is a double redirect");
						resolvedId = resolveIfRedirect(resolvedId, type, language, year, dmc);
						resolvedIdAnt = resolvedId;
					}	
				} else {
					logger.warn(id +" is a redirect but ID of the redirection is not found.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warn("MySQL error (resolveIfRedirect)");
		}
		
		return resolvedId;
	}


	/**
	 * Looks for a {@code resolvedId} given an original redirect {@code id}. Chains of 
	 * redirects are also followed.
	 * The connection must be ready before.
	 * The existence of the tables "page" and "redirect" must be checked before.
	 * 
	 * @param id
	 * @param type
	 * @param language
	 * @param year
	 * @param dmc
	 * @return resolvedId
	 * 			An int with the ID of the redirection
	 */
	public static int resolveRedirect(int id,  int type, String language, int year, WikipediaDriverManager dmc){

		int resolvedId = id;
		int resolvedIdAnt = id;
		
		try {
			String pageTable = getPageTableName(language, year);
			String redirectTable = getRedirectTableName(language, year);
			//Step 1: look for the title of the redirection
			String resolvedTitle = "";
			String query1 = "SELECT `rd_title` FROM `" + redirectTable + "` WHERE `rd_from` = " + id;
			ResultSet rsq1 = dmc.runStatement(query1);
			if (rsq1.next()) { 
				resolvedTitle = getformatWPtitle(rsq1, "rd_title");
			} else {
				logger.warn("The title of the redirected article is not found for ID " + id );
			}
			//Step 2: look for the id corresponding to this title
			String query2 = "SELECT `page_id` FROM `" + pageTable + 
					"` WHERE `page_title` = \"" + resolvedTitle + "\" AND `page_namespace` = " + type;
			ResultSet rsq2 = dmc.runStatement(query2);
			if (rsq2.next()) { 
				resolvedId = rsq2.getInt("page_id");
				if ((resolvedId != resolvedIdAnt) && isRedirect(resolvedId, language, year, dmc)){
					logger.info(id + " is a double redirect");
					resolvedId = resolveIfRedirect(resolvedId, type, language, year, dmc);
					resolvedIdAnt = resolvedId;
				}	
			} else {
				logger.warn("The ID of the redirected article is not found for ID " + id );
			}			
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warn("MySQL error (resolveRedirect)");
		}
		
		return resolvedId;
	}

	/**
	 * Looks in the "page" table of the corresponding {@code language} and 
	 * {@code year} if the {@code id} is a redirect. 
	 * The connection must be ready before.
	 * The existence of the table must be checked before.
	 * 
	 * @param id
	 * @param language
	 * @param year
	 * @param dmc
	 * @return redirect
	 * 			A boolean with the status
	 * @throws SQLException
	 */
	public static boolean isRedirect(int id, String language, int year, WikipediaDriverManager dmc) 
			throws SQLException{
		boolean redirect = false;
		
		String pageTable = getPageTableName(language, year);
		String query = "SELECT `page_id` FROM `" + pageTable + 
								"` WHERE `page_id` = " + id + " AND `page_is_redirect` = 1";
		ResultSet rsq = dmc.runStatement(query);
		if (rsq.next())	redirect = true;
		
		return redirect;
	}

	/**
	 * Generates the name of the table page as stored in the database for a 
	 * given language and year
	 * @param language
	 * @param year
	 * @return titlePage
	 * 		a String with the name of the table
	 */
	public static String getPageTableName(String language, int year){
		String titlePage = "wiki" + language + "_" + year + "_page";
		return titlePage;
	}
	
	/**
	 * Generates the name of the table langlinks as stored in the database for a 
	 * given language and year
	 * @param language
	 * @param year
	 * @return titleLanglinks
	 * 		a String with the name of the table
	 */
	public static String getLanglinksTableName(String language, int year){
		String titleLanglinks = "wiki" + language + "_" + year + "_langlinks";
		return titleLanglinks;
	}
	
	/**
	 * Generates the name of the table redirect as stored in the database for a 
	 * given language and year
	 * @param language
	 * @param year
	 * @return titleRedirect
	 * 		a String with the name of the table
	 */
	public static String getRedirectTableName(String language, int year){
		String titleRedirect = "wiki" + language + "_" + year + "_redirect";
		return titleRedirect;
	}

}
