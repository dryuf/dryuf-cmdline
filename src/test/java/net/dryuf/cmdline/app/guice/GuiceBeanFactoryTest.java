package net.dryuf.cmdline.app.guice;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import net.dryuf.cmdline.app.BeanFactory;
import org.testng.annotations.Test;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.AbstractMap;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertEquals;


public class GuiceBeanFactoryTest
{
	GuiceBeanFactory beans = new GuiceBeanFactory(Guice.createInjector(new GuiceModule()));

	@Test
	public void getBean_class_getClass()
	{
		Object o = beans.getBean(ClassOne.class);
		assertThat(o, instanceOf(ClassOne.class));
	}

	@Test
	public void getBean_sharedAnnotated_getSpecific()
	{
		Object o2 = beans.getBean(IntShared.class, ResourceTwo.class);
		assertThat(o2, instanceOf(ClassTwo.class));
		Object o3 = beans.getBean(IntShared.class, ResourceThree.class);
		assertThat(o3, instanceOf(ClassThree.class));
	}

	@Test
	public void getBean_sharedNamed_getSpecific()
	{
		Object o2 = beans.getBean(IntShared.class, new Named() {
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return Named.class;
			}

			public String value() { return "two"; }
		});
		assertThat(o2, instanceOf(NamedTwo.class));
		Object o3 = beans.getBean(IntShared.class, Names.named("three"));
		assertThat(o3, instanceOf(NamedThree.class));
	}

	@Test
	public void createChild_addingClassClass_getsNew()
	{
		BeanFactory childs = beans.createChild(ImmutableMap.of(
			ChildOne.class, ChildOne.class
		));
		Object o = childs.getBean(ChildOne.class);
		assertThat(o, instanceOf(ChildOne.class));
	}

	@Test
	public void createChild_addingClassObject_getsNew()
	{
		ChildOne instance = new ChildOne();
		BeanFactory childs = beans.createChild(ImmutableMap.of(
			ChildOne.class, instance
		));
		Object o = childs.getBean(ChildOne.class);
		assertEquals(o, instance);
	}

	@Test
	public void createChild_addingAnnotatedClassObject_getsNew()
	{
		ChildOne instanceTwo = new ChildOne();
		ChildTwo instanceThree = new ChildTwo();
		BeanFactory childs = beans.createChildAnnotated(ImmutableMap.of(
			new AbstractMap.SimpleImmutableEntry<>(IntChild.class, ResourceTwo.class), instanceTwo,
			new AbstractMap.SimpleImmutableEntry<>(IntChild.class, ResourceThree.class), instanceThree
		));

		Object o2 = childs.getBean(IntChild.class, ResourceTwo.class);
		assertEquals(o2, instanceTwo);
		Object o3 = childs.getBean(IntChild.class, ResourceThree.class);
		assertEquals(o3, instanceThree);
	}

	@Test
	public void createChild_addingQualifiedClassObject_getsNew()
	{
		ChildOne instanceTwo = new ChildOne();
		ChildTwo instanceThree = new ChildTwo();
		BeanFactory childs = beans.createChildQualified(ImmutableMap.of(
			new AbstractMap.SimpleImmutableEntry<>(IntChild.class, Names.named("two")), instanceTwo,
			new AbstractMap.SimpleImmutableEntry<>(IntChild.class, Names.named("three")), instanceThree
		));

		Object o2 = childs.getBean(IntChild.class, Names.named("two"));
		assertEquals(o2, instanceTwo);
		Object o3 = childs.getBean(IntChild.class, Names.named("three"));
		assertEquals(o3, instanceThree);
	}


	public static class GuiceModule extends AbstractModule
	{
		@Override
		public void configure()
		{
			bind(ClassOne.class).in(Singleton.class);
			bind(Key.get(IntShared.class, ResourceTwo.class)).to(ClassTwo.class).in(Singleton.class);
			bind(Key.get(IntShared.class, ResourceThree.class)).to(ClassThree.class).in(Singleton.class);
		}

		@Provides
		@Singleton
		@Named("two")
		public IntShared shared2()
		{
			return new NamedTwo();
		}

		@Provides
		@Singleton
		@Named("three")
		public IntShared shared3()
		{
			return new NamedThree();
		}
	}

	private static interface IntShared
	{
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Qualifier
	private static @interface ResourceTwo {}

	@Retention(RetentionPolicy.RUNTIME)
	@Qualifier
	private static @interface ResourceThree {}

	private static class ClassOne
	{
	}

	private static class ClassTwo implements IntShared
	{
	}

	private static class ClassThree implements IntShared
	{
	}

	private static class NamedTwo implements IntShared
	{
	}

	private static class NamedThree implements IntShared
	{
	}

	private interface IntChild {
	}

	private static class ChildOne implements IntChild
	{
	}

	private static class ChildTwo implements IntChild
	{
	}
}
