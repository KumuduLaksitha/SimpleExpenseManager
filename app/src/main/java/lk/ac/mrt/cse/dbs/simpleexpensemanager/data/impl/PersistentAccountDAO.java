package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.dbmngr.SQLiteDB;

public class PersistentAccountDAO implements AccountDAO {

    private SQLiteDB helper;

    public PersistentAccountDAO(Context context) {

        helper = new SQLiteDB(context);

    }

    @Override
    public List<String> getAccountNumbersList() {

        return helper.getAccountNoList();

    }

    @Override
    public List<Account> getAccountsList() {

        return helper.getAccountList();

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        return helper.getAccountByNo(accountNo);

    }

    @Override
    public void addAccount(Account account) {

        helper.addAccount(account);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        helper.removeAccount(accountNo);

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        double balance = helper.getBalance(accountNo);

        if (expenseType == ExpenseType.INCOME)
            balance += amount;
        else
            balance -= amount;

        helper.updateAccountBalance(accountNo, balance);

    }
}
