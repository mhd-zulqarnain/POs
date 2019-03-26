package com.goshoppi.pos.view.inventory

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.goshoppi.pos.R
import com.goshoppi.pos.model.master.MasterVariant
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_inventoryproduct_details.*
import timber.log.Timber
import java.io.File
import com.google.gson.Gson
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.architecture.repository.masterVariantRepo.MasterVariantRepository
import com.goshoppi.pos.di.component.DaggerAppComponent
import com.goshoppi.pos.di.module.AppModule
import com.goshoppi.pos.di.module.RoomModule
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.model.master.MasterProduct
import javax.inject.Inject


@SuppressLint("SetTextI18n")
class InventoryProductDetailsActivity : AppCompatActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var currentTheme: Boolean = false
    private lateinit var sharedPref: SharedPreferences
    private lateinit var product: MasterProduct
    @Inject
    lateinit var masterVariantRepository: MasterVariantRepository
    @Inject
    lateinit var localVariantRepository: LocalVariantRepository
    @Inject
    lateinit var localProductRepository: LocalProductRepository
    private lateinit var variantList: ArrayList<MasterVariant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .roomModule(RoomModule(application))
            .build()
            .injectInventoryProductDetailsActivity(this)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        currentTheme = sharedPref.getBoolean(getString(R.string.pref_theme_key), false)
        setAppTheme(currentTheme)
        setContentView(R.layout.activity_inventoryproduct_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        variantList = arrayListOf()
        initView()
    }

    private fun initView() {
        val obj = intent.getStringExtra(Constants.PRODUCT_OBJECT_INTENT)
        if (obj != null) {
            product = Gson().fromJson<MasterProduct>(obj, MasterProduct::class.java)
            prd_name.text = product.productName
        }

        masterVariantRepository.getMasterVariantsByProductId(product.storeProductId).observe(this,
            Observer<List<MasterVariant>> { localVariantList ->
                variantList = localVariantList as ArrayList
                setUpRecyclerView(variantList)
                rc_product_details_variants.adapter?.notifyDataSetChanged()
            })


        btn_add.setOnClickListener {
            /*saving variants to local database*/
            variantList.forEach {
                val json = Gson().toJson(it)
                val variant: LocalVariant = Gson().fromJson(json, LocalVariant::class.java)
                localVariantRepository.insertLocalVariant(variant)
            }

            /*saving product to local database*/
            val json = Gson().toJson(product)
            val product: LocalProduct = Gson().fromJson(json, LocalProduct::class.java)
            localProductRepository.insertLocalProduct(product)

            Utils.showAlert("Product Added", "Added to local Database", this)
        }
    }

    private fun setUpRecyclerView(variantList: ArrayList<MasterVariant>) {

        val initVariant = variantList[0]
        setImage(Utils.getProductImage(initVariant.productId, "1"), iv_prd_img)
        tv_varaint_title.text = "Variant:${initVariant.storeRangeId}"
        et_account.setText(initVariant.offerPrice)
        et_stock.setText(initVariant.stockBalance)
        et_order_limit.setText(initVariant.purchaseLimit)

        rc_product_details_variants.layoutManager = LinearLayoutManager(this@InventoryProductDetailsActivity)
        rc_product_details_variants.adapter =
            RecyclerViewGeneralAdapter(variantList, R.layout.inventory_product_details_variants_item_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val variantImage = mainView.findViewById<ImageView>(R.id.img_variant_product_details)
                val variantBarcode = mainView.findViewById<TextView>(R.id.tv_variant_barcode)
                val variantPrice = mainView.findViewById<TextView>(R.id.tv_variant_price)
                val variantStock = mainView.findViewById<TextView>(R.id.tv_variant_stock)
                val tvVariantId = mainView.findViewById<TextView>(R.id.tv_variant_id)
                val ivEditIcon = mainView.findViewById<ImageView>(R.id.iv_edit_icon)

                variantBarcode.text = "Bar Code :${itemData.barCode}"
                variantPrice.text = "Price :${itemData.offerPrice}"
                variantStock.text = "Stock :${itemData.stockBalance}"
                tvVariantId.text = "Id :${itemData.storeRangeId}"

                ivEditIcon.setOnClickListener {
                    tv_varaint_title.text = "Variant:${itemData.storeRangeId}"
                    et_account.setText(itemData.offerPrice)
                    et_discount.setText("")
                    et_stock.setText(itemData.stockBalance)
                    et_order_limit.setText(itemData.purchaseLimit)

                }
                val file = Utils.getVaraintImage(itemData.productId, itemData.storeRangeId)
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

    private fun setImage(img: File, rsc: ImageView) {
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
                this@InventoryProductDetailsActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
