package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.demos.jpacditesting.support.JtaEnvironment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadOnlyRepository;
import com.github.arielcarrera.cdi.test.services.CustomTransactionalTestService;

/**
 * Tests for Writable Repository with soft delete operations
 * 
 * @author Ariel Carrera
 *
 */
public class CustomTransactionAnnotationTest {

	@ClassRule
	public static JtaEnvironment jtaEnvironment = new JtaEnvironment();
	@Rule
	public WeldInitiator weld = WeldInitiator.from(new Weld()).activate(RequestScoped.class, ApplicationScoped.class)
				.inject(this).build();
	
	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("Starting TEST: " + description.getMethodName());
	   }
	};
	
	@Inject
	protected EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Inject
	protected CustomTransactionalTestService service;
	
	@Inject
	private TestReadOnlyRepository repo;
	
	@Before
	public void load() {
		getEntityManager().getTransaction().begin();
		getEntityManager().persist(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		getEntityManager().getTransaction().commit();
	}
	
	@Test
	public void customTransactionalService_OK() {
		service.doSomething();
		getEntityManager().clear();
		assertTrue(repo.findById(1).get().isLogicalDeleted());
	}
	
	@Test(expected=RuntimeException.class)
	public void customTransactionalService_Rollback() {
		service.doRollback();
		getEntityManager().clear();
		assertFalse(repo.findById(1).get().isLogicalDeleted());
	}

}