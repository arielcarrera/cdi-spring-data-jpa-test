package com.github.arielcarrera.cdi.test;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.github.arielcarrera.cdi.test.config.JtaEnvironment;
import com.github.arielcarrera.cdi.test.services.EmService1;
import com.github.arielcarrera.cdi.test.services.EmService2;

public class CdiJpaRequestScopedTest {

    @ClassRule
    public static JtaEnvironment jtaEnvironment = new JtaEnvironment();
    
    @Rule
    public WeldInitiator weld = WeldInitiator.from(new Weld()).activate(RequestScoped.class).inject(this).build();

    @Inject
    EmService1 em1;
    
    @Inject
    EmService2 em2;
    
    @Test
    public void test() {
    	assert(em1.getEm().hashCode() == em2.getEm().hashCode());
    }
    
}