package uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import uk.ac.ic.sph.pcph.iccp.fhsc.controller.LoginJpaController;
import uk.ac.ic.sph.pcph.iccp.fhsc.controller.UserJpaController;
import uk.ac.ic.sph.pcph.iccp.fhsc.controller.exceptions.NonexistentEntityException;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Login;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.User;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.FHSCUserCategory;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.FHSCUserStatus;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.PersistenceUnitEnum;
import uk.ac.ic.sph.pcph.iccp.fhsc.qualifier.PersistenceUnitQualifier;
import uk.ac.ic.sph.pcph.iccp.fhsc.utility.HashingUtility;

@Named
@ApplicationScoped
public class UserUtility {

	@Inject
	LoginUtility loginUtility;

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

	public boolean userApproval(String userID, String decision, String comment, String category)
			throws NonexistentEntityException, Exception {
		try {
			Integer userIDInt = Integer.parseInt(userID);
			User userTemp = getUser(userIDInt);

			if (decision.equalsIgnoreCase("Approve")) {
				userTemp.setStatus(FHSCUserStatus.ACTIVE.toString());
				if (category.equalsIgnoreCase("Coordinator")) {
					userTemp.setCategory(FHSCUserCategory.COORDINATOR.toString());
				} else if (category.equalsIgnoreCase("Lead investigator")) {
					userTemp.setCategory(FHSCUserCategory.LEAD_INVESTIGATOR.toString());
				} else {
					userTemp.setCategory(FHSCUserCategory.CONTRIBUTING_INVESTIGATOR.toString());
				}

			} else if (decision.equalsIgnoreCase("Reject")) {
				userTemp.setStatus(FHSCUserStatus.DISALLOWED.toString());
				userTemp.setCategory(FHSCUserCategory.DISQUALIFIED.toString());
			} else {
				userTemp.setCategory(FHSCUserCategory.DISQUALIFIED.toString());
				userTemp.setStatus(FHSCUserStatus.DISALLOWED.toString());
			}

			userTemp.setCoordinatorComment(comment);

			new UserJpaController(fhsc_management_emf).edit(userTemp);

		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			throw new Exception("User not found");
		} catch (NonexistentEntityException nee) {
			nee.printStackTrace();
			throw new Exception("User not found");
		}

		return true;
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

	public List<User> getCurrentUser(String username) {
		Login login = loginUtility.getLoginForUsername(username);
		List<User> currentUser = new ArrayList<>();
		currentUser.add(login.getUserId());

		return currentUser;
	}

	public void getAllRecentUsers() {

	}
	
	public void secureUser(User user)throws Exception
	{
		user.setAnswerOne(new HashingUtility().hashString(user.getAnswerOne(), user.getFirstName()));
		user.setAnswerTwo(new HashingUtility().hashString(user.getAnswerTwo(), user.getFirstName()));
		new UserJpaController(this.fhsc_management_emf).edit(user);
	}
	
	private List<User> getUsers(FHSCUserStatus status) {
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
