package com.github.arielcarrera.cdi.test.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionScoped;

import org.springframework.data.RepositoryCreation;

/**
 * EntityManager producer
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ApplicationScoped
public class EntityManagerProducer {

    @Inject
    private EntityManagerFactory emf;

    @Produces
    @TransactionScoped
    public EntityManager produceEntityManager() {
        return emf.createEntityManager();
    }

    public void close(@Disposes EntityManager em) {
        em.close();
    }
    
    @Inject @RepositoryCreation
    private EntityManagerFactory emfCreation;

    @Produces @RepositoryCreation
    @Dependent
    public EntityManager produceEntityManagerCreation() {
        return emf.createEntityManager();
    }

    public void closeCreation(@Disposes @RepositoryCreation EntityManager em) {
        em.close();
    }
}
