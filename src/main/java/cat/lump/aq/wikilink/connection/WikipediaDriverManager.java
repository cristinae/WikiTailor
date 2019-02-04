package cat.lump.aq.wikilink.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.wikilink.config.MySQLWikiConfiguration;

/** Adaptation of {@link cat.talp.lump.co.db.DriverManagerClass} from
 * @author albarron to manage the connection to the database. Configuration
 * issues can be found at {@link cat.lump.aq.basics.config.MySQLWikiConfiguration}.
 * The class 
 * 
 * @author cristina 
 * */
public class WikipediaDriverManager {

	private Connection conn;
	private String db; 
	
	private Statement stmt;

	private static LumpLogger logger = new LumpLogger(
			WikipediaDriverManager.class.getSimpleName());

	
	public static void main(String[] args) throws SQLException	{
		WikipediaDriverManager dmc = new WikipediaDriverManager();
		dmc.createConnection();
		dmc.test();
		dmc.close();
	}

	/**Creates the connection to the database
	 * @see {@link #close()}
	 */
	public void createConnection() {
		DB db = new DB();
		conn=db.dbConnect(MySQLWikiConfiguration.mysqlUrl(),
							MySQLWikiConfiguration.sqlUser(),
							MySQLWikiConfiguration.sqlPass());		
	}
		
	/**Tests the connection by displaying the databases
	 * @throws SQLException
	 */
	public void test() throws SQLException{
		Statement s = conn.createStatement();
		ResultSet rs=s.executeQuery ("SHOW DATABASES");	
		while (rs.next())
			logger.info("Testing, table: " + rs.getString (1));       
	}
	
	/** Closes the connection
	 * @throws SQLException 
	 */
	public void close() throws SQLException{
		conn.close();
		logger.info("Connection closed");
	}
	

	/** 
	 * Checks if a table exists in a database
	 * @param tableName
	 * 			String with the name of the table
	 * @param db 
	 * @return exists
	 * @throws SQLException 
	 */
	public boolean tableExists(String tableName, String db) throws SQLException {	
		java.sql.DatabaseMetaData dbmd = conn.getMetaData();
		ResultSet rsmd = dbmd.getTables(db, null, tableName, null);
		boolean exists = false;
		while (rsmd.next()) {
			if (rsmd.getString(3).equalsIgnoreCase(tableName)){
				return true;
			}
		}	
		return exists;				
	}
	
	public void setDB(String db){
		this.db = db;
	}

	public ResultSet runStatement(String statement) throws SQLException{
		
		stmt = conn.createStatement();
		stmt = conn.createStatement();
		stmt.execute("USE " + db);
		ResultSet rs_src = stmt.executeQuery(statement);	
		
		return rs_src;						
	}
	
	public void closeStatement() throws SQLException{
		stmt.close();
	}	
	
}


class DB{
	public DB() {}

	private static LumpLogger logger = new LumpLogger(
			WikipediaDriverManager.class.getSimpleName());

	public Connection dbConnect(String db_connect_string, String db_userid, String db_password)
	{
		try
		{
			// TO CONSIDER
			// nohup.arExtraction.outLoading class `com.mysql.jdbc.Driver'. This is deprecated. 
			// The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered 
			// via the SPI and manual loading of the driver class is generally unnecessary.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = DriverManager.getConnection(db_connect_string, db_userid, db_password);
			logger.info("Connected to DB");
			return conn;

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
};

