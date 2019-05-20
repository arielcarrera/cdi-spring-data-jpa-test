package com.github.arielcarrera.cdi.test.repositories;

import org.springframework.data.repository.cdi.Eager;

import com.github.arielcarrera.cdi.repositories.ReadOnlySoftDeleteRepository;
import com.github.arielcarrera.cdi.test.entities.TestEntity;

@Eager
public interface TestReadOnlySoftDeleteRepository extends ReadOnlySoftDeleteRepository<TestEntity, Integer>{

}
