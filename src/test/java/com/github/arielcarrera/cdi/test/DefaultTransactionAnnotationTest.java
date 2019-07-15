package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.InvalidTransactionException;
import javax.transaction.TransactionRequiredException;

import org.hamcrest.core.IsInstanceOf;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.test.config.AgroalConnectionProvider;
import com.github.arielcarrera.cdi.test.config.JtaEnvironment;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadOnlyRepository;
import com.github.arielcarrera.cdi.test.services.DefaultTransactionalTestService;
import com.github.arielcarrera.cdi.test.services.TransactionalCrossRepositoryService;

import io.agroal.api.AgroalDataSourceMetrics;

import java.util.Optional;

/**
 * Tests for Writable Repository with soft delete operations
 * 
 * @author Ariel Carrera
 *
 */
public class DefaultTransactionAnnotationTest {

	@ClassRule
	public static JtaEnvironment jtaEnvironment = new JtaEnvironment();
	
	@Rule
	public WeldInitiator weld = WeldInitiator.from(new Weld()).activate(RequestScoped.class, ApplicationScoped.class)
				.inject(this).build();
	
	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	
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
	protected DefaultTransactionalTestService service;
	
	@Inject
	protected TransactionalCrossRepositoryService crossService;
	
	@Inject
	private TestReadOnlyRepository repo;
	
	
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
	public void transactionalService_OK() {
		service.doSomething(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		getEntityManager().clear();
		assertTrue(repo.existsById(1));
	}
	
	@Test(expected=RuntimeException.class)
	public void transactionalService_Rollback() {
		service.doRollback(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		getEntityManager().clear();
		assertFalse(repo.existsById(1));
	}
	
	@Test
	public void mandatoryTransactionalService_Required() {
	    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(TransactionRequiredException.class));
		service.doSomethingMandatory(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
	}
	
	@Test
	public void neverTransactionalService_OK() {
		service.doSomethingNever(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
	}
	
	@Test
	public void neverTransactionalService_Error() {
		thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(InvalidTransactionException.class));
		getEntityManager().getTransaction().begin();
		try {
			service.doSomethingNever(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
			getEntityManager().getTransaction().commit();
		} catch(Exception e) {
			getEntityManager().getTransaction().rollback();
			throw e;
		}
	}
	//TODO REVIEW TxType.NOT_SUPPORTED
//	@Test 
//	public void notSupportedTransactionalService_OutsideTransaction() {
//		try {
//		service.doSomethingNotSupported(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
//		} catch(Exception e) {
//			throw e;
//		}
//	}
//	
//	@Test
//	public void notSupportedTransactionalService_InsideTransaction() {
//		try {
//		getEntityManager().getTransaction().begin();
//		service.doSomethingNotSupported(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
//		getEntityManager().getTransaction().commit();
//	} catch(Exception e) {
//		throw e;
//	}
//	}

	@Test
	public void requiredTransactionalService_OutsideTransaction() {
		service.doSomethingRequired(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		getEntityManager().clear();
		assertTrue(repo.existsById(1));
	}
	
	@Test(expected=RuntimeException.class)
	public void requiredTransactionalService_OutsideTransactionRollback() {
		service.doRollbackRequired(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		getEntityManager().clear();
		assertFalse(repo.existsById(1));
	}
	
	@Test
	public void requiredTransactionalService_InsideTransaction() {
		getEntityManager().getTransaction().begin();
		try {
			service.doSomethingRequired(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
			getEntityManager().getTransaction().commit();
		} catch(Exception e) {
			getEntityManager().getTransaction().rollback();
			throw e;
		}
		getEntityManager().clear();
		assertTrue(repo.existsById(1));
	}
	
	@Test(expected=RuntimeException.class)
	public void requiredTransactionalService_InsideTransactionRollbackOut() {
		getEntityManager().getTransaction().begin();
		getEntityManager().persist(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		try {
			service.doSomethingRequired(new TestEntity(2, 2, 2, LogicalDeletion.NORMAL_STATUS));
			throw new RuntimeException();
		} catch(Exception e) {
			getEntityManager().getTransaction().rollback();
			getEntityManager().clear();
			assertFalse(repo.existsById(1));
			assertFalse(repo.existsById(2));
			throw e;
		}
	}
	
	@Test(expected=RuntimeException.class)
	public void requiredTransactionalService_InsideTransactionRollbackIn() {
		getEntityManager().getTransaction().begin();
		getEntityManager().persist(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		try {
			service.doRollbackRequired(new TestEntity(2, 2, 2, LogicalDeletion.NORMAL_STATUS));
			fail("Exception must to be raised");
		} catch(Exception e) {
			getEntityManager().getTransaction().rollback();
			getEntityManager().clear();
			assertFalse(repo.existsById(1));
			assertFalse(repo.existsById(2));
			throw e;
		}
	}
	
	@Test
	public void requiresNewTransactionalService_OutsideTransaction() {
		service.doSomethingRequiresNew(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		getEntityManager().clear();
		assertTrue(repo.existsById(1));
	}
	
	@Test(expected=RuntimeException.class)
	public void requiresNewTransactionalService_OutsideTransactionRollback() {
		service.doRollbackRequired(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		getEntityManager().clear();
		assertFalse(repo.existsById(1));
	}
	
	@Test(expected=RuntimeException.class)
	public void requiresNewTransactionalService_InsideTransactionRollbackOut() {
		getEntityManager().getTransaction().begin();
		getEntityManager().persist(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		try {
			service.doSomethingRequiresNew(new TestEntity(2, 2, 2, LogicalDeletion.NORMAL_STATUS));
			throw new RuntimeException();
		} catch(Exception e) {
			getEntityManager().getTransaction().rollback();
			getEntityManager().clear();
			assertFalse(repo.existsById(1));
			assertTrue(repo.existsById(2));
			throw e;
		}
	}
	
	@Test(expected=RuntimeException.class)
	public void requiresNewTransactionalService_InsideTransactionRollbackIn() {
		getEntityManager().getTransaction().begin();
		getEntityManager().persist(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		try {
			service.doRollbackRequired(new TestEntity(2, 2, 2, LogicalDeletion.NORMAL_STATUS));
			fail("Exception must to be raised");
		} catch(Exception e) {
			getEntityManager().getTransaction().commit();
			getEntityManager().clear();
			assertTrue(repo.existsById(1));
			assertFalse(repo.existsById(2));
			throw e;
		}
	}
	
	@Test
	public void supportsTransactionalService_InsideTransaction() {
		getEntityManager().getTransaction().begin();
		try {
			service.doSomethingSupports(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
			getEntityManager().getTransaction().commit();
		} catch(Exception e) {
			getEntityManager().getTransaction().rollback();
			throw e;
		}
		getEntityManager().clear();
		assertTrue(repo.existsById(1));
	}
	
	//TODO review it must to create a new transaction inside... but @Transactional(TxType.REQUIRED) is into SpringData interface and it seems to have no proxy 
	@Test
	public void supportsTransactionalService_OutsideTransaction() {
		service.doSomethingSupports(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		//getEntityManager().clear();
		assertTrue(repo.existsById(1));
	}
	
	//TODO review it must to create a new transaction inside... but @Transactional(TxType.REQUIRED) is into SpringData interface and it seems to have no proxy
	@Test
	public void noWrappedTransactionalService_OutsideTransaction() {
		service.doSomethingNoWrapped(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		getEntityManager().clear();
		assertTrue(repo.existsById(1));
	}
	
	@Test
	public void noWrappedTransactionalService_InsideTransaction() {
		getEntityManager().getTransaction().begin();
		service.doSomethingNoWrapped(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		getEntityManager().getTransaction().commit();
		getEntityManager().clear();
		assertTrue(repo.existsById(1));
	}
	
	
	@Test
	public void multiRepositoryService_SharedTransaction_SameRepo() {
	    crossService.sameRepo(new TestEntity(10, 10, 10, LogicalDeletion.NORMAL_STATUS));
	    Optional<TestEntity> e = repo.findById(10);
	    assertTrue(e.isPresent());
	    assertTrue(e.get().getStatus() == LogicalDeletion.DELETED_STATUS);
	}
	
	@Test
	public void multiRepositoryService_SharedContext() {
	    crossService.crossRepo(new TestEntity(10, 10, 10, LogicalDeletion.NORMAL_STATUS));
	    assertFalse(repo.existsById(10));
	}
	
	@Test
	public void multiRepositoryService_MultiSaves() {
	    crossService.crossRepoMultipleSaves(new TestEntity(10, 10, 10, LogicalDeletion.NORMAL_STATUS),
		    new TestEntity(11, 11, 11, LogicalDeletion.NORMAL_STATUS));
		assertTrue(repo.existsById(10));
		assertTrue(repo.existsById(11));
	}
	
	@Test
	public void multiRepositoryService_MultiSavesRollback() {
	    try {
		crossService.crossRepoMultipleSavesRollback(new TestEntity(10, 10, 10, LogicalDeletion.NORMAL_STATUS),
			    new TestEntity(11, 11, 11, LogicalDeletion.NORMAL_STATUS));
	    } catch (Exception ex) {
		assertFalse(repo.existsById(10));
		assertFalse(repo.existsById(11));
	    }
	    
	}
	
	
}