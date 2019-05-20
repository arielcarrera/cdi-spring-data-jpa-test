
package com.github.arielcarrera.cdi.support.transactional;

import javax.enterprise.inject.spi.BeanManager;
import javax.interceptor.InvocationContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.arjuna.ats.jta.cdi.transactional.TransactionalInterceptorRequired;

/**
 * Based on {@link TransactionalInterceptorRequired}
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
public class MyTransactionalInterceptorRequired extends MyTransactionalInterceptorBase {
	private static final long serialVersionUID = -1589716376766222605L;

	public MyTransactionalInterceptorRequired(BeanManager beanManager, TransactionManager transactionManager) {
        super(false, beanManager, transactionManager);
    }
	
    public Object intercept(InvocationContext ic) throws Exception {
        return super.intercept(ic);
    }

    @Override
    protected Object doIntercept(TransactionManager tm, Transaction tx, InvocationContext ic) throws Exception {
        if (tx == null) {
            return invokeInOurTx(ic, tm);
        } else {
            return invokeInCallerTx(ic, tx);
        }
    }
}
