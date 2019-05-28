package com.github.arielcarrera.cdi.support.transactional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.transaction.TransactionManager;

/**
 * Transaction Interceptor Producer
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ApplicationScoped
public class TransactionalInterceptorProducer {
	
	@Produces
	public CustomTransactionalInterceptorRequired txRequired(BeanManager beanManager, TransactionManager transactionManager) {
		return new CustomTransactionalInterceptorRequired(beanManager, transactionManager);
	}
	
	@Produces
	public CustomTransactionalInterceptorRequiresNew txRequiresNew(BeanManager beanManager, TransactionManager transactionManager) {
		return new CustomTransactionalInterceptorRequiresNew(beanManager, transactionManager);
	}
	
	@Produces
	public CustomTransactionalInterceptorSupports txSupports(BeanManager beanManager, TransactionManager transactionManager) {
		return new CustomTransactionalInterceptorSupports(beanManager, transactionManager);
	}
	
	@Produces
	public CustomTransactionalInterceptorMandatory txMandatory(BeanManager beanManager, TransactionManager transactionManager) {
		return new CustomTransactionalInterceptorMandatory(beanManager, transactionManager);
	}
	
	@Produces
	public CustomTransactionalInterceptorNever txNever(BeanManager beanManager, TransactionManager transactionManager) {
		return new CustomTransactionalInterceptorNever(beanManager, transactionManager);
	}
	
	@Produces
	public CustomTransactionalInterceptorNotSupported txNotSupported(BeanManager beanManager, TransactionManager transactionManager) {
		return new CustomTransactionalInterceptorNotSupported(beanManager, transactionManager);
	}
}
