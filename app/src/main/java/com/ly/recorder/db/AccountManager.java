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

    public void insert(Account account) {
        accountDao.insert(account);
    }

    public List<Account> queryAll() {
        return accountDao.queryBuilder().list();
    }

    public List<Account> queryForNum(int num) {
        return accountDao.queryBuilder().limit(num).orderDesc(AccountDao.Properties.Time).build().list();
    }

    public List<Account> queryForDay() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH) + 1;
        int date = ca.get(Calendar.DATE);

        return accountDao.queryBuilder()
                .where(AccountDao.Properties.Year.eq(year)
                        , AccountDao.Properties.Month.eq(month)
                        , AccountDao.Properties.Date.eq(date))
                .build().list();
    }

    public List<Account> queryForMonth() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH) + 1;
        return accountDao.queryBuilder()
                .where(AccountDao.Properties.Year.eq(year)
                        , AccountDao.Properties.Month.eq(month))
                .build().list();
    }

    public List<Account> queryForYear() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        return accountDao.queryBuilder()
                .where(AccountDao.Properties.Year.eq(year))
                .build().list();
    }

}
