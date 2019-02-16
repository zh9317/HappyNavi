package com.trackersurvey.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.trackersurvey.model.GpsData;
import com.trackersurvey.bean.PhoneEventsData;
import com.trackersurvey.model.StepData;
import com.trackersurvey.model.TraceData;

import java.util.ArrayList;

/**
 * Created by zh931 on 2018/5/24.
 * 轨迹相关数据库
 */

public class MyTraceDBHelper {
    private static final String DATABASE_NAME = "GPS_DB";  //数据库名称
    private static final int DATABASE_VERSION = 6;             //数据库版本号
    private static final String TABLE1_NAME = "LOCATION";   //位置信息表
    private static final String TABLE2_NAME = "TRAIL";      //轨迹记录表
    private static final String TABLE3_NAME = "EVENTS";    //事件表
    private static final String TABLE4_NAME = "STEPS";	 //步数表
    private static final String TABLE5_NAME = "TRAILSTATUS"; //轨迹状态表，只存储未上传和记录中断的轨迹:0 正常，已上传；1：记录中断；2：记录正常结束但未上传
    private static final String[] COLUMNS = {"userID","CreateTime","Longitude","Latitude","Altitude","Speed","TraceID","CityID","DeviceID","SportType"};
    private static final String[] COLUMNS2 = {"userID","TraceName","TraceID","StartTime","EndTime","Duration","Distance","SportType","ShareType","Calorie","PoiCount", "Steps"};
    private static final String[] COLUMNS3 = {"userID","CreateTime","EventType","Longitude","Latitude","Altitude"};
    private static final String[] COLUMNS4 = {"userID","TraceID","Steps"};
    private static final String[] COLUMNS5 = {"userID","TraceID","TraceStatus"};
    private DBOpenHelper dbhelper = null;
    //private SQLiteDatabase db;
    private static class DBOpenHelper extends SQLiteOpenHelper {
        private static final String CREATE_TABLE1="create table "+TABLE1_NAME+"("+COLUMNS[0]+
                " integer NOT NULL ,"+COLUMNS[1]+" TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),"
                +COLUMNS[2]+" real ,"+COLUMNS[3]+" real ,"+COLUMNS[4]+" real ,"+COLUMNS[5]+" real ,"+COLUMNS[6]+" integer ,"+
                COLUMNS[7] + " integer ,"+COLUMNS[8] + " text ," + COLUMNS[9] + " integer );";
        private static final String CREATE_TABLE2="create table "+TABLE2_NAME+"("+COLUMNS2[0]+
                " integer not null ,"+COLUMNS2[1]+" text ,"+COLUMNS2[2]+" integer ,"+COLUMNS2[3]+" TimeStamp not null ,"
                +COLUMNS2[4]+" TimeStamp not null ,"+COLUMNS2[5]+" integer ,"+COLUMNS2[6]+" real ,"+COLUMNS2[7]+" integer ,"
                +COLUMNS2[8]+" integer ,"+COLUMNS2[9]+ " integer default 0 ,"+COLUMNS2[10]+ " integer default 0 ,"
                +COLUMNS2[11]+" integer default 0 )";
        private static final String CREATE_TABLE3="create table "+TABLE3_NAME+"("+COLUMNS3[0]+
                " integer NOT NULL ,"+COLUMNS3[1]+" TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),"
                +COLUMNS3[2]+" integer ,"+COLUMNS3[3]+" real ,"+COLUMNS3[4]+" real ,"+COLUMNS3[5]+" real );";
        private static final String CREATE_TABLE4="create table "+TABLE4_NAME+"("+COLUMNS4[0]+
                " integer NOT NULL ,"+COLUMNS4[1]+" integer NOT NULL ,"+COLUMNS4[2]+" integer  );";
        private static final String CREATE_TABLE5="create table "+TABLE5_NAME+"("+COLUMNS5[0]+
                " integer NOT NULL ,"+COLUMNS5[1]+" integer NOT NULL ,"+COLUMNS5[2]+" integer  NOT NULL DEFAULT ('1'));";

        private static DBOpenHelper helper=null;

        private DBOpenHelper(Context context){
            super(context, DATABASE_NAME,null,DATABASE_VERSION);
        }
        public static DBOpenHelper getInstance(Context context){
            if (null == helper)
            {
                helper = new DBOpenHelper(context);
            }

            return helper;
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(CREATE_TABLE1);
            db.execSQL(CREATE_TABLE2);
            db.execSQL(CREATE_TABLE3);
            db.execSQL(CREATE_TABLE4);
            db.execSQL(CREATE_TABLE5);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            //db.execSQL("drop table if exists "+TABLE1_NAME);//删除旧版表格
            //db.execSQL("drop table if exists "+TABLE2_NAME);//删除旧版表格
            //db.execSQL("drop table if exists "+TABLE3_NAME);//删除旧版表格
            //db.execSQL("drop table if exists "+TABLE4_NAME);//删除旧版表格
            if(oldVersion == 1){//增加计步表
                db.execSQL(CREATE_TABLE4);
                db.execSQL(CREATE_TABLE5);
                db.execSQL("ALTER TABLE "+TABLE2_NAME+" RENAME TO "+TABLE2_NAME+"_TEMP");
                db.execSQL(CREATE_TABLE2);
                //{"userID","TraceName","TraceNo","StartTime","EndTime","Duration","Distance","SportType","ShareType","Calorie"}
                db.execSQL("insert into "+TABLE2_NAME+"(userID,TraceName,TraceNo,"
                        + "StartTime,EndTime,Duration,Distance,SportType,ShareType"
                        + ") "+"select userID,TraceName,TraceNo,StartTime,EndTime,"
                        + "Duration,Distance,SportType,ShareType from "+TABLE2_NAME+"_TEMP");
                db.execSQL("drop table "+TABLE2_NAME+"_TEMP");
            }
            else if(oldVersion == 2){
                db.execSQL(CREATE_TABLE5);
                db.execSQL("ALTER TABLE "+TABLE2_NAME+" RENAME TO "+TABLE2_NAME+"_TEMP");
                db.execSQL(CREATE_TABLE2);
                //{"userID","TraceName","TraceNo","StartTime","EndTime","Duration","Distance","SportType","ShareType","Calorie"}
                db.execSQL("insert into "+TABLE2_NAME+"(userID,TraceName,TraceNo,"
                        + "StartTime,EndTime,Duration,Distance,SportType,ShareType"
                        + ") "+"select userID,TraceName,TraceNo,StartTime,EndTime,"
                        + "Duration,Distance,SportType,ShareType from "+TABLE2_NAME+"_TEMP");
                db.execSQL("drop table "+TABLE2_NAME+"_TEMP");
            }
            else if(oldVersion == 3){//轨迹描述里新增卡路里指标
                db.execSQL("ALTER TABLE "+TABLE2_NAME+" RENAME TO "+TABLE2_NAME+"_TEMP");
                db.execSQL(CREATE_TABLE2);
                //{"userID","TraceName","TraceNo","StartTime","EndTime","Duration","Distance","SportType","ShareType","Calorie"}
                db.execSQL("insert into "+TABLE2_NAME+"(userID,TraceName,TraceNo,"
                        + "StartTime,EndTime,Duration,Distance,SportType,ShareType"
                        + ") "+"select userID,TraceName,TraceNo,StartTime,EndTime,"
                        + "Duration,Distance,SportType,ShareType from "+TABLE2_NAME+"_TEMP");
                db.execSQL("drop table "+TABLE2_NAME+"_TEMP");
            }
            else if(oldVersion == 4){//轨迹描述里新增兴趣点个数指标
                db.execSQL("ALTER TABLE "+TABLE2_NAME+" RENAME TO "+TABLE2_NAME+"_TEMP");
                db.execSQL(CREATE_TABLE2);
                //{"userID","TraceName","TraceNo","StartTime","EndTime","Duration","Distance","SportType","ShareType","Calorie","PoiCount"}
                db.execSQL("insert into "+TABLE2_NAME+"(userID,TraceName,TraceNo,"
                        + "StartTime,EndTime,Duration,Distance,SportType,ShareType"
                        + ") "+"select userID,TraceName,TraceNo,StartTime,EndTime,"
                        + "Duration,Distance,SportType,ShareType from "+TABLE2_NAME+"_TEMP");
                db.execSQL("drop table "+TABLE2_NAME+"_TEMP");
            }
            // 这次修改了部分字段的名字，修改比较麻烦。。。2018年5月记。
            else if (oldVersion == 5) {
                // 表1(位置信息表)的TraceNo字段名改为TraceID，添加新字段：CityID,DeviceID,SportType
                db.execSQL("ALTER TABLE "+TABLE1_NAME+" RENAME TO "+TABLE1_NAME+"_TEMP");
                db.execSQL(CREATE_TABLE1);
                db.execSQL("insert into "+TABLE1_NAME+"(userID,CreateTime,Longitude," +
                        "Latitude,Altitude,Speed,TraceID)"+"select userID,CreateTime," +
                        "Longitude,Latitude,Altitude,Speed,TraceNo from "+TABLE1_NAME+"_TEMP");
                db.execSQL("drop table " + TABLE1_NAME + "_TEMP");

                // 表2(轨迹记录表)的TraceNo字段名改为TraceID，添加一个新字段(列)：Steps
                db.execSQL("ALTER TABLE "+TABLE2_NAME+" RENAME TO "+TABLE2_NAME+"_TEMP");
                db.execSQL(CREATE_TABLE2);
                db.execSQL("insert into "+TABLE2_NAME+"(userID,TraceName,TraceID,StartTime," +
                        "EndTime,Duration,Distance,SportType,ShareType,Calorie,PoiCount)"
                        +"select userID,TraceName,TraceNo,StartTime,EndTime,Duration,Distance," +
                        "SportType,ShareType,Calorie,PoiCount from " + TABLE2_NAME+"_TEMP");
                db.execSQL("drop table " + TABLE2_NAME + "_TEMP");

                // 表4(步数表)的TraceNo字段名改为TraceID
                db.execSQL("ALTER TABLE "+TABLE4_NAME+" RENAME TO "+TABLE4_NAME+"_TEMP");
                db.execSQL(CREATE_TABLE4);
                db.execSQL("insert into "+TABLE4_NAME+"(userID,TraceID,Steps)"+
                        "select userID,TraceNo,Steps from "+TABLE4_NAME+"_TEMP");
                db.execSQL("drop table " + TABLE4_NAME + "_TEMP");

                // 表5(位置数据表)的TraceNo字段名改为TraceID
                db.execSQL("ALTER TABLE "+TABLE5_NAME+" RENAME TO "+TABLE5_NAME+"_TEMP");
                db.execSQL(CREATE_TABLE5);
                db.execSQL("insert into "+TABLE5_NAME+"(userID,TraceID,TraceStatus)"+
                        "select userID,TraceNo,TraceStatus from "+TABLE5_NAME+"_TEMP");
                db.execSQL("drop table " + TABLE5_NAME + "_TEMP");
            }
            //onCreate(db);                  //创建表格
        }

    }
    public MyTraceDBHelper(Context context){
        dbhelper = DBOpenHelper.getInstance(context);
        //db=dbhelper.getWritableDatabase();  //获得可写的数据库
    }


    public boolean isGpsExist(String creatTime){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE1_NAME, null,
                "  datetime(createTime)=datetime('"+creatTime+"')", null, null, null, null);
        if(cursor.getCount()>0){
            cursor.close();
            db.close();
            return true;
        }
        else{
            cursor.close();
            db.close();
            return false;
        }
    }

    public boolean isGpsExists(long TraceID) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE1_NAME, null, " TraceID = " + TraceID, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return true;
        }else {
            cursor.close();
            db.close();
            return false;
        }
    }

    public boolean isTraceExists(long traceID, String userID){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Log.i("MyTraceDB isTraceExists", "traceID:" + traceID + "; userID:" + userID);
        Cursor cursor=db.query(TABLE2_NAME, null,
                " userID='"+userID+"'"+" and  TraceID=('"+traceID+"')", null, null, null, null);
        int rows = cursor.getCount();
        Log.i("MyTraceDB isTraceExists", "rows : " + rows);
        if (rows > 0) {
            Log.i("MyTraceDB isTraceExists", "存在这条轨迹，traceID:" + traceID);
        }
        if(rows>0){
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }
    public boolean isStepExists(long traceID, String userID){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor=db.query(TABLE4_NAME, null,
                " userID='"+userID+"'"+" and  TraceID=('"+traceID+"')", null, null, null, null);
        int rows=cursor.getCount();
        if(rows>0){
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }
    public boolean isStatusExists(long num,String userID){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor=db.query(TABLE5_NAME, null,
                " userID='"+userID+"'"+" and  TraceID=('"+num+"')", null, null, null, null);
        int rows=cursor.getCount();
        if(rows>0){
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }
    // 获得记录未结束的轨迹号
    public long getUnStopStatusExists(String userID){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE5_NAME, null,
                " userID='"+userID+"'"+" and  TraceStatus=('"+1+"')", null, null, null, null);
        int rows = cursor.getCount();
        long traceID = 0;
        if(rows > 0){
            cursor.moveToLast();
            traceID = cursor.getLong(1);
        }

        cursor.close();
        db.close();
        return traceID;
    }
    public ArrayList<Long> getUnUploadStatusExists(String userID){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        ArrayList<Long> traceno_up=new ArrayList<Long>();
        Cursor cursor=db.query(TABLE5_NAME, null,
                " userID='"+userID+"'"+" and  TraceStatus=('"+2+"')", null, null, null, null);
        int rows=cursor.getCount();

        if(rows>0){
            cursor.moveToFirst();
            for(int i=0;i<rows;i++){
                traceno_up.add(cursor.getLong(1));
                cursor.moveToNext();
            }

        }
        cursor.close();
        db.close();
        return traceno_up;
    }
    // 更新轨迹记录表
    public void updatetrail(TraceData data, long traceID, String userID){
        if(isTraceExists(traceID,userID)) {
            SQLiteDatabase db = dbhelper.getWritableDatabase();
            try{
                ContentValues values = new ContentValues();
                values.put(COLUMNS2[0], data.getUserID());
                values.put(COLUMNS2[1], data.getTraceName());
                values.put(COLUMNS2[2], data.getTraceID());
                values.put(COLUMNS2[3], data.getStartTime());
                values.put(COLUMNS2[4], data.getEndTime());
                values.put(COLUMNS2[5], data.getDuration());
                values.put(COLUMNS2[6], data.getDistance());
                values.put(COLUMNS2[7], data.getSportTypes());
                values.put(COLUMNS2[8], data.getShareType());
                values.put(COLUMNS2[9], data.getCalorie());
                values.put(COLUMNS2[10], data.getPoiCount());
                values.put(COLUMNS2[11], data.getSteps());
                long row = db.update(TABLE2_NAME, values, " userID='"+userID+"'"+" and TraceID=('"+traceID+"')", null);
                Log.i("TraceDBTrail", "updateTrail row:" + row);
            }catch(SQLException e){

            }
            db.close();
        } else{
            insertintoTrail(data);
        }
    }
    // 更新步数表
    public void updatesteps(StepData data, long traceID, String userID){
        if(isStepExists(traceID, userID)){
            SQLiteDatabase db = dbhelper.getWritableDatabase();

            try{
                ContentValues values=new ContentValues();
                values.put(COLUMNS4[0], data.getUserID());
                values.put(COLUMNS4[1], data.getTraceID());
                values.put(COLUMNS4[2], data.getSteps());

                long row = db.update(TABLE4_NAME, values, " userID='"+userID+"'"+" and TraceID=('"+traceID+"')", null);
                Log.i("MyTraceDB updatesteps", "step:row:"+row);
            }
            catch(SQLException e){
            }
            db.close();
        }
        else{
            insertintoSteps(data);
        }
    }
    public void updateStatus(long traceNo,int status,String userID){
        if(isStatusExists(traceNo,userID)){
            SQLiteDatabase db = dbhelper.getWritableDatabase();

            try{
                ContentValues values=new ContentValues();
                values.put(COLUMNS5[0], userID);
                values.put(COLUMNS5[1], traceNo);
                values.put(COLUMNS5[2], status);

                db.update(TABLE5_NAME, values, " userID='"+userID+"'"+" and TraceID=('"+traceNo+"')", null);
            }
            catch(SQLException e){

            }
            db.close();
        }
        else{
            insertintoStatus(traceNo, status,userID);
        }
    }

    // 更新位置信息(这里是更新TraceID)
    public void updateGpsByTraceID(long traceID, long newTraceID, String userID) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMNS[6], newTraceID);
            db.update(TABLE1_NAME, values, " userID='" + userID + "' and TraceID=('" + traceID + "')", null);
        }catch (SQLException e){
            e.printStackTrace();
        }
        db.close();
    }
    // 插入位置信息，成功时返回0，否则返回-1
    public int insertintoGps(GpsData data){
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        try{
            ContentValues values=new ContentValues();
            values.put(COLUMNS[0], data.getUserID());
            //values.put(COLUMNS[1], data.getcreateTime());
            values.put(COLUMNS[2], data.getLongitude());
            values.put(COLUMNS[3], data.getLatitude());
            values.put(COLUMNS[4], data.getAltitude());
            values.put(COLUMNS[5], data.getSpeed());
            values.put(COLUMNS[6], data.getTraceID());
            values.put(COLUMNS[7], data.getCityID());
            values.put(COLUMNS[8], data.getDeviceID());
            values.put(COLUMNS[9], data.getSportType());
            long row = db.insert(TABLE1_NAME, null, values);
            Log.i("row", "row:" + row);
        }
        catch(SQLException e){
            db.close();
            return -1;
        }
        db.close();
        return 0;
    }
    public int insertintoGpswithDate(GpsData data){
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        try{
            ContentValues values=new ContentValues();
            values.put(COLUMNS[0], data.getUserID());
            values.put(COLUMNS[1], data.getCreateTime());
            values.put(COLUMNS[2], data.getLongitude());
            values.put(COLUMNS[3], data.getLatitude());
            values.put(COLUMNS[4], data.getAltitude());
            values.put(COLUMNS[5], data.getSpeed());
            values.put(COLUMNS[6], data.getTraceID());
            db.insert(TABLE1_NAME, null, values);
        }
        catch(SQLException e){
            db.close();
            return -1;
        }
        db.close();
        return 0;
    }
    public int insertintoTrail(TraceData data){
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        try{
            ContentValues values = new ContentValues();
            values.put(COLUMNS2[0], data.getUserID());
            values.put(COLUMNS2[1], data.getTraceName());
            values.put(COLUMNS2[2], data.getTraceID());
            values.put(COLUMNS2[3], data.getStartTime());
            values.put(COLUMNS2[4], data.getEndTime());
            values.put(COLUMNS2[5], data.getDuration());
            values.put(COLUMNS2[6], data.getDistance());
            values.put(COLUMNS2[7], data.getSportTypes());
            values.put(COLUMNS2[8], data.getShareType());
            values.put(COLUMNS2[9], data.getCalorie());
            values.put(COLUMNS2[10], data.getPoiCount());
            values.put(COLUMNS2[11], data.getSteps());
            long rows = db.insert(TABLE2_NAME, null, values);
            Log.i("MyTraceDB insertTrail", "rows:"+rows);
        }
        catch(SQLException e){
            db.close();
            return -1;
        }
        db.close();
        return 0;
    }

    public int insertintoEvents(PhoneEventsData data){
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        try{
            ContentValues values=new ContentValues();
            values.put(COLUMNS3[0], data.getUserID());
            values.put(COLUMNS3[1], data.getCreateTime());
            values.put(COLUMNS3[2], data.getEventType());
            values.put(COLUMNS3[3], data.getLongitude());
            values.put(COLUMNS3[4], data.getLatitude());
            values.put(COLUMNS3[5], data.getAltitude());

            db.insert(TABLE3_NAME, null, values);
        }
        catch(SQLException e){
            db.close();
            return -1;
        }
        db.close();
        return 0;
    }
    public int insertintoSteps(StepData data){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try{
            ContentValues values=new ContentValues();
            values.put(COLUMNS4[0], data.getUserID());
            values.put(COLUMNS4[1], data.getTraceID());
            values.put(COLUMNS4[2], data.getSteps());

            db.insert(TABLE4_NAME, null, values);
        }
        catch(SQLException e){
            db.close();
            return -1;
        }
        db.close();
        return 0;
    }
    public int insertintoStatus(long traceNo,int status,String userID){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try{
            ContentValues values=new ContentValues();
            values.put(COLUMNS5[0], userID);
            values.put(COLUMNS5[1], traceNo);
            values.put(COLUMNS5[2], status);

            db.insert(TABLE5_NAME, null, values);
        }
        catch(SQLException e){
            db.close();
            return -1;
        }
        db.close();
        return 0;
    }
    public ArrayList<GpsData> queryfromGpsbytraceID(long traceID, String userID){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        ArrayList<GpsData> datalist = new ArrayList<GpsData>();
//        Cursor cursor = db.query(TABLE1_NAME, null,
//                " userID='"+userID+"'"+" and TraceID=('"+traceID+"')", null, null, null, "createTime asc");
        Cursor cursor = db.query(TABLE1_NAME, null,
                " userID='"+userID+"'"+" and TraceID=('"+traceID+"')", null, null, null, null);
        int rows = cursor.getCount();
        if(rows != 0){
            cursor.moveToFirst();
            for(int i = 0; i < rows; i++)
            {
                GpsData data=new GpsData();
                data.setUserID(cursor.getString(0));
                data.setCreateTime(cursor.getString(1));
                data.setLongitude(cursor.getDouble(2));
                data.setLatitude(cursor.getDouble(3));
                data.setAltitude(cursor.getDouble(4));
                data.setSpeed(cursor.getDouble(5));
                data.setTraceID(cursor.getLong(6));
                data.setCityID(cursor.getInt(7));
                data.setDeviceID(cursor.getString(8));
                data.setSportType(cursor.getInt(9));
                datalist.add(data);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return datalist;
    }

    public ArrayList<GpsData> queryfromGpsbylasttime(String lasttime, String userID)
    {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        ArrayList<GpsData> datalist=new ArrayList<GpsData>();
        Cursor cursor=db.query(TABLE1_NAME, null,
                " userID='"+userID+"'"+" and datetime(createTime)>datetime('"+lasttime+"')", null, null, null, "createTime asc");
        //+"' AND datatime(time)<datatime('2015-03-13 21:00:00')"
        int rows=cursor.getCount();
        if(rows!=0){
            cursor.moveToFirst();
            for(int i=0;i<rows;i++)
            {
                GpsData data=new GpsData();
                data.setUserID(cursor.getString(0));
                data.setCreateTime(cursor.getString(1));
                data.setLongitude(cursor.getDouble(2));
                data.setLatitude(cursor.getDouble(3));
                data.setAltitude(cursor.getDouble(4));
                data.setSpeed(cursor.getDouble(5));
                data.setTraceID(cursor.getLong(6));
                data.setCityID(cursor.getInt(7));
                data.setDeviceID(cursor.getString(8));
                data.setSportType(cursor.getInt(9));
                datalist.add(data);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return datalist;
    }
    public ArrayList<GpsData> getallGps(String userID)
    {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        ArrayList<GpsData> datalist=new ArrayList<GpsData>();

        Cursor cursor=db.query(TABLE1_NAME, null,

                " userID='"+userID+"'", null, null, null, null);
        int rows=cursor.getCount();
        if(rows!=0){
            cursor.moveToFirst();
            for(int i=0;i<rows;i++)
            {
                GpsData data=new GpsData();
                data.setUserID(cursor.getString(0));
                data.setCreateTime(cursor.getString(1));
                data.setLongitude(cursor.getDouble(2));
                data.setLatitude(cursor.getDouble(3));
                data.setAltitude(cursor.getDouble(4));
                data.setSpeed(cursor.getDouble(5));
                data.setTraceID(cursor.getLong(6));
                datalist.add(data);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return datalist;
    }
    public ArrayList<TraceData> getallTrail(String userID)
    {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        ArrayList<TraceData> datalist=new ArrayList<TraceData>();

        Cursor cursor=db.query(TABLE2_NAME, null,

                "  userID='"+userID+"'", null, null, null, " StartTime desc");
        int rows=cursor.getCount();
        if(rows!=0){
            cursor.moveToFirst();
            for(int i=0;i<rows;i++)
            {
                TraceData data=new TraceData();
                data.setUserID(cursor.getString(0));
                data.setTraceName(cursor.getString(1));
                data.setTraceID(cursor.getLong(2));
                data.setStartTime(cursor.getString(3));
                data.setEndTime(cursor.getString(4));
                data.setDuration(cursor.getLong(5));
                data.setDistance(cursor.getDouble(6));
                data.setSportTypes(cursor.getInt(7));
                data.setShareType(cursor.getInt(8));
                data.setCalorie(cursor.getInt(9));
                data.setPoiCount(cursor.getInt(10));
                data.setSteps(cursor.getInt(11));
                datalist.add(data);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return datalist;
    }

    public TraceData queryfromTrailbytraceID(long traceID,String userID)
    {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        TraceData data=new TraceData();

        Cursor cursor=db.query(TABLE2_NAME, null,

                " userID='"+userID+"'"+" and  TraceID=('"+traceID+"')", null, null, null,null);
        int rows=cursor.getCount();
        if(rows!=0){
            cursor.moveToFirst();

            data.setUserID(cursor.getString(0));
            data.setTraceName(cursor.getString(1));
            data.setTraceID(cursor.getLong(2));
            data.setStartTime(cursor.getString(3));
            data.setEndTime(cursor.getString(4));
            data.setDuration(cursor.getLong(5));
            data.setDistance(cursor.getDouble(6));
            data.setSportTypes(cursor.getInt(7));
            data.setShareType(cursor.getInt(8));
            data.setCalorie(cursor.getInt(9));
            data.setPoiCount(cursor.getInt(10));
            data.setSteps(cursor.getInt(11));
        }

        cursor.close();
        db.close();
        return data;
    }
    public ArrayList<StepData> getallSteps(String userID){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        ArrayList<StepData> datalist=new ArrayList<StepData>();

        Cursor cursor=db.query(TABLE4_NAME, null,

                "  userID='"+userID+"'", null, null, null, null);
        int rows=cursor.getCount();
        if(rows!=0){
            cursor.moveToFirst();
            for(int i=0;i<rows;i++)
            {
                StepData data=new StepData();
                data.setUserID(cursor.getString(0));
                data.setTraceID(cursor.getLong(1));
                data.setSteps(cursor.getInt(2));
                datalist.add(data);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return datalist;
    }
    public StepData querryformstepsbyTraceNo(long num,String userID){

        SQLiteDatabase db = dbhelper.getReadableDatabase();
        StepData data=new StepData();

        Cursor cursor=db.query(TABLE4_NAME, null,

                "  userID='"+userID+"'"+" and  TraceID=('"+num+"')", null, null, null, null);
        int rows=cursor.getCount();
        if(rows!=0){
            cursor.moveToFirst();
            data.setUserID(cursor.getString(0));
            data.setTraceID(cursor.getLong(1));
            data.setSteps(cursor.getInt(2));

        }

        cursor.close();
        db.close();
        return data;
    }
    public ArrayList<PhoneEventsData> queryfromEventsbylasttime(String lasttime,String userID)
    {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        ArrayList<PhoneEventsData> datalist=new ArrayList<PhoneEventsData>();
        Cursor cursor=db.query(TABLE3_NAME, null,
                " userID='"+userID+"'"+" and datetime(createTime)>datetime('"+lasttime+"')", null, null, null, "createTime asc");
        //+"' AND datatime(time)<datatime('2015-03-13 21:00:00')"
        int rows=cursor.getCount();
        if(rows!=0){
            cursor.moveToFirst();
            for(int i=0;i<rows;i++)
            {
                PhoneEventsData data=new PhoneEventsData();
                data.setUserID(cursor.getString(0));
                data.setCreateTime(cursor.getString(1));
                data.setEventType(cursor.getInt(2));
                data.setLongitude(cursor.getDouble(3));
                data.setLatitude(cursor.getDouble(4));
                data.setAltitude(cursor.getDouble(5));

                datalist.add(data);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return datalist;
    }
    public void deleteGpsByDate(String date,String userID){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.delete(TABLE1_NAME, " userID='"+userID+"'"+" and datetime(createTime)<=datetime('"+date+"')", null);
        db.close();
    }
    public void deleteTrailByTraceNo(long num,String userID){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.delete(TABLE2_NAME, " userID='"+userID+"'"+" and TraceID=('"+num+"')",null);
        db.delete(TABLE1_NAME, " userID='"+userID+"'"+" and TraceID=('"+num+"')",null);
        db.close();
        if(isStepExists(num,userID)){
            SQLiteDatabase db2 = dbhelper.getWritableDatabase();
            db2.delete(TABLE4_NAME, " userID='"+userID+"'"+" and TraceID=('"+num+"')",null);
            db2.close();
        }

    }
    public void deleteallGps(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.delete(TABLE1_NAME, null, null);
        db.close();
    }
    public void deleteStatus(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.delete(TABLE5_NAME, " TraceStatus=('"+0+"')",null);
        db.close();
    }
	/*
	 * 关闭数据库

	public void cloeDB() {
		if (db != null) {
			db.close();
		}

	}*/
}
