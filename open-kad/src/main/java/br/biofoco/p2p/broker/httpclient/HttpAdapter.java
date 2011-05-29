package br.biofoco.p2p.broker.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpAdapter {

	public String doGet(String url) throws ClientProtocolException, IOException {
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		
		HttpEntity entity = response.getEntity();
		StringBuilder sb = new StringBuilder();
		
		if (entity != null) {
			InputStream instream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		}

		return sb.toString(); 
	}

	public void doPost(String url, Map<String,String> paramMap) throws ClientProtocolException, IOException {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(paramMap.size());
			
			for (Entry<String,String> e: paramMap.entrySet()){
				nameValuePairs.add(new BasicNameValuePair(e.getKey(), e.getValue()));
			}
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
 
			HttpResponse response = client.execute(post);
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				System.out.println(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
