package com.github.arielcarrera.cdi.repositories.fragments;
import java.io.Serializable;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.data.jpa.repository.Query;

/**
 * Fragment interface of a data repository that implements delete operations over an entity
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <ID> Entity's PK
 */
public interface DeleteFragment<T, ID extends Serializable> {

	/**
	 * Deletes the entity with the given id.
	 *
	 * @param id must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
	 */
	@Transactional(TxType.REQUIRED)
	void deleteById(ID id);

	/**
	 * Deletes a given entity.
	 *
	 * @param entity
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 */
	@Transactional(TxType.REQUIRED)
	void delete(T entity);

	/**
	 * Deletes the given entities.
	 *
	 * @param entities
	 * @throws IllegalArgumentException in case the given {@link Iterable} is {@literal null}.
	 */
	@Transactional(TxType.REQUIRED)
	void deleteAll(Iterable<? extends T> entities);

	/**
	 * Deletes all entities managed by the repository.
	 */
	@Transactional(TxType.REQUIRED)
	void deleteAll();

	/**
	 * Flushes all pending changes to the database.
	 */
	@Transactional(TxType.REQUIRED)
	void flush();

	/**
	 * Deletes the given entities in a batch which means it will create a single {@link Query}. Assume that we will clear
	 * the {@link javax.persistence.EntityManager} after the call.
	 *
	 * @param entities
	 */
	@Transactional(TxType.REQUIRED)
	void deleteInBatch(Iterable<T> entities);

	/**
	 * Deletes all entities in a batch call.
	 */
	@Transactional(TxType.REQUIRED)
	void deleteAllInBatch();

}