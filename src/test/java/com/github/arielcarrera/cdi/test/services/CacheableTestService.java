package com.github.arielcarrera.cdi.test.services;

import java.util.List;
import java.util.Optional;

import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import javax.cache.annotation.CacheValue;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.infinispan.Cache;

import com.github.arielcarrera.cdi.test.config.CustomCache;
import com.github.arielcarrera.cdi.test.entities.CacheableEntity;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.CacheableReadWriteRepository;
import com.github.arielcarrera.cdi.test.repositories.TestReadOnlyRepository;

//@CacheDefaults(cacheName = "service-cache", cacheKeyGenerator = ServiceCacheKeyGenerator.class, cacheResolverFactory = )
public class CacheableTestService {

	@Inject
	private CacheableReadWriteRepository repo;

	@Inject
	private TestReadOnlyRepository repo2;

	@CustomCache
	@Inject
	private Cache<String, String> cache;

	@Transactional
	public void save(CacheableEntity e) {
		repo.save(e);
	}

	@Transactional
	public Optional<CacheableEntity> findById(Integer id) {
		return repo.findById(id);
	}

	@Transactional
	public void saveAll(List<CacheableEntity> list) {
		repo.saveAll(list);
	}

	@Transactional
	public List<CacheableEntity> findAllByValue(Integer value) {
		return repo.findAllByValue(value);
	}
	
	@Transactional
	public List<CacheableEntity> findWithCacheHintByValue(Integer value) {
		return repo.findByValue(value);
	}

	@Transactional
	public TestEntity findWithoutCacheById(Integer value) {
	    Optional<TestEntity> findById = repo2.findById(value);
		return findById.orElse(null);
	}
	
	//default cache by method name
	@CacheResult
	@Transactional
	public List<CacheableEntity> cacheFindCacheableEntityByValue(Integer value) {
		return repo.findAllByValue(value);
	}
	
	//test-entity cache
	@CacheResult(cacheName = "test-entity")
	@Transactional
	public TestEntity cacheFindTestEntityById(@CacheKey Integer value) {
	    Optional<TestEntity> findById = repo2.findById(value);
		return findById.orElse(null);
	}
	
	//test-entity cache PUT
	@CachePut(cacheName = "test-entity")
	public void putCacheTestEntity(@CacheKey Integer value, @CacheValue TestEntity entity) {
	}
	
	//test-entity cache REMOVE
	@CacheRemove(cacheName = "test-entity")
	public void removeCacheTestEntity(@CacheKey Integer value) {
	}
	
	//test-entity cache REMOVE ALL
	@CacheRemoveAll(cacheName = "test-entity")
	public void removeAllCacheTestEntity() {
	}
	
	//programmatically usage
	@Transactional
	public String getCachedValue(String value) {
		String cachedValue = cache.get(value);
		if (cachedValue == null) {
			cachedValue = "Hello " + value;
			cache.put(value, cachedValue);
		}
		return cachedValue;
	}
}
