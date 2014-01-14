package com.codeskraps.sbrowser.loginsignup;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.codeskraps.sbrowser.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity implements OnClickListener {
	private static final String TAG = SignUpActivity.class.getSimpleName();

	private EditText usernameView;
	private EditText emailView;
	private EditText passwordView;
	private EditText passwordAgainView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_signup);

		// Set up the signup form.
		usernameView = (EditText) findViewById(R.id.username);
		emailView = (EditText) findViewById(R.id.email);
		passwordView = (EditText) findViewById(R.id.password);
		passwordAgainView = (EditText) findViewById(R.id.passwordAgain);
		findViewById(R.id.txtIcon).setOnClickListener(this);
		findViewById(R.id.imgIcon).setOnClickListener(this);

		// Set up the submit button click handler
		findViewById(R.id.action_button).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				// Validate the sign up data
				boolean validationError = false;
				StringBuilder validationErrorMessage = new StringBuilder(getResources().getString(
						R.string.error_intro));
				if (isEmpty(usernameView)) {
					validationError = true;
					validationErrorMessage.append(getResources().getString(
							R.string.error_blank_username));
				}
				if (isEmpty(emailView)) {
					validationError = true;
					validationErrorMessage.append(getResources().getString(
							R.string.error_blank_email));
				}
				if (isEmpty(passwordView)) {
					if (validationError) {
						validationErrorMessage
								.append(getResources().getString(R.string.error_join));
					}
					validationError = true;
					validationErrorMessage.append(getResources().getString(
							R.string.error_blank_password));
				}
				if (!isMatching(passwordView, passwordAgainView)) {
					if (validationError) {
						validationErrorMessage
								.append(getResources().getString(R.string.error_join));
					}
					validationError = true;
					validationErrorMessage.append(getResources().getString(
							R.string.error_mismatched_passwords));
				}
				validationErrorMessage.append(getResources().getString(R.string.error_end));

				// If there is a validation error, display the error
				if (validationError) {
					Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(),
							Toast.LENGTH_LONG).show();
					return;
				}

				// Set up a progress dialog
				final ProgressDialog dlg = new ProgressDialog(SignUpActivity.this);
				dlg.setTitle("Please wait.");
				dlg.setMessage("Signing up. Please wait.");
				dlg.show();

				// Set up a new Parse user
				ParseUser user = new ParseUser();
				user.setUsername(usernameView.getText().toString());
				user.setEmail(emailView.getText().toString());
				user.setPassword(passwordView.getText().toString());
				user.signUpInBackground(new SignUpCallback() {

					@TargetApi(Build.VERSION_CODES.HONEYCOMB)
					@Override
					public void done(ParseException e) {
						dlg.dismiss();
						if (e != null) {
							// Show the error message
							Log.i(TAG, "SignUp", e);
							Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG)
									.show();
						} else {
							// Start an intent for the dispatch activity
							Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							} else intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					}
				});
			}
		});
	}

	private boolean isEmpty(EditText etText) {
		if (etText.getText().toString().trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isMatching(EditText etText1, EditText etText2) {
		if (etText1.getText().toString().equals(etText2.getText().toString())) {
			return true;
		} else {
			return false;
		}
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
