package uk.ac.ic.sph.pcph.iccp.fhsc.restful.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import freemarker.template.TemplateException;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Login;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.User;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.FHSCUserCategory;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.filters.Secured;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility.RegisterUtility;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility.UserUtility;
import uk.ac.ic.sph.pcph.iccp.fhsc.utility.ApplicationParameters;
import uk.ac.ic.sph.pcph.iccp.fhsc.utility.EmailUtility;
import uk.ac.ic.sph.pcph.iccp.fhsc.utility.FreemarkerHelper;

@Path("/fhsc/user")
@Named
@RequestScoped
public class UserService {

	private String serviceMessage = "This is the FHSC User service";

	@Inject
	private ApplicationParameters applicationParameters;

	@Inject
	private FreemarkerHelper freemarkerHelper;

	@Inject
	private EmailUtility emailUtility;

	@Inject
	private RegisterUtility registerUtility;

	@Inject
	private UserUtility userUtility;

	public UserService() {

	}

	// http://localhost:8080/fhscServices/rest/fhsc/user/about
	@GET
	@Path("/about")
	public Response about() {
		String result = "{ \"result\":" + "\"" + serviceMessage + "\" }";
		return Response.status(200).entity(result).build();
	}

	// http://localhost:8080/fhscServices/rest/fhsc/user/register
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/register")
	public Response registerUser(User data) {

		System.out.println("registerUser service called with data: " + data.getTitle() + " " + data.getFirstName() + " "
				+ data.getLastName());

		User tempUser = registerUtility.addUser(data);

		if (tempUser != null) {
			Map<String, Object> dataset = createDatasetForAdminEmail(tempUser);

			String freeMarkerTemplateDir = applicationParameters.getTemplateDirectory();
			String freeMarkerTemplateName = applicationParameters.getAdminNotificationTemplateFileName();
			String emailSubject = applicationParameters.getAdminNotificationEmailSubject();

			System.out.println(freeMarkerTemplateDir + " " + freeMarkerTemplateName + " " + emailSubject);

			String emailContent = createEmailMessage(dataset, freeMarkerTemplateDir, freeMarkerTemplateName);

			sendEmail(emailSubject, emailContent);

			String result = "{ \"result\":" + "\" Registration is successful \"" + "}";
			return Response.status(200).entity(result).build();
		}

		String result = "{ \"result\":" + "\" Registration is not successful. Please, try later. \"" + "}";
		return Response.status(500).entity(result).build();
	}

	// http://localhost:8080/fhscServices/rest/fhsc/user/pending
	@POST
	@Produces("application/json")
	@Path("/pending")
	@Secured({FHSCUserCategory.COORDINATOR})
	public Response getPendingUsers() {

		List<User> pendingUsers = userUtility.getAllPendingUsers();

		GenericEntity<List<User>> list = new GenericEntity<List<User>>(pendingUsers) {};

		return Response.ok(list).build();
	}

	// http://localhost:8080/fhscServices/rest/fhsc/user/rejected
	@POST
	@Produces("application/json")
	@Path("/rejected")
	@Secured({FHSCUserCategory.COORDINATOR})
	public Response getRejectedUsers() {

		List<User> rejectedUsers = userUtility.getAllRejectedUsers();

		GenericEntity<List<User>> list = new GenericEntity<List<User>>(rejectedUsers) {};

		return Response.ok(list).build();

	}

	// http://localhost:8080/fhscServices/rest/fhsc/user/approved
	@POST
	@Produces("application/json")
	@Path("/approved")
	@Secured({FHSCUserCategory.COORDINATOR})
	public Response getApprovedUsers() {

		List<User> approvedUsers = userUtility.getAllApprovedUsers();

		GenericEntity<List<User>> list = new GenericEntity<List<User>>(approvedUsers) {
		};

		return Response.ok(list).build();
	}

	// http://localhost:8080/fhscServices/rest/fhsc/user/all
	@POST
	@Produces("application/json")
	@Path("/all")
	@Secured({FHSCUserCategory.COORDINATOR})
	public Response getAllUsers() {

		List<User> approvedUsers = userUtility.getAllUsers();
		GenericEntity<List<User>> list = new GenericEntity<List<User>>(approvedUsers) {
		};
		return Response.ok(list).build();
	}


	// http://localhost:8080/fhscServices/rest/fhsc/user/
	@POST
	@Produces("application/json")
	@Secured({FHSCUserCategory.COORDINATOR,FHSCUserCategory.INVESTIGATOR})
	public Response getcurrentUser(@Context SecurityContext context) {

		Login login=(Login) context.getUserPrincipal();
		List<User> currentUser = new ArrayList<>();
		currentUser.add(login.getUserId());
		GenericEntity<List<User>> list = new GenericEntity<List<User>>(currentUser) {
		};
		return Response.ok(list).build();
	}
	
	// http://localhost:8080/fhscServices/rest/fhsc/user/{recordID}
	@GET
	@Produces("application/json")
	@Path("/{recordID}")
	@Secured({FHSCUserCategory.COORDINATOR})
	public Response getUser(@PathParam("recordID") String recordID) {

		try {
			Integer userID = Integer.parseInt(recordID);
			User user = userUtility.getUser(userID);
			if(user != null) {
				return Response.status(200).entity(user).build();
			}
		} catch (NumberFormatException nfe) {
			return Response.status(Response.Status.NOT_FOUND).entity("The User ID is not valid").build();
		}
		
		return Response.status(Response.Status.NOT_FOUND).entity("The User not found").build();
	}

	private Map<String, Object> createDatasetForAdminEmail(User user) {

		Map<String, Object> dataset = new HashMap<String, Object>();
		dataset.put("to", "FHSC Admin");

		dataset.put("applicant", user.getTitle() + " " + user.getFirstName());

		dataset.put("registrationDate", user.getApplicationDate().toString());
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

	private void sendEmail(String emailSubject, String emailContent) {
		try {
			emailUtility.notifyAdmin(emailSubject, emailContent);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
