package com.goshoppi.pos.ui.auth

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.goshoppi.pos.R
import com.goshoppi.pos.utils.Constants.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {

    var currentTheme: String = ""
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        currentTheme = sharedPref.getString(KEY_CURRENT_THEME, GREEN_THEME)
        setAppTheme(currentTheme)

        setContentView(R.layout.activity_login)
        setupViewPager(tabViewPager)

    }

    private fun setupViewPager(viewPager: ViewPager) {

        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFrag(SalesAuthFragment(), "Sales")
        adapter.addFrag(AdminAuthFragment(), "Admin")
        adapter.addFrag(SalesAuthFragment(), "Procurement")
        viewPager.adapter = adapter

        tbOptions.setupWithViewPager(viewPager)

    }

    override fun onResume() {
        super.onResume()
        val selectedTheme = sharedPref.getString(KEY_CURRENT_THEME, GREEN_THEME)
        if(selectedTheme!=null)
        if (currentTheme != selectedTheme)
            recreate()
    }

    private fun setAppTheme(currentTheme: String) {
        when (currentTheme) {
            GREEN_THEME -> setTheme(R.style.Theme_App_Green)
            else -> setTheme(R.style.Theme_App)
        }
    }

    class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFrag(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

}
