package com.github.arielcarrera.cdi.test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;		

@RunWith(Suite.class)				
@Suite.SuiteClasses({
    	TransactionalScopedEntityManagerTest.class,
	TransactionalTest.class,
	ReadOnlyRepositoryTest.class,
	ReadOnlySoftDeleteRepositoryTest.class,
	ReadWriteDeleteRepositoryTest.class,
	ReadWriteRepositoryTest.class,
	ReadWriteSoftDeleteRepositoryTest.class,
	CacheTest.class
})		
public class TestSuite {				
			// This class remains empty, it is used only as a holder for the above annotations		
}