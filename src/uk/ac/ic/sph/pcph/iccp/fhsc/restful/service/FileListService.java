package uk.ac.ic.sph.pcph.iccp.fhsc.restful.service;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.google.common.net.MediaType;

import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Files;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.FHSCUserCategory;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.filters.Secured;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility.FileUtility;

@Path("/fhsc/fileList")
@Named
@RequestScoped
public class FileListService {
	
	@Inject
	private FileUtility fileUtility;
	
	private String serviceMessage = "This is the FHSC File List service";

	public FileListService() {
	}

	// http://localhost:8080/fhscServices/rest/fhsc/fileList/about
	@GET
	@Path("/about")
	public Response about() {
		String result = "{ \"result\":" + "\"" + serviceMessage + "\" }";
		return Response.status(200).entity(result).build();
	}
	
	@POST
	@Produces("application/json")
	@Path("/all")
	@Secured({FHSCUserCategory.COORDINATOR,FHSCUserCategory.INVESTIGATOR})
	public Response getAllUploadedFilesList(@Context SecurityContext context) {
		GenericEntity<List<Files>> list = new GenericEntity<List<Files>>(fileUtility.getAllUploadedFileList()) {};
			
		return Response.status(200).entity(list).build();
	}
	
	
	@POST
	@Produces("application/json")
	@Path("/user")
	@Secured({FHSCUserCategory.INVESTIGATOR})
	public Response getuserUploadedFilesList(@Context SecurityContext context) {
		GenericEntity<List<Files>> list = new GenericEntity<List<Files>>(fileUtility.getUploadedFileList(context.getUserPrincipal().getName())) {};
			
		return Response.status(200).entity(list).build();
	}
	
	@GET
	@Produces("application/json")
	@Path("/details/coordinator/{recordID}")
	@Secured({FHSCUserCategory.COORDINATOR})
	public Response getFileDetailsForCoordinator(@PathParam("recordID") int recordID,@Context SecurityContext context) {
		Files f=null;
		try{
			f=fileUtility.getFile(recordID);
			return Response.status(200).entity(f).build();
		}catch(Exception e){
			return Response.status(400).entity(e.getMessage()).type("text/plain").build();
		}
	}

	/**
	 * For investigator need to check that it is his own files
	 * @param context
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("/details/investigator/{recordID}")
	@Secured({FHSCUserCategory.INVESTIGATOR})
	public Response getFileDetailsForInvestigator(@PathParam("recordID") int recordID,@Context SecurityContext context) {
		String username=context.getUserPrincipal().getName();
		
		Files f=null;
		try{
			f=fileUtility.getFile(username,recordID);
			return Response.status(200).entity(f).build();
		}catch(Exception e){
			return Response.status(400).entity(e.getMessage()).type("text/plain").build();
		}
	}
	
	@GET
	@Produces("application/json")
	@Path("/details/coordinator/{recordID}")
	@Secured({FHSCUserCategory.COORDINATOR})
	public Response getFilePerviewForCoordinator(@PathParam("recordID") int recordID,@Context SecurityContext context) {
		Files f=null;
		try{
			f=fileUtility.getFile(recordID);
			/*
			 * transform in pdf and return stream! 
			 */
			
			return Response.status(200).entity(f).build();
		}catch(Exception e){
			return Response.status(400).entity(e.getMessage()).type("text/plain").build();
		}
	}

	/**
	 * For investigator need to check that it is his own files
	 * @param context
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("/details/investigator/{recordID}")
	@Secured({FHSCUserCategory.INVESTIGATOR})
	public Response getFilePreviewForInvestigator(@PathParam("recordID") int recordID,@Context SecurityContext context) {
		String username=context.getUserPrincipal().getName();
		
		Files f=null;
		try{
			f=fileUtility.getFile(username,recordID);
			/*
			 *  transform in pdf and return stream! 
			 */
			
			return Response.status(200).entity(f).build();
		}catch(Exception e){
			return Response.status(400).entity(e.getMessage()).type("text/plain").build();
		}
	}
	
	
}
