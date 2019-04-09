package com.example.songzhihao.myweathertext.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SongZhihao on 2019/3/21.
 */
public class Today {

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

	@SerializedName("city")
	public String city;

	@SerializedName("date_y")
	public String date_y;

	@SerializedName("dressing_index")
	public String dressing_index;

	@SerializedName("dressing_advice")
	public String dressing_advice;

	@SerializedName("uv_index")
	public String uv_index;

	@SerializedName("comfort_index")
	public String comfort_index;

	@SerializedName("wash_index")
	public String wash_index;

	@SerializedName("travel_index")
	public String travel_index;

	@SerializedName("exercise_index")
	public String exercise_index;

	@SerializedName("drying_index")
	public String drying_index;

}
