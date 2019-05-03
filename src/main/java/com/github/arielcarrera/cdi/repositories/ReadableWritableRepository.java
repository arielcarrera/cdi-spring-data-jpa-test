package com.github.arielcarrera.cdi.repositories;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;

/**
 * Interface of a data repository that implements read/write operations over an entity
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <PK> Entity's PK
 */
@NoRepositoryBean
public interface ReadableWritableRepository<T, ID extends Serializable> extends ReadableRepository<T, ID>, WritableRepository<T, ID> {

}