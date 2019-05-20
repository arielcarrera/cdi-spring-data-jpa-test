package com.github.arielcarrera.cdi.repositories;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.repositories.fragments.QuerySoftDeleteFragment;

/**
 * Interface of a data repository that implements read operations over an entity
 * 
 * Important: If it is used with WritableRepository, it must to be placed first
 * by ambiguity resolution. Use instead {@link ReadWriteSoftDeleteRepository2}.
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity that extends {@link LogicalDeletion} class
 * @param <PK> Entity's PK
 */
@NoRepositoryBean
public interface ReadWriteSoftDeleteRepository2<T extends LogicalDeletion, ID extends Serializable>
		extends Repository<T,ID>, QuerySoftDeleteFragment {

}
