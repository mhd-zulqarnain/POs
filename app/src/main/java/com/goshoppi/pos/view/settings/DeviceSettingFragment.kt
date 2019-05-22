package com.goshoppi.pos.view.settings


import android.os.Bundle
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


class DeviceSettingFragment : BaseFragment(), CoroutineScope {
    lateinit var mJob: Job
    lateinit var edDeviceId: EditText
    lateinit var edStoreCode: EditText
    lateinit var tvUserCode: TextView
    lateinit var cbAdmin: CheckBox
    lateinit var cbProc: CheckBox
    lateinit var cbSales: CheckBox
    lateinit var btnUpdateAccess: Button
    lateinit var btnUpdate: Button
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    @Inject
    lateinit var userRepository: UserRepository

    override fun layoutRes(): Int {
        return R.layout.fragment_device_setting

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        mJob = Job()
        val loginUser = SharedPrefs.getInstance()!!.getUser(activity!!)
        edDeviceId = view.findViewById(R.id.edDeviceId)
        edStoreCode = view.findViewById(R.id.edStoreCode)
        tvUserCode = view.findViewById(R.id.tvUserCode)
        cbAdmin = view.findViewById(R.id.cbAdmin)
        cbProc = view.findViewById(R.id.cbProc)
        cbSales = view.findViewById(R.id.cbSales)
        btnUpdate = view.findViewById(R.id.btnUpdate)
        btnUpdateAccess = view.findViewById(R.id.btnUpdateAccess)

        if (loginUser != null) {
            edStoreCode.setText(loginUser.storeCode)
            tvUserCode.setText(loginUser.userCode)
            cbProc.isChecked = loginUser.isProcurement
            cbAdmin.isChecked = loginUser.isAdmin
            cbSales.isChecked = loginUser.isSales
        }

        val user = User()
        user.userId = loginUser!!.userId
        user.storeCode = loginUser.storeCode
        user.userCode = loginUser.userCode
        user.password = loginUser.password
        user.updatedAt = loginUser.updatedAt
        user.isAdmin = loginUser.isAdmin
        user.isSales = loginUser.isSales
        user.isProcurement = loginUser.isProcurement

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

        btnUpdateAccess.setOnClickListener {
            if (loginUser.isAdmin) {
                launch {
                    userRepository.updateUser(user.isAdmin,user.isProcurement,user.isSales,userId = user.userId!!)
                    Utils.setLoginUser(user, activity!!)
                    Utils.showMsgShortIntervel(activity!!, "Updated successfully")

                }
            } else
                Utils.showMsg(activity!!, "You don't have admin access")
        }
    }

}
