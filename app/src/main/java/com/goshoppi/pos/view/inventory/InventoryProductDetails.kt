package com.goshoppi.pos.view.inventory

import android.content.SharedPreferences
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.VariantRepository
import com.goshoppi.pos.model.Variant
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_inventoryproduct_details.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.io.File
import android.view.MotionEvent
import android.view.View






class InventoryProductDetails : AppCompatActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener  {

    private var currentTheme: Boolean = false
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        currentTheme = sharedPref.getBoolean(getString(R.string.pref_theme_key), false)
        setAppTheme(currentTheme)
        setContentView(R.layout.activity_inventoryproduct_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        var variantList: ArrayList<Variant>
        doAsync {
            variantList = VariantRepository.getInstance(this@InventoryProductDetails).getVariantsOfProductsById(
                intent.getStringExtra(Constants.PRODUCT_OBJECT_KEY)
            ) as ArrayList<Variant>
            uiThread {
                Timber.e("Size ${variantList.size}")
                setUpRecyclerView(variantList)
                rc_product_details_variants.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun setUpRecyclerView(variantList: ArrayList<Variant>) {

        val initVariant =variantList[0]
        setImage(Utils.getProductImage(initVariant.productId,"1"),iv_prd_img)
        tv_varaint_title.setText("Varaint:${initVariant.storeRangeId}")
        et_account.setText(initVariant.offerPrice)
        et_stock.setText(initVariant.stockBalance)
        et_order_limit.setText(initVariant.purchaseLimit)

        rc_product_details_variants.layoutManager = LinearLayoutManager(this@InventoryProductDetails)
        rc_product_details_variants.adapter =
            RecyclerViewGeneralAdapter(variantList, R.layout.inventory_product_details_variants_item_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val variantImage = mainView.findViewById<ImageView>(R.id.img_variant_product_details)
                val variantBarcode = mainView.findViewById<TextView>(R.id.tv_variant_barcode)
                val variantPrice = mainView.findViewById<TextView>(R.id.tv_variant_price)
                val variantStock = mainView.findViewById<TextView>(R.id.tv_variant_stock)
                val tv_variant_id = mainView.findViewById<TextView>(R.id.tv_variant_id)
                val iv_edit_icon = mainView.findViewById<ImageView>(R.id.iv_edit_icon)

                variantBarcode.text = "Bar Code :${itemData.barCode}"
                variantPrice.text = "Price :${itemData.offerPrice}"
                variantStock.text = "Stock :${itemData.stockBalance}"
                tv_variant_id.text = "Id :${itemData.storeRangeId}"



                iv_edit_icon.setOnClickListener{
                    tv_varaint_title.setText("Varaint:${itemData.storeRangeId}")
                    et_account.setText(itemData.offerPrice)
                    et_discount.setText("")
                    et_stock.setText(itemData.stockBalance)
                    et_order_limit.setText(itemData.purchaseLimit)

                }
                val file = Utils.getVaraintImage(itemData.productId,itemData.storeRangeId)
                Timber.e("File is there ? ${file.exists()}")

                if (file.exists()) {
                    Picasso.get()
                        .load(file)
                        .error(R.drawable.no_image)
                        .into(variantImage)

                } else {
                    Picasso.get()
                        .load(R.drawable.no_image)
                        .into(variantImage)
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

    fun setImage(img:File, rsc: ImageView){
        if (img.exists()) {
            Picasso.get()
                .load(img)
                .error(R.drawable.no_image)
                .into(rsc)

        } else {
            Picasso.get()
                .load(R.drawable.no_image)
                .into(rsc)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                this@InventoryProductDetails.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
