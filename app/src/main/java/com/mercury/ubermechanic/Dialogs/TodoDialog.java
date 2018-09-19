package com.mercury.ubermechanic.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.mercury.ubermechanic.Custom.CustomDialog;
import com.mercury.ubermechanic.R;


public class TodoDialog extends CustomDialog {
    FrameLayout base;
    FrameLayout rootView;


    public TodoDialog(Context context) {
        super(context);
    }

    public TodoDialog(Context context, int theme) {
        super(context, theme);
    }

    protected TodoDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_todo_layout);
        toolbar.setTitle("Add a Todo");

    }

    @Override
    public View setUpDialog(@LayoutRes int layoutResId,int childLayoutResId) {
        base = (FrameLayout) View.inflate(getContext(), layoutResId, null);
        rootView =  base.findViewById(R.id.parent);

        if (layoutResId != 0) {
            rootView =  (FrameLayout) View.inflate(getContext(), childLayoutResId, rootView);
            setUpViews();
        }

        return base;
    }

    private void setUpViews() {
        Button add = rootView.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
