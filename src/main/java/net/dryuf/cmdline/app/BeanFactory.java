package net.dryuf.cmdline.app;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;


/**
 * Bean producer.
 */
public interface BeanFactory
{
	default <T> T getBean(Class<? extends T> clazz)
	{
		return getBean((Type) clazz);
	}

	default <T> T getBean(Class<? extends T> clazz, Class<? extends Annotation> annotated)
	{
		return getBean((Type) clazz, annotated);
	}

	default <T, Q extends Annotation> T getBean(Class<? extends T> clazz, Q qualifier)
	{
		return getBean((Type) clazz, qualifier);
	}

	<T> T getBean(Type type);

	<T> T getBean(Type type, Class<? extends Annotation> annotated);

	<T, Q extends Annotation> T getBean(Type type, Q qualifier);

	<T> T getBean(BindingType<T> type);

	BeanFactory createChild(Map<Class<?>, Object> childBeans);

	BeanFactory createChildAnnotated(Map<Map.Entry<Type, Class<? extends Annotation>>, Object> childBeans);

	BeanFactory createChildQualified(Map<Map.Entry<Type, Annotation>, Object> childBeans);

	static class BindingType<T>
	{
	}
}
