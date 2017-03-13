package uk.ac.ic.sph.pcph.iccp.fhsc.enums;

public enum PersistenceUnitEnum {
	
	/*
	 * FHSC_MANAGEMENT is the FHSC Web management database
	 * 
	 * FHSC_DATABASE_WAREHOUSE is the FHSC database warehouse
	 * 
	 * both databases will be independent of each other but can be
	 * accessed from the same web application
	 */

	FHSC_MANAGEMENT, FHSC_DATABASE_WAREHOUSE
}
