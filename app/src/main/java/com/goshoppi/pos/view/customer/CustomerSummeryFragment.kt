package com.goshoppi.pos.view.customer


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.gson.Gson

import com.goshoppi.pos.R
import com.goshoppi.pos.model.local.LocalCustomer

private const val CUSTOMER_OBJ = "customerParam"

class CustomerSummeryFragment : Fragment() {
    var customerParam: String? = null
    var customer: LocalCustomer? = null
    lateinit var buttonTitle: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customer_summery, container, false)

        arguments?.let {
            customerParam = it.getString(CUSTOMER_OBJ)
        }
        buttonTitle = view.findViewById(R.id.buttonTitle)
        if (customerParam != null) {
            customer = Gson().fromJson(customerParam, LocalCustomer::class.java)
            buttonTitle.setText(customer!!.name)
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
