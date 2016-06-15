package com.neji.kaboo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudant.sync.notifications.ReplicationCompleted;
import com.cloudant.sync.notifications.ReplicationErrored;
import com.cloudant.sync.replication.ErrorInfo;
import com.cloudant.sync.replication.PullReplication;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorFactory;
import com.neji.kaboo.R;
import com.neji.kaboo.GlobalClass;
import com.google.common.eventbus.Subscribe;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Neji on 11/17/2014.
 */
public class Signup extends Activity {
    TextView username;
    TextView password;
    TextView phone;
    TextView referee;
    TextView email;
    TextView pass2;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Agree = "nameKey";
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Agree)) {


            email = (TextView) findViewById(R.id.txtemail);
            password = (TextView) findViewById(R.id.txtpass);
            phone = (TextView) findViewById(R.id.txtphone);
            username = (TextView) findViewById(R.id.txtusername);
            pass2 = (TextView) findViewById(R.id.txtpass2);

        }else{
Intent intent = new Intent(Signup.this,TermsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void signUpOnClickListener(View view){
        ParseUser user = new ParseUser();

 if (!email.getText().toString().isEmpty()){

    user.setEmail(email.getText().toString());

}else{
   email.setError("Enter your email");
    return;
}

  if (!password.getText().toString().isEmpty()){
      if (pass2.getText().toString().equals(password.getText().toString())){
          user.setPassword(password.getText().toString());

      } else{
          password.setError("Error - Passwords don't match");
          return;
      }

    }else{
            password.setError("Enter a password");
            return;
        }



        if (!phone.getText().toString().isEmpty()){
user.put("phone",phone.getText().toString());
        }else{
            phone.setError("Enter your phone number");
            return;
        }

        if (!username.getText().toString().isEmpty()){
            user.setUsername(username.getText().toString().toLowerCase());
        }else{
            username.setError("Enter a Username");
            return;
        }

        final ProgressDialog ringProgressDialog =
                ProgressDialog.show(Signup.this, "Please wait ...", "Sign up in progress..", true);
        ringProgressDialog.setCancelable(false);
       user.signUpInBackground(new SignUpCallback() {
           @Override
           public void done(com.parse.ParseException e) {
               if (e == null) {
                   // Hooray! Let them use the app now.
                   ringProgressDialog.dismiss();
                    showSuccessDialog();
               } else {
                   // Sign up didn't succeed. Look at the ParseException
                   // to figure out what went wrong
                   ringProgressDialog.dismiss();
                   showFailDialog();
                   e.printStackTrace();
               }
           }
       });

    }

    private void showSuccessDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Registration Successful..");

        // Setting Dialog Message
        alertDialog.setMessage("Thank you for registering with us..");

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
               //launchRingDialog();
                Intent i = new Intent(Signup.this,HomeActivity.class);
                startActivity(i);
                finish();

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
        alertDialog.setMessage("Error in registration, invalid user credentials or check data connection");

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                // Toast.makeText(getApplicationContext(),"New Location created", Toast.LENGTH_SHORT).show();
                return;

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }



    public void loginOnClickListener(View view){
Intent i = new Intent(Signup.this,LoginActivity.class);
        startActivity(i);
    }
}
