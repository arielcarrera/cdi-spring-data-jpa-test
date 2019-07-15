package com.github.arielcarrera.cdi.test.repositories;

import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.github.arielcarrera.cdi.test.entities.TestEntity;

/**
 * Custom Fragment Implementation
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <ID> Entity's PK
 */
@Transactional(TxType.REQUIRED)
public class CustomFragmentImpl implements CustomFragment {

    @Inject
    private EntityManager em;
    

    @Override
    public Optional<TestEntity> custom(Integer id) {
	return Optional.ofNullable(em.find(TestEntity.class, id));
    }

}