package com.goshoppi.pos.ui.category

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.textfield.TextInputEditText
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.model.StoreCategory
import com.goshoppi.pos.model.SubCategory
import com.goshoppi.pos.utils.UiHelper
import com.goshoppi.pos.utils.Utils
import kotlinx.android.synthetic.main.activity_add_category.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class AddCategoryActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener,
    View.OnClickListener, CoroutineScope {
    @Inject
    lateinit var localProductRepository: LocalProductRepository
    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var sharedPref: SharedPreferences

    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_add_category
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }


    fun initView() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.gradient_toolbar_color
                )
            )
        } else {
            toolbar.setBackgroundResource(R.color.colorPrimaryDark)
        }

        val adapter = CategoryViewPager(supportFragmentManager, this@AddCategoryActivity)
        vpCategory.adapter = adapter
        tbOptions.setupWithViewPager(vpCategory)
        adapter.notifyDataSetChanged()

        btnAdd.setOnClickListener {
            showAddDialog(this@AddCategoryActivity)
        }

    }

    private fun showAddDialog(ctx: Context) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val alertBox = AlertDialog.Builder(this)
        alertBox.setView(view)
        alertBox.setCancelable(true)
        val dialog = alertBox.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val edCategory: TextInputEditText = view.findViewById(R.id.edCategory)
        val edName: TextInputEditText = view.findViewById(R.id.edName)
        val spCategory: Spinner = view.findViewById(R.id.spCategory)
        val btnSave: Button = view.findViewById(R.id.btnSave)
        val btnClose: ImageView = view.findViewById(R.id.btn_close_dialog)
        var isCategory = true
        var catId =1L
        spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    isCategory = true
                    edCategory.visibility = View.GONE
                    catId == 1L
                } else {
                    edCategory.visibility = View.VISIBLE
                    isCategory = false
                }
            }
        }
        launch {
            val categories = localProductRepository.loadStoreCategoryMain()
            val lst = ArrayList<String>()
            for (item in categories)
                lst.add(item.categoryName!!)
            UiHelper.setupFloatingSpinner(
                edCategory, lst, "",
                { selectedItem, selectedIndex ->
                    val item = selectedItem as String
                    categories.forEach { obj ->
                        if (obj.categoryName == item) {
                            /* categoryId = obj.categoryId!!
                             categoryName = item
                             subCategoryId = -1*/
                            catId = obj.categoryId!!
                            return@forEach
                        }
                    }
                }, ctx
            )

        }


        dialog.setOnDismissListener {

        }

        btnSave.setOnClickListener {

            if (edName.text!!.toString().trim().isEmpty()) {
                edName.error = getString(R.string.err_not_empty)
                return@setOnClickListener
            }
            if (!isCategory && catId == 1L) {
                Utils.showMsg(this@AddCategoryActivity, getString(R.string.err_category))
                return@setOnClickListener
            }
            if (isCategory) {
                val cat: StoreCategory = StoreCategory(
                    System.currentTimeMillis()
                    , edName.text!!.toString(), "", ""
                )
                launch {
                    localProductRepository.insertStoreCategoryMain(cat)

                }
            }else {
                val subCat: SubCategory = SubCategory(
                    System.currentTimeMillis()
                    , catId
                    , edName.text!!.toString(), "", ""
                )
                launch {
                    localProductRepository.insertSubCategoryMain(subCat)

                }
            }

            dialog.dismiss()
        }

        btnClose.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals(getString(R.string.pref_app_theme_color_key))) {
            setAppTheme(sharedPreferences!!)
            recreate()
        }
    }

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

            if (position == 0)
                return CategoryFragment()
            else
                return SubCategoryFragment()

        }

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 ->
                    return ctx.getString(R.string.category)
                1 ->
                    return ctx.getString(R.string.subcategory)
            }
            return "test"

        }
    }
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
