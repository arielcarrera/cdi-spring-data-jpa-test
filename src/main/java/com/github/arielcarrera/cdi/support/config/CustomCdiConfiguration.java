package com.github.arielcarrera.cdi.support.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;

public class CustomCdiConfiguration implements CdiRepositoryConfiguration {

	@Override
	public String getRepositoryImplementationPostfix() {
		return "Impl";
	}
	
//	@Override
//	public Optional<Class<?>> getRepositoryBeanClass() {
//		return Optional.of(CustomSimpleJpaRepository.class);
//	}


	@Override
	public List<RepositoryProxyPostProcessor> getRepositoryProxyPostProcessors() {
		return Arrays.asList(new DataExceptionMapperPostProcessor(), new CustomTransactionalPostProcessor());
	}
	
}