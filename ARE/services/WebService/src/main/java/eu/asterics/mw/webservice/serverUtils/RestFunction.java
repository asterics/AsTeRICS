/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 * 
 * 
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.     
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 *
 *
 *                    homepage: http://www.asterics.org 
 *
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.mw.webservice.serverUtils;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is just a container that holds the information describing a restfull function.
 * 
 * It contains data like the HTTP Type of a function (GET, POST, PUT...), the resource path (storage/model/...), the parameters required etc.
 * 
 * @author Marios Komodromos (mkomod05@cs.ucy.ac.cy)
 *
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class RestFunction {

    /**
     * This annotation is used to describe the meaning of a REST function.
     * 
     * @author mad <deinhofe@technikum-wien.at>
     * @date Apr 15, 2019
     *
     */
    @Retention(RUNTIME)
    public @interface Description {
        String value() default "";
    }

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

    public RestFunction(String httpRequestType, String path, String consumes, String produces, String bodyParameter, String description) {
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
