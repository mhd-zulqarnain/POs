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

class CustomerFootfallFragment : BaseFragment() {
    override fun layoutRes(): Int {
        return R.layout.customer_footfall_fragment
    }

    lateinit var chart: BarChart
    @Inject
    lateinit var productRepository: LocalProductRepository
    val job: Job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)
    lateinit var btnByMonth: Button
    lateinit var btnByWeek: Button
    lateinit var txtReportAbout: TextView
    lateinit var lvNoData: TextView
val dateFormat =SimpleDateFormat("MM/dd/yyyy")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    val days = getDatesInMonth(year, month)
                    val labels = days
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    days.forEachIndexed { index, obj ->
                        val date = dateFormat.parse(obj)
                        val sale = productRepository.getNumberOfSalesByDay(date)
                        if (sale != 0.00)
                            entries.add(BarEntry(index.toFloat(), sale.toFloat()))
                    }

                } else {
                    val days = getDateofthisWeek(year)
                    days.forEachIndexed { index, obj ->
                        val date = dateFormat.parse(obj)
                        val sale = productRepository.getNumberOfSalesByDay(date)
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

    fun getDatesInMonth(year: Int, month: Int): ArrayList<String> {
        val cal = Calendar.getInstance()
        val fmt = SimpleDateFormat(
            activity!!.resources.getString(R.string.pos_date_format),
            Locale.getDefault()
        )
        cal.clear()
        cal.set(year, month - 1, 1)
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val arr = arrayListOf<String>()

        for (i in 0 until daysInMonth) {
            System.out.println(fmt.format(cal.getTime()))
            cal.add(Calendar.DAY_OF_MONTH, 1)
            arr.add(fmt.format(cal.getTime()))
        }
        return arr
    }

    fun getDateofthisWeek(year: Int): ArrayList<String> {
        val cal = Calendar.getInstance()
        val today = SimpleDateFormat("dd", Locale.getDefault()).format(Date()).toInt()
        val month = cal.get(Calendar.MONTH) + 1


        val arr = arrayListOf<String>()
        val upperLimit:Int
        val lowerLimit:Int
        if(today<8){
            upperLimit = 8
            lowerLimit = 1
        }else{
            upperLimit = today+1
            lowerLimit = today-7
        }
        for (i in lowerLimit until upperLimit) {
            val input_date="$month/$i/$year"
            arr.add(input_date)
        }
        return arr
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
}