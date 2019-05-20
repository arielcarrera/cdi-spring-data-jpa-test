package com.github.arielcarrera.cdi.support.config;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;

import com.github.arielcarrera.cdi.support.transactional.MyTransactionInterceptor;

/**
 * {@link RepositoryProxyPostProcessor} that sets up interceptors to do transactional management

 * @author Ariel Carrera
 *
 */
public class CustomTransactionalRepositoryProxyPostProcessor implements RepositoryProxyPostProcessor {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryProxyPostProcessor#postProcess(org.springframework.aop.framework.ProxyFactory, org.springframework.data.repository.core.RepositoryInformation)
	 */
	@Override
	public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
		//TODO Add support to cache repository annotation metadata here
		factory.addAdvice(new MyTransactionInterceptor());
	}
}
