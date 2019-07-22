package com.github.arielcarrera.cdi.test.config;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.weld.transaction.spi.TransactionServices;

import com.arjuna.ats.jta.common.jtaPropertyManager;

/**
 * Based on hibernate-demos sample project
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
public class TestingTransactionServices implements TransactionServices {

	@Override
	public void cleanup() {
	}

	@Override
	public void registerSynchronization(Synchronization synchronizedObserver) {
		jtaPropertyManager.getJTAEnvironmentBean().getTransactionSynchronizationRegistry()
				.registerInterposedSynchronization(synchronizedObserver);
	}

	@Override
	public boolean isTransactionActive() {
		try {
			return com.arjuna.ats.jta.UserTransaction.userTransaction().getStatus() == Status.STATUS_ACTIVE;
		} catch (SystemException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public UserTransaction getUserTransaction() {
	   UserTransaction ut = com.arjuna.ats.jta.UserTransaction.userTransaction();
	   try {
	    ut.setTransactionTimeout(600000);//FOR DEBUG PURPOSE
	} catch (SystemException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
		return ut;
	}
}
