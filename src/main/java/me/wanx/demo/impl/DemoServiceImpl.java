package me.wanx.demo.impl;

import me.wanx.demo.DemoService;
import me.wanx.demo.httpinvoker.HttpInvokerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("demoServiceImpl")
@HttpInvokerService(serviceUrl="demo.demoService",serviceInterfaceClass=DemoService.class)
public class DemoServiceImpl implements DemoService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void sayHeoll() throws Exception {
		logger.info("=============================");
		logger.info("DemoServiceImpl sayHello!");
		if(true){
			//验证异常 action是否可以捕获
			throw new Exception("sayHeoll exception");
		}
	}

}
