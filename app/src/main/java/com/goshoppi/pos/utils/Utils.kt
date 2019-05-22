package com.goshoppi.pos.utils

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.preference.PreferenceManager
import android.speech.SpeechRecognizer
import android.util.Base64
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.goshoppi.pos.R
import com.goshoppi.pos.model.User
import com.goshoppi.pos.view.PosMainActivity
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


object Utils {


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


    fun getProductImage(productId: Int, index: String): File {
        val root = Environment.getExternalStorageDirectory().toString()
        return File("$root//posImages//prd_${productId}//${productId}_${index}.png")

    }

    fun getVaraintImage(productId: Int, varaintId: Int): File {
        val root = Environment.getExternalStorageDirectory().toString()
        return File("$root//posImages//${Constants.PRODUCT_IMAGE_DIR}${productId}//${Constants.VARIANT_IMAGE_DIR}//${varaintId}.png")

    }

    fun saveImage(ImageUrl: String, imageName: String, dirName: String) {

        /*   Picasso.get()
               .load(ImageUrl)
               .into(object : Target {

                   override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                       try {
                           val root = Environment.getExternalStorageDirectory().toString()
                           var myDir = File("$root/posImages/$dirName")

                           if (!myDir.exists()) {
                               myDir.mkdirs()
                           }

                           val name = "$imageName.png"
                           myDir = File(myDir, name)
                           val out = FileOutputStream(myDir)
                           bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                           Timber.e("Inserted  $myDir")

                           out.flush()
                           out.close()
                       } catch (e: Exception) {
                           // some action
                           Timber.e("Image exception $e")
                       }

                   }

                   override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
                       Timber.e("Saving save failed")

                   }

                   override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                       Timber.e("Saving save prepared")

                   }
               }
               )
   */
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

    fun setLoginUser(user: User, ctx: Context) {
        val prefs = SharedPrefs.getInstance()
        prefs!!.setUser(ctx, user)
    }

    fun logout(ctx: Context) {
        val prefs = SharedPrefs.getInstance()
        prefs!!.clearUser(ctx)
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
        cancellable: Boolean?,
        title: String,
        message: String,
        context: Context,
        listener: DialogInterface.OnClickListener
    ) {
        try {
            //new AlertDialog.Builder(context,R.style.DialogStyle).setTitle(title).setMessage(message).setPositiveButton(R.string.alert_dialog_ok, listener).show();
            AlertDialog.Builder(context).setCancelable(cancellable!!).setTitle(title).setMessage(message)
                .setPositiveButton("Ok", listener).show()
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


    fun showMsgShortIntervel(ctx: Activity, msg: String) {
        val toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT)
        toast.show()
        val handler = Handler();
        handler.postDelayed(Runnable() {

            toast.cancel();

        }, 500);
    }
 fun showMsg(ctx: Activity, msg: String) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()

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

    fun getTodaysDate(): String {
        return SimpleDateFormat("MM/dd/yyyy").format(Date(System.currentTimeMillis()))
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

    // type 0 -> Notification Alert
    // type 1 -> SyncWorker
    fun createNotification(aMessage: String, context: Context, type: Int) {
        var notifyManager: NotificationManager? = null
        // ID of notification
        val id = Constants.CHANNEL_ID // default_channel_id
        val title = "Sync Master Database" // Default Channel
        val intent: Intent
        val pendingIntent: PendingIntent
        val builder: NotificationCompat.Builder
        if (notifyManager == null) {
            notifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            var mChannel: NotificationChannel? = notifyManager.getNotificationChannel(id)
            if (mChannel == null) {
                mChannel = NotificationChannel(id, title, importance)
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                notifyManager.createNotificationChannel(mChannel)
            }
            builder = NotificationCompat.Builder(context, id)
            intent = Intent(context, PosMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            builder.setContentTitle(aMessage)                            // required
                .setSmallIcon(if (type == 0) android.R.drawable.ic_popup_reminder else R.drawable.ic_sync)   // required
                .setContentText(context.getString(R.string.app_name)) // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(aMessage)
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        } else {
            builder = NotificationCompat.Builder(context, id)
            intent = Intent(context, PosMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            builder.setContentTitle(aMessage)                            // required
                .setSmallIcon(if (type == 0) android.R.drawable.ic_popup_reminder else R.drawable.ic_sync)   // required
                .setContentText(context.getString(R.string.app_name)) // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(aMessage)
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)).priority =
                Notification.PRIORITY_HIGH
        }
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        val notification = builder.build()
        notifyManager.notify(Constants.NOTIFY_ID, notification)
    }

}
