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

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import com.github.arielcarrera.cdi.test.entities.TestEntity;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class QuickstartEntityRepository {

    @Inject
    private EntityManager entityManager;
    
    @Inject
    private QuickstartEntityRepository2 repo2;

    public List<TestEntity> findAll() {
        assert entityManager != null;
        List<TestEntity> entities = entityManager
                .createQuery("select qe from TestEntity qe", TestEntity.class).getResultList();
        return entities;
    }

    @Transactional
    public Integer save(TestEntity TestEntity) {
        assert entityManager != null;
        if (TestEntity.getId() == null) {
            entityManager.persist(TestEntity);
        } else {
            entityManager.merge(TestEntity);
        }
        System.out.println("Saved entity: " + TestEntity);
        return TestEntity.getId();
    }

    public void clear() {
        assert entityManager != null;
        findAll().forEach(entityManager::remove);
        repo2.clear();
    }
    
    @Transactional
    public void testSuspendAndRollback(TestEntity TestEntity, TestEntity TestEntity2) {
        assert entityManager != null;
        
        repo2.save(TestEntity);
        System.out.println("Saved 1 entity: " + TestEntity);
        if (TestEntity2.getId() == null) {
            entityManager.persist(TestEntity2);
        } else {
            entityManager.merge(TestEntity2);
        }
        System.out.println("Saved 2 entity: " + TestEntity2);
        
        throw new RuntimeException("for rollback");
    }
    
    @Transactional
    public void testSuspendAndRollback_FirstSave(TestEntity TestEntity, TestEntity TestEntity2) {
        assert entityManager != null;
        
        if (TestEntity.getId() == null) {
            entityManager.persist(TestEntity);
        } else {
            entityManager.merge(TestEntity);
        }
        System.out.println("Saved 1 entity: " + TestEntity);

        repo2.save(TestEntity2);
        
        System.out.println("Saved 2 entity: " + TestEntity2);
        
        throw new RuntimeException("for rollback");
    }

}
