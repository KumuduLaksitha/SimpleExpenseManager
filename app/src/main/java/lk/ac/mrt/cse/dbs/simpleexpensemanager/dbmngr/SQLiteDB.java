package lk.ac.mrt.cse.dbs.simpleexpensemanager.dbmngr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class SQLiteDB extends SQLiteOpenHelper {

    public SQLiteDB(@Nullable Context context) {

        super(context, "expense_mgr", null, 1);

    }

    /**
     * creates database expense_mgr in the first run
     * creates tables account, transaction_log
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // account table
        String accountTableCreateQuery = "CREATE TABLE account " +
                "(account_no TEXT PRIMARY KEY, " +
                "bank_name TEXT, account_holder_name TEXT," +
                "balance REAL);";
        sqLiteDatabase.execSQL(accountTableCreateQuery);

        // transaction_log table
        String transactionTableCreateQuery = "CREATE TABLE transaction_log " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, " +
                "account_no TEXT, transaction_type INTEGER, amount REAL, " +
                "FOREIGN KEY (account_no) REFERENCES account(account_no));";
        sqLiteDatabase.execSQL(transactionTableCreateQuery);

    }

    /**
     * adds new account object to the database system
     * @param account
     * @return boolean showing success of operation
     */
    public boolean addAccount(Account account) {

        SQLiteDatabase database = this.getWritableDatabase();

        // create content value and insert it to database
        ContentValues cv = new ContentValues();
        cv.put("account_no", account.getAccountNo());
        cv.put("bank_name", account.getBankName());
        cv.put("account_holder_name", account.getAccountHolderName());
        cv.put("balance", account.getBalance());

        long returnValue = database.insert("account", null,  cv);

        // check return value for success
        if (returnValue < 0)
            return false;
        else
            return true;

    }

    /**
     * add new transaction to database
     * @param transaction
     * @return boolean showing success of the operation
     */
    public boolean addTransactionLog(Transaction transaction) {

        SQLiteDatabase database = this.getWritableDatabase();

        // create content value and insert it to database
        ContentValues cv = new ContentValues();

        // need to store date as a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String transactionDate = dateFormat.format(transaction.getDate());
        cv.put("date", transactionDate);

        cv.put("account_no", transaction.getAccountNo());

        // storing 1 (income) and 0 (expense) for Expense type in DB
        int transactionType = transaction.getExpenseType() == ExpenseType.INCOME ? 1 : 0;
        cv.put("transaction_type", transactionType);

        cv.put("amount", transaction.getAmount());

        long returnValue = database.insert("transaction_log", null, cv);

        // check return value for success
        if (returnValue < 0)
            return false;
        else
            return true;

    }

    /**
     * get list of all transactions saved in the database
     * @return arraylist of all transactions
     */
    public List<Transaction> getTransactionList() {

        List<Transaction> transactionList = new ArrayList();

        // reading values using sql query
        SQLiteDatabase database = this.getReadableDatabase();
        String readQuery = "SELECT * FROM transaction_log;";
        Cursor cursor = database.rawQuery(readQuery, null);

        // loop through cursor and fetch records
        if (cursor.moveToFirst()) {
            do {

                String transactionDate = cursor.getString(1);
                String accountNo = cursor.getString(2);
                ExpenseType expenseType = cursor.getInt(3) ==
                        1 ? ExpenseType.INCOME : ExpenseType.EXPENSE;
                double amount = cursor.getDouble(4);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date transactionDateObject = null;
                try {
                    transactionDateObject = dateFormat.parse(transactionDate);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                Transaction transaction =
                        new Transaction(transactionDateObject, accountNo, expenseType, amount);
                transactionList.add(transaction);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return transactionList;
    }

    public List<Transaction> getPaginatedTransactionList(int limit) {
        List<Transaction> paginatedTransactionList = new ArrayList();

        // reading values using sql query
        SQLiteDatabase database = this.getReadableDatabase();
        String readQuery = "SELECT * FROM transaction_log LIMIT ?;";
        Cursor cursor = database.rawQuery(readQuery, new String[] {Integer.toString(limit)});

        // loop through cursor and fetch records
        if (cursor.moveToFirst()) {
            do {

                String transactionDate = cursor.getString(1);
                String accountNo = cursor.getString(2);
                ExpenseType expenseType = cursor.getInt(3) ==
                        1 ? ExpenseType.INCOME : ExpenseType.EXPENSE;
                double amount = cursor.getDouble(4);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date transactionDateObject = null;
                try {
                    transactionDateObject = dateFormat.parse(transactionDate);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                Transaction transaction =
                        new Transaction(transactionDateObject, accountNo, expenseType, amount);
                paginatedTransactionList.add(transaction);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paginatedTransactionList;
    }

    /**
     * return list of all saved accounts
     * @return arraylist of account objects
     */
    public List<Account> getAccountList() {

        List<Account> accountList = new ArrayList<>();

        // reading values using sql query
        SQLiteDatabase database = this.getReadableDatabase();
        String readQuery = "SELECT * FROM account";
        Cursor cursor = database.rawQuery(readQuery, null);

        // loop through cursor and fetch records
        if (cursor.moveToFirst()) {
            do {

                String accountNo = cursor.getString(0);
                String bankName = cursor.getString(1);
                String holderName = cursor.getString(2);
                Double balance = cursor.getDouble(3);

                Account account = new Account(accountNo, bankName, holderName, balance);
                accountList.add(account);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return accountList;

    }

    /**
     * return arraylist of account numbers saved
     * @return
     */
    public List<String> getAccountNoList() {

        List<String> accountList = new ArrayList<>();

        // reading values using sql query
        SQLiteDatabase database = this.getReadableDatabase();
        String readQuery = "SELECT account_no FROM account";
        Cursor cursor = database.rawQuery(readQuery, null);

        // loop through cursor and fetch records
        if (cursor.moveToFirst()) {
            do {

                String accountNo = cursor.getString(0);
                accountList.add(accountNo);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return accountList;

    }

    /**
     * gives account object for a given account number
     * @param accountNo
     * @return account object
     */
    public Account getAccountByNo(String accountNo) throws InvalidAccountException{

        SQLiteDatabase database = this.getReadableDatabase();
        String getBalanceQuery = "SELECT * FROM account WHERE account_no = ?;";
        Cursor cursor = database.rawQuery(getBalanceQuery, new String[] {accountNo});

        // no need to loop
        // choosing account by primary key
        // no repetitions can happen
        if (cursor.moveToFirst()) {
            String bankName = cursor.getString(1);
            String holderName = cursor.getString(2);
            Double balance = cursor.getDouble(3);

            Account account = new Account(accountNo, bankName, holderName, balance);

            return account;
        }

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);

    }

    /**
     * removes the given account
     * @param accountNo
     * @return operation successful or not
     */
    public void removeAccount(String accountNo) throws InvalidAccountException{

        SQLiteDatabase database = this.getWritableDatabase();
        int returnValue = database.delete("account", "account_no = ?", new String[]{accountNo});

        // return value gives no of records deleted
        if (returnValue == 0) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

    }

    /**
     * get balance of a give account
     * return -1 if account not found
     * @param accountNo
     * @return balance
     */
    public double getBalance(String accountNo) throws InvalidAccountException{

        SQLiteDatabase database = this.getReadableDatabase();
        String getBalanceQuery = "SELECT balance FROM account WHERE account_no = ?;";
        Cursor cursor = database.rawQuery(getBalanceQuery, new String[] {accountNo});

        if (cursor.moveToFirst()) {
            return cursor.getDouble(0);
        }

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    /**
     * update the balance of a account using given value
     * @param accountNo
     * @param balance
     */
    public void updateAccountBalance(String accountNo, double balance) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("balance", balance);
        database.update("account", cv, "account_no = ?", new String[]{accountNo});

    }

    /**
     * this is version 1.0
     * no need of upgrade code
     * will be needed in future versions
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

}
