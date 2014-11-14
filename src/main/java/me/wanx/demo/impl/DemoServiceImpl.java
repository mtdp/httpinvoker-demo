package me.wanx.demo.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import me.wanx.demo.DemoService;
import me.wanx.demo.httpinvoker.HttpInvokerService;

@Service("demoServiceImpl")
@HttpInvokerService(serviceUrl="demo.demoService",serviceInterfaceClass=DemoService.class)
public class DemoServiceImpl implements DemoService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void sayHeoll() {
		logger.info("=============================");
		logger.info("DemoServiceImpl sayHello!");
	}

}
