package com.goshoppi.pos.ui.customer

import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.creditHistoryRepo.CreditHistoryRepository
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.model.local.CreditHistory
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.android.synthetic.main.activity_customer_managment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@Suppress("DEPRECATION")
class CustomerManagmentActivity : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener, CoroutineScope {
    private lateinit var mJob: Job

    private lateinit var sharedPref: SharedPreferences
    @Inject
    lateinit var masterProductRepository: MasterProductRepository
    @Inject
    lateinit var customerRepository: CustomerRepository
    @Inject
    lateinit var creditHistoryRepository: CreditHistoryRepository

    private var selectedUser: String? = null
    private var userDebt = 0.00
    private var customer: LocalCustomer = LocalCustomer()

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_customer_managment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        mJob = Job()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            toolbar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_toolbar_color) )
        } else{
            toolbar.setBackgroundResource(R.color.colorPrimaryLight)
        }
        initView()
    }

    private fun initView() {


        loadCustomer()

        /*   btnDelete.setOnClickListener {
               if (selectedUser != null)
                   launch {
                       customerRepository.deleteLocalCustomers(selectedUser!!.toLong())

                   }
           }*/
        tbOptions.setSelectedTabIndicatorColor(resources.getColor(R.color.light_green))

        imvEdit.setOnClickListener {
            showDialogue(customer)
        }
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                svSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(param: String?): Boolean {
                if (param != null && param != "") {
                    launch {
                        val listOfCustomer =
                            customerRepository.searchLocalStaticCustomers(param) as ArrayList<LocalCustomer>
                        setUpRecyclerView(listOfCustomer)
                    }

                }
                return true
            }
        })

        svSearch.setOnCloseListener(object : android.widget.SearchView.OnCloseListener, SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                loadCustomer()
                return false
            }

        })


        ivPayDebt.setOnClickListener {
            if (!edCreditPayable.text.trim().isEmpty()) {
                val payable = edCreditPayable.text.toString().toDouble()
                if (payable < userDebt) {
                    payDebt(payable)
                } else {
                    Utils.showMsg(this, "Invalid Amount")
                }
            }
        }

        launch {
            customerRepository.getTotalDebit().observe(this@CustomerManagmentActivity
                , Observer {
                    if (it != null) {
                        tvTotalDebt.text = String.format("%.2f AED", it.toDouble())
                    } else {
                        tvTotalDebt.text = "0.00"

                    }
                })
        }
    }

    private fun payDebt(payable: Double) {
        launch {
            var credit: Double = 0.00
            val debt = customerRepository.getCustomerStaticCredit(selectedUser.toString())

            if (debt != 0.00) {
                credit = debt - payable

                val transaction = CreditHistory()
                transaction.customerId = selectedUser!!.toLong()
                transaction.orderId = 0
                transaction.paidAmount = payable
                transaction.transcationDate = Utils.getTodaysDate()
                transaction.creditAmount = 0.00
                transaction.totalCreditAmount = credit
                customerRepository.updateCredit(
                    transaction.customerId.toString(),
                    credit,
                    Utils.getTodaysDate()
                )
                creditHistoryRepository.insertCreditHistory(transaction)

            }
            Utils.hideSoftKeyboard(this@CustomerManagmentActivity)
            Utils.showMsg(this@CustomerManagmentActivity, "Amount Paid successfully")
            edCreditPayable.setText("")
        }
    }

    private fun loadCustomer() {
        launch {
            val t = customerRepository.loadAllStaticLocalCustomer()
            if (t.size != 0 && t.isNotEmpty()) {
                setUpRecyclerView(t as ArrayList<LocalCustomer>)
                val obj = Gson().toJson(t[0])
                updateView(t[0])
                setupViewPager(obj)
                tvCustomerCount.text = t.size.toString()

            } else {
                Utils.showAlert(false,
                    "No customer added found"
                    , " Please add customers ",
                    this@CustomerManagmentActivity,
                    DialogInterface.OnClickListener { _, _ ->
                        finish()
                    })
            }

        }
        /* customerRepository.loadAllLocalCustomer().observe(this, Observer<List<LocalCustomer>> { t ->

         })*/
    }

    private fun showDialogue(localCustomer: LocalCustomer) {

        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_customer_update, null)
        val alertBox = AlertDialog.Builder(this)
        alertBox.setView(view)
        alertBox.setCancelable(true)
        val dialog = alertBox.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val btnClose: ImageView = view.findViewById(R.id.btn_close_dialog)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }


        val edMbl: TextInputEditText = view.findViewById(R.id.edMbl)
        val edAltMbl: TextInputEditText = view.findViewById(R.id.edAltMbl)
        val edName: TextInputEditText = view.findViewById(R.id.tvPrdDes)
        val edGstin: TextInputEditText = view.findViewById(R.id.edGstin)
        val edAddress: TextInputEditText = view.findViewById(R.id.edAddress)
        val btnSave: Button = view.findViewById(R.id.btnSave)

        edMbl.setText(localCustomer.phone.toString())
        edAltMbl.setText(localCustomer.alternativePhone.toString())
        edName.setText(localCustomer.name.toString())
        edGstin.setText(localCustomer.gstin.toString())
        edAddress.setText(localCustomer.address.toString())

        btnSave.setOnClickListener {
            launch {
                localCustomer.alternativePhone = edAltMbl.text.toString()
                localCustomer.name = edName.text.toString()
                localCustomer.gstin = edGstin.text.toString()
                localCustomer.address = edAddress.text.toString()
                customerRepository.insertLocalCustomer(localCustomer)
                Utils.showMsgShortIntervel(this@CustomerManagmentActivity, "Customer details updated")
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupViewPager(customer: String) {

        val viewpagerAdapter = DashboardViewpager(supportFragmentManager)
        tabViewPager.adapter = viewpagerAdapter
        val summeryFrag = CustomerSummeryFragment.newInstance(customer)
        val billFragment = CustomerBillFragment.newInstance(customer)
        val transactionFragment = TransactionFragment.newInstance(customer)

        viewpagerAdapter.addFragment(summeryFrag, "Summery")
        viewpagerAdapter.addFragment(billFragment, "Bill")
        viewpagerAdapter.addFragment(transactionFragment, "Transaction")
        tbOptions.setupWithViewPager(tabViewPager)
        tabViewPager.adapter!!.notifyDataSetChanged()

    }

    private fun setUpRecyclerView(list: ArrayList<LocalCustomer>) {

        rc_product_details_variants.layoutManager =
            LinearLayoutManager(this@CustomerManagmentActivity)
        rc_product_details_variants.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_dashboard_customer_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvName = mainView.findViewById<TextView>(R.id.tvName)
                val tvPersonPhone = mainView.findViewById<TextView>(R.id.tvPersonPhone)
                val tvDebt = mainView.findViewById<TextView>(R.id.tvDebt)
                val lvCustomersView = mainView.findViewById<LinearLayout>(R.id.lvCustomersView)

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                    lvCustomersView.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.customer_ripple_bg_button) );
                }
                if (itemData.name.equals(Constants.ANONYMOUS)) {
                    tvName.text = getString(R.string.non_registered)
                    tvPersonPhone.text = getString(R.string.non_text)

                } else {
                    tvName.text = itemData.name!!.toUpperCase()
                    tvPersonPhone.text = itemData.phone.toString()
                }
                launch {

                    customerRepository.getCustomerCredit(itemData.phone.toString())
                        .observe(this@CustomerManagmentActivity, Observer {
                            if (it != null) tvDebt.text = String.format("%.2f AED", it.toDouble()) else tvDebt.text =
                                getString(R.string.zero_aed)
                        })
                }

                mainView.setOnClickListener {
                    val obj = Gson().toJson(itemData)
                    setupViewPager(obj)
                    updateView(itemData)

                }
            }
    }

    private fun updateView(customer: LocalCustomer) {
        this.customer = customer
        if (customer.name == Constants.ANONYMOUS) {
            imvEdit.visibility = View.GONE
        } else
            imvEdit.visibility = View.VISIBLE

        selectedUser = customer.phone.toString()
        if (customer.name.equals(Constants.ANONYMOUS)) {
            tvCustomerName.text = "Non Registered"
            tvPhone.text = "Phone:xxxxxxx"

        } else {
            tvCustomerName.text = customer.name
            tvPhone.text = "Phone:${customer.phone.toString()}"
        }
        launch {
            customerRepository.getCustomerCredit(customer.phone.toString())
                .observe(this@CustomerManagmentActivity, Observer {
                    if (it != null) {
                        userDebt = it
                        if (it.toDouble() == 0.00) {
                            tvUserDebt.text = "0.00 AED"

                        } else
                            tvUserDebt.text = String.format("-%.2f AED", it.toDouble())

                    } else {
                        userDebt = 0.00

                    }
                })
            customerRepository.getTotalTransaction(customer.phone.toString()).observe(this@CustomerManagmentActivity
                , Observer {
                    if (it != null) {
//                        edCreditPayable.setText("${it.toString()} AED")
                    }
                })
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key.equals(getString(R.string.pref_app_theme_color_key))) {
            setAppTheme(sharedPreferences)
            recreate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

     class DashboardViewpager(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {

        private val fragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()
        override fun getItem(p0: Int): Fragment {
            return fragmentList[p0]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }

        fun addFragment(fragement: Fragment, title: String) {
            fragmentList.add(fragement)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

}
