package com.goshoppi.pos.ui.settings


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.userRepo.UserRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.model.User
import com.goshoppi.pos.utils.SharedPrefs
import com.goshoppi.pos.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class AddUserFragment : BaseFragment(),
    CoroutineScope {
    lateinit var mJob: Job
    lateinit var cbAdmin: CheckBox
    lateinit var cbProc: CheckBox
    lateinit var cbSales: CheckBox
    lateinit var et_store_code: EditText
    lateinit var et_user_code: EditText
    lateinit var et_password: EditText
    lateinit var btn_add: Button

    @Inject
    lateinit var userRepository: UserRepository
    var user = User()

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun layoutRes(): Int {
        return R.layout.fragment_user_add_setting

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }


    private fun initView(view: View) {
        mJob=Job()
        val loginUser = SharedPrefs.getInstance()!!.getUser(activity!!)
        cbAdmin = view.findViewById(R.id.cbAdmin)
        cbProc = view.findViewById(R.id.cbProc)
        cbSales = view.findViewById(R.id.cbSales)
        btn_add = view.findViewById(R.id.btn_add)

        et_store_code = view.findViewById(R.id.et_store_code)
        et_user_code = view.findViewById(R.id.et_user_code)
        et_password = view.findViewById(R.id.et_password)
        user.isSales =true

        if (loginUser != null) {
            et_store_code.setText(loginUser.storeCode)
        }

        cbAdmin.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                user.isAdmin = isChecked
            }
        })
        cbProc.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                user.isProcurement = isChecked
            }
        })
        cbSales.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                user.isSales = isChecked
            }
        })
        btn_add.setOnClickListener {
            if(loginUser!!.isAdmin)
            adduser()
            else
                Utils.showMsg(activity!!,"You don't have admin access")
        }
    }

    fun adduser() {
        val storeCode = et_store_code.text.toString()
        val userCode =   et_user_code.text.toString()
        val password =  et_password.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(storeCode)) {
            et_store_code.error = getString(R.string.err_not_empty)
            focusView = et_store_code
            cancel = true
        }

        if (TextUtils.isEmpty(userCode)) {
            et_user_code.error = getString(R.string.err_invalid_entry)
            focusView = et_user_code
            cancel = true
        }
        if (TextUtils.isEmpty(password)) {
            et_password.error = getString(R.string.err_invalid_entry)
            focusView = et_password
            cancel = true
        }
        if (cancel) run {
            focusView!!.requestFocus()
            return
        }

        user.userCode = userCode
        user.storeCode = storeCode
        user.password = password

        user.updatedAt =  Utils.getTodaysDate()
        launch {
            userRepository.insertUser(user)
            et_user_code.setText("")
            et_password.setText("")
            user =User()

        }

        Utils.showMsgShortIntervel(activity!!,"User added successfully")
    }
    override fun onDetach() {
        super.onDetach()

        mJob.cancel()
    }
}
