package cat.lump.aq.basics.log;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;


/**
 * A link to the log4j different configurations. It allows for invoking a 
 * logger either for console, file, or both console and file output.
 * 
 * TODO indeed right now only the console option is available. 
 * 
 */
public class LumpLogger {

	/**Logger for the application*/
	private static Logger log;
	
	/**Initialise the logger with some label identifying the process
	 * @param processLabel
	 */
	public LumpLogger(String processLabel){
		String name = new SimpleDateFormat("'wt-'yyyyMMdd-hhmmss").format(new Date());
		System.setProperty("logFilename", name);

		ConfigurationSource source = new ConfigurationSource();
		String logConfigurationFile =  "prop/log4j_console.xml";
		source.setLocation(logConfigurationFile);
		source.setFile(new File(logConfigurationFile));		
		source.setInputStream(getClass().getResourceAsStream(logConfigurationFile));
		
		Configurator.initialize(null, source);
		
		log = LogManager.getLogger(processLabel);
//		log.entry();
				//processLabel);		
	}	
	
    /**Prints a log message
	 * @param message
	 */
	public void info(String message){		
		log.info(message);
	 }	
	
	public void warn(String message){
		log.warn(message);
	}
	
	public void error(String message){
		log.error(message);
	}
	
	
	/**Stops the program execution giving an error message.
	 * @param message
	 */
	public void errorEnd(String message){
		log.fatal(message);
		System.exit(1);
	}
		
//	/**Stops the program execution giving an error message and the 
//	 * options to call the program from the CLI. 
//	 * @param message the required error message
//	 * @param options
//	 */
//	public void errorEnd(String message, Options options){
//		HelpFormatter helpFormatter = new HelpFormatter();
//		
//		log.fatal(message);
//		helpFormatter.printHelp( "SimilarityESA", options);
//		System.exit(1);
//	}	
	
	public static void main(String[] args){
		LumpLogger logger = new LumpLogger( LumpLogger.class.getName() );
		logger.info("kk");
		logger.warn("2kk");
		logger.error("3kk");
	}
        
//        public static void printinfo ( String msg ){
//            System.out.println(msg);
//        }
//        public static void printerror ( String msg ){
//            System.err.println(msg);
//        }        
    
}
