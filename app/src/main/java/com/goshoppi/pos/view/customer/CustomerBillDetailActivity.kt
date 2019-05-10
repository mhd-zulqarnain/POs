package com.goshoppi.pos.view.customer

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
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.view.customer.viewmodel.BillDetailViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.android.synthetic.main.activity_customer_bill_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

private const val ORDER_OBJ = "orderobject"

class CustomerBillDetailActivity : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope, View.OnClickListener {
    lateinit var mJob: Job
    private lateinit var order: Order
    private lateinit var sharedPref: SharedPreferences
    private lateinit var billDetailViewModel: BillDetailViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private var position = 0
    @Inject
    lateinit var localVariantRepository: LocalVariantRepository

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.IO

    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        mJob = Job()
        return R.layout.activity_customer_bill_detail
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key.equals(getString(R.string.pref_app_theme_color_key))) {
            setAppTheme(sharedPreferences)
            recreate()
        }
    }

    override fun onClick(v: View?) = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        billDetailViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(BillDetailViewModel::class.java)
        val temp = intent.getStringExtra(ORDER_OBJ)
        order = Gson().fromJson(temp, Order::class.java)
        initView()
    }

    private fun initView() {
        billDetailViewModel.getOrderData(order.customerId!!.toString())
        billDetailViewModel.listOfOrdersObservable.observe(this, Observer {
            if (it.size != 0) {
                setUpOrderRecyclerView(it as ArrayList<Order>)
            }
            tvTotalOrders.setText(it.size.toString())
        })
        billDetailViewModel.listOfOrderItemObservable.observe(this, Observer {
            if (it.size != 0) {
                setUpOrderItemRecyclerView(it as ArrayList<OrderItem>)
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
        val itemViewList: ArrayList<View> = arrayListOf()
        var total = 0.0
        list.forEach {
            total += it.orderAmount!!.toDouble()
        }
        tvTotalAmount.text = "$total AED"
        if (list.isNotEmpty()&& list.size>0) {
            updateBillView(list[0])
            setOrderItemData(list[0])
            position = 0
        } else {
            Timber.e("Variant List is Empty")
        }
        rvOrders.layoutManager = LinearLayoutManager(this@CustomerBillDetailActivity)
        rvOrders.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.inflator_customer_bill_detail)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvAmount = mainView.findViewById<TextView>(R.id.tvAmount)
                val tvDate = mainView.findViewById<TextView>(R.id.tvDate)
                val tvPaymentStatus = mainView.findViewById<TextView>(R.id.tvPaymentStatus)
                val tvProductName = mainView.findViewById<TextView>(R.id.tvProductName)

                tvAmount.text = String.format("%.2f AED", itemData.orderAmount!!.toDouble())
                tvDate.text = itemData.orderDate
                tvPaymentStatus.setText(itemData.paymentStatus)
                itemViewList.add(viewHolder.itemView)
                tvProductName.visibility = View.GONE

                itemViewList[position].setBackgroundResource(R.color.text_light_gry)

                viewHolder.itemView.setOnClickListener {
                    setOrderItemData(itemData)
                    updateBillView(itemData)

                    position = viewHolder.adapterPosition

                    itemViewList.forEach {
                        if (itemViewList[viewHolder.adapterPosition] == it) {
                            it.setBackgroundResource(R.color.text_light_gry)
                        } else {
                            it.setBackgroundResource(R.color.white)
                        }
                    }
                }

            }
    }

    private fun updateBillView(order: Order) {
        tvbillDate.text = order.orderDate
        tvTotalBillAmount.text =String.format("%.2f AED", order.orderAmount!!.toDouble())
        tvDiscount.text= String.format("%.2f AED", order.discount!!.toDouble())
        tvNetAmount.text=String.format("%.2f AED", order.orderAmount!!.toDouble())
        if(order.paymentStatus==Constants.CREDIT)
        tvCredit.text=String.format("%.2f AED", order.orderAmount!!.toDouble())
        else
        tvCash.text=String.format("%.2f AED", order.orderAmount!!.toDouble())
        if(order.discount!!.toDouble()>1){
val paid =order.orderAmount!!.toDouble()-order.discount!!.toDouble()
            tvTotalPaid.text=String.format("%.2f AED", paid)
        }else
        tvTotalPaid.text= String.format("%.2f AED", order.orderAmount!!.toDouble())
    }

    private fun setOrderItemData(order: Order) {
        billDetailViewModel.getOrderItemData(order.orderId.toString())
    }

    private fun setUpOrderItemRecyclerView(list: ArrayList<OrderItem>) {
        var itemCount = 0
        var totalPrice = 0.0
        if (list.size != 0)
            list.forEach {
                itemCount += it.productQty!!.toInt()
                totalPrice += it.totalPrice!!.toInt()
            }
        tvTotalProduct.text = itemCount.toString()
        tvPrice.text = totalPrice.toString()
        rvOrderItem.layoutManager = LinearLayoutManager(this@CustomerBillDetailActivity)
        rvOrderItem.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.inflator_customer_bill_detail)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvAmount = mainView.findViewById<TextView>(R.id.tvAmount)
                val tvDate = mainView.findViewById<TextView>(R.id.tvDate)
                val tvPaymentStatus = mainView.findViewById<TextView>(R.id.tvPaymentStatus)
                val tvProductName = mainView.findViewById<TextView>(R.id.tvProductName)
                tvAmount.text = String.format("%.2f AED", itemData.totalPrice!!.toDouble())
                tvDate.text = itemData.addedDate.toString()
                launch {

                    /*   localVariantRepository.getVaraintNameByProdId(itemData.productId.toString())
                            .observe(this@CustomerBillDetailActivity, Observer {
    //                        if (it != null) tvProductName.text =it else tvProductName.text = ""
                        })*/
                    val name = localVariantRepository.getVaraintNameByProdId(itemData.productId.toString())

                    runOnUiThread {
                        tvProductName.text = name
                    }
                }
                tvPaymentStatus.setText(itemData.productQty.toString())
            }
    }


}
