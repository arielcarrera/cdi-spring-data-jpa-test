package com.github.arielcarrera.cdi.repositories.helpers;

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
public class CustomJpaRepositoryImpl implements CustomJpaRepository {

    public CustomJpaRepositoryImpl() {
		super();
    }

    @Inject
    EntityManager entityManager;

    @Override
    public EntityManager entityManager() {
	return entityManager;
    }

    @Override
    public boolean contains(Object object) {
	return entityManager.contains(object);
    }
}
