package net.dryuf.cmdline.app.guice;

import net.dryuf.cmdline.app.BeanFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import java.util.Map;


/**
 * Guice based {@link BeanFactory}.
 */
@AllArgsConstructor(onConstructor = @__(@Inject))
public class GuiceBeanFactory implements BeanFactory
{
	private final Injector injector;

	@Override
	public <T> T getBean(Class<? extends T> clazz)
	{
		return injector.getInstance(clazz);
	}

	@Override
	public BeanFactory createChild(Map<Class<?>, Object> childBeans)
	{
		if (childBeans == null)
			return this;
		return new GuiceBeanFactory(injector.createChildInjector(new AbstractModule()
		{
			@SuppressWarnings("unchecked")
			@Override
			protected void configure()
			{
				childBeans.forEach((clazz, value) -> {
					if (clazz == value) {
						bind(clazz);
					}
					else {
						bind((Class<Object>)clazz).toInstance(value);
					}
				});
			}
		}));
	}
}
