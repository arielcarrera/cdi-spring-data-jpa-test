/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.github.arielcarrera.cdi.support.transactional;

import javax.enterprise.inject.spi.BeanManager;
import javax.interceptor.InvocationContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.arjuna.ats.jta.cdi.transactional.TransactionalInterceptorRequiresNew;

/**
 * Based on {@link TransactionalInterceptorRequiresNew}
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
public class CustomTransactionalInterceptorRequiresNew extends CustomTransactionalInterceptorBase {

	private static final long serialVersionUID = -4021274469167378937L;

	public CustomTransactionalInterceptorRequiresNew(BeanManager beanManager, TransactionManager transactionManager) {
    	super(false, beanManager, transactionManager);
    }

    public Object intercept(InvocationContext ic) throws Exception {
        return super.intercept(ic);
    }

    @Override
    protected Object doIntercept(TransactionManager tm, Transaction tx, InvocationContext ic) throws Exception {
        if (tx != null) {
            tm.suspend();
            try {
                return invokeInOurTx(ic, tm);
            } finally {
                tm.resume(tx);
            }
        } else {
            return invokeInOurTx(ic, tm);
        }
    }
}
