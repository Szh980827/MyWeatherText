package com.example.songzhihao.myweathertext.adapter;

public class Citylist {
    private String areaName;
    private int imageId;
    private String nowTemperature;
    private String maxLowTemperature;



    public Citylist(String areaName, int imageId, String nowTemperature, String maxLowTemperature) {
        this.areaName = areaName;
        this.imageId = imageId;
        this.nowTemperature = nowTemperature;
        this.maxLowTemperature = maxLowTemperature;
    }

    public String getAreaName() {
        return areaName;
    }

    public int getImageId() {
        return imageId;
    }

    public String getNowTemperature() {
        return nowTemperature;
    }

    public String getMaxLowTemperature() {
        return maxLowTemperature;
    }

    public void setMaxLowTemperature(String maxLowTemperature) {
        this.maxLowTemperature = maxLowTemperature;
    }
}
