package com.github.arielcarrera.cdi.test.config;

import org.jnp.server.NamingBeanImpl;
import org.junit.rules.ExternalResource;

import com.arjuna.ats.jta.utils.JNDIManager;

public class JtaEnvironment extends ExternalResource {

    private NamingBeanImpl NAMING_BEAN;

    @Override
    protected void before() throws Throwable {
        NAMING_BEAN = new NamingBeanImpl();
        NAMING_BEAN.start();

        JNDIManager.bindJTAImplementation();
        
    }

    @Override
    protected void after() {
        NAMING_BEAN.stop();
    }
}
