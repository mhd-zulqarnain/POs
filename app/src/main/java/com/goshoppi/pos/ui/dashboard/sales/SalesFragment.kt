package com.goshoppi.pos.ui.dashboard.sales

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.utils.Utils
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SalesFragment : BaseFragment() {
    override fun layoutRes(): Int {
        return R.layout.customer_sales_fragment
    }

    lateinit var chart: BarChart
    @Inject
    lateinit var productRepository: LocalProductRepository
    var job: Job ?=null
    lateinit var scope:CoroutineScope
    lateinit var btnByMonth: Button
    lateinit var btnByWeek: Button
    lateinit var txtReportAbout: TextView
    lateinit var lvNoData: TextView
val dateFormat =Utils.dateFormat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = Job()
        scope = CoroutineScope(Dispatchers.Main + job!!)
        chart = view.findViewById(R.id.chart1)
        btnByMonth = view.findViewById(R.id.btnByMonth)
        btnByWeek = view.findViewById(R.id.btnByWeek)
        txtReportAbout = view.findViewById(R.id.txtReportAbout)
        lvNoData = view.findViewById(R.id.lvNoData)
        generateBarData(Filter.MONTH)

        btnByMonth.setOnClickListener {
            generateBarData(Filter.MONTH)
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
            generateBarData(Filter.WEEK)

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

    }

    /*getting the order by date
    * and use their sum of total amount to
    * calculte sales per week/month */

    private fun generateBarData(type: Filter) {
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
        val month = cal.get(Calendar.MONTH) + 1


        scope.launch {
            Utils.showLoading(true, activity!!)
            withContext(Dispatchers.IO) {
                if (type == Filter.MONTH) {
                    val days = Utils.getDatesInMonth(year, month)
                    val labels = days
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    days.forEachIndexed { index, obj ->
                        val date = dateFormat.parse(obj)
                        val sale = productRepository.getAmountOfSalesByDay(date)
                        if (sale != 0.00)
                            entries.add(BarEntry(index.toFloat(), sale.toFloat()))
                    }

                } else {
                    val days = Utils.getDateofthisWeek(year)
                    days.forEachIndexed { index, obj ->
                        val date = dateFormat.parse(obj)
                        val sale = productRepository.getAmountOfSalesByDay(date)
                        if (sale != 0.00)
                            entries.add(BarEntry(index.toFloat(), sale.toFloat()))

                    }
                    val labels = days
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

                }
            }

            Utils.hideLoading()


            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            chart.setDrawGridBackground(false)
            chart.axisLeft.isEnabled = false
            chart.axisRight.isEnabled = false
            chart.description.isEnabled = false

            if (entries.size == 0) {
                lvNoData.visibility=View.VISIBLE
                chart.visibility = View.GONE
            }else
            {
                lvNoData.visibility=View.GONE
                chart.visibility = View.VISIBLE
            }
            val set = BarDataSet(entries, "Sales of this ${type}")
            set.valueTextSize = 12f
            chart.data = BarData(set)
            chart.invalidate()

        }

    }


    enum class Filter {
        WEEK, MONTH;

        override fun toString(): String {
            when (this) {
                WEEK -> return "week"
                MONTH -> return "month"
            }
        }
    }
    override fun onDetach() {
        if(job!=null) job!!.cancel()
        super.onDetach()
    }
}