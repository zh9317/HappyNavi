package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/12.
 */

public class LanguageData {
    private String language;
    private String code;

    public LanguageData(String language, String code) {
        this.language = language;
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
