package com.github.arielcarrera.cdi.test;

import javax.inject.Inject;

import com.github.arielcarrera.cdi.test.repositories.TestReadWriteRepository;

/**
 * Tests for Writable Repository
 * 
 * @author Ariel Carrera
 *
 */
public class ReadWriteRepositoryTest extends AbstractReadWriteRepositoryTest {

	@Inject
	protected TestReadWriteRepository testRepository;
	
	@Override
	public TestReadWriteRepository getTestRepository() {
		return testRepository;
	}
	
}