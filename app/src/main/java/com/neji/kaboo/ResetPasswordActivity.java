package com.neji.kaboo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 * Created by Neji on 11/17/2014.
 */
public class ResetPasswordActivity extends Activity {
    TextView useremail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset);
        useremail = (TextView)findViewById(R.id.txtuseremail);

    }

    public void resetPasswordClickListener(View view){

        if(useremail.getText().toString().isEmpty()){
            useremail.setError("Enter your email ");
            return;
        } else{
            final ProgressDialog ringProgressDialog =
                    ProgressDialog.show(ResetPasswordActivity.this, "Please wait ...", "Password reset in progress..", true);
            ringProgressDialog.setCancelable(false);
            ParseUser.requestPasswordResetInBackground(useremail.getText().toString(), new RequestPasswordResetCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // An email was successfully sent with reset instructions.
                        ringProgressDialog.dismiss();
                        showSuccessDialog();
                    } else {
                        // Something went wrong. Look at the ParseException to see what's up.
                        ringProgressDialog.dismiss();
                        showFailDialog();
                    }
                }
            });
        }

    }

    private void showSuccessDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Success..");

        // Setting Dialog Message
        alertDialog.setMessage("An email was successfully sent to you with reset instructions. ");

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
               Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void showFailDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Error..");

        // Setting Dialog Message
        alertDialog.setMessage("Unable to complete task. Invalid credentials or check your data connection");

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
}
