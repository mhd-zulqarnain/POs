package com.goshoppi.pos.ui.dashboard

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.di2.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jetbrains.anko.find
import timber.log.Timber
import javax.inject.Inject


class AdminSalesFragment : BaseFragment() {

    lateinit var vpSales: ViewPager
    lateinit var tbOptions: TabLayout
    @Inject
    lateinit var productRepository: LocalProductRepository
    val job: Job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)

    override fun layoutRes(): Int {

        return R.layout.fragment_admin_sales
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }


    private fun initView(view: View) {
        vpSales = view.find(R.id.vpSales)
        tbOptions = view.find(R.id.tbOptions)
        val adapter = viewPager(childFragmentManager)
        vpSales.adapter = adapter
        adapter.addFragement(ProfitFragment(), "Profit")
        tbOptions.setupWithViewPager(vpSales)
        vpSales.adapter!!.notifyDataSetChanged()

    }

    class viewPager(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        val titleList = ArrayList<String>()
        val fragmentList = ArrayList<Fragment>()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]

        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragement(fragment: Fragment, title: String) {
            titleList.add(title)
            fragmentList.add(fragment)
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
            try {
                super.restoreState(state, loader);
            } catch (e: NullPointerException) {
              Timber.e("Error :$e")
            }
        }
    }

}
