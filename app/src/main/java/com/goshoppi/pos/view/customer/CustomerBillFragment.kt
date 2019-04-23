package com.goshoppi.pos.view.customer


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.model.local.LocalCustomer


private const val CUSTOMER_OBJ = "customerParam"

class CustomerBillFragment : androidx.fragment.app.Fragment() {

    var customerParam: String? = null
    var customer :LocalCustomer?=null
    lateinit var tvTitle:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_customer_bill, container, false)

        arguments?.let {
            customerParam = it.getString(CUSTOMER_OBJ)
        }
        tvTitle =view.findViewById(R.id.tvTitle)
        if(customerParam!=null){
            customer = Gson().fromJson(customerParam,LocalCustomer::class.java)
            tvTitle.setText(customer!!.name)
        }
        return view}

    companion object {
        @JvmStatic
        fun newInstance(param: String) = CustomerBillFragment().apply {
            arguments = Bundle().apply {
                putString(CUSTOMER_OBJ, param)
            }
        }
    }


}
