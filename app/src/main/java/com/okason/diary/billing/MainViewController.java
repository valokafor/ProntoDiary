/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.okason.diary.billing;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.Purchase;
import com.okason.diary.ui.auth.PremiumUpsellActivity;
import com.okason.diary.utils.SettingsHelper;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Handles control logic of the BaseGamePlayActivity
 */
public class MainViewController {
    private static final String TAG = "MainViewController";
    public static final String SKU_ID_PREMIUM = "remove_ad";
    //public static final String SKU_ID_PREMIUM = "android.test.purchased";


    private final UpdateListener mUpdateListener;
    private PremiumUpsellActivity mActivity;



    public MainViewController(PremiumUpsellActivity activity) {
        mUpdateListener = new UpdateListener();
        mActivity = activity;
        loadData();
    }


    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public boolean isPremiumPurchased() {
        return SettingsHelper.getHelper(mActivity).isPremiumUser();
    }





    /**
     * Handler to billing updates
     */
    private class UpdateListener implements BillingManager.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
            mActivity.onBillingManagerSetupFinished();
        }

        @Override
        public void onConsumeFinished(String token, @BillingResponse int result) {
            Log.d(TAG, "Consumption finished. Purchase token: " + token + ", result: " + result);

            if (result == BillingResponse.OK) {
                Log.d(TAG, "Consumption successful. Provisioning.");
                saveData();
            }

            mActivity.showRefreshedUi();
            Log.d(TAG, "End consumption flow.");
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {
            for (Purchase purchase : purchaseList) {
                switch (purchase.getSku()) {
                    case SKU_ID_PREMIUM:
                        Log.d(TAG, "You are Premium! Congratulations!!!");
                        SettingsHelper.getHelper(mActivity).setPremiumUser(true);
                        mActivity.showRefreshedUi();
                        break;

                }
            }

           // mActivity.showRefreshedUi();
        }
    }


    private void saveData() {
        SharedPreferences.Editor spe = mActivity.getPreferences(MODE_PRIVATE).edit();


    }

    private void loadData() {
        SharedPreferences sp = mActivity.getPreferences(MODE_PRIVATE);

    }
}