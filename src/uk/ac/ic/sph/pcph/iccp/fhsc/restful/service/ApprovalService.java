package uk.ac.ic.sph.pcph.iccp.fhsc.restful.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import freemarker.template.TemplateException;
import uk.ac.ic.sph.pcph.iccp.fhsc.controller.exceptions.NonexistentEntityException;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Login;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.User;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility.LoginUtility;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility.RegisterUtility;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility.UserUtility;
import uk.ac.ic.sph.pcph.iccp.fhsc.utility.ApplicationParameters;
import uk.ac.ic.sph.pcph.iccp.fhsc.utility.EmailUtility;
import uk.ac.ic.sph.pcph.iccp.fhsc.utility.FreemarkerHelper;

@Path("/fhsc/approval")
@Named
@RequestScoped
public class ApprovalService {

	private String serviceMessage = "This is the FHSC Approval service";
	
	@Inject
	private ApplicationParameters applicationParameters;

	@Inject
	private FreemarkerHelper freemarkerHelper;

	@Inject
	private EmailUtility emailUtility;

	@Inject
	private UserUtility userUtility;
	
	@Inject
	private LoginUtility loginUtility;

	public ApprovalService() {
	}

	// http://localhost:8080/fhscServices/rest/fhsc/approval/about
	@GET
	@Path("/about")
	public Response about() {
		String result = "{ \"result\":" + "\"" + serviceMessage + "\" }";
		return Response.status(200).entity(result).build();
	}

	// http://localhost:8080/fhscServices/rest/fhsc/approval
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public Response approval(@FormParam("userID") String userID, @FormParam("decision") String decision, @FormParam("comment") String comment) {
		try{
			userUtility.userApproval(userID, decision, comment);
			
			User user = userUtility.getUser(Integer.parseInt(userID));
			//Generate Login for user. For now I assume password will be stored in plain text. 
			//If something goes wrong when creating login, then then user is deleted and registration fail.
			//TODO: We can either send an email now to user with his login details and the encrypt password in database or we can 
			//TODO: send the email later when the user is approved. 
			try {
				Login login = loginUtility.createLoginForUser(user);
				
				if(decision.equalsIgnoreCase("Approve")) {
					Map<String, Object> dataset = createDatasetForSuccessfulRegistrationEmail(user, login);
					
					String freeMarkerTemplateDir = applicationParameters.getTemplateDirectory();
					String freeMarkerTemplateName = applicationParameters.getUserSuccessfulRegistrationNotificationTemplateFileName();
					String emailSubject = applicationParameters.getUserSuccessfulRegistrationNotificationEmailSubject();
					
					String messageBody = createEmailMessage(dataset, freeMarkerTemplateDir, freeMarkerTemplateName);
					
					sendEmail(emailSubject, messageBody, user.getEmail());
				} else {
					Map<String, Object> dataset = createDatasetForFailedRegistrationEmail(user);
					String freeMarkerTemplateDir = applicationParameters.getTemplateDirectory();
					String freeMarkerTemplateName = applicationParameters.getUserFailedRegistrationNotificationTemplateFileName();
					String emailSubject = applicationParameters.getUserFailedRegistrationNotificationEmailSubject();
					
					String messageBody = createEmailMessage(dataset, freeMarkerTemplateDir, freeMarkerTemplateName);
					
					sendEmail(emailSubject, messageBody, user.getEmail());
				}
			} catch (Exception ex) {
				String result = "{ \"result\":" + "\" User Update is not successful. Please, try later. \"" + "}";
				return Response.status(500).entity(result).build();
			
			}
			return Response.status(200).build();
		} catch (Exception exp) {
			String result = "{ \"result\":" + "\" User Update is not successful. Please, try later. \"" + "}";
			return Response.status(500).entity(result).build();
		}
	}
	
	
	private Map<String, Object> createDatasetForSuccessfulRegistrationEmail(User user, Login login) {

		Map<String, Object> dataset = new HashMap<String, Object>();
		dataset.put("to", user.getTitle() + " " + user.getFirstName() + " " + user.getLastName());

		dataset.put("userName", login.getUserName());
		dataset.put("password", login.getPassword());
		// System.out.println("registrationDate" + " " +
		// user.getApplicationDate() + " " + dataset.get("registrationDate"));

		dataset.put("fhscURL", "la la l al la la");
		dataset.put("from", "EAS FHSC Web");

		return dataset;

	}
	
	private Map<String, Object> createDatasetForFailedRegistrationEmail(User user) {

		Map<String, Object> dataset = new HashMap<String, Object>();
		dataset.put("to", user.getTitle() + " " + user.getFirstName() + " " + user.getLastName());

		dataset.put("comment", user.getCoordinatorComment());
		// System.out.println("registrationDate" + " " +
		// user.getApplicationDate() + " " + dataset.get("registrationDate"));

		dataset.put("fhscURL", "la la l al la la");
		dataset.put("from", "EAS FHSC Web");

		return dataset;

	}

	private String createEmailMessage(Map<String, Object> data, String freeMarkerTemplateDir,
			String freeMarkerTemplateName) {
		String message = "";
		try {
			message = freemarkerHelper.generateMessage(freeMarkerTemplateDir + freeMarkerTemplateName, data);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (TemplateException e) {
			e.printStackTrace();
			return null;
		}

		return message;
	}

	private void sendEmail(String emailSubject, String emailContent, String recipient) {
		try {
			emailUtility.notifyUserAsHTML(emailSubject, emailContent, recipient);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
