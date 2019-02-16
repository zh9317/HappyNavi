package com.trackersurvey.db;

import com.trackersurvey.bean.ThreadInfoData;

import java.util.List;

/**
 * Created by zh931 on 2018/5/13.
 * 数据访问接口
 * @author Yann
 */

public interface ThreadDAO {
    /**
     * 插入线程信息
     */
    public void insertThread(ThreadInfoData threadInfo);
    /**
     * 删除线程信息
     */
    public void deleteThread(String url);
    /**
     * 删除所有线程信息
     */
    public void deleteAllThread();
    /**
     * 更新线程下载进度
     */
    public void updateThread(String url, int thread_id, int finished);
    /**
     * 查询文件的线程信息
     */
    public List<ThreadInfoData> getThreads(String url);
    /**
     * 线程信息是否存在
     */
    public boolean isExists(String url, int thread_id);
}
