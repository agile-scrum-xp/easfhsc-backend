package uk.ac.ic.sph.pcph.iccp.fhsc.restful.service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

import uk.ac.ic.sph.pcph.iccp.fhsc.restful.filters.CustomCorsFilter;

@ApplicationPath("rest")
public class RestApplication extends Application {

	/**
	 * Set<Class<?>> classes is required for CDI
	 */
	private Set<Class<?>> classes = new HashSet<>();
	private Set<Object> singletons;

	public RestApplication() {
		System.out.println("RestApplication() called");

		classes.add(ApprovalService.class);
		classes.add(FileUploadService.class);
		classes.add(FileListService.class);
		classes.add(LoginService.class);
		classes.add(UserService.class);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

	@Override
	public Set<Object> getSingletons() {
		if (singletons == null) {
			CorsFilter corsFilter = new CorsFilter();
			corsFilter.getAllowedOrigins().add("*");
			corsFilter.setAllowCredentials(true);
			corsFilter.setAllowedMethods("GET, POST, PUT, DELETE, OPTIONS, HEAD");
			corsFilter.setAllowedHeaders("origin, content-type, accept, authorization");
			corsFilter.setCorsMaxAge(1209600);
			singletons = new LinkedHashSet<Object>();
			singletons.add(corsFilter);
			
			CustomCorsFilter customCorsFilter = new CustomCorsFilter();
			singletons.add(customCorsFilter);
		}
		return singletons;
	}

}