package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqliteimpl;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.dbmngr.SQLiteDB;

public class SQLiteAccountDAO implements AccountDAO {

    private SQLiteDB helper;

    public SQLiteAccountDAO(Context context) {

        helper = new SQLiteDB(context);

    }

    @Override
    public List<String> getAccountNumbersList() {

        List<String> accountNoList = new ArrayList<>();

        List<Account> list = helper.getAccountList();
        for (Account account : list) {
            accountNoList.add(account.getAccountNo());
        }

        return accountNoList;

    }

    @Override
    public List<Account> getAccountsList() {

        return helper.getAccountList();

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        List<Account> list = helper.getAccountList();

        for (Account account : list) {
            if (account.getAccountNo() == accountNo)
                return account;
        }

        return null;

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
