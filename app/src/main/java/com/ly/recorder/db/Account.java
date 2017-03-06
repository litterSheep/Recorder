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
    private Integer date;
    private Long time;
    private Float money;
    private String remark;

    @Generated(hash = 1006755246)
    public Account(Long id, Integer year, Integer month, Integer date, Long time,
                   Float money, String remark) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.date = date;
        this.time = time;
        this.money = money;
        this.remark = remark;
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


}
