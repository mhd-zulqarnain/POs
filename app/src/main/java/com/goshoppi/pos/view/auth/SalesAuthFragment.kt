package com.goshoppi.pos.view.auth

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.goshoppi.pos.R
import com.goshoppi.pos.view.PosMainActivity

class SalesAuthFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sales_auth, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        val btnLogin: Button = view.findViewById(R.id.btn_login_sign_in)
        btnLogin.setOnClickListener {
            startActivity(Intent(activity!!, PosMainActivity::class.java))
        }
    }

}