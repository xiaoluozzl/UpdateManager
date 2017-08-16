package com.xiaoluo.updatelib;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;


/**
 * 确认对话框
 *
 * @author: xiaoluo
 * @date: 2016-12-21 18:39
 */
public class ConfirmDialog extends Dialog {

    private TextView leftTv;
    private TextView rightTv;
    private TextView messageTv;

    private OnSelectListener mListener;

    public ConfirmDialog(Context context) {
        super(context, R.style.ConfirmDialog);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_confirm);
        leftTv = (TextView)findViewById(R.id.dialog_custom_left_btn);
        rightTv = (TextView)findViewById(R.id.dialog_custom_right_btn);
        messageTv = (TextView) findViewById(R.id.dialog_custom_content_tv);

        leftTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onLeftSelect();
                    dismiss();
                }
            }
        });

        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRightSelect();
                    dismiss();
                }
            }
        });
    }



    public ConfirmDialog setLeftText(String text) {
        leftTv.setText(text);
        return this;
    }

    public ConfirmDialog setRightText(String text) {
        rightTv.setText(text);
        return this;
    }

    public ConfirmDialog setMessage(String text) {
        messageTv.setText(text);
        return this;
    }

    public ConfirmDialog setOnSelectListener(OnSelectListener listener) {
        this.mListener = listener;
        return this;
    }

    public interface OnSelectListener {
        void onLeftSelect();
        void onRightSelect();
    }
}
