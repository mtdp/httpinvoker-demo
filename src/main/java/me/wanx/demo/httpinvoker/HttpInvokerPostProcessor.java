package me.wanx.demo.httpinvoker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

/**
 * httpinvoker 注解后期装配类
 * @author gqwang
 *
 */
public class HttpInvokerPostProcessor implements BeanPostProcessor,ApplicationContextAware{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/** 是否是客户段 **/
	private boolean isClient = true;
	/** 客户端使用httpinvoker服务的map **/
	private Map<String,String> clientUrlMap = new HashMap<String,String>();
	/** 服务端提供httpinvoker服务的map **/
	private Map<String,String> serviceUrlMap = new HashMap<String,String>();
	
	private SimpleUrlHandlerMapping simpleUrlHandlerMapping;
	
	private ApplicationContext ctx;
	
	/**
	 * 注册httpinvoke服务
	 * @param bean
	 * @param beanName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object registerHttpInvokerService(Object bean, String beanName){
		//判断bean是否有注解
		Class<?> clazz = bean.getClass();
		if(clazz.isAnnotationPresent(HttpInvokerService.class)){
			//是service 获取提供服务的地址
			HttpInvokerService httpInvokerService = clazz.getAnnotation(HttpInvokerService.class);
			Class<?>[] annoClass = httpInvokerService.serviceInterfaceClass();
			String serviceUrl = httpInvokerService.serviceUrl();
			//生成exporter
			HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
			exporter.setServiceInterface(annoClass[0]);
			exporter.setService(this.getSimpleUrlHandlerMapping().getApplicationContext().getBean(beanName));
			//初始化此服务
			exporter.prepare();
			Map<String,HttpInvokerServiceExporter> urlMap = (Map<String,HttpInvokerServiceExporter>)this.getSimpleUrlHandlerMapping().getUrlMap();
			urlMap.put(this.serviceUrlMap.get(serviceUrl), exporter);
			this.getSimpleUrlHandlerMapping().initApplicationContext();
		}else{
			logger.info(beanName + " no HttpInvokerService annotation!");
		}
		return bean;
	}
	
	/**
	 * 装配需要使用httpinvoker服务的属性和set方法
	 * @param bean
	 * @param beanName
	 * @return
	 */
	private Object assem(Object bean, String beanName){
		Class<?> clazz = bean.getClass();
		if(!clazz.getName().startsWith("me.wanx")){
			//不是自己的类 不管
			return bean;
		}
		Method[] methods = clazz.getMethods();
		Field[] fields = clazz.getDeclaredFields();
		//遍历方法
		for(Method method : methods){
			if(method.isAnnotationPresent(HttpInvokerClient.class)){
				//获取set方法的形参类型
				Class<?>[] clazzMethod = method.getParameterTypes();
				HttpInvokerClient annoMethod = method.getAnnotation(HttpInvokerClient.class);
				String remoteServiceUrl = annoMethod.remoteServiceUrl();
				
				HttpInvokerProxyFactoryBean proxy = new HttpInvokerProxyFactoryBean();
				proxy.setServiceInterface(clazzMethod[0]);
				proxy.setServiceUrl(this.clientUrlMap.get(remoteServiceUrl));
				proxy.afterPropertiesSet();
				try {
					method.invoke(bean, proxy.getObject());
				} catch (IllegalArgumentException e) {
					logger.error("assem properties error",e);
				} catch (IllegalAccessException e) {
					logger.error("assem properties error",e);
				} catch (InvocationTargetException e) {
					logger.error("assem properties error",e);
				}
			}
		}
		//遍历属性
		for(Field field: fields){
			if(field.isAnnotationPresent(HttpInvokerClient.class)){
				//如果属性上有 HttpInvokerClient 注解  则注入值
				
				//获取属性类型
				Class<?> clazzField = field.getType();
				//获取注解的值
				HttpInvokerClient annoField = field.getAnnotation(HttpInvokerClient.class);
				String remoteServiceUrl = annoField.remoteServiceUrl();
				
				HttpInvokerProxyFactoryBean proxy = new HttpInvokerProxyFactoryBean();
				proxy.setServiceInterface(clazzField);
				proxy.setServiceUrl(this.clientUrlMap.get(remoteServiceUrl));
				//初始化
				proxy.afterPropertiesSet();
				try {
					field.setAccessible(true);
					field.set(bean, proxy.getObject());
				} catch (IllegalArgumentException e) {
					logger.error("assem properties error",e);
				} catch (IllegalAccessException e) {
					logger.error("assem properties error",e);
				}
			}
		}
		return bean;
	}
	
	/**
	 * bean 前期处理 将所有提供 httpinvoker 服务的类
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)throws BeansException {
		logger.info("--------------------------postProcessBeforeInitialization ..." + beanName);
		if(this.isClient){
			return bean;
		}
		if(this.simpleUrlHandlerMapping == null){
			logger.error("simpleUrlHandlerMapping is null!");
			return bean;
		}
		return registerHttpInvokerService(bean,beanName);
	}

	/**
	 * bean 后期处理 将所有客户端 要使用服务的client 将提供服务的url注入到属性中
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		logger.info("==========================postProcessAfterInitialization ..." + beanName);
		if(this.isClient){
			return assem(bean,beanName);
		} else {
			return bean;
		}
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public boolean isClient() {
		return isClient;
	}

	public void setClient(boolean isClient) {
		this.isClient = isClient;
	}

	public Map<String, String> getClientUrlMap() {
		return clientUrlMap;
	}

	public void setClientUrlMap(Map<String, String> clientUrlMap) {
		this.clientUrlMap = clientUrlMap;
	}

	public Map<String, String> getServiceUrlMap() {
		return serviceUrlMap;
	}

	public void setServiceUrlMap(Map<String, String> serviceUrlMap) {
		this.serviceUrlMap = serviceUrlMap;
	}

	public SimpleUrlHandlerMapping getSimpleUrlHandlerMapping() {
		return simpleUrlHandlerMapping;
	}

	public void setSimpleUrlHandlerMapping(
			SimpleUrlHandlerMapping simpleUrlHandlerMapping) {
		this.simpleUrlHandlerMapping = simpleUrlHandlerMapping;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = applicationContext;
	}
	
}
