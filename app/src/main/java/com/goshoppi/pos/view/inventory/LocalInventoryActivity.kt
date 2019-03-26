package com.goshoppi.pos.view.inventory

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.di.component.DaggerAppComponent
import com.goshoppi.pos.di.module.AppModule
import com.goshoppi.pos.di.module.RoomModule
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_local_inventory.*
import timber.log.Timber
import javax.inject.Inject

class LocalInventoryActivity : AppCompatActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var currentTheme: Boolean = false
    private lateinit var sharedPref: SharedPreferences
    private var productList: ArrayList<LocalProduct> = ArrayList()
    private var variantList: ArrayList<LocalVariant> = ArrayList()

    @Inject
    lateinit var localProductRepository: LocalProductRepository

    @Inject
    lateinit var localVariantRepository: LocalVariantRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .roomModule(RoomModule(application))
            .build()
            .injectLocalInventoryActivity(this)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        currentTheme = sharedPref.getBoolean(getString(R.string.pref_theme_key), false)
        setAppTheme(currentTheme)
        setContentView(R.layout.activity_local_inventory)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        initView()
    }

    private fun initView() {
        localProductRepository.loadAllLocalProduct().observe(this,
            Observer<List<LocalProduct>> { localProductsList ->
                productList = localProductsList as ArrayList
                setUpPrdouctRecyclerView(productList)
                if (productList.size != 0) {
                    getShowVaraint(productList[0].storeProductId)
                    tv_varaint_prd_name.text = productList[0].productName
                }
                rc_product_details_variants.adapter?.notifyDataSetChanged()
            })

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
        variantList = ArrayList()
    }

    private fun searchProduct(param: String) {
        if (!productList.isEmpty()) {
            productList.clear()
        }

        localProductRepository.searchLocalProducts(param).observe(this,
            Observer<List<LocalProduct>> { localProductList ->
                productList = localProductList as ArrayList

                if (productList.size > 0) {
                    setUpPrdouctRecyclerView(productList)
                    rc_product_details_variants.adapter?.notifyDataSetChanged()
                } else {
                    Utils.showMsg(this, "No result found")
                }
            })
    }

    private fun setUpPrdouctRecyclerView(list: ArrayList<LocalProduct>) {

        rc_product_details_variants.layoutManager = LinearLayoutManager(this@LocalInventoryActivity)

        rc_product_details_variants.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val productItemTitle = mainView.findViewById<TextView>(R.id.product_item_title)
                val productItemNewPrice = mainView.findViewById<TextView>(R.id.product_item_new_price)
                val productItemOldPrice = mainView.findViewById<TextView>(R.id.product_item_old_price)
                val productItemIcon = mainView.findViewById<ImageView>(R.id.product_item_icon)

                productItemTitle.text = itemData.productName
                productItemOldPrice.text = itemData.productMrp
                productItemNewPrice.text = itemData.offerPrice

                val file = Utils.getProductImage(itemData.storeProductId, "1")
                Timber.e("File is there ? ${file.exists()}")

                mainView.setOnClickListener {
                    getShowVaraint(itemData.storeProductId)
                    tv_varaint_prd_name.text = itemData.productName
                }
                if (file.exists()) {
                    Picasso.get()
                        .load(file)
                        .error(R.drawable.no_image)
                        .into(productItemIcon)

                } else {
                    Picasso.get()
                        .load(R.drawable.no_image)
                        .into(productItemIcon)
                }
            }
    }

    private fun getShowVaraint(productId: String) {
        localVariantRepository.getLocalVariantsByProductId(productId).observe(this,
            Observer<List<LocalVariant>> { localVariantList ->
                variantList = localVariantList as ArrayList
                setUpVaraintRecyclerView(variantList)
                rc_product_details_variants.adapter?.notifyDataSetChanged()
            })
    }

    @SuppressLint("SetTextI18n")
    private fun setUpVaraintRecyclerView(list: ArrayList<LocalVariant>) {

        rv_products_variants.layoutManager = GridLayoutManager(this@LocalInventoryActivity, 2)

        rv_products_variants.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val productItemTitle = mainView.findViewById<TextView>(R.id.product_item_title)
                val productItemNewPrice = mainView.findViewById<TextView>(R.id.product_item_new_price)
                val productItemOldPrice = mainView.findViewById<TextView>(R.id.product_item_old_price)
                val productItemIcon = mainView.findViewById<ImageView>(R.id.product_item_icon)

                productItemTitle.text = "Varaint Id: ${itemData.rangeId}"
                productItemOldPrice.text = itemData.productMrp
                productItemNewPrice.text = itemData.offerPrice

                val file = Utils.getVaraintImage(itemData.productId, itemData.storeRangeId)
                Timber.e("File is there ? ${file.exists()}")

                if (file.exists()) {
                    Picasso.get()
                        .load(file)
                        .error(R.drawable.no_image)
                        .into(productItemIcon)

                } else {
                    Picasso.get()
                        .load(R.drawable.no_image)
                        .into(productItemIcon)
                }
            }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                this@LocalInventoryActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}