package org.springframework.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

//Added for entityManager issue
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
public @interface RepositoryCreation {
	
	public static final class Literal extends AnnotationLiteral<RepositoryCreation> implements RepositoryCreation {

	        public static final Literal INSTANCE = new Literal();

	        private static final long serialVersionUID = 1L;

	    }
}