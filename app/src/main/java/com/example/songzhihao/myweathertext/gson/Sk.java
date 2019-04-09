package com.example.songzhihao.myweathertext.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SongZhihao on 2019/3/21.
 */
public class Sk {

	@SerializedName("temp")
	public String temp;

	@SerializedName("wind_direction")
	public String wind_direction;

	@SerializedName("wind_strength")
	public String wind_strength;

	@SerializedName("humidity")
	public String humidity;

	@SerializedName("time")
	public String time;

	public Sk(String temp, String wind_direction, String wind_strength, String humidity, String time) {
		this.temp = temp;
		this.wind_direction = wind_direction;
		this.wind_strength = wind_strength;
		this.humidity = humidity;
		this.time = time;
	}
}
