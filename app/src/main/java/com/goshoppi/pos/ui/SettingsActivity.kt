package com.goshoppi.pos.ui
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.goshoppi.pos.R
import com.goshoppi.pos.utils.Constants.*
import kotlinx.android.synthetic.main.activity_settings.*



class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val currentTheme = sharedPref.getString(KEY_CURRENT_THEME, DEFAULT_APP_THEME)
        setAppTheme(currentTheme)

        setContentView(R.layout.activity_settings)
        mintTheme.isChecked = currentTheme == GREEN_THEME
        mintTheme.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked)
                sharedPref.edit().putString(KEY_CURRENT_THEME, GREEN_THEME).apply()
            else
                sharedPref.edit().putString(KEY_CURRENT_THEME, DEFAULT_APP_THEME).apply()
            recreate()
        }
    }
    private fun setAppTheme(currentTheme: String) {
        when (currentTheme) {
            GREEN_THEME -> setTheme(R.style.Theme_App_Green)
            else -> setTheme(R.style.Theme_App)
        }
    }

}