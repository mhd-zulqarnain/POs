package com.goshoppi.pos.ui.auth

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.goshoppi.pos.R
import com.goshoppi.pos.ui.PosMainActivity

class SalesAuthFragment : Fragment() {

    lateinit var btn_login_sign_in: Button;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sales_auth, container, false)

initView(view)
        return view

    }

    private fun initView(view: View) {
        btn_login_sign_in = view.findViewById(R.id.btn_login_sign_in)
        btn_login_sign_in.setOnClickListener{
            startActivity(Intent(activity!!,PosMainActivity::class.java))
        }
    }

}