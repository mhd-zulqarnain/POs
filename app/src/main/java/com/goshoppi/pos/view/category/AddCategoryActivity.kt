package com.goshoppi.pos.view.category

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class AddCategoryActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope,
    View.OnClickListener {
    private lateinit var sharedPref: SharedPreferences

    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_add_category
    }

    fun initView() {

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals(getString(R.string.pref_app_theme_color_key))) {
            setAppTheme(sharedPreferences!!)
            recreate()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main

    override fun onClick(v: View?) {
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

    class CategoryViewPager(manager: FragmentManager, var ctx: Context) :
        FragmentStatePagerAdapter(manager) {

        override fun getItem(position: Int): Fragment {

                if(position==0)
                    return FragmentCategory()
                else
                    return SubCategoryFragment()


        }

        override fun getCount(): Int {
        return  2}

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 ->
                    return ctx.getString(R.string.category)
                1 ->
                    return ctx.getString(R.string.subcategory)
            }
            return ""

        }
    }
}
