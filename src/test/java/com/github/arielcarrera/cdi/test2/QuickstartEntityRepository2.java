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
import javax.transaction.Transactional.TxType;

import com.github.arielcarrera.cdi.test.entities.TestEntity;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class QuickstartEntityRepository2 {

    @Inject
    private EntityManager entityManager;

    public List<TestEntity> findAll() {
        assert entityManager != null;
        List<TestEntity> entities = entityManager
        	.createQuery("select qe from TestEntity qe", TestEntity.class).getResultList();
        return entities;
    }

    @Transactional(TxType.REQUIRES_NEW)
    public Integer save(TestEntity quickstartEntity) {
        assert entityManager != null;
        if (quickstartEntity.getId() == null) {
            entityManager.persist(quickstartEntity);
        } else {
            entityManager.merge(quickstartEntity);
        }
        System.out.println("Saved entity: " + quickstartEntity);
        return quickstartEntity.getId();
    }

    public void clear() {
        assert entityManager != null;
        findAll().forEach(entityManager::remove);
    }

}
