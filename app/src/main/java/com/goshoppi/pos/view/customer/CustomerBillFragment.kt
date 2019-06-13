package com.goshoppi.pos.view.customer


import android.annotation.SuppressLint
import android.content.Intent
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
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.view.customer.viewmodel.SummeryViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import javax.inject.Inject


private const val CUSTOMER_OBJ = "customerParam"
private const val ORDER_OBJ = "orderobject"

class CustomerBillFragment : BaseFragment() {
    override fun layoutRes(): Int {
        return R.layout.fragment_customer_bill
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var summeryViewModel: SummeryViewModel
    var customerParam: String? = null
    var customer: LocalCustomer? = null
    lateinit var rvBill: RecyclerView
    lateinit var lvOrders: LinearLayout
    lateinit var cvNoOrderFound: CardView
    lateinit var tvtotal: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        summeryViewModel = ViewModelProviders.of(baseActivity, viewModelFactory).get(SummeryViewModel::class.java)
        arguments?.let {
            customerParam = it.getString(CUSTOMER_OBJ)
        }

        initView(view)
    }

    @SuppressLint("SetTextI18n")
    private fun initView(view: View) {
        if (customerParam != null) {
            customer = Gson().fromJson(customerParam, LocalCustomer::class.java)
            summeryViewModel.getUserData(customer!!.phone.toString())
        }
        rvBill = view.findViewById(R.id.rvBill)
        lvOrders = view.findViewById(R.id.lvOrders)
        cvNoOrderFound = view.findViewById(R.id.cvNoOrderFound)
        tvtotal = view.findViewById(R.id.tvtotal)
        summeryViewModel.listOfOrdersObservable.observe(this, Observer {
            if (it.size != 0) {
                cvNoOrderFound.visibility = View.GONE
                lvOrders.visibility = View.VISIBLE
                setUpOrderRecyclerView(it as ArrayList<Order>)
            } else {
                cvNoOrderFound.visibility = View.VISIBLE
                lvOrders.visibility = View.GONE
            }
        })

     /*   summeryViewModel.totalTransactionObservable.observe(this, Observer {
            if (it != null) {
                tvtotal.text = "Total:  ${String.format("%.2f AED", it.toDouble())}"
            } else {
                tvtotal.text = "Total:  0 AED"

            }
        })*/
    }

    private fun setUpOrderRecyclerView(list: ArrayList<Order>) {

        rvBill.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity!!)
        rvBill.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_customer_bill)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvOrderNum = mainView.findViewById<TextView>(R.id.tvOrderNum)
                val tvAmount = mainView.findViewById<TextView>(R.id.tvAmount)
                val tvDate = mainView.findViewById<TextView>(R.id.tvDate)
                val tvPaymentStatus = mainView.findViewById<TextView>(R.id.tvPaymentStatus)

                tvOrderNum.text = itemData.orderId.toString()
                tvAmount.text = String.format("%.2f AED", itemData.orderAmount!!.toDouble())
                tvDate.text = itemData.orderDate
                tvPaymentStatus.setText(itemData.paymentStatus)
                mainView.setOnClickListener {
                    val intent = Intent(activity!!, CustomerBillDetailActivity::class.java)
                    val tmp = Gson().toJson(itemData)
                    intent.putExtra(ORDER_OBJ, tmp)
                    startActivity(intent)
                }

            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param: String) = CustomerBillFragment().apply {
            arguments = Bundle().apply {
                putString(CUSTOMER_OBJ, param)
            }
        }
    }

}
