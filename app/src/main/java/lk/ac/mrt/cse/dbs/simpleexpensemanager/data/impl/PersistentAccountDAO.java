package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;


import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PersistentAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    public static final String DATABASE_NAME = "170644R.db";
    public static final String TABLE_NAME = "account_table";
    public static final String COL_1 = "accountNo";
    public static final String COL_2 = "bankName";
    public static final String COL_3 = "accountHolderName";
    public static final String COL_4 = "balance";

    public PersistentAccountDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (accountNo STRING PRIMARY KEY ,bankName STRING,accountHolderName STRING, balance DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    @Override
    public  List<String> getAccountNumbersList() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select accountNo from "+TABLE_NAME,null);
        List<String> acc_no_list = new ArrayList<>();

        while (res.moveToNext()) {
            acc_no_list.add(res.getString(0));
        }
        return acc_no_list;
    }

    @Override
    public  List<Account> getAccountsList() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        List<Account> accountList = new ArrayList<>();

        while (res.moveToNext()) {
            Account a = new Account(res.getString(0), res.getString(1), res.getString(2), res.getDouble(3));
            accountList.add(a);
        }
        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+"WHERE ="+accountNo,null);

        while (res.moveToNext()) {
            Account a = new Account(res.getString(0), res.getString(1), res.getString(2), res.getDouble(3));
            return  a;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,account.getAccountNo());
        contentValues.put(COL_2,account.getBankName());
        contentValues.put(COL_3,account.getAccountHolderName());
        contentValues.put(COL_4,account.getBalance());
        long result = db.insert(TABLE_NAME,null ,contentValues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "accountNo = ?",new String[] {accountNo});
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account a = getAccount(accountNo);

        if (a != null) {
            switch (expenseType) {
                case EXPENSE:
                    a.setBalance(a.getBalance() - amount);
                    break;
                case INCOME:
                    a.setBalance(a.getBalance() + amount);
                    break;
            }
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_1, a.getAccountNo());
            contentValues.put(COL_2, a.getBankName());
            contentValues.put(COL_3, a.getAccountHolderName());
            contentValues.put(COL_4, a.getBalance());
            db.update(TABLE_NAME, contentValues, "accountNo = ?", new String[]{id});
        }
        else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }


    }