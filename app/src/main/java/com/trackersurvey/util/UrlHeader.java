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
    public static final String GET_MSG_CODE_URL = "user/sendSMSCode"; //获取短信验证码（已调试）
    public static final String REGISTER_URL = "user/register"; // 注册（已调试）
    public static final String LOGIN_URL_NEW = "user/login"; // 登录（已调试）
    public static final String LOGOUT_URL = "user/logout"; // 退出登录
    // 轨迹和位置上传
    public static final String UPLOAD_LOCATION_URL = "location/uploadLocation";// 上传位置（已调试）
    public static final String UPLOAD_START_TRACE_URL = "trace/startTrace";// 开始轨迹（已调试）
    public static final String UPLOAD_TRACE_UPDATE_URL = "trace/endTrace";// 结束轨迹（已调试）
    // 轨迹和位置展示
    public static final String DOWNLOAD_TRACE_LIST = "trace/getTraceList";// 下载轨迹列表（分页获取）（已调试）
    public static final String DELETE_TRACE = "trace/deleteTrace";// 删除轨迹（已调试）
    public static final String DOWNLOAD_TRACE_DETAIL = "location/getTraceLocation";// 下载轨迹详情（已调试）
    public static final String UPLOAD_TRACE = "trace/uploadTrace"; // 上传本地轨迹
    // 兴趣点
    public static final String DOWNLOAD_POI_CHOICES = "poi/getPoiChoices"; // 下载兴趣点选项列表（已调试）
    public static final String UPLOAD_POI_URL = "poi/uploadPoi"; // 上传兴趣点（已调试）
    public static final String UPLOAD_FILE_URL = "poi/uploadFile"; // 上传文件（已调试）

    public static final String DOWNLOAD_ALL_POI_URL = "poi/getPoiList";// 下载所有兴趣点
    public static final String DOWNLOAD_POI_URL = "poi/getPoiByTraceID"; // 下载轨迹上的兴趣点
    public static final String DELETE_POI_URL = "/poi/deletePoi"; // 删除一条兴趣点

    public static final String QUESTIONARY_URL = "questionnaire/wx_getQuestionnaireList";// 调查问卷

    public static final String UPLOAD_USER_INFO_URL = "user/updateUserInfo";// 更新个人信息

    public static final String DOWNLOAD_GROUP_LIST = "group/getGroupList"; // 获取群组列表
    public static final String DOWNLOAD_USER_GROUP_LIST = "group/getUserGroupList"; // 获取用户已加入的群组列表
    public static final String JOIN_GROUP = "/group/joinGroup";    // 用户申请加群
    public static final String EXIT_GROUP_URL = "/group/exitGroup";

    public static final String DOWNLOAD_FILE = "/poi/downloadFile";
    public static final String DOWNLOAD_THUMB_FILE = "/poi/downloadFileByPoiID";

    // 老版本的接口还有这些：
    // 删除兴趣点
    // 用户是否上下线
    // 用户加入或退出群组
    // 搜索群
    // 获取群信息
    // 上传兴趣点文件
    // 下载兴趣点原文件
    // 下载兴趣点缩略图
    // 版本更新
    // 请求4个时间？？？
}
