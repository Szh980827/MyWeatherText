package com.example.songzhihao.myweathertext.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SongZhihao on 2019/3/21.
 */
public class Weather {
	@SerializedName("resultcode")
	public String resultcode;

	public Weather() {
	}

	public Weather(String resultcode, String reason, String result) {
		this.resultcode = resultcode;
		this.reason = reason;
		this.result = result;
	}

	@SerializedName("reason")
	public String reason;

	@SerializedName("result")
	public String result;
}
