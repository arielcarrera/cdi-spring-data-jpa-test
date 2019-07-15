package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import javax.inject.Inject;

import org.junit.Test;

import com.github.arielcarrera.cdi.test.entities.TestEntity;
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
	
	@Test
	public void custom_OK() {
		Optional<TestEntity> op = getTestRepository().custom(1);
		assertNotNull(op);
		assertTrue(op.isPresent());
		assertTrue(op.get().getValue().equals(101));
	}

	@Test
	public void custom_NotFound() {
		Optional<TestEntity> op = getTestRepository().custom(-1);
		assertNotNull(op);
		assertFalse(op.isPresent());
	}
	
	
	@Test
	public void findOneByValue_OK() {
		TestEntity t = getTestRepository().findOneByValue(101);
		assertNotNull(t);
		assertTrue(t.getValue().equals(101));
	}

	@Test
	public void findOneByValue_NotFound() {
	    	TestEntity t = getTestRepository().findOneByValue(-1);
		assertNull(t);
	}

}