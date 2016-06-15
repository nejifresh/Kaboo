package com.neji.kaboo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by Neji on 11/17/2014.
 */
public class LoginActivity extends Activity {
    TextView username;
    TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        username = (TextView)findViewById(R.id.txtlogusername);
        password = (TextView)findViewById(R.id.txtlogpass);

    }

    public void loginClickListener(View view){
        if(!username.getText().toString().isEmpty()){

        } else {
username.setError("Enter Username");
            return;
        }

        if(!password.getText().toString().isEmpty()){

        } else {
password.setError("Enter Password");
            return;
        }
        final ProgressDialog ringProgressDialog =
                ProgressDialog.show(LoginActivity.this, "Please wait ...", "Login in progress..", true);
        ringProgressDialog.setCancelable(false);

        ParseUser.logInInBackground(username.getText().toString().toLowerCase(), password.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    // Hooray! The user is logged in.
                    ringProgressDialog.dismiss();
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    ringProgressDialog.dismiss();
                    showFailDialog();

                }
            }
        });

    }

    private void showFailDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Error..");

        // Setting Dialog Message
        alertDialog.setMessage("Login failed, invalid user credentials or check data connection");

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                // Toast.makeText(getApplicationContext(),"New Location created", Toast.LENGTH_SHORT).show();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void forgotPasswordClickListener(View view){
Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
        startActivity(intent);
    }
}
