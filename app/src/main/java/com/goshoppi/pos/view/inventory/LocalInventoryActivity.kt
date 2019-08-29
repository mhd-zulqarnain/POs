package com.goshoppi.pos.view.inventory

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Constants.CVS_PRODUCT_FILE
import com.goshoppi.pos.utils.Constants.CVS_VARIANT_FILE
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.view.inventory.viewmodel.LocalInventoryViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_inventoryproduct_details.rc_product_details_variants
import kotlinx.android.synthetic.main.activity_inventroy_home.svSearch
import kotlinx.android.synthetic.main.activity_local_inventory.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

const val PICK_EXCEL_FILE = 12

class LocalInventoryActivity : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener, CoroutineScope {
    lateinit var mJob: Job

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_local_inventory
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var sharedPref: SharedPreferences
    private var productList: ArrayList<LocalProduct> = ArrayList()
    private var variantList: ArrayList<LocalVariant> = ArrayList()
    private lateinit var localInventoryViewModel: LocalInventoryViewModel
    @Inject
    lateinit var localVariantRepository: LocalVariantRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        initView()
        mJob = Job()
        initViewModel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            toolbar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_toolbar_color) )
        } else{
            toolbar.setBackgroundResource(R.color.colorPrimaryLight)
        }
    }

    private fun initViewModel() {
        localInventoryViewModel = ViewModelProviders.of(this@LocalInventoryActivity, viewModelFactory).get(
            LocalInventoryViewModel::class.java
        )

        localInventoryViewModel.localProductLiveDataList.observe(
            this@LocalInventoryActivity,
            Observer { localProductsList ->

                productList = localProductsList as ArrayList
                setUpProductRecyclerView(productList)
                if (productList.size != 0) {
                    getShowVariant(productList[0].storeProductId)
                    tv_varaint_prd_name.text = productList[0].productName
                } else {
                    Utils.showAlert("No Products Found", "Please add products from master table", this)
                }
                rc_product_details_variants.adapter?.notifyDataSetChanged()
            })
    }

    private fun initView() {
        variantList = ArrayList()

        /*localProductRepository.loadAllLocalProduct().observe(this,
            Observer<List<LocalProduct>> { localProductsList ->
                varaintList = localProductsList as ArrayList
                setUpProductRecyclerView(varaintList)
                if (varaintList.size != 0) {
                    getShowVariant(varaintList[0].storeProductId)
                    tv_varaint_prd_name.text = varaintList[0].productName
                } else {
                    Utils.showAlert("No Products Found", "Please add products from master table", this)
                }
                rc_product_details_variants.adapter?.notifyDataSetChanged()
            })*/
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            btnDownload.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_toolbar_color) );
            btnReadExcel.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_toolbar_color) );
        } else{
            btnDownload.setBackgroundResource(R.color.colorPrimaryDark)
            btnReadExcel.setBackgroundResource(R.color.colorPrimaryDark)

        }
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

        btnReadExcel.setOnClickListener {
            openFile()
        }

        btnDownload.setOnClickListener {
            Utils.showLoading(false, this)

            val prodIt = localInventoryViewModel.getAllLocalProduct()
            if (prodIt.isNotEmpty()) {
                val prodList = ArrayList<LocalProduct>()
                prodIt.forEach { prod ->
                    prodList.add(prod)
                }
                generateProductExcel(prodList)
            } else {
                Utils.showMsgShortIntervel(this@LocalInventoryActivity, "No products found to export")
            }

            //val varIt = localVariantRepository.loadAllStaticLocalVariants()

            val varIt = localInventoryViewModel.getAllLocalProductVariants()
            if (varIt.isNotEmpty()) {
                val variants = ArrayList<LocalVariant>()
                varIt.forEach { variant ->
                    variants.add(variant)
                }
                generateVariantExcel(variants)
            } else {
                Utils.showMsgShortIntervel(this@LocalInventoryActivity, "No varaint found to export")
            }
        }
    }

    private fun searchProduct(param: String) {
        if (productList.isNotEmpty()) {
            productList.clear()
        }
        localInventoryViewModel.search(param)

        localInventoryViewModel.searchedLocalProductList.observe(
            this@LocalInventoryActivity,
            Observer { localProductList ->
                productList = localProductList as ArrayList

                if (productList.size > 0) {
                    setUpProductRecyclerView(productList)
                    rc_product_details_variants.adapter?.notifyDataSetChanged()
                } else {
                    Utils.showMsgShortIntervel(this, "No result found")
                }
            })

        /* localProductRepository.searchLocalProducts(param).observe(this,
             Observer<List<LocalProduct>> { localProductList ->
                 varaintList = localProductList as ArrayList

                 if (varaintList.size > 0) {
                     setUpProductRecyclerView(varaintList)
                     rc_product_details_variants.adapter?.notifyDataSetChanged()
                 } else {
                     Utils.showMsgShortIntervel(this, "No result found")
                 }
             })*/
    }

    private fun setUpProductRecyclerView(list: ArrayList<LocalProduct>) {

        rc_product_details_variants.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this@LocalInventoryActivity)

        rc_product_details_variants.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val productItemTitle = mainView.findViewById<TextView>(R.id.product_item_title)
                val productItemNewPrice = mainView.findViewById<TextView>(R.id.product_item_new_price)
                val productItemOldPrice = mainView.findViewById<TextView>(R.id.product_item_old_price)
                val productItemIcon = mainView.findViewById<ImageView>(R.id.product_item_icon)
                val productRemove = mainView.findViewById<ImageView>(R.id.product_remove)

                productItemTitle.text = itemData.productName
                productItemOldPrice.text = itemData.productMrp
                productItemNewPrice.text = itemData.offerPrice
                productRemove.visibility = View.VISIBLE
                val file = Utils.getProductImage(itemData.storeProductId, "1")

                mainView.setOnClickListener {
                    getShowVariant(itemData.storeProductId)
                    tv_varaint_prd_name.text = itemData.productName
                }
                productRemove.setOnClickListener {

                    Utils.showAlert(this@LocalInventoryActivity,
                        getString(R.string.app_name)
                        , getString(R.string.are_you_sure_you_want_to_remove),
                        getString(R.string.ok), getString(R.string.cancel),
                        DialogInterface.OnClickListener { _, _ ->
                            //localProductRepository.deleteLocalProducts(itemData.storeProductId)
                            localInventoryViewModel.deleteLocalProduct(itemData.storeProductId)
                            //val lst = localVariantRepository.getStaticVaraintIdList(itemData.storeProductId)
                            val lst = localInventoryViewModel.getLocalProductVariantById(itemData.storeProductId)
                            if (lst.isNotEmpty()) {
                                //localVariantRepository.deleteVaraint(lst)
                                localInventoryViewModel.deleteLocalProductVariants(lst)
                            }
                        },
                        DialogInterface.OnClickListener { _, _ ->
                        })

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

    private fun getShowVariant(productId: Long) {
        localInventoryViewModel.getAllLocalProductVariantsById(productId)
            .observe(this@LocalInventoryActivity, Observer { localVariantList ->
                variantList = localVariantList as ArrayList
                setUpVariantRecyclerView(variantList)
                rc_product_details_variants.adapter?.notifyDataSetChanged()
            })
    }

    @SuppressLint("SetTextI18n")
    private fun setUpVariantRecyclerView(list: ArrayList<LocalVariant>) {

        rv_products_variants.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(this@LocalInventoryActivity, 2)

        rv_products_variants.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val productItemTitle = mainView.findViewById<TextView>(R.id.product_item_title)
                val productItemNewPrice = mainView.findViewById<TextView>(R.id.product_item_new_price)
                val productItemOldPrice = mainView.findViewById<TextView>(R.id.product_item_old_price)
                val productItemIcon = mainView.findViewById<ImageView>(R.id.product_item_icon)
                val btnDlt = mainView.findViewById<ImageView>(R.id.btnDlt)
                val btnEdt = mainView.findViewById<ImageView>(R.id.btnEdt)

                btnDlt.visibility = View.VISIBLE
                btnEdt.visibility = View.VISIBLE
                productItemTitle.text = "Varaint Id: ${itemData.storeRangeId}"
                productItemOldPrice.text = itemData.productMrp
                productItemNewPrice.text = itemData.offerPrice

                val file = Utils.getVaraintImage(itemData.productId, itemData.storeRangeId)

                btnDlt.setOnClickListener {
                    //localVariantRepository.deleteVaraint(itemData.storeRangeId)
                    localInventoryViewModel.deleteLocalProductVariant(itemData.storeRangeId)
                }
                btnEdt.setOnClickListener {
                    showDialogue(itemData)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /* region Excel file handling */
    private fun generateProductExcel(prodList: ArrayList<LocalProduct>) {
        doAsync {
            try {
                val root = Utils.getPath(this@LocalInventoryActivity)
                val myDir = File("$root/excelfiles")
                if (!myDir.exists()) {
                    myDir.mkdirs()
                }
                val file = File(myDir, CVS_PRODUCT_FILE)
                val csvWrite = CSVWriter(FileWriter(file))
                val arrStr1 = arrayOf(
                    "product_id",
                    "categoryId",
                    "categoryName",
                    "subcategoryId",
                    "subcategoryName",
                    "productImages",
                    "productTags",
                    "productName",
                    "smallDescription",
                    "productMrp",
                    "offerPrice",
                    "storeRangeId",
                    "currencyTitle",
                    "rangeName",
                    "unitName",
                    "unlimitedStock",
                    "outOfStock",
                    "purchaseLimit",
                    "stockBalance",
                    "productImagesArray",
                    "barcode"
                )

                csvWrite.writeNext(arrStr1)

                prodList.forEach { prd ->
                    val arrData = arrayOf(
                        prd.storeProductId.toString(),
                        prd.categoryId.toString(),
                        prd.categoryName.toString(),
                        prd.subcategoryId.toString(),
                        prd.subcategoryName.toString(),
                        prd.productImages.toString(),
                        prd.productTags.toString(),
                        prd.productName.toString(),
                        prd.smallDescription.toString(),
                        prd.productMrp.toString(),
                        prd.offerPrice.toString(),
                        prd.storeRangeId.toString(),
                        prd.currencyTitle.toString(),
                        prd.rangeName.toString(),
                        prd.unitName.toString(),
                        prd.unlimitedStock.toString(),
                        prd.outOfStock.toString(),
                        prd.purchaseLimit.toString(),
                        prd.stockBalance.toString(),
                        prd.productImagesArray.toString(),
                        prd.barcode.toString()
                    )
                    csvWrite.writeNext(arrData)
                }
                Utils.openFile(this@LocalInventoryActivity, file)
                csvWrite.close()

            } catch (e: Exception) {
                Timber.e("Exception $e")

            } finally {
                Timber.e("Completed")
            }
        }
    }

    private fun generateVariantExcel(prodList: ArrayList<LocalVariant>) {
        doAsync {
            try {
                val root = Utils.getPath(this@LocalInventoryActivity)
                val myDir = File("$root/excelfiles")
                if (!myDir.exists()) {
                    myDir.mkdirs()
                }
                val file = File(myDir, CVS_VARIANT_FILE)
                val csvWrite = CSVWriter(FileWriter(file))
                val arrStr1 = arrayOf(
                    "storeRangeId",
                    "sku",
                    "productMrp",
                    "offerPrice",
                    "rangeName",
                    "rangeId",
                    "productImage",
                    "unitId",
                    "unitName",
                    "barCode",
                    "purchaseLimit",
                    "unlimitedStock",
                    "stockBalance",
                    "outOfStock",
                    "offer_product",
                    "productId",
                    "discount"
                )

                csvWrite.writeNext(arrStr1)

                prodList.forEach { prd ->
                    val arrData = arrayOf(
                        prd.storeRangeId.toString(),
                        prd.sku.toString(),
                        prd.productMrp.toString(),
                        prd.offerPrice.toString(),
                        prd.rangeName.toString(),
                        prd.rangeId.toString(),
                        prd.productImage.toString(),
                        prd.unitId.toString(),
                        prd.unitName.toString(),
                        prd.barCode.toString(),
                        prd.purchaseLimit.toString(),
                        prd.unlimitedStock.toString(),
                        prd.stockBalance.toString(),
                        prd.outOfStock.toString(),
                        prd.offer_product.toString(),
                        prd.productId.toString(),
                        prd.discount.toString()

                    )
                    csvWrite.writeNext(arrData)
                }
                csvWrite.close()

            } catch (e: Exception) {
                Timber.e("Exception $e")

            } finally {
                Timber.e("Completed")
                uiThread {
                    Utils.hideLoading()

                    Utils.showMsgShortIntervel(this@LocalInventoryActivity, "Csv File generated successfully")
                }
            }
        }
    }

    private fun openFile() {
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent, "Open CSV"), PICK_EXCEL_FILE)
        } catch (e: Exception) {
            Timber.e("openFile() Exception ${e.cause}")
        }
    }

    private fun readExcel(path: Uri) {
        Utils.showLoading(false, this@LocalInventoryActivity)
        doAsync {
            try {
                val fileName = File(path.path).name
                if (fileName == CVS_PRODUCT_FILE) {
                    readProductData(path)
                    uiThread {
                        Utils.hideLoading()
                    }

                } else if (fileName == CVS_VARIANT_FILE) {
                    readVariantData(path)
                    uiThread {
                        Utils.hideLoading()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun readProductData(path: Uri) {
        val reader = CSVReader(
            InputStreamReader(
                BufferedInputStream(
                    contentResolver.openInputStream(path)
                )
            )
        )

        var line: Array<String>? = null
        var count = 0
        val prodList = ArrayList<LocalProduct>()
        while ({ line = reader.readNext(); line }() != null) {
            System.out.println(line)
            if (count != 0) {
                val prd = LocalProduct()
                prd.storeProductId = line!![0].toLong()
                prd.categoryId = line!![1]
                prd.categoryName = line!![2]
                prd.subcategoryId = line!![3]
                prd.subcategoryName = line!![4]
                prd.productImages = null
                prd.productTags = null
                prd.productName = line!![7]
                prd.smallDescription = line!![8]
                prd.productMrp = line!![9]
                prd.offerPrice = line!![10]
                prd.storeRangeId = line!![11]
                prd.currencyTitle = line!![12]
                prd.rangeName = line!![13]
                prd.unitName = line!![14]
                prd.unlimitedStock = line!![15]
                prd.outOfStock = line!![16]
                prd.purchaseLimit = line!![17]
                prd.stockBalance = line!![18]
                prd.productImagesArray = line!![19]
                prd.barcode = line!![20]

                prodList.add(prd)
            }
            count += 1
        }

        localInventoryViewModel.insertLocalProductList(prodList)
        //localProductRepository.insertLocalProducts(prodList)

    }

    private fun readVariantData(path: Uri) {
        val reader = CSVReader(
            InputStreamReader(
                BufferedInputStream(
                    contentResolver.openInputStream(path)
                )
            )
        )

        var line: Array<String>? = null
        var count = 0
        val varList = ArrayList<LocalVariant>()
        while ({ line = reader.readNext(); line }() != null) {
            System.out.println(line)
            if (count != 0) {
                val prd = LocalVariant()
                prd.storeRangeId = line!![0].toLong()
                prd.sku = line!![1]
                prd.productMrp = line!![2]
                prd.offerPrice = line!![3]
                prd.rangeName = line!![4]
                prd.rangeId = null
                prd.productImage = null
                prd.unitId = line!![7]
                prd.unitName = line!![8]
                prd.barCode = line!![9]
                prd.purchaseLimit = line!![10]
                prd.unlimitedStock = line!![11]
                prd.stockBalance = line!![12]
                prd.outOfStock = line!![13]
                prd.offer_product = line!![14]
                prd.productId = line!![15].toLong()
                prd.discount = line!![16]

                varList.add(prd)
            }
            count += 1
        }
        //localVariantRepository.insertLocalVariants(varList)

        localInventoryViewModel.insertLocalProductVariants(varList)

    }

    //endregion

    @SuppressLint("InflateParams")
    private fun showDialogue(variantObj: LocalVariant) {

        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_varaint_update, null)
        val alertBox = AlertDialog.Builder(this)
        alertBox.setView(view)
        alertBox.setCancelable(true)
        val dialog = alertBox.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val btnClose: ImageView = view.findViewById(R.id.btn_close_dialog)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        val productImage: ImageView = view.findViewById(R.id.img_catalog_product_variant_image)
        val tvVaraiantProduct: TextView = view.findViewById(R.id.tvVaraiantProduct)
        val productPrice: TextInputEditText = view.findViewById(R.id.variant_price)
        val productDiscount: TextInputEditText = view.findViewById(R.id.variant_discount)
        val productPurchaseLimit: TextInputEditText = view.findViewById(R.id.et_catalog_product_variant_purchase_limit)
        val productStockBalance: TextInputEditText = view.findViewById(R.id.et_catalog_product_variant_stock_balance)
        val barcode: TextInputEditText = view.findViewById(R.id.varaint_bar_code)
        val checkBoxOutOfStock: CheckBox = view.findViewById(R.id.checkbox_catalog_product_variant_out_of_stock)
        val checkBoxUnlimitedStock: CheckBox = view.findViewById(R.id.checkbox_catalog_product_variant_unlimited_stock)
        val checkBoxOfferProduct: CheckBox = view.findViewById(R.id.checkbox_catalog_product_variant_offer_product)
        val btnSave: Button = view.findViewById(R.id.btnSave)

        val offerPrice = (variantObj.offerPrice)!!.toDouble()
        val file = Utils.getVaraintImage(variantObj.productId, variantObj.storeRangeId)
        if (file.exists()) {
            Picasso.get()
                .load(file)
                .error(R.drawable.no_image)
                .into(productImage)

        } else {
            Picasso.get()
                .load(R.drawable.no_image)
                .into(productImage)
        }
        if (variantObj.type == Constants.BAR_CODED_PRODUCT) {
            val initialMap = (variantObj.productMrp)!!.toDouble()
            productDiscount.setText((initialMap - offerPrice).toString())

        }
        productStockBalance.setText(variantObj.stockBalance)
        productPrice.setText(variantObj.offerPrice)
        productPurchaseLimit.setText(variantObj.purchaseLimit)
        barcode.setText(variantObj.barCode)
        checkBoxUnlimitedStock.isChecked = variantObj.unlimitedStock == "1"
        checkBoxOutOfStock.isChecked = variantObj.outOfStock == "1"
        checkBoxOfferProduct.isChecked = variantObj.offer_product == "1"
        tvVaraiantProduct.text = tv_varaint_prd_name.text


        productPurchaseLimit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
//                variantsList[position].purchaseLimit = s.toString()
            }
        })



        checkBoxOfferProduct.setOnCheckedChangeListener { _, isChecked ->
            variantObj.offer_product = if (isChecked) "1" else "0"
        }
        checkBoxOutOfStock.setOnCheckedChangeListener { _, isChecked ->
            variantObj.outOfStock = if (isChecked) "1" else "0"
        }
        checkBoxUnlimitedStock.setOnCheckedChangeListener { _, isChecked ->
            variantObj.unlimitedStock = if (isChecked) "1" else "0"
        }
        btnSave.setOnClickListener {
            launch {
                variantObj.discount = productDiscount.text.toString()
                variantObj.barCode = barcode.text.toString()
                variantObj.stockBalance = productStockBalance.text.toString()
                variantObj.purchaseLimit = productPurchaseLimit.text.toString()
                setResult(Activity.RESULT_OK, intent)
                localVariantRepository.insertLocalVariant(variantObj)
                Utils.showMsgShortIntervel(this@LocalInventoryActivity, "Variant updated")
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_EXCEL_FILE) {
            if (data != null) {
                val fPath = data.data!!
                readExcel(fPath)
            }
        }
    }

}
