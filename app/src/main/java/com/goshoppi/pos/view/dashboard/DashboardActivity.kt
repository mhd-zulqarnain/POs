package com.goshoppi.pos.view.dashboard

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.creditHistoryRepo.CreditHistoryRepository
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext

class DashboardActivity : BaseActivity(), CoroutineScope {

    val fragmentManager = getSupportFragmentManager();
    val hashResults: HashMap<String, String> = HashMap()

    @Inject
    lateinit var customerRepository: CustomerRepository
    @Inject
    lateinit var creditHistoryRepository: CreditHistoryRepository
    @Inject
    lateinit var distributorsRepository: DistributorsRepository

    lateinit var mJob: Job

    override fun layoutRes(): Int {
        return R.layout.activity_dashboard
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mJob = Job()
        intView()
    }

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    private fun intView() {

        loaddata()

    }

    fun loaddata() = launch {
        val task1 = async(Dispatchers.IO) {
            val tmp= customerRepository.loadNumberofCustomer()
            hashResults.put(getString(R.string.customers),tmp.toString())

        }

        val task2 = async(Dispatchers.IO) {
           val totalCash= creditHistoryRepository.loadTotalPaidHistory()
            hashResults.put(getString(R.string.cash),totalCash.toString())

        }
        val task3 = async(Dispatchers.IO) {
            val totalDistrutors= distributorsRepository.getTotalDistributor()
            hashResults.put(getString(R.string.distributor),totalDistrutors.toString())

        }
        task1.await()
        task2.await()
        task3.await()
        setIconRecyclerView()

    }


    fun setIconRecyclerView() {
        openFragement(OverViewFragment())

        val iconList = ArrayList<MenuItem>()
        iconList.add(MenuItem(getString(R.string.overview), R.drawable.ic_home_white))
        iconList.add(MenuItem(getString(R.string.sales), R.drawable.hand_shake))
        iconList.add(MenuItem(getString(R.string.profit), R.drawable.add_del_boy))
        iconList.add(MenuItem(getString(R.string.cash), R.drawable.coins_white))
        iconList.add(MenuItem(getString(R.string.customers), R.drawable.add_del_boy))
        iconList.add(MenuItem(getString(R.string.stock), R.drawable.add_del_boy))
        iconList.add(MenuItem(getString(R.string.distributor), R.drawable.add_del_boy))
        iconList.add(MenuItem(getString(R.string.reports), R.drawable.add_del_boy))

        val itemViewList: ArrayList<View> = arrayListOf()
        var position = 0
        rvMenuItem.layoutManager = LinearLayoutManager(this@DashboardActivity)
        rvMenuItem.adapter =
            RecyclerViewGeneralAdapter(
                iconList,
                R.layout.single_dashboard_menu_item
            ) { itemData, viewHolder ->
                val mv = viewHolder.itemView
                val icon: ImageView = mv.findViewById(R.id.ivIcon)
                val tvTitle: TextView = mv.findViewById(R.id.tvTitle)
                val tvDes: TextView = mv.findViewById(R.id.tvDes)
                icon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DashboardActivity,
                        itemData.icon!!
                    )
                )
                tvTitle.setText(itemData.name)
                itemViewList.add(viewHolder.itemView)

                /* itemViewList[position].setBackgroundResource(R.color.white)
                 icon.setColorFilter(ContextCompat.getColor(this@DashboardActivity,R.color.bg_color)); // black Tint
                 tvTitle.textColor = Color.GREEN*/
                var clickedFrag = Fragment()

                when (tvTitle.text.toString()) {
                    getString(R.string.overview) -> {
                        clickedFrag = OverViewFragment()

                    }
                    getString(R.string.stock) -> {
                        clickedFrag = OverViewFragment()

                    }
                    getString(R.string.sales) -> {
                        clickedFrag = AdminSalesFragment()


                    }
                    getString(R.string.profit) -> {
                        clickedFrag = ProfitFragment()

                    }
                    getString(R.string.cash) -> {
                        clickedFrag = AdminCashFragment()
                        tvDes.setText(hashResults.get(getString(R.string.cash)))


                    }
                    getString(R.string.customers) -> {
                        clickedFrag = AdminCustomersFragment()
                        tvDes.setText(hashResults.get(getString(R.string.customers)))


                    }
                    getString(R.string.stock) -> {
                        clickedFrag = AdminStockFragment()


                    }
                    getString(R.string.distributor) -> {
                        clickedFrag = AdminDistributorFragment()
                        tvDes.setText(hashResults.get(getString(R.string.distributor)))


                    }
                    getString(R.string.reports) -> {
                        clickedFrag = AdminReportsFragment()


                    }
                }

                viewHolder.itemView.setOnClickListener {
                    position = viewHolder.adapterPosition
                    itemViewList.forEach {
                        /*      if (itemViewList[position] == it) {
                                  it.setBackgroundResource(R.color.white)
                                  icon.setColorFilter(ContextCompat.getColor(this@DashboardActivity,R.color.bg_color)) // black Tint
                                  tvTitle.setTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.light_green))


                              } else {
                                  it.setBackground(ContextCompat.getDrawable(this@DashboardActivity,R.drawable.bg_dashboard_btn))
                                  icon.setColorFilter(ContextCompat.getColor(this@DashboardActivity,R.color.white)) // White Tint

                              }*/

                    }
                    openFragement(clickedFrag)
                }
            }

    }

    fun openFragement(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(
                R.id.frame_container_fragment,
                fragment
            )
            .commit()
    }

    data class MenuItem(var name: String? = null, var icon: Int? = null)
}
