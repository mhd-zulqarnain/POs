package com.goshoppi.pos.ui.category


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.model.SubCategory
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SubCategoryFragment : BaseFragment() {

    @Inject
    lateinit var localProductRepository: LocalProductRepository
    lateinit var rvSubCategory: RecyclerView
    
    override fun layoutRes(): Int {
        return R.layout.fragment_sub_category
    }

    val job: Job = Job()
    val scope = CoroutineScope(Dispatchers.Default + job)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)

    }
    companion object {
        fun newInstance() = SubCategoryFragment().apply {
            arguments = Bundle().apply {
                //putString(distributor_OBJ, param)
            }
        }
    }
    private fun initView(v: View) {
        
        rvSubCategory = v.findViewById(R.id.rvSubCategory)

            val subcategories = localProductRepository.loadSubCategory()
            subcategories.observe(activity!!, Observer {
                if(it!=null)
                setUpCategoryRecyclerView(it as ArrayList<SubCategory>)
            })
    }
    private fun setUpCategoryRecyclerView(categories: ArrayList<SubCategory>) {

        rvSubCategory.layoutManager = LinearLayoutManager(activity!!)

        rvSubCategory.adapter =
            RecyclerViewGeneralAdapter(categories, R.layout.single_sub_category_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val tvCategoryName = mainView.findViewById<TextView>(R.id.tvCategoryName)
                val tvSubcategory = mainView.findViewById<TextView>(R.id.tvSubcategory)
                val tvId = mainView.findViewById<TextView>(R.id.tvId)
                val tvCategoryStatus = mainView.findViewById<TextView>(R.id.tvCategoryStatus)

                //  tvCategoryName.text = itemData.categoryId
                tvSubcategory.text = itemData.subcategoryName
                tvId.text = itemData.subcategoryId.toString()
                tvCategoryStatus.text = "active"

                scope.launch {
                    val name = localProductRepository.loadSubCategoryNameByCategoryId(itemData.categoryId!!)
                     tvCategoryName.text = name

                }
            }


    }


    override fun onDetach() {
        super.onDetach()
        job.cancel()
    }


}
