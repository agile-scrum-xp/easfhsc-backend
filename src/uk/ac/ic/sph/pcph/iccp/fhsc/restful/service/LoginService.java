package uk.ac.ic.sph.pcph.iccp.fhsc.restful.service;

import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.User;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.filters.Secured;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility.LoginUtility;
import uk.ac.ic.sph.pcph.iccp.fhsc.utility.ApplicationParameters;



/**
 * for security the message must have a header
 * header key name must be Authorization
 * header key value must starts with Bearer + SPACE + TOKEN_STRING
 *
 */
@Path("/fhsc/login")
@Named
@RequestScoped
public class LoginService {
	
	@Inject
	private LoginUtility loginUtility;
	
	@Inject
	private ApplicationParameters applicationParameters;

	private String serviceMessage = "This is the FHSC Login service";
	
	public LoginService() {}

	// http://localhost:8080/fhscServices/rest/fhsc/login/about
	@Secured
	@GET
	@Path("/about")
	public Response about() {
		String result = "{ \"result\":" + "\"" + serviceMessage + "\" }";
		return Response.status(200).entity(result).build();
	}
	
	// http://localhost:8080/fhscServices/rest/fhsc/login/about/asif%20akram
	@GET
	@Path("/about/{param}")
	public Response about(@PathParam("param") String msg) {
		String result = "{ \"result\":" + "\"" + msg + "\", " + "\"test\":" + "\"" + msg + "\"" + "}";
		return Response.status(200).entity(result).build();
	}
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public Response login(@FormParam("username") String username, @FormParam("password") String password) {
		System.out.println("Username is: " + username); // prints output
		System.out.println("Password is: " + password); // prints output
		System.out.println();
		String role = "guest";
		
		User tempUser = /*new LoginUtility()*/loginUtility.validateLogin(username, password);
		
		
		if (tempUser != null) {
			long tokenLife;
			try {	
				tokenLife = Integer.parseInt(applicationParameters.getTokenLife()) * 1000;
			} catch (NumberFormatException nfe) {
				tokenLife = 3600 * 1000;
			}
			
			long nowMillis = System.currentTimeMillis();
	        Date now = new Date(nowMillis);

	        long expMillis = nowMillis + tokenLife;
	        Date expireDate = new Date(expMillis);
	        
	        System.out.println(expireDate.toString());
	        
	        /**
			 * method output is 
			 * {
			 * 	  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBQUFBQSIsInJvbGVzIjoiZ3Vlc3QiLCJzdHVwaWQiOiJndWVzdCIsImlhdCI6MTQ4NDExNTg3Nn0.4fhACuJsKimjx_TWbdkJEJm9pq__XWs2nHBhNnC59R0"
			 * }
			 */
			LoginResponse response = new LoginResponse(Jwts.builder().setSubject(username).claim("roles", tempUser.getCategory()).setIssuedAt(now)
					.signWith(SignatureAlgorithm.HS256, "itShouldNotBeSecret").setExpiration(expireDate).compact());
			
			/*
			 * The client will receive:
			 * {
			   	exp:1487913387,
				iat:1487909787,
				roles:"guest",
				dummy:"guest",
				sub:"aassif14"
			 * }
			 */
			
 	        return Response.status(200).entity(response).build();
		} else {
			LoginResponse response = new LoginResponse("Autentication Failed");
			/*
			 * .entity(response) has no impact what client receives
			 * was hoping to send custom error message
			 * have to find any other way
			 */
			return Response.status(Response.Status.NOT_FOUND).entity(response).build();
		}
	}
	
	/**
	 * http://localhost:8080/fhscServices/rest/fhsc/login/login
	 * 
	 * message body raw "application/json
	 * message type: 
	 * {
	 * "userName" : "Asif Akram",
	 * "passWord" : "dummy password"
	 * }
	 * @param secret
	 * @return
	 */
	
	@POST
    @Produces("application/json")
    @Consumes("application/json")
	@Path("/login")
    public Response authenticateUser(Secret secret) {
		System.out.println("Username is: " + secret.getUserName()); // prints output
		System.out.println("Password is: " + secret.getPassWord()); // prints output
		
		String result = "{ \"result\":" + "\" This is Login Test \", " + "\"test\":" + "\"" + secret.getUserName() + "  " + secret.getPassWord() + "\"" + "}";
		return Response.status(200).entity(result).build();
	}
	
	/*
	 * Utility class should be in separate class ...!
	 */
	@SuppressWarnings("unused")
	@XmlRootElement
	private static class LoginResponse {
		public String token;

		public LoginResponse(final String token) {
			this.token = token;
		}
	}
	
	/**
	 *
	 * The info sent to the server when a user tried to login the registry.
	 * The internal class should be static 
	 * otherwise exception will be thrown: 
	 * no suitable constructor found for type can not instantiate from json object
	 *
	 */
	@SuppressWarnings("unused")
	private static class Secret {

	    private String userName;
	    private String passWord;
	    
	    
		public Secret(String userName, String passWord) {
			this.userName = userName;
			this.passWord = passWord;
		}

		public Secret() {}

	    public String getUserName() {
	        return userName;
	    }

	    public void setUserName(String userName) {
	        this.userName = userName;
	    }

	    public String getPassWord() {
	        return passWord;
	    }

	    public void setPassWord(String passWord) {
	        this.passWord = passWord;
	    }
	}

}
