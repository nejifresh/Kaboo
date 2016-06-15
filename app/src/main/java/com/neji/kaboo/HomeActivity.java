package com.neji.kaboo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseUser;

import it.neokree.materialnavigationdrawer.MaterialAccount;
import it.neokree.materialnavigationdrawer.MaterialAccountListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class HomeActivity extends MaterialNavigationDrawer implements MaterialAccountListener {
MaterialAccount account;


    @Override
    public void init(Bundle savedInstanceState) {
        // add first account
        ParseUser currentUser = ParseUser.getCurrentUser();
        account = new MaterialAccount(currentUser.getUsername(),currentUser.getEmail(),new ColorDrawable(Color.parseColor("#9e9e9e")),this.getResources().getDrawable(R.drawable.taxi));
        this.addAccount(account);
        // set listener
        this.setAccountListener(this);
        // add your sections
        this.addSection(this.newSection("Kaboo! - Taxis Around Me", this.getResources().getDrawable(R.drawable.aroundme) ,new Index()).setSectionColor((Color.parseColor("#ff9800"))));
        this.addSection(this.newSection("Premium Cabs", this.getResources().getDrawable(R.drawable.premium), new Premiumcab()).setSectionColor((Color.parseColor("#ff9800"))));
        this.addSection(this.newSection("Executive Cabs", this.getResources().getDrawable(R.drawable.exclusive), new ExclusiveCabs()).setSectionColor((Color.parseColor("#ff9800"))));
        this.addSection(this.newSection("Car Hire Services", this.getResources().getDrawable(R.drawable.hire),new CarHire()).setSectionColor((Color.parseColor("#ff9800"))));
        this.addSection(this.newSection("Transport Companies", this.getResources().getDrawable(R.drawable.motor),new CarParks()).setSectionColor((Color.parseColor("#ff9800"))));
        this.addSection(this.newSection("Haulage Services", this.getResources().getDrawable(R.drawable.haul),new Haulage()).setSectionColor((Color.parseColor("#ff9800"))));

        this.addDivisor();
        this.addSection(this.newSection("My Favourite Cabs", this.getResources().getDrawable(R.drawable.favs), new Favourites()).setSectionColor((Color.parseColor("#ff9800"))));
        this.addSection(this.newSection("Account Settings", this.getResources().getDrawable(R.drawable.ic_settings_black_24dp), new AccountInfo()).setSectionColor((Color.parseColor("#ff9800"))));




        // this.addDivisor();
        // add custom colored section with only text

       // Intent i = new Intent(this,Settings.class);
       // this.addBottomSection(this.newBottomSection("Settings",this.getResources().getDrawable(R.drawable.ic_settings_black_24dp),i));

        // start thread
        t.start();
    }



    @Override
    public void onAccountOpening(MaterialAccount account) {
        // open profile activity
        Intent i = new Intent(this,Profile.class);
        startActivity(i);
    }

    @Override
    public void onChangeAccount(MaterialAccount newAccount) {
        // when another account is selected
    }

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(5000);
               // account.setPhoto(getResources().getDrawable(R.drawable.photo));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyAccountDataChanged();
                    }
                });
                Log.w("PHOTO", "user account photo setted");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
}
