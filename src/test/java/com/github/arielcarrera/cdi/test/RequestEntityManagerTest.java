package com.github.arielcarrera.cdi.test;

import io.agroal.api.AgroalDataSourceMetrics;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.test.config.AgroalConnectionProvider;
import com.github.arielcarrera.cdi.test.config.JtaEnvironment;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadWriteDeleteRepository;
import com.github.arielcarrera.cdi.test.services.DefaultTransactionalTestService;

public class RequestEntityManagerTest {

    @ClassRule
    public static JtaEnvironment jtaEnvironment = new JtaEnvironment();

    @Rule
    public WeldInitiator weld = WeldInitiator.from(new Weld()).activate(RequestScoped.class).inject(this).build();


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
    protected DefaultTransactionalTestService txService;
    
    @Inject
    protected TestReadWriteDeleteRepository loaderRepository;
    

    @Before
    public void statusPoolBegin() {
	AgroalDataSourceMetrics metrics = AgroalConnectionProvider.getMetrics();
	if (metrics != null) {
	    System.out.println("Metricas del pool INICIO " + currentMethod + ":" + metrics);
	    if (metrics.maxUsedCount() > 1) {
		System.out.println("Alerta: max=" + metrics.maxUsedCount() + " -> metodo: " + currentMethod
			+ " -> previo: " + lastMethod);
	    }
	}
    }

    @After
    public void statusPoolEnd() {
	AgroalDataSourceMetrics metrics = AgroalConnectionProvider.getMetrics();
	if (metrics != null) {
	    System.out.println("Metricas del pool FIN " + currentMethod + ":" + metrics);
	    if (metrics.maxUsedCount() > 1) {
		System.out.println("Alerta: max=" + metrics.maxUsedCount() + " -> metodo: " + currentMethod
			+ " -> previo: " + lastMethod);
	    }
	}
    }

    @Before
    public void load() {
	loaderRepository.save(new TestEntity(1, 1, null, LogicalDeletion.NORMAL_STATUS));
    }

    @Test
    public void test() {
	Assert.assertTrue(txService.testEntityManagerContains(1));
    }


}