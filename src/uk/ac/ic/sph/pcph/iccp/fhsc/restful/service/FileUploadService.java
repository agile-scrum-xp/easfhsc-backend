package uk.ac.ic.sph.pcph.iccp.fhsc.restful.service;

import javax.ws.rs.GET;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.SingleBody;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Login;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.FHSCUserCategory;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.filters.Secured;
import uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility.FileUtility;
import uk.ac.ic.sph.pcph.iccp.fhsc.utility.ApplicationParameters;

@Path("/fhsc/fileUpload")
@Named
@RequestScoped
public class FileUploadService {
	
	@Inject
	private FileUtility fileUtility;
	
	private String serviceMessage = "This is the FHSC File Upload service";
	
	public FileUploadService() {

	}

	//http://localhost:8080/fhscServices/rest/fhsc/fileUpload/about
	@GET
	@Path("/about")
	public Response about() {
		String result = "{ \"result\":" + "\"" + serviceMessage + "\" }";
		return Response.status(200).entity(result).build();
	}
	
	@POST
    @Path("/fileupload")
    @Consumes("multipart/form-data")
    @Produces(MediaType.TEXT_PLAIN)
	@Secured({FHSCUserCategory.COORDINATOR,FHSCUserCategory.LEAD_INVESTIGATOR,FHSCUserCategory.CONTRIBUTING_INVESTIGATOR })
    public Response fileupload(MultipartFormDataInput input, @Context SecurityContext context) {
		/*
		 * Extracting data from request
		 */
        String fileName = "";
        int status = -1;
        Response resp = null;
        byte[] fileContent=null;
        String originalFileName=null;
        try {
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
			String newFilename = readByteArray(uploadForm.get("name").get(0));
            String comment =  readByteArray(uploadForm.get("comment").get(0));
            String type=readByteArray(uploadForm.get("type").get(0));
            
            List<InputPart> inputParts  = uploadForm.get("content");
            for (InputPart inputPart : inputParts) {
                MultivaluedMap<String, String> header = inputPart.getHeaders();
                originalFileName = getFileName(header);
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                fileContent= IOUtils.toByteArray(inputStream);
            }
            
            /*
    		 * Saving file on disk, create file in database
    		 */
            fileUtility.addFile(newFilename,originalFileName, comment, type,context.getUserPrincipal().getName(), fileContent);
            
            System.out.println("File upload completed");
            resp = Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            status = status <= 0 ? 500 : status;
            resp = Response.status(400).entity(e.getMessage()).build();
        }
        return resp;
    }

    private String getFileName(MultivaluedMap<String, String> multivaluedMap) {
        String[] contentDisposition = multivaluedMap.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if (filename.trim().startsWith("filename")) {
                String[] name = filename.split("=");
                String exactFileName = name[1].trim().replaceAll("\"", "");
                return exactFileName;
            }
        }
        return "temp";
    }
	
    /*
     * translate bytes in multipart fom-data into String
     */
    private String readByteArray(InputPart inputPart) throws Exception {
        Field f = inputPart.getClass().getDeclaredField("bodyPart");
        f.setAccessible(true);
        BodyPart bodyPart = (BodyPart) f.get(inputPart);
        SingleBody body = (SingleBody)bodyPart.getBody();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        body.writeTo(os);
        byte[] fileBytes = os.toByteArray();
        return new String(fileBytes, "UTF-8");
    }

    

}
