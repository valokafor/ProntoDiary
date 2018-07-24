package com.okason.diary.ui.auth;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.okason.diary.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PinEntryActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btn_save) Button saveButton;
    @BindView(R.id.btn_save) Button cancelButton;
    @BindView(R.id.input_pin) EditText pinInputEditText;
    @BindView(R.id.input_email) EditText emailInputEditText;
    @BindView(R.id.repeat_email) EditText repeatEmailEditText;
    @BindView(R.id.rootView) View rootView;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_entry);
        ButterKnife.bind(this);
        activity = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @OnClick(R.id.btn_save)
    public void onSaveButtonClicked(View view){
        String pinInput = pinInputEditText.getText().toString().trim();
        if (TextUtils.isEmpty(pinInput)) {
            makeToast(getString(R.string.hint_enter_passcode));
            pinInputEditText.setError(getString(R.string.error_field_required));
            return;
        }
        int pinCode = Integer.parseInt(pinInput);
    }

    private void makeToast(String message) {
        try {
            Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.primary));
            TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
