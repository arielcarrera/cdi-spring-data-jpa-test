package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import com.github.arielcarrera.cdi.test.config.HibernateCacheInterceptor;
import com.github.arielcarrera.cdi.test.config.JtaEnvironment;
import com.github.arielcarrera.cdi.test.entities.CacheableEntity;
import com.github.arielcarrera.cdi.test.services.CacheableTestService;

/**
 * Tests for Transactional Operations
 * 
 * @author Ariel Carrera
 *
 */
@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class CacheableTest {

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
	HibernateCacheInterceptor.clearHistory();

	List<CacheableEntity> list = new ArrayList<>();
	for (int i = 1; i < 11; i++) {
	    list.add(new CacheableEntity(i, i + 100, i + 100, LogicalDeletion.NORMAL_STATUS));
	}
	service.saveAll(list);
	TestJdbcUtil.jdbcPutCacheable(new CacheableEntity(11, 111, 111, LogicalDeletion.NORMAL_STATUS));
	TestJdbcUtil.jdbcPutCacheable(new CacheableEntity(12, 112, 112, LogicalDeletion.NORMAL_STATUS));
    }

    @Test
    public void findById_cachedPreviously() {
	try {
	    Optional<CacheableEntity> optional = service.findById(1);
	    assertTrue(optional.isPresent());
	} finally {
	    List<String> history = HibernateCacheInterceptor.getHistory();
	    assertFalse(history.isEmpty());
	    assertFalse(
		    history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#1=FALSE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertFalse(
		    history.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#1=FALSE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertFalse(
		    history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#1=TRUE"))
			    .collect(Collectors.toList()).isEmpty());
	}
    }

    @Test
    public void findById11_notCachedPreviously() {
	try {
	    Optional<CacheableEntity> optional = service.findById(11);
	    assertTrue(optional.isPresent());
	} finally {
	    List<String> history = HibernateCacheInterceptor.getHistory();
	    assertTrue(history.size() > 2);
	    List<String> inicio = history.subList(0, history.size() - 2);
	    List<String> fin = history.subList(history.size() - 2, history.size());
	    //Check Not loaded previously
	    assertTrue(
		    inicio.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#11=FALSE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertTrue(
		    inicio.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#11=FALSE"))
			    .collect(Collectors.toList()).isEmpty());
	    //Check 
	    assertFalse(
		    fin.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#11=FALSE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertFalse(
		    fin.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#11=FALSE"))
			    .collect(Collectors.toList()).isEmpty());
	}
    }

    @Test
    public void findById100_notFound() {
	try {
	    Optional<CacheableEntity> optional = service.findById(100);
	    assertFalse(optional.isPresent());
	} finally {
	    List<String> history = HibernateCacheInterceptor.getHistory();
	    assertFalse(history.isEmpty());
	    assertFalse(history.stream()
		    .filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#100=FALSE"))
		    .collect(Collectors.toList()).isEmpty());
	    assertTrue(history.stream()
		    .filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#100=FALSE"))
		    .collect(Collectors.toList()).isEmpty());
	    assertTrue(history.stream()
		    .filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#100=TRUE"))
		    .collect(Collectors.toList()).isEmpty());
	}
    }

    @Test
    public void findAllByValue_queryNotById_notCached() {
	try {
	    List<CacheableEntity> findAllByValue = service.findAllByValue(110);
	    assertEquals(findAllByValue.size(), 1);
	} finally {
	    List<String> history = HibernateCacheInterceptor.getHistory();
	    assertFalse(history.isEmpty());
	    
	    //Check that after that, entity is refreshed/added in cache
	    assertFalse(
		    history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#10=FALSE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertFalse(
		    history.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#10=FALSE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertTrue(
		    history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#10=TRUE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertFalse(
		    history.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#10=TRUE"))
			    .collect(Collectors.toList()).isEmpty());
	}
    }
    
    @Test
    public void findAllByValue_queryNotById_cachedService() {
	try {
	    List<CacheableEntity> findAllByValue = service.cachedServiceFindAllByValue(110);
	    assertEquals(findAllByValue.size(), 1);
	} finally {
	    List<String> history = HibernateCacheInterceptor.getHistory();
	    assertFalse(history.isEmpty());
	    
	    //Check that after that, entity is refreshed/added in cache
	    assertFalse(
		    history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#10=FALSE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertFalse(
		    history.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#10=FALSE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertTrue(
		    history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#10=TRUE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertFalse(
		    history.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#10=TRUE"))
			    .collect(Collectors.toList()).isEmpty());
	}
	HibernateCacheInterceptor.clearHistory();
	try {
	    List<CacheableEntity> findAllByValue = service.cachedServiceFindAllByValue(110);
	    assertEquals(findAllByValue.size(), 1);
	} finally {
	    List<String> history = HibernateCacheInterceptor.getHistory();
	    assertFalse(history.isEmpty());
	    
	    //Check that after that, entity is refreshed/added in cache
	    assertFalse(
		    history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#10=FALSE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertFalse(
		    history.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#10=FALSE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertTrue(
		    history.stream().filter(s -> s.trim().equals("GET:" + CacheableEntity.class.getName() + "#10=TRUE"))
			    .collect(Collectors.toList()).isEmpty());
	    assertFalse(
		    history.stream().filter(s -> s.trim().equals("PUT:" + CacheableEntity.class.getName() + "#10=TRUE"))
			    .collect(Collectors.toList()).isEmpty());
	}
    }

}