package uk.ac.ic.sph.pcph.iccp.fhsc.restful.filters;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import com.sun.security.auth.UserPrincipal;

import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Login;

public class FHSCSecurityContext implements SecurityContext{

	private String username, role;
	
	public FHSCSecurityContext() {
		super();
		// TODO Auto-generated constructor stub
	}
	public FHSCSecurityContext(String username, String role) {
		super();
		this.username=username;
		this.role=role;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return new UserPrincipal(this.username);
	}

	@Override
	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return this.role.toUpperCase().equals(role.toUpperCase());
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getAuthenticationScheme() {
		// TODO Auto-generated method stub
		return null;
	}

}
