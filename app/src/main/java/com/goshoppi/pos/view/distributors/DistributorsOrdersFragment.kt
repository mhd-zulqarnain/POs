package com.goshoppi.pos.view.distributors

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.model.local.PurchaseOrder
import com.goshoppi.pos.view.distributors.viewmodel.DistributorOrdersViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.android.synthetic.main.fragment_distributors_orders.*
import javax.inject.Inject

private const val distributor_OBJ = "distributor_object"

class DistributorsOrdersFragment : BaseFragment() {

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
        distributorOrdersViewModel =
            ViewModelProviders.of(baseActivity, viewModelFactory).get(DistributorOrdersViewModel::class.java)
        arguments.let {
            distributorParam = it!!.getString(distributor_OBJ)
        }

        initView()
    }

    private fun initView() {

        if (distributorParam != null) {
            distributor = Gson().fromJson(distributorParam, Distributor::class.java)
            distributorOrdersViewModel.getUserData(distributor!!.phone.toString())

        }
        distributorOrdersViewModel.listOfOrdersObservable.observe(this, Observer {
            if (it != null) {
                setOrderRecyclerView(it as ArrayList<PurchaseOrder>)
            }
        })
    }

    private fun setOrderRecyclerView(list: ArrayList<PurchaseOrder>) {
        rvOrder.layoutManager = LinearLayoutManager(activity!!)
        rvOrder.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_order_view) { itemData, viewHolder ->
                val mv = viewHolder.itemView
                val tvPoDate = mv.findViewById<TextView>(R.id.tvPoDate)
                val tvPoNum = mv.findViewById<TextView>(R.id.tvPoNum)
                val tvPoAmount = mv.findViewById<TextView>(R.id.tvPoAmount)
                val tvPoPending = mv.findViewById<TextView>(R.id.tvPoPending)
                val tvPoStatus = mv.findViewById<TextView>(R.id.tvPoStatus)
                val ivPoShowDetail = mv.findViewById<ImageView>(R.id.ivPoShowDetail)

                tvPoDate.text = itemData.poDate
                tvPoNum.text = itemData.id.toString()
                tvPoAmount.text = itemData.totalAmount.toString()
                tvPoPending.text = itemData.credit.toString()
                tvPoStatus.text = itemData.id.toString()
//                    tvPoStatus.text= itemData.id.toString()

                mv.setOnClickListener{
                    showPoDetailDialog()
                }
                ivPoShowDetail.setOnClickListener {
                }

            }

    }

    fun showPoDetailDialog() {
        val vw = LayoutInflater.from(activity!!).inflate(R.layout.dialog_porder_details, null)
        val bx = AlertDialog.Builder(activity!!)
        bx.setView(vw)
        bx.setCancelable(true)
        val dialog = bx.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    companion object {
        fun newInstance(param: String) = DistributorsOrdersFragment().apply {
            arguments = Bundle().apply {
                putString(distributor_OBJ, param)
            }
        }
    }
}