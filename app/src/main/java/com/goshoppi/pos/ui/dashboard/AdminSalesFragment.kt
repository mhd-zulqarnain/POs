package com.goshoppi.pos.ui.dashboard


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
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

    @Inject
    lateinit var productRepository: LocalProductRepository
    val job: Job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)

    override fun layoutRes(): Int {

        return R.layout.fragment_admin_sales
    }


}
