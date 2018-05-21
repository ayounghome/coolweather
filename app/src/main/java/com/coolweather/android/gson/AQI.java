package com.coolweather.android.gson;

/**
 * Created by ASU on 2018/5/21.
 */

public class AQI {
    public AQICITY city;
    public class AQICITY{
        public String aqi;      /*空气质量指数*/
        public String co;       /*一氧化碳*/
        public String nc2;      /*二氧化氮*/
        public String o3;       /*臭氧*/
        public String pm10;     /*PM10*/
        public String pm25;     /*PM2.5*/
        public String qliy;     /*空气质量（优/良/轻度污染/重度污染/严重污染）*/
        public String so2;      /*二氧化硫*/
    }
}
