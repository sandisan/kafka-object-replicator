package com.demo.kafka.model;

public class CreateTopicResponse {

	private String topicName;
	private Boolean success;
	private String message;
	
	
	public CreateTopicResponse(String topicName, Boolean success, String message) {
		super();
		this.topicName = topicName;
		this.success = success;
		this.message = message;
	}


	public String getTopicName() {
		return topicName;
	}


	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}


	public Boolean getSuccess() {
		return success;
	}


	public void setSuccess(Boolean success) {
		this.success = success;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
	
}
