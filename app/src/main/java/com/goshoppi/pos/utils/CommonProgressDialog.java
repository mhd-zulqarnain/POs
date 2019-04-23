package com.goshoppi.pos.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;
import com.goshoppi.pos.R;

public class CommonProgressDialog extends ProgressDialog {

    private TextView tvTitle;
    private Context context;
    private static String DISPLAY_TEXT = "";

    CommonProgressDialog(Context context) {
        super(context);
        this.context = context;
        setTitle(context.getResources().getString(R.string.load_msg_1));
        setMessage(context.getResources().getString(R.string.load_msg_2));
        setCancelable(false);
        setIndeterminate(true);
        setCanceledOnTouchOutside(true);
        DISPLAY_TEXT = "";
    }

    public CommonProgressDialog(Context context, String title, String message, boolean isCancelable) {
        super(context);
        setTitle(title);
        setMessage(message);
        setCancelable(isCancelable);
        setIndeterminate(true);
        setCanceledOnTouchOutside(true);
        DISPLAY_TEXT = "";
    }

    @Override
    public void setTitle(CharSequence title) {
        try {
            super.setTitle(title);
            tvTitle.setText(title);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.commonprogress_dialog_layout);
        tvTitle = findViewById(R.id.tvTitle);
        if (TextUtils.isEmpty(CommonProgressDialog.DISPLAY_TEXT))
            tvTitle.setText(context.getResources().getString(R.string.load_msg_1));
        else
            tvTitle.setText(CommonProgressDialog.DISPLAY_TEXT);
        TextView tvMessage = findViewById(R.id.tvMessage);
        tvMessage.setText(context.getResources().getString(R.string.load_msg_2));
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.trans));

    }

}
