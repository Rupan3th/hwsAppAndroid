package com.hws.hwsappandroid.util.emoji;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class EditWatcher implements TextWatcher {

    private View view, more;
    private EditText editText;

    public EditWatcher(View view, View more, EditText editText) {
        this.view = view;
        this.editText = editText;
        this.more = more;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count > 0 || s.length() > 0){
            view.setVisibility(View.VISIBLE);
            more.setVisibility(View.GONE);
        }else {
            view.setVisibility(View.GONE);
            more.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
//        int editStart = editText.getSelectionStart();
//        int editEnd = editText.getSelectionEnd();
//        if (editEnd - editStart > 0) {
//            view.setVisibility(View.VISIBLE);
//        } else {
//            view.setVisibility(View.GONE);
//        }
    }
}