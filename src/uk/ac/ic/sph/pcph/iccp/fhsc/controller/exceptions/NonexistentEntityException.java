package uk.ac.ic.sph.pcph.iccp.fhsc.controller.exceptions;

public class NonexistentEntityException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4281489705450483645L;
	
	public NonexistentEntityException(String message, Throwable cause) {
        super(message, cause);
    }
	
    public NonexistentEntityException(String message) {
        super(message);
    }
}
