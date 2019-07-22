package com.github.arielcarrera.cdi.repositories.helpers;

import javax.persistence.EntityManager;

/**
 * Interface that provides custom methods for work with JPA inside Repository Fragments
 *  
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <ID> Entity's PKzz
 */
public interface CustomJpaRepository {

	EntityManager entityManager();
	
	boolean contains(Object object);
	
}
