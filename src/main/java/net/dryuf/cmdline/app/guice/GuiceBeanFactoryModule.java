package net.dryuf.cmdline.app.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.dryuf.cmdline.app.BeanFactory;

import javax.inject.Inject;


/**
 * Minimal Guice module providing BeanFactory.
 */
public class GuiceBeanFactoryModule extends AbstractModule
{
	@Override
	protected void configure()
	{
	}

	@Provides
	@Singleton
	@Inject
	public BeanFactory beanFactory(Injector injector)
	{
		return new GuiceBeanFactory(injector);
	}
}
