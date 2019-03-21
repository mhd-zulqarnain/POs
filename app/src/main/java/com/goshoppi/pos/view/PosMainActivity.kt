package com.goshoppi.pos.view

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
import com.goshoppi.pos.architecture.workmanager.*
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.view.inventory.InventroyHomeActivity
import com.ishaquehassan.recyclerviewgeneraladapter.addListDivider
import kotlinx.android.synthetic.main.activity_pos_main.*


private const val TAG = "PosMainActivity"
private const val ONE_TIME_WORK = "forOnce"

class PosMainActivity : AppCompatActivity(),SharedPreferences.OnSharedPreferenceChangeListener {

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
        setSupportActionBar(toolbar)
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_setting ->
                startActivity(Intent(this@PosMainActivity, SettingsActivity::class.java))
          R.id.inventory_prod ->
                startActivity(Intent(this@PosMainActivity, InventroyHomeActivity::class.java))
        }
        return super.onOptionsItemSelected(item);
    }

    fun initView() {


        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(myConstraints).build()
        val storeProductImageWorker = OneTimeWorkRequestBuilder<StoreProductImageWorker>().setConstraints(myConstraints).build()
        val storeVaraintImageWorker = OneTimeWorkRequestBuilder<StoreVaraintImageWorker>().setConstraints(myConstraints).build()
//        WorkManager.getInstance().enqueueUniqueWork(ONE_TIME_WORK, ExistingWorkPolicy.KEEP, syncWorkRequest)
         WorkManager.getInstance().beginUniqueWork(ONE_TIME_WORK, ExistingWorkPolicy.KEEP, syncWorkRequest)
            .then(storeProductImageWorker)
            .then(storeVaraintImageWorker)
            .enqueue()

        cvInventory.setOnClickListener{
            startActivity(Intent(this@PosMainActivity, InventroyHomeActivity::class.java))
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val selectedTheme = sharedPref.getBoolean(getString(R.string.pref_theme_key),false)
        setAppTheme(selectedTheme)
        recreate()
    }

    private fun setAppTheme(currentTheme: Boolean) {
        when (currentTheme) {
            true-> setTheme(R.style.Theme_App_Green)
            else -> setTheme(R.style.Theme_App)
        }

    }

}
