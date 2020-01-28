package com.demo.kafka.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.stereotype.Service;

import com.demo.kafka.model.CreateTopicResponse;
import com.demo.kafka.model.SourceModel;
import com.demo.kafka.model.TopicDesc;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils; 
import java.io.File; 
import java.io.FileOutputStream; 
import java.io.OutputStream; 

@Service
public class KafkaService {

	final private String successMsg = "Replicated Successfully";

	public Boolean testEsConnection(String server, String api_key) {
		Boolean status = false;
		try (AdminClient client = AdminClient.create(addConnectionConfig(server, api_key))) {
			getTopicListing(client, true);
			client.close();
			status = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	public Boolean testConnection(String server) {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		Boolean status = false;
		Properties properties = new Properties();
		properties.put("bootstrap.servers", server);
		properties.put("client.id", UUID.randomUUID().toString());
		properties.put("request.timeout.ms", "15000");
		try (AdminClient client = AdminClient.create(properties)) {
			getTopicListing(client, true);
			client.close();
			status = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	@Deprecated
	public SourceModel getTopicDetails(Properties properties) {
		List<String> topics = new ArrayList<>();
		SourceModel sourceModel = new SourceModel();
		// Create an AdminClient using the properties initialized earlier
		try (AdminClient client = AdminClient.create(properties)) {
			Collection<TopicListing> listings = getTopicListing(client, false);
			listings.forEach(
					topic -> System.out.println("Name: " + topic.name() + ", isInternal: " + topic.isInternal()));
			topics = listings.stream().map(TopicListing::name).collect(Collectors.toList());
			sourceModel.setTopics(topics);
			sourceModel.setSourceProperties(properties);
			// createTopics(client,listings);
			client.close();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return sourceModel;
	}

	public List<TopicDesc> getTopicDesc(String server) {
		List<String> topics = new ArrayList<>();
		List<TopicDesc> topicDescList = new ArrayList<>();
		Properties properties = new Properties();
		properties.put("bootstrap.servers", server);
		properties.put("client.id", UUID.randomUUID().toString());
		properties.put("request.timeout.ms", "15000");
		// Create an AdminClient using the properties initialized earlier
		try (AdminClient client = AdminClient.create(properties)) {
			Collection<TopicListing> listings = getTopicListing(client, false);
			listings.forEach(
					topic -> System.out.println("Name: " + topic.name() + ", isInternal: " + topic.isInternal()));
			topics = listings.stream().map(TopicListing::name).collect(Collectors.toList());
			DescribeTopicsResult result = client.describeTopics(topics);
			for (String topicName : topics) {
				TopicDesc topicDesc = new TopicDesc();
				TopicDescription desc = result.values().get(topicName).get();
				topicDesc.setName(topicName);
				topicDesc.setPartitions(desc.partitions().size());
				topicDesc.setReplicas(desc.partitions().get(0).replicas().size());
				topicDescList.add(topicDesc);
			}
			client.close();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return topicDescList;
	}

	private Collection<TopicListing> getTopicListing(AdminClient client, boolean isInternal)
			throws InterruptedException, ExecutionException {
		ListTopicsOptions options = new ListTopicsOptions();
		options.listInternal(isInternal);
		return client.listTopics(options).listings().get();
	}

	@Deprecated
	public String createTopics(SourceModel source) {

		String status = "Failure";
		try {
			Collection<NewTopic> newTopics = new ArrayList<>();
			CreateTopicsOptions options = new CreateTopicsOptions();
			AdminClient srcClient = AdminClient.create(source.getSourceProperties());
			List<String> topicNames = source.getSelectedTopics();
			DescribeTopicsResult result = srcClient.describeTopics(topicNames);
			srcClient.close();
			for (String topicName : topicNames) {
				TopicDescription topicDesc = result.values().get(topicName).get();
				System.out.println(topicDesc);
				System.out.println(topicDesc.partitions().size());
				System.out.println(topicDesc.partitions().get(0).replicas().size());
				NewTopic newTopic = new NewTopic(topicName + "_copy", topicDesc.partitions().size(),
						(short) topicDesc.partitions().get(0).replicas().size());
				newTopics.add(newTopic);
			}
			;
			AdminClient tgtClient = AdminClient.create(source.getTargetProperties());
			tgtClient.createTopics(newTopics, options);
			tgtClient.close();
			status = "Success";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return status;
	}

	public List<CreateTopicResponse> createTopicsWithDesc(List<TopicDesc> topicDescList, String server,
			String api_key) {

		Collection<NewTopic> newTopics = new ArrayList<>();
		CreateTopicsOptions options = new CreateTopicsOptions();
		List<CreateTopicResponse> response = new ArrayList<>();
		AdminClient tgtClient = AdminClient.create(addConnectionConfig(server, api_key));
		try {
			for (TopicDesc topics : topicDescList) {
				String topicName = topics.getName();

				System.out.println(topicName);
				System.out.println(topics.getPartitions());
				System.out.println(topics.getReplicas());
				CreateTopicResponse createTopicResponse = new CreateTopicResponse(topicName, true, successMsg);
				NewTopic newTopic = new NewTopic(topicName, topics.getPartitions(), topics.getReplicas().shortValue());
				newTopics.add(newTopic);

				CreateTopicsResult results = tgtClient.createTopics(newTopics, options);
				KafkaFuture future = results.values().get(topics.getName());
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					createTopicResponse.setSuccess(false);
					createTopicResponse.setMessage(e.getMessage());
					e.printStackTrace();
				}

				response.add(createTopicResponse);
			}
			tgtClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	private Properties addConnectionConfig(String server, String api_key) {
		// api_key = "HPS6FlF0IGBz3LTl3thGExe46nrGOeA5qhtnIAgj8_kb";
		Resource resource = new ClassPathResource("classpath:es-cert.jks");
		InputStream inputStream = resource.getInputStream();
		try {
		    byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
		    File file = new File("/deployments/es-cert.jks"); 
	            OutputStream os  = new FileOutputStream(file); 
		    os.write(bytes);
		    os.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		Properties properties = new Properties();
		properties.put("bootstrap.servers", server);
		properties.put("client.id", UUID.randomUUID().toString());
		properties.put("request.timeout.ms", "20000");
		properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
		properties.put("ssl.truststore.password", "password");
		properties.put(SslConfigs.SSL_PROTOCOL_CONFIG, "TLSv1.2");
		properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/deployments/es-cert.jks");
		properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password");
		properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		String saslJaasConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + "admin"
				+ "\" password=" + api_key + ";";
		properties.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
		return properties;
	}

}
