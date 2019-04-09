package com.example.songzhihao.myweathertext;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.songzhihao.myweathertext.adapter.Citylist;
import com.example.songzhihao.myweathertext.adapter.CitylistAdapter;
import com.example.songzhihao.myweathertext.db.AreaList;
import com.example.songzhihao.myweathertext.gson.Future;
import com.example.songzhihao.myweathertext.db.City;
import com.example.songzhihao.myweathertext.gson.Sk;
import com.example.songzhihao.myweathertext.gson.Weather;
import com.example.songzhihao.myweathertext.util.HttpUtil;
import com.example.songzhihao.myweathertext.util.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CityListActivity extends AppCompatActivity {

	private final String TAG = "haohao";     // 用于测试数据库的TAG
	private final String TAG2 = "haohao1";   // 用于测试Json解析
	private final String TAG3 = "haohao2";
	private final String TAG4 = "haohao3";


	private List<Citylist> citylists = new ArrayList<>();
	private List<AreaList> areaLists = new ArrayList<>();
	private Citylist selectCitylist;
	private String areaName;
	private String areaId;

	private String nowTem;
	private String weatherType;
	private String imageId;
	private String maxLowTem;

	private String resultData;
	private String dataSk;
	private String dataToday;
	private String dataFuture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_list);
		/*
		 * 隐藏标题栏
		 */
		ActionBar actionbar = getSupportActionBar();
		if (actionbar != null) {
			actionbar.hide();
		}
		/*
		 * 悬浮按钮的单击事件
		 */
		FloatingActionButton fab = findViewById(R.id.addcity_fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(CityListActivity.this, AddCityActivity.class);
				startActivity(intent);
				finish();
			}
		});

		// 初始化ListView
		final CitylistAdapter adapter = new CitylistAdapter(CityListActivity.this, R.layout.citylist_item, citylists);
		final ListView listView = findViewById(R.id.cityList_list);
		listView.setAdapter(adapter);

		/*
		 * listView 的单击事件
		 * 选取 城市 后 进入 MainActivity
		 */

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				selectCitylist = citylists.get(position);
				Intent intent = new Intent();
				intent.setClass(CityListActivity.this, MainActivity.class);
				intent.putExtra("areaName", selectCitylist.getAreaName());
				startActivity(intent);
				finish();
			}
		});

		/*
		 * ListView 长按事件
		 * 删除所选城市
		 * ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
		 * bug和缺陷：只会删除了cityLists中的列表项，没有进行实质性的数据库删除，重新加载此活动，删除的城市将继续显示
		 *
		 * 已解决：
		 * 使用DataSupport.deleteAll()方法来删除数据库数据
		 */
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
				// 定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
				AlertDialog.Builder builder = new AlertDialog.Builder(CityListActivity.this);
				builder.setMessage("确定删除所选城市？");
				builder.setTitle("提示");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (citylists.remove(position) != null) {
							Log.d(TAG, "success");
						} else {
						}
						/*
						 * 逻辑错误   ListView 无法确定删除的元素，导致数据库数据和ListView数据不同步，app崩溃
						 * 解决方案   每次删除前，加载一次ListView
						 */
						initCitylists();    // 进行加载初始化ListView
						selectCitylist = citylists.get(position);    //  获取选择的城市
						Log.d(TAG, "删除城市的名字：" + selectCitylist.getAreaName());
						// 删除所选项目数据
						DataSupport.deleteAll(AreaList.class, "araeName=?", selectCitylist.getAreaName());
						adapter.notifyDataSetChanged();
						Toast.makeText(getBaseContext(), "删除列表项", Toast.LENGTH_SHORT).show();
						initCitylists();
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				});
				builder.create().show();
				return true;
			}
		});

		/*
		 * 通过添加城市界面获取到 城市名称和Id
		 */
		Intent intent = getIntent();
		areaName = intent.getStringExtra("areaName");
		areaId = intent.getStringExtra("areaId");
		Log.d(TAG, "接收到的数据：" + areaId + areaName);
		sendRequestWithOkHttp(areaName);
		initCitylists();
		/*
		 *  添加城市到数据库
		 *  ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
		 *  目前已知 bug：若添加城市已存在，会继续添加该城市
		 *  大体解决思路：添加前，遍历数据库，若已存在，弹出toast提示用户已添加该城市
		 *
		 *  已解决！
		 */

		if (ifAddCity(areaName)) {
			initCitylists();
			Toast.makeText(this, "您已添加该城市！", Toast.LENGTH_SHORT).show();
		} else {
			AreaList areaList = new AreaList();
			areaList.setAraeName(areaName);
			areaList.setAreaCode(areaId);
			areaList.save();
			initCitylists();
		}
	}

	/*
	 * 判断是否重复添加相同城市
	 */
	private boolean ifAddCity(String str) {
		areaLists = DataSupport.findAll(AreaList.class);
		if (areaLists.size() > 0) {
			for (AreaList areaList : areaLists) {
				if (areaList.getAraeName().equals(str)) {
					return true;
				}
			}
		}
		return false;
	}

	private void sendRequestWithOkHttp(final String name) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					OkHttpClient client = new OkHttpClient();
					Log.d(TAG, "sendRequestWithOkHttp:" + name);
					String address = "http://v.juhe.cn/weather/index?format=2&cityname=" + name + "&key=b737a42de6f1fca11a221bd62cc156a0";
					Log.d(TAG, "address:" + address);
					Request request = new Request.Builder()
							.url(address)
							.build();
					Response response = client.newCall(request).execute();
					String responseData = response.body().string();

					parseJSONWithGSON(responseData);
					parseJSONWithGSON2(resultData);
					parseJSONWithGSON_sk(dataSk);
					parseJSONWithGSON_today(dataToday);
					parseJSONWithGSON_future(dataFuture);
					Log.d("haohao1", responseData+dataSk);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void parseJSONWithGSON(String jsonData) {
		Log.d(TAG3,"jsonData："+jsonData);
		try {


			JSONObject jsonObject = new JSONObject(jsonData.toString());
			String resultCode = jsonObject.getString("resultcode");
			String reason = jsonObject.getString("reason");
			String result = jsonObject.getString("result");

			Log.d("MainActivity", resultCode);
			Log.d("MainActivity", reason);
			Log.d("MainActivity", result);
			resultData = result;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseJSONWithGSON2(String jsonData) {
		Log.d(TAG3,"2jsonData："+jsonData);
		try {

			JSONObject jsonObject = new JSONObject(jsonData.toString());
			String sk = jsonObject.getString("sk");
			String today = jsonObject.getString("today");
			String future = jsonObject.getString("future");
			Log.d("MainActivity", "sk:" + sk);
			Log.d("MainActivity", "today:" + today);
			Log.d("MainActivity", "future:" + future);
			dataSk = sk;
			dataToday = today;
			dataFuture = future;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseJSONWithGSON_sk(String jsonData) {
		Log.d(TAG3,"skJsonData："+jsonData);
		try {
			JSONObject jsonObject = new JSONObject(jsonData.toString());
			String temp = jsonObject.getString("temp");
			String wind_direction = jsonObject.getString("wind_direction");
			String wind_strength = jsonObject.getString("wind_strength");
			String humidity = jsonObject.getString("humidity");
			String time = jsonObject.getString("time");
			Log.d(TAG, "temp:  " + temp);
			Log.d(TAG, "wind_direction:  " + wind_direction);
			Log.d(TAG, "wind_strength:  " + wind_strength);
			Log.d(TAG, "humindity:  " + humidity);
			Log.d(TAG, "time:  " + time);
			nowTem = temp;
			Log.d(TAG, "temp:" + temp + ",nowTem:" + nowTem);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseJSONWithGSON_today(String jsonData) {
		Log.d(TAG3,"todayJsonData："+jsonData);
		try {

			JSONObject jsonObject = new JSONObject(jsonData.toString());
			String temperature = jsonObject.getString("temperature");
			String weather = jsonObject.getString("weather");
			String weatherId = jsonObject.getString("weather_id");
			JSONObject jsonObject1 = new JSONObject(weatherId.toString());
			String fa = jsonObject1.getString("fa");
			String fb = jsonObject1.getString("fb");
			String wind = jsonObject.getString("wind");
			String week = jsonObject.getString("week");
			String city = jsonObject.getString("city");
			String date_y = jsonObject.getString("date_y");
			String dressing_index = jsonObject.getString("dressing_index");
			String dressing_advice = jsonObject.getString("dressing_advice");
			String uv_index = jsonObject.getString("uv_index");
			String comfort_index = jsonObject.getString("comfort_index");
			String wash_index = jsonObject.getString("wash_index");
			String travel_index = jsonObject.getString("travel_index");
			String exercise_index = jsonObject.getString("exercise_index");
			String drying_index = jsonObject.getString("drying_index");

			Log.d("MainActivity_today", "temperature：  " + temperature);
			Log.d("MainActivity_today", "weather：  " + weather);
			Log.d("MainActivity_today", "wind：  " + wind);
			Log.d("MainActivity_today", "week：  " + week);
			Log.d("MainActivity_today", "city：  " + city);
			Log.d("MainActivity_today", "date_y：  " + date_y);
			Log.d("MainActivity_today", "dressing_index：  " + dressing_index);
			Log.d("MainActivity_today", "dressing_advice：  " + dressing_advice);
			Log.d("MainActivity_today", "uv_index：  " + uv_index);
			Log.d("MainActivity_today", "comfort_index：  " + comfort_index);
			Log.d("MainActivity_today", "wash_index：  " + wash_index);
			Log.d("MainActivity_today", "travel_index：  " + travel_index);
			Log.d("MainActivity_today", "exercise_index：  " + exercise_index);
			Log.d("MainActivity_today", "drying_index：  " + drying_index);
			weatherType = weather;
			maxLowTem = temperature;
			imageId = fa;
			Log.d(TAG, "maxLowTem:" + maxLowTem + "fb::::" + fa);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseJSONWithGSON_future(String jsonData) {
		Log.d(TAG3,"futureJsonData："+jsonData);
		Gson gson = new Gson();
		List<Future> appList = gson.fromJson(jsonData, new TypeToken<List<Future>>() {
		}.getType());
		for (Future future : appList) {
			Log.d("MainActivity_future", "Temperature:  " + future.getTemperature());
			Log.d("MainActivity_future", "Weather:  " + future.getWeather());
			Log.d("MainActivity_future", "Wind:  " + future.getWind());
			Log.d("MainActivity_future", "Week:  " + future.getWeek());
			Log.d("MainActivity_future", "Date:  " + future.getDate());
		}
	}

	private void requestWeather(final String name) {

		String address = "http://v.juhe.cn/weather/index?format=2&cityname="
				+ name + "&key=b737a42de6f1fca11a221bd62cc156a0";
		HttpUtil.sendOkHttpRequest(address, new Callback() {
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Log.d(TAG2,"进入requestWeather.onResponse方法");
				final String responseText = response.body().string();
				final Weather weather = Utility.handleWeatherResponse(responseText);
				Log.d(TAG2,"返回的result："+weather.result);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (weather!=null && "200".equals(weather.resultcode)){

						}else {
							Toast.makeText(CityListActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(CityListActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

	}


	/*
	 * 加载ListView数据
	 */
	private void initCitylists() {

		/*
		 * 通过数据库 加载添加的城市
		 * 获取天气 ，显示到 城市管理界面
		 * arealist.getAraeName() 可以获取已添加城市的名称
		 */

		citylists.clear();
		Log.d(TAG, "进入initCitylists()");
		areaLists = DataSupport.findAll(AreaList.class);
		Log.d(TAG, areaLists.size() + "");
		if (areaLists.size() > 0) {
			for (AreaList arealist : areaLists) {
				Log.d(TAG, "areaLists:" + arealist.getAraeName());
				sendRequestWithOkHttp(arealist.getAraeName());
				requestWeather(arealist.getAraeName());
				// 地区名称是 arealist.getAraeName()
				//Citylist city;

				//Log.d("imgid",imageId);
				/*
				 * 根据返回天气类型，加载不同的天气图标
				 */
//				switch (imageId) {
//					case "00":
//						Citylist city = new Citylist(arealist.getAraeName(), R.mipmap.sunny, nowTem + "℃", maxLowTem);
//						citylists.add(city);
//						break;
//					case "01":
//						Citylist city1 = new Citylist(arealist.getAraeName(), R.mipmap.cloudytosunny, nowTem + "℃", maxLowTem);
//						citylists.add(city1);
//						break;
//					case "02":
//						Citylist city2 = new Citylist(arealist.getAraeName(), R.mipmap.cloudy, nowTem + "℃", maxLowTem);
//						citylists.add(city2);
//						break;
//					case "03":
//					case "07":
//					case "21":
//						Citylist city3 = new Citylist(arealist.getAraeName(), R.mipmap.light_rain, nowTem + "℃", maxLowTem);
//						citylists.add(city3);
//						break;
//					case "04":
//					case "05":
//						Citylist city4 = new Citylist(arealist.getAraeName(), R.mipmap.thunder_rain, nowTem + "℃", maxLowTem);
//						citylists.add(city4);
//						break;
//					case "13":
//					case "06":
//					case "14":
//					case "15":
//					case "16":
//					case "17":
//					case "26":
//					case "27":
//					case "28":
//						Citylist city5 = new Citylist(arealist.getAraeName(), R.mipmap.snow, nowTem + "℃", maxLowTem);
//						citylists.add(city5);
//						break;
//					case "08":
//					case "22":
//						Citylist city6 = new Citylist(arealist.getAraeName(), R.mipmap.heavy_rain, nowTem + "℃", maxLowTem);
//						citylists.add(city6);
//						break;
//					case "9":
//					case "10":
//					case "23":
//					case "11":
//					case "24":
//					case "25":
//					case "12":
//					case "19":
//						Citylist city7 = new Citylist(arealist.getAraeName(), R.mipmap.hail, nowTem + "℃", maxLowTem);
//						citylists.add(city7);
//						break;
//					case "18":
//					case "20":
//					case "29":
//					case "30":
//					case "31":
//					case "53":
//						Citylist city8 = new Citylist(arealist.getAraeName(), R.mipmap.windy, nowTem + "℃", maxLowTem);
//						citylists.add(city8);
//						break;
//					default:
//						break;
//				}

				Citylist city = new Citylist(arealist.getAraeName(), R.mipmap.sunny, nowTem + "℃", maxLowTem);
				Log.d(TAG, "列表中获取的天气：" + nowTem + maxLowTem);
				citylists.add(city);
			}
		} else {
			Toast.makeText(this, "您尚未添加任何城市！", Toast.LENGTH_SHORT).show();
		}


		/*
		 * 死数据测试 ListView 能否显示
		 */

//        for (int i = 0; i < 10; i++) {
//            Citylist city1 = new Citylist("济南", R.mipmap.snow, "15℃", "20℃", "8℃");
//            citylists.add(city1);
//            Citylist city2 = new Citylist("潍坊", R.mipmap.sunny, "12℃", "18℃", "6℃");
//            citylists.add(city2);
//            Citylist city3 = new Citylist("烟台", R.mipmap.hail, "9℃", "15℃", "6℃");
//            citylists.add(city3);
//        }

	}


}
