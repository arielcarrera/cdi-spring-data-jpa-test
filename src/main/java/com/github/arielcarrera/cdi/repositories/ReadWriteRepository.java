package com.github.arielcarrera.cdi.repositories;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;

import com.github.arielcarrera.cdi.repositories.fragments.CreateUpdateFragment;

/**
 * Interface of a data repository that implements read/write operations over an
 * entity
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <PK> Entity's PK
 */
@NoRepositoryBean
public interface ReadWriteRepository<T, ID extends Serializable>
		extends ReadOnlyRepository<T, ID>, CreateUpdateFragment<T, ID> {

}