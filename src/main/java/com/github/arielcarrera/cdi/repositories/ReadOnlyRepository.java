package com.github.arielcarrera.cdi.repositories;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.github.arielcarrera.cdi.repositories.fragments.ReadFragment;
import com.github.arielcarrera.cdi.repositories.helpers.CustomJpaRepository;

/**
 * Interface of a data repository that implements read operations over an entity
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <ID> Entity's PK
 */
@NoRepositoryBean
public interface ReadOnlyRepository<T, ID extends Serializable>
		extends Repository<T, ID>, ReadFragment<T, ID>, QueryByExampleExecutor<T>
//, CustomJpaRepository 
{
}