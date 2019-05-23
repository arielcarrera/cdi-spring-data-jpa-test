package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import com.github.arielcarrera.cdi.exceptions.DataAccessException;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadWriteDeleteRepository;

/**
 * Tests for Writable Repository with delete operations
 * 
 * @author Ariel Carrera
 *
 */
public class ReadWriteDeleteRepositoryTest extends AbstractReadWriteRepositoryTest {

	@Inject
	protected TestReadWriteDeleteRepository testRepository;

	@Override
	public TestReadWriteDeleteRepository getTestRepository() {
		return testRepository;
	}

	@Test
	public void deleteById_OK() {
		getTestRepository().deleteById(1);
		getEntityManager().clear();

		assertFalse(getTestRepository().existsById(1));
	}

	@Test(expected = DataAccessException.class)
	public void deleteById_NotFound() {
		try {
			getTestRepository().deleteById(200);
			fail("Exception must to be raised due to entity not exist");
		} catch(DataAccessException e) {
			assertTrue(e.getCause() instanceof EmptyResultDataAccessException);
			throw e;
		}
	}

	@Test(expected = DataAccessException.class)
	public void deleteById_Null() {
		try {
			getTestRepository().deleteById(null);
			fail("Exception must to be raised due to null parameter value");
		} catch(DataAccessException e) {
			assertTrue(e.getCause() instanceof IllegalArgumentException);
			throw e;
		}
	}

	@Test
	public void delete_attached_entity_OK() {
		getTestRepository().delete(getTestRepository().getOne(1));
		getEntityManager().clear();
		
		assertFalse(getTestRepository().existsById(1));
	}

	@Test
	public void delete_detached_entity_OK() {
		getTestRepository().delete(new TestEntity(1, null));
		getEntityManager().clear();
		
		assertFalse(getTestRepository().existsById(1));
	}

	@Test
	public void delete_detached_entity_updated_OK() {
		getTestRepository().delete(new TestEntity(1, 1));
		getEntityManager().clear();
		
		assertFalse(getTestRepository().existsById(1));
	}

	/**
	 * Note: This method does a entity manager merge operation if entity is
	 * detached... (eg. select + insert + delete if entity was not found)
	 */
	// TODO to do a custom and better implementation in replace of default delete
	// implementation
	@Test
	public void delete_detached_entity_NotFound() {
		getTestRepository().delete(new TestEntity(100, null));
		getEntityManager().clear();
		assertFalse(getTestRepository().existsById(100));
	}

	@Test(expected = DataAccessException.class)
	public void delete_entity_Null() {
		try {
			getTestRepository().delete(null);
			fail("Exception must to be raised due to null parameter value");
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof IllegalArgumentException);
			throw e;
		}
	}

	@Test
	public void deleteAll_iterable_detached_OK() {
		List<TestEntity> entities = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			entities.add(new TestEntity(i, i + 100, i + 100));
		}
		getTestRepository().deleteAll(entities);
		getEntityManager().clear();

		assertFalse(getTestRepository().existsById(1));
		assertFalse(getTestRepository().existsById(2));
		assertFalse(getTestRepository().existsById(3));
		assertTrue(getTestRepository().existsById(4));
	}

	@Test
	public void deleteAll_iterable_detached_updated_OK() {
		List<TestEntity> entities = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			entities.add(new TestEntity(i, i + 1000, null));
		}
		getTestRepository().deleteAll(entities);
		getEntityManager().clear();

		assertFalse(getTestRepository().existsById(1));
		assertFalse(getTestRepository().existsById(2));
		assertFalse(getTestRepository().existsById(3));
		assertTrue(getTestRepository().existsById(4));
	}

	@Test
	public void deleteAll_iterable_attached_OK() {
		List<TestEntity> entities = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			entities.add(getTestRepository().getOne(i));
		}
		getTestRepository().deleteAll(entities);
		getEntityManager().clear();
		
		assertFalse(getTestRepository().existsById(1));
		assertFalse(getTestRepository().existsById(2));
		assertFalse(getTestRepository().existsById(3));
		assertTrue(getTestRepository().existsById(4));
	}

	@Test
	public void deleteAll_iterable_NotFound() {
		List<TestEntity> entities = new ArrayList<>();
		entities.add(new TestEntity(100, null));
		getTestRepository().deleteAll(entities);
		getEntityManager().clear();
		
		assertFalse(getTestRepository().existsById(100));
	}

	@Test(expected = DataAccessException.class)
	public void deleteAll_iterable_Null() {
		try {
			getTestRepository().deleteAll(null);
		} catch(Exception e) {
			assertTrue(e.getCause() instanceof IllegalArgumentException);
			throw e;
		}
	}

	@Test
	public void deleteAll_iterable_Empty() {
		getTestRepository().deleteAll(new ArrayList<TestEntity>());
		getEntityManager().clear();
		
		assertTrue(getTestRepository().count() == 20);
	}

	@Test
	public void deleteAll_OK() {
		getTestRepository().deleteAll();
		getEntityManager().clear();
		
		assertTrue(getTestRepository().count() == 0);
	}

	@Test
	public void flush_delete_OK() {
		getTestRepository().deleteById(1);
		getEntityManager().clear();

		assertFalse(getTestRepository().existsById(1));
	}

	@Test
	public void deleteInBatch_OK() {
		List<TestEntity> entities = new ArrayList<>();
		for (int i = 1; i < 3; i++) {
			entities.add(new TestEntity(i, i + 100, i + 100));
		}
		getTestRepository().deleteInBatch(entities);
		getEntityManager().clear();

		assertFalse(getTestRepository().existsById(1));
		assertFalse(getTestRepository().existsById(2));
		assertTrue(getTestRepository().existsById(3));
	}

	@Test
	public void deleteInBatch_NotFound() {
		List<TestEntity> entities = new ArrayList<>();
		entities.add(new TestEntity(100, null));
		getTestRepository().deleteInBatch(entities);
		getEntityManager().clear();
		
		assertFalse(getTestRepository().existsById(100));
	}

	@Test(expected = DataAccessException.class)
	public void deleteInBatch_Null() {
		try {
			getTestRepository().deleteInBatch(null);
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof IllegalArgumentException);
			throw e;
		}
	}

	@Test
	public void deleteInBatch_Empty() {
		getTestRepository().deleteInBatch(new ArrayList<TestEntity>());
		getEntityManager().clear();
		
		assertTrue(getTestRepository().count() == 20);
	}

	@Test
	public void deleteAllInBatch_OK() {
		getTestRepository().deleteAllInBatch();
		getEntityManager().clear();
		
		assertTrue(getTestRepository().count() == 0);
	}

}