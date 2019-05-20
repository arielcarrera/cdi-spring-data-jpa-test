package com.github.arielcarrera.cdi.test.repositories;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.data.repository.cdi.Eager;

import com.github.arielcarrera.cdi.repositories.ReadWriteDeleteRepository;
import com.github.arielcarrera.cdi.test.entities.TestEntity;

@Eager
public interface TestDefaultTransactionalRepository extends ReadWriteDeleteRepository<TestEntity, Integer>{

	@Transactional
	default void customTransactionalWrappedMethod(int id) {
		deleteById(id);
	}
	
	@Transactional(value=TxType.MANDATORY)
	default void customMandatoryTransactionalWrappedMethod(int id) {
		deleteById(id);
	}
	
	@Transactional(value=TxType.NEVER)
	default void customNeverTransactionalWrappedMethod(int id) {
		deleteById(id);
	}
	
	@Transactional(value=TxType.NOT_SUPPORTED)
	default void customNotSupportedTransactionalWrappedMethod(int id) {
		deleteById(id);
	}
	
	@Transactional(value=TxType.REQUIRED)
	default void customRequiredTransactionalWrappedMethod(int id) {
		deleteById(id);
	}
	
	@Transactional(value=TxType.REQUIRES_NEW)
	default void customRequiresNewTransactionalWrappedMethod(int id) {
		deleteById(id);
	}
	
	@Transactional(value=TxType.SUPPORTS)
	default void customSupportsNewTransactionalWrappedMethod(int id) {
		deleteById(id);
	}
	
	default void customNoTransactionalWrappedMethod(int id) {
		deleteById(id);
	}
}
