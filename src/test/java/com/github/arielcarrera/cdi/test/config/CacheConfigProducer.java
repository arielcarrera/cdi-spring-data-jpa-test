package com.github.arielcarrera.cdi.test.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.infinispan.cdi.embedded.ConfigureCache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.InterceptorConfiguration.Position;

import com.github.arielcarrera.cdi.test.cache.TestInfinispanCacheInterceptor;

/**
 * Service Cache producer
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ApplicationScoped
public class CacheConfigProducer {

    /**
     * <p>
     * This producer overrides the default cache configuration used by the default
     * cache manager.
     * </p>
     *
     * <p>
     * The default cache configuration defines that a cache entry will have a
     * lifespan of 60000 ms.
     * </p>
     */
    @ConfigureCache("service-cache")
    @CustomCache
    @Produces
    public Configuration serviceCacheConfiguration() {
	return new ConfigurationBuilder().simpleCache(false).customInterceptors().addInterceptor()
		.interceptor(new TestInfinispanCacheInterceptor()).position(Position.FIRST).expiration().lifespan(60000l)
		.build();
    }
    
    @Produces
    public Configuration defaultCacheConfiguration() {
	return new ConfigurationBuilder().simpleCache(false).customInterceptors().addInterceptor()
		.interceptor(new TestInfinispanCacheInterceptor()).position(Position.FIRST).expiration().lifespan(60000l)
		.build();
    }
}
