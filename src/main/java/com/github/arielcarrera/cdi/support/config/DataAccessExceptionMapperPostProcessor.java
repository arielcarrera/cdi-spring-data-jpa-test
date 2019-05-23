package com.github.arielcarrera.cdi.support.config;

import java.io.Serializable;

import javax.transaction.RollbackException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.lang.Nullable;

import com.github.arielcarrera.cdi.exceptions.DataAccessException;

/**
 * {@link RepositoryProxyPostProcessor} that sets up interceptors to do exception mapping
 * 
 * @author Ariel Carrera
 */
public class DataAccessExceptionMapperPostProcessor implements RepositoryProxyPostProcessor {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryProxyPostProcessor#postProcess(org.springframework.aop.framework.ProxyFactory, org.springframework.data.repository.core.RepositoryInformation)
	 */
	@Override
	public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
		factory.addAdvice(new DataAccessExceptionMapperInterceptor());
	}

	/**
	 * @author Ariel Carrera <carreraariel@gmail.com>
	 */
	static class DataAccessExceptionMapperInterceptor implements MethodInterceptor, Serializable {

		private static final long serialVersionUID = -3519640247926903558L;

		public DataAccessExceptionMapperInterceptor() {}

		@Override
		@Nullable
		public Object invoke(MethodInvocation invocation) throws Throwable {
			try {
				return invocation.proceed();
			} catch (RollbackException | javax.persistence.RollbackException e) {
				throw new DataAccessException(e.getCause());
			} catch (Exception e) {
				throw new DataAccessException(e);
			}
		}

	}
}
