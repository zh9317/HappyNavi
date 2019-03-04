package com.trackersurvey.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.trackersurvey.bean.CommentMediaFilesData;
import com.trackersurvey.bean.DownThumbData;
import com.trackersurvey.bean.InterestMarkerData;
import com.trackersurvey.bean.ListItemData;
import com.trackersurvey.bean.ThumbMediaFiles;
import com.trackersurvey.db.MyTraceDBHelper;
import com.trackersurvey.db.PhotoDBHelper;
import com.trackersurvey.db.PointOfInterestDBHelper;
import com.trackersurvey.http.DeletePOIRequest;
import com.trackersurvey.http.DownloadMediaFiles;
import com.trackersurvey.http.DownloadPoiListRequest;
import com.trackersurvey.http.DownloadPoiRequest;
import com.trackersurvey.http.DownloadThumbMediaFiles;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.httpconnection.DeleteCloudComment;
import com.trackersurvey.httpconnection.GetThumbPic;
import com.trackersurvey.util.ByteHttpUtil;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.GsonHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zh931 on 2018/5/19.
 */

public class MyCommentModel {
    private Context                            context;
    private SharedPreferences                  sp;
    private ArrayList<HashMap<String, Object>> items; // 评论列表单元内容
    private DownCommentListener                mDownComment;
    private DownFileListener                   mDownFile;
    private DeleteCommentListener              mDeleteComment;
    private ChangeBackgroudListener            mChangeBackgroud;
    private DownThumbFileListener              mDownThumbFile;

    // 数据库操作类
    private PhotoDBHelper           dbHelper;
    private PointOfInterestDBHelper poiHelper;
    private Cursor                  cursor = null;

    private       int     numOfUE     = 0; // 该用户本地的总评论
    private       String  bgImageName = "bgImage.jpg";
    private final int     listOnView  = 10; // 列表行数初始最大值
    private       boolean cloudMore   = true; // 云端是否有更多评论
    private       int     listOneTime = 5; // 一次增加列表行数

    //是否在向list添加数据，如果是，则后来的操作取消
    private boolean isAddingComment = false;
    //dbHelper是否可用
    private boolean isDBReady       = false;
    private String  from            = null;//判断是谁调用了此类
    private String  startTime;
    private String  endTime;

    public final String UPDATEUI_ACTION = "android.intent.action.UPDATEUI_RECEIVER";// 给ShowPoiFragment发的广播

    private HashMap<Integer, Integer> mediaFiles = new HashMap<>();

    public ArrayList<HashMap<String, Object>> getItems() {
        return items;
    }

    /**
     * 获取第一项的背景照片
     *
     * @return
     */
    public String getFirst() {
        return bgImageName;
    }

    /**
     * 获取最后一行提示
     *
     * @return
     */
    public boolean cloudMore() {
        return cloudMore;
    }

    /**
     * 读取一条评论
     *
     * @param position
     * @return
     */
    public InterestMarkerData getComment(int position) {
        return ((ListItemData) items.get(position).get("listItem")).getEvent();
    }

    /**
     * 读取一个文件
     */
    public CommentMediaFilesData getFile(int listPosition, int gridPosition) {
        return ((ListItemData) items.get(listPosition).get("listItem"))
                .getFiles()[gridPosition];
    }

    /**
     * 监听评论下载
     *
     * @param mDownComment
     */
    public void setmDownComment(DownCommentListener mDownComment) {
        this.mDownComment = mDownComment;
    }

    /**
     * 监听文件下载
     *
     * @param mDownFile
     */
    public void setmDownFile(DownFileListener mDownFile) {
        this.mDownFile = mDownFile;
    }

    /**
     * 监听评论删除
     *
     * @param mDeleteComment
     */
    public void setmDeleteComment(DeleteCommentListener mDeleteComment) {
        this.mDeleteComment = mDeleteComment;
    }

    /**
     * 监听背景改变
     *
     * @param mChangeBackgroud
     */
    public void setmChangeBackgroud(ChangeBackgroudListener mChangeBackgroud) {
        this.mChangeBackgroud = mChangeBackgroud;
    }

    /**
     * 监听缩略图下载
     */
    public void setmDownThumbFile(DownThumbFileListener mDownThumb) {
        this.mDownThumbFile = mDownThumb;
    }

    /**
     * 构造模型时初始化数据
     */
    public MyCommentModel(Context context, String from) {
        this.context = context;
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        items = new ArrayList<HashMap<String, Object>>();
        selectDB();
        this.from = from;
        if (from.equals("album")) {
            initItems();
        } else if (from.equals("mark")) {

        }
    }

    /**
     * 初始化列表数据
     */
    private void initItems() {
        isAddingComment = true;
        items.removeAll(items);
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        cursor = dbHelper.selectEvent(null, PhotoDBHelper.COLUMNS_UE[14] + "="
                + Common.getUserID(context), null, null, null, "datetime("
                + PhotoDBHelper.COLUMNS_UE[0] + ") desc");

        Log.i("dongsiyuaninitItems", "initItems: " + items.size() + "cursor: " + cursor.getCount());
        // 增加首行背景
        listAddBackgroud(bgImageName);
        // 搜索数据库中项目
        numOfUE = cursor.getCount();
        if (numOfUE > 0) {
            while (items.size() - 1 <= listOnView && !cursor.isLast()) {
                items.add(listAddGrid());
            }
        }

        Log.i("dongsiyuaninitItems", "listAddGridinitItems: " + items.size() + "cursor: numOfUE" + cursor.getCount());
        // 增加末行提示
        listAddHint();
        //cursor.close();
        isAddingComment = false;
    }

    /**
     * 设置时间区间
     */
    public void setTimeRegion(String from, String to) {
        startTime = from;
        endTime = to;
        Log.i("starttoend", "start3:" + from + "; end3:" + to);
    }

    /**
     * 初次请求兴趣点
     *
     * @param traceID
     */
    public void initMarkerItemsOnline(final long traceID) {

        DownloadPoiRequest downloadPoiRequest = new DownloadPoiRequest(sp.getString("token", ""),
                1, 100, traceID);

        Log.i("dongsiyuan", "initMarkerItemsOnline: " + traceID);

        downloadPoiRequest.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {

                    List<InterestMarkerData> interestMarkerDataList = (ArrayList<InterestMarkerData>) responseObject;
                    Log.i("dongsiyuan", "onResponseData: " + interestMarkerDataList.size());
                    for (int i = 0; i < interestMarkerDataList.size(); i++) {
                        Log.i("dongsiyuan", "onResponseData: " + interestMarkerDataList.get(i).toString());
                    }

                    cursor = dbHelper.selectEvent(null, PhotoDBHelper.COLUMNS_UE[14] + "="
                            + Common.getUserID(context), null, null, null, "datetime("
                            + PhotoDBHelper.COLUMNS_UE[0] + ") desc");

                    cursor.moveToFirst();
                    // 同步云端和数据内容
                    int eventsNum = interestMarkerDataList.size();

                    // 获得一个可写的数据库，进行插入删除操作
                    PhotoDBHelper writedDbHelper = new PhotoDBHelper(context, PhotoDBHelper.DBWRITE);

                    if (eventsNum > 0) {
                        // 将本地没有的几行插入数据库
                        for (int i = 0; i < eventsNum; i++) {
                            writedDbHelper.insertEvent(interestMarkerDataList.get(i));
                            Log.i("dongsiyuan", "onResponseData: 插入兴趣点数据" + i);

                            // 插入文件表
                            int imageCount = interestMarkerDataList.get(i).getImageCount();
                            InterestMarkerData.PoiFile poiFile = new InterestMarkerData.PoiFile();
                            Log.i("dongiysuanimageCount", "onResponseData: " + imageCount);
                            int count = 0;
                            for (int j = 0; j < imageCount; j++) {
                                CommentMediaFilesData ev = new CommentMediaFilesData();
                                ev.setDateTime(interestMarkerDataList.get(i).getCreateTime());
                                ev.setFileNo(j);
                                ev.setFileType(CommentMediaFilesData.TYPE_PIC);
                                ev.setFileID(interestMarkerDataList.get(i).getPoiFiles().get(j).getFileID());
                                Log.i("dongsiyuansetFileID", "onResponseData: " + interestMarkerDataList.get(i).getPoiFiles().get(j).getFileID());
                                writedDbHelper.inserFile(ev);
                                count++;
                            }

                            int videoCount = interestMarkerDataList.get(i).getVideoCount();
                            // 插入文件表
                            for (int j = 0; j < videoCount; j++) {
                                CommentMediaFilesData ev = new CommentMediaFilesData();
                                ev.setDateTime(interestMarkerDataList.get(i).getCreateTime());
                                ev.setFileNo(j + count);
                                ev.setFileType(CommentMediaFilesData.TYPE_VIDEO);
                                ev.setFileID(interestMarkerDataList.get(i).getPoiFiles().get(j).getFileID());
                                writedDbHelper.inserFile(ev);
                            }
                        }
                    } else {    // 服务器上没有该traceID的信息 删除本地数据
                        if (cursor.getCount() > 0) {
                            do {
                                if (cursor.getString(10).equals(traceID)) {
                                    writedDbHelper.deleteEvent(cursor.getColumnName(0), String.valueOf(traceID));
                                }
                            } while (cursor.moveToNext());
                        }

                    }

                    if (from.equals("album")) {
                        initItems();
                    } else if (from.equals("mark")) {
                        //                        initItemsByTime(startTime, endTime);
                        initItemsByTraceID(traceID);
                        Log.i("mark", "更新标注handler");
                    }
                    writedDbHelper.closeDB();
                    isAddingComment = false;
                }
            }
        });

        //        downloadComment(Common.currentTime());
        //		downloadAlbum(Common.currentTime());
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = dbHelper.selectEvent(null, PhotoDBHelper.COLUMNS_UE[10] + "="
                + traceID, null, null, null, "datetime("
                + PhotoDBHelper.COLUMNS_UE[0] + ") desc");
        Log.i("dongsiyuanitemsss", "cursor: dbHelper.selectEvent " + cursor.getCount());
        Log.i("itemsss", "MyCommentModel里的items:" + items.toString());
        Log.i("initMarkerItemsOnline", "initMarkerItemsOnline()运行");
        Log.i("starttoend", "start2:" + startTime + "; end2:" + endTime);

    }

    /**
     * 刷新兴趣点
     *
     * @param traceID
     */
    public void refreshMarkerItemsOnline(final long traceID) {
        cursor = dbHelper.selectEvent(null, PhotoDBHelper.COLUMNS_UE[10] + "="
                + traceID, null, null, null, "datetime("
                + PhotoDBHelper.COLUMNS_UE[0] + ") asc");
        DownloadPoiRequest downloadPoiRequest = new DownloadPoiRequest(sp.getString("token", ""),
                1, 100, traceID);

        downloadPoiRequest.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {

                    List<InterestMarkerData> interestMarkerDataList = (ArrayList<InterestMarkerData>) responseObject;
                    Log.i("dongsiyuan", "onResponseData: " + interestMarkerDataList.size());

                    int eventsNum = interestMarkerDataList.size();
                    // 同步云端和数据内容
                    int index = 0;
                    cursor.moveToFirst();
                    // 获得一个可写的数据库，进行插入删除操作
                    PhotoDBHelper writedDbHelper = new PhotoDBHelper(context,
                            PhotoDBHelper.DBWRITE);
                    String eventCTDB;
                    String eventCTCloud;
                    // 本地数据中有内容
                    if (cursor.getCount() > 0) {
                        // 云端也有内容
                        if (eventsNum > 0) {
                            while (index < eventsNum) {
                                // 根据CreateTime判断服务器上的哪些兴趣点本地还没有
                                eventCTDB = cursor.getString(0);
                                eventCTCloud = interestMarkerDataList.get(index).getCreateTime();
                                //Log.i("album", ""+eventCTDB+","+eventCTCloud);
                                long leventCTDB = Common.timeStamp(eventCTDB);
                                long leventCTCloud = Common.timeStamp(eventCTCloud);
                                if (leventCTDB == leventCTCloud) {
                                    // 数据库中的一行和云端一行相等证明这一行数据已经同步，数据库和云端数据皆前移一行
                                    Log.i("Eaa_equal", "" + eventCTDB);
                                    index++;
                                    if (!cursor.moveToNext()) {
                                        break;
                                    }
                                } else if (leventCTDB > leventCTCloud) {
                                    // 数据库中一行大于云端的一行，该行数据已经被其它设备删除，删除数据库中这一行,云端不前移
                                    writedDbHelper
                                            .deleteEvent("datetime(CreateTime) = datetime('"
                                                    + eventCTDB + "')");
                                    Log.i("Eaa_delete",
                                            "getComment delete event:"
                                                    + eventCTDB);
                                    if (!cursor.moveToNext()) {
                                        break;
                                    }
                                } else {
                                    // 数据库中的一行小于云端的一行，云端一行本地没有，插入该行,数据库cursor不前移
                                    writedDbHelper.insertEvent(interestMarkerDataList.get(index));
                                    Log.i("Eaa_insert", "getComment insert event:" + eventCTCloud);
                                    // 插入文件表
                                    mediaFiles = new HashMap<>();
                                    int imageCount = interestMarkerDataList.get(index).getImageCount();
                                    InterestMarkerData.PoiFile poiFile = new InterestMarkerData.PoiFile();
                                    int count = 0;
                                    for (int j = 0; j < imageCount; j++) {
                                        CommentMediaFilesData ev = new CommentMediaFilesData();
                                        ev.setDateTime(interestMarkerDataList.get(index).getCreateTime());
                                        ev.setFileNo(j);
                                        ev.setFileType(CommentMediaFilesData.TYPE_PIC);
                                        ev.setFileID(interestMarkerDataList.get(index).getPoiFiles().get(j).getFileID());
                                        Log.i("dongsiyuansetFileID", "onResponseData: " + interestMarkerDataList.get(index).getPoiFiles().get(j).getFileID());
                                        writedDbHelper.inserFile(ev);
                                        count++;
                                    }

                                    int videoCount = interestMarkerDataList.get(index).getVideoCount();
                                    // 插入文件表
                                    for (int j = 0; j < videoCount; j++) {
                                        CommentMediaFilesData ev = new CommentMediaFilesData();
                                        ev.setDateTime(interestMarkerDataList.get(index).getCreateTime());
                                        ev.setFileNo(j + count);
                                        ev.setFileType(CommentMediaFilesData.TYPE_VIDEO);
                                        ev.setFileID(interestMarkerDataList.get(index).getPoiFiles().get(j).getFileID());
                                        writedDbHelper.inserFile(ev);
                                    }
                                    index++;
                                }
                            }
                            if (!cursor.isAfterLast() && !cloudMore) {
                                // 如果本地数据多于云端，删除本地数据多出的部分

                                do {
                                    eventCTDB = cursor.getString(0);
                                    writedDbHelper
                                            .deleteEvent("datetime(CreateTime) = datetime('"
                                                    + eventCTDB + "')");

                                } while (cursor.moveToNext());
                            }

                        } else {        // 云端没有内容
                            writedDbHelper.deleteEvent(null);
                        }

                    }
                    // 将本地没有的几行插入数据库
                    mediaFiles = new HashMap<>();
                    for (int i = index; i < eventsNum; i++) {
                        writedDbHelper.insertEvent(interestMarkerDataList.get(i));
                        // 插入文件表
                        int imageCount = interestMarkerDataList.get(i).getImageCount();
                        InterestMarkerData.PoiFile poiFile = new InterestMarkerData.PoiFile();
                        int count = 0;
                        for (int j = 0; j < imageCount; j++) {
                            CommentMediaFilesData ev = new CommentMediaFilesData();
                            ev.setDateTime(interestMarkerDataList.get(i).getCreateTime());
                            ev.setFileNo(j);
                            ev.setFileType(CommentMediaFilesData.TYPE_PIC);
                            ev.setFileID(interestMarkerDataList.get(i).getPoiFiles().get(j).getFileID());
                            Log.i("dongsiyuansetFileID", "onResponseData: " + interestMarkerDataList.get(index).getPoiFiles().get(j).getFileID());
                            writedDbHelper.inserFile(ev);
                            count++;
                        }

                        int videoCount = interestMarkerDataList.get(i).getVideoCount();
                        // 插入文件表
                        for (int j = 0; j < videoCount; j++) {
                            CommentMediaFilesData ev = new CommentMediaFilesData();
                            ev.setDateTime(interestMarkerDataList.get(i).getCreateTime());
                            ev.setFileNo(j + count);
                            ev.setFileType(CommentMediaFilesData.TYPE_VIDEO);
                            ev.setFileID(interestMarkerDataList.get(i).getPoiFiles().get(j).getFileID());
                            Log.i("dongsiyuansetFileID", "onResponseData: " + interestMarkerDataList.get(index).getPoiFiles().get(j).getFileID());
                            writedDbHelper.inserFile(ev);
                        }
                    }

                    if (from.equals("album")) {
                        initItems();
                    } else if (from.equals("mark")) {
                        initItemsByTraceID(traceID);
                        Log.i("mark", "更新标注handler");
                    }
                    writedDbHelper.closeDB();
                    isAddingComment = false;
                }
            }
        });
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = dbHelper.selectEvent(null, PhotoDBHelper.COLUMNS_UE[10] + "="
                + traceID, null, null, null, "datetime("
                + PhotoDBHelper.COLUMNS_UE[0] + ") desc");
    }

    public void initMarkerItemsFromDB() {
        initItemsByTime(startTime, endTime);
    }

    /**
     * 查询固定时间段内的标记（评论）信息
     * <p>
     * P.S.数据库里有信息时才可查询
     */
    private void initItemsByTime(String from, String to) {
        items.removeAll(items);
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        //        cursor = dbHelper.selectEvent(null, PhotoDBHelper.COLUMNS_UE[10] + "="
        //                + Common.getUserId(context) + " and datetime("
        //                + PhotoDBHelper.COLUMNS_UE[0] + ") between '" + from +
        //                "' and '" + to + "'", null, null, null, null);

        //        cursor = dbHelper.selectEvent(null, PhotoDBHelper.COLUMNS_UE[10] + "="
        //                + currentTraceID + " and datetime("
        //                + PhotoDBHelper.COLUMNS_UE[0] + ") between '" + from +
        //                "' and '" + to + "'", null, null, null, null);

        cursor = dbHelper.selectEvent(null, PhotoDBHelper.COLUMNS_UE[14] + "="
                + Common.getUserId(context), null, null, null, "datetime("
                + PhotoDBHelper.COLUMNS_UE[0] + ") desc");

        Log.i("itemsssinitItemsByTime", "cursor:" + cursor.getCount() + " ");

        numOfUE = cursor.getCount();
        if (numOfUE > 0) {
            while (!cursor.isLast()) {
                items.add(listAddGrid());
            }
        }
        Log.i("mark", "cursor.getCount() : " + cursor.getCount() + ",items size : " + items.size());
    }

    /**
     * 通过traceID获取本地兴趣点
     *
     * @param traceID
     */
    public void initItemsByTraceID(long traceID) {
        items.removeAll(items);
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        cursor = dbHelper.selectEvent(null, PhotoDBHelper.COLUMNS_UE[10] + "="
                + traceID, null, null, null, "datetime("
                + PhotoDBHelper.COLUMNS_UE[0] + ") desc");

        Log.i("dongsiyuaninitItemBT", "cursor:" + cursor.getCount() + " traceID: " + traceID);
        numOfUE = cursor.getCount();
        if (numOfUE > 0) {
            while (!cursor.isLast()) {
                items.add(listAddGrid());
            }
        }
        Log.i("dongsiyuaninitItemBT", "cursor.getCount() : " + cursor.getCount() + ",items size : " + items.size());
        // 通知ShowPoiFragment刷新
        Intent intent = new Intent();
        intent.setAction(UPDATEUI_ACTION);
        context.sendBroadcast(intent);
    }

    /**
     * 从数据库查询数据
     */
    private void selectDB() {
        dbHelper = new PhotoDBHelper(context, PhotoDBHelper.DBREAD);
        poiHelper = new PointOfInterestDBHelper(context);
        isDBReady = true;
    }

    /**
     * 从数据库查询数据并更新items
     *
     * @return
     */
    private HashMap<String, Object> listAddGrid() {
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            InterestMarkerData event = new InterestMarkerData();
            String eventTime = cursor.getString(0);
            event.setCreateTime(eventTime);
            event.setLongitude(cursor.getDouble(2));
            event.setLatitude(cursor.getDouble(3));
            event.setAltitude(cursor.getDouble(4));
            event.setPlaceName(cursor.getString(8));
            event.setCmt(cursor.getString(9));
            event.setTraceID(cursor.getLong(10));
            event.setImageCount(cursor.getInt(11));
            event.setVideoCount(cursor.getInt(12));
            event.setAudioCount(cursor.getInt(13));
            event.setUserId(cursor.getString(14));
            event.setMotionType(cursor.getInt(15));
            event.setActivityType(cursor.getInt(16));
            event.setRetentionType(cursor.getInt(17));
            event.setCompanionType(cursor.getInt(18));
            event.setRelationType(cursor.getInt(19));
            event.setPoiID(cursor.getInt(21));

            Cursor fileCursor = dbHelper.selectFiles(null, "datetime("
                    + PhotoDBHelper.COLUMNS_FILE[2] + ")=datetime('" + eventTime
                    + "')", null, null, null, null);

            CommentMediaFilesData files[] = new CommentMediaFilesData[fileCursor.getCount()];
            Log.i("album", "Country = " + cursor.getString(5) + ",traceNo:" + cursor.getLong(14)
                    + ",filenum:" + cursor.getInt(7) + ",filesdb :" + files.length);

            int index = 0;
            while (fileCursor.moveToNext()) {
                files[index] = new CommentMediaFilesData(fileCursor.getInt(0),
                        fileCursor.getString(1), fileCursor.getString(2),
                        fileCursor.getInt(3), fileCursor.getString(4), fileCursor.getInt(5));
                // Log.i("Eaa_fileCursor", "" + index);
                index++;
            }

            HashMap<String, Object> listItem = new HashMap<String, Object>();
            ListItemData data = new ListItemData(event, files);        //兴趣点列表数据类型的数据data
            listItem.put("listItem", data);
            fileCursor.close();
            return listItem;
        }
        return null;
    }

    /**
     * 给List增加首栏背景
     */
    private void listAddBackgroud(String path) {
        HashMap<String, Object> listItem = new HashMap<String, Object>();
        listItem.put("listItem", path);
        items.add(0, listItem);
    }

    /**
     * 给List增加末栏提示
     */
    private void listAddHint() {
        String hint = cloudMore ? "more" : "nomore";
        // 增加一栏提示栏
        HashMap<String, Object> listItem = new HashMap<String, Object>();
        listItem.put("listItem", hint);
        items.add(listItem);
    }

    /**
     * 关闭类中的引用
     */
    public void stopModel() {
        /*
         * 如果在这里closeDB,在后台还有任务时退出activity后，任务的回调中调用dbHelper会导致调用已关闭的dbHelper，
         * 应用崩溃，但是因为dbHelper在很多地方使用，在这个类中找不到合适的位置关闭，为了应用不崩溃，注释掉这一行。
         */
        //		dbHelper.closeDB();

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        isDBReady = false;
        dbHelper.closeDB();
        cloudMore = true;
        Log.i("album", "stopModel :close cursor,db");
    }

    /**
     * 添加内容到ListView的尾部
     */
    public void autoAddtoList() {
        cursor = dbHelper.selectEvent(null, PhotoDBHelper.COLUMNS_UE[14] + "="
                + Common.getUserID(context), null, null, null, "datetime("
                + PhotoDBHelper.COLUMNS_UE[0] + ") desc");
        Log.i("dongsiyuanindexCount", "onResponseData: " + cursor.getCount() + " userID : " + Common.getUserId(context));
        if (!isAddingComment) {
            Log.i("dongsiyuan", "addComment:" + items.size() + "numOfUE : " + numOfUE);
            isAddingComment = true;
            // 记录当前滚动到的位置
            int addNum = 0;
            if (items.size() - 2 < numOfUE) {// -2的原因： items中包含顶部背景和底部（more/nomore），去掉这两个才是评论数量
                while (!cursor.isLast() && addNum < listOneTime) {
                    items.add(items.size() - 1, listAddGrid());//保证最后一个元素始终是 hint
                    addNum++;
                }
            }

            // 这次增加的不到listOneTime，本地没有更多数据
            if (addNum < listOneTime - 1) {
                // cloudMore为true云端有更多数据，请求云端
                Log.i("dongsiyuancloudMore", "autoAddtoList: cloudMore" + cloudMore);
                if (cloudMore) {
                    String lastTime;
                    if (numOfUE > 0) {
                        lastTime = ((ListItemData) items.get(items.size() - 2).get("listItem")).getTime();
                    } else {
                        lastTime = Common.currentTime();
                    }
                    Log.i("dongsiyuanlastTime", "autoAddtoList: lastTime +  " + lastTime);

                    DownloadPoiListRequest downloadPoiListRequest = new DownloadPoiListRequest(
                            sp.getString("token", ""), 1, 100);
                    downloadPoiListRequest.requestHttpData(new ResponseData() {
                        @Override
                        public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                            if (isSuccess) {
                                List<InterestMarkerData> interestMarkerDataList = (ArrayList<InterestMarkerData>) responseObject;
                                Log.i("dongsiyuan", "onResponseData: " + interestMarkerDataList.size());
                                for (int i = 0; i < interestMarkerDataList.size(); i++) {
                                    Log.i("dongsiyuantoString()", "onReta: " + interestMarkerDataList.get(i).toString());
                                }

                                int eventsNum = interestMarkerDataList.size();
                                // 同步云端和数据内容
                                int index = 0;
                                cursor.moveToFirst();
                                // 获得一个可写的数据库，进行插入删除操作
                                PhotoDBHelper writedDbHelper = new PhotoDBHelper(context,
                                        PhotoDBHelper.DBWRITE);
                                String eventCTDB;
                                String eventCTCloud;
                                // 本地数据中有内容
                                Log.i("dongsiyuanindexCount", "onResponseData: " + cursor.getCount());
                                if (cursor.getCount() > 0) {
                                    Log.i("dongsiyuanindexCount()", "onResponseData: " + index);
                                    // 云端也有内容
                                    if (eventsNum > 0) {
                                        Log.i("dongsiyuanindexNum", "onResponseData: " + index);
                                        while (index < eventsNum) {
                                            // 根据CreateTime判断服务器上的哪些兴趣点本地还没有
                                            eventCTDB = cursor.getString(0);
                                            eventCTCloud = interestMarkerDataList.get(index).getCreateTime();
                                            //Log.i("album", ""+eventCTDB+","+eventCTCloud);
                                            long leventCTDB = Common.timeStamp(eventCTDB);
                                            long leventCTCloud = Common.timeStamp(eventCTCloud);
                                            if (leventCTDB == leventCTCloud) {
                                                // 数据库中的一行和云端一行相等证明这一行数据已经同步，数据库和云端数据皆前移一行
                                                Log.i("Eaa_equal", "" + eventCTDB);
                                                index++;
                                                if (!cursor.moveToNext()) {
                                                    break;
                                                }
                                            } else if (leventCTDB > leventCTCloud) {
                                                // 数据库中一行大于云端的一行，该行数据已经被其它设备删除，删除数据库中这一行,云端不前移
                                                writedDbHelper
                                                        .deleteEvent("datetime(CreateTime) = datetime('"
                                                                + eventCTDB + "')");
                                                Log.i("Eaa_delete",
                                                        "getComment delete event:"
                                                                + eventCTDB);
                                                if (!cursor.moveToNext()) {
                                                    break;
                                                }
                                            } else {
                                                // 数据库中的一行小于云端的一行，云端一行本地没有，插入该行,数据库cursor不前移
                                                writedDbHelper.insertEvent(interestMarkerDataList.get(index));
                                                Log.i("Eaa_insert",
                                                        "getComment insert event:"
                                                                + eventCTCloud);
                                                // 插入文件表
                                                //
                                                //
                                                index++;
                                            }
                                            Log.i("dongsiyuanindex", "onResponseData: " + index);
                                        }

                                        if (!cursor.isAfterLast() && !cloudMore) {
                                            // 如果本地数据多于云端，删除本地数据多出的部分

                                            do {
                                                eventCTDB = cursor.getString(0);
                                                writedDbHelper
                                                        .deleteEvent("datetime(CreateTime) = datetime('"
                                                                + eventCTDB + "')");

                                            } while (cursor.moveToNext());
                                        }

                                    } else {        // 云端没有内容
                                        writedDbHelper.deleteEvent(null);
                                    }
                                }
                                Log.i("dongsiyuanindex", "onResponseData: " + index);
                                // 将本地没有的几行插入数据库
                                for (int i = index; i < eventsNum; i++) {
                                    writedDbHelper.insertEvent(interestMarkerDataList.get(i));
                                    Log.i("dongsiyuanindex", "onResponseData: " + index);
                                    //                            int fileNum = interestMarkerDataList.get(i).getFileNum();
                                    // 插入文件表
                                    //
                                    //
                                }
                                // 重新查询本地数据
                                if (cursor != null && !cursor.isClosed()) {
                                    cursor.close();
                                }
                                cursor = dbHelper.selectEvent(null,
                                        PhotoDBHelper.COLUMNS_UE[14] + "=" + Common.getUserId(context),
                                        null, null, null, "datetime("
                                                + PhotoDBHelper.COLUMNS_UE[0] + ") desc");
                                cursor.moveToPosition(numOfUE - 1);
                                numOfUE = cursor.getCount();

                                // 记录当前滚动到的位置
                                // int setPosition = lView.getFirstVisiblePosition() + 1;
                                int addNum = 0;
                                while (!cursor.isLast() && addNum < listOneTime) {
                                    items.add(items.size() - 1, listAddGrid());
                                    addNum++;
                                }

                                if (!cloudMore) {
                                    // 删除末栏提示再添加以更改提示文本
                                    items.get(items.size() - 1).put("listItem", "nomore");
                                }
                                writedDbHelper.closeDB();
                                isAddingComment = false;
                                mDownComment.onCommentDownload(0);
                            } else {
                                Log.i("dongsiyuanDownloadPoi", "onResponseData: Failed" + code);
                            }
                        }
                    });
                    //                    GetAlbum gct = new GetAlbum(requestAlbum,
                    //                            Common.URL_DOWNEVENT, Common.getUserId(context),
                    //                            lastTime, Common.getDeviceId(context), "no");
                    //                    gct.start();

					/*GetComment gct = new GetComment(requestComment,
							Common.URL_DOWNEVENT, Common.getUserId(context),
							lastTime,Common.getDeviceId(context));
					gct.start();*/
                } else {// 云端没有更多数据
                    // 请求评论结束，通知UI线程更改提示文本
                    items.get(items.size() - 1).put("listItem", "nomore");
                    mDownComment.onCommentDownload(0);
                    isAddingComment = false;
                }
            } else {
                mDownComment.onCommentDownload(0);
                isAddingComment = false;
            }
        }
    }

    /**
     * 下载评论
     *
     * @param dateTime
     */
    public void downloadComment(String dateTime) {
        isAddingComment = true;
        //        GetComment downComment = new GetComment(refreshComment,
        //                Common.URL_DOWNEVENT, Common.getUserId(context),
        //                startTime, endTime, Common.getDeviceId(context));
		/*GetComment downComment = new GetComment(refreshComment,
				Common.URL_DOWNEVENT, Common.getUserId(context),
				dateTime,Common.getDeviceId(context));*/
        Log.i("Eaa", "downloadComment:" + dateTime);
        //        downComment.start();

    }

    /**
     * 下载相册
     *
     * @param dateTime
     */
    public void downloadAlbum(String dateTime) {
        isAddingComment = true;
        //        GetAlbum downAlbum = new GetAlbum(refreshAlbum,
        //                Common.URL_DOWNEVENT, Common.getUserId(context),
        //                dateTime, Common.getDeviceId(context), "yes");
        //        Log.i("Eaa", "downloadaAlbum:"+dateTime);
        //        downAlbum.start();

        // 测试下载兴趣点
        DownloadPoiListRequest downloadPoiListRequest = new DownloadPoiListRequest(
                sp.getString("token", ""), 1, 100);
        downloadPoiListRequest.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {
                    List<InterestMarkerData> interestMarkerDataList = (ArrayList<InterestMarkerData>) responseObject;
                    Log.i("dongsiyuan", "onResponseData: " + interestMarkerDataList.size());
                    for (int i = 0; i < interestMarkerDataList.size(); i++) {
                        Log.i("dongstoString()", "onReta: " + interestMarkerDataList.get(i).toString());
                    }

                    int eventsNum = interestMarkerDataList.size();
                    // 同步云端和数据内容
                    int index = 0;
                    cursor.moveToFirst();
                    // 获得一个可写的数据库，进行插入删除操作
                    PhotoDBHelper writedDbHelper = new PhotoDBHelper(context,
                            PhotoDBHelper.DBWRITE);
                    String eventCTDB;
                    String eventCTCloud;
                    // 本地数据中有内容
                    if (cursor.getCount() > 0) {
                        // 云端也有内容
                        if (eventsNum > 0) {
                            while (index < eventsNum) {
                                // 根据CreateTime判断服务器上的哪些兴趣点本地还没有
                                eventCTDB = cursor.getString(0);
                                eventCTCloud = interestMarkerDataList.get(index).getCreateTime();
                                Log.i("hhhhhhhhhhhhhhh", "eventCTCloud" + interestMarkerDataList.get(index).getCreateTime());
                                //Log.i("album", ""+eventCTDB+","+eventCTCloud);
                                long leventCTDB = Common.timeStamp(eventCTDB);
                                long leventCTCloud = Common.timeStamp(eventCTCloud);
                                if (leventCTDB == leventCTCloud) {
                                    // 数据库中的一行和云端一行相等证明这一行数据已经同步，数据库和云端数据皆前移一行
                                    Log.i("Eaa_equal", "" + eventCTDB);
                                    index++;
                                    if (!cursor.moveToNext()) {
                                        break;
                                    }
                                } else if (leventCTDB > leventCTCloud) {
                                    // 数据库中一行大于云端的一行，该行数据已经被其它设备删除，删除数据库中这一行,云端不前移
                                    writedDbHelper
                                            .deleteEvent("datetime(CreateTime) = datetime('"
                                                    + eventCTDB + "')");
                                    Log.i("Eaa_delete",
                                            "getComment delete event:"
                                                    + eventCTDB);
                                    if (!cursor.moveToNext()) {
                                        break;
                                    }
                                } else {
                                    // 数据库中的一行小于云端的一行，云端一行本地没有，插入该行,数据库cursor不前移
                                    writedDbHelper.insertEvent(interestMarkerDataList.get(index));
                                    Log.i("Eaa_insert",
                                            "getComment insert event:"
                                                    + eventCTCloud);
                                    // 插入文件表
                                    //
                                    //
                                    index++;
                                }
                            }
                            if (!cursor.isAfterLast() && !cloudMore) {
                                // 如果本地数据多于云端，删除本地数据多出的部分

                                do {
                                    eventCTDB = cursor.getString(0);
                                    writedDbHelper
                                            .deleteEvent("datetime(CreateTime) = datetime('"
                                                    + eventCTDB + "')");

                                } while (cursor.moveToNext());
                            }
                        } else {
                            writedDbHelper.deleteEvent(null);
                        }
                    }
                    // 将本地没有的几行插入数据库
                    for (int i = index; i < eventsNum; i++) {
                        writedDbHelper.insertEvent(interestMarkerDataList.get(i));

                        //                            int fileNum = interestMarkerDataList.get(i).getFileNum();
                        // 插入文件表
                        //
                        //
                    }

                    if (from.equals("album")) {
                        initItems();
                    }
                    writedDbHelper.closeDB();
                    isAddingComment = false;
                    mDownComment.onCommentDownload(0);
                }

            }
        });
    }

    /**
     * 下载文件
     *
     * @param
     * @param fileID
     * @param listPosition
     * @param filePosition
     * @param type
     */
    String cloudPicture = null;
    String cloudVideo = null;

    public void downloadFile(final int listPosition, final int filePosition, final int type, int fileID) {
        RequestFile rf = new RequestFile(listPosition, filePosition, type);
        String commmentId = ((ListItemData) items.get(listPosition).get("listItem")).getTime();

        final String createTime = ((ListItemData) (items.get(listPosition).get("listItem"))).getTime();

        int PoiID = ((ListItemData) items.get(listPosition).get("listItem")).getPoiID();

        Log.i("dongsiyuandownloadFile", "PoiID: " + PoiID + " fileUrl : " + fileID);
        Log.i("dongsiyuandownloadFile", "downloadFile: " + sp.getString("token", ""));

        DownloadMediaFiles downloadMediaFiles = new DownloadMediaFiles(sp.getString("token", ""), fileID);
        downloadMediaFiles.requestHttpData(new ByteHttpUtil.ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, InputStream responseObject) throws IOException {

                if (isSuccess) {
                    InputStream inputStream = responseObject;
                    RandomAccessFile accessFile = null;
                    File file = null;

                    if (type == CommentMediaFilesData.TYPE_PIC) {
                        if (null == inputStream || "noPic".equals(inputStream) || "null".equals(inputStream)) {
                            mDownFile.onFileDownload(-1, listPosition, filePosition);
                            return;
                        }
                        String imageName = Common.currentTimeMill();
                        String dir = Common.PHOTO_PATH + imageName;
                        file = new File(dir + "_cloud.jpg");

                        accessFile = new RandomAccessFile(file, "rw");
                        byte[] b = new byte[1024];
                        int len;
                        while ((len = inputStream.read(b)) != -1) {
                            accessFile.write(b, 0, len);
                        }

                        PhotoDBHelper writeDBHelper = new PhotoDBHelper(context, PhotoDBHelper.DBWRITE);
                        cloudPicture = file.getAbsolutePath();
                        /**
                         * 将该云端图记录到EventFiles表对应文件
                         */
                        ContentValues cv = new ContentValues();
                        cv.put(PhotoDBHelper.COLUMNS_FILE[1], file.getAbsolutePath());
                        int result = writeDBHelper.updateFile(cv, "datetime("
                                + PhotoDBHelper.COLUMNS_FILE[2] + ")=datetime('"
                                + createTime + "') AND "
                                + PhotoDBHelper.COLUMNS_FILE[0] + " = " + filePosition);
                        Log.i("Eaa", "datetime(" + PhotoDBHelper.COLUMNS_FILE[2]
                                + ")=datetime('" + createTime + "') AND "
                                + PhotoDBHelper.COLUMNS_FILE[0] + " = " + filePosition);

                        Log.i("Eaa", "downFile result:" + result);
                        CommentMediaFilesData updateFile = ((ListItemData) items.get(listPosition).get("listItem")).getFiles()[filePosition];
                        updateFile.setFileName(cloudPicture);
                        // 更新list
                        ((ListItemData) items.get(listPosition).get("listItem")).setOneFile(filePosition, updateFile);

                        writeDBHelper.closeDB();
                        mDownFile.onFileDownload(0, listPosition, filePosition);//修改后，第一次下载这里会因为空指针崩溃
                    }
                    // 如果请求的文件是视频
                    else if (type == CommentMediaFilesData.TYPE_VIDEO) {
                        if ("null".equals(inputStream) || "noPic".equals(inputStream)) {
                            mDownFile.onFileDownload(-2, listPosition, filePosition);
                            return;
                        }

                        String videoName = Common.PHOTO_PATH + Common.currentTimeMill();
                        file = new File(videoName + "_cloud.mp4");
                        accessFile = new RandomAccessFile(file, "rw");
                        byte[] b = new byte[1024];
                        int len;
                        while ((len = inputStream.read(b)) != -1) {
                            accessFile.write(b, 0, len);
                        }

                        PhotoDBHelper writeDBHelper = new PhotoDBHelper(context, PhotoDBHelper.DBWRITE);
                        cloudVideo = file.getAbsolutePath();
                        // 更新数据库中文件的记录
                        ContentValues cv = new ContentValues();
                        cv.put(PhotoDBHelper.COLUMNS_FILE[1], cloudVideo);

                        writeDBHelper.updateFile(cv, "datetime(" + PhotoDBHelper.COLUMNS_FILE[2] + ")=datetime('"
                                + createTime + "') AND " + PhotoDBHelper.COLUMNS_FILE[0] + " = " + filePosition);

                        CommentMediaFilesData updateFile = ((ListItemData) items.get(listPosition).get("listItem")).getFiles()[filePosition];
                        updateFile.setFileName(cloudVideo);
                        // 更新list
                        ((ListItemData) items.get(listPosition).get("listItem")).setOneFile(filePosition, updateFile);
                        writeDBHelper.closeDB();
                        mDownFile.onFileDownload(0, listPosition, filePosition);
//
//                        HttpUtils http = new HttpUtils();
//
//                        http.download(fileStr, videoName, true, true,
//                                new RequestCallBack<File>() {
//                                    @Override
//                                    public void onSuccess(ResponseInfo<File> arg0) {
//                                        // TODO Auto-generated method stub
//
//                                    }
//
//                                    @Override
//                                    public void onFailure(HttpException arg0,
//                                                          String arg1) {
//                                        // TODO Auto-generated method stub
//                                        mDownFile.onFileDownload(-2, listPosition, filePosition);
//
//                                    }
//
//                                    @Override
//                                    public void onLoading(long total, long current,
//                                                          boolean isUploading) {
//                                        // TODO Auto-generated method stub
//                                        super.onLoading(total, current, isUploading);
//                                    }
//                                });
                    }
                }
                mDownFile.onFileDownload(0, listPosition, filePosition);//第二次下载时，这里会因为空指针崩溃
            }
        });

        //        GetCloudPicture gcp = new GetCloudPicture(rf, Common.URL_DOWNFILE,
        //                Common.getUserId(context), commmentId, "" + filePosition,
        //                Common.getDeviceId(context));
        //
        //        Log.i("Eaa", "downloadFile:" + commmentId);
        //        gcp.start();
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * 下载缩略图
     *
     * @param position
     * @param poiID
     */
    public void downloadThumbFile(final int position, final String dateTime, int poiID) {
//                GetThumbPic getThumb = new GetThumbPic(new RequestThembFiles(position,
//                        dateTime), Common.URL_DOWNEVENT, Common.getUserId(context),
//                        dateTime, Common.getDeviceId(context));
//                getThumb.start();

        Log.i("DownloadThumbMediaFiles", "downloadThumbFile: " + poiID);

        final ArrayList<HashMap<String, String>> images = new ArrayList<>();
        DownloadThumbMediaFiles downloadThumbMediaFiles = new DownloadThumbMediaFiles(sp.getString("token", ""), poiID);
        downloadThumbMediaFiles.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {

//                    Gson gson = new Gson();
                    LinkedList<ThumbMediaFiles> thumbs = new LinkedList<>();
                    thumbs = (LinkedList<ThumbMediaFiles>) responseObject;
//                    try {
//                        java.lang.reflect.Type fileType = new TypeToken<LinkedList<ThumbMediaFiles>>() {}.getType();
//                        thumbs = gson.fromJson(msg.obj.toString().trim(), fileType);
//                    } catch (Exception e) {
//                        mDownThumbFile.onThumbFileDownload(-1, position, images);
//                    }
                    // Log.d("Eaa", msg.obj.toString());
                    if (thumbs.size() == 0) {
                        return;
                    }
                    PhotoDBHelper writeDBHelper = new PhotoDBHelper(context, PhotoDBHelper.DBWRITE);
                    // 加载缩略图到gridView,同时更新EventFiles 数据库表
                    images.removeAll(images);
                    int index = 0;
                    for (Iterator iterator = thumbs.iterator(); iterator.hasNext(); ) {
                        ThumbMediaFiles thumbPic = (ThumbMediaFiles) iterator.next();
                        int fileType = 1;
                        if (thumbPic.getFileType().equals("image")) {
                            fileType = CommentMediaFilesData.TYPE_PIC;
                        } else if (thumbPic.getFileType().equals("video")) {
                            fileType = CommentMediaFilesData.TYPE_VIDEO;
                        }
                        // 将缩略图写到本地文件夹
                        byte[] picByte = Base64.decode(thumbPic.getFileBase64(), Base64.DEFAULT);
                        BufferedOutputStream bos = null;
                        String imageName = Common.currentTimeMill();
                        File file = new File(Common.CACHEPHOTO_PATH + File.separator + thumbPic.getFileId() + imageName + ".jpg");
                        FileOutputStream fos;
                        try {
                            fos = new FileOutputStream(file);
                            // 将该缩略图记录到EventFiles表对应文件

                            ContentValues cv = new ContentValues();
                            cv.put(PhotoDBHelper.COLUMNS_FILE[3], fileType);
                            cv.put(PhotoDBHelper.COLUMNS_FILE[4], file.getAbsolutePath());
                            // 更新数据库中的缩略图
                            writeDBHelper.updateFile(cv, "datetime("
                                    + PhotoDBHelper.COLUMNS_FILE[2] + ")=datetime('" + dateTime + "') AND "
                                    + PhotoDBHelper.COLUMNS_FILE[0] + " = " + index);
                            bos = new BufferedOutputStream(fos);
                            bos.write(picByte);
                            bos.close();
                            picByte = null;
                            index++;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        String imgPaht = file.getAbsolutePath();
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("itemImage", imgPaht);
                        images.add(map);
                    }

                    // 更新items，保证ListView的同步更新
                    Cursor fileCursor = dbHelper.selectFiles(null, "datetime("
                            + PhotoDBHelper.COLUMNS_UE[0] + ")=datetime('" + dateTime + "')",
                            null, null, null, null);
                    CommentMediaFilesData files[] = new CommentMediaFilesData[fileCursor.getCount()];
                    int cursorIndex = 0;
                    while (fileCursor.moveToNext()) {
                        files[cursorIndex] = new CommentMediaFilesData(fileCursor.getInt(0),
                                fileCursor.getString(1), fileCursor.getString(2),
                                fileCursor.getInt(3), fileCursor.getString(4), fileCursor.getInt(5));
                        cursorIndex++;
                    }
                    if (position < items.size()) {
                        ((ListItemData) (items.get(position).get("listItem"))).setFiles(files);
                    }
                    fileCursor.close();
                    writeDBHelper.closeDB();
                }
                Intent intent = new Intent();
                intent.setAction(UPDATEUI_ACTION);
                Log.i("dongsiyuansendBroadcast", "sendBroadcast: ");
                context.sendBroadcast(intent);
                mDownThumbFile.onThumbFileDownload(0, position, images);
            }
        });


    }

    /**
     * 删除相册里的兴趣点
     *
     * @param dateTime
     * @param poiID
     * @param traceID
     */
    public void deleteComment(String dateTime, final int poiID, long traceID) {
        deletePOI(sp.getString("token", ""), poiID, dateTime, traceID);

        if (from.equals("album")) {
            initItems();
        }
    }

    /**
     * 删除评论
     *
     * @param dateTime
     * @param listPosition
     */
    public boolean deleteComment(String dateTime, int listPosition) {
        DeleteCloudComment dcc = new DeleteCloudComment(context,
                new DeleteCloudEvent(listPosition), Common.URL_DELETEEVENT,
                Common.getUserId(context), dateTime, Common.getDeviceId(context));
        dcc.start();
        return false;
    }

    public boolean deleteComment(String startTime, String endTime) {
        DeleteCloudComment dcc = new DeleteCloudComment(context,
                new DeleteCloudEvent(), Common.URL_DELETEEVENT,
                Common.getUserId(context), startTime, endTime, Common.getDeviceId(context));
        dcc.start();
        return false;
    }

    /**
     * 删除ShowPoiFragment里的兴趣点
     *
     * @param token
     * @param poiID
     */
    public boolean deletePOI(String token, final int poiID, final String dateTime, final long traceID) {
        DeletePOIRequest deletePOIRequest = new DeletePOIRequest(token, poiID);
        deletePOIRequest.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {
                    if (code.equals("0")) {

                        Log.i("dongisyuanDelete", "onResponseData: dateTime: " + dateTime + "traceID: " + traceID + "poiID: " + poiID);

                        dbHelper = new PhotoDBHelper(context, PhotoDBHelper.DBWRITE);
                        //删除兴趣点成功
                        if (deleteFromDB(traceID, dateTime) == 0) {
                            Log.i("dongisyuanDelete", "删除兴趣点成功");
                        } else {

                        }

                        dbHelper = new PhotoDBHelper(context, PhotoDBHelper.DBREAD);
                        if (from.equals("album")) {
                            initItems();
                        } else if (from.equals("mark")) {
                            initItemsByTraceID(traceID);
                            Log.i("mark", "更新标注handler");
                        }
                        // 通知ShowPoiFragment刷新
                        Intent intent = new Intent();
                        intent.setAction(UPDATEUI_ACTION);
                        context.sendBroadcast(intent);

                        // 通知MyAlbumActivity刷新
                        mDownComment.onCommentDownload(0);

                    }
                }
            }
        });
        return false;
    }

    /**
     * 从数据库中删除该兴趣点
     *
     * @return
     */
    private int deleteFromDB(long traceID, String dateTime) {
        int result = -1;
        result = dbHelper.deleteEvent(dateTime, String.valueOf(traceID));
        dbHelper.closeDB();
        if (result != 0) {
            return -1;
        }
        return 0;
    }

    /**
     * 修改背景
     *
     * @param bgImageName
     */
    public void changeBackgroud(String bgImageName) {
        items.remove(0);
        listAddBackgroud(bgImageName);
        mChangeBackgroud.onBackgroudChanged();
    }

    /**
     * 下载评论的监听器
     */
    public interface DownCommentListener {
        public void onCommentDownload(int msg);
    }

    /**
     * 下载文件的监听器
     */
    public interface DownFileListener {
        public void onFileDownload(int msg, int listPosition, int filePosition);
    }

    /**
     * 下载缩略图的监听器
     */
    public interface DownThumbFileListener {
        public void onThumbFileDownload(int msg, int listPosition,
                                        ArrayList<HashMap<String, String>> newThumbs);
    }

    /**
     * 删除评论的监听器
     */
    public interface DeleteCommentListener {
        public void onCommentDeleted(int msg);
    }

    /**
     * 修改背景的监听器
     */
    public interface ChangeBackgroudListener {
        public void onBackgroudChanged();
    }

    /**
     * 请求一个评论完成后的回调 将云端下载但本地没有的评论插入到数据库，更新items
     */
    @SuppressLint("HandlerLeak")
    private Handler requestComment = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (!isDBReady) {
                Log.i("album", "db closed return");
                return;
            }
            switch (msg.what) {
                case 0: { // 成功获得信息
                    CloudComment cloudComment = GsonHelper.parseJson(msg.obj
                            .toString().trim(), CloudComment.class);
                    if (cloudComment == null) {
                        mDownComment.onCommentDownload(-1);
                        isAddingComment = false;
                        break;
                    }
                    LinkedList<InterestMarkerData> events = cloudComment.getEvent();

                    String more = "";
                    more = cloudComment.getMore();
                    if (more.equals("no")) {
                        cloudMore = false;
                    } else {
                        cloudMore = true;
                    }

                    // 获得一个可写的数据库，进行插入删除操作
                    PhotoDBHelper writedDbHelper = new PhotoDBHelper(context,
                            PhotoDBHelper.DBWRITE);
                    // 同步云端和数据内容
                    int eventsNum = events.size();

                    // 将本地没有的几行插入数据库
                    for (int i = 0; i < eventsNum; i++) {
                        writedDbHelper.insertEvent(events.get(i));
                        int fileNum = events.get(i).getImageCount();
                        // 插入文件表
                        for (int j = 0; j < fileNum; j++) {
                            CommentMediaFilesData ev = new CommentMediaFilesData();
                            ev.setDateTime(events.get(i).getCreateTime());
                            ev.setFileNo(j);
                            ev.setFileType(CommentMediaFilesData.TYPE_PIC);
                            writedDbHelper.inserFile(ev);
                        }
                    }

                    // 重新查询本地数据
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                    cursor = dbHelper.selectEvent(null,
                            PhotoDBHelper.COLUMNS_UE[10] + "=" + Common.getUserId(context),
                            null, null, null, "datetime("
                                    + PhotoDBHelper.COLUMNS_UE[0] + ") desc");
                    cursor.moveToPosition(numOfUE - 1);
                    numOfUE = cursor.getCount();

                    // 记录当前滚动到的位置
                    // int setPosition = lView.getFirstVisiblePosition() + 1;
                    int addNum = 0;
                    while (!cursor.isLast() && addNum < listOneTime) {
                        items.add(items.size() - 1, listAddGrid());
                        addNum++;
                    }

                    if (!cloudMore) {
                        // 删除末栏提示再添加以更改提示文本
                        items.get(items.size() - 1).put("listItem", "nomore");
                    }
                    //cursor.close();
                    writedDbHelper.closeDB();
                    break;
                }
                default:
                    break;

            }
            isAddingComment = false;
            mDownComment.onCommentDownload(msg.what);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler requestAlbum = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (!isDBReady) {
                Log.i("album", "db closed return");
                return;
            }
            switch (msg.what) {
                case 0: { // 成功获得信息
                    CloudComment cloudComment = GsonHelper.parseJson(msg.obj
                            .toString().trim(), CloudComment.class);
                    if (cloudComment == null) {
                        mDownComment.onCommentDownload(-1);
                        isAddingComment = false;
                        break;
                    }
                    LinkedList<InterestMarkerData> events = cloudComment.getEvent();

                    String more = "";
                    more = cloudComment.getMore();
                    Log.i("cloudComment", "requestAlbum:" + cloudComment.getMore());
                    if (more.equals("no")) {
                        cloudMore = false;
                    } else {
                        cloudMore = true;
                    }

                    // 获得一个可写的数据库，进行插入删除操作
                    PhotoDBHelper writedDbHelper = new PhotoDBHelper(context,
                            PhotoDBHelper.DBWRITE);
                    // 同步云端和数据内容
                    int eventsNum = events.size();

                    // 将本地没有的几行插入数据库
                    for (int i = 0; i < eventsNum; i++) {
                        writedDbHelper.insertEvent(events.get(i));
                        int fileNum = events.get(i).getImageCount();
                        // 插入文件表
                        for (int j = 0; j < fileNum; j++) {
                            CommentMediaFilesData ev = new CommentMediaFilesData();
                            ev.setDateTime(events.get(i).getCreateTime());
                            ev.setFileNo(j);
                            ev.setFileType(CommentMediaFilesData.TYPE_PIC);
                            writedDbHelper.inserFile(ev);
                        }
                    }

                    // 重新查询本地数据
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                    cursor = dbHelper.selectEvent(null,
                            PhotoDBHelper.COLUMNS_UE[10] + "=" + Common.getUserId(context),
                            null, null, null, "datetime("
                                    + PhotoDBHelper.COLUMNS_UE[0] + ") desc");
                    cursor.moveToPosition(numOfUE - 1);
                    numOfUE = cursor.getCount();

                    // 记录当前滚动到的位置
                    // int setPosition = lView.getFirstVisiblePosition() + 1;
                    int addNum = 0;
                    while (!cursor.isLast() && addNum < listOneTime) {
                        items.add(items.size() - 1, listAddGrid());
                        addNum++;
                    }

                    if (!cloudMore) {
                        // 删除末栏提示再添加以更改提示文本
                        items.get(items.size() - 1).put("listItem", "nomore");
                    }
                    //cursor.close();
                    writedDbHelper.closeDB();
                    break;
                }
                default:
                    break;

            }
            isAddingComment = false;
            mDownComment.onCommentDownload(msg.what);
        }
    };

    /**
     * 删除一条评论后的回调
     */
    class DeleteCloudEvent extends Handler {
        private int position = -1;

        public DeleteCloudEvent(int position) {
            this.position = position;
        }

        public DeleteCloudEvent() {

        }

        public void handleMessage(android.os.Message msg) {
            if (!isDBReady) {
                Log.i("album", "db closed return");
                return;
            }
            switch (msg.what) {
                case 0: { // 删除成功
                    if (position > -1) {
                        MyTraceDBHelper traceHelper = new MyTraceDBHelper(context);
                        TraceData tracedata = new TraceData();
                        InterestMarkerData comment = new InterestMarkerData();
                        comment = ((ListItemData) items.get(position).get("listItem")).getEvent();
                        long traceID = comment.getTraceID();
                        String userID = comment.getUserId();
                        tracedata = traceHelper.queryfromTrailbytraceID(traceID, userID);
                        if (tracedata != null) {
                            tracedata.setPoiCount(tracedata.getPoiCount() - 1);
                            traceHelper.updatetrail(tracedata, traceID, userID);
                        }
                        items.remove(position);
                    }
                    numOfUE--;
                    break;
                }
                default:
                    break;
            }
            // 通知UI线程删除结果
            mDeleteComment.onCommentDeleted(msg.what);
        }
    }

    ;

    /**
     * 刷新数据请求完成后的回调 如果数据请求成功，同步数据
     */
    @SuppressLint("HandlerLeak")
    private Handler refreshComment = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (!isDBReady) {
                Log.i("album", "db closed return");
                return;
            }
            switch (msg.what) {
                case 0: { // 成功获得信息
                    CloudComment cloudComment = GsonHelper.parseJson(msg.obj
                            .toString().trim(), CloudComment.class);
                    if (cloudComment == null) {
                        mDownComment.onCommentDownload(-1);
                        isAddingComment = false;
                        break;
                    }
                    LinkedList<InterestMarkerData> events = cloudComment.getEvent();
                    Log.i("msg.objjj", msg.obj.toString());

                    String more = "";
                    more = cloudComment.getMore();
                    if (more.equals("no")) {
                        cloudMore = false;
                    } else {
                        cloudMore = true;
                    }

                    int eventsNum = events.size();
                    // 同步云端和数据内容
                    int index = 0;
                    cursor.moveToFirst();
                    // 获得一个可写的数据库，进行插入删除操作
                    PhotoDBHelper writedDbHelper = new PhotoDBHelper(context,
                            PhotoDBHelper.DBWRITE);
                    String eventCTDB;
                    String eventCTCloud;
                    // 本地数据中有内容,
                    if (cursor.getCount() > 0) {
                        // 云端也有内容
                        if (eventsNum > 0) {
                            while (index < eventsNum) {
                                eventCTDB = cursor.getString(0);
                                eventCTCloud = events.get(index).getCreateTime();
                                //Log.i("album", ""+eventCTDB+","+eventCTCloud);
                                long leventCTDB = Common.timeStamp(eventCTDB);
                                long leventCTCloud = Common.timeStamp(eventCTCloud);
                                if (leventCTDB == leventCTCloud) {
                                    // 数据库中的一行和云端一行相等证明这一行数据已经同步，数据库和云端数据皆前移一行
                                    Log.i("Eaa_equal", "" + eventCTDB);
                                    index++;
                                    if (!cursor.moveToNext()) {
                                        break;
                                    }
                                } else if (leventCTDB > leventCTCloud) {
                                    // 数据库中一行大于云端的一行，该行数据已经被其它设备删除，删除数据库中这一行,云端不前移
                                    writedDbHelper
                                            .deleteEvent("datetime(CreateTime) = datetime('"
                                                    + eventCTDB + "')");
                                    Log.i("Eaa_delete",
                                            "getComment delete event:"
                                                    + eventCTDB);
                                    if (!cursor.moveToNext()) {
                                        break;
                                    }
                                } else {
                                    // 数据库中的一行小于云端的一行，云端一行本地没有，插入该行,数据库cursor不前移
                                    writedDbHelper.insertEvent(events.get(index));
                                    Log.i("Eaa_insert",
                                            "getComment insert event:"
                                                    + eventCTCloud);
                                    // 插入文件表
                                    int fileNum = events.get(index).getImageCount();
                                    for (int j = 0; j < fileNum; j++) {
                                        CommentMediaFilesData ev = new CommentMediaFilesData();
                                        ev.setDateTime(events.get(index)
                                                .getCreateTime());
                                        ev.setFileNo(j);
                                        ev.setFileType(CommentMediaFilesData.TYPE_PIC);
                                        writedDbHelper.inserFile(ev);
                                    }
                                    index++;
                                }
                            }
                            if (!cursor.isAfterLast() && !cloudMore) {
                                // 如果本地数据多于云端，删除本地数据多出的部分

                                do {
                                    eventCTDB = cursor.getString(0);
                                    writedDbHelper
                                            .deleteEvent("datetime(CreateTime) = datetime('"
                                                    + eventCTDB + "')");

                                } while (cursor.moveToNext());
                            }

                        } else { // 如果云端没有内容，删除本地所有内容
                            writedDbHelper.deleteEvent(null);
                        }
                    }

                    // 将本地没有的几行插入数据库
                    for (int i = index; i < eventsNum; i++) {
                        writedDbHelper.insertEvent(events.get(i));
                        int fileNum = events.get(i).getImageCount();
                        // 插入文件表
                        for (int j = 0; j < fileNum; j++) {
                            CommentMediaFilesData ev = new CommentMediaFilesData();
                            ev.setDateTime(events.get(i).getCreateTime());
                            ev.setFileNo(j);
                            ev.setFileType(CommentMediaFilesData.TYPE_PIC);
                            writedDbHelper.inserFile(ev);
                        }
                    }

                    if (from.equals("album")) {
                        initItems();
                    } else if (from.equals("mark")) {
                        initItemsByTime(startTime, endTime);
                        Log.i("mark", "更新标注handler");
                    }
                    writedDbHelper.closeDB();
                    break;
                }
                default:
                    break;
            }
            isAddingComment = false;
            mDownComment.onCommentDownload(msg.what);
        }

    };

    @SuppressLint("HandlerLeak")
    private Handler refreshAlbum = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (!isDBReady) {
                Log.i("album", "db closed return");
                return;
            }
            switch (msg.what) {
                case 0: { // 成功获得信息
                    CloudComment cloudComment = GsonHelper.parseJson(msg.obj
                            .toString().trim(), CloudComment.class);
                    if (cloudComment == null) {
                        mDownComment.onCommentDownload(-1);
                        isAddingComment = false;
                        break;
                    }
                    LinkedList<InterestMarkerData> events = cloudComment.getEvent();
                    Log.i("msg.objjj", msg.obj.toString());

                    String more = "";
                    more = cloudComment.getMore();
                    Log.i("cloudComment", cloudComment.getMore());
                    if (more.equals("no")) {
                        cloudMore = false;
                    } else {
                        cloudMore = true;
                    }

                    int eventsNum = events.size();
                    // 同步云端和数据内容
                    int index = 0;
                    cursor.moveToFirst();
                    // 获得一个可写的数据库，进行插入删除操作
                    PhotoDBHelper writedDbHelper = new PhotoDBHelper(context,
                            PhotoDBHelper.DBWRITE);
                    String eventCTDB;
                    String eventCTCloud;
                    // 本地数据中有内容,
                    if (cursor.getCount() > 0) {
                        // 云端也有内容
                        if (eventsNum > 0) {
                            while (index < eventsNum) {
                                eventCTDB = cursor.getString(0);
                                eventCTCloud = events.get(index).getCreateTime();
                                //Log.i("album", ""+eventCTDB+","+eventCTCloud);
                                long leventCTDB = Common.timeStamp(eventCTDB);
                                long leventCTCloud = Common.timeStamp(eventCTCloud);
                                if (leventCTDB == leventCTCloud) {
                                    // 数据库中的一行和云端一行相等证明这一行数据已经同步，数据库和云端数据皆前移一行
                                    Log.i("Eaa_equal", "" + eventCTDB);
                                    index++;
                                    if (!cursor.moveToNext()) {
                                        break;
                                    }
                                } else if (leventCTDB > leventCTCloud) {
                                    // 数据库中一行大于云端的一行，该行数据已经被其它设备删除，删除数据库中这一行,云端不前移
                                    writedDbHelper
                                            .deleteEvent("datetime(CreateTime) = datetime('"
                                                    + eventCTDB + "')");
                                    Log.i("Eaa_delete",
                                            "getComment delete event:"
                                                    + eventCTDB);
                                    if (!cursor.moveToNext()) {
                                        break;
                                    }
                                } else {
                                    // 数据库中的一行小于云端的一行，云端一行本地没有，插入该行,数据库cursor不前移
                                    writedDbHelper.insertEvent(events.get(index));
                                    Log.i("Eaa_insert",
                                            "getComment insert event:"
                                                    + eventCTCloud);
                                    // 插入文件表
                                    int fileNum = events.get(index).getImageCount();
                                    for (int j = 0; j < fileNum; j++) {
                                        CommentMediaFilesData ev = new CommentMediaFilesData();
                                        ev.setDateTime(events.get(index)
                                                .getCreateTime());
                                        ev.setFileNo(j);
                                        ev.setFileType(CommentMediaFilesData.TYPE_PIC);
                                        writedDbHelper.inserFile(ev);
                                    }
                                    index++;
                                }
                            }
                            if (!cursor.isAfterLast() && !cloudMore) {
                                // 如果本地数据多于云端，删除本地数据多出的部分

                                do {
                                    eventCTDB = cursor.getString(0);
                                    writedDbHelper
                                            .deleteEvent("datetime(CreateTime) = datetime('"
                                                    + eventCTDB + "')");

                                } while (cursor.moveToNext());
                            }

                        } else { // 如果云端没有内容，删除本地所有内容
                            writedDbHelper.deleteEvent(null);
                        }
                    }

                    // 将本地没有的几行插入数据库
                    for (int i = index; i < eventsNum; i++) {
                        writedDbHelper.insertEvent(events.get(i));
                        int fileNum = events.get(i).getImageCount();
                        // 插入文件表
                        for (int j = 0; j < fileNum; j++) {
                            CommentMediaFilesData ev = new CommentMediaFilesData();
                            ev.setDateTime(events.get(i).getCreateTime());
                            ev.setFileNo(j);
                            ev.setFileType(CommentMediaFilesData.TYPE_PIC);
                            writedDbHelper.inserFile(ev);
                        }
                    }

                    if (from.equals("album")) {
                        initItems();
                    } else if (from.equals("mark")) {
                        initItemsByTime(startTime, endTime);
                        Log.i("mark", "更新标注handler");
                    }
                    writedDbHelper.closeDB();
                    break;
                }
                default:
                    break;
            }
            isAddingComment = false;
            mDownComment.onCommentDownload(msg.what);
        }

    };

    /**
     * 请求一个事件的全部缩略图事件完成后的回调。 如果成功，更新数据库中缩略图的记录，并通知UI线程重新加载缩略图
     */
    private class RequestThembFiles extends Handler {
        ArrayList<HashMap<String, String>> images;
        String                             createTime;
        int                                position;

        public RequestThembFiles(int position, String createTime) {
            this.createTime = createTime;
            this.images = new ArrayList<HashMap<String, String>>();
            this.position = position;
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            if (!isDBReady) {
                Log.i("album", "db closed return");
                return;
            }
            switch (msg.what) {
                case 0: { // 成功获得信息
                    Gson gson = new Gson();
                    LinkedList<DownThumbData> thumbs = new LinkedList<DownThumbData>();
                    try {
                        java.lang.reflect.Type fileType = new TypeToken<LinkedList<DownThumbData>>() {
                        }.getType();
                        thumbs = gson.fromJson(msg.obj.toString().trim(), fileType);
                    } catch (Exception e) {
                        mDownThumbFile.onThumbFileDownload(-1, position, images);
                        break;
                    }
                    // Log.d("Eaa", msg.obj.toString());
                    if (thumbs.size() == 0) {
                        return;
                    }
                    PhotoDBHelper writeDBHelper = new PhotoDBHelper(context,
                            PhotoDBHelper.DBWRITE);
                    // 加载缩略图到gridView,同时更新EventFiles 数据库表
                    images.removeAll(images);
                    int index = 0;
                    for (Iterator iterator = thumbs.iterator(); iterator.hasNext(); ) {
                        DownThumbData thumbPic = (DownThumbData) iterator.next();
                        int fileType = 1;
                        if (thumbPic.getFileType().equals("pic")) {
                            fileType = CommentMediaFilesData.TYPE_PIC;
                        } else if (thumbPic.getFileType().equals("video")) {
                            fileType = CommentMediaFilesData.TYPE_VIDEO;
                        }
                        // 将缩略图写到本地文件夹

                        byte[] picByte = Base64.decode(thumbPic.getPicByte(),
                                Base64.DEFAULT);
                        BufferedOutputStream bos = null;
                        String imageName = Common.currentTimeMill();
                        File file = new File(Common.CACHEPHOTO_PATH
                                + File.separator + thumbPic.getFileId() + imageName
                                + ".jpg");
                        FileOutputStream fos;
                        try {
                            fos = new FileOutputStream(file);

                            /**
                             * 将该缩略图记录到EventFiles表对应文件
                             */
                            ContentValues cv = new ContentValues();
                            cv.put(PhotoDBHelper.COLUMNS_FILE[3], fileType);
                            cv.put(PhotoDBHelper.COLUMNS_FILE[4],
                                    file.getAbsolutePath());
                            // 更新数据库中的缩略图
                            writeDBHelper
                                    .updateFile(cv, "datetime("
                                            + PhotoDBHelper.COLUMNS_FILE[2]
                                            + ")=datetime('" + createTime
                                            + "') AND "
                                            + PhotoDBHelper.COLUMNS_FILE[0] + " = "
                                            + index);
                            bos = new BufferedOutputStream(fos);
                            bos.write(picByte);
                            bos.close();
                            picByte = null;
                            index++;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        String imgPaht = file.getAbsolutePath();
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("itemImage", imgPaht);
                        images.add(map);
                    }

                    // 更新items，保证ListView的同步更新
                    Cursor fileCursor = dbHelper.selectFiles(null, "datetime("
                            + PhotoDBHelper.COLUMNS_UE[0] + ")=datetime('"
                            + createTime + "')", null, null, null, null);
                    CommentMediaFilesData files[] = new CommentMediaFilesData[fileCursor.getCount()];
                    int cursorIndex = 0;
                    while (fileCursor.moveToNext()) {
                        files[cursorIndex] = new CommentMediaFilesData(fileCursor.getInt(0),
                                fileCursor.getString(1), fileCursor.getString(2),
                                fileCursor.getInt(3), fileCursor.getString(4), fileCursor.getInt(5));
                        cursorIndex++;
                    }
                    if (position < items.size()) {
                        ((ListItemData) (items.get(position).get("listItem")))
                                .setFiles(files);
                    }
                    fileCursor.close();
                    writeDBHelper.closeDB();
                    break;
                }
                default:

            }
            mDownThumbFile.onThumbFileDownload(msg.what, position, images);
        }
    }

    /**
     * 请求一个文件
     */
    private class RequestFile extends Handler {
        private String cloudPicture = null;
        private String createTime;
        private int    listPosition;
        private int    fileNo;
        private int    fileType;

        public RequestFile(int listPosition, int no, int type) {
            this.listPosition = listPosition;
            this.createTime = ((ListItemData) (items.get(listPosition)
                    .get("listItem"))).getTime();
            this.fileNo = no;
            this.fileType = type;
        }

        public void handleMessage(android.os.Message msg) {
            if (!isDBReady) {
                Log.i("album", "db closed return");
                return;
            }
            switch (msg.what) {
                case 0: { // 成功获得信息
                    // 如果请求的文件是图片
                    if (fileType == CommentMediaFilesData.TYPE_PIC) {
                        String fileStr = msg.obj.toString().trim();
                        if (null == fileStr || "noPic".equals(fileStr) || "null".equals(fileStr)) {
                            mDownFile.onFileDownload(-1, listPosition, fileNo);
                            return;
                        }

                        Log.i("Eaa", "File downloaded:");
                        byte[] picByte = Base64.decode(fileStr, Base64.DEFAULT);
                        BufferedOutputStream bos = null;
                        PhotoDBHelper writeDBHelper = new PhotoDBHelper(context, PhotoDBHelper.DBWRITE);
                        String imageName = Common.currentTimeMill();

                        File file = new File(Common.PHOTO_PATH + imageName + "_cloud.jpg");
                        FileOutputStream fos;
                        try {
                            fos = new FileOutputStream(file);
                            bos = new BufferedOutputStream(fos);
                            bos.write(picByte);
                            bos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        cloudPicture = file.getAbsolutePath();
                        /**
                         * 将该云端图记录到EventFiles表对应文件
                         */
                        ContentValues cv = new ContentValues();
                        cv.put(PhotoDBHelper.COLUMNS_FILE[1],
                                file.getAbsolutePath());
                        int result = writeDBHelper.updateFile(cv, "datetime("
                                + PhotoDBHelper.COLUMNS_FILE[2] + ")=datetime('"
                                + createTime + "') AND "
                                + PhotoDBHelper.COLUMNS_FILE[0] + " = " + fileNo);
                        Log.i("Eaa", "datetime(" + PhotoDBHelper.COLUMNS_FILE[2]
                                + ")=datetime('" + createTime + "') AND "
                                + PhotoDBHelper.COLUMNS_FILE[0] + " = " + fileNo);

                        Log.i("Eaa", "downFile result:" + result);
                        CommentMediaFilesData updateFile = ((ListItemData) items.get(
                                listPosition).get("listItem")).getFiles()[fileNo];
                        updateFile.setFileName(cloudPicture);
                        // 更新list
                        ((ListItemData) items.get(listPosition).get("listItem"))
                                .setOneFile(fileNo, updateFile);

                        writeDBHelper.closeDB();
                        mDownFile.onFileDownload(msg.what, listPosition, fileNo);//修改后，第一次下载这里会因为空指针崩溃
                        break;
                    }
                    // 如果请求的文件是视频
                    else if (fileType == CommentMediaFilesData.TYPE_VIDEO) {
                        String fileStr = msg.obj.toString().trim();
                        // Log.i("Eaa_video_url", fileStr);
                        if (fileStr == "noVideo" || "null".equals(fileStr)
                                || "noPic".equals(fileStr)) {
                            mDownFile.onFileDownload(-2, listPosition, fileNo);
                            return;
                        }

                        String videoName = Common.PHOTO_PATH
                                + fileStr.substring(fileStr.lastIndexOf("/") + 1,
                                fileStr.length());
                        HttpUtils http = new HttpUtils();
                        http.download(fileStr, videoName, true, true,
                                new RequestCallBack<File>() {

                                    @Override
                                    public void onSuccess(ResponseInfo<File> arg0) {
                                        // TODO Auto-generated method stub
                                        PhotoDBHelper writeDBHelper = new PhotoDBHelper(
                                                context, PhotoDBHelper.DBWRITE);
                                        // 更新数据库中文件的记录
                                        ContentValues cv = new ContentValues();
                                        cv.put(PhotoDBHelper.COLUMNS_FILE[1],
                                                arg0.result.getAbsolutePath());
                                        writeDBHelper.updateFile(cv, "datetime("
                                                + PhotoDBHelper.COLUMNS_FILE[2]
                                                + ")=datetime('" + createTime
                                                + "') AND "
                                                + PhotoDBHelper.COLUMNS_FILE[0]
                                                + " = " + fileNo);

                                        CommentMediaFilesData updateFile = ((ListItemData) items
                                                .get(listPosition).get("listItem"))
                                                .getFiles()[fileNo];
                                        updateFile.setFileName(arg0.result
                                                .getAbsolutePath());
                                        // 更新list
                                        ((ListItemData) items.get(listPosition)
                                                .get("listItem")).setOneFile(
                                                fileNo, updateFile);
                                        writeDBHelper.closeDB();
                                        mDownFile.onFileDownload(0, listPosition, fileNo);
                                    }

                                    @Override
                                    public void onFailure(HttpException arg0,
                                                          String arg1) {
                                        // TODO Auto-generated method stub
                                        mDownFile.onFileDownload(-2, listPosition,
                                                fileNo);

                                    }

                                    @Override
                                    public void onLoading(long total, long current,
                                                          boolean isUploading) {
                                        // TODO Auto-generated method stub
                                        super.onLoading(total, current, isUploading);
                                    }
                                });
                        break;
                    }
                }

                default:
                    mDownFile.onFileDownload(msg.what, listPosition, fileNo);//第二次下载时，这里会因为空指针崩溃
                    break;

            }

        }

    }

    // 解析云端发来Json数据用gson对应类
    private class CloudComment {
        private LinkedList<InterestMarkerData> List;
        String More;

        public String getMore() {
            return More;
        }

        public LinkedList<InterestMarkerData> getEvent() {
            return this.List;
        }
    }

	/*private void delay(int ms){
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
     }*/
}
