package com.codeskraps.sbrowser.loginsignup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.codeskraps.sbrowser.R;

public class SignUpOrLogInActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_or_log_in);

		findViewById(R.id.txtIcon).setOnClickListener(this);
		findViewById(R.id.imgIcon).setOnClickListener(this);

		// Log in button click handler
		((Button) findViewById(R.id.logInButton)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Starts an intent of the log in activity
				startActivity(new Intent(SignUpOrLogInActivity.this, LoginActivity.class));
				SignUpOrLogInActivity.this.finish();
			}
		});

		// Sign up button click handler
		((Button) findViewById(R.id.signUpButton)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Starts an intent for the sign up activity
				startActivity(new Intent(SignUpOrLogInActivity.this, SignUpActivity.class));
				SignUpOrLogInActivity.this.finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtIcon:
		case R.id.imgIcon:
			finish();
		}
	}
}
