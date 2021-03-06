package com.example.demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ComputeController {

	private final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private DiscoveryClient client;
	
	@Autowired
	private Registration registration;
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public Integer add(@RequestParam Integer a, @RequestParam Integer b) {
		ServiceInstance instance = client.getInstances(registration.getServiceId()).get(0);
		
		Integer r = a + b;
		
		logger.info("/add, host: " + instance.getHost() + ", Service id: " + instance.getServiceId() + ", result: " + r);
		
		return r;
	}

	@RequestMapping(value = "/testHystrix", method = RequestMethod.GET)
	@HystrixCommand(fallbackMethod = "hiError")
	public String testHystrix(String name) throws Exception {
		if (StringUtils.isEmpty(name)) {
			throw new Exception("blank name");
		}
		return "success" + name;
	}

	public String hiError(String name) {
		return "hi,"+name+",sorry,error!";
	}
}
