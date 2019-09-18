package com.goshoppi.pos.view.dashboard


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.model.local.PurchaseOrderDetails
import com.goshoppi.pos.view.dashboard.viewmodel.DashboardDistributorViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class AdminDistributorFragment:  BaseFragment() {
    @Inject
    lateinit var distributorsRepository: DistributorsRepository
    @Inject
    lateinit var localProductRepository: LocalProductRepository
   @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var rvDistributor: RecyclerView
    lateinit var rvDetailDistributor: RecyclerView
    lateinit var cvPrdDetails: ConstraintLayout
    lateinit var lvDistrDetails: ConstraintLayout
    lateinit var tvName: TextView
    lateinit var vm: DashboardDistributorViewModel
    lateinit var tvBack: TextView

    val job: Job = Job()
    val scope = CoroutineScope(Dispatchers.Default + job)

    override fun layoutRes(): Int {
        return R.layout.fragment_admin_distributor
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        vm  = ViewModelProviders.of(activity!!,viewModelFactory).get(DashboardDistributorViewModel::class.java)
        rvDistributor = view.findViewById(R.id.rvDistributor)
        rvDetailDistributor = view.findViewById(R.id.rvDetailDistributor)
        lvDistrDetails = view.findViewById(R.id.lvDistrDetails)
        tvName = view.findViewById(R.id.tvName)
        cvPrdDetails = view.findViewById(R.id.cvPrdDetails)
        tvBack = view.findViewById(R.id.tvBack)

        distributorsRepository.loadAllDistributor().observe(activity!!, Observer {
            if(it!=null){
                setUpDistributorRecyclerView(it as ArrayList<Distributor>)
            }
        })

        vm.listOfPurchaseOrder.observe(activity!!, Observer {
            if(it!=null){
                setUpDetailsDistributorRecyclerView(it as ArrayList<PurchaseOrderDetails>)
            }
        })
        tvBack.setOnClickListener {
            cvPrdDetails.visibility = View.GONE
            lvDistrDetails.visibility = View.VISIBLE
            tvName.visibility = View.GONE

        }
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

                mainView.setOnClickListener {
                    vm.getpoInvoiceNumber(itemData.phone)
                    cvPrdDetails.visibility = View.VISIBLE
                    lvDistrDetails.visibility = View.GONE
                }
                getString(R.string.distrutor_detail, itemData.name)
                tvName.visibility = View.VISIBLE
            }
    }

    private fun setUpDetailsDistributorRecyclerView(list: ArrayList<PurchaseOrderDetails>) {

        rvDetailDistributor.layoutManager =
            LinearLayoutManager(activity!!)
        rvDetailDistributor.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_row_dasboard_po_distributor)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvPrdName = mainView.findViewById<TextView>(R.id.tvPrdName)
                val tvOrderDate = mainView.findViewById<TextView>(R.id.tvOrderDate)
                val tvOrderInNum = mainView.findViewById<TextView>(R.id.tvOrderInNum)
                val poInvoiceNumber = mainView.findViewById<TextView>(R.id.tvOrderAmnt)
                val tvstock = mainView.findViewById<TextView>(R.id.tvstock)

                scope.launch {
                    tvPrdName.text = localProductRepository.getProductNameById(itemData.productId!!)
                }
                tvOrderDate.text = itemData.addedDate.toString()
                tvOrderInNum.text = itemData.poInvoiceNumber.toString()
                poInvoiceNumber.text = itemData.poInvoiceNumber.toString()
                poInvoiceNumber.text = itemData.poInvoiceNumber.toString()
                tvstock.text = itemData.productQty.toString()




            }
    }

    override fun onDetach() {
        super.onDetach()

        job.cancel()
    }



}
