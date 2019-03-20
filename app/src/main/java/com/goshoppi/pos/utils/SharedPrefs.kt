package com.goshoppi.pos.utils

import android.content.SharedPreferences

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




}