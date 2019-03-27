package com.goshoppi.pos.view.inventory

import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.View
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.di.component.DaggerAppComponent
import com.goshoppi.pos.di.module.AppModule
import com.goshoppi.pos.di.module.RoomModule
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.utils.Constants.PRODUCT_OBJECT_INTENT
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.view.inventory.adapter.ProductAdapter
import kotlinx.android.synthetic.main.activity_inventroy_home.*
import timber.log.Timber
import javax.inject.Inject

class InventoryHomeActivity : AppCompatActivity(), View.OnClickListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var adapter: ProductAdapter? = null
    private lateinit var gridLayoutManager: GridLayoutManager
    private var productsList: ArrayList<MasterProduct> = ArrayList()
    private lateinit var sharedPref: SharedPreferences
    private var currentTheme: Boolean = false
    @Inject
    lateinit var masterProductRepository: MasterProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .roomModule(RoomModule(application))
            .build()
            .injectInventoryHomeActivity(this)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        currentTheme = sharedPref.getBoolean(getString(R.string.pref_theme_key), false)
        setAppTheme(currentTheme)

        setContentView(R.layout.activity_inventroy_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        initializeUi()
    }

    private fun initializeUi() {

        adapter = ProductAdapter(this@InventoryHomeActivity) {
            Timber.e("OnClick")
            val intent = Intent(this@InventoryHomeActivity, InventoryProductDetailsActivity::class.java)
            val obj = Gson().toJson(it)
            intent.putExtra(PRODUCT_OBJECT_INTENT, obj)
            startActivity(intent)
        }
        rvProduct.adapter = adapter

        gridLayoutManager = GridLayoutManager(this, 4)
        rvProduct.layoutManager = gridLayoutManager
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(param: String?): Boolean {
                if (param != null && param != "")
                    searchProduct(param)
                return true
            }
        })

        searchProduct("");
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val selectedTheme = sharedPref.getBoolean(getString(R.string.pref_theme_key), false)
        setAppTheme(selectedTheme)
        recreate()
    }

    private fun setAppTheme(currentTheme: Boolean) {
        when (currentTheme) {
            true -> setTheme(R.style.Theme_App_Green)
            else -> setTheme(R.style.Theme_App)
        }
    }

    private fun searchProduct(param: String) {
        if (!productsList.isEmpty()) {
            productsList.clear()
        }

        masterProductRepository.searchMasterProducts(param).observe(this,
            Observer<List<MasterProduct>> { masterProductList ->
                productsList = masterProductList as ArrayList
                if (productsList.size > 0) {
                    rvProduct.visibility = View.VISIBLE
                    rlMainSearch.visibility = View.INVISIBLE

                } else {
                    rvProduct.visibility = View.INVISIBLE
                    rlMainSearch.visibility = View.VISIBLE
                    Utils.showMsg(this, "No result found")
                }
                adapter!!.setProductList(productsList)
                adapter!!.notifyDataSetChanged()
            })
    }

    override fun onClick(v: View?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

}