package net.dryuf.cmdline.app;

import java.util.Map;


/**
 * Bean producer.
 */
public interface BeanFactory
{
	<T> T getBean(Class<? extends T> clazz);

	BeanFactory createChild(Map<Class<?>, Object> childBeans);
}
