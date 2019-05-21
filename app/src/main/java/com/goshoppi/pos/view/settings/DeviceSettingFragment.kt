package com.goshoppi.pos.view.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseFragment


class DeviceSettingFragment : BaseFragment() {
    override fun layoutRes(): Int {
    return R.layout.fragment_device_setting
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DeviceSettingFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
