package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.ApplicationTestCase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqliteimpl.SQLiteAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqliteimpl.SQLiteTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.dbmngr.SQLiteDB;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.MainActivity;

public class SQLiteExpenseManager extends ExpenseManager{

    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;

    public SQLiteExpenseManager(Context context) {
        super();
        transactionDAO = new SQLiteTransactionDAO(context);
        accountDAO = new SQLiteAccountDAO();
    }

    @Override
    public void setup() throws ExpenseManagerException {

        setTransactionsDAO(transactionDAO);
        setAccountsDAO(accountDAO);

        // need to add accounts like dummy data????
    }
}
