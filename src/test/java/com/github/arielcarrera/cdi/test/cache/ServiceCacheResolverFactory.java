package com.github.arielcarrera.cdi.test.cache;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheResolver;
import javax.cache.annotation.CacheResolverFactory;
import javax.cache.annotation.CacheResult;
import javax.cache.spi.CachingProvider;
import javax.inject.Inject;

import lombok.NoArgsConstructor;

public class ServiceCacheResolverFactory implements CacheResolverFactory {
	private final CachingProvider provider;
    private final CacheManager manager;
    private final Function<String, CacheResolver> cacheResolverComputer;
 
    protected ServiceCacheResolverFactory() { // for proxies
        provider = null;
        manager = null;
        cacheResolverComputer = null;
    }
 
    @Inject
    public ServiceCacheResolverFactory(final ApplicationConfiguration configuration) {
        final Configuration config = new MutableConfiguration().setStoreByValue(false).setManagementEnabled(configuration.getJCacheConfig().isJmx()).setStatisticsEnabled(configuration.getJCacheConfig().isStatistics());
 
        final Class<?>[] cacheApi = new Class<?>[]{ Cache.class };
        final Function<String, CacheResolver> noCacheResolver = name -> new ConstantCacheResolver(Cache.class.cast(Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(), cacheApi, new EmptyCacheHandler(name, config))));
 
        if (!configuration.getJCacheConfig().isActivated()) {
            provider = null;
            manager = null;
            cacheResolverComputer = noCacheResolver;
        } else {
            final ClassLoader classLoader = ServiceCacheResolverFactory.class.getClassLoader();
            provider = Caching.getCachingProvider(classLoader);
            manager = provider.getCacheManager(provider.getDefaultURI(), classLoader, configuration.getProperties());
 
            final Function<String, CacheResolver> activeCacheResolver = name -> new ConstantCacheResolver(ofNullable(manager.getCache(name))
                .orElseGet(() -> {
                    try {
                        return manager.createCache(name, config);
                    } catch (final CacheException ce) {
                        return manager.getCache(name);
                    }
                }));
 
            final Collection<String> activeCaches = new HashSet<>(asList(activated.split(" *, *")));
            cacheResolverComputer = name -> (configuration.getJCacheConfig().acceptsCache(name) ? activeCacheResolver : noCacheResolver).apply(name);
            }
        }
    }
 
    @PreDestroy
    private void shutdownJCache() {
        ofNullable(manager).ifPresent(CacheManager::close);
        ofNullable(provider).ifPresent(CachingProvider::close);
    }
 
    @Override
    public CacheResolver getCacheResolver(final CacheMethodDetails<? extends Annotation> cacheMethodDetails) {
        return cacheResolverComputer.apply(cacheMethodDetails.getCacheName());
    }
 
    @Override
    public CacheResolver getExceptionCacheResolver(final CacheMethodDetails<CacheResult> cacheMethodDetails) {
        return cacheResolverComputer.apply(ofNullable(cacheMethodDetails.getCacheAnnotation().exceptionCacheName())
            .filter(name -> !name.isEmpty())
            .orElseThrow(() -> new IllegalArgumentException("CacheResult#exceptionCacheName() not specified")));
    }
 
    private static class ConstantCacheResolver implements CacheResolver {
        private final Cache<?, ?> delegate;
 
        public ConstantCacheResolver(final Cache<?, ?> cache) {
            delegate = cache;
        }
 
        @Override
        public <K, V> Cache<K, V> resolveCache(final CacheInvocationContext<? extends Annotation> cacheInvocationContext) {
            return (Cache<K, V>) delegate;
        }
    }
 
    private static class EmptyCacheHandler implements InvocationHandler {
        private final Map<Method, Object> returns = new HashMap<>();
 
        public EmptyCacheHandler(final String name, final Configuration<?, ?> configuration) {
            for (final Method m : Cache.class.getMethods()) {
                if (m.getReturnType() == boolean.class) {
                    returns.put(m, false);
                } else if ("getAll".equals(m.getName())) {
                    returns.put(m, emptyMap());
                } else if ("iterator".equals(m.getName())) {
                    returns.put(m, emptyIterator());
                } else if ("getConfiguration".equals(m.getName())) {
                    returns.put(m, configuration);
                } else if ("getName".equals(m.getName())) {
                    returns.put(m, name);
                } // getCacheManager? will return null for now
                // else null is fine for void and method returning a value etc...
            }
        }
 
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            return returns.get(method);
        }
    }
    
    //////////
	@Inject
	ServiceCacheResolver resolver;
	
	@Override
	public CacheResolver getCacheResolver(CacheMethodDetails<? extends Annotation> cacheMethodDetails) {
		return resolver;
	}

	@Override
	public CacheResolver getExceptionCacheResolver(CacheMethodDetails<CacheResult> cacheMethodDetails) {
		return resolver;
	};

}
