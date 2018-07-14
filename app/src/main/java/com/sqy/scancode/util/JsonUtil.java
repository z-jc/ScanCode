package com.sqy.scancode.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

    public static JSONObject jsonObject = null;
    public static JSONArray jsonArray = null;

    /**
     * 心跳包(不含功能数据)
     */
    public static String getHeartbeat() throws JSONException {
        jsonObject = new JSONObject();
        JSONObject object = new JSONObject();
        object.put("t", DateUtil.getTime());
        jsonArray = new JSONArray();
        jsonArray.put(object);
        jsonObject.put("U", 100002);
        jsonObject.put("P", "zwrx01");
        jsonObject.put("T", "V1");
        jsonObject.put("L", jsonArray.toString());
        return StringEscapeUtils.unescapeJava(jsonObject.toString()).replace("\"[","[").replace("]\"","]") + "\\r\\n";
    }

    /**
     * 数据包(含四条功能数据)
     */
    public static String getData() throws JSONException {
        jsonObject = new JSONObject();
        jsonArray = new JSONArray();
        for (int i = 0; i < 5; i++) {
            JSONObject object = new JSONObject();
            switch (i) {
                case 0:
                    object.put("F", 2005);
                    object.put("R", "Aa");
                    object.put("t",  DateUtil.getTime());
                    object.put("Va", 5);
                    object.put("t",  DateUtil.getTime());
                    object.put("Aa", 13);
                    object.put("t",  DateUtil.getTime());
                    object.put("Ab", 116.38);
                    object.put("t",  DateUtil.getTime());
                    object.put("Da", 38.2);
                    break;
                case 1:
                    object.put("F", 2006);
                    object.put("R", "Aa");
                    object.put("t",  DateUtil.getTime());
                    object.put("Va", 5);
                    object.put("t",  DateUtil.getTime());
                    object.put("Aa", 13);
                    object.put("t",  DateUtil.getTime());
                    object.put("Ab", 116.38);
                    object.put("t",  DateUtil.getTime());
                    object.put("Da", 38.2);
                    break;
                case 2:
                    object.put("F", 2007);
                    object.put("R", "Ab");
                    object.put("t",  DateUtil.getTime());
                    object.put("Va", 5);
                    object.put("t",  DateUtil.getTime());
                    object.put("Lb", 5);
                    object.put("t",  DateUtil.getTime());
                    object.put("Ka", 13);
                    break;
                case 3:
                    object.put("F", 2008);
                    object.put("R", "Ab");
                    object.put("t",  DateUtil.getTime());
                    object.put("Va", 5);
                    object.put("t",  DateUtil.getTime());
                    object.put("Lb", 5);
                    object.put("t",  DateUtil.getTime());
                    object.put("Ka", 13);
                    break;
            }
            jsonArray.put(object);
        }
        jsonObject.put("U", 100002);
        jsonObject.put("P", "zwrx01");
        jsonObject.put("T", "V0");
        jsonObject.put("L", jsonArray.toString());
        return StringEscapeUtils.unescapeJava(jsonObject.toString()).replace("\"[","[").replace("]\"","]") + "\\r\\n";
    }

    /**
     * 状态包
     */
    public static String getStaus() throws JSONException {
        jsonObject = new JSONObject();
        JSONObject object = new JSONObject();
        object.put("Kc", "1");
        object.put("t",  DateUtil.getTime());
        jsonArray = new JSONArray();
        jsonArray.put(object);
        jsonObject.put("U", 100002);
        jsonObject.put("P", "zwrx01");
        jsonObject.put("T", "V2");
        jsonObject.put("L", jsonArray.toString());
        return StringEscapeUtils.unescapeJava(jsonObject.toString()).replace("\"[","[").replace("]\"","]") + "\\r\\n";
    }

    /**
     * 上传照片
     */
    public static String getPhoto(String base64) throws JSONException {
        jsonObject = new JSONObject();
        JSONObject object = new JSONObject();
        object.put("img", base64);
        jsonArray = new JSONArray();
        jsonArray.put(object);
        jsonObject.put("U", 100002);
        jsonObject.put("P", "zwrx01");
        jsonObject.put("T", "V3");
        jsonObject.put("L", jsonArray.toString());
        return StringEscapeUtils.unescapeJava(jsonObject.toString()).replace("\"[","[").replace("]\"","]") + "\\r\\n";
    }

}