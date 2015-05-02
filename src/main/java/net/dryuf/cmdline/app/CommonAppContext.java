package net.dryuf.cmdline.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;


/**
 * {@link AppContext} implementation.
 */
@Getter
@AllArgsConstructor
public class CommonAppContext implements AppContext
{
	private final BeanFactory beanFactory;

	public CommonAppContext createChild(Map<Class<?>, Object> beans)
	{
		return new CommonAppContext(beanFactory.createChild(beans));
	}
}
