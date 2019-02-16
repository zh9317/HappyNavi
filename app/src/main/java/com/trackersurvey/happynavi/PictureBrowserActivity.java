package com.trackersurvey.happynavi;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.adapter.ImageAdapter;
import com.trackersurvey.photoview.ImageWorker;
import com.trackersurvey.photoview.SelectedTreeMap;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class PictureBrowserActivity extends BaseActivity implements View.OnClickListener{

    // 界面中的各个组件
    private GridView gView; // 显示图片的GridView
    private Spinner tvDirName; // 左下角文件夹名字按钮
    private TextView tvPreview; // 右下角预览按钮
    private LinearLayout titleBack; // 标题栏左部回退按钮
    private TextView titleText; // 标题栏名字
    private Button titleRightButton; // 标题栏右部确认按钮

    private ArrayList<HashMap<String, Object>> imageItem; // gridView 组件集
    private int itemNo = 0; // 选中的图片数量

    public final static String RESULT_URIS = "result_uris";
    public final static String INTENT_CLAZZ = "clazz";
    private ImageWorker imageWorker;// 下载图片的异步线程类
    private TreeMap<Long, Uri> selectedTree;// 存放已选中的图片的id和uri数据
    private int selectFold = 0;
    private boolean hasViedo = false;

    private SelectedTreeMap selectedTreeMap = new SelectedTreeMap();
    // 存放有图片的文件夹名和对应路径
    private ArrayList<PicFolder> picFolders;
    private String[] dirs;

    private ImageAdapter adapter;
    private LoadLocalPicture cursorTask;// 获取本地图片数据的异步线程类
    private AlphaAnimation inAlphaAni;// 每个图片加载时渐隐渐显的效果动画
    private AlphaAnimation outAlphaAni;// 每个图片加载时渐隐渐显的效果动画

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_browser);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        AppManager.getAppManager().addActivity(this);
        tvDirName = (Spinner) findViewById(R.id.pbbuttomDirName);
        tvPreview = (TextView) findViewById(R.id.pbbuttomHint);
        gView = (GridView) findViewById(R.id.gridBrowserPicture);
        titleRightButton = (Button) findViewById(R.id.pbbuttomPreview);
        titleRightButton.setVisibility(View.GONE);
        titleRightButton.setOnClickListener(this);
        imageItem = new ArrayList<HashMap<String, Object>>();
        gView.setColumnWidth((Common.winWidth - 8) / 3);
        selectedTree = new TreeMap<Long, Uri>();
        picFolders = new ArrayList<PicFolder>();
        itemNo = 0;
        if (getIntent().getIntExtra("hasVideo", -1) >= 0) {
            itemNo++;
            hasViedo = true;
        }
        init();
    }
    /**
     * 初始化
     */
    private void init() {
        // 选中已选择的图片
        Intent intent = getIntent();
        if (intent.hasExtra(RESULT_URIS)) {
            selectedTree = ((SelectedTreeMap) intent
                    .getParcelableExtra(RESULT_URIS)).getTreeMap();
            if (selectedTree.size() > 0) {
                titleRightButton.setVisibility(View.VISIBLE);
                titleRightButton.setBackgroundColor(0x8866cc00);
            }
        }

        imageWorker = new ImageWorker(this);
        // 这个bitmap是GridView中每一个item默认时的图片
        Bitmap b = Bitmap.createBitmap(new int[] { 0x00000000 }, 1, 1,
                Bitmap.Config.ARGB_8888);
        // Bitmap b = BitmapFactory.decodeResource(getResources(),
        // R.drawable.ic_launcher);

        imageWorker.setLoadBitmap(b);
        adapter = new ImageAdapter((Common.winWidth - 8) / 3, imageWorker,
                this, opcl, ocbcl);
        gView.setAdapter(adapter);

        if (picFolders.size() > 0) {
            dirs = new String[picFolders.size()];
        } else {
            dirs = new String[1];
            dirs[0] = getResources().getString(R.string.allpic);
        }
        ArrayAdapter<String> dirAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dirs);
        dirAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tvDirName.setAdapter(dirAdapter);

        tvDirName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (picFolders.size() > 0) {
                    selectFold = position;
                    adapter.setOrigIdArray(picFolders.get(position).origIdArray);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }

        });

        loadData();
        initAnimation();
    }

    /**
     * GridView中每个item图片加载初始化动画-渐隐渐显的效果
     */
    private void initAnimation() {
        float fromAlpha = 0;
        float toAlpha = 1;
        int duration = 200;
        inAlphaAni = new AlphaAnimation(fromAlpha, toAlpha);
        inAlphaAni.setDuration(duration);
        inAlphaAni.setFillAfter(true);
        outAlphaAni = new AlphaAnimation(toAlpha, fromAlpha);
        outAlphaAni.setDuration(duration);
        outAlphaAni.setFillAfter(true);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        cursorTask = new LoadLocalPicture(this);// 获取本地图片的异步线程类
        /**
         * 回调接口。当完成本地图片数据的获取之后，回调LoadLoacalPhotoCursorTask类中的OnLoadPhotoCursor接口
         * 的onLoadPhotoSursorResult方法，把数据传递到了这里。
         */
        cursorTask.setOnLoadPhotoCursor(new OnLoadPhotoCursor() {
            @Override
            public void onLoadPhotoSursorResult(ArrayList<PicFolder> picFolds) {
                if (isNotNull(picFolds)) {
                    PictureBrowserActivity.this.picFolders = picFolds;
                    int size = picFolders.size();
                    // 设置文件夹名的spinner的内容和适配器
                    dirs = new String[size];
                    for (int i = 0; i < size; i++) {
                        dirs[i] = picFolders.get(i).dirName;
                    }
                    ArrayAdapter<String> dirAdapter = new ArrayAdapter<String>(
                            PictureBrowserActivity.this,
                            android.R.layout.simple_spinner_item, dirs);
                    dirAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    tvDirName.setAdapter(dirAdapter);

                    adapter.setOrigIdArray(picFolders.get(selectFold).origIdArray);
                    Set<Long> stk = selectedTree.keySet();
                    for (Long key : stk) {
                        adapter.putSelectMap(key, true);
                    }
                    itemNo += stk.size();
                    CharSequence text = itemNo + "/9";
                    tvPreview.setText(text);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        cursorTask.execute();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            // 选择文件夹
            case R.id.pbbuttomDirName:

                break;
            // 点击确认，返回结果到调用者Activity
            case R.id.pbbuttomPreview:
                selectedTreeMap.setTreeMap(selectedTree);
                Intent intent = new Intent();
                intent.putExtra(RESULT_URIS, selectedTreeMap);
                setResult(RESULT_OK, intent);
                finish();
                break;
            // 确认
            default:
                break;
        }
    }

    public class LoadLocalPicture extends AsyncTask<Object, Object, Object> {

        private final ContentResolver mContentResolver;
        private boolean mExitTasksEarly = false;// 退出任务线程的标志位
        private OnLoadPhotoCursor onLoadPhotoCursor;// 定义回调接口，获取解析到的数据

        private ArrayList<PicFolder> picFolds = new ArrayList<PicFolder>();

        public LoadLocalPicture(Context mContext) {
            mContentResolver = mContext.getContentResolver();
        }

        @Override
        protected Object doInBackground(Object... params) {
            PicFolder pf = null;
            Uri extUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = null;

            // 获取所有图片文件夹
            String[] totalPicFold = { MediaStore.Images.Media._ID };
            cursor = MediaStore.Images.Media.query(mContentResolver, extUri,
                    totalPicFold, MediaStore.Images.Media.SIZE +">=?", new String[]{20*1024 +""},
                    MediaStore.Images.Media.DATE_ADDED + " desc");
            int totalPicNum = cursor.getCount();
            int currunt = 0;
            if (totalPicNum > 0) {
                cursor.moveToFirst();
                pf = new PicFolder(getResources().getString(R.string.allpic),
                        -1, cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Images.Media._ID)),
                        totalPicNum);
                do { // 移到指定的位置，遍历数据库
                    long origId = cursor.getLong(0);
                    pf.uriArray.add(Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            origId + ""));

                    pf.origIdArray.add(origId);
                    cursor.moveToPosition(currunt);
                    currunt++;
                } while (cursor.moveToNext() && currunt < totalPicNum
                        && !mExitTasksEarly);
                picFolds.add(pf);
            }
            cursor.close();
            // 获取包含图片的文件夹
            String[] projectionFold = { MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_ID,
                    "count(" + MediaStore.Images.Media._ID + ")" };
            String selection = " 0==0) group by bucket_id --(";

            cursor = MediaStore.Images.Media.query(mContentResolver, extUri,
                    projectionFold, selection, null,
                    MediaStore.Images.Media.BUCKET_ID);

            while (cursor.moveToNext()) {
                String folder = cursor
                        .getString(cursor
                                .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                Long folderId = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                int count = cursor.getInt(3);
                // Log.i("Eaa", folder + "|" + folderId + "|" + count);
                pf = new PicFolder(folder, folderId, cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Images.Media._ID)), count);
                picFolds.add(pf);

            }
            cursor.close();
            // 获取图片id
            String[] projection = { MediaStore.Images.Media._ID };
            String where = MediaStore.Images.Media.BUCKET_ID + "=?";
            for (int i = 1; i < picFolds.size(); i++) {

                String[] selectionParas = { "" + picFolds.get(i).dirId };
                cursor = MediaStore.Images.Media.query(mContentResolver,
                        extUri, projection, where, selectionParas,
                        MediaStore.Images.Media.DATE_ADDED + " desc");

                int columnIndex = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media._ID);

                PicFolder pf1 = picFolds.get(i);
                int index = 0;
                int cursorCount = cursor.getCount();
                while (cursor.moveToNext() && index < cursorCount
                        && !mExitTasksEarly) { // 移到指定的位置，遍历数据库
                    long origId = cursor.getLong(columnIndex);
                    pf1.uriArray.add(Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            origId + ""));

                    pf1.origIdArray.add(origId);
                    cursor.moveToPosition(index);
                    index++;
                }
                cursor.close();// 关闭数据库
                System.out.println(cursorCount);
            }


            if (mExitTasksEarly) {
                picFolds = new ArrayList<PicFolder>();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Object o) {
            if (onLoadPhotoCursor != null && !mExitTasksEarly) {
                /**
                 * 查询完成之后，设置回调接口中的数据，把数据传递到Activity中
                 */
                onLoadPhotoCursor.onLoadPhotoSursorResult(picFolds);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled(); // To change body of overridden methods use
            // File | Settings | File Templates.
            mExitTasksEarly = true;
        }

        public void setExitTasksEarly(boolean exitTasksEarly) {
            this.mExitTasksEarly = exitTasksEarly;
        }

        public void setOnLoadPhotoCursor(OnLoadPhotoCursor onLoadPhotoCursor) {
            this.onLoadPhotoCursor = onLoadPhotoCursor;
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        cursorTask.setExitTasksEarly(true);
        imageWorker.setExitTasksEarly(true);
        imageWorker.clearCache();
    }

    /**
     * 判断list不为空
     *
     * @param list
     * @return
     */
    private static boolean isNotNull(ArrayList list) {
        return list != null && list.size() > 0;
    }

    public interface OnLoadPhotoCursor {
        public void onLoadPhotoSursorResult(ArrayList<PicFolder> picFolds);
    }

    /**
     * 点击GridView中每一项中的图片的监听
     */
    private ImageAdapter.OnPictureClickListener opcl = new ImageAdapter.OnPictureClickListener() {

        @Override
        public void onPictureClick(View view, int position, long id) {
            // TODO Auto-generated method stub
            Uri muri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id + "");
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(muri, projection, null,
                    null, null);
            int index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            ArrayList<String> imagePath = new ArrayList<String>();
            imagePath.add(cursor.getString(index));
            cursor.moveToNext();
            System.out.println("cursor.getCount()=" + cursor.getCount());
            cursor.close();
            System.out.println("imagePath.size()=" + imagePath.size());

            Intent sp = new Intent(PictureBrowserActivity.this,
                    SelectedPictureActivity.class);
            sp.putStringArrayListExtra(SelectedPictureActivity.PIC_PATH,
                    imagePath);
            startActivity(sp);
        }
    };

    /**
     * 点击GridView中每一项的CheckBox的监听
     */
    private ImageAdapter.OnCheckBoxClickListener ocbcl = new ImageAdapter.OnCheckBoxClickListener() {

        public void onCheckBoxClick(View view, int position, long id) {
            // TODO Auto-generated method stub
            CheckBox selectBtn = (CheckBox) view.findViewById(R.id.pb_checkBox);
            boolean checked = !selectBtn.isChecked();
            // Log.i("Eaa", "item click id="+ id+" position="+position
            // +"|"+checked);
            if (checked) {
                if (itemNo < 9) {
                    Uri uri = picFolders.get(selectFold).uriArray.get(position);
                    selectBtn.setChecked(checked);
                    // adapter中保存已经点击过的图片的选中情况
                    adapter.putSelectMap(id, checked);
                    selectedTree.put(id, uri);
                    // Log.i("Eaa", "put id"+ id);
                } else {
                    Toast.makeText(PictureBrowserActivity.this,
                            getResources().getString(R.string.nomorethan9),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                selectBtn.setChecked(false);
                // adapter中保存已经点击过的图片的选中情况
                adapter.putSelectMap(id, false);
                selectedTree.remove(id);
                // Log.i("Eaa", "remove id"+ id);
            }
            itemNo = selectedTree.size();
            if (hasViedo) {
                itemNo++;
            }
            if (titleRightButton.getVisibility() == View.GONE && itemNo > 0) {
                titleRightButton.setBackgroundColor(0x8866cc00);
                titleRightButton.startAnimation(inAlphaAni);
                titleRightButton.setVisibility(View.VISIBLE);
            } else if (titleRightButton.getVisibility() == View.VISIBLE
                    && itemNo == 0) {
                titleRightButton.startAnimation(outAlphaAni);
                titleRightButton.setVisibility(View.GONE);
            }

            CharSequence text = itemNo + "/9";
            tvPreview.setText(text);
        }
    };

    /**
     * @author Eaa 一个图片文件夹
     */
    class PicFolder {
        ArrayList<Uri> uriArray = new ArrayList<Uri>();// 存放图片URI
        ArrayList<Long> origIdArray = new ArrayList<Long>();// 存放图片ID
        String dirName;
        long dirId;
        long coverId;
        int picNum;

        public PicFolder(String dirName, long id, long cover, int picNum) {
            this.dirName = dirName;
            this.dirId = id;
            this.coverId = cover;
            this.picNum = picNum;
        }
    }
}
