/*
 * License: Apache License, Version 2.0
 * See the LICENSE file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.demos.jpacditesting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.TransactionalException;

import org.hibernate.demos.jpacditesting.support.JtaEnvironment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.TestReadableRepository;
import com.github.arielcarrera.cdi.test.repositories.TestWritableRepository;

public class CdiJpaTest {

	@ClassRule
	public static JtaEnvironment jtaEnvironment = new JtaEnvironment();

	@Rule
	public WeldInitiator weld = WeldInitiator.from(new Weld()).activate(RequestScoped.class, ApplicationScoped.class)
			.inject(this).build();

	@Inject
	private EntityManager entityManager;

//    @Inject
//    private UserTransaction ut;

	@Inject
	private TestWritableRepository writableRepository;

	@Inject
	private TestReadableRepository readableRepository;


	@Test
	public void testWriteableRepositoryManualTransactionManagement() {
		entityManager.getTransaction().begin();

		TestEntity te = new TestEntity(1, 101);
		writableRepository.save(te);

		te = new TestEntity(2, 102);
		writableRepository.save(te);
		entityManager.getTransaction().commit();
		assertTrue(readableRepository.findAll().size() == 2);
	}

}
