package com.ly.recorder.db;

import com.ly.recorder.App;
import com.ly.recorder.Constants;
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

    public void delete(Account account) {
        accountDao.deleteInTx(account);
    }

    public List<Account> queryAll() {
        return accountDao.queryBuilder().list();
    }

    /**
     * 获取最新入账的num条数据
     * Created by ly on 2017/3/24 11:12
     */
    public List<Account> queryForRecentNum(int num) {
        return accountDao.queryBuilder().limit(num).orderDesc(AccountDao.Properties.Time).build().list();
    }

    /**
     * 按时间降序返回这一天的支出/收入list
     *
     * @param type 收支类别 {@link Constants.TYPE_IN} 或 {@link Constants.TYPE_OUT} type不为其中的值时，返回这一天收支所有list
     *             Created by ly on 2017/3/24 11:18
     */
    public List<Account> queryForDay(int type, int year, int month, int day) {
        if (type != Constants.TYPE_IN && type != Constants.TYPE_OUT) {
            return accountDao.queryBuilder()
                    .where(AccountDao.Properties.Year.eq(year)
                            , AccountDao.Properties.Month.eq(month)
                            , AccountDao.Properties.Day.eq(day))
                    .orderDesc(AccountDao.Properties.Time)
                    .build().list();
        } else {
            return accountDao.queryBuilder()
                    .where(AccountDao.Properties.Year.eq(year)
                            , AccountDao.Properties.Month.eq(month)
                            , AccountDao.Properties.Day.eq(day)
                            , AccountDao.Properties.Type.eq(type))
                    .orderDesc(AccountDao.Properties.Time)
                    .build().list();
        }
    }

    /**
     * 按日期升序返回这一月的支出/收入list
     * @param type 收支类别 {@link Constants.TYPE_IN} 或 {@link Constants.TYPE_OUT}
     * Created by ly on 2017/3/11 9:43
     */
    public List<Account> queryForMonth(int type, int year, int month) {
        return accountDao.queryBuilder()
                .where(AccountDao.Properties.Year.eq(year)
                        , AccountDao.Properties.Month.eq(month)
                        , AccountDao.Properties.Type.eq(type))
                .orderAsc(AccountDao.Properties.Day)
                .build().list();
    }

    /**
     * 按月份升序返回这一年的支出/收入list
     * @param type 收支类别 {@link Constants.TYPE_IN} 或 {@link Constants.TYPE_OUT}
     * Created by ly on 2017/3/11 9:43
     */
    public List<Account> queryForYear(int type, int year) {
        return accountDao.queryBuilder()
                .where(AccountDao.Properties.Year.eq(year)
                        , AccountDao.Properties.Type.eq(type))
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
