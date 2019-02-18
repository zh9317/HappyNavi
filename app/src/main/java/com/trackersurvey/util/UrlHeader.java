package com.trackersurvey.util;

/**
 * Created by zh931 on 2018/5/9.
 */

public class UrlHeader {
    public static final String BASE_URL_OLD = "http://219.218.118.176:8090/Communication/";
    public static final String LOGIN_URL_OLD = "userLogin.aspx";
//    public static final String BASE_URL_NEW = "http://211.87.235.26:8080/footPrint/";
//    public static final String BASE_URL_NEW = "http://121.250.210.80:8080/footPrint/";

    public static final String BASE_URL_NEW = "http://211.87.227.204:8089/";
    public static final String GET_MSG_CODE_URL = "user/sendSMSCode"; //获取短信验证码
    public static final String REGISTER_URL = "user/register"; // 注册
    public static final String LOGIN_URL_NEW = "user/login"; // 登录
    public static final String UPLOAD_LOCATION_URL = "location/uploadLocation";// 上传位置
    public static final String UPLOAD_TRACE_URL = "trace/startTrace";// 开始轨迹
    public static final String UPLOAD_TRACE_UPDATE_URL = "trace/endTrace";// 结束轨迹

    public static final String DOWNLOAD_TRACE_LIST = "trace/getTraceList";// 下载轨迹列表
    public static final String DELETE_TRACE = "trace/deleteTrace";// 删除轨迹
    public static final String DOWNLOAD_TRACE_DETAIL = "location/getTraceLocation";// 下载轨迹详情
    public static final String DOWNLOAD_POI_CHOICES = "poi/getChoices";// 下载兴趣点选项列表
    public static final String UPLOAD_POI_URL = "poi/upload";// 上传兴趣点
    public static final String QUESTIONARY_URL = "questionnaire/wx_getQuestionnaireList";// 调查问卷
    public static final String DOWNLOAD_POI_LIST_URL = "poi/getPoiListPaging";// 下载兴趣点
    public static final String UPLOAD_USER_INFO_URL = "updUserInfo";// 更新个人信息
    public static final String DOWNLOAD_USER_INFO_URL = "getPersonalInfo";
}
