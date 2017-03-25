package com.ly.recorder.db;

import com.ly.recorder.Constants;

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
    /**
     * 1： 收入 2： 支出
     */
    private Integer type;
    /**
     * 对应 {@link Constants.TYPES_IN} 或 {@link Constants.TYPES_OUT} 的下标
     */
    private Integer typeIndex;

    @Generated(hash = 1601282061)
    public Account(Long id, Integer year, Integer month, Integer day, Long time,
                   Float money, String remark, Integer type, Integer typeIndex) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.time = time;
        this.money = money;
        this.remark = remark;
        this.type = type;
        this.typeIndex = typeIndex;
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

    public Integer getTypeIndex() {
        return this.typeIndex;
    }

    public void setTypeIndex(Integer typeIndex) {
        this.typeIndex = typeIndex;
    }


}
