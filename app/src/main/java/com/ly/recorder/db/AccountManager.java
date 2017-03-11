package com.ly.recorder.db;

import com.ly.recorder.App;
import com.ly.recorder.db.greendao.AccountDao;

import java.util.Calendar;
import java.util.List;

/**
 * Created by ly on 2017/3/3 15:00.
 */

public class AccountManager {

    private AccountDao accountDao;

    public AccountManager() {
        accountDao = App.getInstance().getDaoSession().getAccountDao();
    }

    public long insert(Account account) {
        return accountDao.insert(account);
    }

    public List<Account> queryAll() {
        return accountDao.queryBuilder().list();
    }

    public List<Account> queryForNum(int num) {
        return accountDao.queryBuilder().limit(num).orderDesc(AccountDao.Properties.Time).build().list();
    }

    public List<Account> queryForDay(int year, int month, int date) {
        return accountDao.queryBuilder()
                .where(AccountDao.Properties.Year.eq(year)
                        , AccountDao.Properties.Month.eq(month)
                        , AccountDao.Properties.Date.eq(date))
                .build().list();
    }

    /**
     * 按日期升序返回这一月的数据
     * Created by ly on 2017/3/11 9:43
     */
    public List<Account> queryForMonth(int year, int month) {
        return accountDao.queryBuilder()
                .where(AccountDao.Properties.Year.eq(year)
                        , AccountDao.Properties.Month.eq(month))
                .orderAsc(AccountDao.Properties.Date)
                .build().list();
    }

    /**
     * 按月份升序返回这一年的数据
     * Created by ly on 2017/3/11 9:43
     */
    public List<Account> queryForYear(int year) {
        return accountDao.queryBuilder()
                .where(AccountDao.Properties.Year.eq(year))
                .orderAsc(AccountDao.Properties.Month)
                .build().list();
    }

    /**
     * 查询所有记录中最早的年份
     * Created by ly on 2017/3/10 15:38
     */
    public int queryMinYear() {
        List<Account> accountList = accountDao.queryBuilder()
                .orderAsc(AccountDao.Properties.Year).build().list();
        if (accountList != null && accountList.size() > 0)
            return accountList.get(0).getYear();

        return Calendar.getInstance().get(Calendar.YEAR);
    }

}
