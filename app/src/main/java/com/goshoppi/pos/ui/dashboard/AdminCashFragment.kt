package com.goshoppi.pos.ui.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseFragment

/**
 * A simple [Fragment] subclass.
 */
class AdminCashFragment : BaseFragment() {
    override fun layoutRes(): Int {

        return R.layout.fragment_admin_cash
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_cash, container, false)
    }


}
