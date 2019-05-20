package com.github.arielcarrera.cdi.repositories;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.repositories.fragments.QuerySoftDeleteFragment;

/**
 * Interface of a data repository that implements write operations over an
 * entity
 * 
 * Important: If it is used with WritableRepository, it must to be placed first
 * by ambiguity resolution.
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity that extends {@link LogicalDeletion} class
 * @param <PK> Entity's PK
 */
@NoRepositoryBean
public interface ReadOnlySoftDeleteRepository<T extends LogicalDeletion, ID extends Serializable>
		extends ReadOnlyRepository<T, ID>, QuerySoftDeleteFragment<T, ID> {

}
