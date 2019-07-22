package com.github.arielcarrera.cdi.test.config;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.data.RepositoryCreation;

/**
 * EntityManagerFactory producer
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ApplicationScoped
public class EntityManagerFactoryProducer {

	@Inject
	private BeanManager beanManager;

	@Produces
	@ApplicationScoped
	public EntityManagerFactory produceEntityManagerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put("javax.persistence.bean.manager", beanManager);
		return Persistence.createEntityManagerFactory("testPersistenceUnit", props);
	}

	public void close(@Disposes EntityManagerFactory emf) {
		emf.close();
	}
	
	@Produces @RepositoryCreation
	@ApplicationScoped
	public EntityManagerFactory produceEntityManagerFactoryCreation() {
		Map<String, Object> props = new HashMap<>();
		props.put("javax.persistence.bean.manager", beanManager);
		return Persistence.createEntityManagerFactory("testPersistenceUnit", props);
	}

	public void closeCreation(@Disposes @RepositoryCreation EntityManagerFactory emf) {
		emf.close();
	}
}
