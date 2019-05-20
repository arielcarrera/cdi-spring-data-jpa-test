package com.github.arielcarrera.cdi.support.config;

import java.util.Optional;

import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;

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
}