package com.trackersurvey.httpconnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.trackersurvey.bean.CommentMediaFilesData;
import com.trackersurvey.util.Common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zh931 on 2018/5/13.
 */

public class PostCommentFile extends Thread {
    private String userId;
    private String createTime;
    private String uploadHost;
    private int fileId;
    private int fileType;
    private String fileNmae;
    private String deviceId;
    private Context context;
    private Handler handler;
    private Message msg;
    private HttpUtils httpSend;

    public PostCommentFile(Context context, String userId, String createTime,
                           Handler handler, String uploadHost, int fileID, int fileType,
                           String fileName,String deviceId) {
        this.context = context;
        this.createTime = createTime;
        this.userId = userId;
        this.handler = handler;
        this.uploadHost = uploadHost;
        this.fileId = fileID;
        this.fileType = fileType;
        this.fileNmae = fileName;
        this.deviceId = deviceId;
        msg = Message.obtain();
        httpSend = new HttpUtils();
    }

    public void run() {
        uploadFile(this.uploadHost, this.fileId, this.fileType, this.fileNmae);
    }

    // 上传一个文件
    private void uploadFile(final String uploadHost, int fileID, int fileType,
                            String fileName) {
        Log.e("upfile", "fileType : " + fileType);

        switch (fileType) {
            case CommentMediaFilesData.TYPE_PIC: {
                uploadPicture(fileID, fileName, uploadHost);
                break;
            }
            case CommentMediaFilesData.TYPE_VIDEO: {
                uploadVideo(fileID, fileName, uploadHost);
                break;
            }
        }

    }

    private void uploadPicture(int fileNo, String path, String uploadHost) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userID", userId);
        params.addBodyParameter("createTime", createTime);
        params.addBodyParameter("fileNo", "" + fileNo);
        params.addBodyParameter("deviceId", deviceId);
        String fileStr = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只读图片属性，不读像素数据
        BitmapFactory.decodeFile(path, options);
        // 如果需要缩放
        if ((options.inSampleSize = calculateInSampleSize(options,
                Common.decodeImgWidth, Common.decodeImgHeight)) > 1) {
            options.inJustDecodeBounds = false; // 读压缩图片
            Bitmap bmp = BitmapFactory.decodeFile(path, options);
            String tempFile = "uploadTemp.jpg";
            Log.i("upfile", "缩放图片后上传");
            // 生成压缩后的图片，写入本地，用完后删除
            FileOutputStream bos;
            try {
                bos = context.openFileOutput(tempFile, Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileStr = uploadStr(tempFile, 1);
            context.deleteFile(tempFile); // 删除缩放图
        } else { // 如果不需要缩放，直接读原图
            fileStr = uploadStr(path, 2);
        }
        params.addBodyParameter("fileType", "pic");
        params.addBodyParameter("fileStr", fileStr);
        httpSend.send(HttpRequest.HttpMethod.POST, uploadHost, params,
                new fileRequestCallBack());
    }

    /**
     * 上传视频
     *
     * @param path
     */
    private void uploadVideo(int fileNo, String path, String uploadHost) {
        RequestParams rp = new RequestParams();
        rp.addBodyParameter("userId", userId);
        rp.addBodyParameter("createTime", createTime);
        rp.addBodyParameter("fileType", "video");
        rp.addBodyParameter("fileNo", "" + fileNo);
        rp.addBodyParameter("deviceId", deviceId);
        File fileVideo = new File(path);
        rp.addBodyParameter("fileName", fileVideo.getName());
        try {
            InputStream isv = new FileInputStream(fileVideo);
            rp.addBodyParameter("file", isv, fileVideo.length());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("upfile", "上传视频");
        httpSend.send(HttpRequest.HttpMethod.POST, uploadHost, rp,
                new fileRequestCallBack());

    }

    // 计算图片上传图片需要缩放的比例
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            } else {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            }
        }

        if (inSampleSize < 1) {
            inSampleSize = 1;
        }
        return inSampleSize;
    }

    /**
     * 将文件转成字符串
     *
     * @param path
     *            文件地址
     * @return
     */
    public String uploadStr(String path, int mode) {
        FileInputStream fis = null;
        try {
            switch (mode) {
                case 1:
                    fis = context.openFileInput(path);
                    break;
                case 2:
                    fis = new FileInputStream(path);
                    break;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[65535];
            int count = 0;
            while ((count = fis.read(buffer)) >= 0) {
                Log.i("upfile", "count : " + count);
                baos.write(buffer, 0, count);
            }

            String uploadBuffer = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT)); // 进行Base64编码
            // 关闭输出流
            fis.close();
            baos.flush();
            baos.close();
            return uploadBuffer;

        } catch (Exception e) {
            e.printStackTrace();
        }
        // return soapObject;
        return null;

    }

    // 回调，监听上传是否成功
    class fileRequestCallBack extends RequestCallBack<String> {
        @Override
        public void onFailure(HttpException arg0, String result) {
            Log.i("upfile", arg0 + "|||" + result);
            msg.what = 2;
            msg.obj = result;
            handler.handleMessage(msg);
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            // TODO Auto-generated method stub
            super.onLoading(total, current, isUploading);
            //Log.i("upfile", "onLoading : "+total + "|||" + current+","+(int)current/total+"%");

        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            String result = responseInfo.result;

            if ("ok".equals(result)) {
                Log.i("upfile", "picOk");
                msg.what = 0;
                msg.obj = result;
                handler.handleMessage(msg);
            } else {
                Log.e("upfile", "result = "+result);
                msg.what = 1;
                msg.obj = result;
                handler.handleMessage(msg);
            }
        }
    }
}
