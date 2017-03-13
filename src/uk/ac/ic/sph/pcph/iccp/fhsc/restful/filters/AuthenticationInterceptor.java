package uk.ac.ic.sph.pcph.iccp.fhsc.restful.filters;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Login;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility.LoginUtility;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;

@Secured
@Provider
//@PreMatching
@Priority(Priorities.AUTHENTICATION)
@RequestScoped
public class AuthenticationInterceptor implements ContainerRequestFilter, ContainerResponseFilter {


	@Inject 
	private LoginUtility loginUtility;
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		System.out.println("request filter");

		// Get the HTTP Authorization header from the request
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Check if the HTTP Authorization header is present and formatted
		// correctly
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		// Extract the token from the HTTP Authorization header
		String token = authorizationHeader.substring("Bearer".length()).trim();

		try {
			// Validate the token
			FHSCUser user = validateToken(token);
			
			//Storing login as Principal
			Login login=loginUtility.getLoginForUsername(user.userName);
			requestContext.setSecurityContext(new FHSCSecurityContext(login));

		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

	@Override
	public void filter(ContainerRequestContext arg0, ContainerResponseContext arg1) throws IOException {
		System.out.println("response filter");

	}

	private FHSCUser validateToken(String token) throws Exception {
		// Check if it was issued by the server and if it's not expired
		// Throw an Exception if the token is invalid
		
		// Check if it was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
		
		System.out.println(token);

        Claims claim = Jwts.parser().setSigningKey("itShouldNotBeSecret").parseClaimsJws(token).getBody();
        String userName = (String) claim.get("sub");
        String country = (String) claim.get("country");
        String category = (String) claim.get("category");
        if (claim.getExpiration().before(new Date())) {
            throw new Exception("Token expires.");
        }
        
        FHSCUser user = new FHSCUser(userName, category);
        
        System.out.println(userName + "  "  + country + "  " + category + "  " +  claim.getExpiration());
        
        return user;
	}
	
	@SuppressWarnings("unused")
	private static class FHSCUser {

	    private String userName;
	    private String category;
	    
	    
		public FHSCUser(String userName, String category) {
			this.userName = userName;
			this.category = category;
		}

		public FHSCUser() {}

	    public String getUserName() {
	        return userName;
	    }

	    public void setUserName(String userName) {
	        this.userName = userName;
	    }

	    public String getCategory() {
	        return category;
	    }

	    public void setCategory(String category) {
	        this.category = category;
	    }
	}

}
