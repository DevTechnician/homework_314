package com.uw.homework314eichmj2;

import it.restrung.rest.cache.RequestCache;
import it.restrung.rest.client.ContextAwareAPIDelegate;

import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.android.wunderground.api.service.WundergroundApiProvider;
import com.fortysevendeg.android.wunderground.api.service.request.Feature;
import com.fortysevendeg.android.wunderground.api.service.request.Query;
import com.fortysevendeg.android.wunderground.api.service.response.ForecastDayResponse;
import com.fortysevendeg.android.wunderground.api.service.response.WundergroundResponse;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.squareup.otto.Subscribe;

public class WeatherActivity extends Activity implements
		SearchView.OnQueryTextListener, SearchView.OnCloseListener {

	TextView currentTemp;
	TextView currentCondition;
	TextView currentCity;
	ImageView weatherIcon;
	ListView forecastList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);

		currentTemp = (TextView) findViewById(R.id.current_temp);
		currentCondition = (TextView) findViewById(R.id.current_condition);
		currentCity = (TextView) findViewById(R.id.current_city);
		weatherIcon = (ImageView) findViewById(R.id.weather_icon);
		forecastList = (ListView) findViewById(R.id.forecast_list);

		checkQuery("seattle");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weather, menu);

		MenuItem menuItem = menu.add("Search");
		menuItem.setIcon(android.R.drawable.ic_menu_search);
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
				| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		DeviceSearchView mSearchView = new DeviceSearchView(
				getApplicationContext());
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setOnCloseListener(this);
		mSearchView.setIconifiedByDefault(true);
		mSearchView.setQueryHint("ZipCode or City,State");
		mSearchView.setSubmitButtonEnabled(true);

		menuItem.setActionView(mSearchView);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class DeviceSearchView extends SearchView {
		public DeviceSearchView(Context context) {
			super(context);
		}

		// The normal SearchView doesn't clear its search text when
		// collapsed, so we will do this for it.
		@Override
		public void onActionViewCollapsed() {
			setQuery("", false);

		}
	}

	@Override
	public boolean onClose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		checkQuery(query);
		return false;
	}

	private void checkQuery(String queryToCheck) {

		if (!queryToCheck.isEmpty() && TextUtils.isDigitsOnly(queryToCheck)) {
			submitZipCodeQuery(queryToCheck);
		} else {
			GetCityTask results = new GetCityTask();
			//need to fix string here.. city state replace space with comma
			
			String[] mData = queryToCheck.split("\\s+");
			if (mData.length > 1) {
				 
				results.execute(mData[0] + "," + mData[1]);
			}else {
				results.execute(queryToCheck);
			}
			
			
		}
	}

	

	private void submitZipCodeQuery(String query) {

		WundergroundApiProvider.getClient().query(
				new ContextAwareAPIDelegate<WundergroundResponse>(
						WeatherActivity.this, WundergroundResponse.class,
						RequestCache.LoadPolicy.NEVER) {
					@Override
					public void onResults(
							WundergroundResponse wundergroundResponse) {
						if (wundergroundResponse.getCurrentObservation() != null
								&& wundergroundResponse.getForecast() != null) {

							setViews(wundergroundResponse);

						} else {
							onError(null);
						}

					}

					@Override
					public void onError(Throwable e) {
						Toast.makeText(WeatherActivity.this,
								"Please try again.. This city was not found.",
								Toast.LENGTH_LONG).show();
					}
				}, "ad803dbffbba6aa9", null, Query.usZipCode(query),
				Feature.conditions, Feature.forecast);
	}

	private void setViews(WundergroundResponse wundergroundResponse) {

		currentCity.setText("Current observation for "
				+ wundergroundResponse.getCurrentObservation()
						.getObservationLocation().getCity()
				+ " "
				+ wundergroundResponse.getCurrentObservation()
						.getObservationLocation().getState());

		currentTemp.setText(wundergroundResponse.getCurrentObservation()
				.getTemperatureString());
		currentCondition.setText(wundergroundResponse.getCurrentObservation()
				.getWeather());

		UrlImageViewHelper.setUrlDrawable(weatherIcon, wundergroundResponse
				.getCurrentObservation().getIconUrl(), R.drawable.ic_launcher);

		List<ForecastDayResponse> foreCast = wundergroundResponse.getForecast()
				.getTxtForecast().getForecastDay();

		ForecastListAdapter adapter = new ForecastListAdapter(
				getApplicationContext(), R.layout.forecast_row);
		for (int i = 0; i < foreCast.size(); i++) {

			adapter.add(foreCast.get(i));

		}
		forecastList.setAdapter(adapter);

	}

	@Subscribe
	public void submitClosestSearchResult(CityResults event)
			throws JSONException {

		String result = event.resultsArray.getJSONObject(0).get("zmw")
				.toString();
		submitZipCodeQuery(result);

	}

	@Override
	protected void onResume() {

		BusProvider.getInstance().register(this);
		super.onResume();
	}

	@Override
	protected void onPause() {

		BusProvider.getInstance().unregister(this);
		super.onPause();
	}

}
