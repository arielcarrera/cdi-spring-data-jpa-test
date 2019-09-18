package com.github.arielcarrera.cdi.test.cache;

import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Objects;

import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.GeneratedCacheKey;

@ToString
public class ServiceCacheGeneratedKey implements GeneratedCacheKey {

    private static final long serialVersionUID = 1L;
    @Getter
    private final String key;
    private final int hashCode;

    public ServiceCacheGeneratedKey(Method method, CacheInvocationParameter[] parameters) {
	StringBuilder sb = new StringBuilder();
	sb.append(method.getDeclaringClass().getName()).append(".").append(method.getName()).append("(");
	for (CacheInvocationParameter cacheInvocationParameter : parameters) {
	    cacheInvocationParameter.getRawType().getDeclaringClass().getName();
	}
	sb.append(")");
	if (parameters != null && parameters.length > 0) {
	    
	    key = method.getName() + "#" + parameters.toString();
	} else {
	    key = method.getName();
	}
	
	hashCode = key.hashCode();
    }

    @Override
    public int hashCode() {
	return hashCode;
    }

    @Override
    public boolean equals(Object other) {
	if (other == null) {
	    return false;
	}
	if (this == other) {
	    return true;
	}
	if (hashCode != other.hashCode() || !(other instanceof ServiceCacheGeneratedKey)) {
	    // hashCode is part of this check since it is pre-calculated and hash must match
	    // for equals to be true
	    return false;
	}
	final ServiceCacheGeneratedKey that = (ServiceCacheGeneratedKey) other;
	return Objects.equals(key, that.key) && hashCode == that.hashCode;
    };
    
//    private String generateMethodKey() {
//	try {
//            StringBuilder sb = new StringBuilder();
//            specificToStringHeader(sb);
//
//            sb.append('(');
//            separateWithCommas(parameterTypes, sb);
//            sb.append(')');
//            return sb.toString();
//        } catch (Exception e) {
//            return "<" + e + ">";
//        }
//    }
    
}
