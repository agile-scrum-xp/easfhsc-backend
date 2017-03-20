package uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility;

import java.util.List;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import uk.ac.ic.sph.pcph.iccp.fhsc.controller.LoginJpaController;
import uk.ac.ic.sph.pcph.iccp.fhsc.controller.exceptions.NonexistentEntityException;
import uk.ac.ic.sph.pcph.iccp.fhsc.controller.exceptions.PreexistingEntityException;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Login;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.User;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.PersistenceUnitEnum;
import uk.ac.ic.sph.pcph.iccp.fhsc.qualifier.PersistenceUnitQualifier;
import uk.ac.ic.sph.pcph.iccp.fhsc.utility.HashingUtility;

@Named
@ApplicationScoped
public class LoginUtility {

	
	@Inject
	@PersistenceUnitQualifier(PersistenceUnitEnum.FHSC_MANAGEMENT)
	private EntityManagerFactory fhsc_management_emf;

	public LoginUtility() {
	}

	private EntityManager getEntityManager() {
		return fhsc_management_emf.createEntityManager();
	}

	public User validateLogin(String userName, String password) throws Exception {
		EntityManager em = null;
		Login result = null;

		try {
			em = getEntityManager();
			em.getTransaction().begin();

			TypedQuery<Login> query = em.createNamedQuery("Login.findByUserName", Login.class);
			query.setParameter("userName", userName);

			result = query.getSingleResult();

			System.out.println(result.getUserName() + "  " + result.getPassword());

			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}

		if (result != null)
			if (result.getPassword().equals(new HashingUtility().hashString(password, userName))) {
				User tempUser = getUser(result);
				return tempUser;
			}

		return null;
	}
	
	public Login getLoginForUsername(String userName){
		EntityManager em = null;
		List<Login> results = null; 
		
		try {
			em = getEntityManager();
			
			TypedQuery<Login> query = em.createNamedQuery("Login.findByUserName", Login.class);
			query.setParameter("userName", userName);
			
			results = query.getResultList();
			
		} catch (Exception nee)  {
			nee.printStackTrace();
		} finally {
			if (em != null) {
				em.close();
			}
		}
		if(!results.isEmpty())
			return results.get(0);
		else
			return null;
	}
	
	public Login getLoginForUser(User user){
		EntityManager em = null;
		List<Login> results = null; 
		
		try {
			em = getEntityManager();
			
			TypedQuery<Login> query = em.createNamedQuery("Login.findByUserId", Login.class);
			query.setParameter("userId", user);
			
			results = query.getResultList();
			
		} catch (Exception nee)  {
			nee.printStackTrace();
		} finally {
			if (em != null) {
				em.close();
			}
		}
		if(!results.isEmpty())
			return results.get(0);
		else
			return null;
	}
	
	private User getUser(Login login) {
		EntityManager em = null;
		User result = null; 
		
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			
			TypedQuery<User> query = em.createNamedQuery("User.findByUserId", User.class);
			query.setParameter("userId", login.getUserId().getUserId());

			result = query.getSingleResult();
			
			System.out.println(result.getFirstName() + "  " + result.getLastName());
			
			em.getTransaction().commit();
			
		} catch (Exception nee)  {
			nee.printStackTrace();
		} finally {
			if (em != null) {
				em.close();
			}
		}
		
		return result;
	}

	public Login createLoginForUser(User user) throws PreexistingEntityException, Exception {

		System.out.println("Creating login");
		//create username with initials, the add an increment if initial already taken.
		String userName=attributeNewUserName(user);
		System.out.println("new userName is " + userName);
		//create password composed of letter in lower and uppercase, ascii characters between 58-64 and number
		String password=generatePassword();
		System.out.println("new password is " + password);
			
		Login login=new Login();
		login.setUserName(userName);
		//store password in plain text. On approval this password will have to be 1) send to user by mail 2) hashed in database.
		login.setPassword(password);
		login.setUserId(user);
		
		LoginJpaController loginJpaController=new LoginJpaController(this.fhsc_management_emf);
		loginJpaController.create(login);
		
		return login;
	}
	
	/**
	 * Encrypting login after email is sent to user.
	 * @param login
	 */
	public void secureLogin(Login login)throws Exception
	{
		login.setPassword(new HashingUtility().hashString(login.getPassword(), login.getUserName()));
		new LoginJpaController(this.fhsc_management_emf).edit(login);
	}
	
	public void changeLogin(String username, String password) throws Exception
	{
		Login login=this.getLoginForUsername(username);
		login.setPassword(new HashingUtility().hashString(password, username));
		new LoginJpaController(this.fhsc_management_emf).edit(login);
	}
	
	private String generatePassword(){
		int minimum=48;
		int maximum=90;
		String password="";
		for (int index=0;index<10;index++)
		{
			int asciiNumber=minimum+ (int)(Math.random() * (maximum-minimum));
			String currentChar="" +(char)asciiNumber;
			if(Math.random()>0.5){
				currentChar=currentChar.toLowerCase();
			}
			password+=currentChar;
		}
		return password;
		
	}
	
	private String attributeNewUserName(User user){
		boolean userExists=true;
		String baseusername=((String)user.getFirstName().substring(0,1) + user.getLastName().substring(0,1)).toLowerCase();
		String username="";
		int index=0;
		do{
			index++;
			username=baseusername + index;
			userExists=getLoginForUsername(username)!=null;
		}
		while(userExists);
		return username;
	}
	
	
}
