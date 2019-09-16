package com.goshoppi.pos.view.settings


import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.view.home.PosMainActivity
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    View.OnClickListener {
    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
    return R.layout.activity_settings
    }


    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }
    private fun  initView(){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        tvDevice.setOnClickListener(this)
        tvOther.setOnClickListener(this)
        tvUserManage.setOnClickListener(this)
        openFragment(DeviceSettingFragment())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            toolbar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_toolbar_color) )
        } else{
            toolbar.setBackgroundResource(R.color.colorPrimaryLight)
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
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

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                startActivity(Intent(this@SettingsActivity,
                    PosMainActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvDevice -> {

                tvDevice.setBackgroundColor(Color.WHITE)
                tvDevice.setTextColor(ContextCompat.getColor(this, R.color.bg_color))
                tvOther.setBackgroundColor(ContextCompat.getColor(this, R.color.text_vvvlight_gry))
                tvOther.setTextColor(Color.BLACK)
                tvUserManage.setBackgroundColor(ContextCompat.getColor(this, R.color.text_vvvlight_gry))
                tvUserManage.setTextColor(Color.BLACK)
            openFragment(DeviceSettingFragment())
            }
            R.id.tvOther -> {
                tvOther.setBackgroundColor(Color.WHITE)
                tvOther.setTextColor(ContextCompat.getColor(this, R.color.bg_color))
                tvDevice.setBackgroundColor(ContextCompat.getColor(this, R.color.text_vvvlight_gry))
                tvDevice.setTextColor(Color.BLACK)
                tvUserManage.setBackgroundColor(ContextCompat.getColor(this, R.color.text_vvvlight_gry))
                tvUserManage.setTextColor(Color.BLACK)
            openFragment(AddUserFragment())

            }
            R.id.tvUserManage -> {
                tvUserManage.setBackgroundColor(Color.WHITE)
                tvUserManage.setTextColor(ContextCompat.getColor(this, R.color.bg_color))
                tvDevice.setBackgroundColor(ContextCompat.getColor(this, R.color.text_vvvlight_gry))
                tvDevice.setTextColor(Color.BLACK)
                tvOther.setBackgroundColor(ContextCompat.getColor(this, R.color.text_vvvlight_gry))
                tvOther.setTextColor(Color.BLACK)
            openFragment(UserManagmentFragment())

            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        Utils.hideSoftKeyboard(this@SettingsActivity)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.hostRootFrame, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@SettingsActivity, PosMainActivity::class.java))
        finish()
    }
}