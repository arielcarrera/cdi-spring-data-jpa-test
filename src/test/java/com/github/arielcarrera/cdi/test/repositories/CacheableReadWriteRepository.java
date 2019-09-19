package com.github.arielcarrera.cdi.test.repositories;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.cdi.Eager;

import com.github.arielcarrera.cdi.repositories.ReadWriteRepository;
import com.github.arielcarrera.cdi.test.entities.CacheableEntity;

@Eager
public interface CacheableReadWriteRepository extends ReadWriteRepository<CacheableEntity, Integer>{

    List<CacheableEntity> findAllByValue(Integer value);
    
    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    List<CacheableEntity> findByValue(Integer value);
}
