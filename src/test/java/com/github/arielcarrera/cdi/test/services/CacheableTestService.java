package com.github.arielcarrera.cdi.test.services;

import java.util.List;
import java.util.Optional;

import javax.cache.annotation.CacheResult;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.github.arielcarrera.cdi.test.entities.CacheableEntity;
import com.github.arielcarrera.cdi.test.repositories.CacheableReadWriteRepository;

public class CacheableTestService {

	@Inject 
	private CacheableReadWriteRepository repo;
	
	
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
	public List<CacheableEntity> cachedServiceFindAllByValue(Integer value) {
	    return repo.findAllByValue(value);
	}
}
