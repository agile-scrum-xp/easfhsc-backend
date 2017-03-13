package uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import uk.ac.ic.sph.pcph.iccp.fhsc.domain.User;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.FHSCUserStatus;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.PersistenceUnitEnum;
import uk.ac.ic.sph.pcph.iccp.fhsc.qualifier.PersistenceUnitQualifier;

@Named
@ApplicationScoped
public class UserUtility {

	@Inject
	@PersistenceUnitQualifier(PersistenceUnitEnum.FHSC_MANAGEMENT)
	private EntityManagerFactory fhsc_management_emf;

	public UserUtility() {

	}

	private EntityManager getEntityManager() {
		return fhsc_management_emf.createEntityManager();
	}

	public List<User> getAllPendingUsers() {

		return getUsers(FHSCUserStatus.PENDING);
	}

	public List<User> getAllRejectedUsers() {
		return getUsers(FHSCUserStatus.DISALLOWED);
	}
	
	public List<User> getAllApprovedUsers() {
		return getUsers(FHSCUserStatus.ACTIVE);
	}
	
	public User getUser(Integer userID) {
		EntityManager em = null;
		User result = null;
		
		try {
			em = getEntityManager();
			em.getTransaction().begin();

			TypedQuery<User> query = em.createNamedQuery("User.findByUserId", User.class);
			query.setParameter("userId", userID);

			result = query.getSingleResult();

			em.getTransaction().commit();

		} catch (Exception nee) {
			nee.printStackTrace();
		} finally {
			if (em != null) {
				em.close();
			}
		}
		
		return result;
	}
	
	public List<User> getAllUsers() {
		EntityManager em = null;
		List<User> result = null;

		try {
			em = getEntityManager();
			em.getTransaction().begin();

			TypedQuery<User> query = em.createNamedQuery("User.findAll", User.class);

			result = query.getResultList();

			em.getTransaction().commit();

		} catch (Exception nee) {
			nee.printStackTrace();
		} finally {
			if (em != null) {
				em.close();
			}
		}
		return result;
	}
	
	public void getAllRecentUsers() {

	}
	
	private List<User> getUsers(FHSCUserStatus status){
		EntityManager em = null;
		List<User> result = null;

		try {
			em = getEntityManager();
			em.getTransaction().begin();

			TypedQuery<User> query = em.createNamedQuery("User.findByStatus", User.class);
			query.setParameter("status", status.toString());

			result = query.getResultList();

			em.getTransaction().commit();

		} catch (Exception nee) {
			nee.printStackTrace();
		} finally {
			if (em != null) {
				em.close();
			}
		}
		return result;
	}

}
