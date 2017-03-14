package com.ly.recorder.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by ly on 2017/3/2 14:50.
 */
@Entity
public class Account {

    @Id
    private Long id;
    private Integer year;
    private Integer month;
    private Integer day;
    private Long time;
    private Float money;
    private String remark;
    private Integer type;

    @Generated(hash = 1928322829)
    public Account(Long id, Integer year, Integer month, Integer day, Long time,
                   Float money, String remark, Integer type) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.time = time;
        this.money = money;
        this.remark = remark;
        this.type = type;
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

    public Integer getDay() {
        return this.day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }
    public Long getTime() {
        return this.time;
    }
    public void setTime(Long time) {
        this.time = time;
    }
    public Float getMoney() {
        return this.money;
    }
    public void setMoney(Float money) {
        this.money = money;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public Integer getType() {
        return this.type;
    }
    public void setType(Integer type) {
        this.type = type;
    }


}
