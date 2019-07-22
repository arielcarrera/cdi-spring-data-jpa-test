package com.github.arielcarrera.cdi.test.services;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadWriteSoftDeleteRepository;

public class DefaultTransactionalTestService2 {

	@Inject 
	private DefaultTransactionalTestService svc1;
	
	@Inject 
	private TestReadWriteSoftDeleteRepository repo;
	
	public TestReadWriteSoftDeleteRepository getRepo() {
		return repo;
	}
	
	//Transactional Service with inner invocation requires_new (warning: call to methods with @Transaction with a proxy reference like this) 
	@Transactional(value=TxType.REQUIRED)
	public void doSomethingRequiresNew(TestEntity e, TestEntity e2) {
		repo.save(e);
		svc1.doSomethingRequiresNew(e2);
		throw new RuntimeException();
	}
	
}
