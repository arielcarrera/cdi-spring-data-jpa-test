package com.github.arielcarrera.cdi.test.repositories;

import org.springframework.data.repository.cdi.Eager;

import com.github.arielcarrera.cdi.repositories.ReadWriteDeleteRepository;
import com.github.arielcarrera.cdi.test.entities.TestEntity;

@Eager
public interface TestReadWriteDeleteRepository extends ReadWriteDeleteRepository<TestEntity, Integer> {

}
