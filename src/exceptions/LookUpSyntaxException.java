package exceptions;

public class LookUpSyntaxException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public LookUpSyntaxException() {
		
	}
	
	public LookUpSyntaxException(String message){
		super(message);
	}

}
