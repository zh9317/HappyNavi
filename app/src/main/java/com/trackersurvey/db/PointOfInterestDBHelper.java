package com.trackersurvey.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.trackersurvey.bean.PointOfInterestData;

import java.util.ArrayList;

/**
 * Created by zh931 on 2018/5/13.
 * 存放添加兴趣点下拉列表内容的数据库
 * @author zhanghao
 */

public class PointOfInterestDBHelper {
    private static final String DATABASE_NAME = "POI_DB"; // 数据库名字
    private static final int DATABASE_VERSION = 1; // 数据库版本号
    // private static final String TABLE1_NAME="MOOD"; //心情状态表
    private static final String TABLE1_NAME = "DURATION"; // 停留时长表
    private static final String TABLE2_NAME = "BEHAVIOUR"; // 行为类型表
    private static final String TABLE3_NAME = "PARTNERNUM"; // 同伴人数表
    private static final String TABLE4_NAME = "RELATION"; // 同伴关系表
    // private static final String[] COLUMNS={"No","Mood"};
    private static final String[] COLUMNS1 = { "No", "Duration" };
    private static final String[] COLUMNS2 = { "No", "Behaviour" };
    private static final String[] COLUMNS3 = { "No", "PartnerNum" };
    private static final String[] COLUMNS4 = { "No", "PartnerRelation" };
    private DBOpenHelper dbhelper = null;
    private ArrayList<String> duration, behaviour, partnerNum, relation;

    private static class DBOpenHelper extends SQLiteOpenHelper {
        // 创建4张表
        private static final String CREATE_TABLE1 = "create table " + TABLE1_NAME + "(" + COLUMNS1[0]
                + " integer NOT NULL ," + COLUMNS1[1] + " text)";
        private static final String CREATE_TABLE2 = "create table " + TABLE2_NAME + "(" + COLUMNS2[0]
                + " integer not null ," + COLUMNS2[1] + " text)";
        private static final String CREATE_TABLE3 = "create table " + TABLE3_NAME + "(" + COLUMNS3[0]
                + " integer NOT NULL ," + COLUMNS3[1] + " text)";
        private static final String CREATE_TABLE4 = "create table " + TABLE4_NAME + "(" + COLUMNS4[0]
                + " integer NOT NULL ," + COLUMNS4[1] + " text)";
        // private static final String CREATE_TABLE5="create table
        // "+TABLE5_NAME+"("+COLUMNS5[0]+
        // " integer NOT NULL ,"+COLUMNS5[1]+" text)";

        private static DBOpenHelper helper = null;

        public DBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        public static DBOpenHelper getInstence(Context context) {
            if (null == helper) {
                helper = new DBOpenHelper(context);
            }
            return helper;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            // db.execSQL("create table MOOD(_id integer primary key
            // autoincrement,Mood text)");
            db.execSQL(CREATE_TABLE1);
            db.execSQL(CREATE_TABLE2);
            db.execSQL(CREATE_TABLE3);
            db.execSQL(CREATE_TABLE4);
            // db.execSQL(CREATE_TABLE5);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("drop table if exists " + TABLE1_NAME);// 删除旧版表格
            db.execSQL("drop table if exists " + TABLE2_NAME);// 删除旧版表格
            db.execSQL("drop table if exists " + TABLE3_NAME);// 删除旧版表格
            db.execSQL("drop table if exists " + TABLE4_NAME);// 删除旧版表格
            // db.execSQL("drop table if exists "+TABLE5_NAME);//删除旧版表格
        }
    }
    public PointOfInterestDBHelper(Context context) {
        dbhelper = DBOpenHelper.getInstence(context);
    }

    public void insertDuration(PointOfInterestData data) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMNS1[0], data.getKey());
            values.put(COLUMNS1[1], data.getValue());
            db.insert(TABLE1_NAME, null, values);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            db.close();
        }
        db.close();
    }
    public void insertBehaviour(PointOfInterestData data) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMNS2[0], data.getKey());
            values.put(COLUMNS2[1], data.getValue());
            db.insert(TABLE2_NAME, null, values);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            db.close();
        }
        db.close();
    }
    public void insertPartnerNum(PointOfInterestData data) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMNS3[0], data.getKey());
            values.put(COLUMNS3[1], data.getValue());
            db.insert(TABLE3_NAME, null, values);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            db.close();
        }
        db.close();
    }
    public void insertPartnerRelation(PointOfInterestData data) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMNS4[0], data.getKey());
            values.put(COLUMNS4[1], data.getValue());
            db.insert(TABLE4_NAME, null, values);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            db.close();
        }
        db.close();
    }
    public void delete(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.execSQL("delete from DURATION");
        db.execSQL("delete from BEHAVIOUR");
        db.execSQL("delete from PARTNERNUM");
        db.execSQL("delete from RELATION");
        db.close();
    }
    public ArrayList<String> getDuration(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        duration = new ArrayList<String>();
        //Cursor cursor = db.query(TABLE1_NAME, null, "No="+num, null, null, null, null);
        Cursor cursor = db.rawQuery("select * from DURATION", null);
        int row = cursor.getCount();
        if(row!=0){
            cursor.moveToFirst();
            for(int i = 0;i<cursor.getCount();i++){
                //取索引为1的列的数据
                duration.add(cursor.getString(1));
                //Log.i("cursor", cursor.getString(1));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return duration;
    }
    public ArrayList<String> getBehaviour(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        behaviour = new ArrayList<String>();
        //Cursor cursor = db.query(TABLE2_NAME, null, "No="+num, null, null, null, null);
        Cursor cursor = db.rawQuery("select * from BEHAVIOUR", null);
        int row = cursor.getCount();
        if(row!=0){
            cursor.moveToFirst();
            for(int i = 0;i<cursor.getCount();i++){
                behaviour.add(cursor.getString(1));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return behaviour;
    }
    public ArrayList<String> getPartnerNum(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        partnerNum = new ArrayList<String>();
        //Cursor cursor = db.query(TABLE3_NAME, null, "No="+num, null, null, null, null);
        Cursor cursor = db.rawQuery("select * from PARTNERNUM", null);
        int row = cursor.getCount();
        if(row!=0){
            cursor.moveToFirst();
            for(int i = 0;i<cursor.getCount();i++){
                partnerNum.add(cursor.getString(1));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return partnerNum;
    }
    public ArrayList<String> getRelation(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        relation = new ArrayList<String>();
        //Cursor cursor = db.query(TABLE4_NAME, null, "No="+num, null, null, null, null);
        Cursor cursor = db.rawQuery("select * from RELATION", null);
        int row = cursor.getCount();
        if(row!=0){
            cursor.moveToFirst();
            for(int i = 0;i<cursor.getCount();i++){
                relation.add(cursor.getString(1));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return relation;
    }
    public Cursor select(String[] columns, String selection,
                         String[] selectionArgs, String groupBy, String having,
                         String orderBy){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE1_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
        return cursor;
    }
}
