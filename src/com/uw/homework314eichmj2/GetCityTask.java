package com.uw.homework314eichmj2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonArray;

import android.app.Activity;
import android.os.AsyncTask;

public class GetCityTask extends AsyncTask<String, Void, JSONArray> {

	 String endPoint = "http://autocomplete.wunderground.com/aq?query=";//GET http://autocomplete.wunderground.com/aq?query=San%20F
	 JSONArray mCities;
	@Override
	protected JSONArray doInBackground(String... params)  {
		
				HttpResponse response = null;
		       
		        HttpClient client = new DefaultHttpClient();
		        
		        HttpGet request = new HttpGet();
		        
		        try {
					request.setURI(new URI(endPoint+params[0]));
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        try {
					response = client.execute(request);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        try {
		        	InputStream stream = response.getEntity().getContent();
		        	JSONObject mObject = new JSONObject(convertStreamToString(stream));
		        	mCities = mObject.getJSONArray( "RESULTS");
		        	
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        
		return mCities;
	}

	 @Override
	protected void onPostExecute(JSONArray result) {
		 
		
		 if (mCities.length() > 0) {
			 
			BusProvider.getInstance().post(new CityResults(mCities));
			
		}
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	public static String convertStreamToString(InputStream is)
	 {         /*          * To convert the InputStream to String we use the BufferedReader.readLine()
	           * method. We iterate until the BufferedReader return null which means
	           *           * there's no more data to read. Each line will appended to a StringBuilder
	           *                     * and returned as String.          */
		 BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		 StringBuilder sb = new StringBuilder();
		 String line = null;         
		 try {             
			 while ((line = reader.readLine()) != null)
			 {                 sb.append(line + "\n");
			 }         } catch (IOException e) 
			 {             e.printStackTrace();         }
		 finally {             
			 try {                 
				 is.close();            
				 } catch (IOException e) 
				 {                 
					 e.printStackTrace();
					 }
			 }         
		 return sb.toString();
		 }

}
