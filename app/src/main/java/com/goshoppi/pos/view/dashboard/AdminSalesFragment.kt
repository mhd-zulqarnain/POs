package com.goshoppi.pos.view.dashboard


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

import com.goshoppi.pos.R
import com.goshoppi.pos.di2.base.BaseFragment
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class   AdminSalesFragment :  BaseFragment() {

    private val count = 12

    override fun layoutRes(): Int {

        return R.layout.fragment_admin_sales
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      val   chart: CombinedChart = view.findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        chart.drawOrder = arrayOf(
            CombinedChart.DrawOrder.BAR,
            CombinedChart.DrawOrder.BUBBLE,
            CombinedChart.DrawOrder.CANDLE,
            CombinedChart.DrawOrder.LINE,
            CombinedChart.DrawOrder.SCATTER
        )

        val  l: Legend = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        val rightAxis: YAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        val leftAxis:YAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        val  xAxis: XAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
//        xAxis.setValueFormatter(object: ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return months[(int) value % months.length];
//
//            }
//
//        });

        val data =  CombinedData()

        data.setData(generateLineData());
        data.setData(generateBarData());
        data.setData(generateBubbleData());
        data.setData(generateScatterData());
        data.setData(generateCandleData());

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        chart.setData(data);
        chart.invalidate();
    }
    private fun generateLineData(): LineData {

        val d = LineData()

        val entries = ArrayList<Entry>()

        for (index in 0 until count)
            entries.add(Entry(index + 0.5f, getRandom(15, 5)))

        val set = LineDataSet(entries, "Line DataSet")
        set.color = Color.rgb(240, 238, 70)
        set.lineWidth = 2.5f
        set.setCircleColor(Color.rgb(240, 238, 70))
        set.circleRadius = 5f
        set.fillColor = Color.rgb(240, 238, 70)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawValues(true)
        set.valueTextSize = 10f
        set.valueTextColor = Color.rgb(240, 238, 70)

        set.axisDependency = YAxis.AxisDependency.LEFT
        d.addDataSet(set)

        return d
    }

    private fun generateBarData(): BarData {

        val entries1 = ArrayList<BarEntry>()
        val entries2 = ArrayList<BarEntry>()

        for (index in 0 until count) {
//            entries1.add(BarEntry(0, getRandom(25, 25)))
            entries1.add(BarEntry(0f, floatArrayOf(getRandom(11, 12), getRandom(13, 12))))

            // stacked
            entries2.add(BarEntry(0f, floatArrayOf(getRandom(13, 12), getRandom(13, 12))))
        }

        val set1 = BarDataSet(entries1, "Bar 1")
        set1.color = Color.rgb(60, 220, 78)
        set1.valueTextColor = Color.rgb(60, 220, 78)
        set1.valueTextSize = 10f
        set1.axisDependency = YAxis.AxisDependency.LEFT

        val set2 = BarDataSet(entries2, "")
        set2.stackLabels = arrayOf("Stack 1", "Stack 2")
        set2.setColors(Color.rgb(61, 165, 255), Color.rgb(23, 197, 255))
        set2.valueTextColor = Color.rgb(61, 165, 255)
        set2.valueTextSize = 10f
        set2.axisDependency = YAxis.AxisDependency.LEFT

        val groupSpace = 0.06f
        val barSpace = 0.02f // x2 dataset
        val barWidth = 0.45f // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        val d = BarData(set1, set2)
        d.barWidth = barWidth

        // make this BarData object grouped
        d.groupBars(0f, groupSpace, barSpace) // start at x = 0

        return d
    }

    private fun generateScatterData(): ScatterData {

        val d = ScatterData()

        val entries = ArrayList<Entry>()

        var index = 0f
        while (index < count) {
            entries.add(Entry(index + 0.25f, getRandom(10, 55)))
            index += 0.5f
        }

        val set = ScatterDataSet(entries, "Scatter DataSet")
        set.setColors(*ColorTemplate.MATERIAL_COLORS)
        set.scatterShapeSize = 7.5f
        set.setDrawValues(false)
        set.valueTextSize = 10f
        d.addDataSet(set)

        return d
    }

    private fun generateCandleData(): CandleData {

        val d = CandleData()

        val entries = ArrayList<CandleEntry>()

        var index = 0
        while (index < count) {
            entries.add(CandleEntry(index + 1f, 90f, 70f, 85f, 75f))
            index += 2
        }

        val set = CandleDataSet(entries, "Candle DataSet")
        set.decreasingColor = Color.rgb(142, 150, 175)
        set.shadowColor = Color.DKGRAY
        set.barSpace = 0.3f
        set.valueTextSize = 10f
        set.setDrawValues(false)
        d.addDataSet(set)

        return d
    }

    private fun generateBubbleData(): BubbleData {

        val bd = BubbleData()

        val entries = ArrayList<BubbleEntry>()

        for (index in 0 until count) {
            val y = getRandom(10, 105)
            val size = getRandom(100, 105)
            entries.add(BubbleEntry(index + 0.5f, y, size))
        }

        val set = BubbleDataSet(entries, "Bubble DataSet")
        set.setColors(*ColorTemplate.VORDIPLOM_COLORS)
        set.valueTextSize = 10f
        set.valueTextColor = Color.WHITE
        set.highlightCircleWidth = 1.5f
        set.setDrawValues(true)
        bd.addDataSet(set)

        return bd
    }
     fun getRandom(range: Int, start: Int): Float {
        return (Math.random() * range).toFloat() + start
    }

}
