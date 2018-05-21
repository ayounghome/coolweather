package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ASU on 2018/5/21.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;   /*城市名*/
    @SerializedName("id")
    public String weatherId;  /*城市对应的天气id*/
    @SerializedName("iat")
    public String cityIat;    /*城市经度*/
    @SerializedName("lon")
    public String cityLon;    /*城市纬度*/
    public Update update;
    public class Update {
        @SerializedName("loc")
        public String UpdateTime;   /*接口更新时间*/

    }
}
