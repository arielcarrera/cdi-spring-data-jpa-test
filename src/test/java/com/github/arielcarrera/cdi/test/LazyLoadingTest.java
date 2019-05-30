package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertTrue;

import io.agroal.api.AgroalDataSourceMetrics;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.github.arielcarrera.cdi.test.config.AgroalConnectionProvider;
import com.github.arielcarrera.cdi.test.config.JtaEnvironment;
import com.github.arielcarrera.cdi.test.entities.LazyEntity;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadWriteRepository;

/**
 * Tests for lazy Loading 
 * 
 * @author Ariel Carrera
 *
 */
public class LazyLoadingTest {

	@ClassRule
	public static JtaEnvironment jtaEnvironment = new JtaEnvironment();
	
	@Rule
	public WeldInitiator weld = WeldInitiator.from(new Weld()).activate(RequestScoped.class, ApplicationScoped.class)
				.inject(this).build();
	
	
	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("Starting TEST: " + description.getMethodName());
	      lastMethod = currentMethod;
	      currentMethod = description.getMethodName();
	   }
	};
	
	private static String lastMethod = "-";
	private static String currentMethod = "-";
	
	@Inject
	protected EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Inject
	private TestReadWriteRepository repo;
	
	
	@Before
	public void load() {
    	entityManager.getTransaction().begin();
    	entityManager.persist(new TestEntity(1,1,1,1,new LazyEntity(10,10)));
    	entityManager.getTransaction().commit();
	}
	
	
	@Before
	public void statusPoolBegin() {
    	AgroalDataSourceMetrics metrics = AgroalConnectionProvider.getMetrics();
    	if (metrics != null) {
	    	System.out.println("Metricas del pool INICIO " + currentMethod + ":" + metrics);
	    	if (metrics.maxUsedCount() > 1) {
	    		System.out.println("Alerta: max=" + metrics.maxUsedCount() + " -> metodo: " + currentMethod + " -> previo: " +  lastMethod);
	    	}
    	}
	}
	
	@After
	public void statusPoolEnd() {
  	AgroalDataSourceMetrics metrics = AgroalConnectionProvider.getMetrics();
  	if (metrics != null) {
    	System.out.println("Metricas del pool FIN " + currentMethod + ":" + metrics);
	    if (metrics.maxUsedCount() > 1) {
    		System.out.println("Alerta: max=" + metrics.maxUsedCount() + " -> metodo: " + currentMethod + " -> previo: " +  lastMethod);
	  	}
    }
	}
	
	@Test
	public void getReferenceOutsideTransaction() { //Connection is not closed!!!!
		AgroalDataSourceMetrics metrics = AgroalConnectionProvider.getMetrics();
		long active = metrics.activeCount();
		
		TestEntity t = repo.getOne(1);
		Integer i = t.getValue();
		
		assertTrue(i.equals(1));
		assertTrue(metrics.activeCount() - active > 0);
		System.out.println("Warn: connection is not closed");
	}
	
	
	@Test
	public void getReferenceInsideTransaction() {//The connection is closed at the end of the transaction
		AgroalDataSourceMetrics metrics = AgroalConnectionProvider.getMetrics();
		long active = metrics.activeCount();
		entityManager.getTransaction().begin();
		TestEntity t = repo.getOne(1);
		Integer i = t.getValue();
		entityManager.getTransaction().commit();
		
		assertTrue(i.equals(1));
		assertTrue(metrics.activeCount() - active == 0);
	}
	
	@Test
	public void getReferenceCloseEntityManager() { //Connection is not closed!!!!
		AgroalDataSourceMetrics metrics = AgroalConnectionProvider.getMetrics();
		long active = metrics.activeCount();
		
		TestEntity t = repo.getOne(1);
		Integer i = t.getValue();
		
		entityManager.close();
		
		assertTrue(i.equals(1));
		assertTrue(metrics.activeCount() - active > 0);
		System.out.println("Warn: connection is not closed");
	}
	
	
	@Test
	public void lazyAttributeOutsideTransaction() { //Connection is not closed!!!!
		AgroalDataSourceMetrics metrics = AgroalConnectionProvider.getMetrics();
		long active = metrics.activeCount();
		
		Optional<TestEntity> t = repo.findById(1);
		assertTrue(t.isPresent());
		
		LazyEntity l = t.get().getLazy();
		
		assertTrue(l.getValue() == 10);
		
		assertTrue(metrics.activeCount() - active > 0);
		System.out.println("Warn: connection is not closed");
	}
	
	
	@Test
	public void lazyAttributeInsideTransaction() {//The connection is closed at the end of the transaction
		AgroalDataSourceMetrics metrics = AgroalConnectionProvider.getMetrics();
		long active = metrics.activeCount();
		entityManager.getTransaction().begin();
		Optional<TestEntity> t = repo.findById(1);
		assertTrue(t.isPresent());
		
		LazyEntity l = t.get().getLazy();
		entityManager.getTransaction().commit();
		
		assertTrue(l.getId() == 10);
		assertTrue(metrics.activeCount() - active == 0);
	}
	
	@Test
	public void lazyAttributeCloseEntityManager() { //Connection is not closed!!!!
		AgroalDataSourceMetrics metrics = AgroalConnectionProvider.getMetrics();
		long active = metrics.activeCount();
		
		Optional<TestEntity> t = repo.findById(1);
		assertTrue(t.isPresent());
		
		LazyEntity l = t.get().getLazy();
		
		assertTrue(l.getValue() == 10);
		
		entityManager.close();
		
		assertTrue(metrics.activeCount() - active > 0);
		System.out.println("Warn: connection is not closed");
	}
	
}