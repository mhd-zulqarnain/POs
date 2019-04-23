package com.goshoppi.pos.view.inventory

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.Observer
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_user.toolbar
import kotlinx.android.synthetic.main.activity_add_user.tv_varaint_prd_name
import kotlinx.android.synthetic.main.activity_inventoryproduct_details.rc_product_details_variants
import kotlinx.android.synthetic.main.activity_inventroy_home.svSearch
import kotlinx.android.synthetic.main.activity_local_inventory.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import javax.inject.Inject

class LocalInventoryActivity : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_local_inventory
    }

    private lateinit var sharedPref: SharedPreferences
    private var productList: ArrayList<LocalProduct> = ArrayList()
    private var variantList: ArrayList<LocalVariant> = ArrayList()
    private val PICK_EXCEL_FILE = 12

    @Inject
    lateinit var localProductRepository: LocalProductRepository

    @Inject
    lateinit var localVariantRepository: LocalVariantRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        initView()
    }

    private fun initView() {
        variantList = ArrayList()
        localProductRepository.loadAllLocalProduct().observe(this,
            Observer<List<LocalProduct>> { localProductsList ->
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
            Utils.showLoading(false,this)

            val prodIt = localProductRepository.loadAllStaticLocalProduct()
            if (prodIt.size != 0) {
                val prodList = ArrayList<LocalProduct>()
                prodIt.forEach { prod ->
                    prodList.add(prod)
                }
                generateProductExcel(prodList)
            }else{
                Utils.showMsg(this@LocalInventoryActivity,"No products found to export")

            }
            val varIt = localVariantRepository.loadAllStaticLocalVariants()
            if (varIt.size != 0) {
                val varaints = ArrayList<LocalVariant>()
                varIt.forEach { varaint ->
                    varaints.add(varaint)
                }
                generateVariantExcel(varaints)
            }else{
              Utils.showMsg(this@LocalInventoryActivity,"No varaint found to export")
            }

        }
    }

    private fun searchProduct(param: String) {
        if (!productList.isEmpty()) {
            productList.clear()
        }

        localProductRepository.searchLocalProducts(param).observe(this,
            Observer<List<LocalProduct>> { localProductList ->
                productList = localProductList as ArrayList

                if (productList.size > 0) {
                    setUpProductRecyclerView(productList)
                    rc_product_details_variants.adapter?.notifyDataSetChanged()
                } else {
                    Utils.showMsg(this, "No result found")
                }
            })
    }

    private fun setUpProductRecyclerView(list: ArrayList<LocalProduct>) {

        rc_product_details_variants.layoutManager =
           LinearLayoutManager(this@LocalInventoryActivity)

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
                        DialogInterface.OnClickListener { dialog, which ->
                            localProductRepository.deleteLocalProducts(itemData.storeProductId)
                            val lst = localVariantRepository.getStaticVaraintIdList(itemData.storeProductId)
                            if (lst.size != 0) {
                                localVariantRepository.deleteVaraint(lst)
                            }

                        },
                        DialogInterface.OnClickListener { dialog, which ->
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

    private fun getShowVariant(productId: Int) {
        localVariantRepository.getLocalVariantsByProductId(productId).observe(this,
            Observer<List<LocalVariant>> { localVariantList ->
                variantList = localVariantList as ArrayList
                setUpVariantRecyclerView(variantList)
                rc_product_details_variants.adapter?.notifyDataSetChanged()
            })
    }

    @SuppressLint("SetTextI18n")
    private fun setUpVariantRecyclerView(list: ArrayList<LocalVariant>) {

        rv_products_variants.layoutManager =
           GridLayoutManager(this@LocalInventoryActivity, 2)

        rv_products_variants.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val productItemTitle = mainView.findViewById<TextView>(R.id.product_item_title)
                val productItemNewPrice = mainView.findViewById<TextView>(R.id.product_item_new_price)
                val productItemOldPrice = mainView.findViewById<TextView>(R.id.product_item_old_price)
                val productItemIcon = mainView.findViewById<ImageView>(R.id.product_item_icon)
                val btnDlt = mainView.findViewById<ImageView>(R.id.btnDlt)

                btnDlt.visibility = View.VISIBLE
                productItemTitle.text = "Varaint Id: ${itemData.storeRangeId}"
                productItemOldPrice.text = itemData.productMrp
                productItemNewPrice.text = itemData.offerPrice

                val file = Utils.getVaraintImage(itemData.productId, itemData.storeRangeId)

                btnDlt.setOnClickListener {
                    localVariantRepository.deleteVaraint(itemData.storeRangeId)
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
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                this@LocalInventoryActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //region Excel file handling
    fun generateProductExcel(prodList: ArrayList<LocalProduct>) {
        doAsync {
            try {
                val root = Environment.getExternalStorageDirectory().toString()
                val myDir = File("$root/excelfiles")
                if (!myDir.exists()) {
                    myDir.mkdirs()
                }
                val file = File(myDir, Constants.CVS_PRODUCT_FILE)
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
                    val arrdata = arrayOf(
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
                    csvWrite.writeNext(arrdata)
                }
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
                val root = Environment.getExternalStorageDirectory().toString()
                val myDir = File("$root/excelfiles")
                if (!myDir.exists()) {
                    myDir.mkdirs()
                }
                val file = File(myDir, Constants.CVS_VARIANT_FILE)
                val csvWrite = CSVWriter(FileWriter(file))
                val arrStr1 = arrayOf("storeRangeId", "sku", "productMrp", "offerPrice", "rangeName", "rangeId", "productImage", "unitId", "unitName", "barCode", "purchaseLimit", "unlimitedStock", "stockBalance", "outOfStock", "offer_product", "productId", "discount")

                csvWrite.writeNext(arrStr1)

                prodList.forEach { prd ->
                    val arrdata = arrayOf(
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
                    csvWrite.writeNext(arrdata)
                }
                csvWrite.close()

            } catch (e: Exception) {
                Timber.e("Exception $e")

            } finally {
                Timber.e("Completed")
                uiThread {
                    Utils.hideLoading()

                    Utils.showMsg(this@LocalInventoryActivity, "Csv File generated successfully")
                }

            }
        }
    }

    private fun openFile() {
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("*/*")
            startActivityForResult(Intent.createChooser(intent, "Open CSV"), PICK_EXCEL_FILE)

        } catch (e: Exception) {

        }
    }

    private fun readExcel(path: Uri?) {
        Utils.showLoading(false,this@LocalInventoryActivity)
        doAsync {
            try {
                val fileName = File(path!!.getPath()).getName()
                if (fileName == Constants.CVS_PRODUCT_FILE) {
                    readProductdata(path)
                    uiThread {
                        Utils.hideLoading()
                    }

                } else if (fileName == Constants.CVS_VARIANT_FILE) {
                    readVariantdata(path)
                    uiThread {
                        Utils.hideLoading()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun readProductdata(path: Uri) {
        val reader = CSVReader(InputStreamReader(
                BufferedInputStream(
                    getContentResolver().openInputStream(path)
                )
            ))
        var line: Array<String>? = null
        var count = 0
        val prodList = ArrayList<LocalProduct>()
        while ({ line = reader.readNext() ;line }() != null) {
            System.out.println(line)
            if (count != 0) {
                val prd = LocalProduct()
                prd.storeProductId = line!![0].toInt()
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
        localProductRepository.insertLocalProducts(prodList)

    }

    private fun readVariantdata(path: Uri) {
        val reader = CSVReader(
            InputStreamReader(
                BufferedInputStream(
                    getContentResolver().openInputStream(path)
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

                prd.storeRangeId = line!![0].toInt()
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
                prd.productId = line!![15].toInt()
                prd.discount = line!![16]

                varList.add(prd)
            }
            count += 1
        }
        localVariantRepository.insertLocalVariants(varList)

    }

    //endregion

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_EXCEL_FILE) {
            if (data != null) {
                val Fpath = data.data
                readExcel(Fpath)
            }
        }
    }

}
