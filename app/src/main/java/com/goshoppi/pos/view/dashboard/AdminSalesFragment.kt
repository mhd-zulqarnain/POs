package com.goshoppi.pos.view.dashboard


import android.os.Bundle
import android.view.View
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
import javax.inject.Inject
import android.text.method.TextKeyListener.clear
import java.text.SimpleDateFormat
import java.util.*


class AdminSalesFragment : BaseFragment() {

    lateinit var chart: BarChart
    @Inject
    lateinit var productRepository: LocalProductRepository

    override fun layoutRes(): Int {

        return R.layout.fragment_admin_sales
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart = view.findViewById(R.id.chart1)
        //printDatesInMonth(2017, 9);
        chart.zoom(5f,5f,3f,5f)

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

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

        generateBarData()

    }

    private fun generateBarData() {

        val labels = arrayListOf(
            "Ene", "Feb", "Mar",
            "Abr", "May", "Jun",
            "Jul", "Ago", "Set",
            "Oct", "Nov", "Dic",
            "Ene123", "Feb123", "Mar123",
            "Abr123", "May123", "Jun123",
            "Jul123", "Ago123", "Set123",
            "Oct123", "Nov123", "Dic123"
        )

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.setDrawGridBackground(false)
        chart.axisLeft.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        val spaces = 1f
        val entries = arrayListOf(
            BarEntry(0f * spaces, 10f),
            BarEntry(1f * spaces, 20f),
            BarEntry(2f * spaces, 30f),
            BarEntry(3f * spaces, 40f),
            BarEntry(4f * spaces, 50f),
            BarEntry(5f * spaces, 60f),
            BarEntry(6f * spaces, 70f),
            BarEntry(7f * spaces, 60f),
            BarEntry(8f * spaces, 50f),
            BarEntry(9f * spaces, 40f),
            BarEntry(10f * spaces, 30f),
            BarEntry(12f * spaces, 20f),
            BarEntry(13f * spaces, 20f),
            BarEntry(14f * spaces, 203f),
            BarEntry(15f * spaces, 303f),
            BarEntry(16f * spaces, 403f),
            BarEntry(18f * spaces, 503f),
            BarEntry(19f * spaces, 603f),
            BarEntry(20f * spaces, 703f),
            BarEntry(21f * spaces, 603f),
            BarEntry(22f * spaces, 503f),
            BarEntry(23f * spaces, 403f),
            BarEntry(24f * spaces, 330f),
            BarEntry(11f * spaces, 230f)
        )
        val set = BarDataSet(entries, "Sales per of this year")

        set.valueTextSize = 12f

        chart.data = BarData(set)
//        chart.data.barWidth = 2f
        chart.invalidate()
        /*  chart.data = BarDat
          // make this BarData object grouped
  */

    }
    fun printDatesInMonth(year: Int, month: Int) {
        val fmt = SimpleDateFormat("dd/MM/yyyy")
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(year, month - 1, 1)
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 0 until daysInMonth) {
            System.out.println(fmt.format(cal.getTime()))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
}
