package me.wanx.demo.httpinvoker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * httpinvoker 服务端注解
 * @author gqwang
 *
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpInvokerService {
	/**
	 * 提供服务的url
	 * @return
	 */
	public String serviceUrl() default "";
	
	/**
	 * 提供服务的接口
	 * @return
	 */
	public Class<?>[] serviceInterfaceClass();
}
