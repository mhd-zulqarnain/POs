package com.goshoppi.pos.view.customer

import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.android.synthetic.main.activity_customer_managment.*
import javax.inject.Inject

class CustomerManagmentActivity : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {
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

        initView()
    }

    private fun initView() {


        customerRepository.loadAllLocalCustomer().observe(this, Observer<List<LocalCustomer>> { t ->
            if (t != null &&t.size!=0) {
                setUpRecyclerView(t as ArrayList<LocalCustomer>)
                val obj = Gson().toJson(t[0])
                updateView(t[0])
                setupViewPager(obj)
            } else {
                Utils.showAlert(false,
                    "No customer added found"
                    , " Please add customers ",
                    this@CustomerManagmentActivity,
                    DialogInterface.OnClickListener { dialog, which ->
                        finish()
                    })
            }
        })

        btnDelete.setOnClickListener {
            if (selectedUser != null)
                customerRepository.deleteLocalCustomers(selectedUser!!.toLong())
        }
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(param: String?): Boolean {
                if (param != null && param != "") {
                    val listOfCustomer =
                        customerRepository.searchLocalStaticCustomers(param) as ArrayList<LocalCustomer>
                    setUpRecyclerView(listOfCustomer)
                }
                return true
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
        rc_product_details_variants.layoutManager = LinearLayoutManager(this@CustomerManagmentActivity)
        rc_product_details_variants.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_dashboard_customer_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvName = mainView.findViewById<TextView>(R.id.tvName)
                val tvPersonPhone = mainView.findViewById<TextView>(R.id.tvPersonPhone)
                val tvDebt = mainView.findViewById<TextView>(R.id.tvDebt)
                tvName.setText(itemData.name)
                tvDebt.text = "$423423"
                tvPersonPhone.text = itemData.phone.toString()

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
        tvDebt.text = "123 AED"
        tvPhone.text = customer.phone.toString()
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