package com.codeskraps.sbrowser.loginsignup;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codeskraps.sbrowser.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class LoginActivity extends Activity implements OnClickListener {
	// UI references.
	private EditText usernameView;
	private EditText passwordView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		usernameView = (EditText) findViewById(R.id.username);
		passwordView = (EditText) findViewById(R.id.password);

		// Set up the submit button click handler
		findViewById(R.id.action_button).setOnClickListener(this);
		findViewById(R.id.txtIcon).setOnClickListener(this);
		findViewById(R.id.imgIcon).setOnClickListener(this);

		SpannableString content = new SpannableString(getString(R.string.action_forgot_password));
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		((TextView) findViewById(R.id.txtRecovery)).setText(content);
		findViewById(R.id.txtRecovery).setOnClickListener(this);
	}

	private boolean isEmpty(EditText etText) {
		if (etText.getText().toString().trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtIcon:
		case R.id.imgIcon:
			finish();
			return;

		case R.id.txtRecovery:
			recoveryPassword();
			return;
		}

		// Validate the log in data
		boolean validationError = false;
		StringBuilder validationErrorMessage = new StringBuilder(getResources().getString(
				R.string.error_intro));
		if (isEmpty(usernameView)) {
			validationError = true;
			validationErrorMessage.append(getResources().getString(R.string.error_blank_username));
		}
		if (isEmpty(passwordView)) {
			if (validationError) {
				validationErrorMessage.append(getResources().getString(R.string.error_join));
			}
			validationError = true;
			validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
		}
		validationErrorMessage.append(getResources().getString(R.string.error_end));

		// If there is a validation error, display the error
		if (validationError) {
			Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
					.show();
			return;
		}

		// Set up a progress dialog
		final ProgressDialog dlg = new ProgressDialog(LoginActivity.this);
		dlg.setTitle("Please wait.");
		dlg.setMessage("Logging in. Please wait.");
		dlg.show();
		// Call the Parse login method
		ParseUser.logInInBackground(usernameView.getText().toString(), passwordView.getText()
				.toString(), new LogInCallback() {

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void done(ParseUser user, ParseException e) {
				dlg.dismiss();
				if (e != null) {
					// Show the error message
					Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
				} else {
					// Start an intent for the dispatch activity
					Intent intent = new Intent(LoginActivity.this, DispatchActivity.class);
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					} else intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					LoginActivity.this.finish();
				}
			}
		});
	}

	private void recoveryPassword() {
		AlertDialog.Builder recoverPassword = new AlertDialog.Builder(this);
		recoverPassword.setTitle(getString(R.string.action_forgot_password));
		final EditText email = new EditText(this);
		email.setHint(R.string.prompt_email);
		recoverPassword.setView(email);
		recoverPassword.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						boolean validationError = false;
						StringBuilder validationErrorMessage = new StringBuilder(getResources()
								.getString(R.string.error_intro));
						if (isEmpty(email)) {
							validationError = true;
							validationErrorMessage.append(getResources().getString(
									R.string.error_blank_email));
						}
						validationErrorMessage.append(getResources().getString(R.string.error_end));
						// If there is a validation error, display the error
						if (validationError) {
							Toast.makeText(LoginActivity.this, validationErrorMessage.toString(),
									Toast.LENGTH_LONG).show();
							return;
						}
						ParseUser.requestPasswordResetInBackground(email.getText().toString(),
								new RequestPasswordResetCallback() {
									public void done(ParseException e) {
										String message = new String();
										if (e == null) {
											message = "An email was successfully sent with reset instructions.";
										} else {
											message = "Something went wrong.";
										}
										Toast.makeText(getApplication(), message, Toast.LENGTH_LONG)
												.show();
									}
								});
						dialog.dismiss();
					}
				});
		recoverPassword.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		recoverPassword.show();
	}
}
