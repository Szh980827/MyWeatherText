package com.example.songzhihao.myweathertext;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.songzhihao.myweathertext.adapter.Citylist;
import com.example.songzhihao.myweathertext.db.City;
import com.example.songzhihao.myweathertext.db.County;
import com.example.songzhihao.myweathertext.db.Province;
import com.example.songzhihao.myweathertext.util.HttpUtil;
import com.example.songzhihao.myweathertext.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddCityActivity extends AppCompatActivity {


	/*
	 * 设置列表级别
	 */
	private static final int LEVEL_PROVINCE = 0;
	private static final int LEVEL_CITY = 1;
	private static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;

	//    private EditText editText;
//    private Button search_but;
	private Button addcity_but;
	private TextView addcity_title_tv;
	private String areaName;
	private String areaId;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private List<String> dataList = new ArrayList<>();

	/*
	 * 省市县 列表
	 */
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	/*
	 * 选中的 省市
	 */
	private Province selectedProvince;
	private City selectedCity;
	private County selectCounty;
	/*
	 * 当前选中的级别
	 */
	private int currentLevel;


	private final String TAG = "haohao";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_city);
		// 隐藏标题栏
		ActionBar actionbar = getSupportActionBar();
		if (actionbar != null) {
			actionbar.hide();
		}
		// 初始化控件


		addcity_but = findViewById(R.id.addcity_back);
		addcity_title_tv = findViewById(R.id.addcity_title_tv);
		// button 单击事件
		addcity_but.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (currentLevel == LEVEL_PROVINCE) {
					Intent intent = new Intent(AddCityActivity.this, CityListActivity.class);
					startActivity(intent);
					finish();
				} else if (currentLevel == LEVEL_CITY) {
					queryProvinces();
				} else if (currentLevel == LEVEL_COUNTY) {
					queryCities();
				}
			}
		});

		/*
		 * 搜索输入框按钮
		 */
//        editText = findViewById(R.id.editText);
//        search_but = findViewById(R.id.search_but);
//        search_but.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                areaName = editText.getText().toString().trim();
//                Intent intent = new Intent();
//                intent.setClass(AddCityActivity.this, MainActivity.class);
//                intent.putExtra("areaName", areaName);
//                startActivity(intent);
//                finish();
//            }
//        });
		listView = findViewById(R.id.addarea_list);
		adapter = new ArrayAdapter<>(listView.getContext(), android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					// 选择县/市之后，传递城市数据到城市管理界面
					selectCounty = countyList.get(position);
					areaName = selectCounty.getCountyName();
					areaId = selectCounty.getId() + "";
					Intent intent = new Intent();
					intent.setClass(AddCityActivity.this, CityListActivity.class);
					intent.putExtra("areaName", areaName);
					intent.putExtra("areaId", areaId);
					startActivity(intent);
					finish();
				}

			}
		});
		queryProvinces();
	}

	/*
	 * 查询全国所有的省，优先从数据库查询，如果没有再去服务器上查询
	 */
	private void queryProvinces() {
		addcity_title_tv.setText("中国");
		addcity_but.setVisibility(View.VISIBLE);
		provinceList = DataSupport.findAll(Province.class);
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			currentLevel = LEVEL_PROVINCE;
		} else {
			String address = "http://guolin.tech/api/china";
			queryFromServer(address, "province");
		}

	}

	private void queryCities() {
		addcity_title_tv.setText(selectedProvince.getProvinceName());
		addcity_but.setVisibility(View.VISIBLE);
		cityList = DataSupport.where("provinceid=?", String.valueOf(selectedProvince.getId())).find(City.class);

		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			currentLevel = LEVEL_CITY;
		} else {
			int provinceCode = selectedProvince.getProvinceCode();
			String adress = "http://guolin.tech/api/china/" + provinceCode;
			queryFromServer(adress, "city");
		}

	}

	private void queryCounties() {
		addcity_title_tv.setText(selectedCity.getCityName());
		addcity_but.setVisibility(View.VISIBLE);
		countyList = DataSupport.where("cityid=?", String.valueOf(selectedCity.getId())).find(County.class);

		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			currentLevel = LEVEL_COUNTY;
		} else {
			int provinceCode = selectedProvince.getProvinceCode();
			int cityCode = selectedCity.getCityCode();
			Log.d(TAG, provinceCode + "  " + cityCode + "");
			String adress = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
			queryFromServer(adress, "county");
		}

	}


	/*
	 * 根据传入的地址和类型从服务器上查询 省市县数据
	 */
	private void queryFromServer(String adress, final String type) {
		Log.d(TAG, "进入queryFromServer");
		showProgressDialog();
		HttpUtil.sendOkHttpRequest(adress, new Callback() {
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String responseText = response.body().string();
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvinceResponse(responseText);
				} else if ("city".equals(type)) {
					result = Utility.handleCityResponse(responseText, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountyResponse(responseText, selectedCity.getId());
				}
				if (result) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals((type))) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				// 通过 runOnUiThread() 方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(AddCityActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}

		});
	}

	/*
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/*
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}


}
