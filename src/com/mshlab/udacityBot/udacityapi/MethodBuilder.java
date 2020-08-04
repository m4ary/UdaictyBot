package com.mshlab.udacityBot.udacityapi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MethodBuilder {


    public static String TotalEarning(String date) throws UnsupportedEncodingException {
        return "/me/earnings?start_date=" + URLEncoder.encode("\"" + date + "\"", "UTF-8");


    }
}
