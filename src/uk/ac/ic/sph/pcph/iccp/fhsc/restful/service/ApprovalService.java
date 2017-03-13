package uk.ac.ic.sph.pcph.iccp.fhsc.restful.service;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/fhsc/approval")
@Named
@RequestScoped
public class ApprovalService {

	private String serviceMessage = "This is the FHSC Approval service";

	public ApprovalService() {
	}

	// http://localhost:8080/fhscServices/rest/fhsc/approval/about
	@GET
	@Path("/about")
	public Response about() {
		String result = "{ \"result\":" + "\"" + serviceMessage + "\" }";
		return Response.status(200).entity(result).build();
	}

}
