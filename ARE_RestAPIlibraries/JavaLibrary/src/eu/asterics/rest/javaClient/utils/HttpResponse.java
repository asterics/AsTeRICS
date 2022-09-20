package eu.asterics.rest.javaClient.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marios Komodromos
 *			email: mkomod05@cs.ucy.ac.cy
 *
 */
public class HttpResponse {
	private int statusCode;
	private String statusMessage;
	private String protocol;
	private String body;
	private Map<String, List<String>> headers;
	
	public HttpResponse(int statusCode, String statusMessage, String protocol,
			String body, Map<String, List<String>> headers) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.protocol = protocol;
		this.body = body;
		this.headers = headers;
	}
	
	public HttpResponse() {
		this.statusCode = 0;
		this.statusMessage = "";
		this.protocol = "";
		this.body = "";
		this.headers = new HashMap<String, List<String>>();
	}
	

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}
	
	
	@Override
	public String toString() {
		return "HttpResponse [statusCode=" + statusCode + ", statusMessage="
				+ statusMessage + ", protocol=" + protocol
				+ ", \nbody=" + body
				+ ", \nheaders=" + headers + "]";
	}
	
	
}
