package com.goshoppi.pos.view.auth

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.goshoppi.pos.R
import com.goshoppi.pos.model.User
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.view.PosMainActivity

class ProcumentAuthFragment : Fragment() {
    lateinit var edStoreCode: EditText
    lateinit var edUserCode: EditText
    lateinit var edPassCode: EditText
    lateinit var btnLogin: Button
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sales_auth, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        btnLogin = view.findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            getUser()
        }
        edStoreCode = view.findViewById(R.id.edStoreCode)
        edUserCode = view.findViewById(R.id.edUserCode)
        edPassCode = view.findViewById(R.id.edPassCode)


    }
    fun getUser() {
        edStoreCode.setError(null)
        edUserCode.setError(null)
        edPassCode.setError(null)

        val storeCode = edStoreCode.getText().toString()
        val userCode = edUserCode.getText().toString()
        val passCode = edPassCode.getText().toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(userCode)) {
            edUserCode.setError(activity!!.getString(R.string.err_not_empty))
            focusView = edUserCode
            cancel = true
        }
        if (TextUtils.isEmpty(storeCode)) {
            edStoreCode.setError(activity!!.getString(R.string.err_not_empty))
            focusView = edStoreCode
            cancel = true
        }

        if (TextUtils.isEmpty(passCode)) {
            edPassCode.setError(getString(R.string.err_invalid_entry))
            focusView = edPassCode
            cancel = true
        }
        if (cancel) run {
            focusView!!.requestFocus()
            return
        }

       (activity!! as LoginActivity).userRepository.getProcAuthResult(storeCode,userCode,passCode).observe(activity!!,
           Observer<User> {
               if(it!==null ){
                   val i = Intent(activity!!, PosMainActivity::class.java)
                   Utils.setLoginUser(it,activity!!)

                   startActivity(i)
                   activity!!.finish()
               }else{
                   Utils.showMsg(activity!!, "Authentication failed")
               }
           })


    }
}