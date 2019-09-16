package com.goshoppi.pos.view.home

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.Payment
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.CustomerAdapter
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.view.home.viewmodel.CheckoutViewModel
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.include_discount_cal.*
import kotlinx.android.synthetic.main.include_payment_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class CheckoutActivity : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope, View.OnClickListener {

    var discountAmount = 0.00
    private var toastFlag = false
    lateinit var checkoutVm: CheckoutViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    var focusedEditText: EditText? = null

    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        toolbar.setTitleTextColor(ContextCompat.getColor(this@CheckoutActivity, R.color.blue));
        initView()

    }

    override fun layoutRes(): Int {
        return R.layout.activity_checkout
    }

    private fun initView() {

        checkoutVm = ViewModelProviders.of(this, viewModelFactory)
            .get(CheckoutViewModel::class.java)

        etCredit.setOnClickListener {
            focusedEditText = etCredit
            Utils.hideSoftKeyboard(this@CheckoutActivity)
            lvPaymentView.visibility = View.VISIBLE
            if (cvCalculator != null)
                cvCalculator.visibility = View.GONE
        }
        etCash.setOnClickListener {
            focusedEditText = etCash
            Utils.hideSoftKeyboard(this@CheckoutActivity)
            lvPaymentView.visibility = View.VISIBLE
            if (cvCalculator != null)
                cvCalculator.visibility = View.GONE
        }

        setPaymentCalculator()
        btnCustomerAdd.setOnClickListener(this)
        tvDiscount.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnCustomerAdd -> {
                addCustomerDialog()
            }
            R.id.tvDiscount -> {
                lvPaymentView.visibility = View.GONE
                cvCalculator.visibility = View.VISIBLE
                setUpCalculator()
            }
        }
    }

    fun placeOrder(isCredit: Boolean) {

        val cash = etCash.text.toString()
        val credit = etCredit.text.toString()

        val isValidAmount = isvalidAmount(cash, credit)


        checkoutVm.productBarCode.value = "-1"
        checkoutVm.weightedVariantid.value = "-1"

        if (checkoutVm.subtotal < 1 || checkoutVm.orderItemList.size == 0) {
            Utils.showMsg(this, "Please add products to place order")
        } else if (!isValidAmount.isEmpty()) {
            Utils.showMsg(this, isValidAmount)
        } else {


            if (isCredit) {
                checkoutVm.placeOrder(discountAmount, cash, credit, Payment.CREDIT)
                return
            }
            if (!cash.isEmpty() && !credit.isEmpty()) {
                checkoutVm.placeOrder(discountAmount, cash, credit, Payment.PARTIAL)
            }
            if (cash.isEmpty()) {
                checkoutVm.placeOrder(discountAmount, cash, credit, Payment.CREDIT)
            }
            if (credit.isEmpty()) {
                checkoutVm.placeOrder(discountAmount, cash, credit, Payment.CASH)
            }
        }

        //  checkoutVm.placeOrder(payment, discountAmount)

    }

    //region Customer

    private fun addCustomerDialog() {
        val view =
            LayoutInflater.from(this@CheckoutActivity).inflate(R.layout.dialog_add_customer, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val svSearch = view.findViewById<SearchView>(R.id.svSearch)
        val lvAddCus = view.findViewById<LinearLayout>(R.id.lvAddCus)
        val lvSearchCustomer = view.findViewById<LinearLayout>(R.id.lvSearchCustomer)
        val btnCustomerCancel = view.findViewById<Button>(R.id.btnCustomerCancel)
        val btnAddCustomer = view.findViewById<Button>(R.id.btnAddCustomer)
        val btn_close_dialog = view.findViewById<ImageView>(R.id.btn_close_dialog)
        val btnCreateCustomer = view.findViewById<Button>(R.id.btnCreateCustomer)

        val ed_cus_mbl = view.findViewById<TextInputEditText>(R.id.ed_cus_mbl)
        val ed_cus_name = view.findViewById<TextInputEditText>(R.id.ed_cus_name)
        val ed_cus_gstin = view.findViewById<TextInputEditText>(R.id.ed_cus_gstin)
        val ed_cus_address = view.findViewById<TextInputEditText>(R.id.ed_cus_address)
        val ed_alt_cus_mbl = view.findViewById<TextInputEditText>(R.id.ed_alt_cus_mbl)

        checkoutVm.searchCustomer("")
        /*
        * customer observable
        * */
        val listOfCustomer = ArrayList<LocalCustomer>()
        val customerAdapter = CustomerAdapter(this@CheckoutActivity, listOfCustomer)

        checkoutVm.cutomerListObservable.observe(this, Observer {
            if (listOfCustomer.size != 0)
                listOfCustomer.clear()
            if (it.size != 0) {
                it.forEach {
                    if (it.name != Constants.ANONYMOUS) {
                        listOfCustomer.add(it)
                    }
                    customerAdapter.notifyDataSetChanged()
                }

            }
        })
        val listView = view?.findViewById(R.id.lsCustomer) as ListView
        listView.adapter = customerAdapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val person = listOfCustomer[position]
                checkoutVm.customer = person
                tvPerson.setText(
                    person.name!!.toUpperCase() + " - " + person.phone
                )

                //tvUserDebt.text = String.format("%.2f AED", person.totalCredit)
                svSearch.isIconified = true
                dialog.dismiss()
            }
        /*
        * customer observable end
        * */
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                svSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(param: String?): Boolean {
                if (param != "") {
                    checkoutVm.searchCustomer(param!!)
                }
                return true
            }
        })



        btn_close_dialog.setOnClickListener {
            dialog.dismiss()
        }

        btnCustomerCancel.setOnClickListener {
            lvAddCus.visibility = View.GONE
            lvSearchCustomer.visibility = View.VISIBLE
        }
        btnAddCustomer.setOnClickListener {
            lvAddCus.visibility = View.VISIBLE
            lvSearchCustomer.visibility = View.GONE
            //addNewCustomer()
        }
        btnCreateCustomer.setOnClickListener {
            var isValid = true
            if (ed_cus_mbl.text.toString().trim() == "" || ed_cus_mbl.text.toString().trim().length < 9) {
                ed_cus_mbl.error = resources.getString(R.string.err_phone)
                ed_cus_mbl.requestFocus()
                isValid = false
            }
            if (ed_cus_name.text.toString().trim() == "") {
                ed_cus_name.error = resources.getString(R.string.err_not_empty)
                ed_cus_name.requestFocus()
                isValid = false
            }

            if (isValid) {
                val customer = LocalCustomer()
                customer.phone = ed_cus_mbl.text.toString().toLong()
                customer.alternativePhone = ed_alt_cus_mbl.text.toString()
                customer.gstin = ed_cus_gstin.text.toString()
                customer.gstin = ed_cus_gstin.text.toString()
                customer.name = ed_cus_name.text.toString()
                customer.address = ed_cus_address.text.toString()
                customer.isSynced = false
                customer.updatedAt = Utils.getTodaysDate()

                checkoutVm.addCustomer(customer)
                lvAddCus.visibility = View.GONE
                tvPerson.setText(
                    customer.name!!.toUpperCase() + " - " + customer.phone
                )
                checkoutVm.customer = customer
                //tvUserDebt.text = getString(R.string.zero_aed)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun clearCustomer() {
        checkoutVm.customer = checkoutVm.getAnonymousCustomer()
    }

    //endregion
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals(getString(R.string.pref_app_theme_color_key))) {
            setAppTheme(sharedPreferences!!)
            recreate()
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

    ////region Payment handling

    private fun setPaymentCalculator() {
        btnPaySeven.setOnClickListener(paymentOnClick)
        btnPayEight.setOnClickListener(paymentOnClick)
        btnPayNine.setOnClickListener(paymentOnClick)
        btnPayFour.setOnClickListener(paymentOnClick)
        btnPayFive.setOnClickListener(paymentOnClick)
        btnPaySix.setOnClickListener(paymentOnClick)
        btnPayOne.setOnClickListener(paymentOnClick)
        btnPayTwo.setOnClickListener(paymentOnClick)
        btnPayThree.setOnClickListener(paymentOnClick)
        btnPayCee.setOnClickListener(paymentOnClick)
        btnPayZero.setOnClickListener(paymentOnClick)
        btnPayPoint.setOnClickListener(paymentOnClick)

        btnPayErase.setOnClickListener(paymentOnClick)

        btnCurrencyffty.setOnClickListener(paymentOnClick)
        btnCurrencyten.setOnClickListener(paymentOnClick)
        btnCurrencytenty.setOnClickListener(paymentOnClick)
        btnCurrencyhdrd.setOnClickListener(paymentOnClick)
        btnCurrencyttHdrd.setOnClickListener(paymentOnClick)
        btnCurrencyfvHdrd.setOnClickListener(paymentOnClick)
        btnCurrencytwTh.setOnClickListener(paymentOnClick)
        btnCurrencyFive.setOnClickListener(paymentOnClick)
        btnCurrencyffty.setOnClickListener(paymentOnClick)
        btnPayBack.setOnClickListener(paymentOnClick)

    }

    private val paymentOnClick = View.OnClickListener { view ->

        when (view.id) {


            R.id.btnPayErase -> {
//                calculateDiscount(2.0)
                eraseOneCharacter()
            }

            R.id.btnPayCee -> {
                erasePaymentCal()
            }
            R.id.btnPayBack -> {
            }

            R.id.btn_point -> {
                setPaymentText(view as Button)
            }
            else -> {
                setPaymentText(view as Button)
            }
        }
    }


    private fun setPaymentText(btn_point: Button) {

        if (focusedEditText != null) {
            var text = "0"
            if (btn_point.text.toString() == ".") {
                if (!focusedEditText!!.text.toString().contains(".")) {
                    focusedEditText!!.setText(focusedEditText!!.text.toString() + btn_point.text.toString())
                }
            } else {
                if (btn_point.text.split(" ").size > 1) {
                    text = btn_point.text.split(" ")[1]
                } else {
                    text = focusedEditText!!.text.toString() + btn_point.text.toString()
                }
                if (focusedEditText!!.text.toString().trim().length < 7) {
                    focusedEditText!!.setText(text)
                }
            }
        }

    }


    private fun erasePaymentCal() {

        if (focusedEditText != null) {
            focusedEditText!!.setText("")
        }

    }

    private fun eraseOneCharacter() {


        if (focusedEditText != null) {
            if (!focusedEditText!!.text.isEmpty()) {
                val s = focusedEditText!!.text.toString()
                val txt = s.substring(0, s.length - 1)
                focusedEditText!!.setText(txt)
            }
        }

    }

    private fun isvalidAmount(cash: String, credit: String): String {
        val total = checkoutVm.subtotal - discountAmount

        val tmpTotal = total.toString().split('.')[0].toLong()

        if (cash.isEmpty() && credit.isEmpty()) {

            return "Please Enter the cash or credit amount"
        }
        if (!cash.isEmpty() && !credit.isEmpty()) {
            val tmpCredit = credit.toString().split('.')[0].toLong()
            val tmpCash = cash.toString().split('.')[0].toLong()

            if (tmpCredit + tmpCash > total)
                return "Amount is greater than payable amount"
            else
                return ""
        }
        if (!cash.isEmpty()) {
            val tmpCash = cash.toString().split('.')[0].toLong()
            if (cash.toDouble() > total) {
                return ""
                //return "Amount is greater than payable amount"
            } else if (tmpCash != tmpTotal) {
                return "Amount is Smaller than payable amount"
            }
        }
        if (!credit.isEmpty()) {
            val tmpCredit = credit.toString().split('.')[0].toLong()

            if (credit.toDouble() > total)
                return "Amount is greater than payable amount"
            else if (tmpCredit != tmpTotal) {
                return "Amount is Smaller than payable amount"
            }
        }

        return ""
    }

    //endregion

    ////region Discount calculator handling
    private var isCalulated = false

    private fun setUpCalculator() {
        btn_point.setOnClickListener(calcOnClick)
        btn_two_per.setOnClickListener(calcOnClick)
        btn_five_per.setOnClickListener(calcOnClick)
        btn_seven.setOnClickListener(calcOnClick)
        btn_eight.setOnClickListener(calcOnClick)
        btn_nine.setOnClickListener(calcOnClick)
        btn_four.setOnClickListener(calcOnClick)
        btn_five.setOnClickListener(calcOnClick)
        btn_six.setOnClickListener(calcOnClick)
        btn_one.setOnClickListener(calcOnClick)
        btn_two.setOnClickListener(calcOnClick)
        btn_three.setOnClickListener(calcOnClick)
        btn_zero.setOnClickListener(calcOnClick)
        btn_point.setOnClickListener(calcOnClick)
        btn_done.setOnClickListener(calcOnClick)
        btnErase.setOnClickListener(calcOnClick)
        btn_percent.setOnClickListener(calcOnClick)
    }

    private val calcOnClick = View.OnClickListener { view ->
        when (view.id) {
            R.id.btn_done -> {

                if (isCalulated) {
                    tvDiscount.setText(
                        String.format(
                            "%.2f AED",
                            tvCalTotal.text.toString().toDouble()
                        )
                    )
                    tvNetAmount.setText(
                        String.format(
                            "%.2f AED",
                            checkoutVm.subtotal - tvCalTotal.text.toString().toDouble()
                        )
                    )
                }
            }
            R.id.btn_five_per -> {
                calculateDiscount(5.0)
            }
            R.id.btn_two_per -> {
                calculateDiscount(2.0)
            }
            R.id.btn_percent -> {
                val temp = tvCalTotal.text.toString().trim()
                if (temp != "") {
                    if (temp.toDouble() < 100) {
                        calculateDiscount(temp.toDouble())
                    } else
                        Utils.showMsgShortIntervel(this@CheckoutActivity, "Invalid Entry")
                }

            }
            R.id.btnErase -> {
                eraseCal()
            }
            R.id.btn_point -> {
                if (!tvCalTotal.text.toString().contains(".")) {
                    setTextTotal(view as Button)
                }
            }
            else -> {
                setTextTotal(view as Button)
            }
        }
    }


    private fun setTextTotal(btn_point: Button) {

        if (isCalulated) {
            tvCalTotal.setText("")
            isCalulated = false
        }
        if (tvCalTotal.text.toString().contains(".")) {
            tvCalTotal.setText(" ${tvCalTotal.text}${btn_point.text}")
        }
        if (tvCalTotal.text.toString().trim().length < 2) {
            tvCalTotal.setText(" ${tvCalTotal.text}${btn_point.text}")
        }

    }

    private fun eraseCal() {
        if (!isCalulated) {
            var str = tvCalTotal.text
            if (str != null && str.length > 0) {
                str = str.substring(0, str.length - 1)
            }
            tvCalTotal.setText(str)
        } else {
            tvCalTotal.text = ""
            isCalulated = false
        }
    }

    private fun calculateDiscount(discount: Double) {

        isCalulated = true
        val amount = checkoutVm.subtotal
        val res = (amount / 100.0f) * discount
        discountAmount = res
        // val res = amount-(amount*( discount/ 100.0f))
        tvCalTotal.text = String.format("%.2f", res)
    }
    //endregion

}
