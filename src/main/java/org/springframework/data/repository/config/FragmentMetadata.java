/*
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.repository.config;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;

/**
 * Value object for a discovered Repository fragment interface.
 * 
 * @author Mark Paluch
 * @author Oliver Gierke
 * @since 2.1
 */
@RequiredArgsConstructor
public class FragmentMetadata {

	private final MetadataReaderFactory factory;

	/**
	 * Returns all interfaces to be considered fragment ones for the given source interface.
	 * 
	 * @param interfaceName must not be {@literal null} or empty.
	 * @return
	 */
	public Stream<String> getFragmentInterfaces(String interfaceName) {

		Assert.hasText(interfaceName, "Interface name must not be null or empty!");
		
		String[] interfaceNames = getClassMetadata(interfaceName).getInterfaceNames();
		//BEGIN CHANGE LOOKUP
		//search for inherited interfaces
		Set<String> data = new HashSet<String>();
		
		for (String in : interfaceNames) {
			data.addAll(lookUpHierarchy(in));
		}
		
		return data.stream().filter(this::isCandidate);
		
		//END CHANGE LOOKUP
	}

	/**
	 * Returns whether the given interface is a fragment candidate.
	 *
	 * @param interfaceName must not be {@literal null} or empty.
	 * @param factory must not be {@literal null}.
	 * @return
	 */
	private boolean isCandidate(String interfaceName) {

		Assert.hasText(interfaceName, "Interface name must not be null or empty!");

		AnnotationMetadata metadata = getAnnotationMetadata(interfaceName);
		return !metadata.hasAnnotation(NoRepositoryBean.class.getName());

	}

	private AnnotationMetadata getAnnotationMetadata(String className) {

		try {
			return factory.getMetadataReader(className).getAnnotationMetadata();
		} catch (IOException e) {
			throw new BeanDefinitionStoreException(String.format("Cannot parse %s metadata.", className), e);
		}
	}

	private ClassMetadata getClassMetadata(String className) {

		try {
			return factory.getMetadataReader(className).getClassMetadata();
		} catch (IOException e) {
			throw new BeanDefinitionStoreException(String.format("Cannot parse %s metadata.", className), e);
		}
	}
	
	//CHANGE LOOKUP
	private static final String CLASS_LOADING_ERROR = "Could not load type %s.";
	private static final Logger LOGGER = LoggerFactory.getLogger(FragmentMetadata.class);
	
	private Set<String> lookUpHierarchy(String cls) {
		if (cls == null || cls.equals(Object.class.getName()))
			throw new IllegalArgumentException("Invalid parameters in lookUpHierarchy invocation");
		Set<String> interfacesSet = new HashSet<String>();
		interfacesSet.add(cls);
		Class<?> ic = null;
		try {
			ic = Class.forName(cls);
		} catch (ClassNotFoundException e) {
			LOGGER.warn(String.format(CLASS_LOADING_ERROR, cls), e);
		}
		for (final Class<?> clazz : ic.getInterfaces()) {
			String clazzName = clazz.getName();
			if (!interfacesSet.contains(clazzName)) {
				interfacesSet.add(clazzName);
				lookUpHierarchy(clazz, interfacesSet);
			}
		}
		return interfacesSet;
	}
	
	private void lookUpHierarchy(Class<?> cls, Set<String> interfacesSet) {
		if (cls == null || cls.getName().equals(Object.class.getName()) || interfacesSet == null) {
			throw new IllegalArgumentException("Invalid parameters in lookUpHierarchy invocation");
		}

		for (final Class<?> clazz : cls.getInterfaces()) {
			String clazzName = clazz.getName();
			if (!interfacesSet.contains(clazzName)) {
				interfacesSet.add(clazzName);
				lookUpHierarchy(clazz, interfacesSet);
			}
		}
	}
	
	//END CHANGE LOOKUP
}
