package com.goshoppi.pos.ui.customer


import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson

import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.ui.customer.viewmodel.SummeryViewModel
import javax.inject.Inject

private const val CUSTOMER_OBJ = "customerParam"

class CustomerSummeryFragment : BaseFragment() {
    override fun layoutRes(): Int {
        return R.layout.fragment_customer_summery
    }

    @Inject
    lateinit var viewModelFactory : ViewModelFactory
    lateinit var summeryViewModel: SummeryViewModel

    var customerParam: String? = null
    var customer: LocalCustomer? = null
    lateinit var btnOrderNum: Button
    lateinit var btnTransaction: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        summeryViewModel = ViewModelProviders.of(baseActivity ,viewModelFactory).get(SummeryViewModel::class.java)

        arguments?.let {
            customerParam = it.getString(CUSTOMER_OBJ)
        }
        initView(view)
    }
    private fun initView(view: View) {
        if(customerParam!=null){
            customer = Gson().fromJson(customerParam,LocalCustomer::class.java)
            summeryViewModel.getUserData(customer!!.phone.toString())
        }
        btnOrderNum = view.findViewById(R.id.btnOrderPlaceNum)
        btnTransaction = view.findViewById(R.id.btnTransaction)
        summeryViewModel.totalOrderObservable.observe(this, Observer{
            if(it!=null){
                btnOrderNum.text= "Order placed \n \n $it"
            }else{
                btnOrderNum.text= "Order placed \n \n 0"

            }
        })
        summeryViewModel.totalTransactionObservable.observe(this, Observer{
            if(it!=null){
                btnTransaction.text= "${Math.ceil(it.toDouble()/12)} AED \n \n  Average purchaser \n per month  "
            }else{
                btnTransaction.text= "0 AED \n \n  Average purchaser \n per month  "

            }
        })

    }

    companion object {
        fun newInstance(param: String) = CustomerSummeryFragment().apply {
            arguments = Bundle().apply {
                putString(CUSTOMER_OBJ, param)
            }
        }
    }


}
