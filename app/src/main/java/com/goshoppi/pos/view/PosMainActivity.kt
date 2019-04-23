package com.goshoppi.pos.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.utils.Constants.*
import com.goshoppi.pos.utils.CustomerAdapter
import com.goshoppi.pos.utils.FullScannerActivity
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.view.auth.LoginActivity
import com.goshoppi.pos.view.customer.CustomerManagmentActivity
import com.goshoppi.pos.view.inventory.InventoryHomeActivity
import com.goshoppi.pos.view.inventory.LocalInventoryActivity
import com.goshoppi.pos.view.settings.SettingsActivity
import com.goshoppi.pos.view.user.AddUserActivity
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.ishaquehassan.recyclerviewgeneraladapter.addListDivider
import kotlinx.android.synthetic.main.activity_pos_main.*
import kotlinx.android.synthetic.main.include_add_customer.*
import kotlinx.android.synthetic.main.include_customer_search.*
import kotlinx.android.synthetic.main.include_discount_cal.*
import timber.log.Timber
import javax.inject.Inject


@Suppress("DEPRECATION")
class PosMainActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun layoutRes(): Int {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setAppTheme(sharedPref)
        return R.layout.activity_pos_main
    }

    private lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var masterProductRepository: MasterProductRepository

    @Inject
    lateinit var localProductRepository: LocalProductRepository

    @Inject
    lateinit var localCustomerRepository: CustomerRepository

    @Inject
    lateinit var workerFactory: WorkerFactory

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private var createPopupOnce = true
    private var inflater: LayoutInflater? = null
    private var popupWindow: PopupWindow? = null
    lateinit var productList: ArrayList<LocalProduct>
    val ZBAR_CAMERA_PERMISSION = 12
    lateinit var posViewModel: PosMainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        posViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(PosMainViewModel::class.java)
        initView()


        /* doAsync {
             getProductList()
         }
 */
        /*   dummyFragment.setOnClickListener {
            supportFragmentManager.beginTransaction().add(R.id.screenContainer, DummyFragment()).commit()
        }*/
/*
        getBarCodedProduct("8718429757901")
        getBarCodedProduct("8718429757901")*/


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
        productList = ArrayList()
        setUpOrderRecyclerView(productList)

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

        inflater = this@PosMainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater?.inflate(R.layout.spinner_list, null)
        popupWindow =
            PopupWindow(
                layout,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false
            )

        cvInventory.setOnClickListener {
            startActivity(Intent(this@PosMainActivity, InventoryHomeActivity::class.java))
        }
        btnAddUser.setOnClickListener {
            startActivity(Intent(this@PosMainActivity, AddUserActivity::class.java))
        }
        btShowInventory.setOnClickListener {
            startActivity(Intent(this@PosMainActivity, LocalInventoryActivity::class.java))
        }
        ivAddCustomer.setOnClickListener {
            lvAddCus.visibility = View.VISIBLE
            ed_cus_mbl.requestFocus()
            ed_cus_mbl.isFocusableInTouchMode = true
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(ed_cus_mbl, InputMethodManager.SHOW_IMPLICIT)
        }
        btn_add_customer.setOnClickListener {
            addNewCustomer()
        }
        btn_cancel.setOnClickListener {
            lvAddCus.visibility = View.GONE

        }
        ivClose.setOnClickListener {
            lvUserDetails.visibility = View.GONE
            svSearch.visibility = View.VISIBLE
        }
        tvDiscount.setOnClickListener {
            cvCalculator.visibility = View.VISIBLE
            lvAction.visibility = View.GONE
            setUpCalculator()
        }

        btnScan.setOnClickListener {
            //            lanuchScanCode(FullScannerActivity::class.java)
            getBarCodedProduct("8718429762523")

        }
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                svSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(param: String?): Boolean {
                if (param != null && param != "") {
                    if (createPopupOnce) {
                        popupWindow?.update(0, 0, svSearch.width, LinearLayout.LayoutParams.WRAP_CONTENT)
                        popupWindow?.showAsDropDown(svSearch, 0, 0)
                        createPopupOnce = false
                    }
                    val listOfCustomer =
                        localCustomerRepository.searchLocalStaticCustomers(param) as ArrayList<LocalCustomer>

                    val locationAdapter = CustomerAdapter(this@PosMainActivity, listOfCustomer)
                    val listView = layout?.findViewById(R.id.lvMenu) as ListView
                    listView.adapter = locationAdapter
                    listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
                        tvPerson.text = listOfCustomer[position].name
                        lvUserDetails.visibility = View.VISIBLE

                        svSearch.setQuery("", false)
                        svSearch.isIconified = true
                        svSearch.visibility = View.GONE

                        popupWindow?.dismiss()
                        if (listOfCustomer.size == 0)
                            Utils.showMsg(this@PosMainActivity, "No match found")

                    }

                } else {
                    createPopupOnce = true
                    popupWindow?.dismiss()
                }

                return true
            }
        })
        svSearch.setOnCloseListener(object : android.widget.SearchView.OnCloseListener, SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                clearCustomer()
                return false
            }

        })
        posViewModel.items.observe(this, Observer {
            if (it == null) {
                Utils.showMsg(this@PosMainActivity, "No match product found")
            } else {
                posViewModel.totalAmount += it.offerPrice!!.toDouble()
                productList.add(it)
                rvProductList.adapter!!.notifyDataSetChanged()
            }
        })

        btnPay.setOnClickListener {
            posViewModel.placeOrder(productList)
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

        localCustomerRepository.insertLocalCustomer(customer)
        Utils.showMsg(this, "Customer added")
        lvAddCus.visibility = View.GONE

        Utils.hideSoftKeyboard(this)
        tvPerson.text = customer.name
        lvUserDetails.visibility = View.VISIBLE
        svSearch.visibility = View.GONE

        posViewModel.customer = customer

    }

    private fun clearCustomer() {
        posViewModel.customer = null
    }

    fun addToCart(order: OrderItem){

        posViewModel.orderItemList.add(order)
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

    override fun onResume() {

        super.onResume()
        try {
            if (!FullScannerActivity.BARCODE.isEmpty()) {
                getBarCodedProduct(FullScannerActivity.BARCODE)
            }
        } catch (ex: Exception) {
        }

    }

    private fun getBarCodedProduct(barcode: String) {

        posViewModel.search(barcode)

    }

    private fun setUpOrderRecyclerView(list: ArrayList<LocalProduct>) {

        rvProductList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@PosMainActivity)
        rvProductList.addListDivider()
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
                var count = 1

                val orderItem = OrderItem()
                orderItem.orderId = 0
                orderItem.productId = 0
                orderItem.productQty = 0
                orderItem.mrp =itemData.productMrp
               /* orderItem.totalPrice = if(itemData.offerPrice.to!=null) else itemData.offerPrice.toDouble()
                orderItem.taxAmount = 0
                orderItem.addedDate = 0*/

                tvProductName.text = itemData.productName
                tvProductQty.text = "1"
                tvProductEach.text = itemData.offerPrice
                tvProductTotal.text = itemData.offerPrice
//                posViewModel.totalAmount += itemData.offerPrice!!.toDouble()
                tvTotal.setText(String.format("%.2f AED", posViewModel.totalAmount))
                minus_button.setOnClickListener {
                    if (count > 1) {
                        count -= 1
                        val price = tvProductTotal.text.toString().toDouble() - itemData.offerPrice!!.toDouble()
                        tvProductTotal.setText(String.format("%.2f", price))
                        tvProductQty.setText(count.toString())
                        posViewModel.totalAmount -= itemData.offerPrice!!.toDouble()
                        tvTotal.setText(String.format("%.2f AED", posViewModel.totalAmount))

                    } else {
                        posViewModel.totalAmount = posViewModel.totalAmount - itemData.offerPrice!!.toDouble()
                        val pos = viewHolder.position
                        list.remove(itemData)
                        rvProductList.adapter!!.notifyItemRemoved(pos)

                    }
                    tvTotal.setText(String.format("%.2f AED", Math.abs(posViewModel.totalAmount)))

                }
                add_button.setOnClickListener {
                    if (count < 10) {
                        count += 1
                        val price = count * itemData.offerPrice!!.toDouble()
                        tvProductTotal.setText(String.format("%.2f", price))
                        tvProductQty.setText(count.toString())
                        posViewModel.totalAmount += itemData.offerPrice!!.toDouble()
                    }
                    tvTotal.setText(String.format("%.2f AED", posViewModel.totalAmount))

                }


            }
    }

    private fun lanuchScanCode(clss: Class<*>) =
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA), ZBAR_CAMERA_PERMISSION
            )
        } else {
            val intent = Intent(this, clss)
            startActivity(intent)
        }

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
                            posViewModel.totalAmount - tvCalTotal.text.toString().toDouble()
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
                        Utils.showMsg(this@PosMainActivity, "Invalid Entry")
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
        val amount = posViewModel.totalAmount
        val res = (amount / 100.0f) * discount
//        val res = amount-(amount*( discount/ 100.0f))
        tvCalTotal.text = String.format("%.2f", res)
    }
    //</editor-fold>
}
