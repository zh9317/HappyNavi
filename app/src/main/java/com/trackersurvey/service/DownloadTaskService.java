package com.trackersurvey.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.trackersurvey.bean.FileInfoData;
import com.trackersurvey.bean.ThreadInfoData;
import com.trackersurvey.db.ThreadDAO;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
/**
 * 下载任务类
 */
public class DownloadTaskService extends Service {
    private Context context = null;
    private FileInfoData fileInfo = null;
    private ThreadDAO dao = null;
    private int finished = 0;
    public boolean isPause = false;
    private int threadCount = 1;  // 线程数量
    private List<DownloadThread> downloadThreadList = null; // 线程集合
    public DownloadTaskService() {
    }

    public DownloadTaskService(Context context, FileInfoData fileInfo, int threadCount) {
        this.context = context;
        this.fileInfo = fileInfo;
        this.threadCount = threadCount;

    }

    public void downLoad()
    {
        //mDao.deleteAllThread();
        // 读取数据库的线程信息
        List<ThreadInfoData> threads = dao.getThreads(fileInfo.getUrl());
        ThreadInfoData threadInfo = null;

        if (0 == threads.size())
        {
            // 计算每个线程下载长度
            int len = fileInfo.getLength() / threadCount;
            for (int i = 0; i < threadCount; i++)
            {
                // 初始化线程信息对象
                threadInfo = new ThreadInfoData(i, fileInfo.getUrl(),
                        len * i, (i + 1) * len - 1, 0);

                if (threadCount - 1 == i)  // 处理最后一个线程下载长度不能整除的问题
                {
                    threadInfo.setEnd(fileInfo.getLength());
                }

                // 添加到线程集合中
                threads.add(threadInfo);
                dao.insertThread(threadInfo);
            }
        }

        downloadThreadList = new ArrayList<DownloadThread>();
        // 启动多个线程进行下载
        for (ThreadInfoData info : threads)
        {
            DownloadThread thread = new DownloadThread(info);
            thread.start();
            // 添加到线程集合中
            downloadThreadList.add(thread);
        }
    }

    /**
     * 下载线程
     * @author Yann
     * @date 2015-8-8 上午11:18:55
     */
    private class DownloadThread extends Thread
    {
        private ThreadInfoData threadInfo = null;
        public boolean isFinished = false;  // 线程是否执行完毕

        /**
         *@param info
         */
        public DownloadThread(ThreadInfoData info)
        {
            this.threadInfo = info;
        }

        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public void run()
        {
            HttpURLConnection connection = null;
            RandomAccessFile raf = null;
            InputStream inputStream = null;

            try
            {
                URL url = new URL(threadInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                connection.setAllowUserInteraction(true);
                // 设置下载位置
                int start = threadInfo.getStart() + threadInfo.getFinished();//开始位置+已下载的文件长度
                connection.setRequestProperty("Range",
                        "bytes=" + start + "-" + threadInfo.getEnd());
                Log.i("phonelog", threadInfo.getId() +",start:"+start+",end:"+threadInfo.getEnd());
                //connection.setRequestProperty("Connection", "Keep-Alive");
                File file = new File(DownloadService.DOWNLOAD_PATH,
                        fileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                Intent intent = new Intent();
                intent.setAction(DownloadService.ACTION_UPDATE);
                finished += threadInfo.getFinished();
                Log.i("phonelog", threadInfo.getId() + "finished = " + threadInfo.getFinished());
                // 开始下载
                if(start<threadInfo.getEnd()){
                    if (connection.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT )//HttpStatus.SC_PARTIAL_CONTENT
                    {
                        // 读取数据
                        inputStream = connection.getInputStream();
                        byte buf[] = new byte[1024 << 2];
                        int len = -1;
                        long time = System.currentTimeMillis();
                        while ((len = inputStream.read(buf)) != -1)
                        {
                            // 写入文件
                            raf.write(buf, 0, len);
                            // 累加整个文件完成进度
                            finished += len;
                            // 累加每个线程完成的进度
                            threadInfo.setFinished(threadInfo.getFinished() + len);
                            //						if((mThreadInfo.getEnd()-mThreadInfo.getStart())<=mThreadInfo.getFinished()){
                            //							Log.i("phonelog", mThreadInfo.getId() + "下完了，finished = " + mThreadInfo.getFinished()
                            //							+"---total:"+mFinised);
                            //							break;
                            //						}
                            if (System.currentTimeMillis() - time > 1000)
                            {
                                time = System.currentTimeMillis();
                                int f = finished * 100 / fileInfo.getLength();
                                if (f > fileInfo.getFinished())
                                {
                                    intent.putExtra("finished", f);
                                    intent.putExtra("id", fileInfo.getId());
                                    context.sendBroadcast(intent);

                                }
                                Log.i("phonelog", threadInfo.getId() + "下载中，finished = " + threadInfo.getFinished()
                                        +"---total:"+finished);

                            }

                            // 在下载暂停时，保存下载进度
                            if (isPause)
                            {
                                dao.updateThread(threadInfo.getUrl(),
                                        threadInfo.getId(),
                                        threadInfo.getFinished());

                                Log.i("phonelog", threadInfo.getId() + "暂停，finished = " + threadInfo.getFinished());

                                return;
                            }
                        }

                        // 标识线程执行完毕
                        isFinished = true;
                        checkAllThreadFinished();
                    }
                    else{
                        Log.i("phonelog",threadInfo.getId() +"下载异常,返回码:"+connection.getResponseCode());
                    }
                }
                else{
                    // 标识线程执行完毕
                    isFinished = true;
                    checkAllThreadFinished();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Intent intent = new Intent(DownloadService.ACTION_ERROR);
                intent.putExtra("fileInfo", fileInfo);
                context.sendBroadcast(intent);
                Log.i("phonelog","下载异常e----"+e.getMessage());
            }
            finally
            {
                try
                {
                    dao.updateThread(threadInfo.getUrl(),
                            threadInfo.getId(),
                            threadInfo.getFinished());

                    Log.i("phonelog", threadInfo.getId() + "保存进度，finished = " + threadInfo.getFinished());

                    if (connection != null)
                    {
                        connection.disconnect();
                    }
                    if (raf != null)
                    {
                        raf.close();
                    }
                    if (inputStream != null)
                    {
                        inputStream.close();
                    }
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                    Log.i("phonelog","下载异常e2----"+e2.getMessage());
                }
            }
        }
    }

    /**
     * 判断所有的线程是否执行完毕
     * @return void
     * @author Yann
     * @date 2015-8-9 下午1:19:41
     */
    private synchronized void checkAllThreadFinished()
    {
        boolean allFinished = true;

        // 遍历线程集合，判断线程是否都执行完毕
        for (DownloadThread thread : downloadThreadList)
        {
            if (!thread.isFinished)
            {
                allFinished = false;
                break;
            }
        }

        if (allFinished)
        {
            // 删除下载记录
            dao.deleteThread(fileInfo.getUrl());
            // 发送广播知道UI下载任务结束
            Intent intent = new Intent(DownloadService.ACTION_FINISHED);
            intent.putExtra("fileInfo", fileInfo);
            context.sendBroadcast(intent);
            Log.i("phonelog","下载完成");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
