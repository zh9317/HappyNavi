package com.trackersurvey.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SupportLanguageUtil {

    // 简体中文
    public static final String SIMPLIFIED_CHINESE = "zh";
    // 英文
    public static final String ENGLISH = "en";
    // 繁体中文
    public static final String TRADITIONAL_CHINESE = "zh-hant";
    // 法语
    public static final String FRANCE = "fr";
    // 德语
    public static final String GERMAN = "de";
    // 意大利语
    public static final String ITALIAN = "it";
    //日语
    public static final String JAPAN = "ja";

    private static Map<String, Locale> mSupportLanguages = new HashMap<String, Locale>(7) {{
        put(LanguageConstants.ENGLISH, Locale.ENGLISH);
        put(LanguageConstants.SIMPLIFIED_CHINESE, Locale.SIMPLIFIED_CHINESE);
        put(LanguageConstants.TRADITIONAL_CHINESE, Locale.TRADITIONAL_CHINESE);
        put(LanguageConstants.FRANCE, Locale.FRANCE);
        put(LanguageConstants.GERMAN, Locale.GERMANY);
        put(LanguageConstants.ITALIAN, Locale.ITALY);
        put(LanguageConstants.JAPAN, Locale.JAPAN);
    }};

    /**
     * 是否支持此语言
     *
     * @param language language
     * @return true:支持 false:不支持
     */
    public static boolean isSupportLanguage(String language) {
        return mSupportLanguages.containsKey(language);
    }

    /**
     * 获取支持语言
     *
     * @param language language
     * @return 支持返回支持语言，不支持返回系统首选语言
     */
    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSupportLanguage(String language) {
        if (isSupportLanguage(language)) {
            return mSupportLanguages.get(language);
        }
        return getSystemPreferredLanguage();
    }

    /**
     * 获取系统首选语言
     *
     * @return Locale
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Locale getSystemPreferredLanguage() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }
}
