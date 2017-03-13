package uk.ac.ic.sph.pcph.iccp.fhsc.jpa.utility;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import uk.ac.ic.sph.pcph.iccp.fhsc.enums.PersistenceUnitEnum;
import uk.ac.ic.sph.pcph.iccp.fhsc.qualifier.PersistenceUnitQualifier;

@Named
@ApplicationScoped
public class DatabaseManager {
	
	/**
	 * The factory that produces entity manager.
	 */
	private EntityManagerFactory entityManagerFactory;
	
	public DatabaseManager() {
		System.out.println("StupidClass constructor is called ...!");
//		entityManagerFactory = Persistence.createEntityManagerFactory("fhsc_PU");
		try {
			this.setUp();
		} catch (Exception ex) {
			Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	@Produces
	@PersistenceUnitQualifier(PersistenceUnitEnum.FHSC_MANAGEMENT)
	public EntityManagerFactory getEntityManagerFactory() {
		System.out.println("getEntityManagerFactory() is called ...!");
		return entityManagerFactory;
	}
	
	private void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("fhsc_PU");
	}

}
