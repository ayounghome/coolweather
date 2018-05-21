package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by ASU on 2018/5/18.
 */

/*声明类 province 继承于 datasupport类*/

public  class Province extends DataSupport {
    private int id;                                         /*实体类的id  注：private声明 只能被当前类调用*/
    private String provinceName;                            /*省的名字*/
    private int provinceCode;                                /*省的代号*/
                                                            /*生成相应的getter和setter方法；
                                                            相应：类对应数据库中的表，类中的每一字段对应表中每一列*/
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getProvinceName() {
        return provinceName;
    }
    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
    public int getProvinceCode() {
        return provinceCode;
    }
    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
