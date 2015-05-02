package net.dryuf.cmdline.app;

import java.util.Map;


/**
 * Application context.
 */
public interface AppContext
{
	/**
	 * Gets bean factory.
	 *
	 * @return
	 * 	bean factory.
	 */
	BeanFactory getBeanFactory();

	/**
	 * Creates child AppContext.
	 *
	 * @param beans
	 * 	map of beans, pointing either to its Class or instance
	 *
	 * @return
	 * 	child context.
	 */
	CommonAppContext createChild(Map<Class<?>, Object> beans);
}
