package com.goshoppi.pos.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.google.gson.Gson;
import com.goshoppi.pos.R;
import com.goshoppi.pos.model.LoginData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Utils {

    public static LoginData loginData = null;
    public static final int REQUEST_CAMERA_PERMISSION = 3;
    private static ProgressDialog pd;

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    public static void WriteLogs(String errMsg){
        if(Constants.isDebug)
            Log.d(Constants.LOG_TAG,errMsg);
    }

    static public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static void showAlert(String title, String message, Context context){
       new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("Ok", null).show();
    }
    public static double getDouble(String strValue)
    {
        double number = 0;
        try
        {
            number = Double.parseDouble(strValue);
        }
        catch ( Exception e )
        {
            number = 0;
        }
        return number;
    }
    public static void showAlert(String title, String message, Context context, DialogInterface.OnClickListener listener){
        try {
            //new AlertDialog.Builder(context,R.style.DialogStyle).setTitle(title).setMessage(message).setPositiveButton(R.string.alert_dialog_ok, listener).show();
            new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("Ok", listener).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static public int getScreenWidth(Context _context)
    {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }
    static public int getScreenHeight(Context _context)
    {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.y;
        return columnWidth;
    }

    public static void showAlert(Context context, int titleId, int messageId, int positiveTextId, int negativeTextId, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            builder.setTitle(titleId);
            builder.setMessage(messageId);
            if(positiveTextId!=0)
                builder.setPositiveButton(positiveTextId,positiveListener);
            if(negativeTextId!=0)
                builder.setNegativeButton(negativeTextId, negativeListener);
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(Context context, String title, String message, String positiveText, String negativeText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogStyle);
            builder.setTitle(title);
            builder.setMessage(message);
            if(!positiveText.isEmpty())
                builder.setPositiveButton(positiveText,positiveListener);
            if(!negativeText.isEmpty())
                builder.setNegativeButton(negativeText, negativeListener);
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px
                / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp
                * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static void showMsg(Activity ctx, String msg){
        Toast.makeText(ctx,msg, Toast.LENGTH_LONG).show();
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    public static boolean dateBetween(String strStartDate, String strEndDate, String strQuestionDate){
        Date min =null, max=null;   // assume these are set to something
        Date questionDate=null;          // the date in question
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try{
            min = sdf.parse(strStartDate);
            max = sdf.parse(strEndDate);
            questionDate = sdf.parse(strQuestionDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(min);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            min = calendar.getTime();
            calendar.setTime(max);
            calendar.add(Calendar.DAY_OF_YEAR,1);
            max = calendar.getTime();
        }catch (Exception ex){
            Utils.WriteLogs(ex.getMessage());
        }

        return questionDate.after(min) && questionDate.before(max);
    }
    public static long getMiliSeconds(String strFirstDate, String strSecondDate){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
            Date firstDate = sdf.parse(strFirstDate);
            Date secondDate = sdf.parse(strSecondDate);
            /*//the value 0 if the argument Date is equal to this Date;
            // a value less than 0 if this Date is before the Date argument;
            // and a value greater than 0 if this Date is after the Date argument.
            int i = firstDate.compareTo(secondDate);*/
            long diff = firstDate.getTime() - secondDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            return diff;
        }catch (Exception ex){
            Utils.WriteLogs(ex.getMessage());
            return 0;
        }
    }
    static public String getDateTime(String format, Locale locale) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                format, locale);
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static double round(double value, int places)
    {
        try {
            long factor = (long) Math.pow(10, places);
            value = value * factor;
            long tmp = Math.round(value);
            return (double) tmp / factor;
        }catch(Exception e){
            return 0;
        }
    }
    public static String roundDouble(double number){
        return String.format("%.2f", number);
    }
    public static String roundDouble(String doubleValue){
        try{
            return String.format("%.2f", Double.parseDouble(doubleValue));
        }catch (Exception ex){
            return doubleValue;
        }
    }


    public static void hideSoftKeyboard(Activity activity) {
        try {

            if(activity == null)return;

            InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean checkCameraPermission(Context context){


        if(android.os.Build.VERSION.SDK_INT>android.os.Build.VERSION_CODES.HONEYCOMB_MR1){
            int result = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            if (result == PackageManager.PERMISSION_GRANTED){

                return true;

            } else {

                return false;

            }
        }
        else{
            return true;
        }
    }
    public static boolean requestCameraPermission(Activity activity){

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)){

            //Toast.makeText(activity,activity.getString(R.string.camera_settings_message),Toast.LENGTH_LONG).show();
            Utils.showAlert(activity.getString(R.string.security_permission_title),activity.getString(R.string.camera_settings_message),activity);
            return false;

        } else {

            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION);
            return  true;
        }
    }
    public static void savePref(Context context, String key, String value)
    {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static void saveLoginObject(Context context, String key, LoginData LoginData){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(LoginData);
        prefsEditor.putString(key, json);
        prefsEditor.commit();
    }
    public static LoginData getLoginObject(Context context, String key){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(key, "");
        LoginData obj = gson.fromJson(json, LoginData.class);
        return obj;
    }
    public static String getPref(Context context, String key)
    {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String prefValue = appSharedPrefs.getString(key, "");//"" is default value
        return prefValue;
    }
    public static ByteArrayOutputStream getCompressedFile(String filePath){
        Bitmap bMap=null;
        byte[] bMapArray = null;
        ByteArrayOutputStream boasReturn = null;
        try {

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPurgeable = true;
            FileInputStream in = new FileInputStream(filePath);
            BufferedInputStream buf = new BufferedInputStream(in);
            bMapArray= new byte[buf.available()];
            buf.read(bMapArray);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bMap = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length, opt);
            bMap.compress(Bitmap.CompressFormat.JPEG, 95, baos);
            boasReturn = baos;
            //bMapArray = baos.toByteArray();
        }catch (Exception ex){
            Utils.WriteLogs(ex.getMessage());
        }
        return boasReturn;
    }
    public static Bitmap getBitmap(byte[] bImage){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPurgeable = true;
        return BitmapFactory.decodeByteArray(bImage, 0, bImage.length, opt);
    }
    public static String encodeToBase64String(ByteArrayOutputStream baos)
    {

        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

    }

    /**
     *
     * @param mContext
     * @return
     */
    public static int screenWidth(Activity mContext){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int iWidth = displaymetrics.widthPixels;
        //int iHeight = displaymetrics.heightPixels;
        return iWidth;
    }

    /*network check*/
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo=null;
        if(cm!=null){
            netInfo  =cm.getActiveNetworkInfo();
        }
        return netInfo!=null && netInfo.isConnectedOrConnecting();
    }


    public static void hideLoading(){

        if(pd != null)
            pd.dismiss();

    }
    public static void showLoading(boolean isCancellable, Context context){

        if(context == null)
            return;

        //if(pd == null)
        pd = new CommonProgressDialog(context);// ProgressDialog(context); //new ProgressDialog(context,R.style.DialogStyle);
        pd.setCancelable(isCancellable);
        pd.setIndeterminate(true);
        pd.show();
        //pd.setContentView(R.layout.commonprogress_dialog_layout);

    }

}
