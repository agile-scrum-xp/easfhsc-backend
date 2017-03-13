package uk.ac.ic.sph.pcph.iccp.fhsc.utility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

/*
 * This class reads application initialization values from context.xml
 * 
 * Best practice to keep configuration parameters outside the Web Application
 * i.e. neither in any class nor web.xml nor in resources directory
 * 
 * changes in the context.xml still requires restarting the Tomcat
 * but no need to re-compile the web application, 
 *     no need to re-test the web application
 *     no need to re-deploy the web application
 *     
 * example:
 * <!-- Application Parameters -->
 * <Parameter name="uk.ac.ic.sph.pcph.iccp.fhsc.token.life" value="3600"/>
 */

@Named
@ApplicationScoped
public class ApplicationParameters {

	@Inject
	private ServletContext servletContext;

	public String getTokenLife() {
		return servletContext.getInitParameter("uk.ac.ic.sph.pcph.iccp.fhsc.token.life");
	}
	
	public String getTemplateDirectory() {
		return servletContext.getInitParameter("uk.ac.ic.sph.pcph.iccp.fhsc.template.dir");
	}
	
	public String getAdminNotificationTemplateFileName() {
		return servletContext.getInitParameter("uk.ac.ic.sph.pcph.iccp.fhsc.admin.notification.template.name");
	}

	public String getAdminNotificationEmailSubject() {
		return servletContext.getInitParameter("uk.ac.ic.sph.pcph.iccp.fhsc.admin.notification.email.subject");
	}
	
	public String getUploadDirectory() {
		return servletContext.getInitParameter("uk.ac.ic.sph.pcph.iccp.fhsc.upload.dir");
	}
}
