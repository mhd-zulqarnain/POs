package com.goshoppi.pos.view.distributors

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.PurchaseOrderRepo.PurchaseOrderRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.model.local.PurchaseOrder
import com.goshoppi.pos.model.local.PurchaseOrderDetails
import com.goshoppi.pos.view.distributors.viewmodel.DistributorOrdersViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.coroutines.*
import javax.inject.Inject

private const val distributor_OBJ = "distributor_object"

class DistributorsOrdersFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var distributorOrdersViewModel: DistributorOrdersViewModel
    var distributorParam: String? = null
    var distributor: Distributor? = null
    @Inject
    lateinit var purchaseOrderRepository: PurchaseOrderRepository

    lateinit var rvOrder: RecyclerView

    val uiScope = CoroutineScope(Dispatchers.Main)
    override fun layoutRes(): Int {
        return R.layout.fragment_distributors_orders
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        distributorOrdersViewModel =
            ViewModelProviders.of(baseActivity, viewModelFactory)
                .get(DistributorOrdersViewModel::class.java)
        arguments.let {
            distributorParam = it!!.getString(distributor_OBJ)
        }

        initView(view)
    }

    private fun initView(view: View) {
        rvOrder = view.findViewById(R.id.rvOrder)

        if (distributorParam != null) {
            distributor = Gson().fromJson(distributorParam, Distributor::class.java)
            distributorOrdersViewModel.getUserData(distributor!!.phone.toString())

        }
        distributorOrdersViewModel.listOfOrdersObservable.observe(activity!!, Observer {
            if (it != null) {
                setOrderRecyclerView(it as ArrayList<PurchaseOrder>)
            }
        })
    }

    private fun setOrderRecyclerView(list: ArrayList<PurchaseOrder>) {
        if (activity != null) {
            rvOrder.layoutManager = LinearLayoutManager(activity)
            rvOrder.adapter =
                RecyclerViewGeneralAdapter(
                    list,
                    R.layout.single_order_view
                ) { itemData, viewHolder ->
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

                    ivPoShowDetail.setOnClickListener {
                        // showPoDetailDialog(itemData, itemData.poInvoiceNumber!!)
                        loadData(itemData, itemData.poInvoiceNumber!!)
                    }


                }
        }
    }

    fun loadData(po: PurchaseOrder, poId: Long) = uiScope.launch {


        val view: View =
            LayoutInflater.from(activity!!).inflate(R.layout.dialog_porder_details, null)
        val alertBox = AlertDialog.Builder(activity!!)
        alertBox.setView(view)
        alertBox.setCancelable(true)
        val dialog = alertBox.create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvTotalAmount: TextView = view.findViewById(R.id.tvTotalAmount)
        val tvNetAmount: TextView = view.findViewById(R.id.tvNetAmount)
        val tvGstAmount: TextView = view.findViewById(R.id.tvGstAmount)
        val tvCreditAmount: TextView = view.findViewById(R.id.tvCreditAmount)
        val tvTotalPaid: TextView = view.findViewById(R.id.tvTotalPaid)
        val tvDiscount: TextView = view.findViewById(R.id.tvDiscount)
        val rv_podetail: RecyclerView = view.findViewById(R.id.rv_podetail)
        val btnClose: Button = view.findViewById(R.id.btnClose)
        var podList :ArrayList<PurchaseOrderDetails> = ArrayList()
        val task = async(Dispatchers.IO) {
            val list = purchaseOrderRepository.aysnloadPurcahseOrderDetailByInvoiceNumber(poId)
            podList = list as ArrayList<PurchaseOrderDetails>
            setPurchaseOrderDetail(podList, rv_podetail)

        }
        task.await()


        val total = po.paid + po.discount!!
        tvOrderId.text = po.poInvoiceNumber.toString()
        tvOrderDate.text = po.poDate
        tvTotalAmount.text = total.toString()
        tvNetAmount.text = total.toString()
        tvCreditAmount.text = po.credit.toString()
        tvTotalPaid.text = po.paid.toString()
        tvDiscount.text = po.discount.toString()
        tvGstAmount.text = "0.0"
        rv_podetail.adapter!!.notifyDataSetChanged()
        rv_podetail.layoutParams.width = RecyclerView.LayoutParams.MATCH_PARENT
        dialog.show()
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        withTimeout(5000){
           // setPurchaseOrderDetail(list as ArrayList<PurchaseOrderDetails>, rv_podetail)

        }
    }

    private fun setPurchaseOrderDetail(
        list: ArrayList<PurchaseOrderDetails>,
        rv_podetail: RecyclerView
    ) {
             rv_podetail.layoutManager = LinearLayoutManager(activity!!)
            rv_podetail.adapter = RecyclerViewGeneralAdapter(
                list,
                R.layout.single_po_detail
            ) { itemData, viewHolder ->

                val mv = viewHolder.itemView
                val tvTotal: TextView = mv.findViewById(R.id.tvTotal)
                val tvPP: TextView = mv.findViewById(R.id.tvPP)
                val tvMRP: TextView = mv.findViewById(R.id.tvMRP)
                val tvQty: TextView = mv.findViewById(R.id.tvQty)

                tvTotal.text = itemData.totalPrice.toString()
                tvPP.text = itemData.totalPrice.toString()
                tvMRP.text = itemData.totalPrice.toString()
                tvQty.text = itemData.productQty.toString()

            }

    }

    fun showPoDetailDialog(po: PurchaseOrder, poId: Long) {
        /*  val vw = LayoutInflater.from(activity!!).inflate(R.layout.dialog_porder_details, null)
          val bx = AlertDialog.Builder(activity!!)
          bx.setView(vw)
          bx.setCancelable(true)
          val dialog = bx.create()
          dialog.show()*/

        val view: View =
            LayoutInflater.from(activity!!).inflate(R.layout.dialog_porder_details, null)
        val alertBox = AlertDialog.Builder(activity!!)
        alertBox.setView(view)
        alertBox.setCancelable(true)
        val dialog = alertBox.create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvTotalAmount: TextView = view.findViewById(R.id.tvTotalAmount)
        val tvNetAmount: TextView = view.findViewById(R.id.tvNetAmount)
        val tvGstAmount: TextView = view.findViewById(R.id.tvGstAmount)
        val tvCreditAmount: TextView = view.findViewById(R.id.tvCreditAmount)
        val tvTotalPaid: TextView = view.findViewById(R.id.tvTotalPaid)
        val tvDiscount: TextView = view.findViewById(R.id.tvDiscount)
        val rv_podetail: RecyclerView = view.findViewById(R.id.rv_podetail)
        val btnClose: Button = view.findViewById(R.id.btnClose)

        /* setPurchaseOrderDetail(ArrayList(),rv_podetail)
         distributorOrdersViewModel.listOfPurchaseOrder.observe(activity!!, Observer {
             if(it!=null){
                 setPurchaseOrderDetail(it as ArrayList<PurchaseOrderDetails>,rv_podetail)
             }
         })*/
        loadData(po, poId)
        val total = po.paid + po.discount!!
        tvOrderId.text = po.poInvoiceNumber.toString()
        tvOrderDate.text = po.poDate
        tvTotalAmount.text = total.toString()
        tvNetAmount.text = total.toString()
        tvCreditAmount.text = po.credit.toString()
        tvTotalPaid.text = po.paid.toString()
        tvDiscount.text = po.discount.toString()
        tvGstAmount.text = "0.0"

        dialog.show()
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
      //  distributorOrdersViewModel.getpoInvoiceNumber(poId)

    }

    companion object {
        fun newInstance(param: String) = DistributorsOrdersFragment().apply {
            arguments = Bundle().apply {
                putString(distributor_OBJ, param)
            }
        }
    }
}