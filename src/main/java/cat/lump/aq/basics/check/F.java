package cat.lump.aq.basics.check;

import java.io.File;

/**
 * A class that contains methods to check files and directories 
 * 
 * @author alberto
 *
 */
public class F {
	
	/**
	 * throw error if the file cannot be read.
	 * @param file
	 */
	public final static void CAN_READ(final File file, 
			final String message){
		if (! file.canRead()) {
			throw new CheckFailedError(message);
		}
	}

}
