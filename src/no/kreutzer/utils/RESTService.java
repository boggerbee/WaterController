package no.kreutzer.utils;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.StringEntity;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RESTService {
	private final String SERVER_URL = "http://data.kreutzer.no/dataserver";
    private static final Logger logger = LogManager.getLogger(RESTService.class);
	
	public RESTService() {
	}
	
	public String get(String url) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(SERVER_URL+"/"+url);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		HttpEntity entity;
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(1000)
				.setConnectTimeout(1000)
				.build();

		httpGet.setConfig(requestConfig);		 
		
		StringBuilder result = new StringBuilder();
		try {
			logger.info(response.getStatusLine());
			entity = response.getEntity();
			BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
			
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		return result.toString();
	}

	public void post(String url, JsonObject json) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(SERVER_URL+"/"+url);
		StringEntity entity = new StringEntity(json.toString());
		httpPost.setEntity(entity); 
		CloseableHttpResponse response = httpclient.execute(httpPost);

		try {
//			logger.info("POST status:"+response.getStatusLine());
			HttpEntity e = response.getEntity();
			EntityUtils.consume(e);
		} finally {
			response.close();
		}		
	}
	
	private class RunnableGet implements Runnable {
		private String url;
		
		public RunnableGet(String u) {
			url = u;
		}
		
		public void run() {
			try {
				String result = get(url);
//				logger.info(result);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	private class RunnablePost implements Runnable {
		private String url;
		private JsonObject json;
		
		public RunnablePost(String u, JsonObject j) {
			url = u;
			json = j;
		}
		
		public void run() {
			try {
				logger.info("Posting:"+json.toString());
				post(url,json);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			
		}
	}
	
	/**
	 * Async GET
	 * */
	public void doGet(String url) {
		(new Thread(new RunnableGet(url))).start();
	}
	
	/**
	 * Async POST
	 * */
	public void doPost(String url, JsonObject json) {
		(new Thread(new RunnablePost(url,json))).start();
	}

}
