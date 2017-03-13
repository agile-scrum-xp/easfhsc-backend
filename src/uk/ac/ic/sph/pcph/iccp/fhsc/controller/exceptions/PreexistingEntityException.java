package uk.ac.ic.sph.pcph.iccp.fhsc.controller.exceptions;

public class PreexistingEntityException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 319588777448027550L;
	public PreexistingEntityException(String message, Throwable cause) {
        super(message, cause);
    }
    public PreexistingEntityException(String message) {
        super(message);
    }
}
