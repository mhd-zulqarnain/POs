package com.goshoppi.pos.view.distributors


import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.view.distributors.viewmodel.DistributorSummeryViewModel
import timber.log.Timber
import javax.inject.Inject

private const val distributor_OBJ = "customerParam"

class DistributorsSummeryFragment : BaseFragment() {
    override fun layoutRes(): Int {
        return R.layout.fragment_distributors_summery
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var distributorSummeryViewModel: DistributorSummeryViewModel

    var distributorParam: String? = null
    var distributor: Distributor? = null
    lateinit var btnOrderNum: Button
    lateinit var btnTransaction: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        distributorSummeryViewModel =
            ViewModelProviders.of(baseActivity, viewModelFactory).get(DistributorSummeryViewModel::class.java)

        arguments?.let {
            distributorParam = it.getString(distributor_OBJ)
        }
        initView(view)
    }

    private fun initView(view: View) {
        if (distributorParam != null) {
            distributor = Gson().fromJson(distributorParam, Distributor::class.java)

            distributorSummeryViewModel.getUserData(distributor!!.phone.toString())
        }
        btnOrderNum = view.findViewById(R.id.btnOrderPlaceNum)
        btnTransaction = view.findViewById(R.id.btnTransaction)
        distributorSummeryViewModel.totalOrderObservable.observe(this, Observer {

            if (it != null) {
                btnOrderNum.text = "Number of Purchase order \n \n $it"
            } else {
                btnOrderNum.text = "Number of Purchase order \n \n 0"

            }
        })
        distributorSummeryViewModel.totalTransactionObservable.observe(this, Observer {
            Timber.e("distributorSummeryViewModel.totalTransactionObservable.observe")
            if (it != null) {
                btnTransaction.text = "${Math.ceil(it.toDouble() / 12)} AED \n \n  Average purchases \n per month  "
            } else {
                btnTransaction.text = "0 AED \n \n  Average purchaser \n per month  "

            }
        })


    }

    companion object {
        fun newInstance(param: String) = DistributorsSummeryFragment().apply {
            arguments = Bundle().apply {
                putString(distributor_OBJ, param)
            }
        }
    }


}
