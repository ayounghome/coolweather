package com.coolweather.android;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by ASU on 2018/5/20.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;                 /*进度条(加载省市县信息时会出现)*/
    private TextView titleText;                            /*标题*/
    private Button backButton;                             /*返回键*/
    private ListView listView;                             /*省市县列表*/
    private ArrayAdapter<String> adapter;                  /*适配器*/
    private List<String> dataList = new ArrayList<>();     /*泛型*/
    /*省列表*/
    private List<Province> provinceList;
    /*市列表*/
    private List<City> cityList;
    /*省列表*/
    private List<County> countyList;
    /*选中的省份*/
    private Province selectedProvince;
    /*选中的城市*/
    private City selectedCity;
    /*当前选中的级别*/
    private int currentLevel;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*获取控件实例*/
        View view = inflater.inflate(R.layout.choose_area,container,false);      /*获取布局，false表示布局文件转化为View但不绑定到root，
                                                                                              返回以布局文件根节点为根节点的View。*/
        titleText = (TextView) view.findViewById(R.id.title_text);                            /*获取TextView*/
        backButton = (Button) view.findViewById(R.id.back_button);                            /*获取Button*/
        listView = (ListView) view.findViewById(R.id.list_view);                              /*获取ListView*/
        /*初始化ArrayAdapter*/
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        /*将adapter设置为ListView的适配器*/
        listView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {                     /*ListView的点击事件*/
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {                                               /*省级列表*/
                    selectedProvince = provinceList.get(position);                                  /*选择省*/
                    queryCities();                                                                  /*查找城市*/
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {                                  /*Button的点击事件*/
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {                                                 /*如果在县级列表*/
                    queryCities();                                                                  /*则返回城市列表*/
                } else if (currentLevel == LEVEL_CITY) {                                            /*如果在城市列表*/
                    queryProvinces();                                                               /*则放回省级列表*/
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        titleText.setText("中国");                                                                   /*头标题*/
        backButton.setVisibility(View.GONE);                                                        /*当处于省级列表时，返回按键隐藏*/
        provinceList = DataSupport.findAll(Province.class);                                         /*从数据库中读取省级数据*/
        if (provinceList.size() > 0) {                                                              /*如果读到数据，则直接显示到界面上;size()是获取泛型集合里面的元素个数*/
            dataList.clear();                                                                       /*释放dataList集合中的所有对象*/
            for (Province province : provinceList) {                                                /*遍历所有省级数据*/
                dataList.add(province.getProvinceName());                                           /*添加省名*/
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;                                                          /*当前的等级为省级*/
        } else {                                                                                    /*如果没有读到数据，则组装出一个请求地址，
                                                                                                    调用queryFromServer()方法从服务器上查询数据*/
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);                                                     /*当处于市级列表时，返回按键显示*/
        cityList = DataSupport.where("provinceid = ?",                                   /*根据从数据库中读取的省级数据，*/
                String.valueOf(selectedProvince.getId())).find(City.class);                         /*找出相应的城市类*/
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {                                                                                    /*查询出省级的代码*/
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
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
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据。
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {                                        /*向服务器发生请求，响应的数据会回调到onResponse()方法中*/
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);                          /*解析和处理从服务器返回的数据，并存储到数据库中*/
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {                                    /*由于query方法用到UI操作，必须要在主线程中调用。
                                                                                                    借助runOnUiThread()方法实现从子线程切换到主线程*/
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();                                                   /*数据库已经存在数据，调用queryProvinces直接将数据显示到界面上*/
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
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
