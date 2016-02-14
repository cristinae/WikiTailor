package cat.lump.aq.basics.check;

public final class CheckFailedError extends Error{
	private static final long serialVersionUID = -3247792270354884530L;

//	private static final long serialVersionUID = CheckFailedError
	
	CheckFailedError() {super();}
	CheckFailedError(String message) {super(message);}
	CheckFailedError(Throwable cause) {super(cause);}

}
