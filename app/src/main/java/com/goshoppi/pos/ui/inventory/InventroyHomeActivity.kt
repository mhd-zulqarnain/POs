package com.goshoppi.pos.ui.inventory

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.View
import com.goshoppi.pos.R
import com.goshoppi.pos.model.Product
import kotlinx.android.synthetic.main.activity_inventroy_home.*

class InventroyHomeActivity : AppCompatActivity(), View.OnClickListener {


    var adapter: ProductAdapter? = null
    lateinit var gridLayoutManager: GridLayoutManager
    var productsList: ArrayList<Product> = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventroy_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        initializeUi()
    }

    fun initializeUi() {

        ivBigMicSearch.setOnClickListener(this)
        brSearch.setOnClickListener(this)
        adapter!!.setProductList(productsList)
        rvProduct.adapter = adapter

        gridLayoutManager = GridLayoutManager(this, 3)
        rvProduct.layoutManager = gridLayoutManager
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
//                searchproduct(p0.toString().toLowerCase())
                return true;
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true;
            }

        })


    }

    fun searchProduct(){

    }

    override fun onClick(v: View?) {

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
