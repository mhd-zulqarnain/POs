package com.goshoppi.pos.architecture.helper;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.goshoppi.pos.model.Variant;

import java.lang.reflect.Type;
import java.util.List;

public class HelperConverter {


   /* @TypeConverter
    public List<Object> stringToObjectList(String data) {
        if (data == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Object>>() {
        }.getType();
        return gson.fromJson(data, type);
    }

    @TypeConverter
    public String objectListToString(List<Object> someObjects) {
        if (someObjects == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Object>>() {
        }.getType();
        return gson.toJson(someObjects, type);
    }*/


    @TypeConverter
    public List<Variant> stringToVariantList(String data) {
        if (data == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Variant>>() {}.getType();
        return gson.fromJson(data, type);
    }

    @TypeConverter
    public String variantListToString(List<Variant> someObjects) {
        if (someObjects == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Variant>>() {}.getType();
        return gson.toJson(someObjects, type);
    }

    @TypeConverter
    public List<String> stringToStringList(String data) {
        if (data == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(data, type);
    }

    @TypeConverter
    public String stringListToString(List<String> someObjects) {
        if (someObjects == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.toJson(someObjects, type);
    }
}
