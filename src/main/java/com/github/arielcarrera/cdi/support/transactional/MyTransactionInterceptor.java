package com.github.arielcarrera.cdi.support.transactional;

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
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

/**
 * @author Ariel Carrera <carreraariel@gmail.com>
 */
public class MyTransactionInterceptor implements MethodInterceptor, Serializable {

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
		if (optional == null) { //value never processed
			Transactional txAnn = invocation.getMethod().getAnnotation(Transactional.class);
			// If there is no method annotation it will search at Class/Interface level
			if (txAnn == null) {
				txAnn = invocation.getMethod().getDeclaringClass().getAnnotation(Transactional.class);
			}
			optional = Optional.ofNullable(txAnn);
			cache.put(methodStr, optional);
		}
		
		if (optional.isPresent()) {
			//value processed / cached -> with annotation
			MyTransactionalInterceptorBase txInterceptor = (MyTransactionalInterceptorBase) CDI.current()
					.select(getInterceptorClass(optional.get()), Default.Literal.INSTANCE).get();
			return txInterceptor.intercept(createMethodInvocation(invocation, optional.get()));
		}
		
		//else value processed / cached -> No annotation
		return invocation.proceed();
	}

	/**
	 * Get Interceptor implementation by TxType of Transactional annotation
	 * @param txAnn
	 * @return Class of the interceptor
	 */
	private Class<?> getInterceptorClass(Transactional txAnn) {
		Class<?> txInterceptorClass = null;
		switch (txAnn.value()) {
			case REQUIRED:
				txInterceptorClass = MyTransactionalInterceptorRequired.class;
				break;
			case REQUIRES_NEW:
				txInterceptorClass = MyTransactionalInterceptorRequiresNew.class;
				break;
			case MANDATORY:
				txInterceptorClass = MyTransactionalInterceptorMandatory.class;
				break;
			case SUPPORTS:
				txInterceptorClass = MyTransactionalInterceptorSupports.class;
				break;
			case NOT_SUPPORTED:
				txInterceptorClass = MyTransactionalInterceptorNotSupported.class;
				break;
			case NEVER:
				txInterceptorClass = MyTransactionalInterceptorNever.class;
				break;
			default:
				txInterceptorClass = MyTransactionalInterceptorRequired.class;
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

	//
	// //---------------------------------------------------------------------
	// // Serialization support
	// //---------------------------------------------------------------------
	//
	// private void writeObject(ObjectOutputStream oos) throws IOException {
	// // Rely on default serialization, although this class itself doesn't carry state anyway...
	// oos.defaultWriteObject();
	//
	// // Deserialize superclass fields.
	//// oos.writeObject(getTransactionManagerBeanName());
	//// oos.writeObject(getTransactionManager());
	//// oos.writeObject(getTransactionAttributeSource());
	//// oos.writeObject(getBeanFactory());
	// }
	//
	// private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
	// // Rely on default serialization, although this class itself doesn't carry state anyway...
	// ois.defaultReadObject();
	//
	// // Serialize all relevant superclass fields.
	// // Superclass can't implement Serializable because it also serves as base class
	// // for AspectJ aspects (which are not allowed to implement Serializable)!
	//// setTransactionManagerBeanName((String) ois.readObject());
	//// setTransactionManager((PlatformTransactionManager) ois.readObject());
	//// setTransactionAttributeSource((TransactionAttributeSource) ois.readObject());
	//// setBeanFactory((BeanFactory) ois.readObject());
	// }
}
