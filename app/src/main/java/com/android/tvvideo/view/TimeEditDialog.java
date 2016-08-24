package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.tvvideo.R;
import com.android.tvvideo.tools.SystemUtil;

/**
 * Created by yangfengyuan on 16/7/29.
 */
public class TimeEditDialog extends Dialog {

    EditText yearEdit;

    EditText monthEdit;

    EditText dayEdit;

    Button confirmBnt;

    Button cancelBnt;

    TimeChangeListener timeChangeListener;

    Context context;

    public interface TimeChangeListener{
        void change(String date);
    }

    public void setTimeChangeListener(TimeChangeListener timeChangeListener){
        this.timeChangeListener=timeChangeListener;
    }

    public TimeEditDialog(Context context) {
        super(context, R.style.Base_Dialog);

        this.context=context;

        initView();
    }

    private void initView() {

        setContentView(R.layout.dialog_time_edit);

        yearEdit=(EditText)this.findViewById(R.id.year);

        monthEdit=(EditText)this.findViewById(R.id.month);

        dayEdit=(EditText)this.findViewById(R.id.day);

        confirmBnt=(Button)this.findViewById(R.id.ok);

        cancelBnt=(Button)this.findViewById(R.id.cancel);

        confirmBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              String date=yearEdit.getText().toString()+"-"+monthEdit.getText().toString()+"-"+dayEdit.getText().toString();

                if(SystemUtil.validateTime(date)){
                    timeChangeListener.change(date);
                    TimeEditDialog.this.dismiss();
                }else{
                    Toast.makeText(context,"日期输入错误!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeEditDialog.this.dismiss();
            }
        });

    }

}

