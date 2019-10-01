package com.goshoppi.pos.ui.dashboard


import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.creditHistoryRepo.CreditHistoryRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.ui.dashboard.viewmodel.DashboardCustomerViewModel
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class AdminCustomersFragment : BaseFragment() {
    lateinit var rvCustomer: RecyclerView
    lateinit var tvCount: TextView
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var vmCustomer: DashboardCustomerViewModel
    @Inject
    lateinit var creditHistoryRepository: CreditHistoryRepository
    var job: Job ?=null
    lateinit var scope:CoroutineScope

    override fun layoutRes(): Int {

        return R.layout.fragment_admin_customers
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = Job()
        scope = CoroutineScope(Dispatchers.Main + job!!)
        initView(view)
    }

    private fun initView(view: View) {
        rvCustomer = view.find(R.id.rvCustomer)
        tvCount = view.find(R.id.tvCount)
        vmCustomer = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(DashboardCustomerViewModel::class.java)
        vmCustomer.listOfCustomer.observe(activity!!, Observer {
            if (it != null)
                setUpDistributorRecyclerView(it as ArrayList<LocalCustomer>)
        })

    }

    private fun setUpDistributorRecyclerView(list: ArrayList<LocalCustomer>) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)

        val filtered = list.filter { s -> s.name != Constants.ANONYMOUS }
        val format = Utils.dateFormat

        val currentMonthStart = format.parse("${cal.get(Calendar.MONTH) + 1}/1/$year")
        val currentMonthEnd = format.parse("${cal.get(Calendar.MONTH) + 1}/31/$year")

        val lastMonthStart = format.parse("${cal.get(Calendar.MONTH)}/1/$year")
        val lastMonthEnd = format.parse("${cal.get(Calendar.MONTH)}/31/$year")

        tvCount.setText("Count: ${filtered.size.toString()}")
        rvCustomer.layoutManager =
            LinearLayoutManager(activity!!)
        rvCustomer.adapter =
            RecyclerViewGeneralAdapter(
                filtered as ArrayList<LocalCustomer>,
                R.layout.single_row_dasboard_detail_customer
            )
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvName = mainView.findViewById<TextView>(R.id.tvName)
                val tvPhone = mainView.findViewById<TextView>(R.id.tvPhone)
                val tvAddress = mainView.findViewById<TextView>(R.id.tvAddress)
                val tvCurrent = mainView.findViewById<TextView>(R.id.tvCurrent)
                val tvPrev = mainView.findViewById<TextView>(R.id.tvPrev)
                val tvCredit = mainView.findViewById<TextView>(R.id.tvCredit)

                tvPhone.text = itemData.phone.toString()
                tvName.text = itemData.name!!.toUpperCase()
                tvAddress.text = itemData.address
                tvCredit.text = itemData.totalCredit.toString()
                tvPrev.text = "-"

                scope.launch {
                    val currentMothSales = creditHistoryRepository.getMonthlyPurchaseByCustomerId(
                        itemData.phone.toString(),
                        currentMonthStart
                        , currentMonthEnd
                    )
                    val lastMonthEndSales = creditHistoryRepository.getMonthlyPurchaseByCustomerId(
                        itemData.phone.toString(),
                        lastMonthStart,
                        lastMonthEnd
                    )

                    tvCurrent.text = String.format("%.2f AED", currentMothSales)
                    tvPrev.text = String.format("%.2f AED", lastMonthEndSales)

                    /* withContext(Dispatchers.Main){
                         tvCurrent.text = String.format("%.2f AED", currentMothSales)
                         tvPrev.text = String.format("%.2f AED", lastMonthEndSales)

                     }*/

                }

            }
    }

    override fun onDetach() {
        if(job!=null) job!!.cancel()
        super.onDetach()


    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
//        job.start()
    }

}
