package com.okason.diary.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.billing.BillingManager;
import com.okason.diary.billing.BillingProvider;
import com.okason.diary.billing.MainViewController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PremiumUpsellActivity extends AppCompatActivity implements BillingProvider {

    @BindView(R.id.text_view_monthly) TextView monthlyTextView;
    private Activity activity;
    private BillingManager mBillingManager;
    private MainViewController mViewController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_upsell);
        ButterKnife.bind(this);
        activity = this;
        monthlyTextView.setPaintFlags(monthlyTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mViewController = new MainViewController(this);
        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, mViewController.getUpdateListener());

    }

    @Override
    protected void onResume() {
        super.onResume();
        //  checkForDynamicLinkInvite(getIntent());
        if (mBillingManager != null
                && mBillingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK) {
            mBillingManager.queryPurchases();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBillingManager != null) {
            mBillingManager.destroy();
        }
    }

    @OnClick(R.id.btn_upgrade)
    public void onUpgradeButtonClicked(View view){
        mBillingManager.initiatePurchaseFlow(MainViewController.SKU_ID_PREMIUM, BillingClient.SkuType.INAPP);

    }

    @OnClick(R.id.btn_cancel)
    public void onCancelButtonClicked(View view){
        Intent intent = new Intent(activity, NoteListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }




    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    @Override
    public boolean isPremiumPurchased() {
        return mViewController.isPremiumPurchased();
    }

    public void onBillingManagerSetupFinished() {

    }

    /**
     * Remove loading spinner and refresh the UI
     */
    public void showRefreshedUi() {
        Toast.makeText(activity,"Thank you for your purchase", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(activity, NoteListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
