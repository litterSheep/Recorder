package com.ly.recorder.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by ly on 2017/3/2 14:50.
 */
@Entity
public class Account {

    @Id
    private Long id;
    private Integer year;
    private Integer month;
    private Integer date;
    private Float breakfast;
    private Float lunch;
    private Float dinner;
    private Float other;
    @NotNull
    private Float total;
    private String remark;
    private Long time;

    @Generated(hash = 1986716749)
    public Account(Long id, Integer year, Integer month, Integer date,
                   Float breakfast, Float lunch, Float dinner, Float other,
                   @NotNull Float total, String remark, Long time) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.date = date;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.other = other;
        this.total = total;
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

    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return this.month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDate() {
        return this.date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Float getBreakfast() {
        return this.breakfast;
    }

    public void setBreakfast(Float breakfast) {
        this.breakfast = breakfast;
    }

    public Float getLunch() {
        return this.lunch;
    }

    public void setLunch(Float lunch) {
        this.lunch = lunch;
    }

    public Float getDinner() {
        return this.dinner;
    }

    public void setDinner(Float dinner) {
        this.dinner = dinner;
    }

    public Float getOther() {
        return this.other;
    }

    public void setOther(Float other) {
        this.other = other;
    }

    public Float getTotal() {
        return this.total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


}
