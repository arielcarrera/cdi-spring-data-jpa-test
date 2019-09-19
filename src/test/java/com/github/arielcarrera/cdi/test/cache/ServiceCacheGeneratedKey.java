package com.github.arielcarrera.cdi.test.cache;

import java.lang.reflect.Method;
import java.util.Objects;

import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.GeneratedCacheKey;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ServiceCacheGeneratedKey implements GeneratedCacheKey {

	private static final long serialVersionUID = 1L;
	@Getter
	private final String key;
	private final int hashCode;

	public ServiceCacheGeneratedKey(Method method, CacheInvocationParameter[] parameters) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getDeclaringClass().getName()).append(".").append(method.getName()).append("(");
		
		if (parameters != null && parameters.length > 0) {
			StringBuilder vb = new StringBuilder();
			vb.append("#[");
	        for (int j = 0; j < parameters.length; j++) {
	        	sb.append(parameters[j].getRawType().getName());
	        	if (parameters[j].getValue() != null) {
	        		vb.append(parameters[j].getValue().toString());
	        	}
	            if (j < (parameters.length - 1)) {
	                sb.append(",");
	                vb.append(",");
	            }
	        }
	        sb.append(")");
	        vb.append("]");
	        
	        sb.append(vb);
		} else {
			sb.append(")");
		}
		
		key = sb.toString();
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
