package uk.ac.ic.sph.pcph.iccp.fhsc.restful.filters;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Priorities;

import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Login;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.FHSCUserCategory;

@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
@RequestScoped
public class AuthorizationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the resource class which matches with the requested URL
        // Extract the roles declared by it
        Class<?> resourceClass = resourceInfo.getResourceClass();
        List<FHSCUserCategory> classRoles = extractRoles(resourceClass);

        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = resourceInfo.getResourceMethod();
        List<FHSCUserCategory> methodRoles = extractRoles(resourceMethod);

        try {

            // Check if the user is allowed to execute the method
            // The method annotations override the class annotations
            if (methodRoles.isEmpty()) {
                checkPermissions((Login)requestContext.getSecurityContext().getUserPrincipal(),classRoles);
            } else {
                checkPermissions((Login)requestContext.getSecurityContext().getUserPrincipal(),methodRoles);
            }

        } catch (Exception e) {
            requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN).build());
        }
    }

    // Extract the roles from the annotated element
    private List<FHSCUserCategory> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<FHSCUserCategory>();
        } else {
            Secured secured = annotatedElement.getAnnotation(Secured.class);
            if (secured == null) {
                return new ArrayList<FHSCUserCategory>();
            } else {
            	FHSCUserCategory[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }

    private void checkPermissions(Login login,List<FHSCUserCategory> allowedRoles) throws Exception {
        // Check if the user contains one of the allowed roles
        // Throw an Exception if the user has not permission to execute the method
    	for (FHSCUserCategory category:allowedRoles){
    		if(login.getUserId().getCategory().toString().toUpperCase().equals(category.toString().toUpperCase()))
    			return;
    	}
    	throw new Exception("User is not allowed to use this method");
    }

}