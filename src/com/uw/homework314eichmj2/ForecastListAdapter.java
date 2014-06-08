package com.uw.homework314eichmj2;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortysevendeg.android.wunderground.api.service.response.ForecastDayResponse;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class ForecastListAdapter extends ArrayAdapter<ForecastDayResponse> {

	public ForecastListAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}



	List<ForecastDayResponse> currentForecast;
	
	
	
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.forecast_row, parent,false);
		}
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.forecast_icon);
		String url = getItem(position).getIconUrl();
		UrlImageViewHelper.setUrlDrawable(icon, url, R.drawable.ic_launcher);
		
		TextView day = (TextView) convertView.findViewById(R.id.dayofweek);
		String dayOfWeek = getItem(position).getTitle();
		day.setText(dayOfWeek);
		
		TextView condition = (TextView) convertView.findViewById(R.id.forecast);
		String forecastCondition = getItem(position).getFctTextMetric();
		condition.setText(forecastCondition);
		
		
		
		return convertView;
	}

}

