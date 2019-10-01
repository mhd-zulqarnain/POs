package com.goshoppi.pos.ui.customer

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
//import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.ui.customer.viewmodel.BillDetailViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.android.synthetic.main.activity_customer_bill_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

private const val ORDER_OBJ = "orderobject"

class CustomerBillDetailActivity : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope{
    lateinit var mJob: Job
    private lateinit var order: Order
    private lateinit var sharedPref: SharedPreferences
    private lateinit var billDetailViewModel: BillDetailViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private var position = 0
    @Inject
    lateinit var localVariantRepository: LocalVariantRepository
    private var checkIfDoubleDateIsSelected = false
    var dateStart: Long = 0L
    var dateEnd: Long = 0L
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
                tvTotalOrders.text = it.size.toString()
            } else
                showNoDataView()

        })

        billDetailViewModel.listOfOrderFilterItemObservable.observe(this, Observer {
            if (it.size != 0) {
                setUpOrderRecyclerView(it as ArrayList<Order>)
                tvTotalOrders.text = it.size.toString()
            } else
                showNoDataView()
        })
        billDetailViewModel.listOfOrderItemObservable.observe(this, Observer {
            if (it.size != 0) {
                setUpOrderItemRecyclerView(it as ArrayList<OrderItem>)
            }
        })

        ivFilterByMonth.setOnClickListener {
            filterByMonth()
        }
        ivFilterTodays.setOnClickListener {
            filterByToday()
        }
        ivRange.setOnClickListener {
            showCalenderDialog()
        }
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
        hideNoDataView()
        list.forEach {
            total += it.orderAmount!!.toDouble()
        }
        tvTotalAmount.text = String.format("%.2f AED", total)
        if (list.isNotEmpty() && list.size > 0) {
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
                tvDate.text = itemData.orderDate?.let { Utils.getDateFromLong(it) }
                tvPaymentStatus.text = itemData.paymentStatus
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
        tvBillDate.text = order.orderDate?.let { Utils.getDateFromLong(it) }
        tvTotalBillAmount.text = String.format("%.2f AED", order.orderAmount!!.toDouble())
        tvDiscount.text = String.format("%.2f AED", order.discount!!.toDouble())
        tvNetAmount.text = String.format("%.2f AED", order.orderAmount!!.toDouble())
        if (order.paymentStatus == Constants.CREDIT)
            tvCredit.text = String.format("%.2f AED", order.orderAmount!!.toDouble())
        else
            tvCash.text = String.format("%.2f AED", order.orderAmount!!.toDouble())
        if (order.discount!!.toDouble() > 1) {
            val paid = order.orderAmount!!.toDouble() - order.discount!!.toDouble()
            tvTotalPaid.text = String.format("%.2f AED", paid)
        } else
            tvTotalPaid.text = String.format("%.2f AED", order.orderAmount!!.toDouble())
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
                tvPaymentStatus.text = itemData.productQty.toString()
            }
    }

    private fun showCalenderDialog() {
        val view: View = LayoutInflater.from(this).inflate(R.layout.custom_date_picker, null)
        val alertBox = AlertDialog.Builder(this)
        alertBox.setView(view)
        alertBox.setCancelable(false)
        val dialog = alertBox.create()
        val calender: DateRangeCalendarView = view.findViewById(R.id.custom_calendar)
        val containerTo: LinearLayout = view.findViewById(R.id.citizen_custom_calender_contianer_to)
        val txtFromYear: TextView = view.findViewById(R.id.tv_calender_from_year)
        val txtFromDate: TextView = view.findViewById(R.id.tv_calender_from_date)
        val txtFromLabel: TextView = view.findViewById(R.id.tv_calender_from_label)

        val txtToYear: TextView = view.findViewById(R.id.tvcalender_to_year)
        val txtToDate: TextView = view.findViewById(R.id.tv_calender_to_date)

        val btnCancel: Button = view.findViewById(R.id.btn_citizen_custom_calender_cancel)
        val btnClear: Button = view.findViewById(R.id.btn_custom_calender_clear)
        val btnSet: Button = view.findViewById(R.id.btn_custom_calender_set)

        btnClear.setOnClickListener {
            //calender.resetAllSelectedViews()
            containerTo.visibility = View.GONE
            txtFromLabel.text = "Choose Date"
            txtFromYear.text = ""
            txtFromDate.text = ""

        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSet.setOnClickListener {
            if (checkIfDoubleDateIsSelected) {
                val filter = "${order.customerId},$dateStart,$dateEnd"
                billDetailViewModel.getRangedData(filter)
                reset()
            } else {
                checkIfDoubleDateIsSelected = false
            }
            dialog.dismiss()
        }

        calender.setCalendarListener(object : DateRangeCalendarView.CalendarListener {
            override fun onFirstDateSelected(startDate: Calendar) {

            }

            override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
                containerTo.visibility = View.VISIBLE
                txtFromLabel.text = "From"
                txtToYear.text = endDate.get(Calendar.YEAR).toString()
                val dateFormat = SimpleDateFormat("E,MMM d")
                txtToDate.text = dateFormat.format(endDate.time)

                val sDate = java.sql.Date(
                    startDate.get(
                        Calendar.YEAR
                    ) - 1900,
                    startDate.get(Calendar.MONTH),
                    startDate.get(Calendar.DAY_OF_MONTH)
                )
                endDate.set(Calendar.HOUR_OF_DAY, 23)
                endDate.set(Calendar.MINUTE, 59)
                endDate.set(Calendar.SECOND, 59)
                endDate.set(Calendar.MILLISECOND, 999)
                val eDate = java.sql.Date(endDate.timeInMillis)
                dateStart = sDate.time
                dateEnd = eDate.time
                checkIfDoubleDateIsSelected = dateStart != null && dateEnd != null

            }
        })

        dialog.show()
    }

    private fun reset() {

        dateStart = 0L
        dateEnd = 0L
        checkIfDoubleDateIsSelected = false

    }

    private fun showNoDataView() {
        lvOrderNoData.visibility = View.VISIBLE
        lvNoOrderItem.visibility = View.VISIBLE
        lvNoOrderItem.visibility = View.VISIBLE
        lvNoDetails.visibility = View.VISIBLE
        tvTotalOrders.text = "0"
        tvPrice.text = "0 AED"
        tvTotalAmount.text = "0 AED"
        tvTotalProduct.text = "0"
    }

    private fun hideNoDataView() {
        lvOrderNoData.visibility = View.GONE
        lvNoOrderItem.visibility = View.GONE
        lvNoOrderItem.visibility = View.GONE
        lvNoDetails.visibility = View.GONE
    }

    private fun filterByToday() {
        val formatter = Utils.dateFormat
        val date = formatter.parse(
            Utils.getDateFromLong(
                System.currentTimeMillis()
            )
        )
        val dateS= date.time
        //Starting time of the day to next 23 hour
        val dateE= date.time +(23*60*60*1000)

        val filter = "${order.customerId},$dateS,$dateE"
        billDetailViewModel.getRangedData(filter)
        reset()
    }

    private fun filterByMonth() {
        val currentDate = Calendar.getInstance()
        currentDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), 29)
        val dateE = currentDate.timeInMillis
        currentDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), 1)
        val dateS = currentDate.timeInMillis

        val filter = "${order.customerId},$dateS,$dateE"
        billDetailViewModel.getRangedData(filter)
        reset()


    }
    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
}
