package com.goshoppi.pos.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.goshoppi.pos.R;

public class CommonProgressDialog extends ProgressDialog {

	private TextView tvTitle,tvMessage;
	private Context context;
	public static String DISPLAY_TEXT="";

	public CommonProgressDialog(Context context) {
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
		}catch (Exception ex){}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.commonprogress_dialog_layout);
		ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
		//pb.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.dewa_blue_dark),android.graphics.PorterDuff.Mode.MULTIPLY);
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		if (TextUtils.isEmpty(CommonProgressDialog.DISPLAY_TEXT))
			tvTitle.setText(context.getResources().getString(R.string.load_msg_1));
		else
			tvTitle.setText(CommonProgressDialog.DISPLAY_TEXT);
		tvMessage = (TextView)findViewById(R.id.tvMessage);
		tvMessage.setText(context.getResources().getString(R.string.load_msg_2));
		getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.trans));

	}

}
