package com.gaohwang.tomcat.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * 转换器执行位置-AbstractMessageConverterMethodProcessor#writeWithMessageConverters
 * AbstractMessageConverterMethodProcessor#writeWithMessageConverters(T, org.springframework.core.MethodParameter, org.springframework.http.server.ServletServerHttpRequest, org.springframework.http.server.ServletServerHttpResponse)
 * <p>
 * 参数解析和执行结果返回-ServletInvocableHandlerMethod#invokeAndHandle
 * org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod#invokeAndHandle(org.springframework.web.context.request.ServletWebRequest, org.springframework.web.method.support.ModelAndViewContainer, java.lang.Object...)
 *
 * @Author: GH
 * @Date: 2019/12/10 23:41
 * @Version 1.0
 */
@Configurable
@ComponentScan("com.gaohwang.tomcat")
public class Config /*extends WebMvcConfigurationSupport*/ {

	/*@Override
	protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.extendMessageConverters(converters);
		converters.add(fastConverter());

	}*/

	/**
	 * fastJson转换器
	 *
	 * @return
	 */
	private HttpMessageConverter<?> fastConverter() {
		// 定义一个convert转换消息的对象
		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
		// 添加FastJson的配置信息
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		// 默认转换器
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
				SerializerFeature.WriteNullNumberAsZero,
				SerializerFeature.MapSortField,
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.DisableCircularReferenceDetect,
				SerializerFeature.WriteDateUseDateFormat,
				SerializerFeature.WriteNullListAsEmpty);
		fastJsonConfig.setCharset(StandardCharsets.UTF_8);
		// 处理中文乱码问题
		List<MediaType> fastMediaTypes = new ArrayList<>();
		fastMediaTypes.add(MediaType.APPLICATION_JSON);
		fastConverter.setSupportedMediaTypes(fastMediaTypes);
		// 在convert中添加配置信息
		fastConverter.setFastJsonConfig(fastJsonConfig);

		return fastConverter;
	}
}
