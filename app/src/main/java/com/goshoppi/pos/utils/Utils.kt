package com.goshoppi.pos.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Environment
import android.preference.PreferenceManager
import android.speech.SpeechRecognizer
import android.support.v4.app.ActivityCompat
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.gson.Gson
import com.goshoppi.pos.R
import com.goshoppi.pos.model.LoginData
import timber.log.Timber

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object Utils {

    var loginData: LoginData? = null
    val REQUEST_CAMERA_PERMISSION = 3
    private var pd: ProgressDialog? = null


    /*     Picasso.get()
                .load(ImageUrl)
                .into(new Target() {

                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                              try {
                                  String root = Environment.getExternalStorageDirectory().toString();
                                  File myDir = new File(root + "/posImages/" + dirName);

                                  if (!myDir.exists()) {
                                      myDir.mkdirs();
                                  }

                                  String name = imageName + ".png";
                                  myDir = new File(myDir, name);
                                  FileOutputStream out = new FileOutputStream(myDir);
                                  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                  Timber.e("Inserted  " + myDir);

                                  out.flush();
                                  out.close();
                              } catch (Exception e) {
                                  // some action
                                  Timber.e("Image exception " + e);
                              }
                          }

                          @Override
                          public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                              Timber.e("Saving save failed");

                          }

                          @Override
                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                              Timber.e("Saving save prepared");

                          }
                      }
                );*/

    /* Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable(){
                @Override
                public void run() {


                }
            });*/


    val dateTime: String
        get() {
            val dateFormat = SimpleDateFormat(
                "dd-MM-yyyy", Locale.getDefault()
            )
            val date = Date()
            return dateFormat.format(date)
        }

    fun getErrorText(errorCode: Int): String {
        val message: String
        when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> message = "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> message = "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> message = "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> message = "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> message = "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> message = "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> message = "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message = "No speech input"
            else -> message = "Didn't understand, please try again."
        }
        return message
    }


    fun getProductImage( productId:String,index:String): File {
        val root = Environment.getExternalStorageDirectory().toString()
        return  File("$root//posImages//prd_${productId}//${productId}_${index}.png")

    }
    fun getVaraintImage( productId:String,varaintId:String ): File {
        val root = Environment.getExternalStorageDirectory().toString()
        return  File("$root//posImages//${Constants.PRODUCT_IMAGE_DIR}${productId}//${Constants.VARAINT_IMAGE_DIR}//${varaintId}.png")

    }
    fun saveImage(ImageUrl: String, imageName: String, dirName: String) {
        try {
            val url = URL(ImageUrl)
            val bm = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            val root = Environment.getExternalStorageDirectory().toString()
            val myDir = File("$root/posImages/$dirName")

            if (!myDir.exists()) {
                myDir.mkdirs()
            }

            val name = "$imageName.png"
            val imageFile = File(myDir, name)
            val out = FileOutputStream(imageFile)

            bm.compress(Bitmap.CompressFormat.PNG, 20, out) // Compress Image
            out.flush()
            out.close()

        } catch (e: IOException) {
            println(e)
            Timber.e("Exception  $e")

        }

    }


    fun showAlert(title: String, message: String, context: Context) {
        AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("Ok", null).show()
    }

    fun getDouble(strValue: String): Double {
        var number = 0.0
        try {
            number = java.lang.Double.parseDouble(strValue)
        } catch (e: Exception) {
            number = 0.0
        }

        return number
    }

    fun showAlert(title: String, message: String, context: Context, listener: DialogInterface.OnClickListener) {
        try {
            //new AlertDialog.Builder(context,R.style.DialogStyle).setTitle(title).setMessage(message).setPositiveButton(R.string.alert_dialog_ok, listener).show();
            AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("Ok", listener).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getScreenWidth(_context: Context): Int {
        val columnWidth: Int
        val wm = _context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay

        val point = Point()
        try {
            display.getSize(point)
        } catch (ignore: java.lang.NoSuchMethodError) { // Older device
            point.x = display.width
            point.y = display.height
        }

        columnWidth = point.x
        return columnWidth
    }

    fun getScreenHeight(_context: Context): Int {
        val columnWidth: Int
        val wm = _context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay

        val point = Point()
        try {
            display.getSize(point)
        } catch (ignore: java.lang.NoSuchMethodError) { // Older device
            point.x = display.width
            point.y = display.height
        }

        columnWidth = point.y
        return columnWidth
    }

    fun showAlert(
        context: Context,
        titleId: Int,
        messageId: Int,
        positiveTextId: Int,
        negativeTextId: Int,
        positiveListener: DialogInterface.OnClickListener,
        negativeListener: DialogInterface.OnClickListener
    ) {
        try {
            val builder = AlertDialog.Builder(context, R.style.DialogStyle)
            builder.setTitle(titleId)
            builder.setMessage(messageId)
            if (positiveTextId != 0)
                builder.setPositiveButton(positiveTextId, positiveListener)
            if (negativeTextId != 0)
                builder.setNegativeButton(negativeTextId, negativeListener)
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun showAlert(
        context: Context,
        title: String,
        message: String,
        positiveText: String,
        negativeText: String,
        positiveListener: DialogInterface.OnClickListener,
        negativeListener: DialogInterface.OnClickListener
    ) {
        try {
            val builder = AlertDialog.Builder(context, R.style.DialogStyle)
            builder.setTitle(title)
            builder.setMessage(message)
            if (!positiveText.isEmpty())
                builder.setPositiveButton(positiveText, positiveListener)
            if (!negativeText.isEmpty())
                builder.setNegativeButton(negativeText, negativeListener)
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun showMsg(ctx: Activity, msg: String) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
    }


    fun getDateTime(format: String, locale: Locale): String {
        val dateFormat = SimpleDateFormat(
            format, locale
        )
        val date = Date()
        return dateFormat.format(date)
    }



    fun hideSoftKeyboard(activity: Activity?) {
        try {

            if (activity == null) return

            val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun savePref(context: Context, key: String, value: String) {
        val appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val prefsEditor = appSharedPrefs.edit()
        prefsEditor.putString(key, value)
        prefsEditor.commit()
    }

    fun saveLoginObject(context: Context, key: String, LoginData: LoginData) {
        val appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val prefsEditor = appSharedPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(LoginData)
        prefsEditor.putString(key, json)
        prefsEditor.commit()
    }

    fun getLoginObject(context: Context, key: String): LoginData {
        val appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = appSharedPrefs.getString(key, "")
        return gson.fromJson(json, LoginData::class.java)
    }

    fun encodeToBase64String(baos: ByteArrayOutputStream): String {

        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

    }

    /*network check*/
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var netInfo: NetworkInfo? = null
        if (cm != null) {
            netInfo = cm.activeNetworkInfo
        }
        return netInfo != null && netInfo.isConnectedOrConnecting
    }


    fun hideLoading() {

        if (pd != null)
            pd!!.dismiss()

    }

    fun showLoading(isCancellable: Boolean, context: Context?) {

        if (context == null)
            return

        //if(pd == null)
        pd = CommonProgressDialog(context)// ProgressDialog(context); //new ProgressDialog(context,R.style.DialogStyle);
        pd!!.setCancelable(isCancellable)
        pd!!.isIndeterminate = true
        pd!!.show()
        //pd.setContentView(R.layout.commonprogress_dialog_layout);

    }

}
