package com.goshoppi.pos.ui.inventory

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.model.local.PurchaseOrderDetails
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.ui.inventory.viewmodel.ReceiveInventoryViewModel
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.android.synthetic.main.activity_receive_inventory.*
import kotlinx.android.synthetic.main.include_add_distributor.*
import kotlinx.android.synthetic.main.include_distributor_search.*
import kotlinx.android.synthetic.main.include_discount_cal.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class ReceiveInventoryActivity() : BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope, View.OnClickListener {

    private lateinit var sharedPref: SharedPreferences
    lateinit var mJob: Job
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private var popupWindow: PopupWindow? = null
    private var toastFlag = false
    private var createPopupOnce = true
    private var inflater: LayoutInflater? = null
    lateinit var varaintList: ArrayList<LocalVariant>
    var scanCount = 1
    @Inject
    lateinit var receiveViewModel: ReceiveInventoryViewModel

    @Inject
    lateinit var localProductRepository: LocalProductRepository

    @Inject
    lateinit var distributorsRepository: DistributorsRepository


    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_receive_inventory
    }


    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }


    private fun initView() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            toolbar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_toolbar_color) )
        } else{
            toolbar.setBackgroundResource(R.color.colorPrimaryLight)
        }
        mJob = Job()
        receiveViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ReceiveInventoryViewModel::class.java)
        varaintList = ArrayList()
        setUpOrderRecyclerView(varaintList)

        inflater = LayoutInflater.from(this@ReceiveInventoryActivity)
        val layout = inflater?.inflate(
            R.layout.spinner_list,
            null
        )
        popupWindow =
            PopupWindow(
                layout,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false
            )

        receiveViewModel.cutomerListObservable.observe(this, Observer {
            Timber.e("searching products")
            if (it != null && popupWindow != null) {
                if (it.size != 0) {
                    if (createPopupOnce) {
                        Handler().postDelayed({
                            popupWindow?.update(0, 0, svSearch.width, LinearLayout.LayoutParams.WRAP_CONTENT)
                            popupWindow?.showAsDropDown(svSearch, 0, 0)
                            createPopupOnce = false
                        }, 100)

                    }
                    val listOfCustomer = ArrayList<Distributor>()
                    it.forEach {inner->
                        listOfCustomer.add(inner)
                    }

                    val locationAdapter = DistributorAdapter(this@ReceiveInventoryActivity, listOfCustomer)
                    val listView = layout?.findViewById(R.id.lvMenu) as ListView
                    listView.adapter = locationAdapter
                    listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                        tvPerson.text = listOfCustomer[position].name
                        lvUserDetails.visibility = View.VISIBLE
                        receiveViewModel.distributor = listOfCustomer[position]
                        createPopupOnce = true

                        launch {
                            /*   customerRepository.getCustomerCredit(receiveViewModel.customer.phone.toString())
                                   .observe(this@ReceiveInventoryActivity, Observer {
                                       if (it != null)

                                           tvUserDebt.setText(String.format("%.2f AED", it))
                                       else
                                           tvUserDebt.text = getString(R.string.zero_aed)
                                   })*/
                        }
                        svSearch.isIconified = true
                        svSearch.visibility = View.GONE
                        popupWindow?.dismiss()
                    }

                } else {
                  //  clearDistributor()
                    createPopupOnce = true
                    popupWindow?.dismiss()
                }
            }
        })

        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                svSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(param: String?): Boolean {
                if (param != "") {
                    receiveViewModel.searchdistributor(param!!)
                }
                return true
            }
        })

        svSearch.setOnCloseListener(object : android.widget.SearchView.OnCloseListener, SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                clearDistributor()
                popupWindow?.dismiss()
                return false
            }
        })

        receiveViewModel.productObservable.observe(this, Observer {

            if (it == null) {
                if (toastFlag)
                    Utils.showMsgShortIntervel(this@ReceiveInventoryActivity, "No product found")


            } else {
                val temp = isVaraintAdded(it.storeRangeId)
                /*
                * If varaint already scanned
                * */
                if (temp != -1) {
                    val index = indexOfVaraint(receiveViewModel.poDetailList[temp].variantId!!.toLong())
                    if (index != -1) {
                        val orderItem = receiveViewModel.poDetailList[temp]
                        val varaintItem = varaintList[index]
                        val count = orderItem.productQty!! + 1
                        receiveViewModel.poDetailList[temp].productQty = count
                        val v = rvProductList.findViewHolderForAdapterPosition(index)!!.itemView
                        rvProductList.post {
                            val qty: TextView = v.findViewById(R.id.etProductQty)
                            val tvProductTotal: TextView = v.findViewById(R.id.tvProductTotal)
                            val tvProductQty: TextView = v.findViewById(R.id.etProductQty)
                            qty.text = count.toString()
                            val price = orderItem.productQty!! * varaintItem.offerPrice!!.toDouble()
                            tvProductTotal.text = String.format("%.2f", price)
                            orderItem.productQty = orderItem.productQty
                            tvProductQty.text = orderItem.productQty.toString()
                            receiveViewModel.subtotal += varaintItem.offerPrice!!.toDouble()
                            orderItem.totalPrice = String.format("%.2f", price).toDouble()
                            tvTotalBillAmount.text = String.format("%.2f AED", Math.abs(receiveViewModel.subtotal))
                            tvPrice.text = String.format("%.2f AED", Math.abs(receiveViewModel.subtotal))
                        }

                    }
                    /*
                    * if scan new varaint
                    * */

                } else {
                    receiveViewModel.subtotal += it.offerPrice!!.toDouble()
                    val pod = PurchaseOrderDetails()
                    pod.productQty = 1
                    addToCart(pod)
                    varaintList.add(it)
                    rvProductList.adapter!!.notifyItemInserted(varaintList.size)
                }

            }

        })

        tvBillDate.text = Utils.getShortDate(Utils.getTodaysDate())

        receiveViewModel.flag.observe(this, Observer {
            if (it != null) {
                Utils.showMsgShortIntervel(this@ReceiveInventoryActivity, it.msg!!)
            }
            if (it.status!!) {
                reset()
                setResult(Activity.RESULT_OK)
               finish()
            }

        })

        ivClose.setOnClickListener(this)
        btnCustomerCancel.setOnClickListener(this)
        btnAddCustomer.setOnClickListener(this)
        ivAddCustomer.setOnClickListener(this)
        btnCustomerCancel.setOnClickListener(this)
        btnAddCustomer.setOnClickListener(this)
        btnScan.setOnClickListener(this)
        btnProceed.setOnClickListener(this)
        tvDiscount.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        btnDetail.setOnClickListener(this)

    }

    private fun showPoDialog() {
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_po_get_details, null)
        val alertBox = AlertDialog.Builder(this)
        alertBox.setView(view)
        alertBox.setCancelable(true)
        val dialog = alertBox.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val etInvoiceNo: EditText = view.findViewById(R.id.etInvoiceNo)
        val etPOdate: EditText = view.findViewById(R.id.etPOdate)
        val btnSave: Button = view.findViewById(R.id.btnSave)
        val btnClose: ImageView = view.findViewById(R.id.btn_close_dialog)


        dialog.setOnDismissListener {

        }

        btnSave.setOnClickListener {
            if (!etInvoiceNo.text.isEmpty() && !etPOdate.text.isEmpty()) {
                receiveViewModel.placeOrder(
                    etInvoiceNo.text.toString(),
                    etPOdate.text.toString(),
                    tvCash.text.toString(),
                    etCredit.text.toString()
                )
                dialog.dismiss()
            }
        }
        btnClose.setOnClickListener {

            dialog.dismiss()
        }
        etPOdate.setOnClickListener {

            dateDialog(etPOdate)
        }
        dialog.show()

    }

    //region Products

    private fun getBarCodedProduct(barcode: String) {
        receiveViewModel.search(barcode)
    }

    private fun setUpOrderRecyclerView(list: ArrayList<LocalVariant>) {

        rvProductList.layoutManager = LinearLayoutManager(this@ReceiveInventoryActivity)
        rvProductList.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_order_receive)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvProductName = mainView.findViewById<TextView>(R.id.tvProductName)
                val etProductQty = mainView.findViewById<TextView>(R.id.etProductQty)
                val tvProductEach = mainView.findViewById<TextView>(R.id.tvProductEach)
                val tvProductTotal = mainView.findViewById<TextView>(R.id.tvProductTotal)
                val minus_button = mainView.findViewById<ImageButton>(R.id.minus_button)
                val add_button = mainView.findViewById<ImageButton>(R.id.plus_button)
                val poDetail = receiveViewModel.poDetailList[viewHolder.adapterPosition]

//                    poDetail.orderId = receiveViewModel.orderId
                poDetail.productId = itemData.productId
                poDetail.variantId = itemData.storeRangeId
                poDetail.mrp = itemData.productMrp
                poDetail.totalPrice =
                    if (itemData.offerPrice != null) itemData.offerPrice!!.toDouble() else 0.0
//                poDetail.taxAmount = 0.0
                poDetail.addedDate = Utils.getTodaysDate()
                launch {
                    tvProductName.text = localProductRepository.getProductNameById(itemData.productId)
                }

                etProductQty.text = "1"
                tvProductEach.text = itemData.offerPrice

                if (poDetail.productQty!! > 1) {
                    val intialprice = tvProductTotal.text.toString().toDouble() - itemData.offerPrice!!.toDouble()
                    tvProductTotal.text = String.format("%.2f", intialprice)
                } else
                    tvProductTotal.text = itemData.offerPrice

                tvTotalBillAmount.text = String.format("%.2f AED", receiveViewModel.subtotal)
                tvPrice.text = String.format("%.2f AED", Math.abs(receiveViewModel.subtotal))



                minus_button.setOnClickListener {
                    if (poDetail.productQty!! > 1) {
                        val count = poDetail.productQty!! - 1
                        poDetail.productQty = count
                        val price = poDetail.totalPrice!! - itemData.offerPrice!!.toDouble()
                        tvProductTotal.text = String.format("%.2f", price)
                        poDetail.productQty = poDetail.productQty
                        etProductQty.text = poDetail.productQty.toString()
                        receiveViewModel.subtotal-= itemData.offerPrice!!.toDouble()
                        poDetail.totalPrice = String.format("%.2f", price).toDouble()
                    } else {
                        it.setOnClickListener(null)
                        receiveViewModel.subtotal = receiveViewModel.subtotal - itemData.offerPrice!!.toDouble()
                        removeFromCart(poDetail)
                        varaintList.remove(itemData)
                        rvProductList.adapter!!.notifyItemRemoved(viewHolder.adapterPosition)
                    }
                    tvTotalBillAmount.text = String.format("%.2f AED", Math.abs(receiveViewModel.subtotal))
                    tvPrice.text = String.format("%.2f AED", Math.abs(receiveViewModel.subtotal))

                }
                add_button.setOnClickListener {
//                    if (poDetail.productQty!! < 10) {
                        val count = poDetail.productQty!! + 1
                        poDetail.productQty =count
                        val price = poDetail.productQty!! * itemData.offerPrice!!.toDouble()
                    tvProductTotal.text = String.format("%.2f", price)
                        etProductQty.text = poDetail.productQty.toString()
                        receiveViewModel.subtotal += itemData.offerPrice!!.toDouble()
                        poDetail.totalPrice = String.format("%.2f", price).toDouble()
//                    }
                    tvTotalBillAmount.text = String.format("%.2f AED", receiveViewModel.subtotal)
                    tvPrice.text = String.format("%.2f AED", Math.abs(receiveViewModel.subtotal))

                }
                tvTotalProduct.text = list.size.toString()

                //Remove from cart
                tvProductTotal.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        val DRAWABLE_RIGHT = 2
                        if (event!!.action == 0) {
                            if (event.rawX >= tvProductTotal.right - tvProductTotal.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                                receiveViewModel.subtotal =
                                    receiveViewModel.subtotal - tvProductTotal.text.toString().toDouble()
                                removeFromCart(poDetail)
                                varaintList.remove(itemData)
                                tvTotalBillAmount.text = String.format("%.2f AED", receiveViewModel.subtotal)
                                tvPrice.text = String.format("%.2f AED", Math.abs(receiveViewModel.subtotal))
                                rvProductList.adapter!!.notifyItemRemoved(viewHolder.position)
                                v!!.setOnTouchListener(null)
                                return true
                            }
                        }
                        return false
                    }
                })


                etProductQty.setOnClickListener {
                    showDialogue(poDetail.productQty!!, itemData)
                }


            }


    }

    fun setPriceOnchangingQuantity(newQty: Int, prevQty: Int, itemData: LocalVariant) {

        val temp = isVaraintAdded(itemData.storeRangeId)
        val index = indexOfVaraint(receiveViewModel.poDetailList[temp].variantId!!.toLong())
        if (index != -1) {
            val poDetail = receiveViewModel.poDetailList[temp]
            val varaintItem = varaintList[index]
           val itemPrice= varaintItem.offerPrice!!.toDouble()
//            val count = poDetail.productQty!! + 1
//            receiveViewModel.poDetailList[temp].productQty = count
            val v = rvProductList.findViewHolderForAdapterPosition(index)!!.itemView
            rvProductList.post {
                val qty = prevQty - newQty
                val tvProductTotal: TextView = v.findViewById(R.id.tvProductTotal)
                val tvProductQty: TextView = v.findViewById(R.id.etProductQty)

                if (qty > 0) {
                    val count = poDetail.productQty!! - Math.abs(qty)
                    val price = varaintItem.offerPrice!!.toDouble()*Math.abs(qty)
                    poDetail.productQty = count
                    poDetail.totalPrice =  poDetail.totalPrice!! - price
                    tvProductTotal.text = String.format("%.2f", poDetail.totalPrice)
                    poDetail.productQty = poDetail.productQty
                    tvProductQty.text = poDetail.productQty.toString()
                    receiveViewModel.subtotal -= price
                    poDetail.totalPrice = String.format("%.2f", price).toDouble()
                    receiveViewModel.poDetailList[temp].productQty = count

                } else {
                    val count = poDetail.productQty!! + Math.abs(qty)
                    val tmpPrice=itemPrice*Math.abs(qty)
                    poDetail.productQty = count
                    poDetail.totalPrice =  poDetail.totalPrice!! + tmpPrice
                    tvProductTotal.text = String.format("%.2f", poDetail.totalPrice)
                    poDetail.productQty = poDetail.productQty
                    tvProductQty.text = poDetail.productQty.toString()
                    receiveViewModel.subtotal += tmpPrice
                  //  poDetail.totalPrice = String.format("%.2f",  poDetail.totalPrice).toDouble()
                    receiveViewModel.poDetailList[temp].productQty = count

                }
                tvPrice.text = String.format("%.2f AED", Math.abs(receiveViewModel.subtotal))
                tvTotalBillAmount.text = String.format("%.2f AED", Math.abs(receiveViewModel.subtotal))

            }
        }

    }

    private fun showDialogue( prevQty: Int, itemData: LocalVariant) {

            val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_receive_qty_change, null)
        val alertBox = AlertDialog.Builder(this)
        alertBox.setView(view)
        alertBox.setCancelable(true)
        val dialog = alertBox.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val btnClose: ImageView = view.findViewById(R.id.btn_close_dialog)
        val etNewQty: EditText = view.findViewById(R.id.etNewQty)
        val btnSave: Button = view.findViewById(R.id.btnSave)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        receiveViewModel.productBarCode.value = "-1"
        toastFlag= false
        btnSave.setOnClickListener {
            if (!etNewQty.text.isEmpty() && !etNewQty.text.isEmpty()) {
                etNewQty.text.toString()
                tvCash.text.toString()
                etCredit.text.toString()
                 setPriceOnchangingQuantity(etNewQty.text.toString().toInt(), prevQty, itemData)
                dialog.dismiss()
            }else{
                Utils.showMsg(this,"Please enter qutatity")
            }
        }
        dialog.show()

    }

    //endregion

    //region Distributor

    private fun addNewDistributor() {
        if (ed_cus_mbl.text.toString().trim() == "" || ed_cus_mbl.text.toString().trim().length < 9) {
            ed_cus_mbl.error = resources.getString(R.string.err_phone)
            ed_cus_mbl.requestFocus()
            return
        }
        if (ed_cus_name.text.toString().trim() == "") {
            ed_cus_name.error = resources.getString(R.string.err_not_empty)
            ed_cus_name.requestFocus()
            return
        }
        if (ed_cus_name.text.toString().trim() == "") {
            ed_cus_name.error = resources.getString(R.string.err_not_empty)
            ed_cus_name.requestFocus()
            return
        }

        val distributor = Distributor()
        distributor.phone = ed_cus_mbl.text.toString().toLong()
        distributor.alternativePhone = ed_alt_cus_mbl.text.toString()
        distributor.gstin = ed_cus_gstin.text.toString()
        distributor.gstin = ed_cus_gstin.text.toString()
        distributor.name = ed_cus_name.text.toString()
        distributor.address = ed_cus_address.text.toString()
        distributor.isSynced = false
        distributor.updatedAt = Utils.getTodaysDate()

        receiveViewModel.addDistributor(distributor)
        lvAddCus.visibility = View.GONE

        Utils.hideSoftKeyboard(this)
        tvPerson.text = distributor.name
        lvUserDetails.visibility = View.VISIBLE
        svSearch.visibility = View.GONE
        if (popupWindow != null)
            popupWindow!!.dismiss()
        receiveViewModel.distributor = distributor

        ed_cus_mbl.setText("")
        ed_alt_cus_mbl.setText("")
        ed_cus_gstin.setText("")
        ed_cus_gstin.setText("")
        ed_cus_name.setText("")
        ed_cus_address.setText("")

    }

    private fun setDistributorDetail() {
        if (receiveViewModel.distributor == null)
            return
        cvDestributorDetail.visibility = View.VISIBLE
        cvDetailDes.visibility = View.GONE

        tvDesNumber.text = receiveViewModel.distributor!!.phone.toString()
        tvDesAddress.text = receiveViewModel.distributor!!.address.toString()
        tvDesAltNumber.text = receiveViewModel.distributor!!.alternativePhone.toString()
        tvDecName.text = receiveViewModel.distributor!!.name.toString()
        launch {
            distributorsRepository.getDistributorCredit(receiveViewModel.distributor!!.phone.toString())
                .observe(this@ReceiveInventoryActivity, Observer {
                    if (it != null) {
                        tvDebt.text = String.format("-%.2f AED", it.toDouble())

                    } else {
                        tvDebt.text = "0 AED"
                    }
                })

        }

    }

    class DistributorAdapter(var context: Context, var distributorList: ArrayList<Distributor>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View?
            val viewHolder: ViewHolder
            if (convertView == null) {
                val layout = LayoutInflater.from(context)
                view = layout.inflate(R.layout.single_search_customer_view, parent, false)
                viewHolder = ViewHolder(view)
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }
            val distributor = getItem(position) as Distributor
            viewHolder.nameTxt?.text = distributor.name
            viewHolder.PhoneTxt?.text = distributor.phone.toString()

            return view as View
        }

        override fun getItem(position: Int): Any {
            return distributorList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return distributorList.size
        }

        private class ViewHolder(row: View) {
            var nameTxt: TextView? = null
            var PhoneTxt: TextView? = null

            init {
                this.nameTxt = row.findViewById(R.id.tvPersonName) as TextView
                this.PhoneTxt = row.findViewById(R.id.tvPersonPhone) as TextView
            }
        }
    }

    //endregion

    //region Utils


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.ivClose -> {
                svSearch.setQuery("", false)
                lvUserDetails.visibility = View.GONE
                svSearch.visibility = View.VISIBLE
            }
            R.id.btnCancel -> {
                reset()
            }
            R.id.btnCustomerCancel ->
                lvAddCus.visibility = View.GONE
            R.id.btnAddCustomer ->
                addNewDistributor()
            R.id.ivAddCustomer -> {
                lvAddCus.visibility = View.VISIBLE
                ed_cus_mbl.requestFocus()
                ed_cus_mbl.isFocusableInTouchMode = true
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(ed_cus_mbl, InputMethodManager.SHOW_IMPLICIT)
            }
            R.id.btnScan -> {
                toastFlag = true

                if (scanCount == 1) {
                    getBarCodedProduct("8718429762806")
                    scanCount += 1
                } else
                    getBarCodedProduct("8718429762523")

            }
            R.id.btnProceed -> {
                val cash = tvCash.text.toString()
                val credit = etCredit.text.toString()
                val isValidAmount = isvalidAmount(cash, credit)
                if (receiveViewModel.distributor == null) {
                    Utils.showMsg(this, "Please add distributor details ")
                } else if (receiveViewModel.subtotal < 1 || receiveViewModel.poDetailList.size == 0) {
                    Utils.showMsg(this, "Please Add products to place order")
                } else if (!isValidAmount.isEmpty()) {
                    Utils.showMsg(this, isValidAmount)
                } else {
                    showPoDialog()
                    toastFlag = false
                }
            }
            R.id.tvDiscount -> {
              //  cvCalculator.visibility = View.VISIBLE
               // cvDetailParent.visibility = View.GONE
                setUpCalculator()
            }
            R.id.btnDetail -> {
                setDistributorDetail()

            }

        }
    }

    fun reset() {
        toastFlag = false
        receiveViewModel.distributor = null
        receiveViewModel.poDetailList = ArrayList()
        receiveViewModel.subtotal = 0.00
        etCredit.setText("")
        tvCash.setText("")
        tvTotalProduct.text = "0"
        cvDestributorDetail.visibility = View.GONE
        cvDetailDes.visibility = View.VISIBLE
        cvDetailParent.visibility = View.VISIBLE
//        receiveViewModel.productBarCode.value = ""
        varaintList = ArrayList()
        setUpOrderRecyclerView(varaintList)
        tvTotalBillAmount.text = getString(R.string.zero_aed)
        tvDiscount.text = getString(R.string.zero_aed)
        lvUserDetails.visibility = View.GONE
        svSearch.visibility = View.VISIBLE
        receiveViewModel.discount = 0.00
    }

    fun isVaraintAdded(variantId: Long): Int {
        receiveViewModel.poDetailList.forEachIndexed { index, it ->
            if (it.variantId!!.toLong() == variantId) {
                return index
            }
        }
        return -1
    }

    private fun addToCart(po: PurchaseOrderDetails) {
        receiveViewModel.poDetailList.add(po)
    }

    fun indexOfVaraint(variantId: Long): Int {
        varaintList.forEachIndexed { index, it ->
            if (it.storeRangeId == variantId) {
                return index
            }
        }
        return -1

    }

    private fun clearDistributor() {
        cvDestributorDetail.visibility = View.GONE
        cvDetailDes.visibility = View.VISIBLE
        receiveViewModel.distributor = null
    }

    fun removeFromCart(order: PurchaseOrderDetails) {
        receiveViewModel.poDetailList.remove(order)
    }

    private fun isvalidAmount(cash: String, credit: String): String {
        val total = receiveViewModel.subtotal - receiveViewModel.discount
        if (cash.isEmpty() && credit.isEmpty()) {
            return "Please Enter the cash or credit amount"
        }
        if (!cash.isEmpty() && !credit.isEmpty()) {
            if (cash.toDouble() + credit.toDouble() > total)
                return "Amount is greater than payable amount"

        }
        if (!cash.isEmpty()) {
            if (cash.toDouble() > total)
                return "Amount is greater than payable amount"
        }
        if (!credit.isEmpty()) {
            if (credit.toDouble() > total)
                return "Amount is greater than payable amount"
        }
        return ""
    }

    fun dateDialog(etPOdate: EditText) {

        val listener = DatePickerDialog.OnDateSetListener { mView, year, monthOfYear, dayOfMonth ->
            val date = dayOfMonth.toString() + "/" + monthOfYear + "/" + year.toString()
            etPOdate.setText(date)
        }
        val cal = Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val dpDialog = DatePickerDialog(this@ReceiveInventoryActivity, listener, year, month + 1, day)

        dpDialog.show()
    }
    //endregion

    //region Discount calculator handling
    private var isCalulated = false

    private fun setUpCalculator() {
        btn_point.setOnClickListener(calcOnClick)
        btn_two_per.setOnClickListener(calcOnClick)
        btn_five_per.setOnClickListener(calcOnClick)
        btn_seven.setOnClickListener(calcOnClick)
        btn_eight.setOnClickListener(calcOnClick)
        btn_nine.setOnClickListener(calcOnClick)
        btn_four.setOnClickListener(calcOnClick)
        btn_five.setOnClickListener(calcOnClick)
        btn_six.setOnClickListener(calcOnClick)
        btn_one.setOnClickListener(calcOnClick)
        btn_two.setOnClickListener(calcOnClick)
        btn_three.setOnClickListener(calcOnClick)
        btn_zero.setOnClickListener(calcOnClick)
        btn_point.setOnClickListener(calcOnClick)
        btn_done.setOnClickListener(calcOnClick)
        btnErase.setOnClickListener(calcOnClick)
        btn_percent.setOnClickListener(calcOnClick)
    }

    private val calcOnClick = View.OnClickListener { view ->
        when (view.id) {
            R.id.btn_done -> {
               // cvCalculator.visibility = View.GONE
                cvDetailParent.visibility = View.VISIBLE
                if (isCalulated) {
                    tvDiscount.text = String.format("%.2f AED", tvCalTotal.text.toString().toDouble())
                    tvNetAmount.text = String.format(
                        "%.2f AED",
                        receiveViewModel.subtotal - tvCalTotal.text.toString().toDouble()
                    )
                }
            }
            R.id.btn_five_per -> {
                calculateDiscount(5.0)
            }
            R.id.btn_two_per -> {
                calculateDiscount(2.0)
            }
            R.id.btn_percent -> {
                val temp = tvCalTotal.text.toString().trim()
                if (temp != "") {
                    if (temp.toDouble() < 100) {
                        calculateDiscount(temp.toDouble())
                    } else
                        Utils.showMsgShortIntervel(this@ReceiveInventoryActivity, "Invalid Entry")
                }

            }
            R.id.btnErase -> {
                eraseCal()
            }
            R.id.btn_point -> {
                if (!tvCalTotal.text.toString().contains(".")) {
                    setTextTotal(view as Button)
                }
            }
            else -> {
                setTextTotal(view as Button)
            }
        }
    }

    private fun setTextTotal(btn_point: Button) {
        if (isCalulated) {
            tvCalTotal.text = ""
            isCalulated = false
        }
        if (tvCalTotal.text.toString().contains(".")) {
            tvCalTotal.text = " ${tvCalTotal.text}${btn_point.text}"
        }
        if (tvCalTotal.text.toString().trim().length < 2) {
            tvCalTotal.text = " ${tvCalTotal.text}${btn_point.text}"
        }

    }

    private fun eraseCal() {
        if (!isCalulated) {
            var str = tvCalTotal.text
            if (str != null && str.length > 0) {
                str = str.substring(0, str.length - 1)
            }
            tvCalTotal.text = str
        } else {
            tvCalTotal.text = ""
            isCalulated = false
        }
    }

    private fun calculateDiscount(discount: Double) {

        isCalulated = true
        val amount = receiveViewModel.subtotal
        val res = (amount / 100.0f) * discount
//        val res = amount-(amount*( discount/ 100.0f))
        receiveViewModel.discount = res
        tvCalTotal.text = String.format("%.2f", res)
    }

    //endregion

    //region activty life cycle
    override fun onPause() {
        super.onPause()
        if (popupWindow != null)
            popupWindow!!.dismiss()
    }

    private fun setAppTheme(sharedPreferences: SharedPreferences) {

        when (sharedPreferences.getString(
            getString(R.string.pref_app_theme_color_key),
            getString(R.string.pref_color_default_value)
        )) {

            getString(R.string.pref_color_default_value) -> {
                setTheme(R.style.Theme_App)
            }

            getString(R.string.pref_color_blue_value) -> {
                setTheme(R.style.Theme_App_Blue)
            }

            getString(R.string.pref_color_green_value) -> {
                setTheme(R.style.Theme_App_Green)
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key.equals(getString(R.string.pref_app_theme_color_key))) {
            setAppTheme(sharedPreferences)
            recreate()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }

    //endregion

}
