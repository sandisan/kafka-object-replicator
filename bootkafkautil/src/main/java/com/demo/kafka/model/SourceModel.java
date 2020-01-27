package com.demo.kafka.model;

import java.util.List;
import java.util.Properties;

public class SourceModel {
	
	private List<String> topics;
	private List<String> selectedTopics;
	private Properties sourceProperties;
	private Properties targetProperties;
	private List<String> servers;
	private String targetServer;
	private String sourceServer;
	
	public SourceModel() {
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	public Properties getSourceProperties() {
		return sourceProperties;
	}

	public void setSourceProperties(Properties sourceProperties) {
		this.sourceProperties = sourceProperties;
	}

	public Properties getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Properties targetProperties) {
		this.targetProperties = targetProperties;
	}

	public List<String> getServers() {
		return servers;
	}

	public void setServers(List<String> servers) {
		this.servers = servers;
	}

	public List<String> getSelectedTopics() {
		return selectedTopics;
	}

	public void setSelectedTopics(List<String> selectedTopics) {
		this.selectedTopics = selectedTopics;
	}

	public String getTargetServer() {
		return targetServer;
	}

	public void setTargetServer(String targetServer) {
		this.targetServer = targetServer;
	}

	public String getSourceServer() {
		return sourceServer;
	}

	public void setSourceServer(String sourceServer) {
		this.sourceServer = sourceServer;
	}

	


}
