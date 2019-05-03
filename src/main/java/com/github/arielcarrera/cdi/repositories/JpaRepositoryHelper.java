package com.github.arielcarrera.cdi.repositories;

import java.io.Serializable;

import javax.persistence.EntityManager;

/**
 * Interface that provides custom methods for work with JPA inside Repository Fragments
 *  
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <ID> Entity's PK
 */
public interface JpaRepositoryHelper<T, ID extends Serializable> {

	EntityManager getEntityManager();
	
}
