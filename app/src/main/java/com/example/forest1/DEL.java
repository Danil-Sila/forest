package com.example.forest1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DEL {
    private static final String DB_NAME = "database01";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE_DEL = "del";
    private static final String DB_TABLE_KUB = "kub";
    private static final String DB_TABLE_SRAZR = "srazr";

    //поля для делянки
    public static final String DEL_ID = "_id";
    public static final String DEL_KUB_SRAZRID = "kubID";
    public static final String DEL_KOLDEL = "koldel";
    public static final String DEL_KOLDR = "koldr";
    public static final String DEL_VDEL = "vdel";
    public static final String DEL_VDR = "vdr";
    public static final String DEL_ITOG = "itog";

    //поля для таблицы отвода деревьев под рубку
    public static final String KUB_ID = "_id";
    public static final String KUB_SRAZR_ID = "srazrID";
    public static final String KUB_D = "d";
    public static final String KUB_KAT = "kat";
    public static final String KUB_V = "v";

    //поля для таблицы средних разрядов высот
    public static final String SRAZR_ID = "_id";
    public static final String SRAZR_NUMD = "numd";
    public static final String SRAZR_Poroda = "poroda";
    public static final String SRAZR_SRazr = "sraz";


    //таблица для информации о делянке
    private static final String DB_CREATE_DEL =
            "create table " + DB_TABLE_DEL + "(" +
                    DEL_ID + " integer primary key autoincrement, " +
                    DEL_KUB_SRAZRID + " integer, " +
                    DEL_KOLDEL+ " integer, " +
                    DEL_KOLDR+ " integer, " +
                    DEL_VDEL+ " real, " +
                    DEL_VDR+ " real, " +
                    DEL_ITOG+ " real, " +
                    "FOREIGN KEY ("+DEL_KUB_SRAZRID+") REFERENCES "+DB_TABLE_KUB+"("+KUB_SRAZR_ID+"));";

    //таблица для отвода деревьев под рубку
    private static final String DB_CREATE_KUB =
            "create table " + DB_TABLE_KUB + "(" +
                    KUB_ID + " integer primary key autoincrement, "+
                    KUB_SRAZR_ID + " integer, "+
                    KUB_D + " integer, "+
                    KUB_KAT + " text, "+
                    KUB_V + " real, "+
                    "FOREIGN KEY ("+KUB_SRAZR_ID+") REFERENCES "+DB_TABLE_SRAZR+"("+SRAZR_ID+"));";

    //таблица для хранения ср.разрядов высот
    private static final String DB_CREATE_SRAZR =
            "create table " + DB_TABLE_SRAZR + "(" +
                    SRAZR_ID + " integer primary key autoincrement, "+
                    SRAZR_NUMD + " integer, "+
                    SRAZR_Poroda + " text, "+
                    SRAZR_SRazr + " integer"+
                    ");";

    private final Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DEL(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE_DEL
    public Cursor getDataDel() {
        //String table = "del as d INNER JOIN kub as k on d.kubID = k.srazrID INNER JOIN srazr as s on k.srazrID = s._id";
        String table = "del as d INNER JOIN srazr as s on d.kubID = s._id";
        String[] columns = {" d._id, s.numd as numd, s.poroda as poroda, d.koldel as koldel, d.koldr as koldr, d.vdel as vdel, d.vdr as vdr, d.itog as itog"};
        String groupBy = "s.poroda, s.numd";
        String orderBy = "d._id desc";
        return mDB.query(table, columns, null, null, null, null, orderBy);
    }

    // получить все данные из таблицы DB_TABLE_SRAZR
    public Cursor getDataSRazr() {
        return mDB.query(DB_TABLE_SRAZR,null,null,null,null,null,null);
    }

    // получить все данные таблицы DB_TABLE_SRAZR по породе
    public Cursor getPorodaSRazr(String poroda, Integer numD) {
        Cursor c;
        c = mDB.rawQuery("SELECT * from "+ DB_TABLE_SRAZR +" WHERE poroda = " + "'" + poroda + "' and numd = " + numD,null);
        return c;
    }

    // получить все данные из таблицы DB_TABLE_KUB
    public Cursor getAllDataKub(int numd) {
        //return mDB.query(DB_TABLE_KUB, null, null, null, null, null, null);
        Cursor c;
        c = mDB.rawQuery("SELECT * from kub" ,null);
      //  c = mDB.rawQuery("SELECT s.poroda, d, s.sraz, kat, v from kub LEFT JOIN srazr as s ON srazrID = s._id" ,null);
      //  c = mDB.rawQuery("select * from kub LEFT JOIN srazr as s on srazrID = s._id where numd = "+numd ,null);
        return c;
    }

    public Cursor getAllKub(String num) {
        String table = "kub as k INNER JOIN srazr as s on k.srazrID = s._id";
      //  String[] columns = {"k._id, s.poroda as Poroda, k.d as D, s.sraz as Sraz, k.kat as Kat, k.v as V, s.numd as Numd"};
        String[] columns = {"k._id, s.poroda as poroda, k.d as d, s.sraz as sraz, k.kat as kat, k.v as v, s.numd as Numd"};
        String selection = "Numd = ?";
        String[] selectionArgs = {num};
        return mDB.query(table, columns, selection, selectionArgs, null, null, null);
        //return mDB.rawQuery("select s.poroda as Poroda, k.d as D, s.sraz as Sraz, k.kat as Kat, k.v as V from kub as k INNER JOIN srazr as s on k.srazrID = s._id" ,null);
    }

    public int getIdSrazr(int numd, String poroda){
        int id=0;
        Cursor c;
        c = mDB.rawQuery("SELECT "+SRAZR_ID+" FROM "+DB_TABLE_SRAZR+" WHERE "+SRAZR_Poroda+" = '"+poroda+"' and "+SRAZR_NUMD+" = '"+numd+"'",null);
        c.moveToFirst();
        id = c.getInt(0);
        return id;
    }

    //удалить все записи из DB_TABLE_DEL
    public void delRecDel() {
        mDB.delete(DB_TABLE_DEL, null,null);
    }


    //удалить все записи из DB_TABLE_SRAZR
    public void delRecSRazr() {
        mDB.delete(DB_TABLE_SRAZR, null,null);
    }

    //удалить все записи из DB_TABLE_KUB
    public void delRecKub() {
        mDB.delete(DB_TABLE_KUB, null,null);
    }

    // удалить запись из DB_TABLE_DEL
    public void delRecDel(long id) {
        mDB.delete(DB_TABLE_DEL, DEL_ID + " = " + id, null);
    }

    // удалить запись из DB_TABLE_KUB
    public void delRecKub(long id) {
        mDB.delete(DB_TABLE_KUB, KUB_ID + " = " + id, null);//
    }

    //удалить записи из таблицы отвод с введённым номером делянки
    public void delRecKubNum(int num){
        Cursor c = null;
        c = mDB.rawQuery("SELECT k._id FROM kub as k, srazr as s WHERE s._id = k.srazrID AND s.numd = "+num, null);
        if (c.moveToFirst()){
            do {
                mDB.delete(DB_TABLE_KUB, KUB_ID + " = " + c.getInt(0), null);
            }while(c.moveToNext());
        }
    }

//получение расчитаннох данных по породам их категориям и объёмам
    public Cursor getDataForDel(String Poroda, Integer numD){
        Cursor c;
        String sql = "select srazr.numD, srazr.poroda, kub.kat , COUNT(kub.kat), SUM(kub.v), kub.srazrID from srazr, kub where kub.srazrID = srazr._id and srazr.poroda = "+"'"+ Poroda+"'" +" and srazr.numd= "+ numD +" group by srazr.poroda, kub.kat";
        return c = mDB.rawQuery(sql, null);
    }


    //поиск и проверка в таблице средних разрядов на наличие делянки
    public boolean findNumDSrazr(int num){
        Cursor c = null;
        boolean f=false;
        Integer n;
        c = getDataSRazr();
        if (c.moveToFirst()){
            do {
                n = c.getInt(1);
                if (n == num) {
                    f = true;
                    break;
                }
            }while(c.moveToNext());
        }
        return f;
    }


    //получение породы из среднего разряда(1) и отвода деревьев под рубку(2)
    public ArrayList<String> getPorodaSR(int numD, int v){
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        db.beginTransaction();
        String Poroda="", selectPoroda="";
        Cursor cursor;
        try{
            if (v==1) selectPoroda = "SELECT * FROM srazr where numd = "+numD+" GROUP BY poroda";
            if (v==2) selectPoroda = "SELECT s.poroda FROM kub as k, srazr as s where s.numd = "+numD+" and k.srazrID = s._id GROUP BY poroda";
            cursor = db.rawQuery(selectPoroda,null);
            if(cursor.moveToFirst()){
                do {
                    if (v==2) Poroda = cursor.getString(0);
                    if (v==1) Poroda = cursor.getString(2);
                    list.add(Poroda);
                }while(cursor.moveToNext());
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            //db.close();
        }
        return list;
    }

    //добавление записи о среднем разряде
 /*   public void addSrazr(){
        Cursor c1, c2;
        String poroda;
        ContentValues cv = new ContentValues();
        c1 = mDB.rawQuery("select " + RAZR_PORODA + " from " + DB_TABLE_RAZR + " GROUP BY " + RAZR_PORODA,null);
        if (c1.moveToFirst()){
            do {
                poroda = c1.getString(0);
                c2 = mDB.rawQuery("select numd, poroda, sum(raz)/count(numd) from razr WHERE poroda = "+"'"+ poroda +"'",null);
                if (c2.moveToFirst()){
                    do {
                        cv.put(SRAZR_NUMD, c2.getString(0));
                        cv.put(SRAZR_Poroda, c2.getString(1));
                        cv.put(SRAZR_SRazr, c2.getString(2));
                        mDB.insert(DB_TABLE_SRAZR,null,cv);
                      // Toast.makeText(mCtx, c2.getString(0) + " " + c2.getString(1) + " " + c2.getString(2), Toast.LENGTH_SHORT).show();
                    }while(c2.moveToNext());
                    c2.close();
                }
            }while (c1.moveToNext());
        }
        c1.close();
    }
*/
    //добавление данных в таблицу со средними разрядами высот srazr
    public void addSrazr(int num, String poroda, int srazr){
        ContentValues cv = new ContentValues();
        cv.put(SRAZR_NUMD, num);
        cv.put(SRAZR_Poroda, poroda);
        cv.put(SRAZR_SRazr, srazr);
        mDB.insert(DB_TABLE_SRAZR,null,cv);
    }
    //добавить запись в del
    public void addDel(int kubID, int kolDel, int kolDr, double vDel, double vDr, double itog){
        ContentValues cv = new ContentValues();
        cv.put(DEL_KUB_SRAZRID, kubID);
        cv.put(DEL_KOLDEL, kolDel);
        cv.put(DEL_KOLDR, kolDr);
        cv.put(DEL_VDEL, vDel);
        cv.put(DEL_VDR, vDr);
        cv.put(DEL_ITOG, itog);
        mDB.insert(DB_TABLE_DEL, null, cv);
    }

    //обновить записи в таблице
    public void updateDel(int kubID, int kolDel, int kolDr, double vDel, double vDr, double itog){
        String id;
        id = String.valueOf(kubID);
        ContentValues cv = new ContentValues();
        cv.put(DEL_KUB_SRAZRID, kubID);
        cv.put(DEL_KOLDEL, kolDel);
        cv.put(DEL_KOLDR, kolDr);
        cv.put(DEL_VDEL, vDel);
        cv.put(DEL_VDR, vDr);
        cv.put(DEL_ITOG, itog);
        //mDB.insert(DB_TABLE_DEL, null, cv);
        mDB.update(DB_TABLE_DEL, cv, DEL_KUB_SRAZRID + "=?",new String[]{id});
    }

    //получение id о номере делянки
    public int getDEL_SRAZRID(long id){
        Cursor c;
        int getID=0;
        c = getDataDel();
        if (c.moveToFirst()){
            do{

                if (c.getInt(0) == id) {
                    getID = c.getInt(1);
                    break;
                }
            }while(c.moveToNext());
        }
        return getID;
    }

    //добавление записи в DB_TABLE_KUB
    public void addKUB(int srazrID, int d, String kat, double v){
        ContentValues cv = new ContentValues();
        cv.put(KUB_SRAZR_ID, srazrID);
        cv.put(KUB_D, d);
        cv.put(KUB_KAT, kat);
        cv.put(KUB_V, v);
        mDB.insert(DB_TABLE_KUB, null, cv);
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_DEL);
            db.execSQL(DB_CREATE_KUB);
            db.execSQL(DB_CREATE_SRAZR);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          //  onCreate(db);
        }
    }
}
