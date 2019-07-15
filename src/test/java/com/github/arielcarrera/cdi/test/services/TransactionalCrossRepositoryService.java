package com.github.arielcarrera.cdi.test.services;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadWriteDeleteRepository;
import com.github.arielcarrera.cdi.test.repositories.TestReadWriteSoftDeleteRepository;

public class TransactionalCrossRepositoryService {

	@Inject 
	private TestReadWriteSoftDeleteRepository repo;
	
	@Inject 
	private TestReadWriteDeleteRepository repo2;
	
	
	@Transactional
	public void sameRepo(TestEntity e) {
		TestEntity myEntity = repo.save(e);
		repo.delete(myEntity);
	}
	
	@Transactional
	public void delete(TestEntity e) {
		repo.delete(e);
	}
	
	
	@Transactional
	public void crossRepo(TestEntity e) {
	    //if entityManager is not shared between same request (dependent pseudo scope) it will fail
		TestEntity myEntity = repo.save(e);
		//try to get from persistence context
		Optional<TestEntity> myEntity2 = repo2.findById(myEntity.getId());
		if (!myEntity2.isPresent()) throw new RuntimeException("No comparten el mismo persistence context");
		repo2.delete(e);
	}
	
	@Transactional
	public void crossRepoMultipleSaves(TestEntity e, TestEntity e2) {
	    //if entityManager is not shared between same request (dependent pseudo scope) it will fail
		TestEntity myEntity = repo.save(e);
		
		TestEntity myEntity2 = repo2.save(e2);
	}
	
	@Transactional
	public void crossRepoMultipleSavesRollback(TestEntity e, TestEntity e2) {
	    //if entityManager is not shared between same request (dependent pseudo scope) it will fail
		TestEntity myEntity = repo.save(e);
		
		TestEntity myEntity2 = repo2.save(e2);
		
		throw new RuntimeException();
	}
	
}
