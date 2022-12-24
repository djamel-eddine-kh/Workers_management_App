package com.example.workersmanagement.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.workersmanagement.Model.Worker;

public class DBhandler extends SQLiteOpenHelper {
    private static final String db_name="workers";

    public DBhandler(@Nullable Context context) {
        super(context,/* */db_name, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE worker(_id INTEGER ,Id LONG PRIMARY KEY not null,Last_name TEXT,First_name TEXT," +
                "Phone_number TEXT,Email TEXT,Field TEXT,Date DATE,Image BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop Table IF EXISTS worker");
        onCreate(db);

    }
    public void addWorker(Worker e){
        //permet d’insérer l’employé e dans la base de données SQLite.
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("Id",e.getId());
        cv.put("Last_name",e.getLast_name());
        cv.put("First_name",e.getFirst_name());
        cv.put("Phone_number",e.getPhone_number());
        cv.put("Email",e.getEmail());
        cv.put("Field",e.getField());
        cv.put("Date", e.getDate());
        cv.put("Image", e.getImage());
        db.insert("worker",null,cv);
        db.close();//un vide de protection
    }
    public void updateWorker(Worker e){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("Last_name",e.getLast_name());
        cv.put("First_name",e.getFirst_name());
        cv.put("Phone_number",e.getPhone_number());
        cv.put("Email",e.getEmail());
        cv.put("Field",e.getField());
        cv.put("Date", e.getDate());
        cv.put("Image", e.getImage());
        db.update("worker",cv,"Id=?",new String[]{String.valueOf(e.getId())});
        db.close();
    }
    public void deleteWorker(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete("worker","Id=?",new String[]{id});
        db.close();
    }
    public Cursor getWorkers(){
        SQLiteDatabase db=this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM worker",null);
    }
    public Worker getWorker(String id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c= db.rawQuery("SELECT * FROM worker WHERE Id=?",new String[]{String.valueOf(id)});
        if (c.getCount()==0)

            return null;

        c.moveToFirst();
        Worker e=new Worker(c.getLong(1),c.getString(2),c.getString(3),c.getString(4),
                c.getString(5),c.getString(6),c.getString(7),c.getBlob(8));
        c.moveToFirst();
        c.close();
        return e;
    }
}
