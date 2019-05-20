package com.github.arielcarrera.cdi.test.services;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadWriteSoftDeleteRepository;

public class DefaultTransactionalTestService {

	@Inject 
	private TestReadWriteSoftDeleteRepository repo;
	
	public TestReadWriteSoftDeleteRepository getRepo() {
		return repo;
	}
	
	@Transactional
	public void doSomething(TestEntity e) {
		repo.save(e);
	}
	
	@Transactional
	public void doRollback(TestEntity e) {
		repo.save(e);
		throw new RuntimeException();
	}
	
	
	@Transactional(value=TxType.MANDATORY)
	public void doSomethingMandatory(TestEntity e) {
		repo.save(e);
	}
	
	@Transactional(value=TxType.NEVER)
	public void doSomethingNever(TestEntity e) {
		repo.save(e);
	}
	
	@Transactional(value=TxType.NOT_SUPPORTED)
	public void doSomethingNotSupported(TestEntity e) {
		repo.save(e);
	}
	
	@Transactional(value=TxType.REQUIRED)
	public void doSomethingRequired(TestEntity e) {
		repo.save(e);
	}
	
	@Transactional(value=TxType.REQUIRED)
	public void doRollbackRequired(TestEntity e) {
		repo.save(e);
		throw new RuntimeException();
	}
	
	@Transactional(value=TxType.REQUIRES_NEW)
	public void doSomethingRequiresNew(TestEntity e) {
		repo.save(e);
	}
	
	@Transactional(value=TxType.REQUIRES_NEW)
	public void doRollbackRequiresNew(TestEntity e) {
		repo.save(e);
		throw new RuntimeException();
	}
	
	@Transactional(value=TxType.SUPPORTS)
	public void doSomethingSupports(TestEntity e) {
		repo.save(e);
	}
	
	public void doSomethingNoWrapped(TestEntity e) {
		repo.save(e);
	}
}
