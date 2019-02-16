package com.trackersurvey.http;

import java.io.IOException;

/**
 * Created by zh931 on 2018/5/9.
 */

public interface ResponseData {
    void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException;
}
