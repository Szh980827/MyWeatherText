package com.example.songzhihao.myweathertext.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SongZhihao on 2019/3/20.
 */
public class Future {
	@SerializedName("temperature")
	public String temperature;

	@SerializedName("weather")
	public String weather;

	public WeatherId weatherId;
	public class WeatherId{
		public String fa;
		public String fb;
	}

	@SerializedName("wind")
	public String wind;

	@SerializedName("week")
	public String week;

	@SerializedName("date")
	public String date;

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
