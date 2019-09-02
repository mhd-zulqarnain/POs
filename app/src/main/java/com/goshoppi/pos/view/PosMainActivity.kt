package com.goshoppi.pos.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.android.material.textfield.TextInputEditText
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.architecture.repository.userRepo.UserRepository
import com.goshoppi.pos.architecture.viewmodel.PosMainViewModel
import com.goshoppi.pos.architecture.workmanager.CategorySyncWorker
import com.goshoppi.pos.architecture.workmanager.StoreProductImageWorker
import com.goshoppi.pos.architecture.workmanager.StoreVariantImageWorker
import com.goshoppi.pos.architecture.workmanager.SyncWorker
import com.goshoppi.pos.di2.base.BaseActivity
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.model.*
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.utils.*
import com.goshoppi.pos.utils.Constants.*
import com.goshoppi.pos.view.auth.LoginActivity
import com.goshoppi.pos.view.customer.CustomerManagmentActivity
import com.goshoppi.pos.view.distributors.DistributorsManagmentActivity
import com.goshoppi.pos.view.inventory.InventoryHomeActivity
import com.goshoppi.pos.view.inventory.LocalInventoryActivity
import com.goshoppi.pos.view.inventory.ReceiveInventoryActivity
import com.goshoppi.pos.view.settings.SettingsActivity
import com.goshoppi.pos.view.weighted.WeightedProductsActivity
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_pos_main.*
import kotlinx.android.synthetic.main.include_action_btn.*
import kotlinx.android.synthetic.main.include_category_view.*
import kotlinx.android.synthetic.main.include_discount_cal.*
import kotlinx.android.synthetic.main.include_inventory_view.*
import kotlinx.android.synthetic.main.include_payment_view.*
import kotlinx.android.synthetic.main.include_weighted_prod.*
import kotlinx.android.synthetic.main.include_weights.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


class PosMainActivity :
    BaseActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope,
    View.OnClickListener {

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
    lateinit var posCart: PosCart

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var toastFlag = false
    lateinit var varaintList: ArrayList<LocalVariant>
    //    lateinit var lvAction: ConstraintLayout
    //    val ZBAR_CAMERA_PERMISSION = 12
    lateinit var posViewModel: PosMainViewModel

    @Inject
    lateinit var userRepository: UserRepository

    private var isUserAdmin = true
    private var scanCount = 1
    var discountAmount = 0.00
    lateinit var mJob: Job
    var weightedOrder = LocalVariant()
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_pos_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isUserAdmin = SharedPrefs.getInstance()!!.getUser(this@PosMainActivity)!!.isAdmin
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)
        initView()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.gradient_toolbar_color
                )
            )
        } else {
            toolbar.setBackgroundResource(R.color.colorPrimaryDark)
            tvCategoryLable.bringToFront()
            tvInventoryLable.bringToFront()
        }
    }

    private fun initView() {
        mJob = Job()
        userAccessView()
        requestScanViewFocus()

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

            val syncWorkRequest =
                OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(myConstraints).build()
            val storeProductImageWorker =
                OneTimeWorkRequestBuilder<StoreProductImageWorker>().setConstraints(myConstraints)
                    .build()
            val storeVariantImageWorker =
                OneTimeWorkRequestBuilder<StoreVariantImageWorker>().setConstraints(myConstraints)
                    .addTag(STORE_VARIANT_IMAGE_WORKER_TAG).build()
            val categorySyncWorker =
                OneTimeWorkRequestBuilder<CategorySyncWorker>().setConstraints(myConstraints)
                    .build()

            WorkManager.getInstance()
                .beginUniqueWork(ONE_TIME_WORK, ExistingWorkPolicy.KEEP, syncWorkRequest)
                .then(categorySyncWorker)
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


        posViewModel.holdedCount.observe(this, Observer {
            val size = HOLDED_ORDER_LIST.size
            tvholdedCount.setText(size.toString())
        })

        userRepository.getMachineId().observe(this, Observer {
            tvDeviceId.setText("POS - " + it)
        })

        //Bar coded product
        posViewModel.productObservable.observe(this, Observer {
            if (it == null) {
                if (toastFlag)
                    Utils.showMsgShortIntervel(this@PosMainActivity, "No product found")

            } else if (it.type == BAR_CODED_PRODUCT) {
                val isInCart = posCart.checkOrderItemInCart(it.storeRangeId)
                if (isInCart != -1) {
                    val orderItem = posCart.getOrderItemFromCart(isInCart)
                    if (inStock(
                            orderItem.productQty!! + 1
                            , it.stockBalance!!.toInt(), it
                        )
                    ) {
                        orderItem.productQty = orderItem.productQty!! + 1
                        posCart.setOrderItemToCartAtIndex(isInCart, orderItem)
                        posViewModel.subtotal += it.offerPrice!!.toDouble()
                        rvProductList.adapter!!.notifyDataSetChanged()

                    } else {
                        Utils.showMsgShortIntervel(this@PosMainActivity, "Stock limit exceeed")
                    }

                } else {
                    if (inStock(1, it.stockBalance!!.toInt(), it)) {
                        val orderItem = getOrderItem(it)
                        orderItem.productQty = 1
                        posCart.setOrderItemToCart(orderItem)
                        posViewModel.subtotal += it.offerPrice!!.toDouble()
                        varaintList.add(it)
                        rvProductList.adapter!!.notifyDataSetChanged()
                    } else {
                        Utils.showMsgShortIntervel(this@PosMainActivity, "Stock limit exceeed")
                    }
                }
            }

        })

        //weighted coded product Observable
        posViewModel.weightedProductObservable.observe(this, Observer {

            if (it == null) {
                if (toastFlag)
                    Utils.showMsgShortIntervel(this@PosMainActivity, "No product found")

            } else if (weightedOrder.sku != null) {
                if (inStock(weightedOrder.sku!!.toInt(), it.stockBalance!!.toInt(), it)) {
                    val orderItem = getOrderItem(it)
                    orderItem.productQty = weightedOrder.sku!!.toInt()
                    orderItem.totalPrice = weightedOrder.offerPrice!!.toDouble()
                    posCart.setWightedOrderItemToCart(orderItem)
                    posViewModel.subtotal += weightedOrder.offerPrice!!.toDouble()
                    varaintList.add(it)
                    rvProductList.adapter!!.notifyDataSetChanged()
                } else {
                    Utils.showMsgShortIntervel(this@PosMainActivity, "Stock limit exceeed")
                }
            }
        })

        posViewModel.flag.observe(this, Observer {


            if (it != null) {
                Utils.showMsgShortIntervel(this@PosMainActivity, it.msg!!)
            }
            if (posViewModel.subtotal < 1 || posViewModel.orderItemList.size == 0) {
                return@Observer
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
                createReceipt()
                reset()
            }

        })

        tvOrderId.text =
            "# ${posViewModel.orderId.toString().substring(posViewModel.orderId.toString().length - 5)}"


        // edScan.setInputType(InputType.TYPE_NULL)

        edScan.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus)
                    Timber.e("Scan view focused")
                else
                    Timber.e("lost focused")
            }
        }

        edScan.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                getBarCodedProduct(s.toString())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        btnPay.setOnClickListener(this)
        ivCredit.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        btnScan.setOnClickListener(this)
        tvDiscount.setOnClickListener(this)
        ivDiscount.setOnClickListener(this)
        lvDiscount.setOnClickListener(this)
        btShowInventory.setOnClickListener(this)
        cvInventory.setOnClickListener(this)
        btnHoldOrder.setOnClickListener(this)
        ivNext.setOnClickListener(this)
        ivPrevious.setOnClickListener(this)
        btnrecieve.setOnClickListener(this)
        btnWeighted.setOnClickListener(this)
        ivWeightedPrd.setOnClickListener(this)
        btnBack.setOnClickListener(this)
        edScan.setOnClickListener(this)
        lvInventory.setOnClickListener(this)
        lvCategory.setOnClickListener(this)
        lvCategoryView.setOnClickListener(this)
        lvInventoryView.setOnClickListener(this)
        btnCustomerAdd.setOnClickListener(this)

    }

    //region Holded orders

    fun getOrderItem(itemData: LocalVariant): OrderItem {
        val orderItem = OrderItem()
        orderItem.orderId = posViewModel.orderId
        orderItem.productId = itemData.productId
        orderItem.variantId = itemData.storeRangeId
        orderItem.mrp = itemData.productMrp
        orderItem.productName = itemData.productName
        orderItem.totalPrice =
            if (itemData.offerPrice != null) itemData.offerPrice!!.toDouble() else 0.0
        orderItem.taxAmount = 0.0
        orderItem.addedDate =
            SimpleDateFormat("MM/dd/yyyy").format(Date(System.currentTimeMillis()))
        return orderItem
    }

    fun getHoldedOrder(isPrevious: Boolean) {
        //getting index of holded order
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

        if (posViewModel.subtotal < 1 ||
            (posCart.allorderItemsFromCart.size == 0 && posCart.allWightedorderItemsFromCart.size == 0)
        ) {
            Utils.showMsg(this, "Please Add products to hold order")
        } else {
            posCart.allorderItemsFromCart.forEach {
                posViewModel.orderItemList.add(it)
            }
            posCart.allWightedorderItemsFromCart.forEach {
                posViewModel.orderItemList.add(it)
            }
            posCart.clearAllPosCart()
            posCart.clearAllWightedPosCart()

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
            tvPerson.setText(posViewModel.customer.name!!.toUpperCase() + " - " + posViewModel.customer.phone)


        } else {

        }
        tvTotal.setText(String.format("%.2f AED", Math.abs(posViewModel.subtotal)))
        rvProductList.adapter = null

        posViewModel.orderItemList.forEach {
            if (it.type == BAR_CODED_PRODUCT) {
                posCart.setOrderItemToCart(it)
            } else {
                posCart.setWightedOrderItemToCart(it)
            }
        }

        //setting up view for single item of holded item
        holded.varaintList!!.forEachIndexed { index, it ->
            varaintList.add(it)
        }
        setUpOrderRecyclerView(varaintList)
        tvUserDebt.text = String.format("%.2f AED", posViewModel.customer.totalCredit)


    }
    //endregion

    //region Customer

    private fun addCustomerDialog() {
        val view =
            LayoutInflater.from(this@PosMainActivity).inflate(R.layout.dialog_add_customer, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val svSearch = view.findViewById<SearchView>(R.id.svSearch)
        val lvAddCus = view.findViewById<LinearLayout>(R.id.lvAddCus)
        val lvSearchCustomer = view.findViewById<LinearLayout>(R.id.lvSearchCustomer)
        val btnCustomerCancel = view.findViewById<Button>(R.id.btnCustomerCancel)
        val btnAddCustomer = view.findViewById<Button>(R.id.btnAddCustomer)
        val btn_close_dialog = view.findViewById<ImageView>(R.id.btn_close_dialog)
        val btnCreateCustomer = view.findViewById<Button>(R.id.btnCreateCustomer)

        val ed_cus_mbl = view.findViewById<TextInputEditText>(R.id.ed_cus_mbl)
        val ed_cus_name = view.findViewById<TextInputEditText>(R.id.ed_cus_name)
        val ed_cus_gstin = view.findViewById<TextInputEditText>(R.id.ed_cus_gstin)
        val ed_cus_address = view.findViewById<TextInputEditText>(R.id.ed_cus_address)
        val ed_alt_cus_mbl = view.findViewById<TextInputEditText>(R.id.ed_alt_cus_mbl)

        posViewModel.searchCustomer("")
        /*
        * customer observable
        * */
        val listOfCustomer = ArrayList<LocalCustomer>()
        val customerAdapter = CustomerAdapter(this@PosMainActivity, listOfCustomer)

        posViewModel.cutomerListObservable.observe(this, Observer {
           if( listOfCustomer.size!=0)
               listOfCustomer.clear()
            if (it.size != 0) {
                it.forEach {
                    if (it.name != ANONYMOUS) {
                        listOfCustomer.add(it)
                    }
                    customerAdapter.notifyDataSetChanged()
                }

            }
        })
        val listView = view?.findViewById(R.id.lsCustomer) as ListView
        listView.adapter = customerAdapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val person = listOfCustomer[position]
                posViewModel.customer = person
                tvPerson.setText(person.name!!.toUpperCase() + " - " + person.phone)
                tvUserDebt.text = String.format("%.2f AED", person.totalCredit)
                svSearch.isIconified = true
                requestScanViewFocus()
                dialog.dismiss()
            }
        /*
        * customer observable end
        * */
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
        svSearch.setOnCloseListener(object : android.widget.SearchView.OnCloseListener,
            SearchView.OnCloseListener {
            override fun onClose(): Boolean {
//                clearCustomer()
                requestScanViewFocus()
                return false
            }

        })

        btnCustomerCancel.setOnClickListener {
            lvAddCus.visibility = View.GONE
            lvSearchCustomer.visibility = View.VISIBLE
        }
        btn_close_dialog.setOnClickListener {
            dialog.dismiss()
            requestScanViewFocus()

        }

        btnAddCustomer.setOnClickListener {
            lvAddCus.visibility = View.VISIBLE
            lvSearchCustomer.visibility = View.GONE
            //addNewCustomer()
            requestScanViewFocus()
        }

        btnCreateCustomer.setOnClickListener {
            var isValid = true
            if (ed_cus_mbl.text.toString().trim() == "" || ed_cus_mbl.text.toString().trim().length < 9) {
                ed_cus_mbl.error = resources.getString(R.string.err_phone)
                ed_cus_mbl.requestFocus()
                isValid = false
            }
            if (ed_cus_name.text.toString().trim() == "") {
                ed_cus_name.error = resources.getString(R.string.err_not_empty)
                ed_cus_name.requestFocus()
                isValid = false
            }

            if (isValid) {
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
                requestScanViewFocus()
                tvPerson.setText(customer.name!!.toUpperCase() + " - " + customer.phone)
                posViewModel.customer = customer
                tvUserDebt.text = getString(R.string.zero_aed)
                dialog.dismiss()
            }
        }

        dialog.show()
    }


    private fun clearCustomer() {
        posViewModel.customer = posViewModel.getAnonymousCustomer()
    }
    //endregion

    //region Utils

    private fun getBarCodedProduct(barcode: String) {
        posViewModel.searchByBarcode(barcode)
    }

    fun inStock(count: Int, stock: Int, varaintItem: LocalVariant): Boolean {
//         if (varaintItem.outOfStock.equals("1")) {
//             return false
//         } else {
//             if (varaintItem.unlimitedStock.equals("1")) {
//                 return true
//             } else
//
//         }
        return count <= stock
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.lvCategoryView -> {
                showInventory()

            }
            R.id.lvInventoryView -> {
                showCategories()


            }
            R.id.btnCustomerAdd -> {
                addCustomerDialog()
            }
            R.id.btnPay -> {
                /*  toastFlag = false
                  placeOrder(PAID, discountAmount)*/

                showPaymentCalculator()
            }
            R.id.ivCredit -> {
                toastFlag = false
                //placeOrder(CREDIT, discountAmount)

            }
            R.id.btnCancel -> {

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
                posCart.clearAllPosCart()
                posCart.clearAllWightedPosCart()

            }
            R.id.btnScan -> {
                toastFlag = true

                // lanuchScanCode(FullScannerActivity::class.java)
                if (scanCount == 1) {
                    getBarCodedProduct("8718429762806")
                    scanCount += 1
                } else
                    getBarCodedProduct("8718429762523")

//                edScan.setText("8718429762523")
            }
            R.id.ivDiscount,
            R.id.lvDiscount -> {
                if (cvCalculator.visibility == View.VISIBLE) {
                    /* if (isUserAdmin)
                         showActionPane()
                     else
                         showWeightedProd()*/
                    showCategories()
                } else {
                    showCalculator()
                    setUpCalculator()
                }

            }


            R.id.btnrecieve -> {
                posCart.clearAllPosCart()
                posCart.clearAllWightedPosCart()
                startActivityForResult(
                    Intent(this@PosMainActivity, ReceiveInventoryActivity::class.java),
                    UPDATE_VARIANT
                )

            }
            R.id.btnWeighted -> {
                lanuchActivity(WeightedProductsActivity::class.java)

            }
            R.id.btShowInventory -> {
                startActivityForResult(
                    Intent(this@PosMainActivity, LocalInventoryActivity::class.java),
                    UPDATE_VARIANT
                )
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }

            R.id.cvInventory ->
                lanuchActivity(InventoryHomeActivity::class.java)
            R.id.btnHoldOrder -> {
                holdOrder()
            }
            R.id.ivNext -> {
                getHoldedOrder(false)
            }
            R.id.ivPrevious -> {
                getHoldedOrder(true)
            }
            R.id.ivWeightedPrd -> {
                if (lvWeighed.visibility == View.GONE) {
                    showWeightedProd()
                } else
                    if (isUserAdmin)
                        showActionPane()
            }
            R.id.btnBack -> {
                showActionPane()
                lvWeighedVariant.visibility = View.GONE
                lvWeighedProducts.visibility = View.VISIBLE
                lvWeights.visibility = View.GONE
            }
        }
    }

    fun showCalculator() {
        cvCalculator.visibility = View.VISIBLE
        lvAction.visibility = View.GONE
        lvWeighed.visibility = View.GONE
        lvCategoryView.visibility = View.GONE
        lvInventoryView.visibility = View.GONE
        lvPaymentView.visibility = View.GONE

    }

    fun showPaymentCalculator() {
        setPaymentCalculator()
        lvAction.visibility = View.GONE
        lvCategoryView.visibility = View.GONE
        lvInventoryView.visibility = View.GONE
        cvCalculator.visibility = View.GONE
        lvPaymentView.visibility = View.VISIBLE

    }

    fun showCategories() {
        lvAction.visibility = View.VISIBLE
        lvCategoryView.visibility = View.VISIBLE
        lvInventoryView.visibility = View.GONE
        cvCalculator.visibility = View.GONE
        lvPaymentView.visibility = View.GONE
    }

    fun showInventory() {
        lvCategoryView.visibility = View.GONE
        lvInventoryView.visibility = View.VISIBLE
        lvPaymentView.visibility = View.GONE
    }

    fun showActionPane() {
        if (cvCalculator != null)
            cvCalculator.visibility = View.GONE
        lvWeighed.visibility = View.GONE
        lvAction.visibility = View.VISIBLE
    }

    fun showWeightedProd() {
        if (cvCalculator != null)
            cvCalculator.visibility = View.GONE
        lvAction.visibility = View.GONE
        lvWeights.visibility = View.GONE

        lvWeighed.visibility = View.VISIBLE
        launch {

            val categories = localProductRepository.loadStoreCategory()
            setUpCategoryRecyclerView(categories as ArrayList<StoreCategory>)
            if (categories.size != 0) {
                val subCategies = localProductRepository.loadSubCategoryByCategoryId(
                    categories[0].categoryId!!
                )
                setUpSubCategoryRecyclerView(subCategies as ArrayList<SubCategory>)
                if (subCategies.size != 0) {
                    val list =
                        localProductRepository.loadAllWeightedBySubcategoryId(subCategies[0].subcategoryId.toString())
                    setUpWeightedRecyclerView(list as ArrayList<LocalProduct>)
                }
            }
        }
    }

    fun requestScanViewFocus() {
        edScan.requestFocus()
        edScan.setInputType(InputType.TYPE_NULL)
        Utils.hideSoftKeyboard(this@PosMainActivity)
    }

    fun reset() {
        toastFlag = false
        posViewModel.customer = posViewModel.getAnonymousCustomer()
        posViewModel.orderItemList = ArrayList()
        posViewModel.subtotal = 0.00
        posViewModel.productBarCode.value = "-1"
        posViewModel.weightedVariantid.value = "-1"
        posViewModel.orderId = System.currentTimeMillis()
        clearCustomer()
        varaintList = ArrayList()
        setUpOrderRecyclerView(varaintList)
        tvTotal.setText(getString(R.string.zero_aed))
        tvDiscount.setText(getString(R.string.zero_aed))
        tvUserDebt.setText(getString(R.string.zero_aed))
        tvSubtotal.setText(getString(R.string.zero_aed))
        edBlnceDue.setText("")
        edBlnceChange.setText("")
        edBlnceTendered.setText("")
        tvPerson.text = ""
        discountAmount = 0.00
        tvOrderId.text =
            "# ${posViewModel.orderId.toString().substring(posViewModel.orderId.toString().length - 5)}"
        showCategories()
    }

    fun placeOrder(isCredit: Boolean) {

        val cash = edBlnceTendered.text.toString()
        val credit = edBlnceDue.text.toString()
        val isValidAmount = isvalidAmount(cash, credit)


        posViewModel.productBarCode.value = "-1"
        posViewModel.weightedVariantid.value = "-1"
        posCart.allorderItemsFromCart.forEach {
            posViewModel.orderItemList.add(it)
        }
        posCart.allWightedorderItemsFromCart.forEach {
            posViewModel.orderItemList.add(it)
        }
        posCart.clearAllPosCart()
        posCart.clearAllWightedPosCart()
        toastFlag = false

        if (posViewModel.subtotal < 1 || posViewModel.orderItemList.size == 0) {
            Utils.showMsg(this, "Please add products to place order")
        } else if (!isValidAmount.isEmpty()) {
            Utils.showMsg(this, isValidAmount)
        } else {


            if (isCredit) {
                posViewModel.placeOrder(discountAmount, cash, credit, Payment.CREDIT)
                return
            }
            if (!cash.isEmpty() && !credit.isEmpty()) {
                posViewModel.placeOrder(discountAmount, cash, credit, Payment.PARTIAL)
            }
            if (cash.isEmpty()) {
                posViewModel.placeOrder(discountAmount, cash, credit, Payment.CREDIT)
            }
            if (credit.isEmpty()) {
                posViewModel.placeOrder(discountAmount, cash, credit, Payment.CASH)
            }
        }

        //  posViewModel.placeOrder(payment, discountAmount)

    }

    //endregion

    //region Setting recyclerViews
    @SuppressLint("ClickableViewAccessibility")
    private fun setUpOrderRecyclerView(list: ArrayList<LocalVariant>) {
        rvProductList.layoutManager = LinearLayoutManager(this@PosMainActivity)
        rvProductList.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_order_place)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvProductName = mainView.findViewById<TextView>(R.id.tvProductName)
                val tvProductQty = mainView.findViewById<TextView>(R.id.etProductQty)
                val tvProductEach = mainView.findViewById<TextView>(R.id.tvProductEach)
                val tvProductTotal = mainView.findViewById<TextView>(R.id.tvProductTotal)
                val minusButton = mainView.findViewById<ImageButton>(R.id.minus_button)
                val addButton = mainView.findViewById<ImageButton>(R.id.plus_button)
                val orderItem: OrderItem
                val index: Int
                if (itemData.type == BAR_CODED_PRODUCT) {
                    orderItem = posCart.getOrderItemFromCartById(itemData.storeRangeId)
                    orderItem.type = BAR_CODED_PRODUCT
                    index = posCart.checkOrderItemInCart(orderItem.variantId)
                } else {
                    orderItem = posCart.getWightedOrderItemFromCartById(itemData.storeRangeId)
                    index = posCart.checkWightedOrderItemInCart(orderItem.variantId)
                    orderItem.type = WEIGHTED_PRODUCT
                }

                tvProductEach.setText(itemData.offerPrice)
                tvProductName.setText(itemData.productName)

                if (itemData.type == WEIGHTED_PRODUCT) {
                    minusButton.visibility = View.GONE
                    addButton.visibility = View.GONE
                    weightedOrder = LocalVariant() //reset the weightedOrder
                    orderItem.totalPrice = String.format("%.2f", orderItem.totalPrice).toDouble()
                    tvProductTotal.text = orderItem.totalPrice.toString()


                } else if (itemData.type == BAR_CODED_PRODUCT && orderItem.productQty != null) {
                    val price = orderItem.productQty!! * itemData.offerPrice!!.toDouble()
                    orderItem.totalPrice = String.format("%.2f", price).toDouble()
                    tvTotal.setText(String.format("%.2f AED", Math.abs(posViewModel.subtotal)))
                    tvProductTotal.setText(String.format("%.2f", Math.abs(price)))
                    tvProductQty.text = orderItem.productQty!!.toString()
                }

                tvTotal.setText(String.format("%.2f AED", posViewModel.subtotal))

                minusButton.setOnClickListener {
                    if (orderItem.productQty!! > 1) {
                        val price = posViewModel.subtotal - itemData.offerPrice!!.toDouble()
                        val count = orderItem.productQty!! - 1
                        orderItem.productQty = count
                        orderItem.totalPrice = price
                        tvProductTotal.setText(String.format("%.2f", price))
                        tvProductQty.setText(count.toString())
                        posViewModel.subtotal -= itemData.offerPrice!!.toDouble()
                        tvTotal.setText(String.format("%.2f AED", posViewModel.subtotal))
                        orderItem.productQty = orderItem.productQty
                        orderItem.totalPrice = String.format("%.2f", price).toDouble()

                        posCart.setOrderItemToCartAtIndex(index, orderItem)
                    } else {
                        it.setOnClickListener(null)
                        posViewModel.subtotal =
                            posViewModel.subtotal - itemData.offerPrice!!.toDouble()
                        // removeFromCart(orderItem)
                        posCart.removeSingleOrderItemPosCart(index)
                        varaintList.remove(itemData)
                        rvProductList.adapter!!.notifyDataSetChanged()
                    }
                    tvTotal.setText(String.format("%.2f AED", Math.abs(posViewModel.subtotal)))
                }

                //increment in orderItem quantity and sum in subtotal
                addButton.setOnClickListener {
                    if (inStock(
                            orderItem.productQty!! + 1,
                            itemData.stockBalance!!.toInt(),
                            itemData
                        )
                    ) {
//                        if (orderItem.productQty!! < 10) {
                        val count = orderItem.productQty!! + 1
                        orderItem.productQty = count

                        val price = orderItem.productQty!! * itemData.offerPrice!!.toDouble()
                        tvProductTotal.setText(String.format("%.2f", price))
                        orderItem.productQty = orderItem.productQty
                        tvProductQty.text = orderItem.productQty.toString()
                        posViewModel.subtotal += itemData.offerPrice!!.toDouble()
                        orderItem.totalPrice = String.format("%.2f", price).toDouble()
                        posCart.setOrderItemToCartAtIndex(index, orderItem)
//                        }
                        tvTotal.setText(String.format("%.2f AED", posViewModel.subtotal))
                    } else {
                        Utils.showMsgShortIntervel(this@PosMainActivity, "Stock limit exceeed")
                    }

                }


                //Remove from cart
                tvProductTotal.setOnTouchListener(object : View.OnTouchListener {
                    @SuppressLint("ClickableViewAccessibility")
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        val DRAWABLE_RIGHT = 2
                        if (event!!.action == 0) {
                            if (event.rawX >= tvProductTotal.getRight() - tvProductTotal.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) {

                                val popup = PopupMenu(this@PosMainActivity, tvProductTotal)
                                popup.inflate(R.menu.pop_up_prod_option_menu)
                                popup.setOnMenuItemClickListener(
                                    object : PopupMenu.OnMenuItemClickListener {
                                        override fun onMenuItemClick(item: MenuItem?): Boolean {
                                            when (item!!.itemId) {
                                                R.id.nav_remove_btn -> {
                                                    posViewModel.subtotal =
                                                        posViewModel.subtotal - tvProductTotal.text.toString().toDouble()
                                                    // removeFromCart(orderItem)
                                                    if (itemData.type == BAR_CODED_PRODUCT)
                                                        posCart.removeSingleOrderItemPosCart(index)
                                                    else
                                                        posCart.removeSingleWightedOrderItemPosCart(
                                                            index
                                                        )

                                                    varaintList.remove(itemData)
                                                    tvTotal.setText(
                                                        String.format(
                                                            "%.2f AED",
                                                            Math.abs(posViewModel.subtotal)
                                                        )
                                                    )
                                                    rvProductList.adapter!!.notifyDataSetChanged()
                                                    v!!.setOnTouchListener(null)
                                                    return false
                                                }
                                                R.id.nav_return_btn -> {
                                                    return false
                                                }
                                            }
                                            return true
                                        }
                                    }
                                )
                                popup.show()

                                return true
                            }
                        }
                        return false
                    }
                })


            }

    }

    private fun setUpWeightView(variant: LocalVariant) {

        btn_seven_weight.setOnClickListener {
            setWaitedOrder(btn_seven_weight.text.toString(), variant)
        }
        btn_eight_weight.setOnClickListener {
            setWaitedOrder(btn_eight_weight.text.toString(), variant)
        }
        btn_nine_weight.setOnClickListener {
            setWaitedOrder(btn_nine_weight.text.toString(), variant)
        }
        btn_four_weight.setOnClickListener {
            setWaitedOrder(btn_four_weight.text.toString(), variant)
        }
        btn_five_weight.setOnClickListener {
            setWaitedOrder(btn_five_weight.text.toString(), variant)
        }
        btn_six_weight.setOnClickListener {
            setWaitedOrder(btn_six_weight.text.toString(), variant)
        }
        btn_one_weight.setOnClickListener {
            setWaitedOrder(btn_one_weight.text.toString(), variant)
        }
        btn_two_weight.setOnClickListener {
            setWaitedOrder(btn_two_weight.text.toString(), variant)
        }
        btn_three_weight.setOnClickListener {
            setWaitedOrder(btn_three_weight.text.toString(), variant)
        }

        btn_seven_weight.setText("7 ${variant.unitName}")
        btn_eight_weight.setText("8 ${variant.unitName}")
        btn_nine_weight.setText("9 ${variant.unitName}")
        btn_four_weight.setText("4 ${variant.unitName}")
        btn_five_weight.setText("5 ${variant.unitName}")
        btn_six_weight.setText("6 ${variant.unitName}")
        btn_one_weight.setText("1 ${variant.unitName}")
        btn_two_weight.setText("2 ${variant.unitName}")
        btn_three_weight.setText("3 ${variant.unitName}")
    }

    private fun setWaitedOrder(weight: String, variant: LocalVariant) {
        val tmp = weight.split(variant.unitName!!)[0].trim()
        /*price = weight x price
        * added cart price would be the product of price and weight
        **/
        weightedOrder.offerPrice =
            (variant.offerPrice!!.toLong() * tmp.toLong()).toString()// Storing the total price
        weightedOrder.sku = tmp// Storing the weight in sku
        posViewModel.searchWeightedVariantByid(variant.storeRangeId)

        showWeightedProd()
    }

    private fun setUpWeightedRecyclerView(list: ArrayList<LocalProduct>) {
        if (list.size == 0) {
            tvNotFound.visibility = View.VISIBLE
            rvWeightedProductList.visibility = View.GONE
        } else {
            tvNotFound.visibility = View.GONE
            rvWeightedProductList.visibility = View.VISIBLE
            lvWeighedVariant.visibility = View.GONE
            lvWeighedProducts.visibility = View.VISIBLE
            lvWeights.visibility = View.GONE

        }
        rvWeightedProductList.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(this@PosMainActivity, 3)
        rvWeightedProductList.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val productItemTitle = mainView.findViewById<TextView>(R.id.product_item_title)
                val productItemOldPrice =
                    mainView.findViewById<TextView>(R.id.product_item_old_price)
                val product_item_weight_price =
                    mainView.findViewById<TextView>(R.id.product_item_weight_price)
                val btnDlt = mainView.findViewById<ImageView>(R.id.btnDlt)
                val btnEdt = mainView.findViewById<ImageView>(R.id.btnEdt)

                btnDlt.visibility = View.GONE
                btnEdt.visibility = View.GONE

                productItemTitle.text = itemData.productName
                productItemOldPrice.text = " ${itemData.smallDescription}"
                product_item_weight_price.text = "(${itemData.unitName})"

                mainView.setOnClickListener {
                    lvWeighedVariant.visibility = View.VISIBLE
                    lvWeighedProducts.visibility = View.GONE
                    localProductRepository.loadAllWeightedVaraintByProductId(itemData.storeProductId.toString())
                        .observe(this@PosMainActivity, Observer<List<LocalVariant>> {
                            setUpWeighedVariantRecyclerView(it as ArrayList<LocalVariant>)

                        })
                }
            }

    }

    private fun setUpWeighedVariantRecyclerView(list: ArrayList<LocalVariant>) {

        rvWeighedVariant.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(this@PosMainActivity, 3)
        rvWeighedVariant.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val productItemTitle = mainView.findViewById<TextView>(R.id.product_item_title)
                val productItemOldPrice =
                    mainView.findViewById<TextView>(R.id.product_item_old_price)
                val product_item_weight_price =
                    mainView.findViewById<TextView>(R.id.product_item_weight_price)
                val productItemNewPrice =
                    mainView.findViewById<TextView>(R.id.product_item_new_price)
                val btnDlt = mainView.findViewById<ImageView>(R.id.btnDlt)
                val btnEdt = mainView.findViewById<ImageView>(R.id.btnEdt)

                btnDlt.visibility = View.GONE
                btnEdt.visibility = View.GONE

                productItemTitle.text = "Varaint Id: ${itemData.storeRangeId}"
                productItemOldPrice.text = "Price: ${itemData.offerPrice}"
                productItemNewPrice.text = "Stock: ${itemData.stockBalance}"
                product_item_weight_price.text = "(${itemData.unitName})"
                mainView.setOnClickListener {
                    lvWeighed.visibility = View.GONE
                    lvWeights.visibility = View.VISIBLE
                    lvWeighedProducts.visibility = View.GONE
                    weightedOrder.storeRangeId = itemData.storeRangeId
                    weightedOrder.offerPrice = itemData.offerPrice
                    weightedOrder.unitName = itemData.unitName
                    weightedOrder.unlimitedStock = itemData.offerPrice
                    weightedOrder.stockBalance = itemData.offerPrice
                    weightedOrder.outOfStock = itemData.offerPrice
                    weightedOrder.productId = itemData.productId

                    setUpWeightView(itemData)
                }

            }

    }

    private fun setUpCategoryRecyclerView(list: ArrayList<StoreCategory>) {

        rvCategory.layoutManager =
            LinearLayoutManager(this@PosMainActivity, LinearLayout.HORIZONTAL, false)
        rvCategory.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_smallview)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val productItemTitle = mainView.findViewById<TextView>(R.id.product_item_title)
                productItemTitle.text = itemData.categoryName

                mainView.setOnClickListener {
                    launch {
                        val subCategies = localProductRepository.loadSubCategoryByCategoryId(
                            itemData.categoryId!!
                        )
                        setUpSubCategoryRecyclerView(subCategies as ArrayList<SubCategory>)
                    }
                }
            }

    }

    private fun setUpSubCategoryRecyclerView(list: ArrayList<SubCategory>) {

        launch {
            val tmp =
                localProductRepository.loadAllWeightedBySubcategoryId(list[0].subcategoryId.toString())
            setUpWeightedRecyclerView(tmp as ArrayList<LocalProduct>)
        }

        rvSubCategory.layoutManager =
            LinearLayoutManager(this@PosMainActivity, LinearLayout.HORIZONTAL, false)
        rvSubCategory.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_product_smallview)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val productItemTitle = mainView.findViewById<TextView>(R.id.product_item_title)
                val productItemIcon = mainView.findViewById<ImageView>(R.id.product_item_icon)
                productItemTitle.text = itemData.subcategoryName
                Picasso.get()
                    .load(itemData.subcategoryImage)
                    .error(R.drawable.no_image)
                    .into(productItemIcon)

                mainView.setOnClickListener {
                    launch {
                        val tmp =
                            localProductRepository.loadAllWeightedBySubcategoryId(itemData.subcategoryId.toString())
                        setUpWeightedRecyclerView(tmp as ArrayList<LocalProduct>)
                    }
                }

            }

    }

    //endregion

    //region activty life cycle
    override fun onPause() {
        super.onPause()

    }

    private fun userAccessView() {
        if (isUserAdmin) {

            // showActionPane()
        } else {
            //  showWeightedProd()
        }
    }

    override fun onResume() {
        super.onResume()
        requestScanViewFocus()
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        try {
            if (!FullScannerActivity.BARCODE.isEmpty()) {
                //getBarCodedProduct(FullScannerActivity.BARCODE)
            }
        } catch (ex: Exception) {
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == UPDATE_VARIANT) {
                reset()
                posCart.clearAllPosCart()
                posCart.clearAllWightedPosCart()
                toastFlag = false
            } else {
            }
        } catch (ex: Exception) {
            Timber.e("get error")

        }
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

    override fun createDeviceProtectedStorageContext(): Context {
        return super.createDeviceProtectedStorageContext()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val setting = menu!!.findItem(R.id.nav_setting)
        val inventory = menu.findItem(R.id.inventory_prod)
        val customerDashboard = menu.findItem(R.id.customerDashboard)
        val distributorDashboard = menu.findItem(R.id.distributorDashboard)

        if (!isUserAdmin) {
            setting.setVisible(false)
            inventory.setVisible(false)
            customerDashboard.setVisible(false)
            distributorDashboard.setVisible(false)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager
            .getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()

    }

    private fun lanuchActivity(clss: Class<*>) {
        val intent = Intent(this, clss)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_setting -> {
                lanuchActivity(SettingsActivity::class.java)
                finish()
            }
            R.id.inventory_prod ->
                lanuchActivity(InventoryHomeActivity::class.java)
            R.id.customerDashboard ->
                lanuchActivity(CustomerManagmentActivity::class.java)
            R.id.distributorDashboard ->
                lanuchActivity(DistributorsManagmentActivity::class.java)
            R.id.logout -> {
                Utils.logout(this@PosMainActivity)
                lanuchActivity(LoginActivity::class.java)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    //region Receipt generation

    fun createReceipt() {
        val dest = Utils.getPath(this@PosMainActivity) + posViewModel.orderId + ".pdf"
        if (File(dest).exists()) {
            File(dest).delete()
        }

        try {

            val document = Document()

            // Location to save
            PdfWriter.getInstance(document, FileOutputStream(dest))

            // Open to write
            document.open()

            // Document Settings
            val pagesize = Rectangle(288F, 720F)
            document.pageSize = pagesize
            document.addCreationDate()
            document.addAuthor(resources.getString(R.string.app_name))
            document.addCreator(resources.getString(R.string.app_name))


            val mValueFontSize = 15.0f

            /**
             * FONT....
             */
            val urName = BaseFont.createFont(
                "assets/fonts/oswald.ttf",
                "UTF-8", BaseFont.EMBEDDED
            )

            // LINE SEPARATOR
            val lineSeparator = LineSeparator()
            lineSeparator.lineColor = BaseColor(0, 0, 0, 40)

            // Title Order Details...
            // Adding Title....
            val mOrderDetailsTitleFont = Font(urName, 20.0f, Font.NORMAL, BaseColor.BLACK)
            val mOrderDetailsTitleChunk = Chunk("Order Details", mOrderDetailsTitleFont)
            val mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
            mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            document.add(mOrderDetailsTitleParagraph)

            val mOrderDateValueFont = Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK)
            val mOrderDateValueChunk = Chunk("Dated: ${Utils.getTodaysDate()}", mOrderDateValueFont)
            val mOrderDateValueParagraph = Paragraph(mOrderDateValueChunk)
            mOrderDateValueParagraph.alignment = Element.ALIGN_CENTER
            document.add(mOrderDateValueParagraph)
            document.add(Paragraph(" "))

            // Order Number..
            val style = Font(urName, 12.0f, Font.BOLD, BaseColor.BLACK)
            val orderNumChunk = Chunk("Order Number:${posViewModel.orderId}", style)
            val pkey = Paragraph(orderNumChunk)
            pkey.alignment = Element.ALIGN_LEFT
            document.add(pkey)
            document.add(Paragraph(" "))

            // Time...
            val now = Utils.getTimeNow()
            document.add(createParagraphWithTab("Time: ", now, ""))
            document.add(Paragraph(""))
            // Cusotmer...
            var customerName = "Not Known"
            if (posViewModel.customer.name != ANONYMOUS) {
                customerName = posViewModel.customer.name!!
            }
            document.add(createParagraphWithTab("Customer : ", customerName, ""))
            document.add(Paragraph(""))
            document.add(
                createParagraphWithTab(
                    "Number of Items : ",
                    posViewModel.orderItemList.size.toString(),
                    ""
                )
            )
            document.add(Paragraph(""))
            // Payment Type...
            document.add(
                createParagraphWithTab(
                    "Payment Type : ",
                    posViewModel.mPaymentType.toUpperCase(),
                    ""
                )
            )
            document.add(Chunk(lineSeparator))

            // product details
            posViewModel.orderItemList.forEach {
                document.add(Paragraph(""))
                val mproductValueFont = Font(urName, 12.0f, Font.NORMAL, BaseColor.BLACK)
                val mproductValueChunk = Chunk(it.productName, mproductValueFont)
                val mproductParagraph = Paragraph(mproductValueChunk)
                document.add(mproductParagraph)
                document.add(Paragraph(""))
                document.add(
                    createParagraphWithTab(
                        "1x${it.productQty}", "",
                        Utils.getOnlyTwoDecimal(it.totalPrice!!)
                    )
                )
                document.add(Paragraph(""))
            }

            // Total...
            document.add(Chunk(lineSeparator))
            document.add(
                createParagraphWithTab(
                    "Total: ",
                    "",
                    Utils.getOnlyTwoDecimal(posViewModel.subtotal)
                )
            )
            document.add(Paragraph(""))
            document.add(
                createParagraphWithTab(
                    "SubTotal: ",
                    "",
                    Utils.getOnlyTwoDecimal(posViewModel.subtotal + discountAmount)
                )
            )
            document.add(Paragraph(""))
            document.add(
                createParagraphWithTab(
                    "Discount: ",
                    "",
                    Utils.getOnlyTwoDecimal(discountAmount)
                )
            )
            document.add(Paragraph(" "))
            document.add(Chunk(lineSeparator))
            document.add(Paragraph(" "))
            document.add(Paragraph(" "))
            document.add(Paragraph(" "))
            document.add(Paragraph(" "))

            // footer Title....
            val footerDetailsTitleFont = Font(urName, 10.0f, Font.BOLD, BaseColor.BLACK)
            val footerDetailsTitleChunk = Chunk("CONTACT US:121212111", footerDetailsTitleFont)
            val footerDetailsTitleParagraph = Paragraph(footerDetailsTitleChunk)
            footerDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            document.add(footerDetailsTitleParagraph)

            document.add(Paragraph(""))


            document.close()

//            Toast.makeText(this@PosMainActivity, "Created", Toast.LENGTH_SHORT).show()

            //  Utils.openFile(this@PosMainActivity, File(dest))
            showPdfdialog(dest)
            print("createReceipt: destination " + dest)


        } catch (ie: IOException) {
            print("createReceipt: Error " + ie.localizedMessage)
        } catch (ie: DocumentException) {
            print("createReceipt: Error " + ie.localizedMessage)
        } catch (ae: ActivityNotFoundException) {
            Toast.makeText(
                this@PosMainActivity,
                "No application found to open this file.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun showPdfdialog(url: String) {
       val intent = Intent(this@PosMainActivity, PdfViewActivity::class.java)
        intent.putExtra("pdf_intent",url)
        startActivity(intent)


    }

    fun createParagraphWithTab(key: String, value: String, value1: String): Paragraph {
        val p = Paragraph()
        p.setTabSettings(TabSettings(230f))
        val mFont = BaseFont.createFont("assets/fonts/oswald.ttf", "UTF-8", BaseFont.EMBEDDED)

        val style = Font(mFont, 12.0f, Font.NORMAL, BaseColor.BLACK)
        val mKey = Chunk(key, style)
        val pkey = Paragraph(mKey)
        pkey.alignment = Element.ALIGN_LEFT

        val mValue = Chunk(value, style)
        val pValue = Paragraph(mValue)
        pValue.alignment = Element.ALIGN_LEFT

        val mValue1 = Chunk(value1, style)
        val pValue1 = Paragraph(mValue1)
        pValue1.alignment = Element.ALIGN_LEFT


        p.add(mKey)
        p.add(mValue)
        p.add(Chunk.TABBING)
        p.add(Chunk.TABBING)
        p.add(mValue1)
        return p
    }
    //endregion

    ////region Discount calculator handling
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
                if (isUserAdmin)
                    showActionPane()
                else
                    showWeightedProd()
                if (isCalulated) {
                    tvDiscount.setText(
                        String.format(
                            "%.2f AED",
                            tvCalTotal.text.toString().toDouble()
                        )
                    )
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
        // val res = amount-(amount*( discount/ 100.0f))
        tvCalTotal.text = String.format("%.2f", res)
    }
    //endregion


    ////region Payment handling

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setPaymentCalculator() {
        btnPaySeven.setOnClickListener(paymentOnClick)
        btnPayEight.setOnClickListener(paymentOnClick)
        btnPayNine.setOnClickListener(paymentOnClick)
        btnPayFour.setOnClickListener(paymentOnClick)
        btnPayFive.setOnClickListener(paymentOnClick)
        btnPaySix.setOnClickListener(paymentOnClick)
        btnPayOne.setOnClickListener(paymentOnClick)
        btnPayTwo.setOnClickListener(paymentOnClick)
        btnPayThree.setOnClickListener(paymentOnClick)
        btnPayCee.setOnClickListener(paymentOnClick)
        btnPayZero.setOnClickListener(paymentOnClick)
        btnPayPoint.setOnClickListener(paymentOnClick)
        btnPayCredit.setOnClickListener(paymentOnClick)
        btnPayCash.setOnClickListener(paymentOnClick)
        btnPayErase.setOnClickListener(paymentOnClick)

        btnCurrencyffty.setOnClickListener(paymentOnClick)
        btnPaymentDone.setOnClickListener(paymentOnClick)
        btnCurrencyten.setOnClickListener(paymentOnClick)
        btnCurrencytenty.setOnClickListener(paymentOnClick)
        btnCurrencyhdrd.setOnClickListener(paymentOnClick)
        btnCurrencyttHdrd.setOnClickListener(paymentOnClick)
        btnCurrencyfvHdrd.setOnClickListener(paymentOnClick)
        btnCurrencytwTh.setOnClickListener(paymentOnClick)
        btnCurrencyFive.setOnClickListener(paymentOnClick)
        btnCurrencyffty.setOnClickListener(paymentOnClick)
        btnPayBack.setOnClickListener(paymentOnClick)
        edBlnceDue.setShowSoftInputOnFocus(false)
        edBlnceChange.setShowSoftInputOnFocus(false)
        edBlnceTendered.setShowSoftInputOnFocus(false)
    }

    private val paymentOnClick = View.OnClickListener { view ->

        when (view.id) {
            R.id.btnPaymentDone -> {
                placeOrder(false)
            }
            R.id.btnPayCash -> {
                placeOrder(false)
            }
            R.id.btnPayErase -> {
//                calculateDiscount(2.0)
                eraseOneCharacter()
            }
            R.id.btnPayCredit -> {
                placeOrder(true)
            }
            R.id.btnPayCee -> {
                erasePaymentCal()
            }
            R.id.btnPayBack -> {
                showCategories()
            }

            R.id.btn_point -> {
                setPaymentText(view as Button)
            }
            else -> {
                setPaymentText(view as Button)
            }
        }
    }


    private fun setPaymentText(btn_point: Button) {
        var focused: EditText? = null

        if (edBlnceDue.isFocused) {
            focused = edBlnceDue
        } else if (edBlnceChange.isFocused) {
            focused = edBlnceChange

        } else if (edBlnceTendered.isFocused) {
            focused = edBlnceTendered
        }
        if (focused != null) {
            var text = "0"
            if (btn_point.text.toString() == ".") {
                if (!focused.text.toString().contains(".")) {
                    focused.setText(focused.text.toString() + btn_point.text.toString())
                }
            } else {
                if (btn_point.text.split(" ").size > 1) {
                    text = btn_point.text.split(" ")[1]
                } else {
                    text = focused.text.toString() + btn_point.text.toString()
                }
                if (focused.text.toString().trim().length < 7) {
                    focused.setText(text)
                }
            }
        }

    }


    private fun erasePaymentCal() {

        var focused: EditText? = null

        if (edBlnceDue.isFocused) {
            focused = edBlnceDue
        } else if (edBlnceChange.isFocused) {
            focused = edBlnceChange

        } else if (edBlnceTendered.isFocused) {
            focused = edBlnceTendered
        }
        if (focused != null) {
            focused.setText("")
        }

    }

    private fun eraseOneCharacter() {

        var focused: EditText? = null

        if (edBlnceDue.isFocused) {
            focused = edBlnceDue
        } else if (edBlnceChange.isFocused) {
            focused = edBlnceChange

        } else if (edBlnceTendered.isFocused) {
            focused = edBlnceTendered
        }
        if (focused != null) {
            if (!focused.text.isEmpty()) {
                val s = focused.text.toString()
                val txt = s.substring(0, s.length - 1)
                focused.setText(txt)
            }
        }

    }

    private fun isvalidAmount(cash: String, credit: String): String {
        val total = posViewModel.subtotal - discountAmount

        val tmpTotal = total.toString().split('.')[0].toLong()

        if (cash.isEmpty() && credit.isEmpty()) {

            return "Please Enter the cash or credit amount"
        }
        if (!cash.isEmpty() && !credit.isEmpty()) {
            val tmpCredit = credit.toString().split('.')[0].toLong()
            val tmpCash = cash.toString().split('.')[0].toLong()

            if (tmpCredit + tmpCash > total)
                return "Amount is greater than payable amount"
            else
                return ""
        }
        if (!cash.isEmpty()) {
            val tmpCash = cash.toString().split('.')[0].toLong()
            if (cash.toDouble() > total)
                return "Amount is greater than payable amount"
            else if (tmpCash != tmpTotal) {
                return "Amount is Smaller than payable amount"
            }
        }
        if (!credit.isEmpty()) {
            val tmpCredit = credit.toString().split('.')[0].toLong()

            if (credit.toDouble() > total)
                return "Amount is greater than payable amount"
            else if (tmpCredit != tmpTotal) {
                return "Amount is Smaller than payable amount"
            }
        }

        return ""
    }

    //endregion

}
