/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jpa.repository.query;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;

/**
 * Factory to create the appropriate {@link RepositoryQuery} for a {@link JpaQueryMethod}.
 *
 * @author Thomas Darimont
 * @author Mark Paluch
 */
enum JpaQueryFactory {

	INSTANCE;

	private static final SpelExpressionParser PARSER = new SpelExpressionParser();
	private static final Logger LOG = LoggerFactory.getLogger(JpaQueryFactory.class);
//Changed for entityManager issue
	/**
	 * Creates a {@link RepositoryQuery} from the given {@link QueryMethod} that is potentially annotated with
	 * {@link Query}.
	 *
	 * @param method must not be {@literal null}.
	 * @param em must not be {@literal null}.
	 * @param emCreation
	 * @param evaluationContextProvider
	 * @return the {@link RepositoryQuery} derived from the annotation or {@code null} if no annotation found.
	 */
	@Nullable
	AbstractJpaQuery fromQueryAnnotation(JpaQueryMethod method, EntityManager em, EntityManager emCreation,
			QueryMethodEvaluationContextProvider evaluationContextProvider) {

		LOG.debug("Looking up query for method {}", method.getName());
		return fromMethodWithQueryString(method, em, emCreation, method.getAnnotatedQuery(), evaluationContextProvider);
	}

	/**
	 * Creates a {@link RepositoryQuery} from the given {@link String} query.
	 *
	 * @param method must not be {@literal null}.
	 * @param em must not be {@literal null}.
	 * @param emCreation
	 * @param queryString must not be {@literal null} or empty.
	 * @param evaluationContextProvider
	 * @return
	 */
	@Nullable
	AbstractJpaQuery fromMethodWithQueryString(JpaQueryMethod method, EntityManager em, EntityManager emCreation, @Nullable String queryString,
			QueryMethodEvaluationContextProvider evaluationContextProvider) {

		if (queryString == null) {
			return null;
		}

		return method.isNativeQuery() ? new NativeJpaQuery(method, em, emCreation, queryString, evaluationContextProvider, PARSER)
				: new SimpleJpaQuery(method, em, emCreation, queryString, evaluationContextProvider, PARSER);
	}

	/**
	 * Creates a {@link StoredProcedureJpaQuery} from the given {@link JpaQueryMethod} query.
	 *
	 * @param method must not be {@literal null}.
	 * @param em must not be {@literal null}.
	 * @param emCreation
	 * @return
	 */
	@Nullable
	public StoredProcedureJpaQuery fromProcedureAnnotation(JpaQueryMethod method, EntityManager em, EntityManager emCreation) {

		if (!method.isProcedureQuery()) {
			return null;
		}

		return new StoredProcedureJpaQuery(method, em, emCreation);
	}
//End Changed for entityManager issue
}
