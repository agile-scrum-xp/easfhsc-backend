package uk.ac.ic.sph.pcph.iccp.fhsc.qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import uk.ac.ic.sph.pcph.iccp.fhsc.enums.PersistenceUnitEnum;


@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface PersistenceUnitQualifier {
	PersistenceUnitEnum value();
}

