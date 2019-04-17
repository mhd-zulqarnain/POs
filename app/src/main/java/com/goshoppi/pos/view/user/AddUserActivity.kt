package com.goshoppi.pos.view.user

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.userRepo.UserRepository
import com.goshoppi.pos.di2.base.BaseActivity

import com.goshoppi.pos.model.User
import com.goshoppi.pos.utils.Utils
import kotlinx.android.synthetic.main.activity_add_user.*
import javax.inject.Inject

class AddUserActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun layoutRes(): Int {
        return R.layout.activity_add_user
    }

    private lateinit var sharedPref: SharedPreferences
    @Inject
    lateinit var userRepository: UserRepository
    val user = User()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .roomModule(RoomModule(application))
            .build()
            .injectAddUserActivity(this)*/

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        sharedPref.registerOnSharedPreferenceChangeListener(this)

        //setContentView(R.layout.activity_add_user)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btn_add.setOnClickListener {
            adduser()
        }

        cbAdmin.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                user.isAdmin = isChecked
            }
        })
        cbProc.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                user.isProcurement = isChecked
            }
        })
        cbSales.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                user.isSales = isChecked
            }
        })
    }
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key.equals(getString(R.string.pref_app_theme_color_key))) {
            setAppTheme(sharedPreferences)
            recreate()
        }
    }

    fun adduser() {

        val storeCode = et_store_code!!.getText().toString()
        val userCode = et_user_code!!.getText().toString()
        val password = et_password!!.getText().toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(storeCode)) {
            et_store_code!!.setError(getString(R.string.err_not_empty))
            focusView = et_store_code
            cancel = true
        }

        if (TextUtils.isEmpty(userCode)) {
            et_user_code!!.setError(getString(R.string.err_invalid_entry))
            focusView = et_user_code
            cancel = true
        }
        if (TextUtils.isEmpty(password)) {
            et_password!!.setError(getString(R.string.err_invalid_entry))
            focusView = et_password
            cancel = true
        }
        if (cancel) run {
            focusView!!.requestFocus()
            return
        }




        user.userCode = userCode
        user.storeCode = storeCode
        user.password = password

        user.updatedAt = System.currentTimeMillis().toString()
        userRepository.insertUser(user)

        Utils.showMsg(this,"User added successfully")
        this.finish()
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
}
