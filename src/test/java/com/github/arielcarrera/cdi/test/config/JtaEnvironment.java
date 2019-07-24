package com.github.arielcarrera.cdi.test.config;

import org.jnp.server.NamingBeanImpl;
import org.junit.rules.ExternalResource;

import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
import com.arjuna.ats.jta.utils.JNDIManager;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;

public class JtaEnvironment extends ExternalResource {

	private NamingBeanImpl NAMING_BEAN;

	@Override
	protected void before() throws Throwable {
		NAMING_BEAN = new NamingBeanImpl();
		// Start JNDI server
		NAMING_BEAN.start();
		// Bind JTA implementation with default names
		JNDIManager.bindJTAImplementation();

		// Bind datasource
		TransactionalConnectionProvider.bindDataSource();
		// Set transaction log location
		setObjectStoreDir();
	}

	@Override
	protected void after() {
		NAMING_BEAN.stop();
	}


	static void setObjectStoreDir() {
		BeanPopulator.getDefaultInstance(ObjectStoreEnvironmentBean.class).setObjectStoreDir("target/tx-object-store");
		BeanPopulator.getNamedInstance(ObjectStoreEnvironmentBean.class, "communicationStore")
				.setObjectStoreDir("target/tx-object-store");
		BeanPopulator.getNamedInstance(ObjectStoreEnvironmentBean.class, "stateStore")
				.setObjectStoreDir("target/tx-object-store");
	}

}
