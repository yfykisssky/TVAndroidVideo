package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.tvvideo.R;
import com.android.tvvideo.constants.SystemContants;

/**
 * Created by yangfengyuan on 16/8/10.
 */
public class SettingPswdDialog extends Dialog {

    EditText pswdEdit;

    Button confirmBnt;

    Button cancelBnt;

    ConfirmOrCancelListener confirmOrCancelListener;

    public interface ConfirmOrCancelListener{
        void confirmOrCancel(boolean b);
    }

    public SettingPswdDialog(Context context) {
        super(context, R.style.Base_Dialog);

        initDialog(context);
    }

    public SettingPswdDialog(Context context, int themeResId) {
        super(context, R.style.Base_Dialog);

        initDialog(context);
    }

    private void initDialog(final Context context){

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.dialog_setting_pswd);

        this.setCancelable(false);

        pswdEdit= (EditText) this.findViewById(R.id.pswd);

        confirmBnt= (Button) this.findViewById(R.id.ok);

        cancelBnt= (Button) this.findViewById(R.id.cancel);

        confirmBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pswd=pswdEdit.getText().toString();

                if(!TextUtils.isEmpty(pswd)&&pswd.equals(SystemContants.SETTINGS_PSWD)){
                    confirmOrCancelListener.confirmOrCancel(true);
                }else{
                    Toast.makeText(context,"密码错误",Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmOrCancelListener.confirmOrCancel(false);
            }
        });

    }

    public void setConfirmOrCancelListener(ConfirmOrCancelListener confirmOrCancelListener){
        this.confirmOrCancelListener=confirmOrCancelListener;
    }


}

