package cat.lump.aq.wikilink.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Class to handle the connection variables to the database. The information is 
 * extracted from lump_wiki.properties
 * </br>
 * TODO Should we create a Configuration class with the "default"
 * data (such as username, password, url, etc) and extend it to 
 * include Wikipedia-related info?
 * 
 * @author albarron
 *
 */
public abstract class MySQLWikiConfiguration {
	private static Properties p = new Properties();

	/**File with the the information for the connection to the database. */
	private static final String CONFIG_FILE="/configs/lump_wiki.properties";

	private static String mysql_url;
	protected static String mysql_url_jwpl;
	protected static String mysql_usr;
	protected static String mysql_pss;
	
	protected static String pairs_db;
	private static String allPairs_db;
	private static String db_prefix;
	private static String multi_db;
	private static String multi_table;
	private static String jwplDB_prefix;

//	private static File corpusAcquisPath;

	
	static {		
		Class<MySQLWikiConfiguration> c = MySQLWikiConfiguration.class;
//		try { p.load(c.getResourceAsStream("lump_wikiOLD.properties")); }
//		catch (IOException e) { e.printStackTrace(); }

		try {
			File configPath = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			String configFile = configPath.getParent().toString().concat(CONFIG_FILE);
			InputStreamReader isr = new InputStreamReader(new FileInputStream(configFile), Charset.forName("UTF-8")); 
			p.load(isr);
			isr.close();
		} catch (IOException e) { 
			e.printStackTrace(); 
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		mysql_url_jwpl=p.getProperty("MYSQL_URL_JWPL");
		mysql_url=p.getProperty("MYSQL_URL");
		mysql_usr=p.getProperty("MYSQL_USER");
		mysql_pss=p.getProperty("MYSQL_P");
		
		allPairs_db   = p.getProperty("allpairs_db");
		db_prefix     = p.getProperty("db_prefix");
		multi_db 	  = p.getProperty("multi_db");
		multi_table   = p.getProperty("multi_table");
		jwplDB_prefix = p.getProperty("jwplDBprefix");
		pairs_db	  = p.getProperty("DB_PAIRS");
	}
	
	public static String getPairsDBname(){
		return pairs_db;
	}
	
	public static String getAllPairsDBname(){
		return allPairs_db;
	}

	public static String mysqlUrlJwpl(){
		return mysql_url_jwpl;
	}
	
	/**
	 * The prefix for the Wikipedia SQL dumps containing 
	 * page and langlinks tables.
	 * @return the prefix for the dump
	 */
	public static String getDBprefix(){
		return db_prefix;
	}
	
	public static String getMulti_db(){
		return multi_db;		
	}
	
	public static String getMulti_table(){
		return multi_table;
	}	
	
	public static String getJwplDBprefix(){
		return jwplDB_prefix;
	}
	
	public static String sqlUser(){
		return mysql_usr;
	}
	
	public static String sqlPass(){
		return mysql_pss;
	}
	
	public static String mysqlUrl(){
		return mysql_url;
	}	

}