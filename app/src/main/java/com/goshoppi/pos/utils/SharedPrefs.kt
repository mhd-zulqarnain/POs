package com.goshoppi.pos.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.goshoppi.pos.model.User

private const val USER_DATA = "user"
class SharedPrefs private constructor(){
    var mPrefs: SharedPreferences?= null

    companion object {
        private var sharedPrefs:SharedPrefs?=null
        fun getInstance():SharedPrefs?{
            if (sharedPrefs==null)
                sharedPrefs = SharedPrefs();
            return  sharedPrefs
        }

    }

        fun setUser(context: Context, user: User){
            mPrefs = context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            val editor =mPrefs!!.edit();
            val obj = Gson().toJson(user)
            editor.putString(USER_DATA,obj)
            editor.apply()
        }
        fun getUser(context: Context): User? {
            mPrefs = context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            val str= mPrefs!!.getString(USER_DATA,null)
            if(str==null)
                return null
            val obj = Gson().fromJson<User>(str, User::class.java)
            return  obj
        }

        fun savePref(context: Context, key: String, value: String) {
            mPrefs = context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            val prefsEditor = mPrefs!!.edit()
            prefsEditor.putString(key, value)
            prefsEditor.apply()
        }
        fun getPref(context: Context, key: String):String {
            mPrefs = context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            val str= mPrefs!!.getString(key,null)
            if(str!=null)
                return str
            return  ""
        }

        fun clearUser(context: Context){
            mPrefs = context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            val editor = mPrefs!!.edit()
            editor.clear()
            editor.apply()
        }
        fun clearPref(context: Context, key: String){
            mPrefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = mPrefs!!.edit()
            editor.clear()
            editor.apply()
        }






}