package audaque.com.pbting.cache.exception;

public class NeedsRefreshException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NeedsRefreshException(){}
	 
	public NeedsRefreshException(Object content){
		super(content.toString());
	}
}
