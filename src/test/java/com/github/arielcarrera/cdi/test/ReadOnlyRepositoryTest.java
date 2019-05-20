package com.github.arielcarrera.cdi.test;

import javax.inject.Inject;

import com.github.arielcarrera.cdi.test.repositories.TestReadOnlyRepository;

/**
 * Tests for Readable Repository
 * 
 * @author Ariel Carrera
 *
 */
public class ReadOnlyRepositoryTest extends AbstractReadOnlyRepositoryTest {

	@Inject
	protected TestReadOnlyRepository testRepository;

	@Override
	public TestReadOnlyRepository getTestRepository() {
		return testRepository;
	}

}