package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.repositories.ReadOnlySoftDeleteRepository;
import com.github.arielcarrera.cdi.test.entities.TestEntity;

public abstract class AbstractReadOnlySoftDeleteRepositoryTest extends AbstractReadOnlyRepositoryTest {

	@Override
	public abstract ReadOnlySoftDeleteRepository<TestEntity, Integer> getTestRepository();

	@Test
	public void findByStatus_OK() {
		List<TestEntity> l = getTestRepository().findByStatus(LogicalDeletion.NORMAL_STATUS);
		assertNotNull(l);
		assertTrue(l.size() == 19);
	}

	@Test
	public void findByStatus_NoResult() {
		List<TestEntity> l = getTestRepository().findByStatus(LogicalDeletion.DRAFT_STATUS);
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	@Test
	public void findByStatus_pageable_OK() {
		Page<TestEntity> p = getTestRepository().findByStatus(LogicalDeletion.NORMAL_STATUS, PageRequest.of(1, 5, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 5);
		assertTrue(p.getNumber() == 1);
		assertTrue(p.getTotalElements() == 19);
		assertTrue(p.getTotalPages() == 4);
		assertTrue(p.getContent().get(0).getId().equals(14));
	}

	@Test
	public void findByStatus_pageable_PageNotFound() {
		Page<TestEntity> p = getTestRepository().findByStatus(LogicalDeletion.NORMAL_STATUS, PageRequest.of(10, 5));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0);
		assertTrue(p.getTotalPages() == 4);
	}

	@Test
	public void findByStatusIn_OK() {
		List<TestEntity> l = getTestRepository().findByStatusIn(Arrays.asList(LogicalDeletion.NORMAL_STATUS, LogicalDeletion.DELETED_STATUS));
		assertNotNull(l);
		assertTrue(l.size() == 20);
	}

	@Test
	public void findByStatusIn_NoResult() {
		List<TestEntity> l = getTestRepository().findByStatusIn(Arrays.asList(LogicalDeletion.DRAFT_STATUS, 3333, 4444));
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	@Test
	public void findByStatusIn_Empty() {
		List<TestEntity> l = getTestRepository().findByStatusIn(Collections.emptyList());
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	@Test
	public void findByStatusIn_pageable_OK() {
		Page<TestEntity> p = getTestRepository().findByStatusIn(Arrays.asList(LogicalDeletion.NORMAL_STATUS, LogicalDeletion.DELETED_STATUS), PageRequest.of(1, 5, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 5);
		assertTrue(p.getNumber() == 1);
		assertTrue(p.getTotalElements() == 20);
		assertTrue(p.getTotalPages() == 4);
		assertTrue(p.getContent().get(0).getId().equals(15));
	}

	@Test
	public void findByStatusIn_pageable_PageNotFound() {
		Page<TestEntity> p = getTestRepository().findByStatusIn(Arrays.asList(LogicalDeletion.NORMAL_STATUS, LogicalDeletion.DELETED_STATUS), PageRequest.of(10, 5));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0);
		assertTrue(p.getTotalPages() == 4);
	}

	@Test
	public void findByStatusIn_pageable_NoResult() {
		Page<TestEntity> p = getTestRepository().findByStatusIn(Arrays.asList(LogicalDeletion.DRAFT_STATUS, 3333, 4444), PageRequest.of(1, 10));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0);
	}

	@Test
	public void findByStatusIn_pageable_Empty() {
		Page<TestEntity> p = getTestRepository().findByStatusIn(Collections.emptyList(), PageRequest.of(0, 10));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0);
	}

	@Test
	public void findByStatusNot_OK() {
		List<TestEntity> l = getTestRepository().findByStatusNot(LogicalDeletion.NORMAL_STATUS);
		assertNotNull(l);
		assertTrue(l.size() == 1);
	}

	@Test
	public void findByStatusNot_NoResult() {
		getLoaderRepository().deleteById(20);
		getEntityManager().clear();
		List<TestEntity> l = getTestRepository().findByStatusNot(LogicalDeletion.NORMAL_STATUS);
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	@Test
	public void findByStatusNot_pageable_OK() {
		Page<TestEntity> p = getTestRepository().findByStatusNot(LogicalDeletion.DELETED_STATUS, PageRequest.of(1, 5, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 5);
		assertTrue(p.getNumber() == 1);
		assertTrue(p.getTotalElements() == 19);
		assertTrue(p.getTotalPages() == 4);
		assertTrue(p.getContent().get(0).getId().equals(14));
	}

	@Test
	public void findByStatusNot_pageable_PageNotFound() {
		Page<TestEntity> p = getTestRepository().findByStatusNot(LogicalDeletion.NORMAL_STATUS, PageRequest.of(3, 5));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0);
		assertTrue(p.getTotalPages() == 1);
	}

	@Test
	public void findByStatusNotIn_OK() {
		List<TestEntity> l = getTestRepository().findByStatusNotIn(Arrays.asList(LogicalDeletion.DRAFT_STATUS, LogicalDeletion.DELETED_STATUS));
		assertNotNull(l);
		assertTrue(l.size() == 19);
	}

	@Test
	public void findByStatusNotIn_NoResult() {
		List<TestEntity> l = getTestRepository().findByStatusNotIn(Arrays.asList(LogicalDeletion.NORMAL_STATUS, LogicalDeletion.DELETED_STATUS, 4444));
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	//TODO improve findByStatus not in clause for empty collections
	@Test
	public void findByStatusNotIn_Empty() {
		List<TestEntity> l = getTestRepository().findByStatusNotIn(Collections.emptyList());
		assertNotNull(l);
		assertTrue(l.size() == 0); //because it has zero pages.
	}

	@Test
	public void findByStatusNotIn_pageable_OK() {
		Page<TestEntity> p = getTestRepository().findByStatusNotIn(Arrays.asList(LogicalDeletion.DRAFT_STATUS, LogicalDeletion.DELETED_STATUS), PageRequest.of(1, 5, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 5);
		assertTrue(p.getNumber() == 1);
		assertTrue(p.getTotalElements() == 19);
		assertTrue(p.getTotalPages() == 4);
		assertTrue(p.getContent().get(0).getId().equals(14));
	}

	@Test
	public void findByStatusNotIn_pageable_PageNotFound() {
		Page<TestEntity> p = getTestRepository().findByStatusNotIn(Arrays.asList(LogicalDeletion.NORMAL_STATUS, LogicalDeletion.DRAFT_STATUS), PageRequest.of(2, 5));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0);
		assertTrue(p.getTotalPages() == 1);
	}

	@Test
	public void findByStatusNotIn_pageable_NoResult() {
		Page<TestEntity> p = getTestRepository().findByStatusNotIn(Arrays.asList(LogicalDeletion.NORMAL_STATUS, LogicalDeletion.DELETED_STATUS, 4444), PageRequest.of(1, 10));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0);
	}

	//TODO improve findByStatus not in clause for empty collections
	@Test
	public void findByStatusNotIn_pageable_Empty() {
		Page<TestEntity> p = getTestRepository().findByStatusNotIn(Collections.emptyList(), PageRequest.of(0, 10));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0); //because it has zero pages.
		assertTrue(p.getNumber() == 0);
		assertTrue(p.getTotalElements() == 0);
		assertTrue(p.getTotalPages() == 0);
	}

	@Test
	public void countByStatus_OK() {
		long c = getTestRepository().countByStatus(LogicalDeletion.NORMAL_STATUS);
		assertTrue(c == 19);
	}

	@Test
	public void countByStatusIn_OK() {
		long c = getTestRepository().countByStatusIn(Arrays.asList(LogicalDeletion.NORMAL_STATUS, LogicalDeletion.DELETED_STATUS));
		assertTrue(c == 20);
	}

	@Test
	public void countByStatusNot_OK() {
		long c = getTestRepository().countByStatusNot(LogicalDeletion.NORMAL_STATUS);
		assertTrue(c == 1);
	}

	@Test
	public void countByStatusNotIn_OK() {
		long c = getTestRepository().countByStatusNotIn(Arrays.asList(LogicalDeletion.DELETED_STATUS));
		assertTrue(c == 19);
	}

	@Test
	public void findAllStatusActive_OK() {
		List<TestEntity> l = getTestRepository().findAllStatusActive();
		assertNotNull(l);
		assertTrue(l.size() == 19);
	}

	@Test
	public void findAllStatusActive_pageable_OK() {
		Page<TestEntity> p = getTestRepository().findAllStatusActive(PageRequest.of(1, 5, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 5);
		assertTrue(p.getNumber() == 1);
		assertTrue(p.getTotalElements() == 19);
		assertTrue(p.getTotalPages() == 4);
		assertTrue(p.getContent().get(0).getId().equals(14));
	}

	@Test
	public void findAllStatusActive_pageable_PageNotFound() {
		Page<TestEntity> p = getTestRepository().findAllStatusActive(PageRequest.of(10, 10, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0);
		assertTrue(p.getTotalElements() == 19);
		assertTrue(p.getTotalPages() == 2);
	}

	@Test
	public void findAllStatusNotDeleted_OK() {
		List<TestEntity> l = getTestRepository().findAllStatusNotDeleted();
		assertNotNull(l);
		assertTrue(l.size() == 19);
	}

	@Test
	public void findAllStatusNotDeleted_pageable_OK() {
		Page<TestEntity> p = getTestRepository().findAllStatusNotDeleted(PageRequest.of(1, 5, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 5);
		assertTrue(p.getNumber() == 1);
		assertTrue(p.getTotalElements() == 19);
		assertTrue(p.getTotalPages() == 4);
		assertTrue(p.getContent().get(0).getId().equals(14));
	}

	@Test
	public void findAllStatusNotDeleted_pageable_PageNotFound() {
		Page<TestEntity> p = getTestRepository().findAllStatusNotDeleted(PageRequest.of(10, 10, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0);
		assertTrue(p.getTotalElements() == 19);
		assertTrue(p.getTotalPages() == 2);
	}

	@Test
	public void findAllStatusDrafted_OK() {
		getLoaderRepository().save(new TestEntity(21, 121, null, LogicalDeletion.DRAFT_STATUS));
		getEntityManager().clear();
		
		List<TestEntity> l = getTestRepository().findAllStatusDrafted();
		assertNotNull(l);
		assertTrue(l.size() == 1);
		
	}

	@Test
	public void findAllStatusDrafted_pageable_OK() {
		getLoaderRepository().saveAll(Arrays.asList(new TestEntity(21, 121, null, LogicalDeletion.DRAFT_STATUS),
				new TestEntity(22, 122, null, LogicalDeletion.DRAFT_STATUS),
				new TestEntity(23, 123, null, LogicalDeletion.DRAFT_STATUS),
				new TestEntity(24, 124, null, LogicalDeletion.DRAFT_STATUS),
				new TestEntity(25, 125, null, LogicalDeletion.DRAFT_STATUS),
				new TestEntity(26, 126, null, LogicalDeletion.DRAFT_STATUS)));
		getEntityManager().clear();
		
		Page<TestEntity> p = getTestRepository().findAllStatusDrafted(PageRequest.of(1, 3, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 3);
		assertTrue(p.getNumber() == 1);
		assertTrue(p.getTotalElements() == 6);
		assertTrue(p.getTotalPages() == 2);
		assertTrue(p.getContent().get(0).getId().equals(23));
	}

	@Test
	public void findAllStatusDrafted_pageable_PageNotFound() {
		getLoaderRepository().save(new TestEntity(21, 121, null, LogicalDeletion.DRAFT_STATUS));
		getEntityManager().clear();
		
		Page<TestEntity> p = getTestRepository().findAllStatusDrafted(PageRequest.of(1, 10, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0);
		assertTrue(p.getTotalPages() == 1);
	}

	@Test
	public void findAllStatusDeleted_OK() {
		List<TestEntity> l = getTestRepository().findAllStatusDeleted();
		assertNotNull(l);
		assertTrue(l.size() == 1);
	}

	@Test
	public void findAllStatusDeleted_pageable_OK() {
		Page<TestEntity> p = getTestRepository().findAllStatusDeleted(PageRequest.of(0, 5, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 1);
		assertTrue(p.getNumber() == 0);
		assertTrue(p.getTotalElements() == 1);
		assertTrue(p.getTotalPages() == 1);
		assertTrue(p.getContent().get(0).getId().equals(20));
	}

	@Test
	public void findAllStatusDeleted_pageable_PageNotFound() {
		Page<TestEntity> p = getTestRepository().findAllStatusDeleted(PageRequest.of(10, 10, Sort.by(Direction.DESC, "id")));
		assertNotNull(p);
		assertTrue(p.getNumberOfElements() == 0);
		assertTrue(p.getTotalElements() == 1);
		assertTrue(p.getTotalPages() == 1);
	}

	@Test
	public void countAllStatusActive_OK() {
		long c = getTestRepository().countAllStatusActive();
		assertTrue(c == 19);
	}

	@Test
	public void countAllStatusNotDeleted_OK() {
		long c = getTestRepository().countAllStatusNotDeleted();
		assertTrue(c == 19);
	}

	@Test
	public void countAllStatusDrafted_OK() {
		long c = getTestRepository().countAllStatusDrafted();
		assertTrue(c == 0);
	}

	@Test
	public void countAllStatusDeleted_OK() {
		long c = getTestRepository().countAllStatusDeleted();
		assertTrue(c == 1);
	}

}