package com.github.arielcarrera.cdi.repositories;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.exceptions.ItemNotFoundException;
import com.github.arielcarrera.cdi.exceptions.NotSupportedException;

/**
 * Interface of a data repository that implements read operations over an entity
 * 
 * Important: If it is used with WritableRepository, it must to be placed first by ambiguity resolution. Use instead {@link ReadableWritableLogicalDeletionRepository}.
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity that extends {@link LogicalDeletion} class
 * @param <PK> Entity's PK
 */
@NoRepositoryBean
public interface ReadableWritableLogicalDeletionRepository<T extends LogicalDeletion, ID extends Serializable> 
extends LogicalDeletionRepository<T,ID>, ReadableWritableRepository<T,ID>, JpaRepositoryHelper<T, ID> {


	/**
	 * Deletes the entity with the given id.
	 *
	 * @param id must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
	 */
	@Transactional(TxType.REQUIRED)
	@Override
	default void deleteById(ID id) {
		Optional<T> optional = findById(id);
		if (optional.isPresent()) {
			optional.get().statusDeleted();
		} else {
			throw new ItemNotFoundException("La entidad que intenta dar de baja es inexistente");
		}
	}

	/**
	 * Deletes a given entity.
	 *
	 * @param entity
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 */
	@Transactional(TxType.REQUIRED)
	@Override
	default void delete(T entity) {
		entity = getEntityManager().merge(entity);
		entity.statusDeleted();
	}
	
	/**
	 * Deletes the given entities.
	 *
	 * @param entities
	 * @throws IllegalArgumentException in case the given {@link Iterable} is {@literal null}.
	 */
	@Transactional(TxType.REQUIRED)
	@Override
	default void deleteAll(Iterable<? extends T> entities) {
		if (entities != null) {
			entities.forEach(e -> e.statusDeleted());
		}
	}

	/**
	 * Deletes all entities managed by the repository.
	 */
	@Transactional(TxType.REQUIRED)
	@Override
	default void deleteAll() {
		List<T> entities = findAll();
		if (entities != null) {
			entities.forEach(e -> e.statusDeleted());
		}
	}
	
	/**
	 * Deletes the given entities in a batch which means it will create a single {@link Query}. Assume that we will clear
	 * the {@link javax.persistence.EntityManager} after the call.
	 *
	 * @param entities
	 */
	@Override
	default void deleteInBatch(Iterable<T> entities) {
		throw new NotSupportedException("El borrado fisico no es soportado para entidades de borrado logico");
	}

	/**
	 * Deletes all entities in a batch call.
	 */
	@Override
	default void deleteAllInBatch() {
		throw new NotSupportedException("El borrado fisico no es soportado para entidades de borrado logico");
	}
}
