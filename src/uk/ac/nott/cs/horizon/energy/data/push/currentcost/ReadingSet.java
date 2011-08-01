package uk.ac.nott.cs.horizon.energy.data.push.currentcost;

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
		setUser(user);
		setApiKey(apiKey);
		setHubId(hubId);
		setReadings(readings);
		setTimeStamp(timeStamp);
	}
	
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public JSONObject getJSON () 
	{
		JSONObject pushJson = new JSONObject();
		try 
		{
			JSONObject json = new JSONObject();
			json.put("user", getUser());
			json.put("apiKey", getApiKey());
			json.put("timeStamp", dateFormatter.format(getTimeStamp()));
			json.put("hubId", getHubId());
			json.put("readings", getReadingsJSON());
			pushJson.put("readingSet",json);
			System.out.println(pushJson.toString());
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return pushJson;
	}

	public void setUser(String user) 
	{
		this.user = user;
	}

	public String getUser() 
	{
		return user;
	}

	public void setApiKey(String apiKey) 
	{
		this.apiKey = apiKey;
	}

	public String getApiKey() 
	{
		return apiKey;
	}

	public void setTimeStamp(long timeStamp) 
	{
		this.timeStamp = timeStamp;
	}

	public long getTimeStamp() 
	{
		return timeStamp;
	}

	public void setHubId(int hubId) 
	{
		this.hubId = hubId;
	}

	public int getHubId() 
	{
		return hubId;
	}

	public void setReadings(ArrayList<Reading> readings) 
	{
		this.readings = readings;
	}

	public ArrayList<Reading> getReadings() 
	{
		return readings;
	}
	
	public JSONArray getReadingsJSON () 
	{
		JSONArray readings = new JSONArray();
		
		for (Reading r : getReadings()) 
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
	
	public void upload()
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
					uri = URIUtils.createURI("http", "79.125.20.47",- 1, "/dataStore/push.php",URLEncodedUtils.format(qparams, "UTF-8"),null);
					HttpGet httpGet = new HttpGet(uri);
					HttpClient httpClient = new DefaultHttpClient();
					//HttpHost proxy = new HttpHost("mainproxy.nottingham.ac.uk",8080);
					//httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
					httpClient.execute(httpGet);
					System.out.println(httpGet.getURI());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}

}
