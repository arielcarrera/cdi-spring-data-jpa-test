package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;
import com.github.arielcarrera.cdi.test.cache.TestInfinispanCacheInterceptor;
import com.github.arielcarrera.cdi.test.config.JtaEnvironment;
import com.github.arielcarrera.cdi.test.entities.CacheableEntity;
import com.github.arielcarrera.cdi.test.entities.TestEntity;
import com.github.arielcarrera.cdi.test.services.CacheableTestService;

/**
 * Tests for Transactional Operations
 * 
 * @author Ariel Carrera
 *
 */
@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class CacheTest {

    @ClassRule
    public static JtaEnvironment jtaEnvironment = new JtaEnvironment();

    @Rule
    public WeldInitiator weld = WeldInitiator.from(new Weld()).inject(this).build();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TestRule watcher = new TestWatcher() {
	protected void starting(Description description) {
	    log.info("Starting TEST: " + description.getMethodName());
	}
    };

    @Inject
    protected CacheableTestService service;

    @Before
    public void load() {
	TestInfinispanCacheInterceptor.clearHistory();

	List<CacheableEntity> list = new ArrayList<>();
	for (int i = 1; i < 11; i++) {
	    list.add(new CacheableEntity(i, i + 100, i + 100, LogicalDeletion.NORMAL_STATUS));
	}
	service.saveAll(list);
	// adding more CacheableEntity (without caching them)
	TestJdbcUtil.jdbcPutCacheable(new CacheableEntity(11, 111, 111, LogicalDeletion.NORMAL_STATUS));
	TestJdbcUtil.jdbcPutCacheable(new CacheableEntity(12, 112, 112, LogicalDeletion.NORMAL_STATUS));

	// adding more TestEntity (without caching them)
	TestJdbcUtil.jdbcPut(new TestEntity(1, 111, 111, LogicalDeletion.NORMAL_STATUS));
	TestJdbcUtil.jdbcPut(new TestEntity(2, 112, 112, LogicalDeletion.NORMAL_STATUS));
    }

    //****************HIBERNATE 2ND LEVEL CACHE TESTS:****************
    @Test
    public void findById_cachedPreviously() {
	Optional<CacheableEntity> optional = service.findById(1);
	assertTrue(optional.isPresent());
	List<String> history = TestInfinispanCacheInterceptor.getHistory();
	assertFalse(history.isEmpty());
	assertFalse(history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#1=FALSE"))
		.collect(Collectors.toList()).isEmpty());
	assertFalse(history.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#1=FALSE"))
		.collect(Collectors.toList()).isEmpty());
	assertFalse(history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#1=TRUE"))
		.collect(Collectors.toList()).isEmpty());
    }

    @Test
    public void findById11_notCachedPreviously() {
	Optional<CacheableEntity> optional = service.findById(11);
	assertTrue(optional.isPresent());
	List<String> history = TestInfinispanCacheInterceptor.getHistory();
	assertTrue(history.size() > 2);
	List<String> inicio = history.subList(0, history.size() - 2);
	List<String> fin = history.subList(history.size() - 2, history.size());
	// Check Not loaded previously
	assertTrue(inicio.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#11=FALSE"))
		.collect(Collectors.toList()).isEmpty());
	assertTrue(inicio.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#11=FALSE"))
		.collect(Collectors.toList()).isEmpty());
	// Check
	assertFalse(fin.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#11=FALSE"))
		.collect(Collectors.toList()).isEmpty());
	assertFalse(fin.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#11=FALSE"))
		.collect(Collectors.toList()).isEmpty());
    }

    @Test
    public void findById100_notFound() {
	Optional<CacheableEntity> optional = service.findById(100);
	assertFalse(optional.isPresent());
	List<String> history = TestInfinispanCacheInterceptor.getHistory();
	assertFalse(history.isEmpty());
	assertFalse(
		history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#100=FALSE"))
			.collect(Collectors.toList()).isEmpty());
	assertTrue(
		history.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#100=FALSE"))
			.collect(Collectors.toList()).isEmpty());
	assertTrue(history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#100=TRUE"))
		.collect(Collectors.toList()).isEmpty());
    }

    @Test
    public void findAllByValue_queryNotById_notCached() {
	List<CacheableEntity> findAllByValue = service.findAllByValue(110);
	assertEquals(findAllByValue.size(), 1);
	List<String> history = TestInfinispanCacheInterceptor.getHistory();
	assertFalse(history.isEmpty());

	// Check that after that, entity is refreshed/added in cache
	assertFalse(
		history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#10=FALSE"))
			.collect(Collectors.toList()).isEmpty());
	assertFalse(
		history.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#10=FALSE"))
			.collect(Collectors.toList()).isEmpty());
	assertTrue(history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#10=TRUE"))
		.collect(Collectors.toList()).isEmpty());
	assertFalse(history.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#10=TRUE"))
		.collect(Collectors.toList()).isEmpty());
    }

    //****************JCACHE TESTS:****************
    @Test
    public void findById_noCacheableEntity_cacheResult() {
	TestEntity entity = service.cacheFindTestEntityById(1);
	assertNotNull(entity);
	List<String> history = TestInfinispanCacheInterceptor.getHistory();
	assertFalse(history.isEmpty());

	// Check that after that, result is cached at service layer
	assertTrue(history.stream().filter(s -> s.trim().equals("GET:" + TestEntity.class.getName() + "#1=FALSE"))
		.collect(Collectors.toList()).isEmpty());
	assertTrue(history.stream().filter(s -> s.trim().equals("PUT:" + TestEntity.class.getName() + "#1=FALSE"))
		.collect(Collectors.toList()).isEmpty());
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEY:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=FALSE"))
		.collect(Collectors.toList()).isEmpty());
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEYVALUE:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=FALSE"))
		.collect(Collectors.toList()).isEmpty());
	TestInfinispanCacheInterceptor.clearHistory();

	entity = service.cacheFindTestEntityById(1);
	assertNotNull(entity);
	history = TestInfinispanCacheInterceptor.getHistory();
	assertEquals(history.size(), 1);

	assertTrue(history.get(0).trim().startsWith("READWRITEKEY:DefaultCacheKey{parameters=[1], hashCode=")
		&& history.get(0).trim().endsWith("=TRUE"));
    }

    @Test
    public void putCacheTestEntity_noCacheableEntity_cachePut() {
	service.cacheFindTestEntityById(1);

	TestInfinispanCacheInterceptor.clearHistory();

	service.putCacheTestEntity(1, new TestEntity(99, 99, 99));

	List<String> history = TestInfinispanCacheInterceptor.getHistory();
	assertEquals(history.size(), 1);

	// Check for cache UPDATE
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEYVALUE:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=FALSE"))
		.collect(Collectors.toList()).isEmpty());

	TestInfinispanCacheInterceptor.clearHistory();

	TestEntity cacheFindTestEntityById = service.cacheFindTestEntityById(1);

	assertNotNull(cacheFindTestEntityById);

	assertEquals(cacheFindTestEntityById.getId(), new Integer(99));
	assertEquals(cacheFindTestEntityById.getValue(), new Integer(99));
	
	history = TestInfinispanCacheInterceptor.getHistory();
	assertEquals(history.size(), 1);

	// Check that after that, result is the new value cached
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEY:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=TRUE"))
		.collect(Collectors.toList()).isEmpty());
    }
    
    @Test
    public void removeCacheTestEntity_noCacheableEntity_cacheRemove() {
	TestInfinispanCacheInterceptor.clearHistory();
	
	TestEntity cacheFindTestEntityById = service.cacheFindTestEntityById(1);

	assertNotNull(cacheFindTestEntityById);
	List<String> history = TestInfinispanCacheInterceptor.getHistory();
	assertEquals(history.size(), 2);

	// Check that result is cached
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEY:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=FALSE")).collect(Collectors.toList()).isEmpty());
		
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEYVALUE:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=FALSE")).collect(Collectors.toList()).isEmpty());
	
	TestInfinispanCacheInterceptor.clearHistory();
	//remove from cache
	service.removeCacheTestEntity(1);
	history = TestInfinispanCacheInterceptor.getHistory();
	assertEquals(history.size(), 1);
	
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEY:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=TRUE")).collect(Collectors.toList()).isEmpty());
	
	TestInfinispanCacheInterceptor.clearHistory();
	
	//Check that if I call again it is not cached
	cacheFindTestEntityById = service.cacheFindTestEntityById(1);

	assertNotNull(cacheFindTestEntityById);
	history = TestInfinispanCacheInterceptor.getHistory();
	assertEquals(history.size(), 2);

	// Check that result is cached
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEY:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=FALSE")).collect(Collectors.toList()).isEmpty());
		
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEYVALUE:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=FALSE")).collect(Collectors.toList()).isEmpty());
    }
    
    @Test
    public void removeAllCacheTestEntity_noCacheableEntity_cacheRemoveAll() {
	TestInfinispanCacheInterceptor.clearHistory();
	
	TestEntity cacheFindTestEntityById = service.cacheFindTestEntityById(1);
	assertNotNull(cacheFindTestEntityById);
	TestEntity cacheFindTestEntityById2 = service.cacheFindTestEntityById(2);
	assertNotNull(cacheFindTestEntityById2);

	List<String> history = TestInfinispanCacheInterceptor.getHistory();
	assertEquals(history.size(), 4);

	// Check that result is cached
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEY:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=FALSE")).collect(Collectors.toList()).isEmpty());
		
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEYVALUE:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=FALSE")).collect(Collectors.toList()).isEmpty());
	
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEY:DefaultCacheKey{parameters=[2], hashCode=")
			&& s.trim().endsWith("=FALSE")).collect(Collectors.toList()).isEmpty());
		
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEYVALUE:DefaultCacheKey{parameters=[2], hashCode=")
			&& s.trim().endsWith("=FALSE")).collect(Collectors.toList()).isEmpty());
	
	TestInfinispanCacheInterceptor.clearHistory();
	//remove all from cache
	service.removeAllCacheTestEntity();
	
	history = TestInfinispanCacheInterceptor.getHistory();
	assertEquals(history.size(), 1);
	assertTrue(history.get(0).equals("CLEAR"));
	
	TestInfinispanCacheInterceptor.clearHistory();
	
	//Check that if I call again it is not cached
	cacheFindTestEntityById = service.cacheFindTestEntityById(1);
	assertNotNull(cacheFindTestEntityById);
	cacheFindTestEntityById2 = service.cacheFindTestEntityById(2);
	assertNotNull(cacheFindTestEntityById2);

	history = TestInfinispanCacheInterceptor.getHistory();
	assertEquals(history.size(), 4);

	// Check that result is cached
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEY:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=FALSE")).collect(Collectors.toList()).isEmpty());
		
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEYVALUE:DefaultCacheKey{parameters=[1], hashCode=")
			&& s.trim().endsWith("=FALSE")).collect(Collectors.toList()).isEmpty());
	
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEY:DefaultCacheKey{parameters=[2], hashCode=")
			&& s.trim().endsWith("=FALSE")).collect(Collectors.toList()).isEmpty());
		
	assertFalse(history.stream()
		.filter(s -> s.trim().startsWith("READWRITEKEYVALUE:DefaultCacheKey{parameters=[2], hashCode=")
			&& s.trim().endsWith("=FALSE")).collect(Collectors.toList()).isEmpty());
    }
    
    //****************PROGRAMMATICA MANAGEMENT OF CUSTOM CACHE:****************
    @Test
    public void getCachedValue_customCacheHandling() {
	TestInfinispanCacheInterceptor.clearHistory();
	
	String result = service.getCachedValue("Test");
	assertEquals("Hello Test", result);

	List<String> history = TestInfinispanCacheInterceptor.getHistory();
	
	assertEquals(history.size(),2);
	assertTrue(history.get(0).trim().equals("GET:Test=FALSE"));
	assertTrue(history.get(1).trim().equals("PUT:Test=FALSE"));
	
	TestInfinispanCacheInterceptor.clearHistory();
	result = service.getCachedValue("Test");
	assertEquals("Hello Test", result);
	history = TestInfinispanCacheInterceptor.getHistory();
	
	assertEquals(history.size(),1);
	assertTrue(history.get(0).trim().equals("GET:Test=TRUE"));
    }

    //****************SPRING DATA JPA QUERY WITH HINT:****************
    @Test
    public void findWithCacheHintByValue_queryNotById() {
	TestInfinispanCacheInterceptor.clearHistory();
	List<CacheableEntity> findAllByValue = service.findWithCacheHintByValue(111);
	assertEquals(findAllByValue.size(), 1);
	List<String> history = TestInfinispanCacheInterceptor.getHistory();
	assertEquals(history.size(), 3);

	// Check cache
	assertTrue(history.get(0).trim().startsWith("GET:sql: select cacheablee0_.id as id1_0_, cacheablee0_.status as status2_0_, "
			+ "cacheablee0_.lazy_id as lazy_id5_0_, cacheablee0_.uniqueValue as uniqueVa3_0_, cacheablee0_.value as value4_0_ from "
			+ "CacheableEntity cacheablee0_ where cacheablee0_.value=?; parameters: ; named parameters: {param0=111}")
			&& history.get(0).trim().endsWith("=FALSE"));
	assertTrue(history.get(1).trim().equals("PUT:com.github.arielcarrera.cdi.test.entities.CacheableEntity#11=FALSE"));
	assertTrue(history.get(2).trim().startsWith("PUT:sql: select cacheablee0_.id as id1_0_, cacheablee0_.status as status2_0_, "
		+ "cacheablee0_.lazy_id as lazy_id5_0_, cacheablee0_.uniqueValue as uniqueVa3_0_, cacheablee0_.value as value4_0_ from "
		+ "CacheableEntity cacheablee0_ where cacheablee0_.value=?; parameters: ; named parameters: {param0=111}")
		&& history.get(2).trim().endsWith("=FALSE"));
	
	
	TestInfinispanCacheInterceptor.clearHistory();
	findAllByValue = service.findWithCacheHintByValue(111);
	assertEquals(findAllByValue.size(), 1);
	history = TestInfinispanCacheInterceptor.getHistory();
	assertEquals(history.size(), 2);
	
	// Check that value returns from cache
	assertTrue(history.get(0).trim().startsWith("GET:sql: select cacheablee0_.id as id1_0_, cacheablee0_.status as status2_0_, "
			+ "cacheablee0_.lazy_id as lazy_id5_0_, cacheablee0_.uniqueValue as uniqueVa3_0_, cacheablee0_.value as value4_0_ from "
			+ "CacheableEntity cacheablee0_ where cacheablee0_.value=?; parameters: ; named parameters: {param0=111}")
			&& history.get(0).trim().endsWith("=TRUE"));
	assertTrue(history.get(1).trim().equals("GET:com.github.arielcarrera.cdi.test.entities.CacheableEntity#11=TRUE"));
	
    }
    
    //****************SPRING DATA QUERY WITHOUT CACHE (DEFAULT):****************
    @Test
    public void findWithoutCacheByValue_queryNotById() {
	TestInfinispanCacheInterceptor.clearHistory();
	TestEntity entity = service.findWithoutCacheById(1);
	assertNotNull(entity);
	
	List<String> history = TestInfinispanCacheInterceptor.getHistory();
	assertEquals(history.size(), 0);
    }
    
}