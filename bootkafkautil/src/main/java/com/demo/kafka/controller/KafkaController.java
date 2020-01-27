package com.demo.kafka.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.demo.kafka.model.SourceModel;
import com.demo.kafka.service.KafkaService;

@RestController
public class KafkaController {
	
	@Autowired
	private KafkaService kafkaService;
	
	@GetMapping(value = "/servers")
	public ModelAndView renderMessages() {
		ModelAndView modelAndView = new ModelAndView();
		List<String> servers = new ArrayList<>();
		servers.add("localhost:9092");
		modelAndView.addObject("servers", servers);
        modelAndView.setViewName("servers");
		return modelAndView;
	}
	
	@PostMapping("/topics")
	public ModelAndView getTopics(@RequestParam("server") String server){
		System.out.println("server : "+server);
		Properties properties = new Properties();
		properties.put("bootstrap.servers", server);
		properties.put("client.id", "java-admin-client-1");
		SourceModel sourceModel = kafkaService.getTopicDetails(properties);
		System.out.println("Topics : "+sourceModel.getTopics());
		ModelAndView modelAndView = new ModelAndView();
		//modelAndView.addObject("topics", sourceModel.getTopics());
		List<String> servers = new ArrayList<>();
		servers.add("localhost:9092");
		//modelAndView.addObject("servers", servers);
		sourceModel.setServers(servers);
		sourceModel.setSourceServer(server);
		modelAndView.addObject("sourcemodel", sourceModel);
        modelAndView.setViewName("topics");
		return modelAndView;
				
	}
	
	@PostMapping("/topics/create")
	public ModelAndView createTopics(@ModelAttribute(value="sourcemodel") SourceModel source){
		Properties properties = new Properties();
		properties.put("bootstrap.servers", source.getTargetServer());
		properties.put("client.id", UUID.randomUUID().toString());
		source.setTargetProperties(properties);
		Properties srcProperties = new Properties();
		srcProperties.put("bootstrap.servers", source.getSourceServer());
		srcProperties.put("client.id", UUID.randomUUID().toString());
		source.setSourceProperties(srcProperties);
		System.out.println("source : " +source.getSelectedTopics());
		System.out.println("source : " +source.getTargetServer());
		System.out.println("source serv: " +source.getSourceServer());
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("result", kafkaService.createTopics(source));
		modelAndView.setViewName("results");
		//return kafkaService.createTopics(source);
		return modelAndView;
	}

}
