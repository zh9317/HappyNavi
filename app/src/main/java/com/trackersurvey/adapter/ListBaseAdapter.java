package com.trackersurvey.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trackersurvey.bean.CommentMediaFilesData;
import com.trackersurvey.bean.ListItemData;
import com.trackersurvey.db.PointOfInterestDBHelper;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.happynavi.SelectedPictureActivity;
import com.trackersurvey.model.MyCommentModel;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.NoScrollGridView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zh931 on 2018/5/20.
 */

public class ListBaseAdapter extends BaseAdapter {
    private static final int                                ItemType_image = 0;
    private static final int                                ItemType_grid  = 1;
    private static final int                                ItemType_text  = 2;
    private static final int                                typeCount      = ItemType_text + 1;
    private static final int                                leftBlankWidth = 105; // 左边空白的宽度
    private static final int                                gridColumn     = 4; // gridView的列数
    private static       int                                colWidth       = (Common.winWidth - leftBlankWidth - gridColumn * 5)
            / gridColumn - 5; // gridView的列宽
    private              Context                            context;
    private              ArrayList<HashMap<String, Object>> items;
    private              HashMap<String, ProgressBar>       downloadingFiles;
    private              HashMap<Integer, GridItemAdapter>  downloadingThumbs;
    private              HashMap<String, String>            picture;
    private              ImageView                          backImage;
    private              String                             bgImageName    = "bgImage.jpg";

    private BackImageListener mbackImageListener;
    // private ButtomTextListener buttomTextListener;
    private DeleCommListener  deleCommentListener;

    private MyCommentModel myComment;
    private String         from;//谁使用了这个适配器
    private int            posInItems = -1;
    //	private ArrayList<String> pathes = new ArrayList<String>();
    //	private ArrayList<String> thumbpathes = new ArrayList<String>();
    //	private int count;
    private Intent         intent;
    //	private String dataComment = "";

    public ListBaseAdapter(Context context, MyCommentModel model,
                           ArrayList<HashMap<String, Object>> items, String from) {
        this.context = context;
        this.myComment = model;
        this.items = items;
        this.from = from;
        downloadingFiles = new HashMap<String, ProgressBar>();
        downloadingThumbs = new HashMap<Integer, GridItemAdapter>();

        myComment.setmDownThumbFile(thumbDownloaded);
        myComment.setmDownFile(fileDownloaded);
    }

    public ListBaseAdapter(Context context, MyCommentModel model,
                           ArrayList<HashMap<String, Object>> items, String from, int posInItems) {
        this.context = context;
        this.myComment = model;
        this.items = items;
        this.from = from;
        this.posInItems = posInItems;
        downloadingFiles = new HashMap<String, ProgressBar>();
        downloadingThumbs = new HashMap<Integer, GridItemAdapter>();

        myComment.setmDownThumbFile(thumbDownloaded);
        myComment.setmDownFile(fileDownloaded);
    }

    public void setItems(ArrayList<HashMap<String, Object>> items) {
        this.items = items;
        //		pathes.clear();
        //		thumbpathes.clear();
        //		for(int i = 0;i<items.size();i++){
        //        	CommentMediaFiles[] lid = ((ListItemData) items.get(i)
        //					.get("listItem")).getFiles();
        //        	for(int j = 0;j<lid.length;j++){
        //        		String pathName = lid[j].getFileName();
        //        		String thumbName = lid[j].getThumbnailName();
        //        		pathes.add(pathName);
        //        		thumbpathes.add(thumbName);
        //        	}
        //        }

    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        if (position == 0) {
            return ItemType_image;
        }
        if (items.get(position).get("listItem") instanceof String) {
            return ItemType_text;
        }

        return ItemType_grid;
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return typeCount;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int listPosition = position;
        ListViewHolder holder = new ListViewHolder();
        final ListItemData itemEntity;//
        boolean allThumb = true;

        int type = getItemViewType(listPosition);
        if (from.equals("mark")) {
            type = ItemType_grid;
        }
        switch (type) {
            case ItemType_image: {
                // 我的相册首栏背景图片
                convertView = View.inflate(context, R.layout.list_topimage, null);
                backImage = (ImageView) convertView
                        .findViewById(R.id.imageViewOfList);
                android.view.ViewGroup.LayoutParams params = backImage
                        .getLayoutParams();
                params.height = Common.winHeight / 3;
                backImage.setLayoutParams(params);
                // 读取背景图片
                FileInputStream fis;
                Bitmap bmp = null;
                try {
                    fis = context.openFileInput(bgImageName);
                    bmp = BitmapFactory.decodeStream(fis);
                    backImage.setImageBitmap(bmp);
                } catch (FileNotFoundException e) {

                    // Log.i("Eaa_bgImg", "未设置我的相册背景图片");
                } catch (NullPointerException e) {
                    // Log.i("Eaa_bgImg", "未设置我的相册背景图片");
                } catch (OutOfMemoryError e) {

                }
                backImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        mbackImageListener.backImageClick();
                    }
                });
                break;
            }
            case ItemType_grid: {
                // 初始化嵌套了gridView的listItem各个组件
                if (null == convertView) {
                    convertView = View.inflate(context, R.layout.poi_list_item, null);
                    holder.tv_time = (TextView) convertView
                            .findViewById(R.id.tv_time);
                    holder.tv_place = (TextView) convertView
                            .findViewById(R.id.tv_place);
                    holder.tv_partner_num = (TextView) convertView
                            .findViewById(R.id.tv_partner_num);
                    holder.tv_relation = (TextView) convertView
                            .findViewById(R.id.tv_relation);
                    holder.tv_duration = (TextView) convertView
                            .findViewById(R.id.tv_duration);
                    holder.tv_comment = (TextView) convertView
                            .findViewById(R.id.tv_comment);
                    holder.iv_feeling = (ImageView) convertView
                            .findViewById(R.id.iv_feeling);
                    holder.tv_behaviour = (TextView) convertView
                            .findViewById(R.id.tv_behaviour);
                    holder.gridview = (NoScrollGridView) convertView
                            .findViewById(R.id.myCommentGridView);
                    holder.delete = (RelativeLayout) convertView
                            .findViewById(R.id.listview_delete);
                    //				holder.trace = (RelativeLayout) convertView
                    //						.findViewById(R.id.listview_path);
                    convertView.setTag(holder);
                } else {
                    holder = (ListViewHolder) convertView.getTag();
                }
                break;
            }
            case ItemType_text: {
                convertView = View.inflate(context, R.layout.list_bottomtext, null);
                TextView bottomText = (TextView) convertView
                        .findViewById(R.id.bottomTextOflist);
                bottomText
                        .setText((String) items.get(listPosition).get("listItem"));
                bottomText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        // buttomTextListener.onButtomText();
                    }
                });
                break;
            }
        }
        if (type == ItemType_text) {
            TextView bottomText = (TextView) convertView.findViewById(R.id.bottomTextOflist);
            bottomText.setText((String) items.get(listPosition).get("listItem"));
            return convertView;

        }
        if (type == ItemType_image) {
            return convertView;
        }
        //Log.i("Eaa", "listPosition" + listPosition + " ||");
        itemEntity = (ListItemData) items.get(listPosition).get("listItem");
        int poiID = itemEntity.getPoiID();
        Log.i("itemEntity", "itemEntity = " + itemEntity.toString() + " poiID: " + poiID);
        ArrayList<String> partnerNum = new ArrayList<String>();
        ArrayList<String> relation = new ArrayList<String>();
        ArrayList<String> duration = new ArrayList<String>();
        ArrayList<String> behaviour = new ArrayList<String>();
        PointOfInterestDBHelper helper = new PointOfInterestDBHelper(context);
        partnerNum = helper.getPartnerNum();
        relation = helper.getRelation();
        duration = helper.getDuration();
        behaviour = helper.getBehaviour();
        holder.tv_time.setText(itemEntity.getTime());
        holder.tv_place.setText(itemEntity.getPlace());
        holder.tv_comment.setText(itemEntity.getComment());
        if (partnerNum != null && relation != null && duration != null && behaviour != null) {
            holder.tv_partner_num.setText(partnerNum.get(itemEntity.getCompanion()));
            holder.tv_relation.setText(relation.get(itemEntity.getRelation()));
            holder.tv_duration.setText(duration.get(itemEntity.getDuration()));
            holder.tv_behaviour.setText(behaviour.get(itemEntity.getBehaviour()));
        }
        switch (itemEntity.getFeeling()) {
            case 0:
                holder.iv_feeling.setImageResource(R.mipmap.ic_happy);
                //holder.tv_feeling.setText(R.string.happy);
                break;
            case 1:
                holder.iv_feeling.setImageResource(R.mipmap.ic_general);
                //holder.tv_feeling.setText(R.string.general);
                break;
            case 2:
                holder.iv_feeling.setImageResource(R.mipmap.ic_unhappy);
                //holder.tv_feeling.setText(R.string.unhappy);
                break;
        }
        final String dateTime = itemEntity.getTime();

        String dataComment = itemEntity.getComment();
        // 点击删除按钮，云端和本地删除该评论
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //                deleCommentListener.clickDelete(dateTime, listPosition);
                deleCommentListener.clickDelete(dateTime, itemEntity.getPoiID(), itemEntity.getTraceID());
            }
        });

        //		holder.trace.setVisibility(View.GONE);

        // 用户事件文件
        CommentMediaFilesData imageUrls[] = itemEntity.getFiles();
        Log.i("dongsiyuanimageUrls", "getView: " + imageUrls.length + " imageUrls: " + imageUrls[0].toString() );
        ArrayList<HashMap<String, String>> imageItems = new ArrayList<HashMap<String, String>>();
        holder.gridview.setVisibility(View.VISIBLE);
        if (imageUrls == null || imageUrls.length == 0) { // 没有图片资源就隐藏GridView
            Log.i("album", "position = " + listPosition + "," + itemEntity.getComment() + ",files = 0");
            holder.gridview.setVisibility(View.GONE);
            holder.delete.setLayoutParams(new LinearLayout.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT, 0, 3));
        } else {
            for (int i = 0; i < imageUrls.length; i++) {
                String thumbPic = imageUrls[i].getThumbnailName();
                HashMap<String, String> map = new HashMap<String, String>();
                // 默认灰色背景
                String imgPath = "";
                // 如果本地没有缩略图，不加载图片，向云端请求
                if (thumbPic == null || "".equals(thumbPic)) {
                    allThumb = false;
                } else {
                    // 解码缩略图放入gridView
                    File imgFile = new File(thumbPic);
                    Log.i("dongiysuanimgFile", "getView: " + imgFile);
                    if (imgFile.exists()) {
                        imgPath = thumbPic;
                    } else {
                        allThumb = false;
                    }
                }
                map.put("itemImage", imgPath);
                imageItems.add(map);
            }
            holder.sAdapter = new GridItemAdapter(context, imageItems, colWidth);
            holder.gridview.setAdapter(holder.sAdapter);
            // 点击九宫格，查看大图，当该评论所有图片本地都存在时，可滑动查看所有图片，
            // 当该评论有>=1张图片未成功下载时，只能查看点击的图片，不能滑动到相邻的图片。望以后改进！
            holder.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                //private boolean isDownloaded;

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //计算所点击的item之前的所有照片数
                    int count = 0;
                    CommentMediaFilesData[] lid1 = null;
                    for (int j = 0; j < listPosition; j++) {
                        lid1 = ((ListItemData) items.get(j).get("listItem")).getFiles();
                        for (int k = 0; k < lid1.length; k++) {
                            count++;
                        }
                    }

                    CommentMediaFilesData[] lid5 = ((ListItemData) items.get(listPosition).get("listItem")).getFiles();
                    boolean isDownloaded = true;
                    boolean isClickedDownloaded = true;
                    int clickedType = lid5[position].getFileType();
                    String clickedpathName = lid5[position].getFileName();
                    String clickedThumbName = lid5[position].getThumbnailName();
                    ArrayList<String> pathes = new ArrayList<String>();
                    ArrayList<String> thumbpathes = new ArrayList<String>();
                    Log.i("dongsiyuanclickedType", "onItemClick: " + clickedType);

                    //下载所有未下载的图片
                    for (int j = 0; j < items.size(); j++) {
                        CommentMediaFilesData[] lid = ((ListItemData) items.get(j).get("listItem")).getFiles();
                        Log.i("lidddd", "" + lid.length);
                        for (int i = 0; i < lid.length; i++) {
                            int type = lid[i].getFileType();
                            if (type == clickedType) {
                                String pathName = lid[i].getFileName();
                                String thumbpathName = lid[i].getThumbnailName();
                                int FileID = lid[i].getFileID();
                                Log.i("dongisyuanFileID", "onItemClick: " + FileID);
                                pathes.add(pathName);
                                thumbpathes.add(thumbpathName);
                                //Log.i("pathes", pathes.toString());
                                // 如果本地没有该图片，向云端请求
                                if ("".equals(pathName) || null == pathName
                                        || !(new File(pathName).exists())) {
                                    ProgressBar pb = (ProgressBar) view.findViewById(R.id.down_img_pb);
                                    pb.setVisibility(View.VISIBLE);          //出现一个环形进度条
                                    if (posInItems > -1) {
                                        downFile(posInItems, i, type, pb, FileID);
                                        Log.i("dongsiyuanpos-1", "posInItems=" + posInItems + " fileposition" + i);
                                    } else {
                                        downFile(j, i, type, pb, FileID);
                                        Log.i("dongsiyuanpos", "posInItems=" + j + " fileposition" + i);
                                    }
                                    view.setClickable(false);
                                    isDownloaded = false;
                                    if (i == position) {
                                        isClickedDownloaded = false;
                                    }
                                }
                                Log.i("bitmap", "isDownloaded=" + isDownloaded);
                                Log.i("bitmap", "isClickedDownloaded=" + isClickedDownloaded);
                                Log.i("bitmap", "pathes2=" + pathes);
                            }
                        }
                    }
                    //如果本地有图片
                    if (clickedType == CommentMediaFilesData.TYPE_PIC && isDownloaded) {
                        Intent intent = new Intent(context,
                                SelectedPictureActivity.class);
                        intent.putStringArrayListExtra(
                                SelectedPictureActivity.PIC_PATH, pathes);
                        intent.putExtra(SelectedPictureActivity.PIC_POSITION, count + position);
                        Log.i("bitmap", "pathes=" + pathes);

                        //将当前item的评论字符串传递出去
                        ListItemData itemEntity;
                        ArrayList<String> comment = new ArrayList<String>();
                        ArrayList<String> time = new ArrayList<String>();
                        ArrayList<Integer> feeling = new ArrayList<Integer>();
                        for (int i = 0; i < items.size(); i++) {
                            CommentMediaFilesData[] lid2 = ((ListItemData) items.get(i)
                                    .get("listItem")).getFiles();
                            itemEntity = (ListItemData) items.get(i).get("listItem");
                            for (int k = 0; k < lid2.length; k++) {
                                comment.add(itemEntity.getComment());
                                time.add(itemEntity.getTime());
                                feeling.add(itemEntity.getFeeling());
                            }
                        }
                        try {
                            intent.putStringArrayListExtra("tv_comment", comment);
                            intent.putStringArrayListExtra("time", time);
                            intent.putIntegerArrayListExtra("feeling", feeling);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Log.i("comment", comment.toString());
                        context.startActivity(intent);
                    } else if (clickedType == CommentMediaFilesData.TYPE_PIC && !isDownloaded && isClickedDownloaded) {
                        //							for(int i = 0;i<items.size();i++){
                        //								Log.i("isDownloaded", "进入外循环"+i+"次："+"isDownloaded="+isDownloaded+"isClickedDownloaded="+isClickedDownloaded);
                        //								CommentMediaFiles[] lid = ((ListItemData) items.get(i)
                        //										.get("listItem")).getFiles();
                        //								for(int j = 0;j<lid.length;j++){
                        //									Log.i("isDownloaded", "进入内循环"+j+"次："+"isDownloaded="+isDownloaded+"isClickedDownloaded="+isClickedDownloaded);
                        //									if(pathes.get(j)==null){
                        //										Log.i("isDownloaded", "第"+j+"张照片下载失败");
                        //										String replace = thumbpathes.get(j);
                        //										pathes.set(j, replace);
                        //										Log.i("isDownloaded", "插入了缩略图路径后：pathes="+pathes);
                        //									}
                        //								}
                        //							}
                        //							pathes.clear();
                        //							pathes.add(clickedpathName);
                        String failed = "failed";
                        Intent intent = new Intent(context,
                                SelectedPictureActivity.class);
                        intent.putExtra("failed", failed);
                        intent.putStringArrayListExtra(
                                SelectedPictureActivity.PIC_PATH, pathes);
                        intent.putStringArrayListExtra(
                                SelectedPictureActivity.THUMB_PATH, thumbpathes);
                        intent.putExtra(SelectedPictureActivity.PIC_POSITION, count + position);

                        //将当前item的评论字符串传递出去
                        Log.i("bitmap", "pathes3=" + pathes);
                        ListItemData itemEntity;
                        ArrayList<String> comment = new ArrayList<String>();
                        ArrayList<String> time = new ArrayList<String>();
                        ArrayList<Integer> feeling = new ArrayList<Integer>();
                        for (int i = 0; i < items.size(); i++) {
                            CommentMediaFilesData[] lid2 = ((ListItemData) items.get(i)
                                    .get("listItem")).getFiles();
                            itemEntity = (ListItemData) items.get(i).get("listItem");
                            for (int k = 0; k < lid2.length; k++) {
                                try {
                                    comment.add(itemEntity.getComment());
                                    time.add(itemEntity.getTime());
                                    feeling.add(itemEntity.getFeeling());
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                        try {
                            intent.putStringArrayListExtra("tv_comment", comment);
                            intent.putStringArrayListExtra("time", time);
                            intent.putIntegerArrayListExtra("feeling", feeling);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        context.startActivity(intent);
                        Log.i("isDownloaded", "这里执行了：isDownloaded=" + isDownloaded + ";isClickedDownloaded=" + isClickedDownloaded);
                        Log.i("isDownloaded", "这里执行了：pathes=" + pathes);
                        Log.i("isDownloaded", "这里执行了：thumbpathes=" + thumbpathes);
                    } else if (clickedType == CommentMediaFilesData.TYPE_VIDEO && isDownloaded) {
                        // 调用系统视频播放器
                        Uri uri = null;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File videoUri = new File(clickedpathName);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            uri = FileProvider.getUriForFile(context,  "com.trackersurvey.happynavi.fileProvider", videoUri);
                        } else {
                            uri = Uri.fromFile(videoUri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }

                        intent.setDataAndType(uri, "video/*");
                        context.startActivity(intent);
                    }

                }
            });

            // 如果不是全部缩略图都有
            if (!allThumb) {
                if (posInItems > -1) {
                    downThumbFile(holder.sAdapter, posInItems, dateTime, poiID);
                } else {
                    downThumbFile(holder.sAdapter, listPosition, dateTime, poiID);
                }
            }

        }
        return convertView;
    }

    /**
     * 通知Model下载文件
     */
    private void downFile(int listPosition, int filePosition, int type, ProgressBar pb, int fileID) {
        myComment.downloadFile(listPosition, filePosition, type, fileID);
        downloadingFiles.put("" + listPosition + filePosition, pb);
    }

    /**
     * 文件下载完后，停止进度条并隐藏
     */
    MyCommentModel.DownFileListener fileDownloaded = new MyCommentModel.DownFileListener() {
        @Override
        public void onFileDownload(int msg, int listPosition, int filePosition) {
            Message message = new Message();
//            ProgressBar pb = downloadingFiles.remove("" + listPosition + filePosition);
            if (msg == 0) {
                message.what = 3;
                message.arg1 = listPosition;
                message.arg2 = filePosition;
                handler.sendMessage(message);
            } else {
                message.what = 2;
                handler.sendMessage(message);
            }


//            if (pb != null) {
//                pb.setVisibility(View.GONE);
//            }
//            if (msg != 0) {
//                modelTips(msg);
//            }
        }
    };

    /**
     * 通知model下载缩略图
     * @param gridView
     * @param position
     * @param time
     * @param poiID
     */
    private void downThumbFile(GridItemAdapter gridView, int position, String time, int poiID) {
        if (!downloadingThumbs.containsKey(position)) {
            downloadingThumbs.put(position, gridView);
            myComment.downloadThumbFile(position, time, poiID);
        }
    }

    /**
     * 缩略图下载完后，更新该条评论的缩略图
     */
    MyCommentModel.DownThumbFileListener thumbDownloaded = new MyCommentModel.DownThumbFileListener() {
        @Override
        public void onThumbFileDownload(int msg, int listPosition, ArrayList<HashMap<String, String>> newThumbs) {
            Message message = new Message();
            if (msg == 0) {
                message.what = 0;
                message.arg1 = listPosition;
                message.obj = newThumbs;
                handler.sendMessage(message);
//                Log.i("Eaa", listPosition + "newThumbs:" + newThumbs.toString());
//                GridItemAdapter gView = downloadingThumbs.remove(listPosition);
//                if (null != gView) {
//                    gView.setItems(newThumbs).notifyDataSetChanged();
//                }
            } else {
                message.what = 2;
                handler.sendMessage(message);
//                modelTips(msg);
            }
        }
    };

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ArrayList<HashMap<String, String>> newThumbs = (ArrayList<HashMap<String, String>>) msg.obj;
                    int listPosition = msg.arg1;
                    Log.i("Eaa", listPosition + "newThumbs:" + newThumbs.toString());
                    GridItemAdapter gView = downloadingThumbs.remove(listPosition);
                    if (null != gView) {
                        gView.setItems(newThumbs).notifyDataSetChanged();
                    }
                    break;
                case 2:
                    modelTips(8);
                    break;
                case 3:
                    ProgressBar pb = downloadingFiles.remove("" + msg.arg1 + msg.arg2);
                    if (pb != null) {
                        pb.setVisibility(View.GONE);
                    }
            }
            return false;
        }
    });

    /**
     * 根据Model操作的回调，弹出Toast提醒用户
     */
    private void modelTips(int msg) {
        switch (msg) {
            case -2:
                Toast.makeText(context,
                        R.string.tips_postfail,
                        Toast.LENGTH_SHORT).show();
                break;
            case -1:
                Toast.makeText(context,
                        R.string.tips_postfail,
                        Toast.LENGTH_SHORT).show();
                break;
            case 1:// 获取数据不成功但连接了
                // Log.i("Eaa", "获取云端评论时提示" + "服务器忙，请稍后再试");
                Toast.makeText(context,
                        R.string.tips_postfail,
                        Toast.LENGTH_SHORT).show();
                break;
            case 8:
                // 查询有误
                Toast.makeText(context,
                        R.string.tips_postfail,
                        Toast.LENGTH_SHORT).show();
                break;
            case 10:// 连接失败
                Toast.makeText(
                        context,
                        R.string.tips_netdisconnect, Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }
    }

    /**
     * listview组件复用，防止“卡顿”
     */
    class ListViewHolder {
        private TextView         tv_time;
        private TextView         tv_place;
        private TextView         tv_comment;
        private TextView         tv_partner_num;
        private TextView         tv_relation;
        private TextView         tv_duration;
        private ImageView        iv_feeling;
        //private TextView tv_feeling;
        private TextView         tv_behaviour;
        private NoScrollGridView gridview;
        private GridItemAdapter  sAdapter;
        private RelativeLayout   delete;
        //		private RelativeLayout trace;
    }

    /**
     * 设置改变背景图片的监听器
     *
     * @author 易
     */
    public interface BackImageListener {
        void backImageClick();
    }

    public void setOnBackImageChange(BackImageListener mListener) {
        this.mbackImageListener = mListener;
    }

    /**
     * 设置底部提示文本的监听器
     */
    public interface ButtomTextListener {
        void onButtomText();
    }

    // public void setButtomTextListener(ButtomTextListener mListener) {
    // buttomTextListener = mListener;
    // }

    /**
     * 设置删除按钮的监听器
     */
    public interface DeleCommListener {
        void clickDelete(String dateTime, int position, long traceID);
    }

    public void setDeleCommListener(DeleCommListener mListener) {
        deleCommentListener = mListener;
    }
}
