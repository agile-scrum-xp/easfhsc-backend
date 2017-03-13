package uk.ac.ic.sph.pcph.iccp.fhsc.restful.filters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

import uk.ac.ic.sph.pcph.iccp.fhsc.enums.FHSCUserCategory;

//import net.easfhsc.enums.FHSCUserCategory;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Secured {
	 FHSCUserCategory[] value() default{};
}