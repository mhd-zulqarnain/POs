package com.goshoppi.pos.view.customer

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.model.local.LocalCustomer
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
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_customer_managment
    }

    private lateinit var sharedPref: SharedPreferences
    @Inject
    lateinit var masterProductRepository: MasterProductRepository
    @Inject
    lateinit var customerRepository: CustomerRepository

    private var selectedUser: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        mJob = Job()
        initView()
    }

    private fun initView() {


        loadCustomer()

        btnDelete.setOnClickListener {
            if (selectedUser != null)
                launch {
                    customerRepository.deleteLocalCustomers(selectedUser!!.toLong())

                }
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

    }

    private fun loadCustomer() {
        customerRepository.loadAllLocalCustomer().observe(this, Observer<List<LocalCustomer>> { t ->
            if (t != null && t.isNotEmpty()) {
                setUpRecyclerView(t as ArrayList<LocalCustomer>)
                val obj = Gson().toJson(t[0])
                updateView(t[0])
                setupViewPager(obj)
            } else {
                Utils.showAlert(false,
                    "No customer added found"
                    , " Please add customers ",
                    this@CustomerManagmentActivity,
                    DialogInterface.OnClickListener { _, _ ->
                        finish()
                    })
            }
        })
    }

    private fun setupViewPager(customer: String) {

        val viewpagerAdapter = DashboardViewpager(supportFragmentManager)
        tabViewPager.adapter = viewpagerAdapter
        val summeryFrag = CustomerSummeryFragment.newInstance(customer)
        val billFragment = CustomerBillFragment.newInstance(customer)

        viewpagerAdapter.addFragment(summeryFrag, "Summery")
        viewpagerAdapter.addFragment(billFragment, "Bill")
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
                tvName.text = itemData.name!!.toUpperCase()
                tvPersonPhone.text = itemData.phone.toString()
                launch {
                    customerRepository.getCustomerCredit(itemData.phone.toString())
                        .observe(this@CustomerManagmentActivity, Observer {
                            //                     if(pos==viewHolder.position)
                            if (it != null) tvDebt.text = "$it AED" else tvDebt.text = "0 AED"
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
        selectedUser = customer.phone.toString()
        tvCustomerName.text = customer.name
        launch {
            customerRepository.getCustomerCredit(customer.phone.toString())
                .observe(this@CustomerManagmentActivity, Observer {
                    if (it != null)
                        tvUserDebt.setText("$it AED")
                    else
                        tvUserDebt.text = "0 AED"
                })
        }
        launch {
            customerRepository.getTotalTransaction(customer.phone.toString()).observe(this@CustomerManagmentActivity
                , Observer {
                    if (it != null) {
                        tvTotalTransaction.setText("${it.toString()} AED")
                    }
                })
        }
        tvPhone.text = "Phone:${customer.phone.toString()}"
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
