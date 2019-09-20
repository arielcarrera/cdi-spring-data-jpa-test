Test Project: CDI + JPA + JTA + Spring Data JPA integration
=========================================

Sample project for CDI, JPA, JTA and Spring Data JPA integration.

# Added Support for:
  - Better repository composition (Spring Data JPA)
  - @Transactional (JTA)
  - @TransactionalScoped EntityManager (JPA + CDI) / Support for a second entityManager with @Dependent (for initialization usage)
  - @ChacheResult, @CachePut, @CacheRemove, @CacheRemoveAll (JCache)
  - @Cacheable (JPA 2ND Level Cache)


Other resources:

hibernate-demos Link: https://github.com/hibernate/hibernate-demos/tree/master/other/cdi-jpa-testing

Gunnar Morling post: http://in.relation.to/2019/01/23/testing-cdi-beans-and-persistence-layer-under-java-se/
