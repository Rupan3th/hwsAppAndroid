package com.hws.hwsappandroid.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hws.hwsappandroid.R;

public class KindTipsView extends LinearLayout {

    public Context mContext;
    private TextView mTxtTitle;
    private TextView mTxtConfirmMsg;

    private int[] mTitle = {R.string.kind_tips, R.string.cancel_this_order, R.string.are_you_sure_received_goods, R.string.confirm_return_apply, R.string.delete_this_record, R.string.delete_this_order};
    private int[] mMsg = {R.string.want_to_delete_change_address, R.string.cannot_recover_after_cancel, R.string.to_protect_after_sales, R.string.wait_patiently_merchant, R.string.cannot_recover_after_delete, R.string.cannot_recover_after_delete};

    public KindTipsView(Context context, int confirmType) {
        super(context);
        this.mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sheet_dialog_custom, this, true);

        mTxtTitle = (TextView)findViewById(R.id.title);
        mTxtTitle.setText(mTitle[confirmType]);

        mTxtConfirmMsg = (TextView)findViewById(R.id.warning);
        mTxtConfirmMsg.setText(mMsg[confirmType]);

    }

    public KindTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

}
