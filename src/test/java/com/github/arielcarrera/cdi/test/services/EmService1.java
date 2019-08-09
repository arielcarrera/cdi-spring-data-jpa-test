package com.github.arielcarrera.cdi.test.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.jboss.weld.proxy.WeldClientProxy;

@ApplicationScoped
public class EmService1 {

	@Inject 
	private EntityManager em;
	
	public int getEmHashCodeNoTx() {
	    return getEmHashCode();
	}

	@Transactional
	public int getEmHashCodeTxReq() {
	    return getEmHashCode();
	}
	
	@Transactional(TxType.REQUIRES_NEW)
	public int getEmHashCodeTxNew() {
	    return getEmHashCode();
	}
	
	private int getEmHashCode() {
	    if (em instanceof WeldClientProxy) {
		return ((WeldClientProxy) em).getMetadata().getContextualInstance().hashCode();
	    }
	    return em.hashCode();
	}
}
