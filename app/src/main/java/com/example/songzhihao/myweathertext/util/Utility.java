package com.example.songzhihao.myweathertext.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.songzhihao.myweathertext.db.City;
import com.example.songzhihao.myweathertext.db.County;
import com.example.songzhihao.myweathertext.db.Province;
import com.example.songzhihao.myweathertext.gson.Sk;
import com.example.songzhihao.myweathertext.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SongZhihao on 2019/3/11.
 */
public class Utility {


	/*
	 * 解析和处理服务器返回的省级数据
	 */

	public static boolean handleProvinceResponse(String response) {
		if (!TextUtils.isEmpty(response)) {
			try {
				/*
				 * 解析 Json 数据
				 */
				JSONArray allProvinces = new JSONArray(response);
				for (int i = 0; i < allProvinces.length(); i++) {
					JSONObject provinceObject = allProvinces.getJSONObject(i);
					Province province = new Province();
					province.setProvinceName(provinceObject.getString("name"));
					province.setProvinceCode(provinceObject.getInt("id"));
					province.save();
				}
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean handleCityResponse(String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			try {
				JSONArray allCitys = new JSONArray(response);
				for (int i = 0; i < allCitys.length(); i++) {
					JSONObject cityObject = allCitys.getJSONObject(i);
					City city = new City();
					city.setCityName(cityObject.getString("name"));
					city.setCityCode(cityObject.getInt("id"));
					city.setProvinceId(provinceId);
					city.save();
				}
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean handleCountyResponse(String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			try {
				JSONArray allCounties = new JSONArray(response);
				for (int i = 0; i < allCounties.length(); i++) {
					JSONObject countyObject = allCounties.getJSONObject(i);
					County county = new County();
					county.setCountyName(countyObject.getString("name"));
					county.setWeatherId(countyObject.getString("weather_id"));
					county.setCityId(cityId);
					county.save();
				}
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * 将返回的 JSON 数据解析成 Weather 实体类
	 */

	public static Weather handleWeatherResponse(String response){
		Log.d("haohao1","进入handleWeatherResponse方法");
		try {
			JSONObject jsonObject = new JSONObject(response);
//			Gson gson = new Gson();
//			List<Weather> weatherList = gson.fromJson(response,new TypeToken<List<Weather>>(){}.getType());
			Log.d("haohao1","进入handleWeatherResponse.try");
			String resultcode = jsonObject.getString("resultcode");
			String reason = jsonObject.getString("reason");
			String result = jsonObject.getString("result");
			Weather weather = new Weather(resultcode,reason,result);
			return weather;

		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

//	/*
//	 * 将返回的 JSON 数据解析成 Sk，Today ，Future 实体类
//	 */
//
//	public static Sk handleSkResponse(String response){
//		Log.d("haohao1","进入handleSkResponse()方法");
//		try {
//			JSONObject jsonObject = new JSONObject(response);
//			String temp = jsonObject.getString("temp");
//			String wind_direction = jsonObject.getString("wind_direction");
//			String wind_strength = jsonObject.getString("wind_strength");
//			String humidity = jsonObject.getString("humidity");
//			String time = jsonObject.getString("time");
//			Sk sk = new Sk(temp,wind_direction,wind_strength,humidity,time);
//			return sk;
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//		return null;
//	}
}
