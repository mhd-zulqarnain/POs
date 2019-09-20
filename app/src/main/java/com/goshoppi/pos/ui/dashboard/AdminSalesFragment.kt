package com.goshoppi.pos.ui.dashboard


import android.os.Bundle
import android.view.View
import android.widget.Button
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.utils.Utils
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class AdminSalesFragment : BaseFragment() {

    lateinit var chart: BarChart
    @Inject
    lateinit var productRepository: LocalProductRepository
    val job: Job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)
    lateinit var btnDay:Button

    override fun layoutRes(): Int {

        return R.layout.fragment_admin_sales
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart = view.findViewById(R.id.chart1)
        btnDay = view.findViewById(R.id.btnDay)
        //printDatesInMonth(2017, 9)
        //chart.zoom(5f, 5f, 3f, 5f)
        chart.invalidate()


        generateBarData()
        btnDay.setOnClickListener{
            generateBarData()
        }

    }

    private fun generateBarData() {
        val cal = Calendar.getInstance()

        val leftAxis = chart.axisLeft
        val rightAxis = chart.axisRight
        val xAxis = chart.xAxis

        xAxis.position = XAxisPosition.BOTTOM
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
        val month = cal.get(Calendar.MONTH)+1

        scope.launch {
            Utils.showLoading(true, activity!!)
            withContext(IO) {
                val days = printDatesInMonth(year, month)
                days.forEachIndexed { index, obj ->
                    val sale = productRepository.getNumberOfSalesByDay(obj)
                    Timber.e("Sales $sale  date :$obj")
                    if(sale!=0)
                        entries.add(BarEntry(index.toFloat(), sale.toFloat()))
                }
            }

            Utils.hideLoading()


            val labels = printDatesInMonth(year, month)

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
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
            val set = BarDataSet(entries, "Sales of this month")

            set.valueTextSize = 12f

            chart.data = BarData(set)
//        chart.data.barWidth = 2f
            chart.invalidate()
            /*  chart.data = BarDat
              // make this BarData object grouped
      */
        }

    }

    fun printDatesInMonth(year: Int, month: Int): ArrayList<String> {
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
}
