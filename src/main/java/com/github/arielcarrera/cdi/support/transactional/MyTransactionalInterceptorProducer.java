package com.github.arielcarrera.cdi.support.transactional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.transaction.TransactionManager;

/**
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ApplicationScoped
public class MyTransactionalInterceptorProducer {
	
	@Produces
	public MyTransactionalInterceptorRequired txRequired(BeanManager beanManager, TransactionManager transactionManager) {
		return new MyTransactionalInterceptorRequired(beanManager, transactionManager);
	}
	
	@Produces
	public MyTransactionalInterceptorRequiresNew txRequiresNew(BeanManager beanManager, TransactionManager transactionManager) {
		return new MyTransactionalInterceptorRequiresNew(beanManager, transactionManager);
	}
	
	@Produces
	public MyTransactionalInterceptorSupports txSupports(BeanManager beanManager, TransactionManager transactionManager) {
		return new MyTransactionalInterceptorSupports(beanManager, transactionManager);
	}
	
	@Produces
	public MyTransactionalInterceptorMandatory txMandatory(BeanManager beanManager, TransactionManager transactionManager) {
		return new MyTransactionalInterceptorMandatory(beanManager, transactionManager);
	}
	
	@Produces
	public MyTransactionalInterceptorNever txNever(BeanManager beanManager, TransactionManager transactionManager) {
		return new MyTransactionalInterceptorNever(beanManager, transactionManager);
	}
	
	@Produces
	public MyTransactionalInterceptorNotSupported txNotSupported(BeanManager beanManager, TransactionManager transactionManager) {
		return new MyTransactionalInterceptorNotSupported(beanManager, transactionManager);
	}
}
