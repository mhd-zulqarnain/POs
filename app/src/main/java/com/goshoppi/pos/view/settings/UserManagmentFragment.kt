package com.goshoppi.pos.view.settings


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.userRepo.UserRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.model.User
import com.goshoppi.pos.utils.SharedPrefs
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import android.graphics.Paint
import android.widget.Button


class UserManagmentFragment : BaseFragment(),
    CoroutineScope {
    lateinit var mJob: Job
    lateinit var cbAdmin: CheckBox
    lateinit var cbProc: CheckBox
    lateinit var cbSales: CheckBox
    lateinit var rvUser: RecyclerView
    lateinit var svSearch: SearchView
    lateinit var tvUsername: TextView
    lateinit var btnUpdate: Button

    @Inject
    lateinit var userRepository: UserRepository
    var user = User()

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun layoutRes(): Int {
        return R.layout.fragment_user_managment_setting

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }


    private fun initView(view: View) {
        mJob = Job()
        val loginUser = SharedPrefs.getInstance()!!.getUser(activity!!)
        cbAdmin = view.findViewById(R.id.cbAdmin)
        cbProc = view.findViewById(R.id.cbProc)
        cbSales = view.findViewById(R.id.cbSales)
        tvUsername = view.findViewById(R.id.tvUsername)
        svSearch = view.findViewById(R.id.svSearch)
        btnUpdate = view.findViewById(R.id.btnUpdate)

        rvUser = view.findViewById(R.id.rvUser)

        user.isSales = true


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
        loadUser()

        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                svSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(param: String?): Boolean {
                if (param != null && param != "") {
                    launch {
                        val listOfUser =
                            userRepository.searchLocalStaticUser(param) as ArrayList<User>
                        setUpRecyclerView(listOfUser)
                    }

                }
                return true
            }
        })

        svSearch.setOnCloseListener(object : android.widget.SearchView.OnCloseListener, SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                loadUser()
                return false
            }

        })
        btnUpdate.setOnClickListener {
            if(loginUser!!.isAdmin)
                updateUser()
            else
                Utils.showMsg(activity!!,"You don't have admin access")
        }

    }

    fun updateUser() {

        user.updatedAt = System.currentTimeMillis().toString()
        launch {
            userRepository.insertUser(user)
        }

        Utils.showMsgShortIntervel(activity!!,"User added successfully")
    }

    @SuppressLint("SetTextI18n")
    private fun updateView(user: User) {
        this.user = user
        tvUsername.text ="User Code: ${user.userCode}"
        tvUsername.setPaintFlags(tvUsername.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        cbProc.isChecked = user.isProcurement
        cbAdmin.isChecked = user.isAdmin
        cbSales.isChecked = user.isSales
    }

    private fun loadUser() {
        launch {
            val t = userRepository.loadLocalAllStaticUsers()
            if (t.size != 0 && t.isNotEmpty()) {
                setUpRecyclerView(t as ArrayList<User>)
                val obj = Gson().toJson(t[0])
                updateView(t[0])

            } else {
                Utils.showAlert(false,
                    "No user found"
                    , " Please add user ",
                    activity!!,
                    DialogInterface.OnClickListener { _, _ ->
                    })
            }

        }
    }

    private fun setUpRecyclerView(list: ArrayList<User>) {

        rvUser.layoutManager =
            LinearLayoutManager(activity!!)
        rvUser.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_dashboard_customer_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvName = mainView.findViewById<TextView>(R.id.tvName)
                val tvPersonPhone = mainView.findViewById<TextView>(R.id.tvPersonPhone)
                val tvDebt = mainView.findViewById<TextView>(R.id.tvDebt)
                tvName.text = itemData.userCode!!.toUpperCase()
                tvPersonPhone.text = "Store Code:${itemData.storeCode.toString()}"
                tvDebt.visibility=View.INVISIBLE
                mainView.setOnClickListener {
                    updateView(itemData)
                }
            }
    }
}
