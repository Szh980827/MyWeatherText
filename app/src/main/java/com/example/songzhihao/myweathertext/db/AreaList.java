package com.example.songzhihao.myweathertext.db;

import org.litepal.crud.DataSupport;

/**
 * Created by SongZhihao on 2019/3/12.
 */
public class AreaList extends DataSupport {

    private int id;
    private String araeName;
    private String areaCode;

    public String getAraeName() {
        return araeName;
    }

    public void setAraeName(String araeName) {
        this.araeName = araeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
}
