package com.goshoppi.pos.view.inventory

import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.ProductRepository
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_local_inventory.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.io.File

class LocalInventory : AppCompatActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    private var currentTheme: Boolean = false
    private lateinit var sharedPref: SharedPreferences
    var productList: ArrayList<LocalProduct> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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


    fun initView(){



        doAsync {
            productList = ProductRepository.getInstance(this@LocalInventory).getAllLocalProducts(
               ) as ArrayList<LocalProduct>
            uiThread {
                Timber.e("Size ${productList.size}")
                setUpRecyclerView(productList)
                rc_product_details_variants.adapter?.notifyDataSetChanged()
            }
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


    }

    fun searchProduct(param: String) {
        if (!productList.isEmpty()) {
            productList.clear()
        }
        productList = ProductRepository.getInstance(this@LocalInventory).searhLocalProduct(param) as ArrayList<LocalProduct>

        if (productList.size > 0) {
            setUpRecyclerView(productList)
            rc_product_details_variants.adapter?.notifyDataSetChanged()

        } else {

            Utils.showMsg(this, "No result found")
        }
    }

    private fun setUpRecyclerView(list: ArrayList<LocalProduct>) {


//        setImage(Utils.getProductImage(initVariant.productId,"1"),iv_prd_img)

        rc_product_details_variants.layoutManager = LinearLayoutManager(this@LocalInventory)
        rc_product_details_variants.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val product_item_title = mainView.findViewById<TextView>(R.id.product_item_title)
                val product_item_new_price = mainView.findViewById<TextView>(R.id.product_item_new_price)
                val product_item_old_price = mainView.findViewById<TextView>(R.id.product_item_old_price)
                val product_item_icon = mainView.findViewById<ImageView>(R.id.product_item_icon)

                val product = itemData

                product_item_title.text = product.productName
                product_item_old_price.text = product.productMrp
                product_item_new_price.text = product.offerPrice


                val file = Utils.getProductImage(product.storeProductId,"1")
                Timber.e("File is there ? ${file.exists()}")

                if (file.exists()) {
                    Picasso.get()
                        .load(file)
                        .error(R.drawable.no_image)
                        .into(product_item_icon)

                } else {
                    Picasso.get()
                        .load(R.drawable.no_image)
                        .into(product_item_icon)
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

    fun setImage(img: File, rsc: ImageView){
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
                this@LocalInventory.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
