package com.gingos.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Response {

	public static final String OK = "OK";
	public static final String ERROR = "ERROR";

	private String status;
	private Object data;

	public void setStatus(String status) {
		this.status = status;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Response() {
	}

	public String Build() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}

	public String BuildException(String error) {
		return String.format("{\"status\":\"%s\", \"data\": \"%s\" }", ERROR, error);
	}

	public String getStatus() {
		return this.status;
	}

	public Object getData() {
		return this.data;
	}
}
