package com.codeskraps.sbrowser.services;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.codeskraps.sbrowser.bill_util.IabHelper;
import com.codeskraps.sbrowser.bill_util.IabHelper.QueryInventoryFinishedListener;
import com.codeskraps.sbrowser.bill_util.IabResult;
import com.codeskraps.sbrowser.bill_util.Inventory;
import com.codeskraps.sbrowser.bill_util.Purchase;
import com.codeskraps.sbrowser.misc.Cons;

public class ProAccoundVerificationService extends Service implements
		QueryInventoryFinishedListener {
	private static final String TAG = ProAccoundVerificationService.class.getSimpleName();

	private IabHelper mHelper = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mHelper = new IabHelper(this, Cons.BASE64ENCODEDPUBLICKEY);
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (mHelper == null) return;
				if (result.isFailure()) {
					Log.d(TAG, "Problem setting up In-app Billing: " + result);
					return;
				}

				Log.d(TAG, "Hooray, IAB is fully set up!");
				try {
					ArrayList<String> additionalSkuList = new ArrayList<String>();
					additionalSkuList.add(Cons.PRO_USER);
					mHelper.queryInventoryAsync(true, additionalSkuList,
							ProAccoundVerificationService.this);

				} catch (IllegalStateException e) {
					Log.i(TAG, "Handled: queryInventoryAsync", e);
				}

			}
		});
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
		if (mHelper == null) return;
		if (result.isFailure()) {
			Log.i(TAG, "Error getting inventory:" + result.getMessage());
			return;
		}

		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
				.edit();
		Purchase premiumPurchase = inventory.getPurchase(Cons.PRO_USER);
		if (premiumPurchase != null) {
			Log.v(TAG, "User already has premiumPurchase");
			editor.putBoolean(Cons.hasPro, true);

		} else {
			Log.v(TAG, "User does not have premiumPurchase");
			editor.putBoolean(Cons.hasPro, false);
		}
		editor.apply();

		stopSelf();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null) mHelper.dispose();
		mHelper = null;
	}
}
