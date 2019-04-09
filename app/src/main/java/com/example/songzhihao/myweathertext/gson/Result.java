package com.example.songzhihao.myweathertext.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by SongZhihao on 2019/3/21.
 */
public class Result {
	public String sk;
	public String today;

	@SerializedName("future")
	public List<Future> futureList;
}
