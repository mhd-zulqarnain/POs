package com.goshoppi.pos.view.distributors

import android.app.Activity
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.PurchaseOrderRepo.PurchaseOrderRepository
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.model.local.PoHistory
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_customer_managment.*
import kotlinx.android.synthetic.main.activity_customer_managment.edCreditPayable
import kotlinx.android.synthetic.main.activity_customer_managment.ivPayDebt
import kotlinx.android.synthetic.main.activity_customer_managment.rc_product_details_variants
import kotlinx.android.synthetic.main.activity_customer_managment.svSearch
import kotlinx.android.synthetic.main.activity_customer_managment.tabViewPager
import kotlinx.android.synthetic.main.activity_customer_managment.tbOptions
import kotlinx.android.synthetic.main.activity_customer_managment.tvCustomerCount
import kotlinx.android.synthetic.main.activity_customer_managment.tvCustomerName
import kotlinx.android.synthetic.main.activity_customer_managment.tvPhone
import kotlinx.android.synthetic.main.activity_customer_managment.tvTotalDebt
import kotlinx.android.synthetic.main.activity_customer_managment.tvUserDebt
import kotlinx.android.synthetic.main.activity_distributors_managment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@Suppress("DEPRECATION")
class DistributorsManagmentActivity :
    BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope {
    private lateinit var mJob: Job

    private lateinit var sharedPref: SharedPreferences
    @Inject
    lateinit var masterProductRepository: MasterProductRepository
    @Inject
    lateinit var distributorsRepository: DistributorsRepository

    @Inject
    lateinit   var purchaseOrderRepository: PurchaseOrderRepository

    private var selectedUser: String? = null
    private var userDebt = 0.00
    private var distributor =Distributor()

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_distributors_managment
    }

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


        loadDistributor()

        /*  btnDelete.setOnClickListener {
              if (selectedUser != null)
                  launch {
                      distributorsRepository.deleteDistributors(selectedUser!!.toLong())

                  }
          }*/
        ivEdit.setOnClickListener{
            showDialogue(distributor)
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
                            distributorsRepository.searchLocalStaticDistributors(param) as ArrayList<Distributor>
                        setUpRecyclerView(listOfCustomer)
                    }

                }
                return true
            }
        })

        svSearch.setOnCloseListener(object : android.widget.SearchView.OnCloseListener, SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                loadDistributor()
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
            distributorsRepository.getTotalDebit().observe(this@DistributorsManagmentActivity
                , Observer {
                    if (it != null) {
                        tvTotalDebt.text = String.format("%.2f AED", it.toDouble())
                    } else {
                        tvTotalDebt.setText("0.00")

                    }
                })
        }
    }

    private fun payDebt(payable: Double) {
        launch {
            var credit: Double = 0.00
            val debt = distributorsRepository.getDistributorStaticCredit(selectedUser.toString())

            if (debt != 0.00) {
                credit = debt - payable

                val transaction = PoHistory()
                transaction.distributorId = selectedUser!!.toLong()
                transaction.poId = 0
                transaction.paidAmount = payable
                transaction.transcationDate = Utils.getTodaysDate()
                transaction.creditAmount = 0.00
                distributorsRepository.updateCredit(
                    transaction.distributorId.toString(),
                    credit,
                    System.currentTimeMillis().toString()
                )
                purchaseOrderRepository.insertPoHistory(transaction)

            }
            Utils.hideSoftKeyboard(this@DistributorsManagmentActivity)
            Utils.showMsg(this@DistributorsManagmentActivity, "Amount Paid successfully")
            edCreditPayable.setText("")
        }
    }

    private fun loadDistributor() {
        launch {
            val t = distributorsRepository.loadAllStaticDistributor()
            if (t.size != 0 && t.isNotEmpty()) {
                setUpRecyclerView(t as ArrayList<Distributor>)
                val obj = Gson().toJson(t[0])
                updateView(t[0])
                setupViewPager(obj)
                tvCustomerCount.setText(t.size.toString())

            } else {
                Utils.showAlert(false,
                    "No customer added found"
                    , " Please add customers ",
                    this@DistributorsManagmentActivity,
                    DialogInterface.OnClickListener { _, _ ->
                        finish()
                    })
            }

        }
        /* distributorsRepository.loadAllDistributor().observe(this, Observer<List<Distributor>> { t ->

         })*/
    }

    private fun setupViewPager(distributors: String) {

        val viewpagerAdapter = DashboardViewpager(supportFragmentManager)
        tabViewPager.adapter = viewpagerAdapter
        val summeryFrag = DistributorsSummeryFragment.newInstance(distributors)
        val transactionFragment = DistributorsTransactionFragment.newInstance(distributors)

        viewpagerAdapter.addFragment(summeryFrag, "Summery")
        viewpagerAdapter.addFragment(transactionFragment, "Transaction")
        tbOptions.setupWithViewPager(tabViewPager)
        tabViewPager.adapter!!.notifyDataSetChanged()

    }

    private fun setUpRecyclerView(list: ArrayList<Distributor>) {

        rc_product_details_variants.layoutManager =
            LinearLayoutManager(this@DistributorsManagmentActivity)
        rc_product_details_variants.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_dashboard_customer_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvName = mainView.findViewById<TextView>(R.id.tvName)
                val tvPersonPhone = mainView.findViewById<TextView>(R.id.tvPersonPhone)
                val tvDebt = mainView.findViewById<TextView>(R.id.tvDebt)
                if (itemData.name.equals(Constants.ANONYMOUS)) {
                    tvName.text = "NON REGISTERED"
                    tvPersonPhone.text = "xxxxxx"

                } else {
                    tvName.text = itemData.name!!.toUpperCase()
                    tvPersonPhone.text = itemData.phone.toString()
                }
                launch {

                    distributorsRepository.getDistributorCredit(itemData.phone.toString())
                        .observe(this@DistributorsManagmentActivity, Observer {
                            if (it != null) tvDebt.text = String.format("%.2f AED", it.toDouble()) else tvDebt.text =
                                "0 AED"
                        })
                }

                mainView.setOnClickListener {
                    val obj = Gson().toJson(itemData)
                    setupViewPager(obj)
                    updateView(itemData)

                }
            }
    }

    private fun updateView(distributor: Distributor) {
        this.distributor=distributor
        selectedUser = distributor.phone.toString()
            tvCustomerName.text = distributor.name
        launch {
            distributorsRepository.getDistributorCredit(distributor.phone.toString())
                .observe(this@DistributorsManagmentActivity, Observer {
                    if (it != null) {
                        userDebt = it
                        tvUserDebt.text = String.format("-%.2f AED", it.toDouble())

                    } else {
                        tvUserDebt.text = "0 AED"
                        userDebt = 0.00

                    }
                })

        }

        tvPhone.text = "Phone:${distributor.phone.toString()}"
    }

    private fun showDialogue(distributor: Distributor) {

        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_distributor_update, null)
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
        val edName: TextInputEditText = view.findViewById(R.id.edName)
        val edGstin: TextInputEditText = view.findViewById(R.id.edGstin)
        val edAddress: TextInputEditText = view.findViewById(R.id.edAddress)
        val btnSave: Button = view.findViewById(R.id.btnSave)

        edMbl.setText(distributor.phone.toString())
        edAltMbl.setText(distributor.alternativePhone.toString())
        edName.setText(distributor.name.toString())
        edGstin.setText(distributor.gstin.toString())
        edAddress.setText(distributor.address.toString())

        btnSave.setOnClickListener {
            launch {
                distributor.alternativePhone= edAltMbl.text.toString()
                distributor.name= edName.text.toString()
                distributor.gstin= edGstin.text.toString()
                distributor.address= edAddress.text.toString()
                distributorsRepository.insertDistributor(distributor)
                Utils.showMsgShortIntervel(this@DistributorsManagmentActivity,"Distributor details updated")
            }
            dialog.dismiss()
        }

        dialog.show()
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