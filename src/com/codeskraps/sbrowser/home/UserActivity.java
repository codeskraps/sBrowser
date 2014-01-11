package com.codeskraps.sbrowser.home;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.codeskraps.sbrowser.R;
import com.codeskraps.sbrowser.bill_util.IabHelper;
import com.codeskraps.sbrowser.bill_util.IabHelper.OnIabPurchaseFinishedListener;
import com.codeskraps.sbrowser.bill_util.IabHelper.QueryInventoryFinishedListener;
import com.codeskraps.sbrowser.bill_util.IabResult;
import com.codeskraps.sbrowser.bill_util.Inventory;
import com.codeskraps.sbrowser.bill_util.Purchase;
import com.codeskraps.sbrowser.misc.SBrowserData;

public class UserActivity extends Activity implements QueryInventoryFinishedListener,
		OnClickListener, OnIabPurchaseFinishedListener {
	private static final String TAG = UserActivity.class.getSimpleName();
	private static final String PRO_USER = "pro_user";

	private IabHelper mHelper = null;
	private String uuid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		SBrowserData sBrowserData = ((SBrowserApplication) getApplication()).getsBrowserData();
		if (sBrowserData.isChkFullscreen()) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		setContentView(R.layout.user);

		findViewById(R.id.txtIcon).setOnClickListener(this);
		findViewById(R.id.imgIcon).setOnClickListener(this);
		findViewById(R.id.btnBuy).setOnClickListener(this);

		String base64EncodedPublicKey = "";
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					Log.d(TAG, "Problem setting up In-app Billing: " + result);
				} else {
					Log.d(TAG, "Hooray, IAB is fully set up!");
					try {
						ArrayList<String> additionalSkuList = new ArrayList<String>();
						additionalSkuList.add(PRO_USER);
						mHelper.queryInventoryAsync(true, additionalSkuList, UserActivity.this);
					} catch (IllegalStateException e) {
						Log.i(TAG, "Handled: queryInventoryAsync", e);
					}
				}
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null) mHelper.dispose();
		mHelper = null;
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
		if (result.isFailure()) {
			Log.i(TAG, "Error getting inventory:" + result.getMessage());
			return;
		}

		String proUserPrice = inventory.getSkuDetails(PRO_USER).getPrice();
		Log.v(TAG, "Pro user price:" + proUserPrice);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.txtIcon:
		case R.id.imgIcon:
			finish();
			break;
		case R.id.btnBuy:
			uuid = UUID.randomUUID().toString();
			mHelper.launchPurchaseFlow(this, PRO_USER, 10001, this, uuid);
			break;
		}
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
		if (result.isFailure()) {
			Log.d(TAG, "Error purchasing: " + result);
		} else if (purchase.getSku().equals(PRO_USER)) {
			Log.v(TAG, "orderId:" + purchase.getOrderId());
			Log.v(TAG, "PayLoad:" + purchase.getDeveloperPayload());
			Log.v(TAG, "Payload equal:" + purchase.getDeveloperPayload().equals(uuid));
			Log.v(TAG, "Update pro user account");
		}
	}
}
