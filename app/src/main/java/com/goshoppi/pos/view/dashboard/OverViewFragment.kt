package com.goshoppi.pos.view.dashboard


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.budiyev.android.circularprogressbar.CircularProgressBar
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.creditHistoryRepo.CreditHistoryRepository
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.utils.Constants
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import javax.inject.Inject


class OverViewFragment : BaseFragment() {

    @Inject
    lateinit var distributorsRepository: DistributorsRepository
    @Inject
    lateinit var customerRepository: CustomerRepository
 @Inject
    lateinit var creditHistoryRepository: CreditHistoryRepository

    lateinit var rvDistributor: RecyclerView
    lateinit var rvCustomers: RecyclerView
    lateinit var tvPaymentReceived: TextView
    lateinit var tvCredit: TextView
    lateinit var tvCash: TextView
    lateinit var tvTotalSales: TextView
    lateinit var tvTodaySales: TextView
    lateinit var progress_delivery: CircularProgressBar
    val job: Job = Job()
    val scope = CoroutineScope(Dispatchers.Default + job)

    override fun layoutRes(): Int {
        return R.layout.fragment_over_view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        rvDistributor = view.findViewById(R.id.rvDistributor)
        tvTodaySales = view.findViewById(R.id.tvTodaySales)
        tvPaymentReceived = view.findViewById(R.id.tvPaymentReceived)
        tvCash = view.findViewById(R.id.tvCash)
        tvCredit = view.findViewById(R.id.tvCredit)
        tvTotalSales = view.findViewById(R.id.tvTotalSales)
        rvCustomers = view.findViewById(R.id.rvCustomers)
        progress_delivery = view.findViewById(R.id.progress_delivery)
        distributorsRepository.loadAllDistributor().observe(activity!!, Observer {
            if(it!=null){
                setUpDistributorRecyclerView(it as ArrayList<Distributor>)
            }
        })
        customerRepository.loadAllLocalCustomer().observe(activity!!, Observer {
            if(it!=null){
                setUpCustomerRecyclerView(it as ArrayList<LocalCustomer>)
            }
        })
        progress_delivery.setProgress(30f);
        loadData()
    }

    private fun loadData() = scope.async {

        async(Dispatchers.IO) {
            val totalCash= creditHistoryRepository.loadTotalPaidHistory()
            val totalCredit= creditHistoryRepository.loadTotalCredit()
            val totalSales= creditHistoryRepository.totalSales()
            tvPaymentReceived.text = String.format("%.2f AED", totalCash)
            tvCash.text = tvCash.text.toString()+": "+String.format("%.2f ", totalCash)
            tvCredit.text = tvCredit.text.toString()+": "+String.format("%.2f", totalCredit)
            tvTotalSales.text = String.format("%.2f AED", totalSales)

        }

    }
    private fun setUpDistributorRecyclerView(list: ArrayList<Distributor>) {

        rvDistributor.layoutManager =
            LinearLayoutManager(activity!!)
        rvDistributor.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_overview_distributor)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvName = mainView.findViewById<TextView>(R.id.tvName)
                val tvDebt = mainView.findViewById<TextView>(R.id.tvDue)
                val tvDueDate = mainView.findViewById<TextView>(R.id.tvDueDate)

                tvDebt.text = String.format("%.2f AED", itemData.totalCredit)
                tvName.text = itemData.name


            }
    }
    private fun setUpCustomerRecyclerView(list: ArrayList<LocalCustomer>) {

        rvCustomers.layoutManager =
            LinearLayoutManager(activity!!)
        rvCustomers.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_overview_customer)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvName = mainView.findViewById<TextView>(R.id.tvName)
                val tvDue = mainView.findViewById<TextView>(R.id.tvDue)
                val btnRemind = mainView.findViewById<Button>(R.id.btnRemind)
                tvDue.text = String.format("%.2f", itemData.totalCredit)
                tvName.setText(itemData.name)

            }
    }


}
