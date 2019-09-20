package com.goshoppi.pos.ui.weighted

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.UiHelper
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.ui.weighted.viewmodel.WeightedProductViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_weighted_products.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class WeightedProductsActivity :
    BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope {
    private lateinit var mJob: Job

    private lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var localProductRepository: LocalProductRepository
    @Inject
    lateinit var localVariantRepository: LocalVariantRepository
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var weightedProductViewModel: WeightedProductViewModel
    var categoryId: Long = -1
    var subCategoryId: Long = -1
    var categoryName: String = ""
    var subCategoryName: String = ""

    var list: ArrayList<LocalVariant> = ArrayList()
    var unitName = "Kilogram"
    var productId = System.currentTimeMillis()
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

        weightedProductViewModel = ViewModelProviders.of(this@WeightedProductsActivity,viewModelFactory)
            .get(WeightedProductViewModel::class.java)

        setUpVariantRecyclerView()
        updateView()
        checkbox_catalog_product_variant_out_of_stock.setOnCheckedChangeListener { _, isChecked ->
            variant.offer_product = if (isChecked) "1" else "0"
        }
        checkbox_catalog_product_variant_unlimited_stock.setOnCheckedChangeListener { _, isChecked ->
            variant.outOfStock = if (isChecked) "1" else "0"
        }
        checkbox_catalog_product_variant_offer_product.setOnCheckedChangeListener { _, isChecked ->
            variant.unlimitedStock = if (isChecked) "1" else "0"
        }
        val units = arrayListOf<String>("Kilogram", "gram")

        launch {
            val categories = localProductRepository.loadStoreCategoryMain()
            val lst = ArrayList<String>()
            for (item in categories)
                lst.add(item.categoryName!!)
            UiHelper.setupFloatingSpinner(
                edCategory, lst, "",
                { selectedItem, selectedIndex ->
                    val item = selectedItem as String
                    categories.forEach { obj ->
                        if (obj.categoryName == item) {
                            categoryId = obj.categoryId!!
                            categoryName = item
                            subCategoryId = -1
                            loadSubCategory(obj.categoryId!!)
                            return@forEach
                        }
                    }
                }, this@WeightedProductsActivity
            )
            if (categories.size != 0)
                loadSubCategory(categories[0].categoryId!!)
        }
        UiHelper.setupFloatingSpinner(
            edUnitSpinner, units, units[0],
            { selectedItem, selectedIndex ->
                unitName = selectedItem
            }, this@WeightedProductsActivity
        )

        btnSave.setOnClickListener {
            saveProduct()

        }
    }

    private fun loadSubCategory(cid: Long) {

        launch {
            val subCategories = localProductRepository.loadSubCategoryByCategoryId(cid)
            val lstSub = ArrayList<String>()
            if (subCategories.size == 1)
                subCategoryId = subCategories[0].subcategoryId!!
            for (item in subCategories)
                lstSub.add(item.subcategoryName!!)
            UiHelper.setupFloatingSpinner(
                edSubCategory, lstSub, "",
                { selectedItem, selectedIndex ->
                    val item = selectedItem as String
                    subCategories.forEach { obj ->
                        if (obj.subcategoryName == item) {
                            subCategoryId = obj.subcategoryId!!
                            subCategoryName = item
                            return@forEach
                        }
                    }
                }, this@WeightedProductsActivity
            )
        }

    }

    private fun saveProduct() {

        if (tvPrdName.text.toString().trim().isEmpty()) {
            tvPrdName.requestFocus()
            tvPrdName.error = "This field can not be empty"
            return
        }

        if (tvPrdDes.text.toString().trim().isEmpty()) {
            tvPrdDes.requestFocus()
            tvPrdDes.error = "This field can not be empty"
            return
        }
        if (categoryId == -1L) {
            edCategory.requestFocus()
            edCategory.error = "Please select category"
            return
        }
        if (subCategoryId == -1L) {
            edSubCategory.requestFocus()
            edSubCategory.error = "Please select sub category"
            return
        }

        val prd = LocalProduct()
        prd.storeProductId = productId
        prd.productName = tvPrdName.text.toString()
        prd.smallDescription = tvPrdDes.text.toString()
        prd.unitName = unitName
        prd.type = Constants.WEIGHTED_PRODUCT
        prd.categoryId = categoryId.toString()
        prd.categoryName = categoryName
        prd.subcategoryId = subCategoryId.toString()
        prd.subcategoryName = subCategoryName

        launch {
            if (list.size == 0) {
                Utils.showMsg(this@WeightedProductsActivity, "Please add variant for the product")
            } else {
                localProductRepository.insertLocalProduct(prd)
                localVariantRepository.insertLocalVariants(list)
                Utils.showMsg(this@WeightedProductsActivity, "Product Added to local Database")
                this@WeightedProductsActivity.finish()
            }
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
                val product_item_weight_price = mainView.findViewById<TextView>(R.id.product_item_weight_price)
                val productItemIcon = mainView.findViewById<ImageView>(R.id.product_item_icon)
                val btnDlt = mainView.findViewById<ImageView>(R.id.btnDlt)
                val btnEdt = mainView.findViewById<ImageView>(R.id.btnEdt)

                btnDlt.visibility = View.GONE
                btnEdt.visibility = View.GONE

                productItemTitle.text = "Varaint Id: ${itemData.storeRangeId}"
                productItemOldPrice.text = "Price: ${itemData.offerPrice}"
                productItemNewPrice.text = "Stock: ${itemData.stockBalance}"
                product_item_weight_price.text = "(${itemData.unitName})"
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
            Utils.hideSoftKeyboard(this@WeightedProductsActivity)
        }
    }

    private fun addVariant() {
        if (edOfferPrice.text.toString().trim().isEmpty()) {
            edOfferPrice.requestFocus()
            edOfferPrice.error = "This field can not be empty"
            return
        }

        if (edStockBalance.text.toString().trim().isEmpty()) {
            edStockBalance.requestFocus()
            edStockBalance.error = "This field can not be empty"
            return
        }

        variant.offerPrice = edOfferPrice.text.toString()
        variant.storeRangeId = System.currentTimeMillis()
        variant.productId = productId
        variant.unitName = unitName
        variant.type = Constants.WEIGHTED_PRODUCT
        variant.productName = tvPrdName.text.toString()

        variant.stockBalance = edStockBalance.text.toString()
//        tmp.storeRangeId
//        tmp.productMrp
//        tmp.rangeName
//        tmp.rangeId
//        tmp.discount
        list.add(variant)
        updateView()
        variant = LocalVariant()
        edOfferPrice.setText("")
        edStockBalance.setText("")
        unitName = "Kilogram"
        checkbox_catalog_product_variant_out_of_stock.isChecked = false
        checkbox_catalog_product_variant_unlimited_stock.isChecked = false
        checkbox_catalog_product_variant_offer_product.isChecked = false
        rvVariants.adapter?.notifyDataSetChanged()

    }

    private fun updateView() {
        if (list.size == 0) {
            noData.visibility = View.VISIBLE
            rvVariants.visibility = View.GONE
        } else {
            noData.visibility = View.GONE
            rvVariants.visibility = View.VISIBLE
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
}
