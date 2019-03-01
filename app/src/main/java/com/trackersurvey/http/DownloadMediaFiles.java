package com.trackersurvey.http;

import com.trackersurvey.util.ByteHttpUtil;
import com.trackersurvey.util.UrlHeader;

import java.io.InputStream;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class DownloadMediaFiles extends ByteHttpUtil {

    private String Token;
    private long FileID;

    public DownloadMediaFiles(String token, long FileID) {
        this.Token = token;
        this.FileID = FileID;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DOWNLOAD_FILE;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("fileID", String.valueOf(FileID))
                .add("token", Token)
                .build();
        return requestBody;
    }

    @Override
    public ByteHttpUtil handleData(InputStream inputStream) {
        ByteHttpUtil byteHttpUtil = new ByteHttpUtil();
        byteHttpUtil.responseObject = inputStream;
        return byteHttpUtil;
    }
}
