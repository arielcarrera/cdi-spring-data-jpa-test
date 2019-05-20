package com.github.arielcarrera.cdi.repositories;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;

import com.github.arielcarrera.cdi.entities.Identifiable;
import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.exceptions.NotSupportedException;

/**
 * Interface of a data repository that implements read operations over an entity
 * 
 * Important: If it is used with WritableRepository, it must to be placed first
 * by ambiguity resolution. Use instead {@link ReadWriteSoftDeleteRepository}.
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity that extends {@link LogicalDeletion} class
 * @param <PK> Entity's PK
 */
@NoRepositoryBean
public interface ReadWriteSoftDeleteRepository<T extends LogicalDeletion & Identifiable<ID>, ID extends Serializable>
		extends ReadOnlySoftDeleteRepository<T,ID>, ReadWriteRepository<T,ID> {

	/**
	 * Deletes (soft delete) the entity with the given id.
	 *
	 * @param id must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@code id} is
	 *                                  {@literal null}
	 */
	@Transactional(TxType.REQUIRED)
	default void deleteById(ID id) {
		Assert.notNull(id, "The given id must not be null!");
		
		findById(id).orElseThrow(() -> new EmptyResultDataAccessException(
					String.format("No entity with id %s exists!",  id), 1)).statusDeleted();
	}

	/**
	 * Deletes (soft delete) a given entity.
	 *
	 * @param entity
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 */
	@Transactional(TxType.REQUIRED)
	default void delete(T entity) {
		Assert.notNull(entity, "The entity must not be null!");
		Optional<T> opt = findById(entity.getId());
		if (!opt.isPresent()) {
			entity = save(entity);
		} else {
			entity = opt.get();
		}
		entity.statusDeleted();
	}

	/**
	 * Deletes (soft delete) the given entities if presents
	 *
	 * @param entities
	 * @throws IllegalArgumentException in case the given {@link Iterable} is
	 *                                  {@literal null}.
	 */
	@Transactional(TxType.REQUIRED)
	default void deleteAll(Iterable<? extends T> entities) {
		Assert.notNull(entities, "The given Iterable of entities not be null!");
		entities.forEach(e -> delete(e));
	}

	/**
	 * Deletes (soft delete) all entities managed by the repository.
	 */
	@Transactional(TxType.REQUIRED)
	default void deleteAll() {
		List<T> entities = findAll();
		if (entities != null) {
			entities.forEach(e -> delete(e));
		}
	}
	
	/**
	 * Deletes (soft delete) the given entities in a batch which means it will create a single {@link Query}. Assume that we will clear
	 * the {@link javax.persistence.EntityManager} after the call.
	 *
	 * @param entities
	 */
	@Transactional(TxType.REQUIRED)
	default void deleteInBatch(Iterable<T> entities) {
		Assert.notNull(entities, "The given Iterable of entities not be null!");
		throw new NotSupportedException("Operation not supported");
	}

	/**
	 * Deletes (soft delete) all entities in a batch call.
	 */
	@Transactional(TxType.REQUIRED)
	default void deleteAllInBatch() {
		throw new NotSupportedException("Operation not supported");
		
	}
}
