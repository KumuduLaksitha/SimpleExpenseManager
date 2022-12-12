package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqliteimpl;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.dbmngr.SQLiteDB;

public class SQLiteTransactionDAO implements TransactionDAO {

    private SQLiteDB helper;

    public SQLiteTransactionDAO (Context context) {

        helper = new SQLiteDB(context);

    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        helper.addTransactionLog(new Transaction(date, accountNo, expenseType, amount));

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        return helper.getTransactionList();

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        List<Transaction> list = helper.getTransactionList();

        // if list is smaller than limit
        if (list.size() < limit)
            return list;

        return list.subList(list.size() - limit, list.size());

    }
}
