package com.goshoppi.pos.ui.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
    val job: Job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)
    lateinit var btnByMonth: Button
    lateinit var btnByWeek: Button
    lateinit var txtReportAbout: TextView
    lateinit var rvSales: RecyclerView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart = view.findViewById(R.id.chart1)
        btnByMonth = view.findViewById(R.id.btnByMonth)
        btnByWeek = view.findViewById(R.id.btnByWeek)
        txtReportAbout = view.findViewById(R.id.txtReportAbout)
        rvSales = view.findViewById(R.id.rvSales)

        //printDatesInMonth(2017, 9)
//        chart.zoom(2f, 2f, 2f, 5f)

        generateBarData(Filter.MONTH)

        btnByMonth.setOnClickListener {
            chart.invalidate()
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
            chart.invalidate()
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

    private fun setSalesRecycler(list: ArrayList<String>,datalist: ArrayList<OrderItem>  ) {

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
            var sales= 0.00
            datalist.forEach{
                if(it.addedDate.equals(itemData)){
                    sales+=it.totalPrice!!
                }
            }
            tvDate.text = itemData
            tvPhone.text = "0 AED"
            tvDueDate.text = "0 AED"
            tvOutStanding.text = "$sales AED"


        }
    }

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
            val listData = arrayListOf<OrderItem>()
            withContext(Dispatchers.IO) {
                if (type == Filter.MONTH) {

                    val days = printDatesInMonth(year, month)
                    days.forEachIndexed { index, obj ->
                        val sale = productRepository.getNumberOfSalesByDay(obj)
                        val saleList = productRepository.getSalesByDay(obj)
                        listData.addAll(saleList)
                        Timber.e("Sales $sale  date :$obj")
                        if (sale != 0.00)
                            entries.add(BarEntry(index.toFloat(), sale.toFloat()))

                    }
                    withContext(Dispatchers.Main) {
                        setSalesRecycler(days,listData)

                    }

                } else {
                    val days = getDateofthisWeek(year)
                    days.forEachIndexed { index, obj ->
                        val sale = productRepository.getNumberOfSalesByDay(obj)
                        val saleList = productRepository.getSalesByDay(obj)

                        Timber.e("Sales $sale  date :$obj")
                        if (sale != 0.00)
                            entries.add(BarEntry(index.toFloat(), sale.toFloat()))

                    }
                    withContext(Dispatchers.Main) {
                        setSalesRecycler(days,listData)

                    }

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

    fun getDateofthisWeek(year: Int): ArrayList<String> {
        val cal = Calendar.getInstance()
        val today = SimpleDateFormat("dd", Locale.getDefault()).format(Date()).toInt()
        val month = cal.get(Calendar.MONTH) + 1

        val fmt = SimpleDateFormat(
            activity!!.resources.getString(R.string.pos_date_format),
            Locale.getDefault()
        )
        cal.clear()
        cal.set(year, month - 1, today)
        val arr = arrayListOf<String>()
        for (i in today until today + 7) {
            System.out.println(fmt.format(cal.getTime()))
            cal.add(Calendar.DAY_OF_MONTH, 1)
            arr.add(fmt.format(cal.getTime()))
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
