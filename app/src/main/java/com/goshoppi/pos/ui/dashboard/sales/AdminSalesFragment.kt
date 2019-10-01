package com.goshoppi.pos.ui.dashboard.sales

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
import com.goshoppi.pos.ui.dashboard.ProfitFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jetbrains.anko.find
import timber.log.Timber
import javax.inject.Inject
import android.view.ViewGroup

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
        val adapter =
            viewPager(childFragmentManager)
        vpSales.adapter = adapter
        adapter.addFragement(SalesFragment(), activity!!.getString(R.string.sales))
        adapter.addFragement(CustomerFootfallFragment(),  activity!!.getString(R.string.customer_footfall))
        adapter.addFragement(ProfitFragment(),  activity!!.getString(R.string.category_wise))
        adapter.addFragement(ProfitFragment(),  activity!!.getString(R.string.company_wise))
        tbOptions.setupWithViewPager(vpSales)
        vpSales.adapter!!.notifyDataSetChanged()
        tbOptions.getTabAt(1)!!.select()
        for (i in 0 until tbOptions.getTabCount()) {
            val tab = (tbOptions.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(0, 0, 5, 0)
            tab.requestLayout()
        }
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
