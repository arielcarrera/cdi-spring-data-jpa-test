package com.github.arielcarrera.cdi.test.services;

import javax.inject.Inject;

import com.github.arielcarrera.cdi.support.transactional.CustomTransactionalAnnotation;
import com.github.arielcarrera.cdi.test.repositories.TestReadWriteSoftDeleteRepository;

@CustomTransactionalAnnotation
public class CustomTransactionalTestService {

	@Inject 
	private TestReadWriteSoftDeleteRepository repo;
	
	public TestReadWriteSoftDeleteRepository getRepo() {
		return repo;
	}
	
	@CustomTransactionalAnnotation
	public void doSomething() {
		repo.deleteById(1);
	}
	
	
	@CustomTransactionalAnnotation
	public void doRollback() {
		repo.deleteById(1);
		throw new RuntimeException();
	}
}
