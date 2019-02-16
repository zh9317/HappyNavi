package com.trackersurvey.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by zh931 on 2018/5/14.
 * Activity堆栈管理
 */

public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）

     public void finishActivity() {
     Activity activity = activityStack.lastElement();
     finishActivity(activity);
     }
     */
    /**
     * 结束指定的Activity

     public void finishActivity(Activity activity) {
     if (activity != null) {

     activity.finish();
     activityStack.remove(activity);
     activity = null;
     }
     }
     */
    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        /**这样删除会报异常：ConcurrentModificationException
         * for (Activity activity : activityStack) {
         if (activity.getClass().equals(cls)) {
         finishActivity(activity);
         }
         }*/
        Iterator<Activity> iter= activityStack.iterator();
        while(iter.hasNext()){
            Activity activity=(Activity)iter.next();
            if(activity.getClass().equals(cls)){
                iter.remove();
                activity.finish();
                break;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 结束除了当前页其他所有Activity
     */
    public void finishOtherAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size-1; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        // activityStack.clear();
    }
    public void finishOtherActivity() {
//		for (int i = 1, size = activityStack.size(); i < size; i++) {
//			if (null != activityStack.get(i)) {
        activityStack.get(activityStack.size()-1).finish();
        activityStack.get(activityStack.size()-2).finish();
//			}
//		}
        // activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());

        } catch (Exception e) {
        }
    }
}
