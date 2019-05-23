package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;

import com.github.arielcarrera.cdi.exceptions.DataAccessException;
import com.github.arielcarrera.cdi.repositories.ReadWriteRepository;
import com.github.arielcarrera.cdi.test.entities.TestEntity;

public abstract class AbstractReadWriteRepositoryTest extends AbstractReadOnlyRepositoryTest {

	public AbstractReadWriteRepositoryTest() {
		super();
	}

	@Override
	public abstract ReadWriteRepository<TestEntity, Integer> getTestRepository();

	@Test
	public void save_new_OK() {
		TestEntity result = getTestRepository().save(new TestEntity(21, 121));
		getEntityManager().clear();
		assertNotNull(result);
		assertTrue(result.getId().equals(21));
		
		assertTrue(getTestRepository().findAll().size() == 21);
		TestEntity e = getTestRepository().getOne(21);
		assertNotNull(e);
		assertTrue(e.getId().equals(21));
		assertTrue(e.getValue().equals(121));
	}

	@Test
	public void save_update_OK() {
		TestEntity result = getTestRepository().save(new TestEntity(1, 121));
		getEntityManager().clear();
		assertNotNull(result);
		assertTrue(result.getId().equals(1));
		
		assertTrue(getTestRepository().findAll().size() == 20);
		TestEntity e = getTestRepository().getOne(1);
		assertNotNull(e);
		assertTrue(e.getId().equals(1));
		assertTrue(e.getValue().equals(121));
	}

	@Test(expected = DataAccessException.class)
	public void save_new_NotUnique() {
		try {
			getTestRepository().save(new TestEntity(21, 121, 101));
			fail("Exception must to be raised due to non unique constraint violation");
		} catch(DataAccessException e) {
			assertTrue(e.getCause().getCause() instanceof ConstraintViolationException);
			throw e;
		}
	}

	@Test(expected = DataAccessException.class)
	public void save_update_NotUnique() {
		try {
			getTestRepository().save(new TestEntity(2, 102, 101));
			fail("Exception must to be raised due to non unique constraint violation");
		} catch(DataAccessException e) {
			assertTrue(e.getCause().getCause() instanceof ConstraintViolationException);
			throw e;
		}
	}

	@Test
	public void saveAll_new_OK() {
		List<TestEntity> entities = new ArrayList<>();
		for (int i = 21; i < 24; i++) {
			entities.add(new TestEntity(i, i + 100, i + 100));
		}
		Iterable<TestEntity> result = getLoaderRepository().saveAll(entities);
		getEntityManager().clear();
		
		assertNotNull(result);
		int count = 0;
		boolean has21 = false, has22 = false, has23 = false;
		for (TestEntity testEntity : result) {
			count++;
			switch (testEntity.getId()) {
			case 21:
				has21 = true;
				break;
			case 22:
				has22 = true;
				break;
			case 23:
				has23 = true;
				break;
			}
		}
		assertTrue(count == 3);
		assertTrue(has21 && has22 && has23);
		
		List<TestEntity> l = getTestRepository().findAll();
		assertNotNull(l);
		assertTrue(l.size() == 23);
	}

	@Test
	public void saveAll_update_OK() {
		List<TestEntity> entities = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			entities.add(new TestEntity(i, i + 200, i + 200));
		}
		Iterable<TestEntity> result = getLoaderRepository().saveAll(entities);
		assertNotNull(result);
		int count = 0;
		boolean has1 = false, has2 = false, has3 = false;
		for (TestEntity testEntity : result) {
			count++;
			switch (testEntity.getId()) {
			case 1:
				assertTrue(testEntity.getValue().equals(201));
				has1 = true;
				break;
			case 2:
				assertTrue(testEntity.getValue().equals(202));
				has2 = true;
				break;
			case 3:
				assertTrue(testEntity.getValue().equals(203));
				has3 = true;
				break;
			}
		}
		assertTrue(count == 3);
		assertTrue(has1 && has2 && has3);
		
		List<TestEntity> l = getTestRepository().findAll();
		assertNotNull(l);
		assertTrue(l.size() == 20);
		
		TestEntity e = getTestRepository().getOne(1);
		assertNotNull(e);
		assertTrue(e.getValue().equals(201));
		e = getTestRepository().getOne(2);
		assertNotNull(e);
		assertTrue(e.getValue().equals(202));
		e = getTestRepository().getOne(3);
		assertNotNull(e);
		assertTrue(e.getValue().equals(203));
	}

	@Test(expected = DataAccessException.class)
	public void saveAll_NotUnique() {
		List<TestEntity> entities = new ArrayList<>();
		entities.add(new TestEntity(21, 221, 101));
		for (int i = 22; i < 24; i++) {
			entities.add(new TestEntity(i, i + 200, i + 200));
		}
		try {
			getTestRepository().saveAll(entities);
			fail("Exception must to be raised due to non unique constraint violation");
		} catch(DataAccessException e) {
			getEntityManager().clear();
			assertTrue(e.getCause().getCause() instanceof ConstraintViolationException);
			assertFalse(getTestRepository().existsById(21));
			assertFalse(getTestRepository().existsById(22));
			assertFalse(getTestRepository().existsById(23));
			throw e;
		} 
	}

	@Test
	public void saveAndFlush_OK() {
		TestEntity result = getTestRepository().saveAndFlush(new TestEntity(21, 121));
		getEntityManager().clear();
		
		assertNotNull(result);
		assertTrue(result.getId().equals(21));
		
		assertTrue(getTestRepository().findAll().size() == 21);
		TestEntity e = getTestRepository().getOne(21);
		assertNotNull(e);
		assertTrue(e.getId().equals(21));
		assertTrue(e.getValue().equals(121));
	}

	@Test
	public void flush_OK() {
		getEntityManager().getTransaction().begin();
		TestEntity result = getTestRepository().save(new TestEntity(21, 121));
		getTestRepository().flush();
		getEntityManager().getTransaction().commit();
		getEntityManager().clear();
		
		assertNotNull(result);
		assertTrue(result.getId().equals(21));
		
		assertTrue(getTestRepository().findAll().size() == 21);
		TestEntity e = getTestRepository().getOne(21);
		assertNotNull(e);
		assertTrue(e.getId().equals(21));
		assertTrue(e.getValue().equals(121));
	}

}