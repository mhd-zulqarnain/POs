package com.goshoppi.pos.view.distributors

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.view.distributors.viewmodel.DistributorOrdersViewModel
import javax.inject.Inject

private const val distributor_OBJ ="distributor_object"
class DistributorsOrdersFragment:BaseFragment(){

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var distributorOrdersViewModel: DistributorOrdersViewModel
    var distributorParam: String? = null
    var distributor: Distributor? = null

    override fun layoutRes(): Int {
    return R.layout.fragment_distributors_orders
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        distributorOrdersViewModel = ViewModelProviders.
            of(baseActivity,viewModelFactory).get(DistributorOrdersViewModel::class.java)
        arguments.let {
            distributorParam=it!!.getString(distributor_OBJ)
        }

        initView()
    }

    private fun initView() {

        if(distributorParam!=null) {
            distributor = Gson().fromJson(distributorParam, Distributor::class.java)
            distributorOrdersViewModel.getUserData(distributor!!.phone.toString())

        }
        distributorOrdersViewModel.listOfOrdersObservable.observe(this, Observer {
            if(it!=null){

            }
        })
    }

    companion object{
        fun newInstance(param:String)=DistributorsOrdersFragment().apply {
            arguments = Bundle().apply {
                putString(distributor_OBJ, param)
            }

        }
    }
}