package com.goshoppi.pos.ui.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.budiyev.android.circularprogressbar.CircularProgressBar
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.creditHistoryRepo.CreditHistoryRepository
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class OverViewFragment : BaseFragment() {

    @Inject
    lateinit var distributorsRepository: DistributorsRepository
    @Inject
    lateinit var customerRepository: CustomerRepository
    @Inject
    lateinit var creditHistoryRepository: CreditHistoryRepository

    lateinit var rvDistributor: RecyclerView
    lateinit var rvCustomers: RecyclerView
    lateinit var tvPaymentReceived: TextView
    lateinit var tvCredit: TextView
    lateinit var tvCash: TextView
    lateinit var tvTotalSales: TextView
    lateinit var tvTodaySales: TextView
    lateinit var tvCustomerCredit: TextView
    lateinit var tvCalender: TextView
    lateinit var cvCusNoDate: ConstraintLayout
    lateinit var cvDistNoDate: ConstraintLayout
    lateinit var progress_delivery: CircularProgressBar
    var job: Job? = null
    lateinit var scope: CoroutineScope
    private var checkIfDoubleDateIsSelected = false
    var dateStart: Long = 0L
    var dateEnd: Long = 0L

    override fun layoutRes(): Int {
        return R.layout.fragment_over_view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = Job()
        scope = CoroutineScope(Dispatchers.Main + job!!)
        initView(view)
    }

    private fun initView(view: View) {
        rvDistributor = view.findViewById(R.id.rvDistributor)
        tvTodaySales = view.findViewById(R.id.tvTodaySales)
        tvPaymentReceived = view.findViewById(R.id.tvPaymentReceived)
        tvCash = view.findViewById(R.id.tvCash)
        tvCredit = view.findViewById(R.id.tvCredit)
        tvCustomerCredit = view.findViewById(R.id.tvCustomerCredit)
        tvTotalSales = view.findViewById(R.id.tvTotalSales)
        rvCustomers = view.findViewById(R.id.rvCustomers)
        progress_delivery = view.findViewById(R.id.progress_delivery)
        tvCalender = view.findViewById(R.id.tvCalender)
        cvCusNoDate = view.findViewById(R.id.cvCusNoDate)
        cvDistNoDate = view.findViewById(R.id.cvDistNoDate)

        distributorsRepository.loadAllDistributor().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (isAdded() && isVisible() && getUserVisibleHint()) {
                    setUpDistributorRecyclerView(it as ArrayList<Distributor>)

                }
            }
        })

        customerRepository.loadAllLocalCustomer().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (isAdded() && isVisible() && getUserVisibleHint())
                    setUpCustomerRecyclerView(it)
            }
        })
        tvCalender.setOnClickListener {
            showCalenderDialog()
        }
        progress_delivery.progress = 30f
        scope.async(Dispatchers.IO) {
            val totalCash = creditHistoryRepository.loadTotalPaidHistory()
            val totalCredit = creditHistoryRepository.loadTotalCredit()
            val totalSales = creditHistoryRepository.totalSales()
            tvPaymentReceived.text = String.format("%.2f AED", totalCash)
            tvCash.text = tvCash.text.toString() + ": " + String.format("%.2f ", totalCash)
            tvCredit.text = tvCredit.text.toString() + ": " + String.format("%.2f", totalCredit)
            tvCustomerCredit.text = String.format("%.2f", totalCredit)
            tvTotalSales.text = String.format("%.2f AED", totalSales)
            tvTodaySales.text = String.format("%.2f AED", totalSales)

        }
    }

    private fun showCalenderDialog() {
        val view: View = LayoutInflater.from(activity!!).inflate(R.layout.custom_date_picker, null)
        val alertBox = AlertDialog.Builder(activity!!)
        alertBox.setView(view)
        alertBox.setCancelable(false)
        val dialog = alertBox.create()
        val calender: DateRangeCalendarView = view.findViewById(R.id.custom_calendar)
        val containerTo: LinearLayout = view.findViewById(R.id.citizen_custom_calender_contianer_to)
        val txtFromYear: TextView = view.findViewById(R.id.tv_calender_from_year)
        val txtFromDate: TextView = view.findViewById(R.id.tv_calender_from_date)
        val txtFromLabel: TextView = view.findViewById(R.id.tv_calender_from_label)

        val txtToYear: TextView = view.findViewById(R.id.tvcalender_to_year)
        val txtToDate: TextView = view.findViewById(R.id.tv_calender_to_date)

        val btnCancel: Button = view.findViewById(R.id.btn_citizen_custom_calender_cancel)
        val btnClear: Button = view.findViewById(R.id.btn_custom_calender_clear)
        val btnSet: Button = view.findViewById(R.id.btn_custom_calender_set)

        btnClear.setOnClickListener {
            //calender.resetAllSelectedViews()
            containerTo.visibility = View.GONE
            txtFromLabel.text = "Choose Date"
            txtFromYear.text = ""
            txtFromDate.text = ""

        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSet.setOnClickListener {
            if (checkIfDoubleDateIsSelected) {
                Utils.showLoading(true, activity!!)

                val sd = Date(dateStart)
                val ed = Date(dateEnd)

                var record = scope.async(Dispatchers.IO) {
                    val totalCash = creditHistoryRepository.loadTotalPaidHistoryByDate(sd, ed)
                    val totalCredit = creditHistoryRepository.loadTotalCreditByDate(sd, ed)
                    val totalSales = creditHistoryRepository.totalSalesByDate(sd, ed)
                    val distributorList = distributorsRepository.loadAllDistributorBydate(sd, ed)
                    val cusList = customerRepository.loadAllLocalCustomerByDate(sd, ed)

                    scope.launch(Dispatchers.Main) {
                        tvPaymentReceived.text = String.format("%.2f AED", totalCash)
                        tvCash.text = "Cash: " + String.format("%.2f ", totalCash)
                        tvCredit.text =
                            "Credit: " + String.format("%.2f", totalCredit)
                        tvCustomerCredit.text = String.format("%.2f", totalCredit)
                        tvTotalSales.text = String.format("%.2f AED", totalSales)
                        tvTodaySales.text = String.format("%.2f AED", totalSales)
                        setUpDistributorRecyclerView(distributorList as ArrayList<Distributor>)
                        setUpCustomerRecyclerView(cusList as ArrayList)
                        Utils.hideLoading()

                    }
                }
                reset()
            } else {
                checkIfDoubleDateIsSelected = false
            }
            dialog.dismiss()
        }

        calender.setCalendarListener(object : DateRangeCalendarView.CalendarListener {
            override fun onFirstDateSelected(startDate: Calendar) {

            }

            override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
                containerTo.visibility = View.VISIBLE
                txtFromLabel.text = "From"
                txtToYear.text = endDate.get(Calendar.YEAR).toString()
                val dateFormat = SimpleDateFormat("E,MMM d")
                txtToDate.text = dateFormat.format(endDate.time)

                val sDate = java.sql.Date(
                    startDate.get(
                        Calendar.YEAR
                    ) - 1900,
                    startDate.get(Calendar.MONTH),
                    startDate.get(Calendar.DAY_OF_MONTH)
                )
                endDate.set(Calendar.HOUR_OF_DAY, 23)
                endDate.set(Calendar.MINUTE, 59)
                endDate.set(Calendar.SECOND, 59)
                endDate.set(Calendar.MILLISECOND, 999)
                val eDate = java.sql.Date(endDate.timeInMillis)
                dateStart = sDate.time
                dateEnd = eDate.time
                checkIfDoubleDateIsSelected = dateStart != null && dateEnd != null

            }
        })

        dialog.show()
    }

    private fun setUpDistributorRecyclerView(list: ArrayList<Distributor>) {
        if (list.size == 0) {
            cvDistNoDate.visibility = View.VISIBLE
        } else
            cvDistNoDate.visibility = View.INVISIBLE
        rvDistributor.layoutManager =
            LinearLayoutManager(activity!!)
        rvDistributor.adapter =
            RecyclerViewGeneralAdapter(list, R.layout.single_overview_distributor)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvName = mainView.findViewById<TextView>(R.id.tvName)
                val tvDebt = mainView.findViewById<TextView>(R.id.tvDue)
                val tvDueDate = mainView.findViewById<TextView>(R.id.tvDueDate)

                tvDebt.text = String.format("%.2f AED", itemData.totalCredit)
                tvName.text = itemData.name


            }
    }

    private fun setUpCustomerRecyclerView(list: List<LocalCustomer>) {

        val filtered = list.filter { s -> s.name != Constants.ANONYMOUS }

        if (filtered.size == 0) {
            cvCusNoDate.visibility = View.VISIBLE
        } else
            cvCusNoDate.visibility = View.INVISIBLE

        rvCustomers.layoutManager =
            LinearLayoutManager(activity!!)
        rvCustomers.adapter =
            RecyclerViewGeneralAdapter(filtered as ArrayList, R.layout.single_overview_customer)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvName = mainView.findViewById<TextView>(R.id.tvName)
                val tvDue = mainView.findViewById<TextView>(R.id.tvDue)
                val btnRemind = mainView.findViewById<Button>(R.id.btnRemind)
                tvDue.text = String.format("%.2f", itemData.totalCredit)
                tvName.text = itemData.name

            }
    }


    private fun reset() {

        dateStart = 0L
        dateEnd = 0L
        checkIfDoubleDateIsSelected = false

    }

    override fun onDetach() {
        if (job != null) job!!.cancel()
        super.onDetach()
        /*  var record:Deferred<Unit> ?=null

          btnSet.setOnClickListener {
                  val sd =Date(dateStart)
                  val ed =Date(dateEnd)
                  record =scope.async(Dispatchers.IO) {
                      val totalCash = creditHistoryRepository.loadTotalPaidHistoryByDate(sd,ed)
                  }
              if(record.isCompleted){

              }
          }*/

    }

}


