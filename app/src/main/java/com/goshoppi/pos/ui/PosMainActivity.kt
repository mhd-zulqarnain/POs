package com.goshoppi.pos.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.TextView
import androidx.work.*
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.model.ProductViewModel
import com.goshoppi.pos.architecture.workmanager.SyncWorker
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.ui.inventory.InventroyHomeActivity
import com.goshoppi.pos.utils.Constants
import com.ishaquehassan.recyclerviewgeneraladapter.addListDivider
import timber.log.Timber


private const val TAG = "PosMainActivity"
private const val ONE_TIME_WORK = "forOnce"

class PosMainActivity : AppCompatActivity(),SharedPreferences.OnSharedPreferenceChangeListener {

    private var productViewModel: ProductViewModel? = null
    private var productList: ArrayList<Product>? = null
    private var adapter: ProductSearchAdapter? = null
    var currentTheme: Boolean = false
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        currentTheme = sharedPref.getBoolean(getString(R.string.pref_theme_key), false)
        setAppTheme(currentTheme)

        setContentView(R.layout.activity_pos_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar);
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu)
    }
    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.nav_setting ->
                startActivity(Intent(this@PosMainActivity, SettingsActivity::class.java))
          R.id.inventory_prod ->
                startActivity(Intent(this@PosMainActivity, InventroyHomeActivity::class.java))
        }
        return super.onOptionsItemSelected(item);
    }

    fun initView() {

        val mRecyclerView: RecyclerView = findViewById(R.id.rc_report)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.addListDivider()

        val myConstraints = Constraints.Builder()
            //.setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(myConstraints).build()
        WorkManager.getInstance().enqueueUniqueWork(ONE_TIME_WORK, ExistingWorkPolicy.KEEP, syncWorkRequest)
        WorkManager.getInstance().getWorkInfoByIdLiveData(syncWorkRequest.id)
            .observe(this@PosMainActivity, Observer { workInfo ->

            })

        productViewModel = ViewModelProviders.of(this@PosMainActivity).get(ProductViewModel(application)::class.java)
        productList = ArrayList()

        adapter = ProductSearchAdapter(this@PosMainActivity)
        mRecyclerView.adapter = adapter

        productViewModel?.listProductLiveData?.observe(this@PosMainActivity, Observer<List<Product>> {
            productList!!.clear()
            it!!.forEach { obj ->
                productList!!.add(obj)
            }
            adapter!!.setProductList(productList!!)
        })
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val selectedTheme = sharedPref.getBoolean(getString(R.string.pref_theme_key),false)
        setAppTheme(selectedTheme)
        recreate()
    }


    override fun onResume() {
        super.onResume()

    }

    private fun setAppTheme(currentTheme: Boolean) {
        when (currentTheme) {
            true-> setTheme(R.style.Theme_App_Green)
            else -> setTheme(R.style.Theme_App)
        }

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
                holder.etPrice.text = product.productMrp
                holder.etStock.text = product.stockBalance
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
