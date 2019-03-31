package com.goshoppi.pos.view.inventory

import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.arch.paging.PagedListAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.architecture.repository.masterVariantRepo.MasterVariantRepository
import com.goshoppi.pos.di.component.DaggerAppComponent
import com.goshoppi.pos.di.module.AppModule
import com.goshoppi.pos.di.module.RoomModule
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.model.master.MasterVariant
import com.goshoppi.pos.utils.Constants.PRODUCT_OBJECT_INTENT
import com.goshoppi.pos.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_inventroy_home.*
import javax.inject.Inject

class InventoryHomeActivity : AppCompatActivity(), View.OnClickListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var pagerAdapter: MyPagerAdapter? = null
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var masterProductRepository: MasterProductRepository
    @Inject
    lateinit var localVariantRepository: LocalVariantRepository
    @Inject
    lateinit var localProductRepository: LocalProductRepository
    @Inject
    lateinit var masterVariantRepository: MasterVariantRepository

    private lateinit var variantList: ArrayList<MasterVariant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .roomModule(RoomModule(application))
            .build()
            .injectInventoryHomeActivity(this)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        sharedPref.registerOnSharedPreferenceChangeListener(this)

        setContentView(R.layout.activity_inventroy_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        initializeUi()
    }

    private fun initializeUi() {

        variantList = ArrayList()
        gridLayoutManager = GridLayoutManager(this, 4)
        rvProduct.layoutManager = gridLayoutManager

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

        setPagerAdapter()
        searchProduct("")
        rvProduct.visibility = View.VISIBLE
        rlMainSearch.visibility = View.INVISIBLE

    }

    private fun setPagerAdapter() {
        pagerAdapter =MyPagerAdapter(this){prd,isOption->
            if (!isOption) {
                val intent = Intent(this@InventoryHomeActivity, InventoryProductDetailsActivity::class.java)
                val obj = Gson().toJson(prd)
                intent.putExtra(PRODUCT_OBJECT_INTENT, obj)
                startActivity(intent)
            } else {
                addtoLocaldb(prd)
            }
        }
        gridLayoutManager = GridLayoutManager(this, 4)
        rvProduct.setHasFixedSize(true);
        rvProduct.layoutManager = gridLayoutManager
        rvProduct.adapter = pagerAdapter

    }

    private fun addtoLocaldb(it: MasterProduct) {

        val mjson = Gson().toJson(it)
        val product: LocalProduct = Gson().fromJson(mjson, LocalProduct::class.java)
        this.localProductRepository.insertLocalProduct(product)
        variantList = masterVariantRepository.getMasterStaticVariantsOfProducts(product.storeProductId) as ArrayList

        /*saving variants to local database*/
        variantList.forEach {
            val json = Gson().toJson(it)
            val variant: LocalVariant = Gson().fromJson(json, LocalVariant::class.java)
            localVariantRepository.insertLocalVariant(variant)
        }

        Utils.showAlert("Product Added", "Added to local Database", this)

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

    private fun searchProduct(param: String) {
        /*if (!productsList.isEmpty()) {
            productsList.clear()
        }*/

        masterProductRepository.loadAllPaginatedMasterSearchProduct(param).observe(this,
            Observer<PagedList<MasterProduct>> {it->
                if(it!=null) {
                    pagerAdapter!!.submitList(it)

                }
            }
        )
    }

    override fun onClick(v: View?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                this@InventoryHomeActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class MyPagerAdapter(
        var ctx: Context,
        private val onItemClick: (productObj: MasterProduct, isOption: Boolean) -> Unit
    ) :
        PagedListAdapter<MasterProduct, MyPagerAdapter.MyViewHolder>(object : DiffUtil.ItemCallback<MasterProduct>() {
            override fun areItemsTheSame(oldItem: MasterProduct, newItem: MasterProduct) =
                oldItem.productName == newItem.productName

            override fun areContentsTheSame(oldItem: MasterProduct, newItem: MasterProduct) =
                oldItem.productName == newItem.productName

        }) {

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(ctx).inflate(
                    R.layout.single_product_view,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val product = getItem(position)
            if (product != null) {
            }

            if (product != null) {
                holder.product_item_title.text = product.productName
                holder.product_weight_range.text = product.productMrp
                holder.product_item_new_price.text = product.offerPrice
                holder.tv_Options.visibility = View.VISIBLE

                holder.itemView.setOnClickListener {
                    onItemClick(product, false)
                }
                holder.tv_Options.setOnClickListener {
                    val popup = PopupMenu(ctx, holder.tv_Options)
                    popup.inflate(R.menu.pop_up_product_menu)
                    popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                        override fun onMenuItemClick(item: MenuItem?): Boolean {
                            when (item!!.itemId) {
                                R.id.nav_add_to_local -> {
                                    onItemClick(product, true)
                                    return true
                                }
                            }
                            return true
                        }

                    })
                    popup.show()
                }
                val file = Utils.getProductImage(product.storeProductId, "1")
                if (file.exists()) {
                    Picasso.get()
                        .load(file)
                        .error(R.drawable.no_image)
                        .into(holder.product_item_icon)

                } else {
                    Picasso.get()
                        .load(R.drawable.no_image)
                        .into(holder.product_item_icon)
                }
            }
        }

        class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            internal var product_item_title: TextView = view.findViewById<View>(R.id.product_item_title) as TextView
            internal var product_weight_range: TextView =
                view.findViewById<View>(R.id.product_item_new_price) as TextView
            internal var product_item_new_price: TextView =
                view.findViewById<View>(R.id.product_item_new_price) as TextView
            internal var product_item_icon: ImageView = view.findViewById<View>(R.id.product_item_icon) as ImageView
            internal var tv_Options: TextView = view.findViewById<View>(R.id.tv_Options) as TextView
        }
    }
}