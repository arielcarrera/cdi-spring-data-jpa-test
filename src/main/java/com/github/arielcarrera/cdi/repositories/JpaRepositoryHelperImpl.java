package com.github.arielcarrera.cdi.repositories;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Helper Class for custom JPA queries insidea a Repository Fragment
 *  
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <ID> Entity's PK
 */
public class JpaRepositoryHelperImpl<T, ID extends Serializable> {

	@Inject
	EntityManager entityManager;
	
	EntityManager getEntityManager() {
		return entityManager;
	}
	
}
