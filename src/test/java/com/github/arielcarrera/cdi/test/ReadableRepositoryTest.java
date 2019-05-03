package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.demos.jpacditesting.support.JtaEnvironment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadableRepository;
import com.github.arielcarrera.cdi.test.repositories.TestWritableRepository;

/**
 * Tests for Readable Repository
 * 
 * @author Ariel Carrera
 *
 */
public class ReadableRepositoryTest {

	@ClassRule
	public static JtaEnvironment jtaEnvironment = new JtaEnvironment();

	@Rule
	public WeldInitiator weld = WeldInitiator.from(new Weld()).activate(RequestScoped.class, ApplicationScoped.class)
			.inject(this).build();

	@Inject
	private EntityManager entityManager;

//    @Inject
//    private UserTransaction ut;

	@Inject
	private TestWritableRepository writableRepository;

	@Inject
	private TestReadableRepository readableRepository;

	@Before
	public void load() {
		entityManager.getTransaction().begin();
		List<TestEntity> points = new ArrayList<>();
		for (int i = 1; i < 20; i++) {
			points.add(new TestEntity(i, i + 100));
		}
		points.add(new TestEntity(20, 110));

		writableRepository.saveAll(points);

		entityManager.getTransaction().commit();
	}

	@Test
	public void findById_OK() {
		Optional<TestEntity> op = readableRepository.findById(1);
		assertNotNull(op);
		assertTrue(op.isPresent());
		assertEquals(new Integer(101), op.get().getValue());
	}

	@Test
	public void findById_NotFound() {
		Optional<TestEntity> op = readableRepository.findById(-1);
		assertNotNull(op);
		assertFalse(op.isPresent());
	}

	@Test
	public void existsById_OK() {
		assertTrue(readableRepository.existsById(1));
	}

	@Test
	public void existsById_NotFound() {
		assertFalse(readableRepository.existsById(-1));
	}

	@Test
	public void findAll_OK() {
		List<TestEntity> l = readableRepository.findAll();
		assertNotNull(l);
		assertTrue(l.size() == 20);
	}

	@Test
	public void findAll_sort_OK() {
		List<TestEntity> l = readableRepository.findAll(Sort.by(Direction.ASC, "id"));
		assertNotNull(l);
		assertTrue(l.size() == 20);
		assertEquals(new Integer(20), l.get(0).getId());
	}

	@Test
	public void findAll_pageable_OK() {
		Page<TestEntity> p = readableRepository.findAll(PageRequest.of(2, 5, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 5);
		assertTrue(p.getNumber() == 2);
		assertTrue(p.getTotalElements() == 20);
		assertTrue(p.getTotalPages() == 4);
		assertEquals(new Integer(6), p.getContent().get(0).getId());
	}

	@Test
	public void findAll_pageable_NotFound() {
		Page<TestEntity> p = readableRepository.findAll(PageRequest.of(10, 5));
		assertNotNull(p);
		assertTrue(p.getSize() == 0);
	}

	@Test
	public void findAllByID_OK() {
		List<TestEntity> l = readableRepository
				.findAllById(Arrays.asList(new Integer(1), new Integer(2), new Integer(3)));
		assertNotNull(l);
		assertTrue(l.size() == 3);
	}

	@Test
	public void findAllByID_Partially() {
		List<TestEntity> l = readableRepository
				.findAllById(Arrays.asList(new Integer(1), new Integer(2), new Integer(-1)));
		assertNotNull(l);
		assertTrue(l.size() == 2);
	}

	@Test
	public void findAllByID_NotFound() {
		List<TestEntity> l = readableRepository
				.findAllById(Arrays.asList(new Integer(-1), new Integer(-2), new Integer(-3)));
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	@Test
	public void count_OK() {
		long c = readableRepository.count();
		assertTrue(c == 20);
	}

	@Test
	public void getOne_OK() {
		TestEntity p = readableRepository.getOne(1);
		assertNotNull(p);
		assertEquals(new Integer(101), p.getValue());
	}

	@Test
	public void getOne_NotFound() {
		TestEntity p = readableRepository.getOne(-1);
		assertNull(p);
	}

	@Test
	public void findAll_example_OK() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 110),
				ExampleMatcher.matching().withIgnoreNullValues());
		List<TestEntity> l = readableRepository.findAll(example);
		assertNotNull(l);
		assertTrue(l.size() == 1);
		assertEquals(new Integer(110), l.get(0).getValue());
	}

	@Test
	public void findAll_example_NotFound() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 0),
				ExampleMatcher.matching().withIgnoreNullValues());
		List<TestEntity> l = readableRepository.findAll(example);
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	@Test
	public void findAll_example_sort_OK() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 110),
				ExampleMatcher.matching().withIgnoreNullValues());
		List<TestEntity> l = readableRepository.findAll(example, Sort.by(Direction.ASC, "value"));
		assertNotNull(l);
		assertTrue(l.size() == 2);
		assertEquals(new Integer(110), l.get(0).getValue());
	}

	@Test
	public void findAll_example_pageable_OK() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 110),
				ExampleMatcher.matching().withIgnoreNullValues());
		Page<TestEntity> p = readableRepository.findAll(example, PageRequest.of(1, 1, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 1);
		assertTrue(p.getNumber() == 1);
		assertTrue(p.getTotalElements() == 2);
		assertTrue(p.getTotalPages() == 2);
		assertEquals(new Integer(10), p.getContent().get(0).getId());
	}

	@Test
	public void findOne_example_OK() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 101),
				ExampleMatcher.matching().withIgnoreNullValues());
		Optional<TestEntity> op = readableRepository.findOne(example);
		assertNotNull(op);
		assertTrue(op.isPresent());
		assertEquals(new Integer(1), op.get().getId());
	}

	@Test
	public void findOne_example_NotFound() {
		Example<TestEntity> example = Example.of(new TestEntity(null, -1),
				ExampleMatcher.matching().withIgnoreNullValues());
		Optional<TestEntity> op = readableRepository.findOne(example);
		assertNotNull(op);
		assertFalse(op.isPresent());
	}

	@Test(expected = IncorrectResultSizeDataAccessException.class)
	public void findOne_example_ErrorMultipleResults() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 110),
				ExampleMatcher.matching().withIgnoreNullValues());
		readableRepository.findOne(example);
	}

	@Test
	public void count_example_OK() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 110),
				ExampleMatcher.matching().withIgnoreNullValues());
		assertTrue(readableRepository.count(example) == 2);
	}

	@Test
	public void exists_example_OK() {
		Example<TestEntity> example = Example.of(new TestEntity(null, 110),
				ExampleMatcher.matching().withIgnoreNullValues());
		assertTrue(readableRepository.exists(example));
	}

	@Test
	public void exists_example_NotFound() {
		Example<TestEntity> example = Example.of(new TestEntity(null, -1),
				ExampleMatcher.matching().withIgnoreNullValues());
		assertFalse(readableRepository.exists(example));
	}

}