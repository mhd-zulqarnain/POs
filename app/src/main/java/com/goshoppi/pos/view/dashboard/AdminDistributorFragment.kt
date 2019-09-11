package com.goshoppi.pos.view.dashboard


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.model.local.Distributor
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class AdminDistributorFragment:  BaseFragment() {
    @Inject
    lateinit var distributorsRepository: DistributorsRepository
    lateinit var rvDistributor: RecyclerView
    override fun layoutRes(): Int {
        return R.layout.fragment_admin_distributor
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        rvDistributor = view.findViewById(R.id.rvDistributor)

        distributorsRepository.loadAllDistributor().observe(activity!!, Observer {
            if(it!=null){
                setUpDistributorRecyclerView(it as ArrayList<Distributor>)
            }
        })
    }

    private fun setUpDistributorRecyclerView(list: ArrayList<Distributor>) {

        rvDistributor.layoutManager =
            LinearLayoutManager(activity!!)
        rvDistributor.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_row_dasboard_detail_distributor)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvName = mainView.findViewById<TextView>(R.id.tvName)
                val tvPhone = mainView.findViewById<TextView>(R.id.tvPhone)
                val tvDueDate = mainView.findViewById<TextView>(R.id.tvDueDate)
                val tvOutStanding = mainView.findViewById<TextView>(R.id.tvOutStanding)

                tvPhone.text = itemData.phone.toString()
                tvName.text = itemData.name
                tvOutStanding.text = itemData.totalCredit.toString()
                tvDueDate.text = "-"


            }
    }

    private fun setUpDetailDistributorRecyclerView(list: ArrayList<Distributor>) {

        rvDistributor.layoutManager =
            LinearLayoutManager(activity!!)
        rvDistributor.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_row_dasboard_detail_distributor)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvName = mainView.findViewById<TextView>(R.id.tvName)
                val tvPhone = mainView.findViewById<TextView>(R.id.tvPhone)
                val tvDueDate = mainView.findViewById<TextView>(R.id.tvDueDate)
                val tvOutStanding = mainView.findViewById<TextView>(R.id.tvOutStanding)

                tvPhone.text = itemData.phone.toString()
                tvName.text = itemData.name
                tvOutStanding.text = itemData.totalCredit.toString()
                tvDueDate.text = "-"


            }
    }




}
