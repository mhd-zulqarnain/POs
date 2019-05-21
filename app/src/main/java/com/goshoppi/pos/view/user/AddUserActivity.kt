package com.goshoppi.pos.view.user

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.userRepo.UserRepository
import com.goshoppi.pos.di2.base.BaseActivity

import com.goshoppi.pos.model.User
import com.goshoppi.pos.utils.SharedPrefs
import com.goshoppi.pos.utils.Utils
import kotlinx.android.synthetic.main.activity_add_user.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AddUserActivity : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope{

    lateinit var mJob: Job
    @Inject
    lateinit var userRepository: UserRepository

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_add_user
    }

    private lateinit var sharedPref: SharedPreferences
    val user = User()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        mJob=Job()
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btn_add.setOnClickListener {
            adduser()
        }

        val temp  =SharedPrefs.getInstance()!!.getUser(this)
        et_store_code.setText(temp!!.storeCode)
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
        user.isSales=true
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
        launch {
            userRepository.insertUser(user)

        }

        Utils.showMsgShortIntervel(this,"User added successfully")
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                this@AddUserActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
