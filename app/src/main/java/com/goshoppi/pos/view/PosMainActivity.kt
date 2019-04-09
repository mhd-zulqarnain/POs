package com.goshoppi.pos.view

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ListView
import android.widget.PopupWindow
import androidx.work.*
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.architecture.workmanager.StoreProductImageWorker
import com.goshoppi.pos.architecture.workmanager.StoreVariantImageWorker
import com.goshoppi.pos.architecture.workmanager.SyncWorker
import com.goshoppi.pos.di.component.DaggerAppComponent
import com.goshoppi.pos.di.module.AppModule
import com.goshoppi.pos.di.module.RoomModule
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.utils.Constants.*
import com.goshoppi.pos.utils.CustomerAdapter
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.view.inventory.InventoryHomeActivity
import com.goshoppi.pos.view.inventory.LocalInventoryActivity
import com.goshoppi.pos.view.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_pos_main.*
import kotlinx.android.synthetic.main.include_add_customer.*
import kotlinx.android.synthetic.main.include_customer_search.*
import timber.log.Timber
import javax.inject.Inject


class PosMainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var sharedPref: SharedPreferences
    @Inject
    lateinit var masterProductRepository: MasterProductRepository

    @Inject
    lateinit var localCustomerRepository: CustomerRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .roomModule(RoomModule(application))
            .build()
            .injectPosMainActivity(this)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        sharedPref.registerOnSharedPreferenceChangeListener(this)

        setContentView(R.layout.activity_pos_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        masterProductRepository.loadAllMasterProduct().observe(this,
            Observer<List<MasterProduct>> { t -> Timber.e("Total = ${t!!.size}") })

        setSupportActionBar(toolbar)
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_setting ->
                startActivity(Intent(this@PosMainActivity, SettingsActivity::class.java))
            R.id.inventory_prod ->
                startActivity(Intent(this@PosMainActivity, InventoryHomeActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {

        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        if (!sharedPref.getBoolean(MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY, false)) {
            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(myConstraints).build()
            val storeProductImageWorker =
                OneTimeWorkRequestBuilder<StoreProductImageWorker>().setConstraints(myConstraints).build()
            val storeVariantImageWorker =
                OneTimeWorkRequestBuilder<StoreVariantImageWorker>().setConstraints(myConstraints)
                    .addTag(STORE_VARIANT_IMAGE_WORKER_TAG).build()

            WorkManager.getInstance().beginUniqueWork(ONE_TIME_WORK, ExistingWorkPolicy.KEEP, syncWorkRequest)
                .then(storeProductImageWorker)
                .then(storeVariantImageWorker)
                .enqueue()

            WorkManager.getInstance().getWorkInfoByIdLiveData(storeVariantImageWorker.id)
                .observe(this@PosMainActivity, Observer { workInfo ->
                    if (workInfo?.state!!.isFinished && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    }

                })
        } else {
            Timber.e("No need to sync master")
        }


        cvInventory.setOnClickListener {
            startActivity(Intent(this@PosMainActivity, InventoryHomeActivity::class.java))
        }
        btShowInventory.setOnClickListener {
            startActivity(Intent(this@PosMainActivity, LocalInventoryActivity::class.java))
        }
        ivAddCustomer.setOnClickListener {
            lvAddCus.visibility = View.VISIBLE
            ed_cus_mbl.requestFocus();
            ed_cus_mbl.setFocusableInTouchMode(true);
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(ed_cus_mbl, InputMethodManager.SHOW_IMPLICIT)
        }
        btn_add_customer.setOnClickListener {
            addNewCustomer()
        }
        btn_cancel.setOnClickListener {
            lvAddCus.visibility = View.GONE

        }
        ivClose.setOnClickListener {
            lvUserDetails.visibility = View.GONE
            svSearch.visibility = View.VISIBLE
        }

        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(param: String?): Boolean {
                if (param != null && param != "")
                    searchCustomer(param)
                return true
            }
        })

    }

    private fun addNewCustomer() {

        if (ed_cus_mbl.text.toString().trim() == "" || ed_cus_mbl.text.toString().trim().length < 9) {
            ed_cus_mbl.error = resources.getString(R.string.err_phone)
            ed_cus_mbl.requestFocus()
            return
        }
        if (ed_cus_name.text.toString().trim() == "") {
            ed_cus_name.error = resources.getString(R.string.err_not_empty)
            ed_cus_name.requestFocus()
            return
        }
        if (ed_cus_name.text.toString().trim() == "") {
            ed_cus_name.error = resources.getString(R.string.err_not_empty)
            ed_cus_name.requestFocus()
            return
        }

        val customer = LocalCustomer()
        customer.phone = ed_cus_mbl.text.toString().toLong()
        customer.alternativePhone = ed_alt_cus_mbl.text.toString()
        customer.gstin = ed_cus_gstin.text.toString()
        customer.gstin = ed_cus_gstin.text.toString()
        customer.name = ed_cus_name.text.toString()
        customer.address = ed_cus_address.text.toString()
        customer.isSynced = false
        customer.updatedAt = System.currentTimeMillis().toString()

        localCustomerRepository.insertLocalCustomer(customer)
        Utils.showMsg(this, "Customer added")
        lvAddCus.visibility = View.GONE

        Utils.hideSoftKeyboard(this)
        tvPerson.setText(customer.name)
        lvUserDetails.visibility = View.VISIBLE
        svSearch.visibility = View.GONE


    }

    private fun searchCustomer(param: String) {
        var listOfCustomer = ArrayList<LocalCustomer>()
        localCustomerRepository.searchLocalCustomers(param).observe(this,
            Observer<List<LocalCustomer>> { localCustomerList ->
                listOfCustomer = localCustomerList as ArrayList

                if (listOfCustomer.size > 0) {
                    val locationAdapter = CustomerAdapter(this@PosMainActivity, listOfCustomer)
                    setupPopupWindow(listOfCustomer)
                } else {
                    Utils.showMsg(this, "No result found")
                }
            })


    }

    private fun setupPopupWindow(listOfCustomer: ArrayList<LocalCustomer>) {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.spinner_list, null)
        val popupWindow =
            PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAsDropDown(svSearch, 0, 0)
        val locationAdapter = CustomerAdapter(this@PosMainActivity, listOfCustomer)
        val listView = layout.findViewById(R.id.lvMenu) as ListView
        listView.adapter = locationAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            tvPerson.setText(listOfCustomer[position].name)
            lvUserDetails.visibility = View.VISIBLE
            svSearch.visibility = View.GONE
            popupWindow.dismiss()
        }

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key.equals(getString(R.string.pref_app_theme_color_key))) {
            setAppTheme(sharedPreferences)
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

}
