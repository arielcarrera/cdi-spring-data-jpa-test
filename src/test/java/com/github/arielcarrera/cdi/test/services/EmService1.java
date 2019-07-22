package com.github.arielcarrera.cdi.test.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class EmService1 {

	@Inject 
	private EntityManager em;
	
	public EntityManager getEm() {
		return em;
	}
}
