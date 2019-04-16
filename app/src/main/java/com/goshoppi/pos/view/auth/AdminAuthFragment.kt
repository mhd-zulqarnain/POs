@file:Suppress("DEPRECATION")

package com.goshoppi.pos.view.auth

import android.app.Activity
import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.facebook.stetho.common.Util
import com.goshoppi.pos.R
import com.goshoppi.pos.model.LoginResponse
import com.goshoppi.pos.model.User
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.view.PosMainActivity
import com.goshoppi.pos.webservice.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import javax.inject.Inject

@Suppress("DEPRECATION")
class AdminAuthFragment : Fragment() {
    private lateinit var pd: ProgressDialog
    private var mEmailView: EditText? = null
    private var mPasswordView: EditText? = null
    private lateinit var strLocationValues: Array<String>

    private lateinit var btn_login_sign_in: Button
    lateinit var spnLocation: Spinner
    private lateinit var strCountries: Array<String>
    var strLocationValue = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_auth, container, false)

        initView(view)
        return view

    }

    private fun initView(view: View) {
        pd = ProgressDialog(activity!!)
        pd.setTitle(getResources().getString(R.string.progress_dialog_title))
        pd.setMessage(getResources().getString(R.string.progress_dialog_desc))
        pd.setCancelable(false)
        pd.setIndeterminate(true)
        strLocationValues = resources.getStringArray(R.array.locations_array_values)
        btn_login_sign_in = view.findViewById(R.id.btn_login_sign_in)
        mPasswordView = view.findViewById(R.id.mPasswordView)
        mEmailView = view.findViewById(R.id.mEmailView)

        spnLocation = view.findViewById(R.id.spnLocation)

        val allCountries = ArrayList<ListItem>()
        strCountries = resources.getStringArray(R.array.locations_array)
        val resourceIds = intArrayOf(
            R.drawable.flag_uae,
            R.drawable.flag_australia,
            R.drawable.flag_india,
            R.drawable.flag_usa,
            R.drawable.flag_netherland,
            R.drawable.flag_newzealand,
            R.drawable.flag_uk,
            R.drawable.flag_bahrain
        )
        for (i in strCountries.indices) {
            allCountries.add(ListItem(strCountries[i], resourceIds[i]))
        }
        spnLocation.setAdapter(MyAdapter(activity!!, R.layout.spinner_row_item, allCountries))

        btn_login_sign_in.setOnClickListener {
            getUser()

        }
        if (Constants.isDebug) {
            //mEmailView.setText("admin-mankool@newstore.com");
            mEmailView!!.setText("admin@sb.com")
            mPasswordView!!.setText("welcome")
        }
    }

    fun adduser(){
        val user =User()
        user.isAdmin =true
        user.isProcurement =true
        user.isSales =true
        user.userCode =mEmailView!!.text.toString()
        user.storeCode =mEmailView!!.text.toString()
        user.password =mPasswordView!!.text.toString()
        user.updatedAt = System.currentTimeMillis().toString()
        Utils.setLoginUser(user,activity!!)
        (activity!! as LoginActivity).userRepository.insertUser(user)

    }

    fun getUser() {
        mEmailView!!.setError(null)
        mPasswordView!!.setError(null)

        val email = mEmailView!!.getText().toString()
        val password = mPasswordView!!.getText().toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(password)) {
            mPasswordView!!.setError(activity!!.getString(R.string.err_not_empty))
            focusView = mPasswordView
            cancel = true
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView!!.setError(getString(R.string.err_invalid_entry))
            focusView = mEmailView
            cancel = true
        }
        if (cancel) run {
            focusView!!.requestFocus()
            return
        }
        pd.show()

        if (!Utils.isNetworkAvailable(activity!!)) {
            (activity!! as LoginActivity).userRepository.getAuthResult(mEmailView!!.text.toString(),
                mPasswordView!!.text.toString()).observe(activity!!,
                Observer<List<User>> {
                    var user:User

                    if(it!==null && it.size!=0){
                        val i = Intent(activity!!, PosMainActivity::class.java)
                        pd.dismiss()
                        Utils.setLoginUser(it[0],activity!!)
                        startActivity(i)
                        activity!!.finish()
                    }else{
                        Utils.showMsg(activity!!, "Authentication failed")

                    }
                    pd.dismiss()

                }


            )
        } else {


            strLocationValue = strLocationValues[spnLocation.selectedItemPosition]

            val pass = mPasswordView!!.text.toString()
            val memail = mEmailView!!.text.toString()
            RetrofitClient.getInstance()?.getService()
                ?.getvalidateStore(strLocationValue, memail, pass)
                ?.enqueue(object : Callback<LoginResponse> {
                    override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                        println("error")
                        pd.dismiss()
                    }

                    override fun onResponse(call: Call<LoginResponse>?, response: retrofit2.Response<LoginResponse>?) {


                        if (response!!.body() != null) {
                            val obj: LoginResponse = response.body()!!
                            if (obj.code == 200) {
//                            SharedPrefs.getInstance()!!.setUser(activity!!, obj.adminData!!)
                                obj.adminData!!.clientKey = strLocationValue
                                Constants.DEVELOPER_KEY = strLocationValue
//                            SharedPrefs.getInstance()!!.savePref(activity!!,Constants.GET_DEVELOPER_KEY,strLocationValue);
                                adduser()
                                val i = Intent(activity!!, PosMainActivity::class.java)
                                startActivity(i)
                                activity!!.finish()
                            } else {
                                Utils.showMsg(activity!!, obj.error!!)
                            }

                        }

                        pd.dismiss()

                    }
                })
        }
    }
    class ListItem(internal var title: String, internal var imageResourceId: Int)
    inner class MyAdapter(context: Context, textViewResourceId: Int, internal var objects: ArrayList<ListItem>) :
        ArrayAdapter<ListItem>(context, textViewResourceId, objects) {

        internal var inflater: LayoutInflater
        internal var holder: ViewHolder? = null

        init {
            inflater = (context as Activity).layoutInflater
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getCustomView(position, convertView, parent)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getCustomView(position, convertView, parent)
        }

        fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {

            val listItem = objects[position]
            var row = convertView

            if (null == row) {
                holder = ViewHolder()
                row = inflater.inflate(R.layout.spinner_row_item, parent, false)
                holder!!.title = row!!.findViewById(R.id.title) as TextView
                holder!!.imgThumb = row.findViewById(R.id.imgThumb) as ImageView
                row.tag = holder
            } else {
                holder = row.tag as ViewHolder
            }

            holder!!.title!!.text = listItem.title
            holder!!.imgThumb!!.setBackgroundResource(listItem.imageResourceId)
//            holder!!.title!!.setTextColor(resources.getColor(R.color.orange))
            return row
        }

        inner class ViewHolder {
            internal var title: TextView? = null
            internal var imgThumb: ImageView? = null
        }
    }

}