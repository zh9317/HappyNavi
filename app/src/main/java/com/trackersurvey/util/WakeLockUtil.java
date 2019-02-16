package com.trackersurvey.util;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by zh931 on 2018/5/12.
 * 这是干啥的？
 */

public class WakeLockUtil {
    private static final String TAG = WakeLockUtil.class.getSimpleName();
    private Context context;
    private PowerManager.WakeLock wakeLock;

    public WakeLockUtil(Context context) {
        super();
        this.context = context;
    }
    /**
     * Acquires the wake lock.
     */
    public void acquireWakeLock() {
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager == null) {
                return;
            }
            if (wakeLock == null) {
                wakeLock = powerManager.newWakeLock(
                        PowerManager.PARTIAL_WAKE_LOCK, TAG);
                if (wakeLock == null) {
                    Log.e("AmapErr", "wakeLock is null.");
                    return;
                }
            }
            if (!wakeLock.isHeld()) {
                wakeLock.acquire();
                Log.i("LogDemo", "wakelock");
                if (!wakeLock.isHeld()) {
                    Log.e("AmapErr", "Unable to hold wakeLock.");
                }
            }
        } catch (RuntimeException e) {
            Log.e("AmapErr", "Caught unexpected exception", e);
        }
    }
    /**
     * Releases the wake lock.
     */
    public void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
