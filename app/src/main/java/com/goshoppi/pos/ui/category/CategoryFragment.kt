package com.goshoppi.pos.ui.category


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.model.StoreCategory
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject


class CategoryFragment : BaseFragment() {

    @Inject
    lateinit var localProductRepository: LocalProductRepository
    lateinit var rvCategory: RecyclerView
    override fun layoutRes(): Int {
        return R.layout.fragment_category

    }

    val job: Job = Job()
    val scope = CoroutineScope(Dispatchers.Default + job)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)

    }

    private fun initView(v: View) {
        rvCategory = v.findViewById(R.id.rvCategory)

            val categories = localProductRepository.loadStoreCategory()
         categories.observe(activity!!, Observer {
             if(it!=null)
                 setUpCategoryRecyclerView(it as ArrayList<StoreCategory>)

         })

    }


    private fun setUpCategoryRecyclerView(categories: ArrayList<StoreCategory>) {

        rvCategory.layoutManager = LinearLayoutManager(activity!!)

        rvCategory.adapter =
            RecyclerViewGeneralAdapter(categories, R.layout.single_category_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvCategoryName = mainView.findViewById<TextView>(R.id.tvCategoryName)
                val tvId = mainView.findViewById<TextView>(R.id.tvId)
                val tvCategoryStatus = mainView.findViewById<TextView>(R.id.tvCategoryStatus)
                val tvAction = mainView.findViewById<TextView>(R.id.tvAction)

                tvCategoryName.text = itemData.categoryName
                tvId.text = itemData.categoryId.toString()
                tvCategoryStatus.text = "active"

            }


    }

    companion object {
        fun newInstance() = CategoryFragment().apply {
            arguments = Bundle().apply {
                //putString(distributor_OBJ, param)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        job.cancel()
    }
}
