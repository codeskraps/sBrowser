package com.codeskraps.sbrowser.home;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.codeskraps.sbrowser.R;
import com.codeskraps.sbrowser.bill_util.IabHelper;
import com.codeskraps.sbrowser.bill_util.IabHelper.OnIabPurchaseFinishedListener;
import com.codeskraps.sbrowser.bill_util.IabHelper.QueryInventoryFinishedListener;
import com.codeskraps.sbrowser.bill_util.IabResult;
import com.codeskraps.sbrowser.bill_util.Inventory;
import com.codeskraps.sbrowser.bill_util.Purchase;
import com.codeskraps.sbrowser.loginsignup.DispatchActivity;
import com.codeskraps.sbrowser.misc.Cons;
import com.codeskraps.sbrowser.misc.L;
import com.codeskraps.sbrowser.misc.SBrowserData;

public class PurchaseActivity extends Activity implements QueryInventoryFinishedListener,
		OnClickListener, OnIabPurchaseFinishedListener {
	private static final String TAG = PurchaseActivity.class.getSimpleName();

	private IabHelper mHelper = null;
	private String uuid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		L.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		SBrowserData sBrowserData = ((SBrowserApplication) getApplication()).getsBrowserData();
		if (sBrowserData.isChkFullscreen()) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		setContentView(R.layout.purchase);

		findViewById(R.id.txtIcon).setOnClickListener(this);
		findViewById(R.id.imgIcon).setOnClickListener(this);
		findViewById(R.id.btnBuy).setOnClickListener(this);

		mHelper = new IabHelper(this, Cons.BASE64ENCODEDPUBLICKEY);
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (mHelper == null) return;
				if (result.isFailure()) {
					L.d(TAG, "Problem setting up In-app Billing: " + result);
					complain("Problem setting up in-app billing: " + result);
					return;
				}

				L.d(TAG, "Hooray, IAB is fully set up!");
				try {
					ArrayList<String> additionalSkuList = new ArrayList<String>();
					additionalSkuList.add(Cons.PRO_USER);
					mHelper.queryInventoryAsync(true, additionalSkuList, PurchaseActivity.this);

				} catch (IllegalStateException e) {
					L.i(TAG, "Handled: queryInventoryAsync", e);
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
		if (mHelper == null) return;
		if (result.isFailure()) {
			L.i(TAG, "Error getting inventory:" + result.getMessage());
			complain("Failed to query inventory: " + result);
			return;
		}

		Purchase premiumPurchase = inventory.getPurchase(Cons.PRO_USER);
		if (premiumPurchase != null) {
			L.v(TAG, "User already has premiumPurchase");
			String price = getString(R.string.pro_price);
			price = String.format(price, "Purchased");
			((TextView) findViewById(R.id.txtPrice)).setText(price);
			findViewById(R.id.btnBuy).setEnabled(false);
			// mHelper.consumeAsync(premiumPurchase, this);

		} else {
			String proUserPrice = inventory.getSkuDetails(Cons.PRO_USER).getPrice();
			String price = getString(R.string.pro_price);
			price = String.format(price, proUserPrice);
			L.v(TAG, "Pro user price:" + proUserPrice);
			((TextView) findViewById(R.id.txtPrice)).setText(price);
			findViewById(R.id.btnBuy).setEnabled(true);
		}
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
			try {
				mHelper.launchPurchaseFlow(this, Cons.PRO_USER, 10001, this, uuid);
			} catch (IllegalStateException e) {
				L.i(TAG, "Handled: launchPurchaseFlow", e);
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		L.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
		if (mHelper == null) return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			L.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
				.edit();

		if (result.isFailure()) {
			L.d(TAG, "Error purchasing: " + result);
			editor.putBoolean(Cons.hasPro, false);

		} else if (purchase.getSku().equals(Cons.PRO_USER)) {
			if (purchase.getDeveloperPayload().equals(uuid)) {
				L.v(TAG, "orderId:" + purchase.getOrderId());
				L.v(TAG, "PayLoad:" + purchase.getDeveloperPayload());
				L.v(TAG, "Payload equal:" + purchase.getDeveloperPayload().equals(uuid));
				L.v(TAG, "Updated to pro user account");
				((TextView) findViewById(R.id.txtPrice)).setText("Purchased");
				findViewById(R.id.btnBuy).setEnabled(false);
				editor.putBoolean(Cons.hasPro, true);

				startActivity(new Intent(this, DispatchActivity.class));
				finish();

			} else {
				editor.putBoolean(Cons.hasPro, false);
				complain("There was an issue with the payLoad!!!");
			}
		}

		editor.apply();
	}

	void complain(String message) {
		L.e(TAG, "**** TrivialDrive Error: " + message);
		message = "Error: " + message;
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		L.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

	/*-
	@Override
	public void onConsumeFinished(Purchase purchase, IabResult result) {
		Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

		// if we were disposed of in the meantime, quit.
		if (mHelper == null) return;

		// We know this is the "gas" sku because it's the only one we consume,
		// so we don't check which sku was consumed. If you have more than one
		// sku, you probably should check...
		if (result.isSuccess()) {
			// successfully consumed, so we apply the effects of the item in our
			// game world's logic, which in our case means filling the gas tank
			// a bit
			Log.d(TAG, "Consumption successful. Provisioning.");
		} else {
			complain("Error while consuming: " + result);
		}
		Log.d(TAG, "End consumption flow.");
	}*/
}
