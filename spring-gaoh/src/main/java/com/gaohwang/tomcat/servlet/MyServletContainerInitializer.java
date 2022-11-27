package com.gaohwang.tomcat.servlet;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.annotation.HandlesTypes;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.ReflectionUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * ServletContainerInitializer实现类
 *
 * @author gaoh
 * @since 2022/11/24
 */
@HandlesTypes(HandlesInterface.class)
public class MyServletContainerInitializer implements ServletContainerInitializer {

	/**
	 * spi  META-INF/services/javax.servlet.ServletContainerInitializer
	 * <p>
	 * Servlet 3.0 spi 会获取ServletContainerInitializer接口实现类上面@HandlesTypes所有接口所有实现类
	 *
	 * @param handlesInterfaceClasses
	 * @param ctx
	 * @throws ServletException
	 */
	@Override
	public void onStartup(Set<Class<?>> handlesInterfaceClasses, ServletContext ctx) throws ServletException {
		List<HandlesInterface> initializers = Collections.emptyList();

		if (handlesInterfaceClasses != null) {
			initializers = new ArrayList<>(handlesInterfaceClasses.size());
			for (Class<?> waiClass : handlesInterfaceClasses) {
				// Be defensive: Some servlet containers provide us with invalid classes,
				// no matter what @HandlesTypes says...
				if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) &&
						HandlesInterface.class.isAssignableFrom(waiClass)) {
					try {
						initializers.add((HandlesInterface)
								ReflectionUtils.accessibleConstructor(waiClass).newInstance());
					} catch (Throwable ex) {
						throw new ServletException("Failed to instantiate MyServletContainerInitializer class", ex);
					}
				}
			}
		}

		if (initializers.isEmpty()) {
			ctx.log("No MyServletContainerInitializer types detected on classpath");
			return;
		}

		ctx.log(initializers.size() + " MyServletContainerInitializer detected on classpath");
		AnnotationAwareOrderComparator.sort(initializers);
		for (HandlesInterface initializer : initializers) {
			initializer.Handles(ctx);
		}
	}
}
