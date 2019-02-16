package com.trackersurvey.bean;

import com.trackersurvey.model.TraceData;

/**
 * Created by zh931 on 2018/5/16.
 */

public class TraceListItemData {
    private TraceData trace;
    private boolean isLocal;
    private boolean isCloud;

    public TraceListItemData() {
    }

    public TraceListItemData(TraceData trace, boolean isLocal, boolean isCloud) {
        this.trace = trace;
        this.isLocal = isLocal;
        this.isCloud = isCloud;
    }

    public TraceData getTrace() {
        return trace;
    }

    public void setTrace(TraceData trace) {
        this.trace = trace;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public boolean isCloud() {
        return isCloud;
    }

    public void setCloud(boolean cloud) {
        isCloud = cloud;
    }
}
