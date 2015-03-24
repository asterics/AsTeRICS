package eu.asterics.mw.webservice.serverUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class RestFunction {
	private String httpRequestType;
	private String path;
	private String consumes;
	private String produces;
	private String bodyParameter;
	private String description;
	
	
	public RestFunction() {
		this.httpRequestType = "";
		this.path = "";
		this.consumes = "";
		this.produces = "";
		this.bodyParameter = "";
		this.description = "";
	}
	
	public RestFunction(String httpRequestType, String path, String consumes,
			String produces, String bodyParameter, String description) {
		this.httpRequestType = httpRequestType;
		this.path = path;
		this.consumes = consumes;
		this.produces = produces;
		this.bodyParameter = bodyParameter;
		this.description = description;
	}


	public String getHttpRequestType() {
		return httpRequestType;
	}
	
	public void setHttpRequestType(String httpRequestType) {
		this.httpRequestType = httpRequestType;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getConsumes() {
		return consumes;
	}
	
	public void setConsumes(String consumes) {
		this.consumes = consumes;
	}
	
	public String getProduces() {
		return produces;
	}
	
	public void setProduces(String produces) {
		this.produces = produces;
	}
	
	public String getBodyParameter() {
		return bodyParameter;
	}
	
	public void setBodyParameter(String bodyParameter) {
		this.bodyParameter = bodyParameter;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
