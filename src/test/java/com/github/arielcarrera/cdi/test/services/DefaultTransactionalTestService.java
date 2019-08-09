package com.github.arielcarrera.cdi.test.services;

import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import javax.transaction.Transactional.TxType;

import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadWriteSoftDeleteRepository;

public class DefaultTransactionalTestService {

	@Inject 
	private TestReadWriteSoftDeleteRepository repo;
	
	@Inject
	UserTransaction tx;
	
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
	public boolean doSomethingNever() throws SystemException {
	    if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
		throw new IllegalStateException();
	    }
	    return true;
	}
	
	@Transactional(value=TxType.NEVER)
	public void doSomethingNeverNewTxInside(TestEntity e) {
		repo.save(e);
	}
	
	@Transactional(value=TxType.NOT_SUPPORTED)
	public boolean doSomethingNotSupported() throws SystemException {
	    if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
		throw new IllegalStateException();
	    }
	    return true;
	}
	
	@Transactional(value=TxType.NOT_SUPPORTED)
	public void doSomethingNotSupportedNewTxInside(TestEntity e) {
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
		TestEntity e2 = repo.save(e);
		e2.getStatus();
	}
	
	@Transactional(value=TxType.REQUIRES_NEW)
	public void doRollbackRequiresNew(TestEntity e) {
		repo.save(e);
		throw new RuntimeException();
	}
	
	@Transactional(value=TxType.SUPPORTS)
	public boolean doSupportsHasTransaction() throws SystemException {
	    if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
		return true;
	    }
	    return false;
	}
	
	@Transactional(value=TxType.SUPPORTS)
	public void doSomethingSupports(TestEntity e) {
		repo.save(e);
	}
	
	public void doSomethingNoWrapped(TestEntity e) {
		repo.save(e);
	}
	
	@Transactional
	public boolean testEntityManagerContains(Integer i) {
	    return repo.contains(repo.findById(i).get());
	}
}
