package com.goshoppi.pos.view.weighted

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_weighted_products.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class WeightedProductsActivity :
    BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope {
    private lateinit var mJob: Job

    private lateinit var sharedPref: SharedPreferences
    var list: ArrayList<LocalVariant> = ArrayList()
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main
    var variant = LocalVariant()
    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_weighted_products

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
        setUpVariantRecyclerView()

        checkbox_catalog_product_variant_out_of_stock.setOnCheckedChangeListener { _, isChecked -> variant.offer_product = if (isChecked) "1" else "0" }
        checkbox_catalog_product_variant_unlimited_stock.setOnCheckedChangeListener { _, isChecked -> variant.outOfStock = if (isChecked) "1" else "0" }
        checkbox_catalog_product_variant_offer_product.setOnCheckedChangeListener { _, isChecked -> variant.unlimitedStock = if (isChecked) "1" else "0" }

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

    @SuppressLint("SetTextI18n")
    private fun setUpVariantRecyclerView() {

        rvVariants.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(this@WeightedProductsActivity, 2)

        rvVariants.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val productItemTitle = mainView.findViewById<TextView>(R.id.product_item_title)
                val productItemNewPrice = mainView.findViewById<TextView>(R.id.product_item_new_price)
                val productItemOldPrice = mainView.findViewById<TextView>(R.id.product_item_old_price)
                val productItemIcon = mainView.findViewById<ImageView>(R.id.product_item_icon)
                val btnDlt = mainView.findViewById<ImageView>(R.id.btnDlt)
                val btnEdt = mainView.findViewById<ImageView>(R.id.btnEdt)

                btnDlt.visibility = View.GONE
                btnEdt.visibility = View.GONE
                productItemTitle.text = "Varaint Id: ${itemData.storeRangeId}"
                productItemOldPrice.text = itemData.productMrp
                productItemNewPrice.text = itemData.offerPrice

                val file = Utils.getVaraintImage(itemData.productId, itemData.storeRangeId)

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

        ivAddVariant.setOnClickListener {
            addVariant()
        }
    }

    private fun addVariant() {


        if (edOfferPrice.text.toString().trim().isEmpty()) {
            edOfferPrice.requestFocus()
            edOfferPrice.error = "This field can not be empty"
            return
        }

        if (edUnitName.text.toString().trim().isEmpty()) {
            edUnitName.requestFocus()
            edUnitName.error = "This field can not be empty"
            return
        }

        if (edStockBalance.text.toString().trim().isEmpty()) {
            edStockBalance.requestFocus()
            edStockBalance.error = "This field can not be empty"
            return
        }

        variant.offerPrice = edOfferPrice.text.toString()
        variant.unitName = edUnitName.text.toString()
        variant.stockBalance = edStockBalance.text.toString()
//        tmp.storeRangeId
//        tmp.productMrp
//        tmp.rangeName
//        tmp.rangeId
//        tmp.discount
        list.add(variant)
        variant=LocalVariant()
        rvVariants.adapter?.notifyDataSetChanged()

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
}
