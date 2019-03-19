package com.goshoppi.pos.ui.inventory

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.View
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.model.ProductViewModel
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.utils.Utils
import kotlinx.android.synthetic.main.activity_inventroy_home.*

class InventroyHomeActivity : AppCompatActivity(), View.OnClickListener,SharedPreferences.OnSharedPreferenceChangeListener {


    var adapter: ProductAdapter? = null
    lateinit var gridLayoutManager: GridLayoutManager
    var productsList: ArrayList<Product> = ArrayList<Product>()
    private var productViewModel: ProductViewModel? = null
    private lateinit var sharedPref: SharedPreferences
    private val WRITE_PERMISSION =322

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventroy_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPref.registerOnSharedPreferenceChangeListener(this)

        setSupportActionBar(toolbar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        initializeUi()
    }

    fun initializeUi() {
        askWritePermission()

        adapter = ProductAdapter(this@InventroyHomeActivity)
        rvProduct.adapter = adapter
        productViewModel = ViewModelProviders.of(this@InventroyHomeActivity).get(ProductViewModel(application)::class.java)

        gridLayoutManager = GridLayoutManager(this, 4)
        rvProduct.layoutManager = gridLayoutManager
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true;
            }

            override fun onQueryTextChange(param: String?): Boolean {
               if(param!=null && param !="")
                searchProduct(param)

                return true;
            }

        })


    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val selectedTheme = sharedPref.getBoolean(getString(R.string.pref_theme_key),false)
        setAppTheme(selectedTheme)
        recreate()
    }

    private fun askWritePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(findViewById(android.R.id.content), "Needed storage permission", Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok) { requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_PERMISSION) }
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_PERMISSION)
        }
        return false
    }
    private fun setAppTheme(currentTheme: Boolean) {
        when (currentTheme) {
            true-> setTheme(R.style.Theme_App_Green)
            else -> setTheme(R.style.Theme_App)
        }

    }

    fun searchProduct(param: String) {
        if(!productsList.isEmpty()){
            productsList.clear()
        }
        productsList= productViewModel!!.productRepository.searhMasterProduct(param) as ArrayList<Product>

      if(productsList.size>0){
         rvProduct.visibility =View.VISIBLE
         rlMainSearch.visibility =View.INVISIBLE

     }else{
         rvProduct.visibility =View.INVISIBLE
         rlMainSearch.visibility =View.VISIBLE
          Utils.showMsg(this,"No result found")
     }
        adapter!!.setProductList(productsList)
        adapter!!.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {

    }
    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }
    private fun filterDataByBarcode(query: String) {


        /*    pd!!.show()

            ServiceHandler.instance?.getProdbyBarcode(WebServiceConstants.getMerchantWebServiceKey(), Utils.loginData.storeId, query)
                ?.enqueue(object : Callback<ProductResponse> {
                    override fun onResponse(call: retrofit2.Call<ProductResponse>, response: Response<ProductResponse>) {

                        if (response.isSuccessful) {
                            if (response.body() != null) {
                                val myResponse = response.body() as ProductResponse

                                if (myResponse.code == 200) {
                                    if (!productsList.isEmpty()) {
                                        productsList.clear()
                                    }

                                    val product = myResponse.data!!.products!![0]
                                    productsList.add(product)
                                    adapter!!.notifyDataSetChanged()

                                    rvProduct.visibility = View.VISIBLE
                                    rlMainSearch.visibility = View.GONE


                                } else {
                                    Toast.makeText(this@InventroyHomeActivity, "No product found", Toast.LENGTH_LONG).show()
                                    rlMainSearch.visibility = View.VISIBLE

                                }
                            }
                        }
                        pd!!.dismiss()

                    }

                    override fun onFailure(call: retrofit2.Call<ProductResponse>, t: Throwable) {
                        pd!!.dismiss()
                    }

                })
            FullScannerActivity.BARCODE = null

    */
    }


}
