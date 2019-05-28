package com.github.arielcarrera.cdi.support.config;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.interceptor.InvocationContext;
import javax.transaction.Transactional;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import com.github.arielcarrera.cdi.support.transactional.CustomTransactionalInterceptorBase;
import com.github.arielcarrera.cdi.support.transactional.CustomTransactionalInterceptorMandatory;
import com.github.arielcarrera.cdi.support.transactional.CustomTransactionalInterceptorNever;
import com.github.arielcarrera.cdi.support.transactional.CustomTransactionalInterceptorNotSupported;
import com.github.arielcarrera.cdi.support.transactional.CustomTransactionalInterceptorRequired;
import com.github.arielcarrera.cdi.support.transactional.CustomTransactionalInterceptorRequiresNew;
import com.github.arielcarrera.cdi.support.transactional.CustomTransactionalInterceptorSupports;

/**
 * {@link RepositoryProxyPostProcessor} that sets up interceptors to do
 * transactional management
 * 
 * @author Ariel Carrera
 *
 */
public class CustomTransactionalPostProcessor implements RepositoryProxyPostProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.core.support.RepositoryProxyPostProcessor
	 * #postProcess(org.springframework.aop.framework.ProxyFactory,
	 * org.springframework.data.repository.core.RepositoryInformation)
	 */
	@Override
	public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
		factory.addAdvice(new MyTransactionInterceptor());
	}

	/**
	 * @author Ariel Carrera <carreraariel@gmail.com>
	 */
	static class MyTransactionInterceptor implements MethodInterceptor, Serializable {

		private static final long serialVersionUID = -3519640247926903556L;

		private static final ConcurrentHashMap<String, Optional<Transactional>> cache = new ConcurrentHashMap<>();

		/**
		 * Create a new TransactionInterceptor.
		 * <p>
		 * Transaction manager and transaction attributes still need to be set.
		 * 
		 * @see #setTransactionManager
		 * @see #setTransactionAttributes(java.util.Properties)
		 * @see #setTransactionAttributeSource(TransactionAttributeSource)
		 */
		public MyTransactionInterceptor() {
		}

		@Override
		@Nullable
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String methodStr = invocation.getMethod().toString();
			Optional<Transactional> optional = cache.get(methodStr);
			if (optional == null) { // value never processed
				Transactional txAnn = invocation.getMethod().getAnnotation(Transactional.class);
				// If there is no method annotation it will search at Class/Interface level
				if (txAnn == null) {
					txAnn = invocation.getMethod().getDeclaringClass().getAnnotation(Transactional.class);
				}
				optional = Optional.ofNullable(txAnn);
				cache.put(methodStr, optional);
			}

			if (optional.isPresent()) {
				// value processed / cached -> with annotation
				CustomTransactionalInterceptorBase txInterceptor = (CustomTransactionalInterceptorBase) CDI.current()
						.select(getInterceptorClass(optional.get()), Default.Literal.INSTANCE).get();
				return txInterceptor.intercept(createMethodInvocation(invocation, optional.get()));
			}

			// else value processed / cached -> No annotation
			return invocation.proceed();
		}

		/**
		 * Get Interceptor implementation by TxType of Transactional annotation
		 * 
		 * @param txAnn
		 * @return Class of the interceptor
		 */
		private Class<?> getInterceptorClass(Transactional txAnn) {
			Class<?> txInterceptorClass = null;
			switch (txAnn.value()) {
			case REQUIRED:
				txInterceptorClass = CustomTransactionalInterceptorRequired.class;
				break;
			case REQUIRES_NEW:
				txInterceptorClass = CustomTransactionalInterceptorRequiresNew.class;
				break;
			case MANDATORY:
				txInterceptorClass = CustomTransactionalInterceptorMandatory.class;
				break;
			case SUPPORTS:
				txInterceptorClass = CustomTransactionalInterceptorSupports.class;
				break;
			case NOT_SUPPORTED:
				txInterceptorClass = CustomTransactionalInterceptorNotSupported.class;
				break;
			case NEVER:
				txInterceptorClass = CustomTransactionalInterceptorNever.class;
				break;
			default:
				txInterceptorClass = CustomTransactionalInterceptorRequired.class;
				break;
			}
			return txInterceptorClass;
		}

		private InvocationContext createMethodInvocation(MethodInvocation invocation, Transactional txAnnotation) {
			return new InvocationContext() {

				@Override
				public void setParameters(Object[] params) {
					// TODO Auto-generated method stub
				}

				@Override
				public Object proceed() throws Exception {
					try {
						return invocation.proceed();
					} catch (Exception e) {
						throw (Exception) e;
					} catch (Throwable t) {
						throw new InvocationTargetException(t);
					}
				}

				@Override
				public Object getTimer() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Object getTarget() {
					// Work out the target class: may be {@code null}.
					// The TransactionAttributeSource should be passed the target class
					// as well as the method, which may be from an interface.
					return (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
				}

				@Override
				public Object[] getParameters() {
					return invocation.getArguments();
				}

				@Override
				public Method getMethod() {
					return invocation.getMethod();
				}

				@Override
				public Map<String, Object> getContextData() {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put(Transactional.class.getName(), txAnnotation);
					return map;
				}

				@Override
				public Constructor<?> getConstructor() {
					// TODO Auto-generated method stub
					return null;
				}
			};
		}

	}
}
