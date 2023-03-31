package net.dryuf.cmdline.app.guice;

import com.google.inject.Key;
import net.dryuf.cmdline.app.BeanFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;


/**
 * Guice based {@link BeanFactory}.
 */
@AllArgsConstructor(onConstructor = @__(@Inject))
public class GuiceBeanFactory implements BeanFactory
{
	private final Injector injector;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(Type type)
	{
		return (T) injector.getInstance(Key.get(type));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(Type type, Class<? extends Annotation> annotated)
	{
		return (T) injector.getInstance(Key.get(type, annotated));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, Q extends Annotation> T getBean(Type type, Q qualifier)
	{
		return (T) injector.getInstance(Key.get(type, qualifier));
	}

	@Override
	public <T> T getBean(BindingType<T> type)
	{
		throw new UnsupportedOperationException();
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

	@Override
	public BeanFactory createChildAnnotated(Map<Map.Entry<Type, Class<? extends Annotation>>, Object> childBeans)
	{
		if (childBeans == null)
			return this;
		return new GuiceBeanFactory(injector.createChildInjector(new AbstractModule()
		{
			@SuppressWarnings("unchecked")
			@Override
			protected void configure()
			{
				childBeans.forEach((key, value) -> {
					Type type = key.getKey();
					Class<? extends Annotation> annotation = key.getValue();
					if (type == value) {
						bind((Class<?>) type);
					}
					else {
						bind((Key<Object>) (annotation == null ? Key.get(type) : Key.get(type, annotation)))
							.toInstance(value);
					}
				});
			}
		}));
	}

	@Override
	public BeanFactory createChildQualified(Map<Map.Entry<Type, Annotation>, Object> childBeans)
	{
		if (childBeans == null)
			return this;
		return new GuiceBeanFactory(injector.createChildInjector(new AbstractModule()
		{
			@SuppressWarnings("unchecked")
			@Override
			protected void configure()
			{
				childBeans.forEach((key, value) -> {
					Type type = key.getKey();
					Annotation qualifier = key.getValue();
					if (type == value) {
						bind((Class<?>) type);
					}
					else {
						bind((Key<Object>) (qualifier == null ? Key.get(type) : Key.get(type, qualifier)))
							.toInstance(value);
					}
				});
			}
		}));
	}
}
