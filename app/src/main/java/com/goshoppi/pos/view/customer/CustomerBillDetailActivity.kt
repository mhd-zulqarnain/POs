package com.goshoppi.pos.view.customer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.view.customer.viewmodel.BillDetailViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.ishaquehassan.recyclerviewgeneraladapter.addListDivider
import kotlinx.android.synthetic.main.activity_customer_bill_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
private const val ORDER_OBJ = "orderobject"

class CustomerBillDetailActivity : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope, View.OnClickListener {
    lateinit var mJob: Job
    private lateinit var order :Order
    private lateinit var sharedPref: SharedPreferences
    private lateinit var billDetailViewModel: BillDetailViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.IO

    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        mJob=Job()
        return R.layout.activity_customer_bill_detail
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key.equals(getString(R.string.pref_app_theme_color_key))) {
            setAppTheme(sharedPreferences)
            recreate()
        }
    }

    override fun onClick(v: View?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        billDetailViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(BillDetailViewModel::class.java)
        val temp=intent.getStringExtra(ORDER_OBJ)
        order = Gson().fromJson(temp, Order::class.java)
      initView()
    }

    private fun initView() {
        billDetailViewModel.getUserData(order.customerId!!.toString())
        billDetailViewModel.listOfOrdersObservable.observe(this, Observer {
            if (it.size != 0) {
                setUpOrderRecyclerView(it as ArrayList<Order>)
            }
        })
    }

    private fun setAppTheme(sharedPreferences: SharedPreferences) {

        when (sharedPreferences.getString(
            getString(R.string.pref_app_theme_color_key),
            getString(R.string.pref_color_default_value)
        )) {

            getString(R.string.pref_color_default_value) -> {
                setTheme(R.style.Theme_App)
            }

            getString(R.string.pref_color_blue_value) -> {
                setTheme(R.style.Theme_App_Blue)
            }

            getString(R.string.pref_color_green_value) -> {
                setTheme(R.style.Theme_App_Green)
            }
        }
    }

    private fun setUpOrderRecyclerView(list: ArrayList<Order>) {

        rvOrders.layoutManager = LinearLayoutManager(this@CustomerBillDetailActivity)
        rvOrders.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.inflator_customer_bill_detail)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvAmount = mainView.findViewById<TextView>(R.id.tvAmount)
                val tvDate = mainView.findViewById<TextView>(R.id.tvDate)
                val tvPaymentStatus = mainView.findViewById<TextView>(R.id.tvPaymentStatus)
                tvAmount.text = String.format("%.2f AED", itemData.orderAmount!!.toDouble())
                tvDate.text = itemData.orderDate
                tvPaymentStatus.setText(itemData.paymentStatus)
            }
    }

}
