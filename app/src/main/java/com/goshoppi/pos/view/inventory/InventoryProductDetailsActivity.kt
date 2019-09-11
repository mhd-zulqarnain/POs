package com.goshoppi.pos.view.inventory

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.goshoppi.pos.R
import com.goshoppi.pos.model.master.MasterVariant
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.io.File
import com.google.gson.Gson
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.view.inventory.viewmodel.InvProdDetailViewModel
import kotlinx.android.synthetic.main.activity_inventoryproduct_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@SuppressLint("SetTextI18n")
class InventoryProductDetailsActivity : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener ,CoroutineScope {

    lateinit var mJob:Job
    override val coroutineContext: CoroutineContext
        get() = mJob +Dispatchers.Main

    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_inventoryproduct_details
    }
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var  localProductRepository: LocalProductRepository
    private lateinit var sharedPref: SharedPreferences
    private lateinit var product: MasterProduct
    private lateinit var variantList: ArrayList<MasterVariant>
    private var position = 0
    lateinit var invProdDetailViewModel: InvProdDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        mJob = Job()
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        variantList = arrayListOf()
        initView()
        initViewModel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            toolbar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_toolbar_color) )
        } else{
            toolbar.setBackgroundResource(R.color.colorPrimaryLight)
        }
    }

    private fun initViewModel() {
        invProdDetailViewModel = ViewModelProviders.of(this@InventoryProductDetailsActivity, viewModelFactory).get(
            InvProdDetailViewModel::class.java
        )
        invProdDetailViewModel.getMasterVariantListByID(product.storeProductId)

        invProdDetailViewModel.masterVariantLiveDataList.observe(
            this@InventoryProductDetailsActivity,
            Observer { localVariantList ->
                variantList = localVariantList as ArrayList
                setUpRecyclerView(variantList)
                rc_product_details_variants.adapter?.notifyDataSetChanged()
            })
    }

    private fun initView() {
        val obj = intent.getStringExtra(Constants.PRODUCT_OBJECT_INTENT)
        if (obj != null) {
            product = Gson().fromJson<MasterProduct>(obj, MasterProduct::class.java)
            prd_name.text = product.productName
            if (product.variants != null && product.variants.isNotEmpty()) {
                setImage(Utils.getProductImage(product.variants[0].productId!!, "1"), iv_prd_img)
            }
            launch {
               val isProductExists=  localProductRepository.isProductExist(product.storeProductId)
                if(isProductExists!=null){
                    Utils.showMsg(this@InventoryProductDetailsActivity,"Product already added in local database ")
                finish()
                }
            }
        }

        btn_add.setOnClickListener {
            invProdDetailViewModel.insertLocalProductAndItsVariants(product, variantList)
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
                editedMasterVariantObj.barCode = et_bar_code.text.toString()

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
        et_bar_code.setText(variantObj.barCode)
    }

    private fun setUpRecyclerView(variantList: ArrayList<MasterVariant>) {
        val itemViewList: ArrayList<View> = arrayListOf()

        if (variantList.isNotEmpty()) {
            setEditData(variantList[0])
            position = 0
        } else {
            Timber.e("Variant List is Empty")
        }

        rc_product_details_variants.layoutManager =
            LinearLayoutManager(this@InventoryProductDetailsActivity)
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
                val ivDeleteIcon = mainView.findViewById<ImageView>(R.id.iv_delete_icon)

                variantBarcode.text = "Bar Code :${itemData.barCode}"
                variantPrice.text = "Price :${itemData.offerPrice}"
                variantStock.text = "Stock :${itemData.stockBalance}"
                tvVariantId.text = "Id :${itemData.storeRangeId}"

                viewHolder.itemView.setOnClickListener {
                    setEditData(itemData)
                    position = viewHolder.adapterPosition

                    itemViewList.forEach {
                        if (itemViewList[viewHolder.adapterPosition] == it) {
                            it.setBackgroundResource(R.color.text_light_gry)
                        } else {
                            it.setBackgroundResource(R.color.white)
                        }
                    }
                }

                ivDeleteIcon.setOnClickListener {
                    if (variantList.size != 1) {
                        variantList.removeAt(viewHolder.adapterPosition)
                        setEditData(variantList[viewHolder.adapterPosition])
                        rc_product_details_variants.adapter?.notifyDataSetChanged()
                    } else {
                        Utils.showMsgShortIntervel(this@InventoryProductDetailsActivity, "You can't delete all Variant")
                    }
                }

                val file = Utils.getVaraintImage(itemData.productId!!, itemData.storeRangeId!!)

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
        when (item.itemId) {
            android.R.id.home -> {
                this@InventoryProductDetailsActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        mJob.cancel()
    }
}
