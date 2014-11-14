package me.wanx.demo.httpinvoker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * httpinvoker 客户端注解
 * @author gqwang
 *
 */

@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpInvokerClient {
	/**
	 * 远程提供服务的url地址
	 * @return
	 */
	public String remoteServiceUrl() default "";
}
