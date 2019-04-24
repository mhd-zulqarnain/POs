package com.goshoppi.pos.view.customer


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.gson.Gson

import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.model.local.LocalCustomer

private const val CUSTOMER_OBJ = "customerParam"

class CustomerSummeryFragment : BaseFragment() {
    override fun layoutRes(): Int {
        return R.layout.fragment_customer_summery

    }

    var customerParam: String? = null
    var customer: LocalCustomer? = null
    lateinit var btnOrderNum: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customer_summery, container, false)

        arguments?.let {
            customerParam = it.getString(CUSTOMER_OBJ)
        }
        btnOrderNum = view.findViewById(R.id.btnOrderPlaceNum)
        if (customerParam != null) {
            customer = Gson().fromJson(customerParam, LocalCustomer::class.java)
            btnOrderNum.setText(customer!!.name)
        }
        return view
    }

    companion object {
        fun newInstance(param: String) = CustomerSummeryFragment().apply {
            arguments = Bundle().apply {
                putString(CUSTOMER_OBJ, param)
            }
        }
    }

}
