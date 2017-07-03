package com.okason.diary.ui.settings;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.okason.diary.R;
import com.okason.diary.ui.auth.AuthUiActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private View mRootView;
    @BindView(R.id.toolbar_button_login)
    Button loginButton;

    @BindView(R.id.toolbar_button_logout) Button logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, mRootView);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null){
            mFirebaseUser = mAuth.getCurrentUser();
            if (mFirebaseUser != null){
                loginButton.setVisibility(View.VISIBLE);
                logoutButton.setVisibility(View.GONE);
            }else {
                loginButton.setVisibility(View.GONE);
                logoutButton.setVisibility(View.VISIBLE);
            }
        }else {
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
        }

        return mRootView;
    }

    @OnClick(R.id.toolbar_button_login)
    public void onLoginButtonClicked(View view){
        startActivity(new Intent(AuthUiActivity.createIntent(getActivity())));
    }

    @OnClick(R.id.toolbar_button_logout)
    public void onLogoutButtonClicked(View view){
        startActivity(new Intent(AuthUiActivity.createIntent(getActivity())));

        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(AuthUiActivity.createIntent(getActivity()));
                            getActivity().finish();
                        } else {
                            showSnackbar(R.string.sign_out_failed);
                        }
                    }
                });
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG)
                .show();
    }


}
