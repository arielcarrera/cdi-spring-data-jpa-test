package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.exceptions.DataAccessException;
import com.github.arielcarrera.cdi.exceptions.NotSupportedException;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadWriteSoftDeleteRepository;

/**
 * Tests for Writable Repository with soft delete operations
 * 
 * @author Ariel Carrera
 *
 */
public class ReadWriteSoftDeleteRepositoryTest extends AbstractReadOnlySoftDeleteRepositoryTest {

	@Inject
	protected TestReadWriteSoftDeleteRepository testRepository;

	@Override
	public TestReadWriteSoftDeleteRepository getTestRepository() {
		return testRepository;
	}


	@Test
	public void deleteById_OK() {
		getTestRepository().deleteById(1);
		getEntityManager().clear();

		assertTrue(getTestRepository().getOne(1).getStatus() == LogicalDeletion.DELETED_STATUS);
	}

	@Test(expected = DataAccessException.class)
	public void deleteById_NotFound() {
		try {
			getTestRepository().deleteById(200);
			fail("Exception must to be raised due to entity not exist");
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof EmptyResultDataAccessException);
			throw e;
		}
	}

	@Test(expected = DataAccessException.class)
	public void deleteById_Null() {
		try {
			getTestRepository().deleteById(null);
			fail("Exception must to be raised due to null parameter value");
		} catch (DataAccessException e) {
			assertTrue(e.getCause() instanceof IllegalArgumentException);
			throw e;
		}
	}

	@Test
	public void delete_attached_entity_OK() {
		getTestRepository().delete(getTestRepository().getOne(1));
		getEntityManager().clear();

		assertTrue(getTestRepository().getOne(1).getStatus() == LogicalDeletion.DELETED_STATUS);
	}

	@Test
	public void delete_detached_entity_OK() {
		getTestRepository().delete(new TestEntity(1, null));
		getEntityManager().clear();

		assertTrue(getTestRepository().getOne(1).getStatus() == LogicalDeletion.DELETED_STATUS);
	}

	@Test
	public void delete_detached_entity_updated_OK() {
		getTestRepository().delete(new TestEntity(1, 1));
		getEntityManager().clear();

		assertTrue(getTestRepository().getOne(1).getStatus() == LogicalDeletion.DELETED_STATUS);
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
		//it will do insert and update (With default repository implementation)
		assertTrue(getTestRepository().getOne(100).getStatus() == LogicalDeletion.DELETED_STATUS);
	}

	@Test(expected = DataAccessException.class)
	public void delete_entity_Null() {
		try {
			getTestRepository().delete(null);
			fail("Exception must to be raised due to null parameter value");
		} catch (DataAccessException e) {
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

		assertTrue(getTestRepository().getOne(1).getStatus() == LogicalDeletion.DELETED_STATUS);
		assertTrue(getTestRepository().getOne(2).getStatus() == LogicalDeletion.DELETED_STATUS);
		assertTrue(getTestRepository().getOne(3).getStatus() == LogicalDeletion.DELETED_STATUS);
		assertTrue(getTestRepository().getOne(4).getStatus() == LogicalDeletion.NORMAL_STATUS);
	}

	@Test
	public void deleteAll_iterable_detached_updated_OK() {
		List<TestEntity> entities = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			entities.add(new TestEntity(i, i + 1000, null));
		}
		getTestRepository().deleteAll(entities);
		getEntityManager().clear();

		assertTrue(getTestRepository().getOne(1).getStatus() == LogicalDeletion.DELETED_STATUS);
		assertTrue(getTestRepository().getOne(2).getStatus() == LogicalDeletion.DELETED_STATUS);
		assertTrue(getTestRepository().getOne(3).getStatus() == LogicalDeletion.DELETED_STATUS);
		assertTrue(getTestRepository().getOne(4).getStatus() == LogicalDeletion.NORMAL_STATUS);
	}

	@Test
	public void deleteAll_iterable_attached_OK() {
		List<TestEntity> entities = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			entities.add(getTestRepository().getOne(i));
		}
		getTestRepository().deleteAll(entities);
		getEntityManager().clear();

		assertTrue(getTestRepository().getOne(1).getStatus() == LogicalDeletion.DELETED_STATUS);
		assertTrue(getTestRepository().getOne(2).getStatus() == LogicalDeletion.DELETED_STATUS);
		assertTrue(getTestRepository().getOne(3).getStatus() == LogicalDeletion.DELETED_STATUS);
		assertTrue(getTestRepository().getOne(4).getStatus() == LogicalDeletion.NORMAL_STATUS);
	}

	@Test
	public void deleteAll_iterable_NotFound() {
		List<TestEntity> entities = new ArrayList<>();
		entities.add(new TestEntity(100, null));
		getTestRepository().deleteAll(entities);
		getEntityManager().clear();

		assertTrue(getTestRepository().getOne(100).getStatus() == LogicalDeletion.DELETED_STATUS);
	}

	@Test(expected = DataAccessException.class)
	public void deleteAll_iterable_Null() {
		try {
			getTestRepository().deleteAll(null);
		} catch (DataAccessException e) {
			assertTrue(e.getCause() instanceof IllegalArgumentException);
			throw e;
		}
	}

	@Test
	public void deleteAll_iterable_Empty() {
		getTestRepository().deleteAll(new ArrayList<TestEntity>());
		getEntityManager().clear();
		
		List<TestEntity> list = getTestRepository().findAll();
		assertTrue(list.size() == 20);
		list.forEach(e -> assertTrue(e.getStatus() == LogicalDeletion.NORMAL_STATUS || (e.getId() == 20 && e.getStatus() == LogicalDeletion.DELETED_STATUS)));
	}

	@Test
	public void deleteAll_OK() {
		getTestRepository().deleteAll();
		getEntityManager().clear();

		List<TestEntity> list = getTestRepository().findAll();
		assertTrue(list.size() == 20);
		list.forEach(e -> assertTrue(e.getStatus() == LogicalDeletion.DELETED_STATUS));
	}

	@Test
	public void flush_delete_OK() {
		getEntityManager().getTransaction().begin();
		getTestRepository().deleteById(1);
		getTestRepository().flush();
		getEntityManager().clear();
		TestEntity e1 = getTestRepository().getOne(1);
		assertTrue(e1.getStatus() == LogicalDeletion.DELETED_STATUS);
		getEntityManager().getTransaction().commit();
		getEntityManager().clear();

		e1 = getTestRepository().getOne(1);
		assertTrue(e1.getStatus() == LogicalDeletion.DELETED_STATUS);
	}
	

	@Test
	public void flush_delete_Rollback() {
		getEntityManager().getTransaction().begin();
		getTestRepository().deleteById(1);
		getTestRepository().flush();
		getEntityManager().clear();
		TestEntity e1 = getTestRepository().getOne(1);
		assertTrue(e1.getStatus() == LogicalDeletion.DELETED_STATUS);
		getEntityManager().getTransaction().rollback();
		getEntityManager().clear();

		e1 = getTestRepository().getOne(1);
		assertTrue(e1.getStatus() == LogicalDeletion.NORMAL_STATUS);                 
	}

	@Test(expected=DataAccessException.class)
	public void deleteInBatch_OK() {
		List<TestEntity> entities = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			entities.add(new TestEntity(i, i + 100, i + 100));
		}
		try {
			getTestRepository().deleteInBatch(entities);
			fail("Exception must to be raised");
		} catch(DataAccessException e) {
			assertTrue(e.getCause() instanceof NotSupportedException);
			throw e;
		} 
	}

	//TODO improve delete default implementation for not found
	@Test(expected = DataAccessException.class)
	public void deleteInBatch_NotFound() {
		List<TestEntity> entities = new ArrayList<>();
		entities.add(new TestEntity(100, null));
		try {
			getTestRepository().deleteInBatch(entities);
			fail("Exception must to be raised");
		} catch(DataAccessException e) {
			assertTrue(e.getCause() instanceof NotSupportedException);
			throw e;
		} 
		
	}

	@Test(expected = DataAccessException.class)
	public void deleteInBatch_Null() {
		try {
			getTestRepository().deleteInBatch(null);
		} catch (DataAccessException e) {
			assertTrue(e.getCause() instanceof IllegalArgumentException);
			throw e;
		}
	}

	@Test(expected=DataAccessException.class)
	public void deleteInBatch_Empty() {
		try {
			getTestRepository().deleteInBatch(new ArrayList<TestEntity>());
			fail("Exception must to be raised");
		} catch(DataAccessException e) {
			assertTrue(e.getCause() instanceof NotSupportedException);

			getEntityManager().clear();
			List<TestEntity> list = getTestRepository().findAll();
			assertTrue(list.size() == 20);
			list.forEach(a -> assertTrue(a.getStatus() == LogicalDeletion.NORMAL_STATUS || (a.getId() == 20 && a.getStatus() == LogicalDeletion.DELETED_STATUS)));
			throw e;
		} 
		
	}

	@Test(expected=DataAccessException.class)
	public void deleteAllInBatch_OK() {
		try {
			getTestRepository().deleteAllInBatch();
			fail("Exception must to be raised");
		} catch(DataAccessException e) {
			assertTrue(e.getCause() instanceof NotSupportedException);
			
			getEntityManager().clear();
			List<TestEntity> list = getTestRepository().findAll();
			assertTrue(list.size() == 20);
			list.forEach(a -> assertTrue(a.getStatus() == LogicalDeletion.NORMAL_STATUS || (a.getId() == 20 && a.getStatus() == LogicalDeletion.DELETED_STATUS)));
			throw e;
		} 

	}
	
	

}