package com.walnutlabs.android;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ProgressHUD extends Dialog {
	public ProgressHUD(Context context) {
		super(context);
	}

	public ProgressHUD(Context context, int theme) {
		super(context, theme);
	}

	public void setMessage(CharSequence message) {
		if(message != null && message.length() > 0) {
			findViewById(R.id.message).setVisibility(View.VISIBLE);			
			TextView txt = (TextView)findViewById(R.id.message);
			txt.setText(message);
			txt.invalidate();
		}
	}

	public void stopSpine() {
		findViewById(R.id.spinnerImageView).setAlpha(0);
		findViewById(R.id.spinnerImageView).setVisibility(View.GONE);
		findViewById(R.id.greenCheck).setVisibility(View.VISIBLE);

	}
	
	public static ProgressHUD show(Context context, CharSequence message, boolean indeterminate, boolean cancelable,
			OnCancelListener cancelListener) {
		ProgressHUD dialog = new ProgressHUD(context,R.style.ProgressHUD);
		dialog.setTitle("");
		dialog.setContentView(R.layout.progress_hud);
		if(message == null || message.length() == 0) {
			dialog.findViewById(R.id.message).setVisibility(View.GONE);			
		} else {
			TextView txt = (TextView)dialog.findViewById(R.id.message);
			txt.setText(message);
		}
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.getWindow().getAttributes().gravity=Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
		lp.dimAmount=0.2f;
		dialog.getWindow().setAttributes(lp); 
		//dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();
		return dialog;
	}

	public static ProgressHUD showMulti(Context context, CharSequence message1, CharSequence message2, boolean indeterminate, boolean cancelable,
								   OnCancelListener cancelListener) {
		ProgressHUD dialog = new ProgressHUD(context,R.style.ProgressHUD);
		dialog.setTitle("");
		dialog.setContentView(R.layout.progress_multy);
		if(message1 == null || message1.length() == 0) {
			dialog.findViewById(R.id.message1).setVisibility(View.GONE);
		} else {
			TextView txt = (TextView)dialog.findViewById(R.id.message1);
			txt.setText(message1);
		}
		if(message2 == null || message2.length() == 0) {
			dialog.findViewById(R.id.message2).setVisibility(View.GONE);
		} else {
			TextView txt = (TextView)dialog.findViewById(R.id.message2);
			txt.setText(message2);
		}
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.getWindow().getAttributes().gravity=Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.dimAmount=0.2f;
		dialog.getWindow().setAttributes(lp);
		//dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();
		return dialog;
	}
}
