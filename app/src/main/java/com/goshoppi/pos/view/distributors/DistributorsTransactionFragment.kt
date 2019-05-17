package com.goshoppi.pos.view.distributors

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.model.local.PoHistory
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.view.distributors.viewmodel.DistributorTransactionViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import javax.inject.Inject


private const val CUSTOMER_OBJ = "customerParam"

class DistributorsTransactionFragment : BaseFragment() {
    override fun layoutRes(): Int {
        return R.layout.fragment_distributor_transaction
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var distributorTransactionViewModel: DistributorTransactionViewModel
    var customerParam: String? = null
    var customer: LocalCustomer? = null
    lateinit var rvBill: RecyclerView
    lateinit var lvOrders: LinearLayout
    lateinit var cvNoOrderFound: CardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        distributorTransactionViewModel = ViewModelProviders.of(baseActivity, viewModelFactory).get(DistributorTransactionViewModel::class.java)
        arguments?.let {
            customerParam = it.getString(CUSTOMER_OBJ)
        }

        initView(view)
    }

    private fun initView(view: View) {
        if (customerParam != null) {
            customer = Gson().fromJson(customerParam, LocalCustomer::class.java)
            distributorTransactionViewModel.getUserData(customer!!.phone.toString())
        }
        rvBill = view.findViewById(R.id.rvBill)
        lvOrders = view.findViewById(R.id.lvOrders)
        cvNoOrderFound = view.findViewById(R.id.cvNoOrderFound)
        distributorTransactionViewModel.listOfCreditHistoryObservable.observe(this, Observer {
            if (it.size != 0) {
                cvNoOrderFound.visibility = View.GONE
                lvOrders.visibility = View.VISIBLE
                setUpOrderRecyclerView(it as ArrayList<PoHistory>)
            } else {
                cvNoOrderFound.visibility = View.VISIBLE
                lvOrders.visibility = View.GONE
            }
        })

    }

    private fun setUpOrderRecyclerView(list: ArrayList<PoHistory>) {

        rvBill.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity!!)
        rvBill.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_customer_transaction)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvDate = mainView.findViewById<TextView>(R.id.tvDate)
                val tvAmount = mainView.findViewById<TextView>(R.id.tvAmount)
                val tvTotalCredit = mainView.findViewById<TextView>(R.id.tvTotalCredit)
                val tvStatus = mainView.findViewById<TextView>(R.id.tvStatus)

                tvTotalCredit.text = itemData.poId.toString()
                if(itemData.paidAmount!!.toDouble()<1){
                    tvAmount.text = String.format("%.2f AED", itemData.creditAmount!!.toDouble())
                    tvStatus.setText(Constants.CREDIT)

                }else{
                    tvAmount.text = String.format("%.2f AED", itemData.paidAmount!!.toDouble())
                    tvStatus.setText(Constants.PAID)

                }
                tvDate.text =itemData.transcationDate.toString()

            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param: String) = DistributorsTransactionFragment().apply {
            arguments = Bundle().apply {
                putString(CUSTOMER_OBJ, param)
            }
        }
    }

}
