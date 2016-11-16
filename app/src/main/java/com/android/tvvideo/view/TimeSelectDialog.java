package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tvvideo.R;
import com.android.tvvideo.tools.SystemUtil;

/**
 * Created by yangfengyuan on 16/7/29.
 */
public class TimeSelectDialog extends Dialog implements View.OnClickListener{

    Button yearUp;

    Button yearDown;

    Button monthUp;

    Button monthDown;

    Button dayUp;

    Button dayDown;

    TextView yearEdit;

    TextView monthEdit;

    TextView dayEdit;

    Button confirmBnt;

    Button cancelBnt;

    TimeChangeListener timeChangeListener;

    Context context;

    int year;

    int month;

    int day;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.year_up:
                checkYear(year+1);
                break;
            case R.id.year_down:
                checkYear(year-1);
                break;
            case R.id.month_up:
                checkMonth(month+1);
                break;
            case R.id.month_down:
                checkMonth(month-1);
                break;
            case R.id.day_up:
                checkDay(day+1);
                break;
            case R.id.day_down:
                checkDay(day-1);
                break;
        }
    }

    private void checkDay(int dayNew){

        if(dayNew<1||dayNew>31){
            return;
        }

        if (month == 4 || month == 6 || month == 9 || month == 11) {
            if(dayNew>30){
                dayNew=30;
            }
        } else if (month == 2) {
            if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
                if(dayNew>29){
                    // 是闰年
                    dayNew = 29;
                }
            } else {
                if(dayNew>28){
                    // 不是闰年
                    dayNew = 28;
                }
            }
        }

        day=dayNew;

        yearEdit.setText(String.valueOf(year));

        monthEdit.setText(String.valueOf(month));

        dayEdit.setText(String.valueOf(day));

    }

    private void checkYear(int yearNew){

        if(yearNew<2000||yearNew>2100){
            return;
        }

        year=yearNew;

        if (month == 2) {
            if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
                if(day>29){
                    // 是闰年
                    day = 29;
                }
            } else {
                if(day>28){
                    // 不是闰年
                    day = 28;
                }
            }
        }

        yearEdit.setText(String.valueOf(year));

        monthEdit.setText(String.valueOf(month));

        dayEdit.setText(String.valueOf(day));

    }

    private void checkMonth(int monthNew){

        if(monthNew<1||monthNew>12){
            return;
        }

        month=monthNew;

        if (month == 4 || month == 6 || month == 9 || month == 11) {
            if(day>30){
                day=30;
            }
        } else if (month == 2) {
            if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
                if(day>29){
                    // 是闰年
                    day = 29;
                }
            } else {
                if(day>28){
                    // 不是闰年
                    day = 28;
                }
            }
        }

        yearEdit.setText(String.valueOf(year));

        monthEdit.setText(String.valueOf(month));

        dayEdit.setText(String.valueOf(day));
    }

    public void setNowTime(int year,int month,int day){

        this.year=year;
        this.month=month;
        this.day=day;

        yearEdit.setText(String.valueOf(year));

        monthEdit.setText(String.valueOf(month));

        dayEdit.setText(String.valueOf(day));
    }

    public interface TimeChangeListener{
        void change(String date);
    }

    public void setTimeChangeListener(TimeChangeListener timeChangeListener){
        this.timeChangeListener=timeChangeListener;
    }

    public TimeSelectDialog(Context context) {
        super(context, R.style.Base_Dialog);

        this.context=context;

        initView();
    }

    private void initView() {

        setContentView(R.layout.dialog_time_select);

        yearEdit=(TextView)this.findViewById(R.id.year);

        monthEdit=(TextView)this.findViewById(R.id.month);

        dayEdit=(TextView)this.findViewById(R.id.day);

        yearUp=(Button)this.findViewById(R.id.year_up);

        yearUp.setOnClickListener(this);

        yearDown=(Button)this.findViewById(R.id.year_down);

        yearDown.setOnClickListener(this);

        monthUp=(Button)this.findViewById(R.id.month_up);

        monthUp.setOnClickListener(this);

        monthDown=(Button)this.findViewById(R.id.month_down);

        monthDown.setOnClickListener(this);

        dayUp=(Button)this.findViewById(R.id.day_up);

        dayUp.setOnClickListener(this);

        dayDown=(Button)this.findViewById(R.id.day_down);

        dayDown.setOnClickListener(this);

        confirmBnt=(Button)this.findViewById(R.id.ok);

        cancelBnt=(Button)this.findViewById(R.id.cancel);

        confirmBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date=String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day);

                if(SystemUtil.validateTime(date)){
                    timeChangeListener.change(date);
                    TimeSelectDialog.this.dismiss();
                }else{
                    Toast.makeText(context,"日期错误!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeSelectDialog.this.dismiss();
            }
        });

    }

}

