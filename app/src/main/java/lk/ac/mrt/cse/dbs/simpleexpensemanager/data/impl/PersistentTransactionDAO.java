package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.dbmngr.SQLiteDB;

public class PersistentTransactionDAO implements TransactionDAO {

    private SQLiteDB helper;

    public PersistentTransactionDAO(Context context) {

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

        return helper.getPaginatedTransactionList(limit);

    }
}
