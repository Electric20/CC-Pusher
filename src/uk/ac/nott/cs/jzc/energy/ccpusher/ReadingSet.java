package uk.ac.nott.cs.jzc.energy.ccpusher;

import java.io.FileNotFoundException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReadingSet {
	
	private String user;
	private String apiKey;
	private long timeStamp;
	private int hubId;
	private ArrayList<Reading> readings;
	
	public ReadingSet (String user, String apiKey, int hubId, long timeStamp, ArrayList<Reading> readings) 
	{
		this.user = user;
		this.apiKey = apiKey;
		this.hubId = hubId; 
		this.readings = readings;
	}
	
	private  SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public JSONObject getJSON () throws FileNotFoundException 
	{
		JSONObject pushJson = new JSONObject();
		try 
		{
			JSONObject json = new JSONObject();
			json.put("user", user);
			json.put("apiKey", apiKey);
			json.put("timeStamp", dateFormatter.format(timeStamp));
			json.put("hubId", hubId);
			json.put("readings", getReadingsJSON());
			pushJson.put("readingSet",json);
			System.out.println(pushJson.toString());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return pushJson;
	}


	
	public JSONArray getReadingsJSON () throws FileNotFoundException 
	{
		JSONArray readings = new JSONArray();
		
		for (Reading r : this.readings) 
		{
			JSONObject j = new JSONObject();
			try 
			{
				j.put("sensorId", r.getSensorId());
				j.put("load", r.getValue());
			} catch (JSONException e) 
			{
				e.printStackTrace();
			}
			readings.put(j);
		}
		
		return readings;
	}
	
	public void upload() throws FileNotFoundException
	{
		final JSONObject json = this.getJSON();
		new Thread(new Runnable()
		{
			public void run()
			{
				System.out.println("starting upload");
				List<NameValuePair> qparams = new ArrayList<NameValuePair>();
				qparams.add(new BasicNameValuePair("json", json.toString()));
				URI uri;
				try
				{
					uri = URIUtils.createURI("http", "electric20.com",- 1, "/dataStore/push.php",URLEncodedUtils.format(qparams, "UTF-8"),null);
					HttpGet httpGet = new HttpGet(uri);
					HttpClient httpClient = new DefaultHttpClient();
					HttpHost proxy = new HttpHost("mainproxy.nottingham.ac.uk",8080); // comment out for non proxy version
					httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy); // comment out for non proxy version
					httpClient.execute(httpGet);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					
				}
			}
		}).start();
	}

}
