package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.UserTransaction;

import org.hibernate.LazyInitializationException;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.exceptions.DataAccessException;
import com.github.arielcarrera.cdi.repositories.ReadOnlyRepository;
import com.github.arielcarrera.cdi.test.config.JtaEnvironment;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadWriteDeleteRepository;

@Slf4j
public abstract class AbstractReadOnlyRepositoryTest {

	@ClassRule
	public static JtaEnvironment jtaEnvironment = new JtaEnvironment();

	@Rule
	public WeldInitiator weld = WeldInitiator.from(new Weld()).inject(this).build();

	@Rule
	public TestRule watcher = new TestWatcher() {
		protected void starting(Description description) {
			log.info("Starting TEST: " + description.getMethodName());
		}
	};

	@Inject
	protected EntityManager entityManager;

	@Inject
	protected TestReadWriteDeleteRepository loaderRepository;

	@Inject
	UserTransaction tx;

	public AbstractReadOnlyRepositoryTest() {
		super();
	}
	public AbstractReadOnlyRepositoryTest(EntityManager em, TestReadWriteDeleteRepository loaderRepository) {
		super();
		this.entityManager = em;
		this.loaderRepository = loaderRepository;
	}
	
	@Before
	public void load() {
		List<TestEntity> points = new ArrayList<>();
		for (int i = 1; i < 20; i++) {
			points.add(new TestEntity(i, i + 100, i + 100, LogicalDeletion.NORMAL_STATUS));
		}
		points.add(new TestEntity(20, 110, null, LogicalDeletion.DELETED_STATUS));
	
		getLoaderRepository().saveAll(points);
	}

	public TestReadWriteDeleteRepository getLoaderRepository(){
		return loaderRepository;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public abstract ReadOnlyRepository<TestEntity, Integer> getTestRepository();

	@Test
	public void entityManager_OK() {
		assertNotNull(getTestRepository().entityManager());
	}

	@Test
	public void entityManager_contains_outsideGlobalTx() {
		assertFalse(getTestRepository().contains(getTestRepository().findById(1).get()));
	}

	@Test
	public void entityManager_contains_insideGlobalTx_simpleJpaRepoMethod() throws Exception {
		tx.begin();
		try {
			assertTrue(getTestRepository().contains(getTestRepository().findById(1).get()));
		} finally {
			tx.commit();
		}
	}

	@Test
	public void entityManager_contains_insideGlobalTx_queryGeneratedByMethodName() throws Exception {
		tx.begin();
		try {
			assertTrue(getTestRepository().contains(getTestRepository().findOneById(1)));
		} finally {
			tx.commit();
		}
	}

	@Test
	public void findById_OK() {
		Optional<TestEntity> op = getTestRepository().findById(1);
		assertNotNull(op);
		assertTrue(op.isPresent());
		assertTrue(op.get().getValue().equals(101));
	}

	@Test
	public void findById_NotFound() {
		Optional<TestEntity> op = getTestRepository().findById(-1);
		assertNotNull(op);
		assertFalse(op.isPresent());
	}

	@Test
	public void findOneById_OK() {
		TestEntity e = getTestRepository().findOneById(1);
		assertNotNull(e);
		assertTrue(e.getValue().equals(101));
	}

	@Test
	public void findOneById_NotFound() {
		TestEntity e = getTestRepository().findOneById(-1);
		assertNull(e);
	}

	@Test
	public void existsById_OK() {
		assertTrue(getTestRepository().existsById(1));
	}

	@Test
	public void existsById_NotFound() {
		assertFalse(getTestRepository().existsById(-1));
	}

	@Test
	public void findAll_OK() {
		List<TestEntity> l = getTestRepository().findAll();
		assertNotNull(l);
		assertTrue(l.size() == 20);
	}

	@Test
	public void findAll_sort_OK() {
		List<TestEntity> l = getTestRepository().findAll(Sort.by(Direction.DESC, "id"));
		assertNotNull(l);
		assertTrue(l.size() == 20);
		assertTrue(l.get(0).getId().equals(20));
	}

	@Test
	public void findAll_pageable_OK() {
		Page<TestEntity> p = getTestRepository().findAll(PageRequest.of(2, 5, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 5);
		assertTrue(p.getNumber() == 2);
		assertTrue(p.getTotalElements() == 20);
		assertTrue(p.getTotalPages() == 4);
		assertTrue(p.getContent().get(0).getId().equals(10));
	}

	@Test
	public void findAll_pageable_NotFound() {
		Page<TestEntity> p = getTestRepository().findAll(PageRequest.of(10, 5));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0);
	}

	@Test
	public void findAllByID_OK() {
		List<TestEntity> l = getTestRepository()
				.findAllById(Arrays.asList(new Integer(1), new Integer(2), new Integer(3)));
		assertNotNull(l);
		assertTrue(l.size() == 3);
	}

	@Test
	public void findAllByID_Partially() {
		List<TestEntity> l = getTestRepository()
				.findAllById(Arrays.asList(new Integer(1), new Integer(2), new Integer(-1)));
		assertNotNull(l);
		assertTrue(l.size() == 2);
	}

	@Test
	public void findAllByID_NotFound() {
		List<TestEntity> l = getTestRepository()
				.findAllById(Arrays.asList(new Integer(-1), new Integer(-2), new Integer(-3)));
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	@Test
	public void count_OK() {
		long c = getTestRepository().count();
		assertTrue(c == 20);
	}

	@Test(expected = LazyInitializationException.class)
	public void getOne_NoTx() {
		TestEntity p = getTestRepository().getOne(1);
		assertNotNull(p);
		p.getValue();
	}

	@Test
	public void getOne_InTx() throws Exception {
		tx.begin();
		try {
			TestEntity p = getTestRepository().getOne(1);
			assertNotNull(p);
			assertTrue(p.getValue().equals(101));
		} finally {
			tx.commit();
		}
	}

	@Test(expected = LazyInitializationException.class)
	public void getOne_OutTx() throws Exception {
		tx.begin();
		TestEntity p;
		try {
			p = getTestRepository().getOne(1);
			assertNotNull(p);
		} finally {
			tx.commit();
		}
		assertNotNull(p);
		p.getValue();
	}

	@Test(expected = EntityNotFoundException.class)
	public void getOne_NotFound() throws Exception {
		tx.begin();
		try {
			TestEntity p = getTestRepository().getOne(-1);
			if (p != null) {
				assertNull(p.getValue());
			} else {
				assertNull(p);
			}
		} finally {
			tx.commit();
		}
	}

	@Test
	public void findAll_example_OK() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 110),
				ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		List<TestEntity> l = getTestRepository().findAll(example);
		assertNotNull(l);
		assertTrue(l.size() == 2);
		assertTrue(l.get(0).getValue().equals(110));
	}

	@Test
	public void findAll_example_NotFound() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 0),
				ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		List<TestEntity> l = getTestRepository().findAll(example);
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	@Test
	public void findAll_example_sort_OK() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 110),
				ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		List<TestEntity> l = getTestRepository().findAll(example, Sort.by(Direction.ASC, "value"));
		assertNotNull(l);
		assertTrue(l.size() == 2);
		assertTrue(l.get(0).getValue().equals(110));
	}

	@Test
	public void findAll_example_pageable_OK() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 110),
				ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		Page<TestEntity> p = getTestRepository().findAll(example, PageRequest.of(1, 1, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 1);
		assertTrue(p.getNumber() == 1);
		assertTrue(p.getTotalElements() == 2);
		assertTrue(p.getTotalPages() == 2);
		assertTrue(p.getContent().get(0).getId().equals(10));
	}

	@Test
	public void findOne_example_OK() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 101),
				ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		Optional<TestEntity> op = getTestRepository().findOne(example);
		assertNotNull(op);
		assertTrue(op.isPresent());
		assertTrue(op.get().getId().equals(1));
	}

	@Test
	public void findOne_example_NotFound() {
		Example<TestEntity> example = Example.of(new TestEntity(null, -1),
				ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		Optional<TestEntity> op = getTestRepository().findOne(example);
		assertNotNull(op);
		assertFalse(op.isPresent());
	}

	@Test(expected = DataAccessException.class)
	public void findOne_example_ErrorMultipleResults() throws Throwable {
		Example<TestEntity> example = Example.of(new TestEntity(null, 110, null),
				ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		try {
			getTestRepository().findOne(example);
		} catch (UndeclaredThrowableException e) {
			Throwable cause = e.getCause().getCause();
			assertTrue(cause instanceof NonUniqueResultException);
			assertTrue(cause.getMessage().contains("query did not return a unique result"));
			throw cause;
		}
	}

	@Test
	public void count_example_OK() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 110),
				ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		assertTrue(getTestRepository().count(example) == 2);
	}

	@Test
	public void exists_example_OK() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 110),
				ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		assertTrue(getTestRepository().exists(example));
	}

	@Test
	public void exists_example_NotFound() {
		Example<TestEntity> example = Example.of(new TestEntity(null, -1),
				ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		assertFalse(getTestRepository().exists(example));
	}

}