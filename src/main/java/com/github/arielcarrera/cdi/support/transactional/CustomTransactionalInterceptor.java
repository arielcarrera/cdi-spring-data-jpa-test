package com.github.arielcarrera.cdi.support.transactional;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@CustomTransactionalAnnotation
@Interceptor
class CustomTransactionalInterceptor {

	@Inject
	@Any
	private EntityManager entityManager;

	@AroundInvoke
	public Object runInTransaction(InvocationContext ctx) throws Exception {
		EntityTransaction entityTransaction = this.entityManager.getTransaction();
		boolean isNew = !entityTransaction.isActive();
		try {
			if (isNew) {
				entityTransaction.begin();
			}
			Object result = ctx.proceed();
			if (isNew) {
				entityTransaction.commit();
			}
			return result;
		} catch (RuntimeException r) {
			if (isNew) {
				entityTransaction.rollback();
			}
			throw r;
		}
	}
}