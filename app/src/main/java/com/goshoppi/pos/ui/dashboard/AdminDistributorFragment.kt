package com.goshoppi.pos.ui.dashboard


import android.os.Bundle
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
import com.goshoppi.pos.ui.dashboard.viewmodel.DashboardDistributorViewModel
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import javax.inject.Inject

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
    lateinit var tvDistributorName: TextView
    lateinit var vm: DashboardDistributorViewModel
    lateinit var tvBack: TextView
    var job: Job ?=null
    lateinit var scope:CoroutineScope

    override fun layoutRes(): Int {
            return R.layout.fragment_admin_distributor
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = Job()
        scope = CoroutineScope(Dispatchers.Main + job!!)
        vm  = ViewModelProviders.of(this,viewModelFactory).get(DashboardDistributorViewModel::class.java)

        initView(view)
    }

    private fun initView(view: View) {
        rvDistributor = view.findViewById(R.id.rvDistributor)
        rvDetailDistributor = view.findViewById(R.id.rvDetailDistributor)
        lvDistrDetails = view.findViewById(R.id.lvDistrDetails)
        tvDistributorName = view.findViewById(R.id.tvDistributorName)
        cvPrdDetails = view.findViewById(R.id.cvPrdDetails)
        tvBack = view.findViewById(R.id.tvBack)

        distributorsRepository.loadAllDistributor().observe(viewLifecycleOwner, Observer {
            if(it!=null){
                setUpDistributorRecyclerView(it as ArrayList<Distributor>)
            }
        })

        vm.listOfPurchaseOrder.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                setUpDetailsDistributorRecyclerView(it as ArrayList<PurchaseOrderDetails>)
            }
        })
        tvBack.setOnClickListener {
            cvPrdDetails.visibility = View.GONE
            lvDistrDetails.visibility = View.VISIBLE
            tvDistributorName.visibility = View.GONE
            tvBack.visibility = View.GONE

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
                tvName.text = itemData.name!!.toUpperCase()
                tvOutStanding.text = itemData.totalCredit.toString()
                tvDueDate.text = "-"

                mainView.setOnClickListener {
                    vm.getpoInvoiceNumber(itemData.phone)
                    cvPrdDetails.visibility = View.VISIBLE
                    lvDistrDetails.visibility = View.GONE
                    tvBack.visibility = View.VISIBLE
                    tvDistributorName.visibility = View.VISIBLE

                }

                tvDistributorName.text = getString(R.string.distrutor_detail, itemData.name)
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
                    var name ="     "
                    withContext(IO){
                        name=  localProductRepository.getProductNameById(itemData.productId!!)
                    }
                    tvPrdName.text =name
                }
                tvOrderDate.text = Utils.getShortDate(itemData.addedDate!!)
                tvOrderInNum.text = itemData.poInvoiceNumber.toString()
                poInvoiceNumber.text = itemData.poInvoiceNumber.toString()
                poInvoiceNumber.text = itemData.poInvoiceNumber.toString()
                tvstock.text = itemData.productQty.toString()




            }
    }

    override fun onDetach() {
        super.onDetach()
        if(job!=null) job!!.cancel()

    }


}
