package com.goshoppi.pos.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import androidx.work.*
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.workmanager.*
import com.goshoppi.pos.utils.Constants.CHANNEL_ID
import com.goshoppi.pos.utils.Constants.MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY
import com.goshoppi.pos.view.inventory.InventroyHomeActivity
import kotlinx.android.synthetic.main.activity_pos_main.*
import timber.log.Timber
import com.goshoppi.pos.utils.TinyDB


private const val ONE_TIME_WORK = "forOnce"

class PosMainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var currentTheme: Boolean = false
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
        menuInflater.inflate(R.menu.menu, menu)
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
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {

        val tinyDb = TinyDB(this@PosMainActivity)

        if (!tinyDb.getBoolean(MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY)) {

            val myConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(myConstraints).build()
            val storeProductImageWorker = OneTimeWorkRequestBuilder<StoreProductImageWorker>().setConstraints(myConstraints).build()
            val storeVariantImageWorker = OneTimeWorkRequestBuilder<StoreVariantImageWorker>().setConstraints(myConstraints).build()
            WorkManager.getInstance().beginUniqueWork(ONE_TIME_WORK, ExistingWorkPolicy.KEEP, syncWorkRequest)
                .then(storeProductImageWorker)
                .then(storeVariantImageWorker)
                .enqueue()

            WorkManager.getInstance().getWorkInfoByIdLiveData(storeVariantImageWorker.id)
                .observe(this@PosMainActivity, Observer { workInfo ->
                    if (workInfo?.state!!.isFinished && workInfo.state == WorkInfo.State.SUCCEEDED) {
                        tinyDb.putBoolean(MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY, true)
                        createNotificationChannel()
                    }
                })
        }else{
            Timber.e("No Need To Start Worker Because Master is Already In Terminal")
        }

        cvInventory.setOnClickListener {
            startActivity(Intent(this@PosMainActivity, InventroyHomeActivity::class.java))
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Sync Master Database"
            val descriptionText = "Syncing Master Database in Terminal is completed"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, PosMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Sync Master Database")
            .setContentText("Syncing Master Database in Terminal is completed")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
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

}
