package com.goshoppi.pos.view.settings


import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.userRepo.UserRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.utils.SharedPrefs
import org.jetbrains.anko.support.v4.act
import javax.inject.Inject


class DeviceSettingFragment : BaseFragment() {
    lateinit var edDeviceId: EditText
    lateinit var edStoreCode: EditText
    lateinit var tvUserCode: TextView
    lateinit var cbAdmin: CheckBox
    lateinit var cbProc: CheckBox
    lateinit var cbSales: CheckBox

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

        val user = SharedPrefs.getInstance()!!.getUser(activity!!)
        edDeviceId = view.findViewById(R.id.edDeviceId)
        edStoreCode = view.findViewById(R.id.edStoreCode)
        tvUserCode = view.findViewById(R.id.tvUserCode)
        cbAdmin = view.findViewById(R.id.cbAdmin)
        cbProc = view.findViewById(R.id.cbProc)
        cbSales = view.findViewById(R.id.cbSales)

        if (user != null) {
            edStoreCode.setText(user.storeCode)
            tvUserCode.setText(user.userCode)
            cbProc.isChecked =user.isProcurement
            cbAdmin.isChecked =user.isAdmin
            cbSales.isChecked =user.isSales

        }

        cbAdmin.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//                user.isAdmin = isChecked
            }
        })
        cbProc.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//                user.isProcurement = isChecked
            }
        })
        cbSales.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//                user.isSales = isChecked
            }
        })
    }

}
