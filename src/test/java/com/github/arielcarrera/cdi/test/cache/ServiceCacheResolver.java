package com.github.arielcarrera.cdi.test.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import javax.cache.Cache;
import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheResolver;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.infinispan.jcache.annotation.DefaultCacheResolver;

@ApplicationScoped
public class ServiceCacheResolver implements CacheResolver {

	@Inject
	DefaultCacheResolver defaultCacheResolver;

	@Override
	public <K, V> Cache<K, V> resolveCache(CacheInvocationContext<? extends Annotation> cacheInvocationContext) {
		cacheInvocationContext.getMethod().getAnnotation(annotationClass);
		new CacheInvocationContext<Annotation>() {

			@Override
			public Method getMethod() {
				return cacheInvocationContext.getMethod();
			}

			@Override
			public Set<Annotation> getAnnotations() {
				return cacheInvocationContext.getAnnotations();
			}

			@Override
			public Annotation getCacheAnnotation() {
				return cacheInvocationContext.getCacheAnnotation();
			}

			@Override
			public String getCacheName() {
				return null;
			}

			@Override
			public Object getTarget() {
				return cacheInvocationContext.getTarget();
			}

			@Override
			public CacheInvocationParameter[] getAllParameters() {
				return cacheInvocationContext.getAllParameters();
			}

			@Override
			public <T> T unwrap(Class<T> cls) {
				return cacheInvocationContext.unwrap(cls);
			}
		};
		return defaultCacheResolver.resolveCache(cacheInvocationContext);
	}
	

}
