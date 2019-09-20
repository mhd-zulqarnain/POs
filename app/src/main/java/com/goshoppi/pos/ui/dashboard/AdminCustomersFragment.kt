package com.goshoppi.pos.ui.dashboard


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.RecyclerView

import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseFragment
import org.jetbrains.anko.find

/**
 * A simple [Fragment] subclass.
 */
class AdminCustomersFragment : BaseFragment() {
    lateinit var rvCustomer:RecyclerView
    override fun layoutRes(): Int {

        return R.layout.fragment_admin_customers
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        rvCustomer = view.find(R.id.rvCustomer)
    }


}
