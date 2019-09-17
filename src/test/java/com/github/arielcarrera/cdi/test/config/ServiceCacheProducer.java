package com.github.arielcarrera.cdi.test.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;

/**
 * Service Cache producer
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ApplicationScoped
public class ServiceCacheProducer {

    /**
     * <p>This producer overrides the default cache configuration used by the default cache manager.</p>
     *
     * <p>The default cache configuration defines that a cache entry will have a lifespan of 60000 ms.</p>
     */
    @Produces
    public Configuration defaultCacheConfiguration() {
       return new ConfigurationBuilder()
             .expiration().lifespan(60000l).customInterceptors().addInterceptor().interceptor(new HibernateCacheInterceptor())
             .build();
    }
}
