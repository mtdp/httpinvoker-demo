package me.wanx.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import me.wanx.demo.httpinvoker.HttpInvokerClient;

@Service("clientTest")
public class ClientTest {
	private static Logger logger = LoggerFactory.getLogger(ClientTest.class);
	
	private DemoService demoService;
	
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-demo.xml");
		//HttpInvokerPostProcessor post = (HttpInvokerPostProcessor)ctx.getBean("httpInvokerPostProcessorService");
		ClientTest clientTest = (ClientTest)ctx.getBean("clientTest");
		logger.info("========================" + clientTest.demoService);
		try {
			clientTest.demoService.sayHeoll();
		} catch (Exception e) {
			logger.error("catch demoService exception",e);
		}
	}
	
	@HttpInvokerClient(remoteServiceUrl="demo.demoService")
	public void setDemoService(DemoService demoService){
		this.demoService = demoService;
	}

}
