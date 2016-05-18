package cat.lump.aq.basics.check;

/**
 * A class that contains methods to check 
 * 
 * @author alberto
 *
 */
public class CHK {
	
	/**
	 * throw CheckFailedError if false
	 * @param condition
	 */
	public final static void CHECK(final boolean condition){
		if (!condition) {
			throw new CheckFailedError();
		}
	}
	
	/**
	 * throw CheckFailedError if false, displaying the required message
	 * @param condition
	 * @param message
	 */
	public final static void CHECK(final boolean condition, 
									final String message){
		if (!condition) {
			throw new CheckFailedError(message);
		}		
	}
	
	/**
	 * Check that the given object is not null; throws a CheckFailedError
	 * if it is 
	 * @param object
	 */
	public final static void CHECK_NOT_NULL(final Object object){
		if (object == null) {
			throw new CheckFailedError(new NullPointerException());
		}		
	}

}
