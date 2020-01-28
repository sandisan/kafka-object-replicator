package com.demo.kafka.controller.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.kafka.model.CreateTopicResponse;
import com.demo.kafka.model.TopicDesc;
import com.demo.kafka.service.KafkaService;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class KafkaRestController {

	@Autowired
	private KafkaService kafkaService;

	@GetMapping(value = "/servers", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getServers() {
		List<String> servers = new ArrayList<>();
		servers.add("localhost:9092");
		return servers;
	}
	
	@GetMapping("/testConnection")
	public Boolean testConnection(@RequestParam("server") String server) {
		System.out.println("server : " + server);
		return kafkaService.testConnection(server);

	}
	
	@GetMapping("/es/testConnection")
	public Boolean testEsConnection(@RequestParam("server") String server,@RequestParam("api_key") String api_key) {
		System.out.println("server : " + server);
		System.out.println("##### Working Directory #### " + System.getProperty("user.dir"));
		return kafkaService.testEsConnection(server,api_key);

	}

	@GetMapping("/topics")
	public List<TopicDesc> getTopics(@RequestParam("server") String server) {
		System.out.println("server : " + server);
		return kafkaService.getTopicDesc(server);

	}

	@PostMapping("/topics/create")
	public List<CreateTopicResponse> createTopics(@RequestParam("server") String server, @RequestParam("api_key") String api_key, @RequestBody List<TopicDesc> topicDescList) {
		return kafkaService.createTopicsWithDesc(topicDescList, server, api_key);
	}

}
