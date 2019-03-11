package com.goshoppi.pos

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.goshoppi.pos.architecture.SyncWorker
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.architecture.model.ProductViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.addListDivider


private const val TAG = "PosMainActivity"

class PosMainActivity : AppCompatActivity() {

    private var productViewModel: ProductViewModel? = null
    var productList: ArrayList<Product>? = null
    var adapter: ProductSearchAdapter? = null
    var totalCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pos_main)

        val mRecyclerView: RecyclerView = findViewById(R.id.rc_report)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.addListDivider()

        val myConstraints = Constraints.Builder()
                .setRequiresCharging(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                // Many other constraints are available, see the
                // Constraints.Builder reference
                .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(myConstraints).build()

        WorkManager.getInstance().enqueue(syncWorkRequest)

        WorkManager.getInstance().getWorkInfoByIdLiveData(syncWorkRequest.id)
                .observe(this@PosMainActivity, Observer { workInfo ->
                    // Do something with the status
                    /*if (workInfo != null && workInfo.state.isFinished) {
                    }*/
                })

        productViewModel = ViewModelProviders.of(this@PosMainActivity).get(ProductViewModel(application)::class.java)
        productList = ArrayList()

        adapter = ProductSearchAdapter(this@PosMainActivity)
        mRecyclerView.adapter = adapter

        //getProductList()

        productViewModel?.listProductLiveData?.observe(this@PosMainActivity, Observer<List<Product>> {

            productList!!.clear()
            it!!.forEach { obj ->
                productList!!.add(obj)
            }

            adapter!!.setProductList(productList!!)
        })

        productViewModel?.totalCount?.observe(this@PosMainActivity,object: Observer<Int> {
            override fun onChanged(t: Int?) {
                totalCount = t!!
                //getProductList()
                productViewModel?.totalCount?.removeObserver(this)
            }
        })
    }

    inner class ProductSearchAdapter(var ctx: Context) : RecyclerView.Adapter<MyViewHolder>() {
        private var productList: ArrayList<Product>? = null

        fun setProductList(productList: ArrayList<Product>) {
            this.productList = productList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
            val v = MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.single_row_search_products, parent, false))
            return v
        }

        override fun getItemCount(): Int {
            if (null == productList) return 0
            return productList!!.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            val product = productList!![position]
            try {

                holder.tvName.text = product.productName
                holder.etPrice.setText(product.productMrp)
                holder.etStock.setText(product.stockBalance)
                holder.itemView.setOnClickListener {
                   // val intent = Intent(this@PosMainActivity, CatalogDetail::class.java)
                    /*val extr = Gson().toJson(product)
                    intent.putExtra("catalog_obj", extr)
                    intent.putExtra("position", position.toString())
                    //startActivityForResult(intent, PRODUCT_UPDATE_INFO)
                    startActivity(intent)*/

                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var tvName: TextView = view.findViewById<View>(R.id.tvName) as TextView
        internal var etPrice: TextView = view.findViewById<View>(R.id.etPrice) as TextView
        internal var etStock: TextView = view.findViewById<View>(R.id.etStock) as TextView
    }
}
