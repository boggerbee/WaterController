package no.kreutzer.water;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class RESTService {
	private final String SERVER_URL = "http://192.168.10.123:8080";
    private static final Logger logger = LogManager.getLogger(RESTService.class);
	
	public RESTService() {
	}
	
	public HttpEntity get(String url) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(SERVER_URL+"/"+url);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		HttpEntity entity;
		
		try {
			logger.info(response.getStatusLine());
			entity = response.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		return entity;
	}
/*	
	public void post(TankPOJO pojo) {
		HttpPost httpPost = new HttpPost("http://targethost/login");
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("username", "vip"));
		nvps.add(new BasicNameValuePair("password", "secret"));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response2 = httpclient.execute(httpPost);

		try {
			System.out.println(response2.getStatusLine());
			HttpEntity entity2 = response2.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			EntityUtils.consume(entity2);
		} finally {
			response2.close();
		}		
	}
	*/ 
}
