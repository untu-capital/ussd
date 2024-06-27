package com.example.ussd1.util;

import org.json.JSONObject;
import org.json.XML;

public class XmlUtil {
    public static String convertToXml(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return XML.toString(jsonObject);
    }
}