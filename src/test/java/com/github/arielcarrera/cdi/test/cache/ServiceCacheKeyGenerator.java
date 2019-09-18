package com.github.arielcarrera.cdi.test.cache;

import java.lang.annotation.Annotation;

import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.CacheKeyInvocationContext;
import javax.cache.annotation.GeneratedCacheKey;

public class ServiceCacheKeyGenerator implements CacheKeyGenerator {
    @Override
    public GeneratedCacheKey generateCacheKey(final CacheKeyInvocationContext< ? extends Annotation> cacheKeyInvocationContext) {
	return new ServiceCacheGeneratedKey(cacheKeyInvocationContext.getMethod(), cacheKeyInvocationContext.getAllParameters());
    }
    
}