package com.github.arielcarrera.cdi.test.services;

import java.util.List;
import java.util.Optional;

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheResult;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.infinispan.Cache;

import com.github.arielcarrera.cdi.test.cache.ServiceCacheKeyGenerator;
import com.github.arielcarrera.cdi.test.config.ServiceCache;
import com.github.arielcarrera.cdi.test.entities.CacheableEntity;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.repositories.CacheableReadWriteRepository;
import com.github.arielcarrera.cdi.test.repositories.TestReadOnlyRepository;

@CacheDefaults(cacheName = "service-cache", cacheKeyGenerator = ServiceCacheKeyGenerator.class, cacheResolverFactory = )
public class CacheableTestService {

	@Inject
	private CacheableReadWriteRepository repo;

	@Inject
	private TestReadOnlyRepository repo2;

	@ServiceCache
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

	@CacheResult
	@Transactional
	public List<CacheableEntity> cachedServiceFindCacheableEntityByValue(Integer value) {
		return repo.findAllByValue(value);
	}

	@CacheResult(cacheName = "service-cache2")
	@Transactional
	public TestEntity cachedServiceFindTestEntityByValue(Integer value) {
		return repo2.findOneByValue(value);
	}
	
//	@CacheRemove(cacheName = "service-cache", cacheKeyGenerator = ServiceCacheKeyGenerator.class)
//	public void cachedServiceFindTestEntityByValue(Integer value) {
//	}

	@ServiceCache
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
