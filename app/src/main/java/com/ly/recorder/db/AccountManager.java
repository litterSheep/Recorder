package com.ly.recorder.db;

import com.ly.recorder.App;
import com.ly.recorder.db.greendao.AccountDao;

import java.util.List;

/**
 * Created by ly on 2017/3/3 15:00.
 */

public class AccountManager {


    private AccountDao accountDao;

    public AccountManager() {
        accountDao = App.getInstance().getDaoSession().getAccountDao();
    }

    public List<Account> queryAll() {
        return accountDao.queryBuilder().list();
    }
}
