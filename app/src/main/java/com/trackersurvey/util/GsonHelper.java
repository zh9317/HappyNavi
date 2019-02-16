package com.trackersurvey.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zh931 on 2018/5/12.
 * gson解析json
 */

public class GsonHelper {
    public static <T> T parseJson(String jsonString, Class<T> clazz) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, clazz);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("解析json失败");
        }
        return t;

    }
    public static <T> List<T> parseJsonToList(String json, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            //list = gson.fromJson(jsonString, new TypeToken<ArrayList<T>>(){}.getType());//泛型在编译期类型被擦除
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for(final JsonElement elem : array){
                list.add(new Gson().fromJson(elem, clazz));
            }
        } catch (Exception e) {
            System.out.println("解析json list失败");
        }
        return list;
    }
    /*
     * public static List<TrailRoughData> parseTraceRoughList(String jsonString){
         List<TrailRoughData> list= new ArrayList<TrailRoughData>();
         try {
                Gson gson = new Gson();
                list = gson.fromJson(jsonString, new TypeToken<ArrayList<TrailRoughData>>(){}.getType());//new TypeToken<ArrayList<GpsData>>(){}.getType()
            } catch (Exception e) {
                System.out.println("解析json list失败");
            }
            return list;
     }
     public static List<GpsData> parseTraceDetailList(String jsonString){
         List<GpsData> list= new ArrayList<GpsData>();
         try {
                Gson gson = new Gson();
                list = gson.fromJson(jsonString, new TypeToken<ArrayList<GpsData>>(){}.getType());//new TypeToken<ArrayList<GpsData>>(){}.getType()
            } catch (Exception e) {
                System.out.println("解析json list失败");
            }
            return list;
     }*/
    public static  String toJson(Object obj){
        String jsonstr="";
        try {
            Gson gson = new Gson();
            jsonstr=gson.toJson(obj);
        }
        catch (Exception e) {
            System.out.println("转为json list失败");
        }
        return jsonstr;
    }
}
