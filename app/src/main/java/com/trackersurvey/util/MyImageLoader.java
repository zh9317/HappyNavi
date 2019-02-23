package com.trackersurvey.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.trackersurvey.adapter.GroupAdapter;
import com.trackersurvey.happynavi.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zh931 on 2018/5/21.
 *
 * @author ZY
 * 2016/5/30
 */

public class MyImageLoader {
    private ListView                 mListView;
    private ImageView                mImageView;
    private String                   mUrl;
    private LruCache<String, Bitmap> mCaches;
    private Set<BmpAsyncTask>        mTasks; //存放正在加载的异步任务

    public MyImageLoader() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取应用所能使用的最大内存
        int cacheSize = maxMemory / 4;
        mCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {//每次存入缓存时调用该方法
                return value.getByteCount();
            }
        };
        mTasks = new HashSet<BmpAsyncTask>();
    }

    public MyImageLoader(ListView listView) {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取应用所能使用的最大内存
        int cacheSize = maxMemory / 4;
        mCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {//每次存入缓存时调用该方法
                return value.getByteCount();
            }
        };
        mListView = listView;
        mTasks = new HashSet<BmpAsyncTask>();
    }

    //添加图片到内存缓存
    public void addBmpToCache(String url, Bitmap bmp) {
        if (getBmpFromCache(url) == null) {
            mCaches.put(url, bmp);
        }
    }

    //从缓存中获取图片
    public Bitmap getBmpFromCache(String url) {
        return mCaches.get(url);
    }

    //从SD卡中获取图片
    public Bitmap getBmpFromSDCard(String filename) {

        return BitmapFactory.decodeFile(Common.GROUPHEAD_PATH + filename);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mUrl)) {//防止listview由于convertview复用导致的图片错乱,原因：时序问题，可能后一个图片比上一个图片先加载完，于是先
                //发送消息，导致mimageview和bmp不对应。
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    public void showImageByThread(ImageView imageView, final String url) {
        mImageView = imageView;
        mUrl = url;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Bitmap bmp = getBmpFromUrl(url);
                //                try {
                //                    Thread.sleep(1000);//模拟网络延迟，助于发现图片错乱问题
                //                } catch (InterruptedException e) {
                //                    e.printStackTrace();
                //                }
                Message msg = Message.obtain();
                msg.obj = bmp;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    //通过异步任务加载图片
    public void showImageByAsyncTask(ImageView imageView, final String url) {

        Bitmap bitmap;
        bitmap = getBmpFromCache(url);//先从缓存中获取，如果有，直接用，没有，异步加载

        if (bitmap == null) {
            new BmpAsyncTask(imageView, url).execute(url);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    //在adapter中的getview使用，当缓存中有该图片时不使用默认图片，避免出现滑动时由于不执行loadImagesNonScroll导致的已加载的图片却不显示
    public void showImageIfExist(ImageView imageView, final String url) {

        Bitmap bitmap;
        bitmap = getBmpFromCache(url);//先从缓存中获取，如果有，直接用，没有，使用默认图片

        if (bitmap == null) {
            imageView.setImageResource(R.mipmap.ic_group);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    //滑动结束后调用此方法，加载可见的item
    public void loadImagesNonScroll(int start, int end) {
        for (int i = start; i < end; i++) {
            Bitmap bitmap;
            String url = GroupAdapter.URLArray[i];
            String name = GroupAdapter.IconName[i];
            bitmap = getBmpFromCache(url);//先从缓存中获取，如果有，直接用，没有，从sd卡加载

            if (bitmap == null) {
                bitmap = getBmpFromSDCard(name);
                if (bitmap == null) {//sd卡中也没有，从网络获取
                    BmpAsyncTask task = new BmpAsyncTask(url, name);
                    task.execute(url);
                    mTasks.add(task);
                } else {
                    Log.i("mylog", "get from sd card");
                    addBmpToCache(url, bitmap);
                    ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                    imageView.setImageBitmap(bitmap);
                }
            } else {
                Log.i("mylog", "get from cache");
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    //二级缓存 加载单个图片
    public void loadOneImage(ImageView imageView, final String url, String name) {
        Bitmap bitmap;


        bitmap = getBmpFromCache(url);//先从缓存中获取，如果有，直接用，没有，从sd卡加载

        if (bitmap == null) {
            bitmap = getBmpFromSDCard(name);
            if (bitmap == null) {//sd卡中也没有，从网络获取
                BmpAsyncTask task = new BmpAsyncTask(imageView, url, false);
                task.execute(url);
                mTasks.add(task);
            } else {
                Log.i("mylog", "get from sd card");
                addBmpToCache(url, bitmap);

                imageView.setImageBitmap(bitmap);
            }
        } else {
            Log.i("mylog", "get from cache");

            imageView.setImageBitmap(bitmap);
        }
    }

    //滑动时取消所有异步任务
    public void cancelAllTasks() {
        if (mTasks != null) {
            for (BmpAsyncTask task : mTasks) {
                task.cancel(false);
            }
        }
    }

    public class BmpAsyncTask extends AsyncTask<String, Void, Bitmap> {
        //不能使用外部mImageView和mUrl，
        private ImageView mImageView;
        private String    mUrl;
        private String    mIconName;
        private boolean   isScrollTask;
        private boolean   isFromList = true;

        public BmpAsyncTask(ImageView imageView, final String url) {
            mImageView = imageView;
            mUrl = url;
            isScrollTask = false;
        }

        public BmpAsyncTask(ImageView imageView, final String url, boolean isFromList) {
            mImageView = imageView;
            mUrl = url;
            isScrollTask = false;
            this.isFromList = isFromList;
        }

        public BmpAsyncTask(final String url, final String name) {
            mUrl = url;
            mIconName = name;
            isScrollTask = true;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = getBmpFromUrl(params[0]);
            if (bitmap != null) {
                addBmpToCache(params[0], bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isScrollTask) {
                ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);
                if (imageView != null && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    try {
                        saveFile(bitmap, mIconName);
                        Log.i("mylog", "save " + mIconName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("mylog", "save error");
                    }
                }
                mTasks.remove(this);
            } else {
                if (isFromList) {
                    if (mImageView.getTag().equals(mUrl) && bitmap != null) {
                        mImageView.setImageBitmap(bitmap);
                    }
                } else {
                    if (bitmap != null) {
                        mImageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }

    public Bitmap getBmpFromUrl(String url) {
        Bitmap bmp = null;
        InputStream is = null;
        try {
            is = new URL(url).openStream();
            bmp = BitmapFactory.decodeStream(is);
            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void saveFile(Bitmap bmp, String fileName) throws IOException {

        File myBmpFile = new File(Common.GROUPHEAD_PATH + fileName);

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myBmpFile));
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }
}
