package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.InvalidTransactionException;
import javax.transaction.SystemException;
import javax.transaction.TransactionRequiredException;
import javax.transaction.TransactionalException;
import javax.transaction.UserTransaction;

import org.hamcrest.core.IsInstanceOf;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.test.config.JtaEnvironment;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.services.DefaultTransactionalTestService;

/**
 * Tests for Transactional Operations
 * 
 * @author Ariel Carrera
 *
 */
@Slf4j
public class TransactionalTest {

	@ClassRule
	public static JtaEnvironment jtaEnvironment = new JtaEnvironment();

	@Rule
	public WeldInitiator weld = WeldInitiator.from(new Weld()).inject(this).build();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public TestRule watcher = new TestWatcher() {
		protected void starting(Description description) {
			log.info("Starting TEST: " + description.getMethodName());
		}
	};

	@Inject
	protected EntityManager entityManager;

	@Inject
	protected UserTransaction tx;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Inject
	protected DefaultTransactionalTestService service;

	@Test
	public void transactionalService_OK() {
		service.doSomething(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		assertTrue(TestJdbcUtil.jdbcExistById(1));
	}

	@Test(expected = RuntimeException.class)
	public void transactionalService_Rollback() {
		service.doRollback(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));

		assertFalse(TestJdbcUtil.jdbcExistById(1));
	}

	@Test
	public void mandatoryTransactionalService_Required() {
		thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(TransactionRequiredException.class));
		service.doSomethingMandatory(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
	}

	@Test
	public void neverTransactionalService_NewTxInside() {
		service.doSomethingNeverNewTxInside(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		assertTrue(TestJdbcUtil.jdbcExistById(1));
	}

	@Test
	public void neverTransactionalService_InNoTx() throws SystemException {
		assertTrue(service.doSomethingNever());
	}

	@Test(expected = TransactionalException.class)
	public void neverTransactionalService_InTx() throws Exception {
		tx.begin();
		try {
			service.doSomethingNever();
			fail("Exception must to be raised");
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof InvalidTransactionException);
			tx.rollback();
			throw e;
		}

	}

	@Test
	public void notSupportedTransactionalService_InNoTx() throws SystemException {
		assertTrue(service.doSomethingNotSupported());
	}

	@Test
	public void notSupportedTransactionalService_InTx() throws Exception {
		tx.begin();
		assertTrue(service.doSomethingNotSupported());
		tx.commit();
	}

	@Test
	public void notSupportedTransactionalService_InnerNewTx() {
		service.doSomethingNotSupportedNewTxInside(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		assertTrue(TestJdbcUtil.jdbcExistById(1));
	}

	@Test
	public void requiredTransactionalService_OutsideTransaction() {
		service.doSomethingRequired(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		//
		assertTrue(TestJdbcUtil.jdbcExistById(1));
	}

	@Test(expected = RuntimeException.class)
	public void requiredTransactionalService_OutsideTransactionRollback() {
		service.doRollbackRequired(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));

		assertFalse(TestJdbcUtil.jdbcExistById(1));
	}

	@Test
	public void requiredTransactionalService_InsideTransaction() throws Exception {

		try {
			tx.begin();
			service.doSomethingRequired(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		}

		assertTrue(TestJdbcUtil.jdbcExistById(1));
	}

	@Test(expected = RuntimeException.class)
	public void requiredTransactionalService_InsideTransactionRollbackOut() throws Exception {
		try {
			tx.begin();
			getEntityManager().persist(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
			service.doSomethingRequired(new TestEntity(2, 2, 2, LogicalDeletion.NORMAL_STATUS));
			throw new RuntimeException();
		} catch (Exception e) {
			tx.rollback();

			assertFalse(TestJdbcUtil.jdbcExistById(1));
			assertFalse(TestJdbcUtil.jdbcExistById(2));
			throw e;
		}
	}

	@Test(expected = RuntimeException.class)
	public void requiredTransactionalService_InsideTransactionRollbackIn() throws Exception {
		try {
			tx.begin();
			getEntityManager().persist(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
			service.doRollbackRequired(new TestEntity(2, 2, 2, LogicalDeletion.NORMAL_STATUS));
			fail("Exception must to be raised");
		} catch (Exception e) {
			tx.rollback();

			assertFalse(TestJdbcUtil.jdbcExistById(1));
			assertFalse(TestJdbcUtil.jdbcExistById(2));
			throw e;
		}
	}

	@Test
	public void requiresNewTransactionalService_OutsideTransaction() {
		service.doSomethingRequiresNew(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));

		assertTrue(TestJdbcUtil.jdbcExistById(1));
	}

	@Test(expected = RuntimeException.class)
	public void requiresNewTransactionalService_OutsideTransactionRollback() {
		service.doRollbackRequired(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));

		assertFalse(TestJdbcUtil.jdbcExistById(1));
	}

	@Test(expected = RuntimeException.class)
	public void requiresNewTransactionalService_InsideTransactionRollbackOut() throws Exception {
		try {
			tx.begin();
			getEntityManager().persist(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
			service.doSomethingRequiresNew(new TestEntity(2, 2, 2, LogicalDeletion.NORMAL_STATUS));
			throw new RuntimeException();
		} catch (Exception e) {
			tx.rollback();
			assertFalse(TestJdbcUtil.jdbcExistById(1));
			assertTrue(TestJdbcUtil.jdbcExistById(2));
			throw e;
		}
	}

	@Test(expected = RuntimeException.class)
	public void requiresNewTransactionalService_InsideTransactionRollbackIn() throws Exception {
		try {
			tx.begin();
			getEntityManager().persist(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
			service.doRollbackRequiresNew(new TestEntity(2, 2, 2, LogicalDeletion.NORMAL_STATUS));
			fail("Exception must to be raised");
		} catch (Exception e) {
			tx.commit();
			assertTrue(TestJdbcUtil.jdbcExistById(1));
			assertFalse(TestJdbcUtil.jdbcExistById(2));
			throw e;
		}
	}

	@Test(expected = RuntimeException.class)
	public void requiresNewTransactionalService_InsideTransactionRollbackOut_Inverse() throws Exception {
		try {
			tx.begin();
			service.doSomethingRequiresNew(new TestEntity(2, 2, 2, LogicalDeletion.NORMAL_STATUS));
			getEntityManager().persist(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
			throw new RuntimeException();
		} catch (Exception e) {
			tx.rollback();
			assertFalse(TestJdbcUtil.jdbcExistById(1));
			assertTrue(TestJdbcUtil.jdbcExistById(2));
			throw e;
		}
	}

	@Test(expected = RuntimeException.class)
	public void requiresNewTransactionalService_InsideTransactionRollbackIn_Inverse() throws Exception {
		try {
			tx.begin();
			service.doRollbackRequiresNew(new TestEntity(2, 2, 2, LogicalDeletion.NORMAL_STATUS));
			fail("Exception must to be raised");
		} catch (Exception e) {
			getEntityManager().persist(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
			tx.commit();
			assertTrue(TestJdbcUtil.jdbcExistById(1));
			assertFalse(TestJdbcUtil.jdbcExistById(2));
			throw e;
		}
	}

	@Test
	public void supportsTransactionalService_InsideTransaction() throws Exception {
		try {
			tx.begin();
			service.doSomethingSupports(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		}

		assertTrue(TestJdbcUtil.jdbcExistById(1));
	}

	@Test
	public void supportsTransactionalService_NoTx() throws SystemException {
		assertFalse(service.doSupportsHasTransaction());
	}

	@Test
	public void supportsTransactionalService_inTx() throws Exception {
		tx.begin();
		assertTrue(service.doSupportsHasTransaction());
		tx.commit();
	}

	@Test
	public void supportsTransactionalService_inTx_Save() throws Exception {
		tx.begin();
		service.doSomethingSupports(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		tx.commit();
		assertTrue(TestJdbcUtil.jdbcExistById(1));
	}

	@Test
	public void supportsTransactionalService_inTx_SaveRollback() throws Exception {
		tx.begin();
		service.doSomethingSupports(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		tx.rollback();
		assertFalse(TestJdbcUtil.jdbcExistById(1));
	}

	@Test
	public void supportsTransactionalService_NewTxInside() {
		service.doSomethingSupports(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));

		assertTrue(TestJdbcUtil.jdbcExistById(1));
	}

	@Test
	public void noWrappedTransactionalService_OutsideTransaction() {
		service.doSomethingNoWrapped(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));

		assertTrue(TestJdbcUtil.jdbcExistById(1));
	}

	@Test
	public void noWrappedTransactionalService_InsideTransaction() throws Exception {
		tx.begin();
		service.doSomethingNoWrapped(new TestEntity(1, 1, 1, LogicalDeletion.NORMAL_STATUS));
		tx.commit();

		assertTrue(TestJdbcUtil.jdbcExistById(1));
	}
}