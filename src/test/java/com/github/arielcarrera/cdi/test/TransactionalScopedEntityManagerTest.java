package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.test.config.JtaEnvironment;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadWriteDeleteRepository;
import com.github.arielcarrera.cdi.test.services.DefaultTransactionalTestService;
import com.github.arielcarrera.cdi.test.services.EmService1;
import com.github.arielcarrera.cdi.test.services.EmService2;

@Slf4j
public class TransactionalScopedEntityManagerTest {

	@ClassRule
	public static JtaEnvironment jtaEnvironment = new JtaEnvironment();

	@Rule
	public WeldInitiator weld = WeldInitiator.from(new Weld()).inject(this).build();

	@Rule
	public TestRule watcher = new TestWatcher() {
		protected void starting(Description description) {
			log.info("Starting TEST: " + description.getMethodName());
		}
	};

	@Inject
	protected DefaultTransactionalTestService txService;

	@Inject
	protected TestReadWriteDeleteRepository loaderRepository;

	@Inject
	EmService1 es1;

	@Inject
	EmService2 es2;

	@Inject
	UserTransaction tx;

	@Test
	public void test() {
		loaderRepository.save(new TestEntity(1, 1, null, LogicalDeletion.NORMAL_STATUS));
		Assert.assertTrue(txService.testEntityManagerContains(1));
	}

	@Test(expected = ContextNotActiveException.class)
	public void testHashCodeInNoTx() {
		es1.getEmHashCodeNoTx();
		fail("Exception must to be raised");
	}

	@Test
	public void testHashCodeInRequiredTx() {
		assertNotEquals(es1.getEmHashCodeTxReq(), es2.getEmHashCodeTxReq());
	}

	@Test
	public void testHashCodeInRequiredNewTx() {
		assertNotEquals(es1.getEmHashCodeTxNew(), es2.getEmHashCodeTxNew());
	}

	@Test
	public void testHashCodeInRequiredNewTx_InGlobal() throws Exception {
		tx.begin();
		try {
			assertNotEquals(es1.getEmHashCodeTxNew(), es2.getEmHashCodeTxNew());
		} finally {
			tx.commit();
		}
	}

	@Test
	public void testHashCodeInGlobalTx() throws Exception {
		tx.begin();
		try {
			assertEquals(es1.getEmHashCodeNoTx(), es2.getEmHashCodeNoTx());
		} finally {
			tx.commit();
		}
	}

	@Test
	public void testHashCodeInJoinedGlobalTx() throws Exception {
		tx.begin();
		try {
			assertEquals(es1.getEmHashCodeTxReq(), es2.getEmHashCodeTxReq());
		} finally {
			tx.commit();
		}
	}

	@Test
	public void testHashCodeInTx_OneServiceJoined_OneServiceInNewTx() throws Exception {
		tx.begin();
		try {
			assertNotEquals(es1.getEmHashCodeTxReq(), es2.getEmHashCodeTxNew());
		} finally {
			tx.commit();
		}
	}

	@Test
	public void testHashCodeInTx_BothServicesInNewTx_ExistGlobal() throws Exception {
		tx.begin();
		try {
			assertNotEquals(es1.getEmHashCodeTxNew(), es2.getEmHashCodeTxNew());
		} finally {
			tx.commit();
		}
	}

}