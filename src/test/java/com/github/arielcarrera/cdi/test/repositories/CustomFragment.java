package com.github.arielcarrera.cdi.test.repositories;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.github.arielcarrera.cdi.test.entities.TestEntity;

/**
 * Custom Fragment
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <ID> Entity's PK
 */
@Transactional(TxType.REQUIRED)
public interface CustomFragment {

	Optional<TestEntity> custom(Integer id);

}