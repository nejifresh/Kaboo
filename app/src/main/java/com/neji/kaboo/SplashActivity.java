package com.neji.kaboo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.PuffInAnimation;
import com.easyandroidanimations.library.BounceAnimation;
import com.easyandroidanimations.library.SlideInAnimation;
import com.parse.ParseUser;
import com.sinch.android.rtc.SinchError;

/**
 * Created by Neji on 11/17/2014.
 */
public class SplashActivity extends Activity  {
    ImageView logo;
    TextView alto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = (ImageView)findViewById(R.id.logoimage);
        alto= (TextView)findViewById(R.id.textstart);




        new PuffInAnimation(logo).animate();
        new BounceAnimation(logo).setNumOfBounces(3)
                .setDuration(Animation.DURATION_LONG).animate();

        /****** Create Thread that will sleep for 5 seconds *************/
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(5*1000);

                    // After 5 seconds redirect to another intent
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null) {

                        String userName = currentUser.getUsername().toString();
                        Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();

                       } else {
                        // show the signup or login screen
                        Intent i = new Intent(SplashActivity.this, Signup.class);
                        startActivity(i);
                        finish();
                    }



                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();



    }

    //turn gps on
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }


}
