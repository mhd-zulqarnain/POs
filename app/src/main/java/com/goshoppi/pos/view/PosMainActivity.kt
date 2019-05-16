package com.goshoppi.pos.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.architecture.viewmodel.PosMainViewModel
import com.goshoppi.pos.architecture.workmanager.StoreProductImageWorker
import com.goshoppi.pos.architecture.workmanager.StoreVariantImageWorker
import com.goshoppi.pos.architecture.workmanager.SyncWorker
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.HoldOrder
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.utils.Constants.*
import com.goshoppi.pos.utils.CustomerAdapter
import com.goshoppi.pos.utils.FullScannerActivity
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.view.auth.LoginActivity
import com.goshoppi.pos.view.customer.CustomerManagmentActivity
import com.goshoppi.pos.view.inventory.InventoryHomeActivity
import com.goshoppi.pos.view.inventory.LocalInventoryActivity
import com.goshoppi.pos.view.inventory.ReceiveInventoryActivity
import com.goshoppi.pos.view.settings.SettingsActivity
import com.goshoppi.pos.view.user.AddUserActivity
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.android.synthetic.main.activity_pos_main.*
import kotlinx.android.synthetic.main.include_add_customer.*
import kotlinx.android.synthetic.main.include_customer_search.*
import kotlinx.android.synthetic.main.include_discount_cal.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


@Suppress("DEPRECATION")
@SuppressLint("SimpleDateFormat")
class PosMainActivity :
    BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope, View.OnClickListener {

    private lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var masterProductRepository: MasterProductRepository

    @Inject
    lateinit var localProductRepository: LocalProductRepository

    @Inject
    lateinit var workerFactory: WorkerFactory
    @Inject
    lateinit var customerRepository: CustomerRepository

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private var createPopupOnce = true
    private var toastFlag = false
    private var inflater: LayoutInflater? = null
    private var popupWindow: PopupWindow? = null
    lateinit var varaintList: ArrayList<LocalVariant>
    //    val ZBAR_CAMERA_PERMISSION = 12
    lateinit var posViewModel: PosMainViewModel
    var scanCount = 1
    var discountAmount = 0.00
    lateinit var mJob: Job

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_pos_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_setting ->
                startActivity(Intent(this@PosMainActivity, SettingsActivity::class.java))
            R.id.inventory_prod ->
                startActivity(Intent(this@PosMainActivity, InventoryHomeActivity::class.java))
            R.id.customerDashboard ->
                startActivity(Intent(this@PosMainActivity, CustomerManagmentActivity::class.java))
            R.id.logout -> {
                Utils.logout(this@PosMainActivity)
                startActivity(Intent(this@PosMainActivity, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        mJob = Job()
        posViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(PosMainViewModel::class.java)
        varaintList = ArrayList()
        setUpOrderRecyclerView(varaintList)
        /*
        * Sycning the master data
        * if device is online
        * sync once
        */
        if (!workerInitialization) {
            val config = Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
            WorkManager.initialize(this, config)
        }
        workerInitialization = true
        if (!sharedPref.getBoolean(MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY, false)) {
            val myConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(myConstraints).build()
            val storeProductImageWorker =
                OneTimeWorkRequestBuilder<StoreProductImageWorker>().setConstraints(myConstraints).build()
            val storeVariantImageWorker =
                OneTimeWorkRequestBuilder<StoreVariantImageWorker>().setConstraints(myConstraints)
                    .addTag(STORE_VARIANT_IMAGE_WORKER_TAG).build()

            WorkManager.getInstance().beginUniqueWork(ONE_TIME_WORK, ExistingWorkPolicy.KEEP, syncWorkRequest)
                .then(storeProductImageWorker)
                .then(storeVariantImageWorker)
                .enqueue()

            WorkManager.getInstance().getWorkInfoByIdLiveData(storeVariantImageWorker.id)
                .observe(this@PosMainActivity, Observer { workInfo ->
                    if (workInfo?.state!!.isFinished && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    }
                })
        } else {
            Timber.e("No need to sync master")
        }

        inflater = LayoutInflater.from(this@PosMainActivity)
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

        posViewModel.cutomerListObservable.observe(this, Observer {
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
                    val listOfCustomer = ArrayList<LocalCustomer>()
                    it.forEach {
                        if (it.name != ANONYMOUS) {
                            listOfCustomer.add(it)
                        }
                    }

                    val locationAdapter = CustomerAdapter(this@PosMainActivity, listOfCustomer)
                    val listView = layout?.findViewById(R.id.lvMenu) as ListView
                    listView.adapter = locationAdapter
                    listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                        tvPerson.text = listOfCustomer[position].name
                        lvUserDetails.visibility = View.VISIBLE
                        posViewModel.customer = listOfCustomer[position]
                        createPopupOnce = true

                        launch {
                            customerRepository.getCustomerCredit(posViewModel.customer.phone.toString())
                                .observe(this@PosMainActivity, Observer {
                                    if (it != null)

                                        tvUserDebt.setText(String.format("%.2f AED", it))
                                    else
                                        tvUserDebt.text = getString(R.string.zero_aed)
                                })
                        }
                        svSearch.isIconified = true
                        svSearch.visibility = View.GONE
                        popupWindow?.dismiss()
                    }

                } else {
                    clearCustomer()
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
                    posViewModel.searchCustomer(param!!)
                }
                return true
            }
        })
        svSearch.setOnCloseListener(object : android.widget.SearchView.OnCloseListener, SearchView.OnCloseListener {
            override fun onClose(): Boolean {
//                svSearch.setQuery("", false);
                clearCustomer()
                popupWindow?.dismiss()
                return false
            }

        })
        posViewModel.holdedCount.observe(this, Observer {
            val size = HOLDED_ORDER_LIST.size
            tvholdedCount.setText(size.toString())
        })
        posViewModel.productObservable.observe(this, Observer {

            if (it == null) {
                if (toastFlag)
                    Utils.showMsgShortIntervel(this@PosMainActivity, "No product found")

            } else {
                val temp = isVaraintAdded(it.storeRangeId)
                /*
                * If varaint already scanned
                * */
                if (temp != -1) {
                    val index = indexOfVaraint(posViewModel.orderItemList[temp].variantId!!)
                    if (index != -1) {
                        val orderItem = posViewModel.orderItemList[temp]
                        val varaintItem = varaintList[index]
                        if (inStock(orderItem.productQty!!, varaintItem.stockBalance!!.toInt() - 1, varaintItem)) {
                            val count = orderItem.productQty!! + 1
                            posViewModel.orderItemList[temp].productQty = count
                            val v = rvProductList.findViewHolderForAdapterPosition(index)!!.itemView
                            rvProductList.post {
                                val qty: TextView = v.findViewById(R.id.tvProductQty)
                                val tvProductTotal: TextView = v.findViewById(R.id.tvProductTotal)
                                val tvProductQty: TextView = v.findViewById(R.id.tvProductQty)
                                qty.text = count.toString()
                                val price = orderItem.productQty!! * varaintItem.offerPrice!!.toDouble()
                                tvProductTotal.setText(String.format("%.2f", price))
                                orderItem.productQty = orderItem.productQty
                                tvProductQty.text = orderItem.productQty.toString()
                                posViewModel.subtotal += varaintItem.offerPrice!!.toDouble()
                                orderItem.totalPrice = String.format("%.2f", price).toDouble()
                                tvTotal.setText(String.format("%.2f AED", Math.abs(posViewModel.subtotal)))
                            }
                        } else {
                            Utils.showMsgShortIntervel(this@PosMainActivity, "Stock limit exceeed")
                        }
                    }
                    /*
                    * if scan new varaint
                    * */

                } else {
                    posViewModel.subtotal += it.offerPrice!!.toDouble()
                    val orderItem = OrderItem()
                    orderItem.productQty = 1
                    addToCart(orderItem)
                    varaintList.add(it)
                    rvProductList.adapter!!.notifyItemInserted(varaintList.size)
                }

            }

        })

        posViewModel.flag.observe(this, Observer {
            if (it != null) {
                Utils.showMsgShortIntervel(this@PosMainActivity, it.msg!!)
            }
            if (it.status!!) {
                val holdedId = fun(): Int {
                    HOLDED_ORDER_LIST.forEachIndexed { ind, it ->
                        if (it.holdorderId == posViewModel.orderId) {
                            return ind
                        }
                    }
                    return -1
                }
                if (holdedId() != -1) {
                    HOLDED_ORDER_LIST.removeAt(holdedId())
                    posViewModel.holdedCount.value = "order placed"
                }
                reset()
            }

        })

        tvOrderId.setText("Order Number:${posViewModel.orderId}")
        btnPay.setOnClickListener(this)
        ivCredit.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        btnScan.setOnClickListener(this)
        tvDiscount.setOnClickListener(this)
        ivClose.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
        btn_add_customer.setOnClickListener(this)
        ivAddCustomer.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
        btn_add_customer.setOnClickListener(this)
        btShowInventory.setOnClickListener(this)
        btnAddUser.setOnClickListener(this)
        cvInventory.setOnClickListener(this)
        btnHoldOrder.setOnClickListener(this)
        ivNext.setOnClickListener(this)
        ivPrevious.setOnClickListener(this)
        btnrecieve.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnPay -> {
                toastFlag = false
                posViewModel.placeOrder(PAID, discountAmount)

            }
            R.id.ivCredit -> {
                toastFlag = false
                posViewModel.placeOrder(CREDIT, discountAmount)
            }
            R.id.btnCancel ->
                reset()
            R.id.btnScan -> {
                toastFlag = true

                // lanuchScanCode(FullScannerActivity::class.java)
                if (scanCount == 1) {
                    getBarCodedProduct("8718429762806")
                    scanCount += 1
                } else
                    getBarCodedProduct("8718429762523")
            }
            R.id.tvDiscount -> {
                cvCalculator.visibility = View.VISIBLE
                lvAction.visibility = View.GONE
                setUpCalculator()
            }
            R.id.ivClose -> {
                svSearch.setQuery("", false)
                lvUserDetails.visibility = View.GONE
                svSearch.visibility = View.VISIBLE
            }
            R.id.btn_cancel ->
                lvAddCus.visibility = View.GONE
            R.id.btn_add_customer ->
                addNewCustomer()
            R.id.ivAddCustomer -> {
                lvAddCus.visibility = View.VISIBLE
                ed_cus_mbl.requestFocus()
                ed_cus_mbl.isFocusableInTouchMode = true
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(ed_cus_mbl, InputMethodManager.SHOW_IMPLICIT)
            }
            R.id.btnrecieve -> {
                startActivity(Intent(this@PosMainActivity, ReceiveInventoryActivity::class.java))
            }
            R.id.btShowInventory ->
                startActivityForResult(Intent(this@PosMainActivity, LocalInventoryActivity::class.java), UPATE_VARIANT)
            R.id.btnAddUser ->
                startActivity(Intent(this@PosMainActivity, AddUserActivity::class.java))
            R.id.cvInventory ->
                startActivity(Intent(this@PosMainActivity, InventoryHomeActivity::class.java))
            R.id.btnHoldOrder -> {
                holdOrder()
            }
            R.id.ivNext -> {
                getHoldedOrder(false)
            }
            R.id.ivPrevious -> {
                getHoldedOrder(true)
            }

        }
    }


    fun reset() {
        toastFlag = false
        posViewModel.customer = posViewModel.getAnonymousCustomer()
        posViewModel.orderItemList = ArrayList()
        posViewModel.subtotal = 0.00
        posViewModel.productBarCode.value = ""
        posViewModel.orderId = System.currentTimeMillis()
        clearCustomer()
        varaintList = ArrayList()
        setUpOrderRecyclerView(varaintList)
        tvTotal.setText(getString(R.string.zero_aed))
        tvDiscount.setText(getString(R.string.zero_aed))
        tvUserDebt.setText(getString(R.string.zero_aed))
        tvSubtotal.setText(getString(R.string.zero_aed))
        lvUserDetails.visibility = View.GONE
        svSearch.visibility = View.VISIBLE
        discountAmount = 0.00
        tvOrderId.setText("Order Number:${posViewModel.orderId}")

    }

    fun getHoldedOrder(isPrevious: Boolean) {

        val currentOrderIndex =
            fun(): Int {
                HOLDED_ORDER_LIST.forEachIndexed { index, it ->
                    if (it.holdorderId == posViewModel.orderId)
                        return index
                }
                return -1
            }
        if (isPrevious && currentOrderIndex() != -1) {
            val index = currentOrderIndex() - 1
            if (HOLDED_ORDER_LIST.size > index && index != -1) {
                setHoldedOrder(HOLDED_ORDER_LIST[index])
                Utils.showMsg(this@PosMainActivity, "Previous order found")

            } else {
                Utils.showMsg(this@PosMainActivity, "No Previous order found")
            }

        } else if (!isPrevious && currentOrderIndex() != -1) {
            val index = currentOrderIndex()
            if (HOLDED_ORDER_LIST.size > index && (HOLDED_ORDER_LIST.size - 1) != index) {
                setHoldedOrder(HOLDED_ORDER_LIST[index + 1])
            } else {
                reset()
                Utils.showMsg(this@PosMainActivity, "No order found")

            }
        } else {
            if (!isPrevious) {
                Utils.showMsg(this@PosMainActivity, "No order found")
            } else if (!HOLDED_ORDER_LIST.isEmpty()) {
                setHoldedOrder(HOLDED_ORDER_LIST[HOLDED_ORDER_LIST.size - 1])
            }
        }


    }

    fun holdOrder() {
        if (posViewModel.subtotal < 1 || posViewModel.orderItemList.size == 0) {
            Utils.showMsg(this, "Please Add products to hold order")

        } else {
            val temp = HoldOrder(
                posViewModel.customer,
                posViewModel.orderItemList,
                varaintList,
                posViewModel.orderId,
                posViewModel.subtotal
            )
            val isAlreadyAdded = fun(): Int {
                HOLDED_ORDER_LIST.forEachIndexed { ind, it ->
                    if (it.holdorderId == temp.holdorderId) {
                        return ind
                    }
                }
                return -1
            }
            if (isAlreadyAdded() != -1) {
                val index = isAlreadyAdded()
                HOLDED_ORDER_LIST.set(index, temp)
            } else {
                HOLDED_ORDER_LIST.add(temp)

            }
            posViewModel.holdedCount.value = "order holded"
            reset()
            Utils.showMsg(this, "Order Holded")

        }
    }

    fun setHoldedOrder(holded: HoldOrder) {
        reset()
        posViewModel.orderId = holded.holdorderId!!
        posViewModel.subtotal = holded.holdorderSubTotal!!
        posViewModel.orderItemList = holded.holdorderlist!!

        if (holded.holdcustomer!!.name != ANONYMOUS) {
            posViewModel.customer = holded.holdcustomer!!
            tvPerson.text = posViewModel.customer.name
            svSearch.visibility = View.VISIBLE
            lvUserDetails.visibility = View.GONE
        } else {
            svSearch.visibility = View.GONE
            lvUserDetails.visibility = View.VISIBLE

        }
        tvOrderId.setText("Order Number:${posViewModel.orderId}")
        tvTotal.setText(String.format("%.2f AED", Math.abs(posViewModel.subtotal)))
        svSearch.isIconified = true
        rvProductList.adapter = null
        setUpOrderRecyclerView(varaintList)

        holded.varaintList!!.forEach {
            varaintList.add(it)
            rvProductList.adapter!!.notifyItemInserted(varaintList.size)

            val temp = isVaraintAdded(it.storeRangeId)
            if (temp != -1) {
                val index = indexOfVaraint(posViewModel.orderItemList[temp].variantId!!)
                if (index != -1) {
                    val orderItem = posViewModel.orderItemList[temp]
                    posViewModel.orderItemList[temp].productQty = holded.holdorderlist!![temp].productQty

                    rvProductList.post {
                        val v = rvProductList.findViewHolderForAdapterPosition(index)!!.itemView
                        val tvProductTotal: TextView = v.findViewById(R.id.tvProductTotal)
                        val tvProductQty: TextView = v.findViewById(R.id.tvProductQty)
                        val price = orderItem.productQty!! * it.offerPrice!!.toDouble()
                        tvProductTotal.setText(String.format("%.2f", price))
                        tvProductQty.text = orderItem.productQty.toString()
                        orderItem.totalPrice = String.format("%.2f", price).toDouble()
                        tvTotal.setText(String.format("%.2f AED", Math.abs(posViewModel.subtotal)))
                    }

                }
            }
        }
    }

    private fun addNewCustomer() {
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

        val customer = LocalCustomer()
        customer.phone = ed_cus_mbl.text.toString().toLong()
        customer.alternativePhone = ed_alt_cus_mbl.text.toString()
        customer.gstin = ed_cus_gstin.text.toString()
        customer.gstin = ed_cus_gstin.text.toString()
        customer.name = ed_cus_name.text.toString()
        customer.address = ed_cus_address.text.toString()
        customer.isSynced = false
        customer.updatedAt = System.currentTimeMillis().toString()

        posViewModel.addCustomer(customer)
        lvAddCus.visibility = View.GONE

        Utils.hideSoftKeyboard(this)
        tvPerson.text = customer.name
        lvUserDetails.visibility = View.VISIBLE
        svSearch.visibility = View.GONE
        if (popupWindow != null)
            popupWindow!!.dismiss()
        posViewModel.customer = customer

        ed_cus_mbl.setText("")
        ed_alt_cus_mbl.setText("")
        ed_cus_gstin.setText("")
        ed_cus_gstin.setText("")
        ed_cus_name.setText("")
        ed_cus_address.setText("")

    }

    private fun clearCustomer() {
        posViewModel.customer = posViewModel.getAnonymousCustomer()
    }

    private fun addToCart(order: OrderItem) {
        posViewModel.orderItemList.add(order)
    }

    fun removeFromCart(order: OrderItem) {

        posViewModel.orderItemList.remove(order)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key.equals(getString(R.string.pref_app_theme_color_key))) {
            setAppTheme(sharedPreferences)
            recreate()
        }
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

    override fun onPause() {
        super.onPause()
        if (popupWindow != null)
            popupWindow!!.dismiss()
    }

    override fun onResume() {

        super.onResume()
        try {
            if (!FullScannerActivity.BARCODE.isEmpty()) {
                //getBarCodedProduct(FullScannerActivity.BARCODE)
            }
        } catch (ex: Exception) {
        }

    }

    private fun getBarCodedProduct(barcode: String) {
        posViewModel.search(barcode)
    }

    private fun setUpOrderRecyclerView(list: ArrayList<LocalVariant>) {
        rvProductList.layoutManager = LinearLayoutManager(this@PosMainActivity)
        rvProductList.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_order_place)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvProductName = mainView.findViewById<TextView>(R.id.tvProductName)
                val tvProductQty = mainView.findViewById<TextView>(R.id.tvProductQty)
                val tvProductEach = mainView.findViewById<TextView>(R.id.tvProductEach)
                val tvProductTotal = mainView.findViewById<TextView>(R.id.tvProductTotal)
                val minus_button = mainView.findViewById<ImageButton>(R.id.minus_button)
                val add_button = mainView.findViewById<ImageButton>(R.id.plus_button)

                val orderItem = posViewModel.orderItemList[viewHolder.position]

                if (inStock(orderItem.productQty!!, itemData.stockBalance!!.toInt(), itemData)) {
                    orderItem.orderId = posViewModel.orderId
                    orderItem.productId = itemData.productId.toLong()
                    orderItem.variantId = itemData.storeRangeId
                    orderItem.mrp = itemData.productMrp
                    orderItem.totalPrice =
                        if (itemData.offerPrice != null) itemData.offerPrice!!.toDouble() else 0.0
                    orderItem.taxAmount = 0.0
                    orderItem.addedDate = SimpleDateFormat("MM/dd/yyyy").format(Date(System.currentTimeMillis()))
                    launch {
                        tvProductName.text = localProductRepository.getProductNameById(itemData.productId)
                    }
                } else {
                    Utils.showMsgShortIntervel(this@PosMainActivity, "Stock limit exceeed")
                    rvProductList.post {
                        posViewModel.subtotal -= itemData.offerPrice!!.toDouble()
                        tvTotal.setText(String.format("%.2f AED", Math.abs(posViewModel.subtotal)))
                        varaintList.remove(itemData)
                        rvProductList.adapter!!.notifyItemRemoved(viewHolder.position)
                    }
                }
                tvProductQty.text = "1"
                tvProductEach.text = itemData.offerPrice

                if (orderItem.productQty!! > 1) {
                    val intialprice = tvProductTotal.text.toString().toDouble() - itemData.offerPrice!!.toDouble()
                    tvProductTotal.setText(String.format("%.2f", intialprice))
                } else
                    tvProductTotal.text = itemData.offerPrice

                tvTotal.setText(String.format("%.2f AED", posViewModel.subtotal))
                minus_button.setOnClickListener {
                    if (orderItem.productQty!! > 1) {
                        val price = tvProductTotal.text.toString().toDouble() - itemData.offerPrice!!.toDouble()
                        val count = orderItem.productQty!! - 1
                        orderItem.productQty = count
                        orderItem.totalPrice = price
                        tvProductTotal.setText(String.format("%.2f", price))
                        tvProductQty.setText(count.toString())
                        posViewModel.subtotal -= itemData.offerPrice!!.toDouble()
                        tvTotal.setText(String.format("%.2f AED", posViewModel.subtotal))
                        orderItem.productQty = orderItem.productQty
                        orderItem.totalPrice = String.format("%.2f", price).toDouble()
                    } else {
                        posViewModel.subtotal = posViewModel.subtotal - itemData.offerPrice!!.toDouble()
                        removeFromCart(orderItem)
                        varaintList.remove(itemData)
                        rvProductList.adapter!!.notifyItemRemoved(viewHolder.position)
                    }
                    tvTotal.setText(String.format("%.2f AED", Math.abs(posViewModel.subtotal)))
                }
                add_button.setOnClickListener {
                    if (inStock(orderItem.productQty!!, itemData.stockBalance!!.toInt() - 1, itemData)) {
                        if (orderItem.productQty!! < 10) {
                            val count = orderItem.productQty!! + 1
                            orderItem.productQty = count

                            val price = orderItem.productQty!! * itemData.offerPrice!!.toDouble()
                            tvProductTotal.setText(String.format("%.2f", price))
                            orderItem.productQty = orderItem.productQty
                            tvProductQty.text = orderItem.productQty.toString()
                            posViewModel.subtotal += itemData.offerPrice!!.toDouble()
                            orderItem.totalPrice = String.format("%.2f", price).toDouble()
                        }
                        tvTotal.setText(String.format("%.2f AED", posViewModel.subtotal))
                    } else {
                        Utils.showMsgShortIntervel(this@PosMainActivity, "Stock limit exceeed")
                    }

                }
            }


    }

    fun inStock(count: Int, stock: Int, varaintItem: LocalVariant): Boolean {
        if (varaintItem.unlimitedStock.equals("1")) {
            return true
        } else if (varaintItem.unlimitedStock.equals("1")) {
            return false
        } else
            return count <= stock
    }

    fun isVaraintAdded(variantId: Int): Int {
        posViewModel.orderItemList.forEachIndexed { index, it ->
            if (it.variantId == variantId) {
                return index
            }
        }
        return -1
    }

    fun indexOfVaraint(variantId: Int): Int {
        varaintList.forEachIndexed { index, it ->
            if (it.storeRangeId == variantId) {
                return index
            }
        }
        return -1

    }
    /*   private fun lanuchScanCode(clss: Class<*>) =
           if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(
                   this,
                   arrayOf(Manifest.permission.CAMERA), ZBAR_CAMERA_PERMISSION
               )
           } else {
               val intent = Intent(this, clss)
               startActivity(intent)
           }*/

    //<editor-fold desc="Discount calculator handling">
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
                cvCalculator.visibility = View.GONE
                lvAction.visibility = View.VISIBLE
                if (isCalulated) {
                    tvDiscount.setText(String.format("%.2f AED", tvCalTotal.text.toString().toDouble()))
                    tvSubtotal.setText(
                        String.format(
                            "%.2f AED",
                            posViewModel.subtotal - tvCalTotal.text.toString().toDouble()
                        )
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
                        Utils.showMsgShortIntervel(this@PosMainActivity, "Invalid Entry")
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
            tvCalTotal.setText("")
            isCalulated = false
        }
        if (tvCalTotal.text.toString().contains(".")) {
            tvCalTotal.setText(" ${tvCalTotal.text}${btn_point.text}")
        }
        if (tvCalTotal.text.toString().trim().length < 2) {
            tvCalTotal.setText(" ${tvCalTotal.text}${btn_point.text}")
        }

    }

    private fun eraseCal() {
        if (!isCalulated) {
            var str = tvCalTotal.text
            if (str != null && str.length > 0) {
                str = str.substring(0, str.length - 1)
            }
            tvCalTotal.setText(str)
        } else {
            tvCalTotal.text = ""
            isCalulated = false
        }
    }

    private fun calculateDiscount(discount: Double) {

        isCalulated = true
        val amount = posViewModel.subtotal
        val res = (amount / 100.0f) * discount
        discountAmount = res
//        val res = amount-(amount*( discount/ 100.0f))
        tvCalTotal.text = String.format("%.2f", res)
    }
    //</editor-fold>

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == UPATE_VARIANT) {
                reset()
                toastFlag = false
            } else {
            }
        } catch (ex: Exception) {
            Timber.e("get error")

        }
    }

}
