package com.goshoppi.pos.ui.dashboard


import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.utils.Utils
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.coroutines.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class AdminCashFragment : BaseFragment() {
    override fun layoutRes(): Int {

        return R.layout.fragment_admin_cash
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_cash, container, false)
    }


    lateinit var chart: BarChart
    @Inject
    lateinit var productRepository: LocalProductRepository
    var job: Job ?=null
    lateinit var scope:CoroutineScope
    lateinit var btnByMonth: Button
    lateinit var btnByWeek: Button
    lateinit var txtReportAbout: TextView
    lateinit var rvSales: RecyclerView
    lateinit var spMonth: AppCompatSpinner


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = Job()
        scope = CoroutineScope(Dispatchers.Main + job!!)
        chart = view.findViewById(R.id.chart1)
        btnByMonth = view.findViewById(R.id.btnByMonth)
        btnByWeek = view.findViewById(R.id.btnByWeek)
        txtReportAbout = view.findViewById(R.id.txtReportAbout)
        rvSales = view.findViewById(R.id.rvSales)
        spMonth = view.findViewById(R.id.spMonth)

        //getDatesInMonth(2017, 9)
//        chart.zoom(2f, 2f, 2f, 5f)
        val cal = Calendar.getInstance()
        var month = cal.get(Calendar.MONTH) + 1

        generateBarData(Filter.MONTH,month)

        btnByMonth.setOnClickListener {
            chart.invalidate()
            generateBarData(Filter.MONTH,month)
            btnByMonth.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            btnByWeek.setTextColor(ContextCompat.getColor(activity!!, R.color.black))

            btnByWeek.setBackground(
                ContextCompat.getDrawable(
                    activity!!,
                    R.drawable.bg_black_bordered
                )
            )
            btnByMonth.setBackground(ContextCompat.getDrawable(activity!!, R.color.blue))

        }

        btnByWeek.setOnClickListener {
            chart.invalidate()
            generateBarData(Filter.WEEK,month)

            btnByWeek.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            btnByMonth.setTextColor(ContextCompat.getColor(activity!!, R.color.black))

            btnByMonth.setBackground(
                ContextCompat.getDrawable(
                    activity!!,
                    R.drawable.bg_black_bordered
                )
            )
            btnByWeek.setBackground(ContextCompat.getDrawable(activity!!, R.color.blue))
        }
//        spMonth.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        spMonth.setOnItemSelectedListener (object :AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.e("position none")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position ==0){
                    month = cal.get(Calendar.MONTH) + 1
                }else
                    month = position
                generateBarData(Filter.MONTH,month)

            }

        })
    }

    /*getting the order by date
    * and use their sum of total amount to
    * calculte sales per week/month */
    private fun setSalesRecycler(list: ArrayList<String>, datalist: ArrayList<OrderItem>) {

        rvSales.layoutManager = LinearLayoutManager(activity!!)
        rvSales.adapter = RecyclerViewGeneralAdapter(
            list,
            R.layout.single_row_cash_detail
        ) { itemData, viewHolder ->
            val mainView = viewHolder.itemView
            val tvDate = mainView.findViewById<TextView>(R.id.tvName)
            val tvPhone = mainView.findViewById<TextView>(R.id.tvPhone)
            val tvDueDate = mainView.findViewById<TextView>(R.id.tvDueDate)
            val tvOutStanding = mainView.findViewById<TextView>(R.id.tvOutStanding)
            var sales = 0.00
            val date =Utils.dateFormat.parse(itemData)
            datalist.forEach {
                if (it.addedDate!!.equals(date)) {
                    sales += it.totalPrice!!
                }
            }
            tvDate.text = itemData
            tvPhone.text =getString(R.string.zero_aed)
            tvDueDate.text = getString(R.string.zero_aed)
            tvOutStanding.text = "$sales AED"


        }
    }

    fun generateBarData(type: Filter ,month :Int) {
        val cal = Calendar.getInstance()

        val leftAxis = chart.axisLeft
        val rightAxis = chart.axisRight
        val xAxis = chart.xAxis

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(false)


        leftAxis.textSize = 10f
        leftAxis.setDrawLabels(false)
        leftAxis.setDrawAxisLine(true)
        leftAxis.setDrawGridLines(false)

        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawLabels(false)

        val entries = arrayListOf<BarEntry>()
        val year = cal.get(Calendar.YEAR)


        scope.launch {
            val listData = arrayListOf<OrderItem>()
            withContext(Dispatchers.IO) {
                if (type == Filter.MONTH) {

                    val days = Utils.getDatesInMonth(year, month)
                    val labels = days
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    days.forEachIndexed { index, obj ->
                        val date =Utils.dateFormat.parse(obj)
                        val sale = productRepository.getAmountOfSalesByDay(date)
                        val saleList = productRepository.getSalesByDay(date)
                        listData.addAll(saleList)

                        if (sale != 0.00)
                            entries.add(BarEntry(index.toFloat(), sale.toFloat()))

                    }
                    withContext(Dispatchers.Main) {
                        setSalesRecycler(days, listData)

                    }

                } else {
                    val days = Utils.getDateofthisWeek(year)
                    days.forEachIndexed { index, obj ->
                        val date = Utils.dateFormat.parse(obj)
                        val sale = productRepository.getAmountOfSalesByDay(date)
                        val saleList = productRepository.getSalesByDay(date)
                        listData.addAll(saleList)
                        if (sale != 0.00)
                            entries.add(BarEntry(index.toFloat(), sale.toFloat()))

                    }

                    val labels = days

                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    withContext(Dispatchers.Main) {
                        setSalesRecycler(days, listData)

                    }

                }


            }
            Utils.hideLoading()
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            chart.setDrawGridBackground(false)
            chart.axisLeft.isEnabled = false
            chart.axisRight.isEnabled = false
            chart.description.isEnabled = false
            val spaces = 1f

            /*  val entries = arrayListOf(
                  BarEntry(0f * spaces, 10f),
                  BarEntry(1f * spaces, 20f),
                  BarEntry(2f * spaces, 30f),
                  BarEntry(3f * spaces, 40f),
                  BarEntry(4f * spaces, 50f)
              )*/
            if (entries.size == 0) {
                Utils.showMsg(activity!!, "No transaction found")
            }
            val set = BarDataSet(entries, "Sales of this ${type}")
            set.valueTextSize = 12f
            chart.data = BarData(set)
//        chart.data.barWidth = 2f
            chart.invalidate()
            /*  chart.data = BarDat
              // make this BarData object grouped
      */
        }

    }


    enum class Filter {
        WEEK, MONTH;

        override fun toString(): String {
            when (this) {
                WEEK -> return "week"
                MONTH -> return "month"
            }
            return ""
        }
    }

    override fun onDetach() {
       if(job!=null) job!!.cancel()
        super.onDetach()
    }
}
