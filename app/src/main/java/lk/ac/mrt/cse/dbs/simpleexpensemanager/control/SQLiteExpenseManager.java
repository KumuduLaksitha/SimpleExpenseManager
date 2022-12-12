package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.database.sqlite.SQLiteOpenHelper;
import android.test.ApplicationTestCase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqliteimpl.SQLiteAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqliteimpl.SQLiteTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.dbmngr.SQLiteDB;

public class SQLiteExpenseManager extends ExpenseManager{
    @Override
    public void setup() throws ExpenseManagerException {

        TransactionDAO transactionDAO = new SQLiteTransactionDAO();
        setTransactionsDAO(transactionDAO);

        AccountDAO accountDAO = new SQLiteAccountDAO();
        setAccountsDAO(accountDAO);

        // need to add accounts like dummy data????
    }
}
