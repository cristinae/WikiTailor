package cat.lump.aq.alignment.control;

/**
 * Control the possible errors triggered by the manual text alignment 
 * process. Three identifiers are defined:
 * 
 * <ol start="0">
 * <li> CORRECT. The new pair should be accepted
 * <li> ERROR. The user is trying to assign a sentence to more than 
 * 		one parallel pair or the pair already exists: the pair should 
 * 		<b>not</b> accepted
 * <li> WARNING. The user is trying to assign a sentence to more than 
 * 		one comparable pair; the pair can still be accepted
 * </ol>
 * TODO include more documentation about how this is used
 * 
 * @author albarron
 * @since Sep 2012
 * @version 0.3
 *
 */
public class ErrorControl {

	/**Numerical identifier of the error */
	private int errorID;
	
	
	/**	 */
	private String errorText;
	
	public void setID(int errorID) {
		//CHK.
		this.errorID = errorID;
	}
	
	public int getID() {
		return errorID;
	}
	
	public void setText(String errorText) {
		this.errorText = errorText;
	}
	
	public String getText() {
		return errorText;
	}
	
	/**Sets the kind of behavior and a legend
	 * @param errorID	error identifier: 0=ok; 1=error; 2=warning
	 * @param errorText legend
	 */
	public void setIDtxt(int errorID, String errorText){
		this.errorID = errorID;
		this.errorText = errorText;
	}
	
	/**
	 * @return String label for the status (CORRECT, ERROR, WARNING, 
	 * 	OR UNKNOWN) according to the last operation.
	 */
	public String errorKind(){
		if (errorID == 0)		//everything is correct
			return "CORRECT";
		if (errorID == 1)
			return "ERROR";
		if (errorID == 2)
			return "WARNING";
		
		return "UNKNOWN";
	}
}
