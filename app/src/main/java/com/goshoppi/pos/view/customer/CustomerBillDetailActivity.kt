package com.goshoppi.pos.view.customer

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class CustomerBillDetailActivity : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope, View.OnClickListener {
    lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.IO

    override fun layoutRes(): Int {
        return R.layout.activity_customer_bill_detail
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    }

    override fun onClick(v: View?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_bill_detail)
    }
}
