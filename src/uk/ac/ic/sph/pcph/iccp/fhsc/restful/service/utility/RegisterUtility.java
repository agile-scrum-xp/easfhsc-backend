package uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

import uk.ac.ic.sph.pcph.iccp.fhsc.controller.UserJpaController;
import uk.ac.ic.sph.pcph.iccp.fhsc.controller.exceptions.NonexistentEntityException;
import uk.ac.ic.sph.pcph.iccp.fhsc.controller.exceptions.PreexistingEntityException;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Login;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.User;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.FHSCUserCategory;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.FHSCUserStatus;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.PersistenceUnitEnum;
import uk.ac.ic.sph.pcph.iccp.fhsc.qualifier.PersistenceUnitQualifier;

@Named
@ApplicationScoped
public class RegisterUtility {
	
	@Inject
	private LoginUtility loginUtility;
	
	@Inject
	@PersistenceUnitQualifier(PersistenceUnitEnum.FHSC_MANAGEMENT)
	private EntityManagerFactory fhsc_management_emf;
	
	public RegisterUtility() {
		
	}
	
	public User addUser(User data) {
		
		//should be okay since comment is now mandatory
		if(data.getComment() == null) {
			data.setStatus("No Comments");
		}
		
		if(data.getCategory()== null) {
			data.setCategory(FHSCUserCategory.PENDING.toString());
		}
		
		if(data.getStatus() == null) {
			data.setStatus(FHSCUserStatus.PENDING.toString());
		}
		
		if(data.getApplicationDate() == null) {
			data.setApplicationDate(new java.util.Date());
		}
		
		UserJpaController userController = new UserJpaController(fhsc_management_emf);
		
		userController.create(data);
		
		if(data.getUserId() == null) {
			System.out.println("RegisterUtility add User failed: ");
			return null;
		}

		System.out.println("RegisterUtility add User: " + data.getUserId() + " " + data.getApplicationDate());
		return data;
	}

}
