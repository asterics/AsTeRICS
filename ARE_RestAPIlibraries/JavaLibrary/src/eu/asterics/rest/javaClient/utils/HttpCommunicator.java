package eu.asterics.rest.javaClient.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * This class targets to simplify the HTTP communication through the network. It provides
 * a set of methods that hide the unnecessary and repeating code of the standard HTTP
 * service calls.
 * 
 * @author Marios Komodromos
 *			email: mkomod05@cs.ucy.ac.cy
 */
public class HttpCommunicator {
	private String baseUrl;
	
	//dataTypes
	public static final String DATATYPE_TEXT_PLAIN  = "text/plain";
	public static final String DATATYPE_TEXT_XML = "text/xml";
	public static final String DATATYPE_TEXT_HTML = "text/html";
	public static final String DATATYPE_APPLICATION_JSON = "application/json";
	public static final String DATATYPE_APPLICATION_XML = "application/xml";
	public static final String DATATYPE_APPLICATION_XHTML_XML = "application/xhtml+xml";
	
	//requestTypes
	public static final String REQTYPE_GET  = "GET";
	public static final String REQTYPE_POST = "POST";
	public static final String REQTYPE_PUT = "PUT";
	public static final String REQTYPE_DELETE = "DELETE";
	
	
	public HttpCommunicator() {
		baseUrl = null;
	}
	
	public HttpCommunicator(String baseUrl) {
		setBaseUrl(baseUrl);
	}


	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		if (baseUrl == null) {
			return;
		}
		
		if (baseUrl.endsWith("/")) {
			baseUrl = baseUrl.substring(0, baseUrl.length()-1);
		}
		this.baseUrl = baseUrl;
	}


	
	/**
	 * Method that reads the response from a {@link BufferedReader} object.
	 * 
	 * @param reader - A {@link BufferedReader} object to extract the information from
	 * 
	 * @return - The response that HTTP service provider will return as a {@link String}
	 * @throws IOException 
	 */
	protected String readResponse(BufferedReader reader) throws IOException {
		String str;
		StringBuffer sb = new StringBuffer();
		
		while ((str = reader.readLine()) != null) {
			sb.append(str);
			sb.append("\n");
		}
		reader.close();
		
		String response = sb.toString();
		return response;
	}
	
	/**
	 * Converts the given Map to a URL-parameter string.
	 * 
	 * URL-parameters structure (key1=value&key2=value)
	 * 
	 * @param paramsMap
	 * @return
	 */
	protected String mapToParam(Map<String,String> paramsMap) {
		String params = "";
		if (paramsMap != null) {
			for (String key: paramsMap.keySet()) {
				if ( params!="" ) {
					params += "&";
				}
				String value = paramsMap.get(key);
				params += key+"="+value;
			}
		}
		
		return params;
	}
	
	
	
	/**
	 * Method that sends a GET request with no parameters.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * @param dataTypeOfResponse - The data type of the response that will be returned from the HTTP service provider.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * 
	 * @return - The response that HTTP service provider will return as a {@link String}
	 * @throws Exception 
	 */
	public HttpResponse getRequest(String extraUrl, String dataTypeOfResponse) throws Exception {
		return getRequest(extraUrl, null, null, dataTypeOfResponse);
	}
	
	/**
	 * Method that sends a GET request. Url-parameters and request-headers can be passed.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * In the case where this string already contains url parameters, the paramsMap will not be used.
	 * @param dataTypeOfResponse - The data type of the response that will be returned from the HTTP service provider.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param paramsMap - A {@link Map} that should contain the parameters that will be passed
	 * in the url of the request.
	 * @param headersMap - A {@link Map} that should contain the parameters that will be passed
	 * in the header of the request. 
	 * 
	 * @return - The response that HTTP service provider will return as a {@link String}
	 * @throws Exception 
	 */
	public HttpResponse getRequest(String extraUrl, Map<String,String> paramsMap, Map<String,String> headersMap, String dataTypeOfResponse) throws Exception {
		URL url = null;
		BufferedReader reader = null;
		HttpResponse httpResponse = new HttpResponse();
		
		String params = mapToParam(paramsMap);
		
		// Construct the final URL address
		String finalUrl = baseUrl;
		if ( extraUrl != "" ) {
			finalUrl += extraUrl;
		}
		if ( !finalUrl.contains("?") && !params.equals("") ) {
			finalUrl += "?" + params;
		}

		try {
			url = new URL(finalUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod(HttpCommunicator.REQTYPE_GET);
            httpConnection.setRequestProperty("Accept", dataTypeOfResponse);
            
            if (headersMap != null) {
	            for (String header: headersMap.keySet()) {
	            	//this method will not override an entry if it already exists
	            	httpConnection.addRequestProperty(header, headersMap.get(header));
	            }
            }
            
            //getInputStream() method calls the connect() method internally
			reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			String responseText = readResponse(reader);
			
			//build the HttpResponse object
			httpResponse.setStatusCode(httpConnection.getResponseCode());
			httpResponse.setStatusMessage(httpConnection.getResponseMessage());
			httpResponse.setBody(responseText);
			httpResponse.setHeaders(httpConnection.getHeaderFields());
	
			reader.close();
			return httpResponse;
		} catch (Exception e) {
			try { 
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			throw new Exception("",e);
		}
	}
	
	
	

	
	
	/**
	 * Method that sends a POST request to the HTTP service provider without any data in the body.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * @param dataTypeOfResponse - The data type of the response that will be returned from the HTTP service provider.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * 
	 * @return - The response that HTTP service provider will return as a {@link String}
	 * @throws Exception 
	 */
	public HttpResponse postRequest(String extraUrl, String dataTypeOfResponse) throws Exception {
		return this.postRequest(extraUrl, null, null, "", dataTypeOfResponse, "");
	}
	
	
	/**
	 * Method that sends a POST request to the HTTP service provider without receiving a response,
	 * but with data in the body.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * @param dataTypeOfRequest - The type of data contained in the request
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param dataOfRequest - The actual data of the request.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public HttpResponse postRequest(String extraUrl, String dataTypeOfRequest, String dataOfRequest) throws Exception {
		return this.postRequest(extraUrl, null, null, dataTypeOfRequest, "", dataOfRequest);
	}
	
	
	/**
	 * Method that sends a POST request to the HTTP service provider, with data in the body and expecting a response.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * @param dataTypeOfRequest - The data type contained in the body of the request.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param dataOfRequest - The actual data of the request.
	 * @param dataTypeOfResponse - The data type of the response that will be returned from the HTTP service provider.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public HttpResponse postRequest(String extraUrl, String dataTypeOfRequest, String dataTypeOfResponse, String dataOfRequest) throws Exception {
		return this.postRequest(extraUrl, null, null, dataTypeOfRequest, dataTypeOfResponse, dataOfRequest);
	}
	
	
	/**
	 * Method that sends a POST request to the HTTP service provider with data in the body 
	 * and expecting to receive a response. Url-parameters and request-headers can be passed.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * In the case where this string already contains url parameters, the paramsMap will not be used.
	 * @param paramsMap - A {@link Map} that should contain the parameters that will be passed
	 * in the url of the request.
	 * @param headersMap - A {@link Map} that should contain the parameters that will be passed
	 * in the header of the request. 
	 * @param dataTypeOfRequest - The data type contained in the body of the request.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param dataTypeOfResponse - The data type of the response that will be returned from the HTTP service provider.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param dataOfRequest - The actual data of the request.
	 * 
	 * @return - The response that HTTP service provider will return as a {@link String}
	 * @throws Exception 
	 */
	public HttpResponse postRequest(String extraUrl, Map<String,String> paramsMap, Map<String,String> headersMap, String dataTypeOfRequest,
			String dataTypeOfResponse, String dataOfRequest) throws Exception {
		URL url = null;
		BufferedReader reader = null;
		HttpResponse httpResponse = new HttpResponse();
		
		String params = mapToParam(paramsMap);
		
		// Construct the final URL address
		String finalUrl = baseUrl;
		if ( extraUrl != "" ) {
			finalUrl += extraUrl;
		}
		if (params != "") {
			finalUrl += "?" + params;
		}

		
		try {
			url = new URL(finalUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            
            //set the parameters
            httpConnection.setRequestMethod(HttpCommunicator.REQTYPE_POST);
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            
            if ( (dataTypeOfRequest != null) && (dataTypeOfRequest.length() > 0) ) {
            	httpConnection.setRequestProperty("Content-Type", dataTypeOfRequest);
            }
            
            if ( (dataTypeOfResponse != null) && (dataTypeOfResponse.length() > 0) ) {
            	httpConnection.setRequestProperty("Accept", dataTypeOfResponse);
            }
            
            if (headersMap != null) {
	            for (String header: headersMap.keySet()) {
	            	httpConnection.addRequestProperty(header, headersMap.get(header));
	            }
            }
            
            OutputStream outputStream = null;
            if (dataTypeOfRequest.length() > 0) {
	            outputStream = httpConnection.getOutputStream();
	            outputStream.write(dataOfRequest.getBytes());
	            outputStream.flush();
            }
            
			reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			String responseText = readResponse(reader);
			
			//build the HttpResponse object
			httpResponse.setStatusCode(httpConnection.getResponseCode());
			httpResponse.setStatusMessage(httpConnection.getResponseMessage());
			httpResponse.setBody(responseText);
			httpResponse.setHeaders(httpConnection.getHeaderFields());
	
			if (outputStream != null) {
				outputStream.close();
			}
			reader.close();
			return httpResponse;
		} catch (Exception e) {
			try { 
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			throw new Exception("",e);
		}
	}
	
	
	

	
	
	/**
	 * Method that sends a PUT request to the HTTP service provider without any data in the body.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * @param dataTypeOfResponse - The data type of the response that will be returned from the HTTP service provider.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * 
	 * @return - The response that HTTP service provider will return as a {@link String}
	 * @throws Exception 
	 */
	public HttpResponse putRequest(String extraUrl, String dataTypeOfResponse) throws Exception {
		return this.putRequest(extraUrl, null, null, "", dataTypeOfResponse, "");
	}
	

	/**
	 * Method that sends a PUT request to the HTTP service provider without receiving a response,
	 * but with data in the body.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * @param dataTypeOfRequest - The data type contained in the body of the request.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param dataOfRequest - The actual data of the request.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public HttpResponse putRequest(String extraUrl, String dataTypeOfRequest, String dataOfRequest) throws Exception {
		return this.putRequest(extraUrl, null, null, dataTypeOfRequest, "", dataOfRequest);
	}
	
	
	/**
	 * Method that sends a PUT request to the HTTP service provider, with data in the body and expecting a response.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * @param dataTypeOfRequest - The data type contained in the body of the request.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param dataOfRequest - The actual data of the request.
	 * @param dataTypeOfResponse - The data type of the response that will be returned from the HTTP service provider.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public HttpResponse putRequest(String extraUrl, String dataTypeOfRequest, String dataTypeOfResponse, String dataOfRequest) throws Exception {
		return this.putRequest(extraUrl, null, null, dataTypeOfRequest, dataTypeOfResponse, dataOfRequest);
	}
	
	
	/**
	 * Method that sends a PUT request to the HTTP service provider with data in the body 
	 * and expecting to receive a response. Url-parameters and request-headers can be passed.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * In the case where this string already contains url parameters, the paramsMap will not be used.
	 * @param paramsMap - A {@link Map} that should contain the parameters that will be passed
	 * in the url of the request.
	 * @param headersMap - A {@link Map} that should contain the parameters that will be passed
	 * in the header of the request. 
	 * @param dataTypeOfRequest - The data type contained in the body of the request.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param dataTypeOfResponse - The data type of the response that will be returned from the HTTP service provider.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param dataOfRequest - The actual data of the request.
	 * 
	 * @return - The response that HTTP service provider will return as a {@link String}
	 * @throws Exception 
	 */
	public HttpResponse putRequest(String extraUrl, Map<String,String> paramsMap, Map<String,String> headersMap,
			String dataTypeOfRequest, String dataTypeOfResponse, String dataOfRequest) throws Exception {
		URL url = null;
		BufferedReader reader = null;
		HttpResponse httpResponse = new HttpResponse();
		
		String params = mapToParam(paramsMap);
		
		// Construct the final URL address
		String finalUrl = baseUrl;
		if ( extraUrl != "" ) {
			finalUrl += extraUrl;
		}
		if (params != "") {
			finalUrl += "?" + params;
		}
		
		try {
			url = new URL(finalUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            
            //set the parameters
            httpConnection.setRequestMethod(HttpCommunicator.REQTYPE_PUT);
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            
            if ( (dataTypeOfRequest != null) && (dataTypeOfRequest.length() > 0) ) {
            	httpConnection.setRequestProperty("Content-Type", dataTypeOfRequest);
            }
            if ( (dataTypeOfResponse != null) && (dataTypeOfResponse.length() > 0) ) {
            	httpConnection.setRequestProperty("Accept", dataTypeOfResponse);
            }
            
            if (headersMap != null) {
	            for (String header: headersMap.keySet()) {
	            	httpConnection.addRequestProperty(header, headersMap.get(header));
	            }
            }
            
            
            //enter the input data to the request
            OutputStream outputStream = null;
            if (dataTypeOfRequest.length() > 0) {
	            outputStream = httpConnection.getOutputStream();
	            outputStream.write(dataOfRequest.getBytes());
	            outputStream.flush();
            }
            
			reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			String responseText = readResponse(reader);
			
			//build the HttpResponse object
			httpResponse.setStatusCode(httpConnection.getResponseCode());
			httpResponse.setStatusMessage(httpConnection.getResponseMessage());
			httpResponse.setBody(responseText);
			httpResponse.setHeaders(httpConnection.getHeaderFields());
	
			if (outputStream != null) {
				outputStream.close();
			}
			reader.close();
			return httpResponse;
		} catch (Exception e) {
			try { 
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			throw new Exception("",e);
		}
	}
	
	
	
	
	
	
	/**
	 * Method that sends a DELETE request to the HTTP service provider without any data in the body.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * @param dataTypeOfResponse - The data type of the response that will be returned from the HTTP service provider.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * 
	 * @return - The response that HTTP service provider will return as a {@link String}
	 * @throws Exception 
	 */
	public HttpResponse deleteRequest(String extraUrl,String dataTypeOfResponse) throws Exception {
		return deleteRequest(extraUrl, null, null, "", dataTypeOfResponse, "");
	}
	
	
	/**
	 * Method that sends a DELETE request to the HTTP service provider without receiving a response,
	 * but with data in the body.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * @param dataTypeOfRequest - The data type contained in the body of the request.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param dataOfRequest - The actual data of the request.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public HttpResponse deleteRequest(String extraUrl, String dataTypeOfRequest, String dataOfRequest) throws Exception {
		return this.deleteRequest(extraUrl, null, null, dataTypeOfRequest, "", dataOfRequest);
	}
	
	
	/**
	 * Method that sends a DELETE request to the HTTP service provider, with data in the body and expecting a response.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * @param dataTypeOfRequest - The data type contained in the body of the request.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param dataOfRequest - The actual data of the request.
	 * @param dataTypeOfResponse - The data type of the response that will be returned from the HTTP service provider.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public HttpResponse deleteRequest(String extraUrl, String dataTypeOfRequest, String dataOfRequest, String dataTypeOfResponse) throws Exception {
		return this.deleteRequest(extraUrl, null, null, dataTypeOfRequest, dataTypeOfResponse, dataOfRequest);
	}
	
	
	/**
	 * Method that sends a DELETE request to the HTTP service provider with data in the body 
	 * and expecting to receive a response. Url-parameters and request-headers can be passed.
	 * 
	 * @param extraUrl - The extra URL that will be added on the base URL for the specific request.
	 * In the case where this string already contains url parameters, the paramsMap will not be used.
	 * @param paramsMap - A {@link Map} that should contain the parameters that will be passed
	 * in the url of the request.
	 * @param headersMap - A {@link Map} that should contain the parameters that will be passed
	 * in the header of the request. 
	 * @param dataTypeOfRequest - The data type contained in the body of the request.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param dataTypeOfResponse - The data type of the response that will be returned from the HTTP service provider.
	 * The basic data types are available as static string variables in the {@link HttpCommunicator} class.
	 * @param dataOfRequest - The actual data of the request.
	 * 
	 * @return - The response that HTTP service provider will return as a {@link String}
	 * @throws Exception 
	 */
	public HttpResponse deleteRequest(String extraUrl, Map<String,String> paramsMap, Map<String,String> headersMap, 
			String dataTypeOfRequest, String dataTypeOfResponse, String dataOfRequest) throws Exception {
		URL url = null;
		BufferedReader reader = null;
		HttpResponse httpResponse = new HttpResponse();
		
		String params = mapToParam(paramsMap);
		
		// Construct the final URL address
		String finalUrl = baseUrl;
		if ( extraUrl != "" ) {
			finalUrl += extraUrl;
		}
		if (params != "") {
			finalUrl += "?" + params;
		}
		
		try {
			url = new URL(finalUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            
            //set the parameters
            httpConnection.setRequestMethod(HttpCommunicator.REQTYPE_DELETE);
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            
            if ( (dataTypeOfRequest != null) && (dataTypeOfRequest.length() > 0) ) {
            	httpConnection.setRequestProperty("Content-Type", dataTypeOfRequest);
            }
            if ( (dataTypeOfResponse != null) && (dataTypeOfResponse.length() > 0) ) {
            	httpConnection.setRequestProperty("Accept", dataTypeOfResponse);
            }
            
            if (headersMap != null) {
	            for (String header: headersMap.keySet()) {
	            	httpConnection.addRequestProperty(header, headersMap.get(header));
	            }
            }
            
            //enter the input data to the request
            OutputStream outputStream = null;
            if (dataTypeOfRequest.length() > 0) {
	            outputStream = httpConnection.getOutputStream();
	            outputStream.write(dataOfRequest.getBytes());
	            outputStream.flush();
            }
            
			reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			String responseText = readResponse(reader);
			
			//build the HttpResponse object
			httpResponse.setStatusCode(httpConnection.getResponseCode());
			httpResponse.setStatusMessage(httpConnection.getResponseMessage());
			httpResponse.setBody(responseText);
			httpResponse.setHeaders(httpConnection.getHeaderFields());
	
			if (outputStream != null) {
				outputStream.close();
			}
			reader.close();
			return httpResponse;
		} catch (Exception e) {
			try { 
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			throw new Exception("",e);
		}
	}

	
}
