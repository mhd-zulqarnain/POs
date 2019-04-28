package com.goshoppi.pos.architecture.helper;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.goshoppi.pos.model.master.MasterVariant;

import java.lang.reflect.Type;
import java.util.List;

public class HelperConverter {

    @TypeConverter
    public List<MasterVariant> stringToVariantList(String data) {
        if (data == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<MasterVariant>>() {}.getType();
        return gson.fromJson(data, type);
    }

    @TypeConverter
    public String variantListToString(List<MasterVariant> someObjects) {
        if (someObjects == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<MasterVariant>>() {}.getType();
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
