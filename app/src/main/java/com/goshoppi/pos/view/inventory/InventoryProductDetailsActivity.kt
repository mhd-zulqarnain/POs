package com.goshoppi.pos.view.inventory

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
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

    private lateinit var sharedPref: SharedPreferences
    private lateinit var product: MasterProduct
    @Inject
    lateinit var masterVariantRepository: MasterVariantRepository
    @Inject
    lateinit var localVariantRepository: LocalVariantRepository
    @Inject
    lateinit var localProductRepository: LocalProductRepository
    private lateinit var variantList: ArrayList<MasterVariant>
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .roomModule(RoomModule(application))
            .build()
            .injectInventoryProductDetailsActivity(this)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
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
            if (product.variants != null && product.variants.isNotEmpty()) {
                setImage(Utils.getProductImage(product.variants[0].productId, "1"), iv_prd_img)
            }
        }

        masterVariantRepository.getMasterVariantsByProductId(product.storeProductId).observe(this,
            Observer<List<MasterVariant>> { localVariantList ->
                variantList = localVariantList as ArrayList
                setUpRecyclerView(variantList)
                rc_product_details_variants.adapter?.notifyDataSetChanged()
            })


        btn_add.setOnClickListener {

            /*saving product to local database*/
            val mjson = Gson().toJson(product)
            val product: LocalProduct = Gson().fromJson(mjson, LocalProduct::class.java)
            localProductRepository.insertLocalProduct(product)


            /*saving variants to local database*/
            variantList.forEach {
                val json = Gson().toJson(it)
                val variant: LocalVariant = Gson().fromJson(json, LocalVariant::class.java)
                localVariantRepository.insertLocalVariant(variant)
            }


            Utils.showAlert("Product Added", "Added to local Database", this)
        }

        btn_update_variant.setOnClickListener {
            if (variantList.isNotEmpty()) {
                val editedMasterVariantObj: MasterVariant = variantList[position]

                if (et_account.text.toString().trim().isEmpty()) {
                    et_account.requestFocus()
                    et_account.error = "This field can not be empty"
                    return@setOnClickListener
                }

                if (et_discount.text.toString().trim().isEmpty()) {
                    et_discount.requestFocus()
                    et_discount.error = "This field can not be empty"
                    return@setOnClickListener
                }

                if (et_stock.text.toString().trim().isEmpty()) {
                    et_stock.requestFocus()
                    et_stock.error = "This field can not be empty"
                    return@setOnClickListener
                }

                if (et_order_limit.text.toString().trim().isEmpty()) {
                    et_order_limit.requestFocus()
                    et_order_limit.error = "This field can not be empty"
                    return@setOnClickListener
                }

                editedMasterVariantObj.offerPrice = et_account.text.toString()
                editedMasterVariantObj.discount = et_discount.text.toString()
                editedMasterVariantObj.stockBalance = et_stock.text.toString()
                editedMasterVariantObj.purchaseLimit = et_order_limit.text.toString()

                variantList.removeAt(position)
                variantList.add(position, editedMasterVariantObj)
                rc_product_details_variants.adapter?.notifyDataSetChanged()

            } else {
                Timber.e("Variant List is Empty")
            }
        }
    }

    private fun setEditData(variantObj: MasterVariant) {
        tv_varaint_title.text = "Variant:${variantObj.storeRangeId}"
        et_account.setText(variantObj.offerPrice)
        et_discount.setText(variantObj.discount)
        et_stock.setText(variantObj.stockBalance)
        et_order_limit.setText(variantObj.purchaseLimit)
    }

    private fun setUpRecyclerView(variantList: ArrayList<MasterVariant>) {
        val itemViewList: ArrayList<View>  = arrayListOf()

        if (variantList.isNotEmpty()) {
            setEditData(variantList[0])
            position = 0
        } else {
            Timber.e("Variant List is Empty")
        }

        rc_product_details_variants.layoutManager = LinearLayoutManager(this@InventoryProductDetailsActivity)
        rc_product_details_variants.adapter =
            RecyclerViewGeneralAdapter(variantList, R.layout.inventory_product_details_variants_item_view)
            { itemData, viewHolder ->

                itemViewList.add(viewHolder.itemView)
                itemViewList[position].setBackgroundResource(R.color.text_light_gry)

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

                    setEditData(itemData)
                    position = viewHolder.adapterPosition

                    itemViewList.forEach {
                        if(itemViewList[viewHolder.adapterPosition] == it) {
                            it.setBackgroundResource(R.color.text_light_gry)
                        }
                        else{
                            it.setBackgroundResource(R.color.white)
                        }
                    }

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
