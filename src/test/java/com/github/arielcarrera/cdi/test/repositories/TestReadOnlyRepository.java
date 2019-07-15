package com.github.arielcarrera.cdi.test.repositories;

import org.springframework.data.repository.cdi.Eager;

import com.github.arielcarrera.cdi.repositories.ReadOnlyRepository;
import com.github.arielcarrera.cdi.test.entities.TestEntity;

@Eager
public interface TestReadOnlyRepository extends ReadOnlyRepository<TestEntity, Integer>, CustomFragment, CustomFragment2 {

}
