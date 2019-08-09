/*
 * Copyright 2008-2019 the original author or authors.
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

import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Query lookup strategy to execute finders.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Mark Paluch
 */
public final class JpaQueryLookupStrategy {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private JpaQueryLookupStrategy() {}

	/**
	 * Base class for {@link QueryLookupStrategy} implementations that need access to an {@link EntityManager}.
	 *
	 * @author Oliver Gierke
	 * @author Thomas Darimont
	 */
	private abstract static class AbstractQueryLookupStrategy implements QueryLookupStrategy {

		private final EntityManager em;
		private final QueryExtractor provider;

		//modificado entityManager
		private final EntityManager emCreation;
		
		/**
		 * Creates a new {@link AbstractQueryLookupStrategy}.
		 *
		 * @param em
		 * @param extractor
		 */
		public AbstractQueryLookupStrategy(EntityManager em, EntityManager emCreation, QueryExtractor extractor) {

			this.em = em;
			this.emCreation = emCreation;
			this.provider = extractor;
		}
		
		@Override
		public final RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory,
				NamedQueries namedQueries) {
			return resolveQuery(new JpaQueryMethod(method, metadata, factory, provider), em, emCreation, namedQueries);
		}

		protected abstract RepositoryQuery resolveQuery(JpaQueryMethod method, EntityManager em, EntityManager emCreation, NamedQueries namedQueries);
		//fin modificado entityManager
	}

	/**
	 * {@link QueryLookupStrategy} to create a query from the method name.
	 *
	 * @author Oliver Gierke
	 * @author Thomas Darimont
	 */
	private static class CreateQueryLookupStrategy extends AbstractQueryLookupStrategy {

		private final PersistenceProvider persistenceProvider;
		private final EscapeCharacter escape;

		//modificado entitymanager
		public CreateQueryLookupStrategy(EntityManager em, EntityManager emCreation, QueryExtractor extractor, EscapeCharacter escape) {

			super(em, emCreation, extractor);

			this.persistenceProvider = PersistenceProvider.fromEntityManager(emCreation);
			this.escape = escape;
		}
		
		@Override
		protected RepositoryQuery resolveQuery(JpaQueryMethod method, EntityManager em, EntityManager emCreation, NamedQueries namedQueries) {
			return new PartTreeJpaQuery(method, em, emCreation, persistenceProvider, escape);
		}
		//fin modificado entitymanager
	}

	/**
	 * {@link QueryLookupStrategy} that tries to detect a declared query declared via {@link Query} annotation followed by
	 * a JPA named query lookup.
	 *
	 * @author Oliver Gierke
	 * @author Thomas Darimont
	 */
	private static class DeclaredQueryLookupStrategy extends AbstractQueryLookupStrategy {

		private final QueryMethodEvaluationContextProvider evaluationContextProvider;

		//modificado entityManager
		/**
		 * Creates a new {@link DeclaredQueryLookupStrategy}.
		 *
		 * @param em
		 * @param emCreation
		 * @param extractor
		 * @param evaluationContextProvider
		 */
		public DeclaredQueryLookupStrategy(EntityManager em, EntityManager emCreation, QueryExtractor extractor,
				QueryMethodEvaluationContextProvider evaluationContextProvider) {

			super(em, emCreation, extractor);
			this.evaluationContextProvider = evaluationContextProvider;
		}
		
		@Override
		protected RepositoryQuery resolveQuery(JpaQueryMethod method, EntityManager em, EntityManager emCreation, NamedQueries namedQueries) {

			RepositoryQuery query = JpaQueryFactory.INSTANCE.fromQueryAnnotation(method, em, emCreation, evaluationContextProvider);

			if (null != query) {
				return query;
			}

			query = JpaQueryFactory.INSTANCE.fromProcedureAnnotation(method, em, emCreation);

			if (null != query) {
				return query;
			}

			String name = method.getNamedQueryName();
			if (namedQueries.hasQuery(name)) {
				return JpaQueryFactory.INSTANCE.fromMethodWithQueryString(method, em, emCreation, namedQueries.getQuery(name),
						evaluationContextProvider);
			}

			query = NamedQuery.lookupFrom(method, em, emCreation);

			if (null != query) {
				return query;
			}

			throw new IllegalStateException(
					String.format("Did neither find a NamedQuery nor an annotated query for method %s!", method));
		}
		//fin modificado entityManager
	}

	/**
	 * {@link QueryLookupStrategy} to try to detect a declared query first (
	 * {@link org.springframework.data.jpa.repository.Query}, JPA named query). In case none is found we fall back on
	 * query creation.
	 *
	 * @author Oliver Gierke
	 * @author Thomas Darimont
	 */
	private static class CreateIfNotFoundQueryLookupStrategy extends AbstractQueryLookupStrategy {

		private final DeclaredQueryLookupStrategy lookupStrategy;
		private final CreateQueryLookupStrategy createStrategy;

		//modificado entityManager
		/**
		 * Creates a new {@link CreateIfNotFoundQueryLookupStrategy}.
		 *
		 * @param em
		 * @param emCreation
		 * @param extractor
		 * @param createStrategy
		 * @param lookupStrategy
		 */
		public CreateIfNotFoundQueryLookupStrategy(EntityManager em, EntityManager emCreation, QueryExtractor extractor,
				CreateQueryLookupStrategy createStrategy, DeclaredQueryLookupStrategy lookupStrategy) {

			super(em, emCreation, extractor);

			this.createStrategy = createStrategy;
			this.lookupStrategy = lookupStrategy;
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy.AbstractQueryLookupStrategy#resolveQuery(org.springframework.data.jpa.repository.query.JpaQueryMethod, javax.persistence.EntityManager, org.springframework.data.repository.core.NamedQueries)
		 */
		@Override
		protected RepositoryQuery resolveQuery(JpaQueryMethod method, EntityManager em, EntityManager emCreation, NamedQueries namedQueries) {

			try {
				return lookupStrategy.resolveQuery(method, em, emCreation, namedQueries);
			} catch (IllegalStateException e) {
				return createStrategy.resolveQuery(method, em, emCreation, namedQueries);
			}
		}
		//fin modificado entityManager
	}
	
	//modificado entitymanager
	/**
	 * Creates a {@link QueryLookupStrategy} for the given {@link EntityManager} and {@link Key}.
	 *
	 * @param em must not be {@literal null}.
	 * @param emCreation if is null uses em
	 * @param key may be {@literal null}.
	 * @param extractor must not be {@literal null}.
	 * @param evaluationContextProvider must not be {@literal null}.
	 * @param escape
	 * @return
	 */
	public static QueryLookupStrategy create(EntityManager em, EntityManager emCreation, @Nullable Key key, QueryExtractor extractor,
			QueryMethodEvaluationContextProvider evaluationContextProvider, EscapeCharacter escape) {

		Assert.notNull(em, "EntityManager must not be null!");
		Assert.notNull(extractor, "QueryExtractor must not be null!");
		Assert.notNull(evaluationContextProvider, "EvaluationContextProvider must not be null!");

		switch (key != null ? key : Key.CREATE_IF_NOT_FOUND) {
			case CREATE:
				return new CreateQueryLookupStrategy(em, emCreation, extractor, escape);
			case USE_DECLARED_QUERY:
				return new DeclaredQueryLookupStrategy(em, emCreation, extractor, evaluationContextProvider);
			case CREATE_IF_NOT_FOUND:
				return new CreateIfNotFoundQueryLookupStrategy(em, emCreation, extractor,
						new CreateQueryLookupStrategy(em, emCreation, extractor, escape),
						new DeclaredQueryLookupStrategy(em, emCreation, extractor, evaluationContextProvider));
			default:
				throw new IllegalArgumentException(String.format("Unsupported query lookup strategy %s!", key));
		}
	}
	//fin modificado entityManager
}