package com.github.arielcarrera.cdi.test.repositories;

import org.springframework.data.repository.cdi.Eager;

import com.github.arielcarrera.cdi.repositories.ReadWriteRepository;
import com.github.arielcarrera.cdi.test.entities.TestEntity;

@Eager
public interface TestReadWriteRepository extends ReadWriteRepository<TestEntity, Integer>{

}
