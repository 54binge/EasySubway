package com.binge.easysubway;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by Binge on 2017/9/2.
 */

public class test {
    public test() {
        Type type = null;
       List<Map<String, String>> list =  new Gson().fromJson("s", type);
    }
}
