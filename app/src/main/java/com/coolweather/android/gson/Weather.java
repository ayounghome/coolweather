package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ASU on 2018/5/22.
 */

public class Weather {
    /*Weather类作为总的实例类来引用以上各个实体类*/
    public String status;                              /*status数据，成功获取天气数据的时候会返回ok,失败时会返回具体原因。*/
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Forecast forecast;
    public Suggestion suggestion;
    @SerializedName("dally_forecast")                  /*由于daily_forecast包含的是一个数组，因此使用List集合来引用Forecast类*/
    public List<Forecast> forecastList;
}
