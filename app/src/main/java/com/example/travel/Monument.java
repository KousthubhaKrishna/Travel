package com.example.travel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;

public class Monument {
    public String name,info;
    String latitude,longitude;
    String likes;

    public Monument(){}

    public Monument(String name,String info,String latitude,String longitude,String likes)
    {
        this.name = name;
        this.info = info;
        this.latitude = latitude;
        this.longitude = longitude;
        this.likes = likes;
    }

    public HashMap<String, String> toMap() {
        HashMap<String,String> result = new HashMap<>();
        result.put("name", name);
        result.put("info", info);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("likes", likes);
        return result;
    }
}
