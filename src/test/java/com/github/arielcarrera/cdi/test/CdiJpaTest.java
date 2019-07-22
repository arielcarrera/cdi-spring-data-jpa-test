package com.github.arielcarrera.cdi.test;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.github.arielcarrera.cdi.test.config.EntityManagerFactoryProducer;
import com.github.arielcarrera.cdi.test.config.EntityManagerProducer;
import com.github.arielcarrera.cdi.test.config.JtaEnvironment;

public class CdiJpaTest {

    @ClassRule
    public static JtaEnvironment jtaEnvironment = new JtaEnvironment();
    
    @Inject
    private EntityManager em;
    
    @Rule
    public WeldInitiator weld = WeldInitiator.from(EntityManagerFactoryProducer.class, EntityManagerProducer.class)
            .activate(RequestScoped.class,SessionScoped.class)
            .inject(this)	
            .build();
    
    @Test
    public void canInjectEntityManager() {
    	Assert.assertTrue(em.hashCode() != 0);
    }
    
    @Test
    public void canInjectEntityManagerAndTransaction() {
    	Assert.assertNotNull(em.getTransaction());
    }
}