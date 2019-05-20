package com.github.arielcarrera.cdi.test.repositories;

import org.springframework.data.repository.cdi.Eager;

import com.github.arielcarrera.cdi.repositories.ReadWriteSoftDeleteRepository;
import com.github.arielcarrera.cdi.test.entities.TestEntity;

@Eager
public interface TestReadWriteSoftDeleteRepository extends ReadWriteSoftDeleteRepository<TestEntity, Integer>{

}
