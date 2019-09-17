package com.github.arielcarrera.cdi.test.cache;

import java.util.Collection;

import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheRemoveAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.infinispan.Cache;

import com.github.arielcarrera.cdi.test.entities.CacheableEntity;

@Named
@ApplicationScoped
public class ServiceCacheManager {

   @Inject
   private Cache<CacheKey, CacheableEntity> cache;

   public String getCacheName() {
      return cache.getName();
   }

   public int getNumberOfEntries() {
      return cache.size();
   }

   public long getExpirationLifespan() {
      return cache.getCacheConfiguration().expiration().lifespan();
   }

   public CacheableEntity[] getCachedValues() {
      Collection<CacheableEntity> cachedValues = cache.values();
      return cachedValues.toArray(new CacheableEntity[cachedValues.size()]);
   }

   @CacheRemoveAll
   public void clearCache() {
   }

}