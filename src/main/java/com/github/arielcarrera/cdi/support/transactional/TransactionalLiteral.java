package com.github.arielcarrera.cdi.support.transactional;

import javax.enterprise.util.AnnotationLiteral;
import javax.transaction.Transactional;

@SuppressWarnings("all")
public class TransactionalLiteral extends AnnotationLiteral<Transactional> implements Transactional {

    private TransactionalLiteral() {
    }

    public static final TransactionalLiteral INSTANCE = new TransactionalLiteral();

	@Override
	public TxType value() {
		return TxType.REQUIRED;
	}

	@Override
	public Class[] rollbackOn() {
		// TODO Auto-generated method stub
		return new Class[] {RuntimeException.class};
	}

	@Override
	public Class[] dontRollbackOn() {
		// TODO Auto-generated method stub
		return new Class[] {};
	}
}