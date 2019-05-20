package com.github.arielcarrera.cdi.repositories.fragments;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.github.arielcarrera.cdi.repositories.helpers.CustomJpaRepository;

/**
 * Fragment interface of a data repository that implements read operations over an entity
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <ID> Entity's PK
 */
public interface ReadFragment<T, ID extends Serializable> extends QueryByExampleFragment<T>, CustomJpaRepository {

	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal Optional#empty()} if none found
	 * @throws IllegalArgumentException if {@code id} is {@literal null}.
	 */
	@Transactional(value = TxType.REQUIRED)
	Optional<T> findById(ID id);

	/**
	 * Returns whether an entity with the given id exists.
	 *
	 * @param id must not be {@literal null}.
	 * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
	 * @throws IllegalArgumentException if {@code id} is {@literal null}.
	 */
	@Transactional(value = TxType.REQUIRED)
	boolean existsById(ID id);

	/**
	 * Returns all instances of the type.
	 *
	 * @return all entities
	 */
	@Transactional(value = TxType.REQUIRED)
	List<T> findAll();
	
	/**
	 * Realiza la busqueda con ordenamiento
	 * @param sort
	 * @return all entities roted
	 */
	@Transactional(value = TxType.REQUIRED)
	List<T> findAll(Sort sort);
	
	/**
	 * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
	 *
	 * @param pageable
	 * @return a page of entities
	 */
	@Transactional(value = TxType.REQUIRED)
	Page<T> findAll(Pageable pageable);

	/**
	 * Returns all instances of the type with the given Ids.
	 *
	 * @param ids
	 * @return
	 */
	@Transactional(value = TxType.REQUIRED)
	List<T> findAllById(Iterable<ID> ids);
	
	/**
	 * Returns the number of entities available.
	 *
	 * @return the number of entities
	 */
	@Transactional(value = TxType.REQUIRED)
	long count();
	
	/**
	 * Returns a reference to the entity with the given identifier. Depending on how the JPA persistence provider is
	 * implemented this is very likely to always return an instance and throw an
	 * {@link javax.persistence.EntityNotFoundException} on first access. Some of them will reject invalid identifiers
	 * immediately.
	 *
	 * @param id must not be {@literal null}.
	 * @return a reference to the entity with the given identifier.
	 * @see EntityManager#getReference(Class, Object) for details on when an exception is thrown.
	 */
	@Transactional(value = TxType.REQUIRED)
	T getOne(ID id);
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.QueryByExampleExecutor#findAll(org.springframework.data.domain.Example)
	 */
	@Transactional(value = TxType.REQUIRED)
	@Override
	<S extends T> List<S> findAll(Example<S> example);
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.QueryByExampleExecutor#findAll(org.springframework.data.domain.Example, org.springframework.data.domain.Sort)
	 */
	@Transactional(value = TxType.REQUIRED)
	@Override
	<S extends T> List<S> findAll(Example<S> example, Sort sort);
}