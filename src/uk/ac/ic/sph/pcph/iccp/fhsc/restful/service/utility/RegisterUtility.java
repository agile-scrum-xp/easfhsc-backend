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
		
		//Generate Login for user. For now I assume password will be stored in plain text. 
		//If something goes wrong when creating login, then then user is deleted and registration fail.
		//TODO: We can either send an email now to user with his login details and the encrypt password in database or we can 
		//TODO: send the email later when the user is approved. 
		try {
			loginUtility.createLoginForUser(data);
		} catch (Exception ex) {
			try {
				userController.destroy(data.getUserId());
				System.out.println("RegisterUtility add User failed: ");
				return null;
			} catch (NonexistentEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}

		System.out.println("RegisterUtility add User: " + data.getUserId() + " " + data.getApplicationDate());
		return data;
	}

}
