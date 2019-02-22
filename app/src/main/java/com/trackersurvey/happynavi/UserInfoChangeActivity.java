package com.trackersurvey.happynavi;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.http.UploadUserInfoRequest;
import com.trackersurvey.model.UserInfoData;
import com.trackersurvey.model.UserInfoModel;
import com.trackersurvey.util.ActivityCollector;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.BitmapToFile;
import com.trackersurvey.util.CompressImageUtil;
import com.trackersurvey.util.GsonHelper;
import com.trackersurvey.util.RoundImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

public class UserInfoChangeActivity extends AppCompatActivity implements View.OnClickListener{

    private RoundImageView headImgIv;
    private EditText nicknameEt;
    private EditText realNameEt;
    private TextView birthDateTv;
    private RelativeLayout birthDateLayout;
    private TextView sexTv;
    private RelativeLayout sexLayout;
    // 下面是RegisterItems
    private EditText nativePlaceEt;
    private EditText addressEt;
    private TextView educationTv;
    private RelativeLayout educationLayout;
    private TextView occupationTv;
    private RelativeLayout occupationLayout;
    private TextView incomeTv;
    private RelativeLayout incomeLayout;
    private TextView marriageTv;
    private RelativeLayout marriageLayout;
    private TextView childNumTv;
    private RelativeLayout childNumLayout;
    private Button save;

    private SharedPreferences sp;
    private String nicknameStr;
    private String realNameStr;
    private String birthDateStr;
    private String sexStr;
    private String nativePlaceStr;
    private String addressStr;
    private String occupationStr;
    private String educationStr;
    private String incomeStr;
    private String marriageStr;
    private String childNumStr;
    private String[] birthDate;
    private int sexChoice;
    private int occupationChoice;
    private int educationChoice;
    private int incomeChoice;
    private int marriageChoice;
    private int childNumChoice;

    final  static  int  CAMERA =1;
    final  static  int  ICON =2;
    final  static  int  CAMERAPRESS =3;
    final  static  int  ICONPRESS=4;

    private File imageFile; //图片文件
    private Uri imageUri; //图片路径
    String imagePath;
    Bitmap bitmapdown;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_change);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        AppManager.getAppManager().addActivity(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        headImgIv = findViewById(R.id.user_head_img);
        nicknameEt = findViewById(R.id.nickname_et);
        realNameEt = findViewById(R.id.real_name_et);
        birthDateTv = findViewById(R.id.user_info_birth_date);
        sexTv = findViewById(R.id.user_info_sex);

        Glide.with(this).load("http://211.87.227.204:8089"
                + sp.getString("headurl", "") + "?token="
                + sp.getString("token", "")).into(headImgIv);

        birthDateLayout = findViewById(R.id.birth_date_layout);
        sexLayout = findViewById(R.id.sex_layout);
        educationLayout = findViewById(R.id.education_layout);
        occupationLayout = findViewById(R.id.occupation_layout);
        incomeLayout = findViewById(R.id.income_layout);
        marriageLayout = findViewById(R.id.marriage_layout);
        childNumLayout = findViewById(R.id.child_num_layout);

        birthDateLayout.setOnClickListener(this);
        sexLayout.setOnClickListener(this);
        educationLayout.setOnClickListener(this);
        occupationLayout.setOnClickListener(this);
        incomeLayout.setOnClickListener(this);
        marriageLayout.setOnClickListener(this);
        childNumLayout.setOnClickListener(this);

        nativePlaceEt = findViewById(R.id.native_place_et);
        addressEt = findViewById(R.id.address_et);
        occupationTv = findViewById(R.id.user_info_occupation);
        educationTv = findViewById(R.id.user_info_education);
        incomeTv = findViewById(R.id.user_info_income);
        marriageTv = findViewById(R.id.user_info_marriage);
        childNumTv = findViewById(R.id.user_info_child_num);
        save = findViewById(R.id.save_user_info_btn);

        headImgIv.setOnClickListener(this);
        birthDateTv.setOnClickListener(this);
        sexTv.setOnClickListener(this);
        occupationTv.setOnClickListener(this);
        educationTv.setOnClickListener(this);
        incomeTv.setOnClickListener(this);
        marriageTv.setOnClickListener(this);
        childNumTv.setOnClickListener(this);
        save.setOnClickListener(this);

//        Intent intent = getIntent();
//        nicknameStr = intent.getStringExtra("nickname");
//        realNameStr = intent.getStringExtra("realName");
//        birthDateStr = intent.getStringExtra("birthDate");
//        sexStr = intent.getStringExtra("sex");
//        occupationStr = intent.getStringExtra("occupation");
//        educationStr = intent.getStringExtra("education");
        nicknameEt.setText(sp.getString("nickname", ""));
        realNameEt.setText(sp.getString("realName", ""));
        birthDateTv.setText(sp.getString("birthDate", ""));
        if (sp.getInt("sex", 0) == 0) {
            sexTv.setText("保密");
        } else if (sp.getInt("sex", 0) == 1) {
            sexTv.setText("男");
        } else if (sp.getInt("sex", 0) == 2) {
            sexTv.setText("女");
        }
        nativePlaceEt.setText(sp.getString("nativePlace", ""));
        addressEt.setText(sp.getString("address", ""));
        occupationTv.setText(sp.getString("occupation", ""));
        educationTv.setText(sp.getString("education", ""));
        incomeTv.setText(sp.getString("income", ""));
        marriageTv.setText(sp.getString("marriage", ""));
        childNumTv.setText(sp.getString("childCount", ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head_img:
                selectPicDialog();
                break;
                // 选择出生日期
            case R.id.user_info_birth_date:
                selectBirthDate();
                break;
            case R.id.birth_date_layout:
                selectBirthDate();
                break;
                // 选择性别
            case R.id.user_info_sex:
                selectSex();
                break;
            case R.id.sex_layout:
                selectSex();
                break;
                // 选择职业
            case R.id.user_info_occupation:
                selectOccupation();
                break;
            case R.id.occupation_layout:
                selectOccupation();
                break;
                // 选择教育程度
            case R.id.user_info_education:
                selectEducation();
                break;
            case R.id.education_layout:
                selectEducation();
                break;
                // 选择年收入
            case R.id.user_info_income:
                selectIncome();
                break;
            case R.id.income_layout:
                selectIncome();
                break;
                // 选择婚姻状况
            case R.id.user_info_marriage:
                selectMarriage();
                break;
            case R.id.marriage_layout:
                selectMarriage();
                break;
                // 选择子女数
            case R.id.user_info_child_num:
                selectChildNum();
                break;
            case R.id.child_num_layout:
                selectChildNum();
                break;
                // 保存
            case R.id.save_user_info_btn:
                UserInfoData userInfoData = new UserInfoData(
                        sp.getInt("userID", 0),
                        nicknameEt.getText().toString(),
                        realNameEt.getText().toString(),
                        birthDateTv.getText().toString(),
                        sexChoice,
                        nativePlaceEt.getText().toString(),
                        addressEt.getText().toString(),
                        educationTv.getText().toString(),
                        incomeTv.getText().toString(),
                        occupationTv.getText().toString(),
                        marriageTv.getText().toString(),
                        childNumTv.getText().toString());
                String userInfo = GsonHelper.toJson(userInfoData);
                Log.i("UserInfoChange", "userInfo : " + userInfo);
                Log.i("UserInfoChange", "_timestamp:"+String.valueOf(System.currentTimeMillis())
                        +"|Token:"+sp.getString("Token", "")
                        +"|nickname:"+nicknameEt.getText().toString()
                        +"|realName:"+realNameEt.getText().toString()
                        +"|birthDate:"+birthDateTv.getText().toString()
                        +"|sex:"+String.valueOf(sexChoice)
                        +"|hobbyIDs:"+""
                        +"|nativePlace:"+nativePlaceEt.getText().toString()
                        +"|address:"+addressEt.getText().toString()
                        +"|education:"+educationTv.getText().toString()
                        +"|income:"+incomeTv.getText().toString()
                        +"|occupation:"+occupationTv.getText().toString()
                        +"|marriage:"+marriageTv.getText().toString()
                        +"|childNum:"+childNumTv.getText().toString());
                UploadUserInfoRequest uploadUserInfoRequest = new UploadUserInfoRequest(
                        sp.getString("token", ""),
                        file == null ? "" : file.getPath(), userInfo);
                uploadUserInfoRequest.requestHttpData(new ResponseData() {
                    @Override
                    public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                        if (isSuccess) {
                            if (code.equals("0")) {
                                UserInfoModel model = (UserInfoModel) responseObject;
                                SharedPreferences.Editor loginEditor = sp.edit();
                                loginEditor.putString("birthDate", model.getBirthDate());
                                loginEditor.putString("headurl", model.getHeadurl());
                                loginEditor.putString("nickname", model.getNickname());
                                loginEditor.putString("realName", model.getRealName());
                                loginEditor.putString("city", model.getRegisteritem2());
                                loginEditor.putString("address", model.getRegisteritem3());
                                loginEditor.putString("education", model.getRegisteritem4());
                                loginEditor.putString("income", model.getRegisteritem5());
                                loginEditor.putString("occupation", model.getRegisteritem6());
                                loginEditor.putString("marriage", model.getRegisteritem7());
                                loginEditor.putString("childCount", model.getRegisteritem8());
                                loginEditor.putInt("sex", model.getSex());
                                loginEditor.apply();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(UserInfoChangeActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            if (code.equals("100") || code.equals("101")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(UserInfoChangeActivity.this, "登录信息过期，请重新登录！", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("token", ""); // 清空token
                                        editor.apply();
                                        ActivityCollector.finishActivity("UserInfoChangeActivity");
                                        ActivityCollector.finishActivity("UserInfoActivity");
                                        ActivityCollector.finishActivity("MainActivity");
                                    }
                                });
                            }
                            if (code.equals("400") || code.equals("401")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(UserInfoChangeActivity.this, "头像图片上传出错！请重新选择头像图片上传！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });
                break;
        }
    }

    // 点击头像图标弹出对话框
    private void selectPicDialog(){
        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(this);
        singleChoiceDialog.setItems(new String[]{"拍摄照片", "从相册选择"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                if( Build.VERSION.SDK_INT >= 23){ // 判断是否为android6.0及以上
//                                    Toast.makeText(getContext(),"当前的版本号"+Build.VERSION.SDK_INT,Toast.LENGTH_LONG).show();
                                    //android 6.0权限问题
                                    if (ContextCompat.checkSelfPermission(UserInfoChangeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED||
                                            ContextCompat.checkSelfPermission(UserInfoChangeActivity.this,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                        ActivityCompat.requestPermissions(UserInfoChangeActivity.this,
                                                new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERAPRESS);
//                                        Toast.makeText(getContext(),"执行了权限请求",Toast.LENGTH_LONG).show();
                                    }else {
                                        startCamera();
                                    }
                                }else {
                                    startCamera();
                                }
                                dialog.dismiss();
                                break;
                            case 1:
                                if( Build.VERSION.SDK_INT >= 23){
                                    // Toast.makeText(getContext(),"当前的版本号"+Build.VERSION.SDK_INT,Toast.LENGTH_LONG).show();
                                    // android 6.0权限问题
                                    if (ContextCompat.checkSelfPermission(UserInfoChangeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED||
                                            ContextCompat.checkSelfPermission(getApplicationContext(),
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                        Toast.makeText(UserInfoChangeActivity.this,"执行了权限请求",Toast.LENGTH_LONG).show();
                                        ActivityCompat.requestPermissions(UserInfoChangeActivity.this,
                                                new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERAPRESS);
                                    }else {
                                        startIcon();
                                    }

                                }else {
                                    startIcon();
                                }
                                dialog.dismiss();
                                break;
                        }
                    }
                }).show();
    }

    // 开启相机
    private void startCamera(){
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        imageFile = new File(path, "head.png");
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将File对象转换为Uri并启动照相程序
        imageUri = Uri.fromFile(imageFile);
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE"); //照相
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //照相
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
        startActivityForResult(intent, CAMERA); //启动照相
    }

    // 打开图库
    private void startIcon(){
        Intent intent1 = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent1, ICON);
    }

    // 获取文件路径
    private static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private Bitmap getBitmapFromFile(File dst) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            //opts.inJustDecodeBounds = false;
            opts.inSampleSize = 2;

            try {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void selectBirthDate() {
//        birthDate = birthDateStr.split("-");
        final Calendar calendar = Calendar.getInstance();
//        calendar.set(Integer.parseInt(birthDate[0]), Integer.parseInt(birthDate[1])-1, Integer.parseInt(birthDate[2]));
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                birthDateTv.setText(android.text.format.DateFormat.format("yyyy-MM-dd", calendar));
                birthDateStr = String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void selectSex() {
        final String[] items = getResources().getStringArray(R.array.sex);
        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(this);
        singleChoiceDialog.setTitle(getResources().getString(R.string.select_sex_please));
        singleChoiceDialog.setSingleChoiceItems(items, sexChoice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sexChoice = which;
                sexStr = items[sexChoice];
                sexTv.setText(items[sexChoice]);
                dialog.dismiss();
            }
        });
        singleChoiceDialog.show();
    }

    private void selectOccupation() {
        final String[] items = getResources().getStringArray(R.array.occupation);
        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(this);
        singleChoiceDialog.setTitle(getResources().getString(R.string.select_occupation_please));
        singleChoiceDialog.setSingleChoiceItems(items, occupationChoice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                occupationChoice = which;
                occupationStr = items[occupationChoice];
                occupationTv.setText(items[occupationChoice]);
                dialog.dismiss();
            }
        });
        singleChoiceDialog.show();
    }

    private void selectEducation() {
        final String[] items = getResources().getStringArray(R.array.education);
        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(this);
        singleChoiceDialog.setTitle(getResources().getString(R.string.select_education));
        singleChoiceDialog.setSingleChoiceItems(items, educationChoice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                educationStr = items[educationChoice];
                educationChoice = which;
                educationTv.setText(items[educationChoice]);
                dialog.dismiss();
            }
        });
        singleChoiceDialog.show();
    }

    private void selectIncome() {
        final String[] items = getResources().getStringArray(R.array.income);
        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(this);
        singleChoiceDialog.setTitle(getResources().getString(R.string.select_income));
        singleChoiceDialog.setSingleChoiceItems(items, incomeChoice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                incomeChoice = which;
                incomeStr = items[incomeChoice];
                incomeTv.setText(items[incomeChoice]);
                dialog.dismiss();
            }
        });
        singleChoiceDialog.show();
    }

    private void selectMarriage() {
        final String[] items = getResources().getStringArray(R.array.marriage);
        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(this);
        singleChoiceDialog.setTitle(getResources().getString(R.string.select_marriage));
        singleChoiceDialog.setSingleChoiceItems(items, marriageChoice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                marriageChoice = which;
                marriageStr = items[marriageChoice];
                marriageTv.setText(items[marriageChoice]);
                dialog.dismiss();
            }
        });
        singleChoiceDialog.show();
    }

    private void selectChildNum() {
        final String[] items = getResources().getStringArray(R.array.children);
        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(this);
        singleChoiceDialog.setTitle(getResources().getString(R.string.select_child_num));
        singleChoiceDialog.setSingleChoiceItems(items, childNumChoice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                childNumChoice = which;
                childNumStr = items[childNumChoice];
                childNumTv.setText(items[childNumChoice]);
                dialog.dismiss();
            }
        });
        singleChoiceDialog.show();
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Mine","requestCode"+requestCode+"resultCode"+resultCode);
        switch (requestCode) {
            case CAMERA:
                // 返回相机拍摄的照片地址
                if (data == null) { // 如果指定了目标uri，data就没有数据，如果没有指定uri，则data就返回有数据，所以此处做空判断！
                    Bitmap bitmap1 = null;
                    try {
                        bitmap1 = BitmapFactory.decodeStream(UserInfoChangeActivity.this.getContentResolver().openInputStream(imageUri));
                        imagePath = getPath(UserInfoChangeActivity.this, imageUri);// 获取文件路径
                        // 开始压缩
                        Bitmap compressBitmap = CompressImageUtil.getimage(imagePath);
                        file = BitmapToFile.compressImage(compressBitmap);
                        try {
                            long size = new FileInputStream(file).available();
                            Log.i("MineFragment", size + "");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // 拍照返回，直接在ImageView上显示缩略图
                        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bitmap1, 200, 200); // 缩略图
                        bitmapdown = bitmap;
                        headImgIv.setImageBitmap(bitmapdown); // ImageView加载图片(缩略图)

//                        FileOutputStream out = new FileOutputStream(imagePath);
//                        if (bitmap1.compress(Bitmap.CompressFormat.PNG, 100, out)) {
//                            out.flush();
//                            out.close();
//                        }
                    } catch (FileNotFoundException e) {
                        imageFile = null;
                        e.printStackTrace();
                    }
                    Log.i("Mine","imagePath"+imagePath);
                }else {

                }
                break;
            case ICON:
                // 返回图库的图片地址
                if (data != null) {
                    DisplayMetrics metric = new DisplayMetrics();
                    UserInfoChangeActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metric);
                    String dst = getPath(this, data.getData());
                    imageFile = new File(dst);
                    imagePath = dst;
                    // 开始压缩
                    Bitmap compressBitmap = CompressImageUtil.getimage(imagePath);
                    file = BitmapToFile.compressImage(compressBitmap);
                    try {
                        long size = new FileInputStream(file).available();
                        Log.i("MineFragment", size + ""); // 单位Byte
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 图片选择完，直接在ImageView上显示缩略图
                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(getBitmapFromFile(imageFile), 200, 200);
                    bitmapdown = bitmap;
                    headImgIv.setImageBitmap(bitmapdown);
                    Log.i("Mine","imagePath"+imagePath);
                }
                break;
        }
    }
}
