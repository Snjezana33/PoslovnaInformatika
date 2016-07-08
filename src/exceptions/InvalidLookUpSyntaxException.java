package exceptions;

public class InvalidLookUpSyntaxException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidLookUpSyntaxException() {
		
	}
	
	public InvalidLookUpSyntaxException(String message){
		super(message);
	}

}
