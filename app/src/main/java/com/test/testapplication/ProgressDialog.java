package com.test.testapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ProgressDialog {
    private AlertDialog dialog;
    private TextView msgTv;
    public ProgressDialog(Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.progress_dialog_layout, null);
        msgTv = view.findViewById(R.id.msgTv);
        alert.setView(view);
        dialog = alert.create();
    }

    public void showProgress(String msg, boolean cancellable){
        msgTv.setText(msg);
        dialog.setCancelable(cancellable);
        dialog.show();
    }

    public void updateProgress(String msg){
        msgTv.setText(msg);
    }

    public void finishProgress(){
        dialog.dismiss();
    }
}
