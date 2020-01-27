package com.demo.kafka.model;

public class TopicDesc {
	
	private String name;
	private Integer partitions;
	private Integer replicas;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getPartitions() {
		return partitions;
	}
	public void setPartitions(Integer partitions) {
		this.partitions = partitions;
	}
	public Integer getReplicas() {
		return replicas;
	}
	public void setReplicas(Integer i) {
		this.replicas = i;
	}
	
	

}
