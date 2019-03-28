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
import android.view.MenuItem
import android.view.View
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.architecture.repository.masterVariantRepo.MasterVariantRepository
import com.goshoppi.pos.di.component.DaggerAppComponent
import com.goshoppi.pos.di.module.AppModule
import com.goshoppi.pos.di.module.RoomModule
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.model.master.MasterVariant
import com.goshoppi.pos.utils.Constants.PRODUCT_OBJECT_INTENT
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.view.inventory.adapter.ProductAdapter
import kotlinx.android.synthetic.main.activity_inventroy_home.*
import javax.inject.Inject

class InventoryHomeActivity : AppCompatActivity(), View.OnClickListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var adapter: ProductAdapter? = null
    private lateinit var gridLayoutManager: GridLayoutManager
    private var productsList: ArrayList<MasterProduct> = ArrayList()
    private lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var masterProductRepository: MasterProductRepository
    @Inject
    lateinit var localVariantRepository: LocalVariantRepository
    @Inject
    lateinit var localProductRepository: LocalProductRepository
    @Inject
    lateinit var masterVariantRepository: MasterVariantRepository

    private lateinit var variantList: ArrayList<MasterVariant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .roomModule(RoomModule(application))
            .build()
            .injectInventoryHomeActivity(this)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        sharedPref.registerOnSharedPreferenceChangeListener(this)

        setContentView(R.layout.activity_inventroy_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        initializeUi()
    }

    private fun initializeUi() {

        adapter = ProductAdapter(this@InventoryHomeActivity) { it, isOption ->
            if (!isOption) {
                val intent = Intent(this@InventoryHomeActivity, InventoryProductDetailsActivity::class.java)
                val obj = Gson().toJson(it)
                intent.putExtra(PRODUCT_OBJECT_INTENT, obj)
                startActivity(intent)
            }else{
                addtoLocaldb(it)
            }
        }
        rvProduct.adapter = adapter
        variantList =ArrayList()
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

    private fun addtoLocaldb(it: MasterProduct) {

        val mjson = Gson().toJson(it)
        val product: LocalProduct = Gson().fromJson(mjson, LocalProduct::class.java)
        localProductRepository.insertLocalProduct(product)
        variantList=  masterVariantRepository.getMasterStaticVariantsOfProducts(product.storeProductId) as ArrayList;

        /*saving variants to local database*/
        variantList.forEach {
            val json = Gson().toJson(it)
            val variant: LocalVariant = Gson().fromJson(json, LocalVariant::class.java)
            localVariantRepository.insertLocalVariant(variant)
        }

        Utils.showAlert("Product Added", "Added to local Database", this)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                this@InventoryHomeActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}