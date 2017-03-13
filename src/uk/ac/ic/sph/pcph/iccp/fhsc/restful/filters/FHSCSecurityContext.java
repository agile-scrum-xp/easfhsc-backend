package uk.ac.ic.sph.pcph.iccp.fhsc.restful.filters;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Login;

public class FHSCSecurityContext implements SecurityContext{

	private Login login;
	
	public FHSCSecurityContext() {
		super();
		// TODO Auto-generated constructor stub
	}
	public FHSCSecurityContext(Login login) {
		super();
		this.login=login;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return login;
	}

	@Override
	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return login.getUserId().getCategory().toString().equals(role.toUpperCase());
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
