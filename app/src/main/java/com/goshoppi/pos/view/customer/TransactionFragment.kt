package com.goshoppi.pos.view.customer



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
import com.goshoppi.pos.model.local.CreditHistory
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.view.customer.viewmodel.TransactionViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import javax.inject.Inject


private const val CUSTOMER_OBJ = "customerParam"

class TransactionFragment : BaseFragment() {
    override fun layoutRes(): Int {
        return R.layout.fragment_customer_transaction
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var transactionViewModel: TransactionViewModel
    var customerParam: String? = null
    var customer: LocalCustomer? = null
    lateinit var rvBill: RecyclerView
    lateinit var lvOrders: LinearLayout
    lateinit var cvNoOrderFound: CardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionViewModel = ViewModelProviders.of(baseActivity, viewModelFactory).get(TransactionViewModel::class.java)
        arguments?.let {
            customerParam = it.getString(CUSTOMER_OBJ)
        }

        initView(view)
    }

    private fun initView(view: View) {
        if (customerParam != null) {
            customer = Gson().fromJson(customerParam, LocalCustomer::class.java)
            transactionViewModel.getUserData(customer!!.phone.toString()).observe(this, Observer {
                if (it.size != 0) {
                    cvNoOrderFound.visibility = View.GONE
                    lvOrders.visibility = View.VISIBLE
                    setUpOrderRecyclerView(it as ArrayList<CreditHistory>)
                } else {
                    cvNoOrderFound.visibility = View.VISIBLE
                    lvOrders.visibility = View.GONE
                }
            })
        }
        rvBill = view.findViewById(R.id.rvBill)
        lvOrders = view.findViewById(R.id.lvOrders)
        cvNoOrderFound = view.findViewById(R.id.cvNoOrderFound)
        /*transactionViewModel.listOfCreditHistoryObservable.observe(this, Observer {
            if (it.size != 0) {
                cvNoOrderFound.visibility = View.GONE
                lvOrders.visibility = View.VISIBLE
                setUpOrderRecyclerView(it as ArrayList<CreditHistory>)
            } else {
                cvNoOrderFound.visibility = View.VISIBLE
                lvOrders.visibility = View.GONE
            }
        })*/

    }

    private fun setUpOrderRecyclerView(list: ArrayList<CreditHistory>) {

        rvBill.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity!!)
        rvBill.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_customer_transaction)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvDate = mainView.findViewById<TextView>(R.id.tvDate)
                val tvAmount = mainView.findViewById<TextView>(R.id.tvAmount)
                val tvTotalCredit = mainView.findViewById<TextView>(R.id.tvTotalCredit)
                val tvStatus = mainView.findViewById<TextView>(R.id.tvStatus)

                tvTotalCredit.text = String.format("%.2f AED", itemData.totalCreditAmount!!.toDouble())
                /*
                * Payment type considered by paid amount*/
                if(itemData.paidAmount!!.toDouble()<1){
                    tvAmount.text = String.format("%.2f AED", itemData.creditAmount!!.toDouble())
                    tvStatus.text = Constants.CREDIT

                }else{
                    tvAmount.text = String.format("%.2f AED", itemData.paidAmount!!.toDouble())
                    tvStatus.text = Constants.PAID

                }
                tvDate.text =itemData.transcationDate.toString()

            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param: String) = TransactionFragment().apply {
            arguments = Bundle().apply {
                putString(CUSTOMER_OBJ, param)
            }
        }
    }

}
