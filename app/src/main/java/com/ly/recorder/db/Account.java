package com.ly.recorder.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ly on 2017/3/2 14:50.
 */
@Entity
public class Account {

    @Id
    private Long id;
    private int year;
    private int month;
    private float breakfast;
    private float lunch;
    private float dinner;
    private float other;
    private String remark;
    private long time;
    @Generated(hash = 812308178)
    public Account(Long id, int year, int month, float breakfast, float lunch,
            float dinner, float other, String remark, long time) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.other = other;
        this.remark = remark;
        this.time = time;
    }
    @Generated(hash = 882125521)
    public Account() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getYear() {
        return this.year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getMonth() {
        return this.month;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public float getBreakfast() {
        return this.breakfast;
    }
    public void setBreakfast(float breakfast) {
        this.breakfast = breakfast;
    }
    public float getLunch() {
        return this.lunch;
    }
    public void setLunch(float lunch) {
        this.lunch = lunch;
    }
    public float getDinner() {
        return this.dinner;
    }
    public void setDinner(float dinner) {
        this.dinner = dinner;
    }
    public float getOther() {
        return this.other;
    }
    public void setOther(float other) {
        this.other = other;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }



}
