/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.arielcarrera.cdi.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.jnp.server.NamingBeanImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.arjuna.ats.arjuna.common.recoveryPropertyManager;
import com.arjuna.ats.arjuna.recovery.RecoveryManager;
import com.arjuna.ats.internal.arjuna.recovery.AtomicActionRecoveryModule;
import com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule;
import com.arjuna.ats.jta.utils.JNDIManager;
import com.github.arielcarrera.cdi.test.config.JtaEnvironment;
import com.github.arielcarrera.cdi.test.config.TransactionalConnectionProvider;
import com.github.arielcarrera.cdi.test.entities.TestEntity;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class TestCase {

    @Rule
    public WeldInitiator weld = WeldInitiator.from(new Weld()).activate(RequestScoped.class).inject(this).build();

    /**
     * JNDI server.
     */
    private static final NamingBeanImpl NAMING_BEAN = new NamingBeanImpl();

    /**
     * Transaction manager for transaction demarcation.
     */
    private static TransactionManager transactionManager;

    /**
     * Repository to create test entities.
     */
    @Inject
    private QuickstartEntityRepository quickstartEntityRepository;
    

    @BeforeClass
    public static void beforeClass() throws Exception {
	NAMING_BEAN.start();
	JNDIManager.bindJTAImplementation();
	new InitialContext().bind(TransactionalConnectionProvider.DATASOURCE_JNDI,
		TransactionalConnectionProvider.getDataSource());
	recoveryPropertyManager.getRecoveryEnvironmentBean().setRecoveryModuleClassNames(getRecoveryModuleClassNames());
	recoveryPropertyManager.getRecoveryEnvironmentBean().setRecoveryBackoffPeriod(1);
	JtaEnvironment.setObjectStoreDir();
	RecoveryManager.manager(RecoveryManager.DIRECT_MANAGEMENT).getModules().stream()
		.filter(m -> m instanceof XARecoveryModule)
		.forEach(m -> ((XARecoveryModule) m).addXAResourceRecoveryHelper(new DummyXAResourceRecoveryHelper()));
    }

    static List<String> getRecoveryModuleClassNames() {
	List<String> recoveryModuleClassNames = new ArrayList<>();
	recoveryModuleClassNames.add(AtomicActionRecoveryModule.class.getName());
	recoveryModuleClassNames.add(XARecoveryModule.class.getName());

	return recoveryModuleClassNames;
    }

    @AfterClass
    public static void afterClass() {
	// Stop JNDI server
	NAMING_BEAN.stop();
    }

    @Before
    public void before() throws Exception {
	transactionManager = InitialContext.doLookup("java:/TransactionManager");
    }

    @After
    public void after() {
	try {
	    System.out.println("Ejecutando TEST: AFTER - forzando Rollback en el TM para TESTING");
	    transactionManager.rollback();
	    quickstartEntityRepository.clear();
	} catch (Throwable t) {
	}

//	weld.shutdown();
    }

    private void assertEntities(TestEntity... expected) throws Exception {
	assertEquals(Arrays.asList(expected), getEntitiesFromTheDatabase());
    }

    private List<TestEntity> getEntitiesFromTheDatabase() throws Exception {
	DataSource dataSource = InitialContext.doLookup("java:/testDS");
	Connection connection = dataSource.getConnection(TransactionalConnectionProvider.USERNAME,
		TransactionalConnectionProvider.PASSWORD);
	Statement statement = connection.createStatement();
	ResultSet resultSet = statement.executeQuery("SELECT `id`,`value`,`uniqueValue` FROM `TestEntity`");
	List<TestEntity> entities = new LinkedList<>();
	while (resultSet.next()) {
	    entities.add(
		    new TestEntity(resultSet.getInt("id"), resultSet.getInt("value"), resultSet.getInt("uniqueValue")));
	}
	resultSet.close();
	statement.close();
	connection.close();
	return entities;
    }

    private int count = 0;

    private TestEntity getNewEntity() {
	return new TestEntity(null, ++count, count);
    }

    @Test(expected=RuntimeException.class)
    public void testSuspendTransactionRollbackFirstNew() throws Exception {
	TestEntity firstEntity = getNewEntity();
	TestEntity secondEntity = getNewEntity();
	try {
	    quickstartEntityRepository.testSuspendAndRollback(firstEntity, secondEntity);
	} catch (Exception e) {
	    assertEntities(firstEntity);
	    throw e;
	}
    }
    
    @Test(expected=RuntimeException.class)
    public void testSuspendTransactionRollbackFirstSave() throws Exception {
	TestEntity firstEntity = getNewEntity();
	TestEntity secondEntity = getNewEntity();
	try {
	    quickstartEntityRepository.testSuspendAndRollback_FirstSave(firstEntity, secondEntity);
	} catch (Exception e) {
	    assertEntities(secondEntity);
	    throw e;
	}
    }

    /**
     * Adds two entries to the database and commits the transaction. At the end of the test two entries should be in the
     * database.
     *
     * @throws Exception
     */
    @Test
    public void testCommit() throws Exception {
        TestEntity firstEntity = getNewEntity();
        TestEntity secondEntity = getNewEntity();
        transactionManager.begin();
        quickstartEntityRepository.save(firstEntity);
        quickstartEntityRepository.save(secondEntity);
        transactionManager.commit();
        assertEntities(firstEntity, secondEntity);
    }

    /**
     * Adds two entries to the database and rolls back the transaction. At the end of the test no entries should be in the
     * database.
     * 
     * @throws Exception
     */
    @Test
    public void testRollback() throws Exception {
	TestEntity firstEntity = getNewEntity();
	TestEntity secondEntity = getNewEntity();
        transactionManager.begin();
        quickstartEntityRepository.save(firstEntity);
        quickstartEntityRepository.save(secondEntity);
        transactionManager.rollback();
        assertEntities();
    }

}
