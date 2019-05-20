package com.github.arielcarrera.cdi.test;

import javax.inject.Inject;

import com.github.arielcarrera.cdi.repositories.ReadOnlySoftDeleteRepository;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadOnlySoftDeleteRepository;

/**
 * Tests for Readable Repository of Soft Delete Entities
 * 
 * @author Ariel Carrera
 *
 */
public class ReadOnlySoftDeleteRepositoryTest extends AbstractReadOnlySoftDeleteRepositoryTest {

	@Inject
	protected TestReadOnlySoftDeleteRepository testRepository;
	
	@Override
	public ReadOnlySoftDeleteRepository<TestEntity,Integer> getTestRepository() {
		return testRepository;
	}

}