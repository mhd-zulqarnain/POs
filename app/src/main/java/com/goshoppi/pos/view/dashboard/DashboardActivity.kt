package com.goshoppi.pos.view.dashboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseActivity
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColor
import java.util.*

class DashboardActivity : BaseActivity() {

   val fragmentManager = getSupportFragmentManager();

    override fun layoutRes(): Int {
        return R.layout.activity_dashboard
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intView()
    }

    private fun intView() {
        setIconRecyclerView()

    }

    fun setIconRecyclerView() {
        openFragement(OverViewFragment())

        val iconList = ArrayList<MenuItem>()
        iconList.add(MenuItem(getString(R.string.overview), R.drawable.add_del_boy))
        iconList.add(MenuItem(getString(R.string.sales), R.drawable.add_del_boy))
        iconList.add(MenuItem(getString(R.string.profit), R.drawable.add_del_boy))
        iconList.add(MenuItem(getString(R.string.cash), R.drawable.add_del_boy))
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
                icon.setImageDrawable(ContextCompat.getDrawable(this@DashboardActivity,itemData.icon!!))
                tvTitle.setText(itemData.name)
                itemViewList.add(viewHolder.itemView)

               /* itemViewList[position].setBackgroundResource(R.color.white)
                icon.setColorFilter(ContextCompat.getColor(this@DashboardActivity,R.color.bg_color)); // black Tint
                tvTitle.textColor = Color.GREEN*/

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
                    when(tvTitle.text.toString()){
                        getString(R.string.overview)->{
                            openFragement(OverViewFragment())
                        }getString(R.string.stock)->{
                        openFragement(OverViewFragment())
                        }
                        getString(R.string.sales)->{
                            openFragement(AdminSalesFragment())

                        }
                        getString(R.string.profit)->{
                            openFragement(ProfitFragment())

                        } getString(R.string.cash)->{
                        openFragement(AdminCashFragment())

                        }
                        getString(R.string.customers)->{
                            openFragement(AdminCustomersFragment())

                        }
                        getString(R.string.stock)->{
                            openFragement(AdminStockFragment())

                        }
                        getString(R.string.distributor)->{
                            openFragement(AdminDistributorFragment())

                        }
                        getString(R.string.reports)->{
                            openFragement(AdminReportsFragment())

                        }
                    }
                }

            }

    }

    fun openFragement(fragment:Fragment){
        fragmentManager.beginTransaction()
            .replace(
                R.id.frame_container_fragment,
                fragment
            )
            .commit()
    }
    data class MenuItem(var name: String? = null,var icon: Int? = null)
}
