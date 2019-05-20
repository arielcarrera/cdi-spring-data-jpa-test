package com.github.arielcarrera.cdi.support.config;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;

import com.github.arielcarrera.cdi.support.base.CustomSimpleJpaRepository;

public class CustomCdiConfiguration implements CdiRepositoryConfiguration {

	@Override
	public String getRepositoryImplementationPostfix() {
		return "Impl";
	}
	
	@Override
	public Optional<Class<?>> getRepositoryBeanClass() {
		return Optional.of(CustomSimpleJpaRepository.class);
	}


	@Override
	public Optional<List<RepositoryProxyPostProcessor>> getRepositoryProxyPostProcessorClassList() {
		return Optional.of(Arrays.asList(new CustomTransactionalRepositoryProxyPostProcessor()));
	}
	
}