package com.trackersurvey.util;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zh931 on 2018/6/7.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
        Log.i("ActivityCollector", "Add " + activity.getLocalClassName());
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
        Log.i("ActivityCollector", "Remove " + activity.getLocalClassName());
    }

    public static void finishActivity(String activityName){
        for (Activity activity : activities){
            Log.i("ActivityCollector", "Finish " + activity.getLocalClassName());
            if (activityName.equals(activity.getLocalClassName())) {
                activity.finish();
            }
        }
    }

    public static void finishAll(){
        for (Activity activity : activities){
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
