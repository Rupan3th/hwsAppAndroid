package com.hws.hwsappandroid.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hws.hwsappandroid.R;

public class ConfirmDialogView extends LinearLayout {

    public Context mContext;
    private TextView mTxtTitle;
    private TextView mTxtConfirmMsg;
    private ImageView mImgConfirmIcon;

    private int[] mResIds = {R.mipmap.warning_icon, R.mipmap.good_icon, R.mipmap.result_icon, R.mipmap.warning_icon, R.mipmap.warning_icon, R.mipmap.warning_icon};
    private int[] mTitle = {R.string.cancel_this_order, R.string.are_you_sure_received_goods, R.string.confirm_return_apply, R.string.delete_this_record, R.string.delete_this_order, R.string.clear_local_cache};
    private int[] mMsg = {R.string.cannot_recover_after_cancel, R.string.to_protect_after_sales, R.string.wait_patiently_merchant, R.string.cannot_recover_after_delete, R.string.cannot_recover_after_delete, R.string.want_to_clear};

    public ConfirmDialogView(Context context, int confirmType) {
        super(context);
        this.mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sheet_dialog, this, true);

        mTxtTitle = (TextView)findViewById(R.id.title);
        mTxtTitle.setText(mTitle[confirmType]);

        mTxtConfirmMsg = (TextView)findViewById(R.id.warning);
        mTxtConfirmMsg.setText(mMsg[confirmType]);

        mImgConfirmIcon = (ImageView) findViewById(R.id.mark_image);
        mImgConfirmIcon.setImageResource(mResIds[confirmType]);
    }

    public ConfirmDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }
}